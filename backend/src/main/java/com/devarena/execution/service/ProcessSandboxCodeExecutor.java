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
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

@Component
public class ProcessSandboxCodeExecutor implements CodeExecutor {

    private static final Logger log = LoggerFactory.getLogger(ProcessSandboxCodeExecutor.class);
    private static final int MAX_OUTPUT_BYTES = 32 * 1024; // 32 KB

    // Hostile patterns security blacklist for local sandbox
    private static final Set<String> SECURITY_PATTERNS = Set.of(
            "System.exit",
            "Runtime.getRuntime()",
            "ProcessBuilder",
            "child_process",
            "subprocess",
            "os.system",
            "os.popen",
            "fs.unlink",
            "fs.rm",
            "fs.writeFile",
            "fs.appendFile",
            "fs.truncate",
            "fs.chmod",
            "fs.chown",
            "java.lang.reflect"
    );

    @Value("${devarena.execution.sandbox.enabled:true}")
    private boolean sandboxEnabled;

    @Override
    public String getName() {
        return "ProcessSandboxExecutor";
    }

    @Override
    public boolean isAvailable() {
        return sandboxEnabled;
    }

    @Override
    public ExecutionOutput execute(ExecutionLanguage language, String sourceCode, String stdin, long timeoutMs) {
        // 1. Security scan
        for (String pattern : SECURITY_PATTERNS) {
            if (sourceCode.contains(pattern)) {
                return ExecutionOutput.failed(
                        SubmissionStatus.RUNTIME_ERROR,
                        "",
                        "Security violation: restricted pattern '" + pattern + "' detected in source code.",
                        0L,
                        1,
                        "Restricted execution violation"
                );
            }
        }

        Path tempDir = null;
        try {
            tempDir = Files.createTempDirectory("devarena_sandbox_");

            return switch (language) {
                case JAVA -> executeJava(tempDir, sourceCode, stdin, timeoutMs);
                case PYTHON -> executePython(tempDir, sourceCode, stdin, timeoutMs);
                case JAVASCRIPT -> executeNode(tempDir, sourceCode, stdin, timeoutMs);
            };

        } catch (Exception e) {
            log.error("Process sandbox execution failed", e);
            return ExecutionOutput.error(SubmissionStatus.SYSTEM_ERROR, "Sandbox execution error: " + e.getMessage());
        } finally {
            cleanupTempDir(tempDir);
        }
    }

    private ExecutionOutput executeJava(Path tempDir, String sourceCode, String stdin, long timeoutMs) throws Exception {
        Path srcFile = tempDir.resolve("Solution.java");
        Files.writeString(srcFile, sourceCode, StandardCharsets.UTF_8);

        String javacCmd = resolveJavaBinary("javac");
        String javaCmd = resolveJavaBinary("java");

        // 1. Compile
        ProcessBuilder compilePb = new ProcessBuilder(javacCmd, "-encoding", "UTF-8", "Solution.java");
        compilePb.directory(tempDir.toFile());
        Process compileProcess = compilePb.start();
        boolean compiledOk = compileProcess.waitFor(timeoutMs, TimeUnit.MILLISECONDS);

        if (!compiledOk) {
            compileProcess.destroyForcibly();
            return ExecutionOutput.timeout(timeoutMs);
        }

        if (compileProcess.exitValue() != 0) {
            String compileErr = readStreamWithLimit(compileProcess.getErrorStream(), MAX_OUTPUT_BYTES);
            return ExecutionOutput.failed(SubmissionStatus.COMPILATION_ERROR, "", compileErr, 0L, compileProcess.exitValue(), compileErr);
        }

        // 2. Run
        ProcessBuilder runPb = new ProcessBuilder(javaCmd, "-Xmx128m", "-Dfile.encoding=UTF-8", "Solution");
        runPb.directory(tempDir.toFile());
        return runProcessWithTimeout(runPb, stdin, timeoutMs);
    }

    private String resolveJavaBinary(String tool) {
        String javaHome = System.getProperty("java.home");
        if (javaHome != null) {
            Path toolPathWin = Path.of(javaHome, "bin", tool + ".exe");
            if (Files.exists(toolPathWin)) {
                return toolPathWin.toString();
            }
            Path toolPath = Path.of(javaHome, "bin", tool);
            if (Files.exists(toolPath)) {
                return toolPath.toString();
            }
        }
        return tool;
    }

    private ExecutionOutput executePython(Path tempDir, String sourceCode, String stdin, long timeoutMs) throws Exception {
        Path srcFile = tempDir.resolve("solution.py");
        Files.writeString(srcFile, sourceCode, StandardCharsets.UTF_8);

        // Try 'python' or 'python3'
        String pythonCmd = isCommandAvailable("python") ? "python" : "python3";
        ProcessBuilder runPb = new ProcessBuilder(pythonCmd, "-u", "solution.py");
        runPb.directory(tempDir.toFile());
        return runProcessWithTimeout(runPb, stdin, timeoutMs);
    }

    private ExecutionOutput executeNode(Path tempDir, String sourceCode, String stdin, long timeoutMs) throws Exception {
        Path srcFile = tempDir.resolve("solution.js");
        Files.writeString(srcFile, sourceCode, StandardCharsets.UTF_8);

        ProcessBuilder runPb = new ProcessBuilder("node", "--max-old-space-size=128", "solution.js");
        runPb.directory(tempDir.toFile());
        return runProcessWithTimeout(runPb, stdin, timeoutMs);
    }

    private ExecutionOutput runProcessWithTimeout(ProcessBuilder pb, String stdin, long timeoutMs) throws Exception {
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

        return ExecutionOutput.failed(SubmissionStatus.RUNTIME_ERROR, stdout, stderr, duration, exitCode, stderr);
    }

    private boolean isCommandAvailable(String cmd) {
        try {
            Process p = new ProcessBuilder(cmd, "--version").start();
            return p.waitFor(1, TimeUnit.SECONDS) && p.exitValue() == 0;
        } catch (Exception e) {
            return false;
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
                log.warn("Failed to clean up sandbox directory: {}", dir, e);
            }
        }
    }
}
