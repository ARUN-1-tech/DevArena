package com.devarena.execution.service;

import com.devarena.execution.model.ExecutionLanguage;
import com.devarena.execution.model.ExecutionOutput;
import com.devarena.submission.model.SubmissionStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Comparator;
import java.util.concurrent.TimeUnit;

@Component
public class DockerCodeExecutor implements CodeExecutor {

    private static final Logger log = LoggerFactory.getLogger(DockerCodeExecutor.class);
    private static final int MAX_OUTPUT_BYTES = 32 * 1024; // 32 KB

    @Value("${devarena.execution.docker.enabled:false}")
    private boolean dockerEnabled;

    @Value("${devarena.execution.docker.image-java:eclipse-temurin:21-alpine}")
    private String javaImage;

    @Value("${devarena.execution.docker.image-python:python:3.11-alpine}")
    private String pythonImage;

    @Value("${devarena.execution.docker.image-javascript:node:20-alpine}")
    private String nodeImage;

    @Override
    public String getName() {
        return "DockerIsolatedExecutor";
    }

    @Override
    public boolean isAvailable() {
        if (!dockerEnabled) {
            return false;
        }
        try {
            Process process = new ProcessBuilder("docker", "--version").start();
            boolean finished = process.waitFor(2, TimeUnit.SECONDS);
            return finished && process.exitValue() == 0;
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public ExecutionOutput execute(ExecutionLanguage language, String sourceCode, String stdin, long timeoutMs) {
        Path tempDir = null;
        try {
            tempDir = Files.createTempDirectory("devarena_docker_exec_");
            String fileName = switch (language) {
                case JAVA -> "Solution.java";
                case PYTHON -> "solution.py";
                case JAVASCRIPT -> "solution.js";
            };

            Path srcPath = tempDir.resolve(fileName);
            Files.writeString(srcPath, sourceCode, StandardCharsets.UTF_8);

            String containerImage = switch (language) {
                case JAVA -> javaImage;
                case PYTHON -> pythonImage;
                case JAVASCRIPT -> nodeImage;
            };

            String runCommand = switch (language) {
                case JAVA -> "javac Solution.java && java Solution";
                case PYTHON -> "python3 solution.py";
                case JAVASCRIPT -> "node solution.js";
            };

            ProcessBuilder pb = new ProcessBuilder(
                    "docker", "run", "--rm", "-i",
                    "--network", "none",
                    "--memory", "256m",
                    "--cpus", "1.0",
                    "--pids-limit", "64",
                    "-v", tempDir.toAbsolutePath() + ":/app:ro",
                    "-w", "/app",
                    containerImage,
                    "sh", "-c", runCommand
            );

            long startTime = System.currentTimeMillis();
            Process process = pb.start();

            if (stdin != null && !stdin.isEmpty()) {
                try (OutputStream os = process.getOutputStream()) {
                    os.write(stdin.getBytes(StandardCharsets.UTF_8));
                    os.flush();
                }
            }

            boolean completed = process.waitFor(timeoutMs, TimeUnit.MILLISECONDS);
            long duration = System.currentTimeMillis() - startTime;

            if (!completed) {
                process.destroyForcibly();
                return ExecutionOutput.timeout(timeoutMs);
            }

            String stdout = readStreamWithLimit(process.getInputStream(), MAX_OUTPUT_BYTES);
            String stderr = readStreamWithLimit(process.getErrorStream(), MAX_OUTPUT_BYTES);
            int exitCode = process.exitValue();

            if (exitCode == 0) {
                return ExecutionOutput.success(stdout, duration, 0L);
            }

            SubmissionStatus status = stderr.contains("error:") || stderr.contains("javac")
                    ? SubmissionStatus.COMPILATION_ERROR
                    : SubmissionStatus.RUNTIME_ERROR;

            return ExecutionOutput.failed(status, stdout, stderr, duration, exitCode, stderr);

        } catch (Exception e) {
            log.error("Docker execution failed", e);
            return ExecutionOutput.error(SubmissionStatus.SYSTEM_ERROR, "Docker executor error: " + e.getMessage());
        } finally {
            cleanupTempDir(tempDir);
        }
    }

    private String readStreamWithLimit(InputStream is, int maxBytes) throws IOException {
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        byte[] data = new byte[1024];
        int nRead;
        int total = 0;
        while ((nRead = is.read(data, 0, Math.min(data.length, maxBytes - total))) != -1) {
            buffer.write(data, 0, nRead);
            total += nRead;
            if (total >= maxBytes) {
                buffer.write("\n...[output truncated by DevArena security]".getBytes(StandardCharsets.UTF_8));
                break;
            }
        }
        return buffer.toString(StandardCharsets.UTF_8);
    }

    private void cleanupTempDir(Path dir) {
        if (dir != null && Files.exists(dir)) {
            try {
                Files.walk(dir)
                        .sorted(Comparator.reverseOrder())
                        .map(Path::toFile)
                        .forEach(File::delete);
            } catch (Exception e) {
                log.warn("Failed to clean up temp dir: {}", dir, e);
            }
        }
    }
}
