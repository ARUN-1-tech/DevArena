package com.devarena.user.service;

import com.devarena.common.exception.BadRequestException;
import com.devarena.common.exception.DevArenaException;
import com.devarena.common.exception.ResourceNotFoundException;
import com.devarena.security.JwtTokenProvider;
import com.devarena.security.JwtTokenProviderImpl;
import com.devarena.security.UserRole;
import com.devarena.user.dto.*;
import com.devarena.user.model.*;
import com.devarena.user.repository.*;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final ProfileRepository profileRepository;
    private final PlayerStatsRepository playerStatsRepository;
    private final PlayerProgressionRepository playerProgressionRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;
    private final AuthenticationManager authenticationManager;

    public AuthService(
            UserRepository userRepository,
            ProfileRepository profileRepository,
            PlayerStatsRepository playerStatsRepository,
            PlayerProgressionRepository playerProgressionRepository,
            RefreshTokenRepository refreshTokenRepository,
            PasswordEncoder passwordEncoder,
            JwtTokenProvider jwtTokenProvider,
            AuthenticationManager authenticationManager) {
        this.userRepository = userRepository;
        this.profileRepository = profileRepository;
        this.playerStatsRepository = playerStatsRepository;
        this.playerProgressionRepository = playerProgressionRepository;
        this.refreshTokenRepository = refreshTokenRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtTokenProvider = jwtTokenProvider;
        this.authenticationManager = authenticationManager;
    }

    @Transactional
    public AuthResponse register(RegisterRequest request) {
        String normalizedEmail = request.email().trim().toLowerCase();
        String normalizedUsername = request.username().trim();

        if (userRepository.existsByEmailIgnoreCase(normalizedEmail)) {
            throw new DevArenaException("That email address is already registered.", HttpStatus.CONFLICT, "DUPLICATE_EMAIL");
        }

        if (userRepository.existsByUsernameIgnoreCase(normalizedUsername) || profileRepository.existsByUsernameIgnoreCase(normalizedUsername)) {
            throw new DevArenaException("That username is already taken.", HttpStatus.CONFLICT, "DUPLICATE_USERNAME");
        }

        // 1. Create User
        UserEntity user = new UserEntity(
                normalizedUsername,
                normalizedEmail,
                passwordEncoder.encode(request.password())
        );
        user = userRepository.save(user);

        // 2. Create Profile
        ProfileEntity profile = new ProfileEntity(
                user,
                normalizedUsername,
                request.displayName().trim(),
                "avatar-1"
        );
        profile = profileRepository.save(profile);
        user.setProfile(profile);

        // 3. Create Player Stats
        PlayerStatsEntity stats = new PlayerStatsEntity(user);
        stats = playerStatsRepository.save(stats);
        user.setStats(stats);

        // 4. Create Player Progression
        PlayerProgressionEntity progression = new PlayerProgressionEntity(user);
        progression = playerProgressionRepository.save(progression);
        user.setProgression(progression);

        // 5. Generate JWT Tokens
        String accessToken = jwtTokenProvider.generateAccessToken(user.getUsername(), user.getRoles());
        String refreshToken = createAndSaveRefreshToken(user);

        UserSummaryDto summary = new UserSummaryDto(
                user.getId(),
                user.getUsername(),
                user.getEmail(),
                profile.getDisplayName(),
                profile.getAvatar(),
                user.getRoles().stream().map(UserRole::name).collect(Collectors.toSet())
        );

        return AuthResponse.of(accessToken, refreshToken, summary);
    }

    @Transactional
    public AuthResponse login(LoginRequest request) {
        String credential = request.email().trim();

        // Check if user exists by email or username
        UserEntity user = userRepository.findByEmailIgnoreCase(credential)
                .or(() -> userRepository.findByUsernameIgnoreCase(credential))
                .orElseThrow(() -> new DevArenaException("Invalid email or password", HttpStatus.UNAUTHORIZED, "UNAUTHORIZED"));

        // Authenticate with Spring Security
        Authentication auth = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(user.getUsername(), request.password())
        );

        String accessToken = jwtTokenProvider.generateAccessToken(user.getUsername(), user.getRoles());
        String refreshToken = createAndSaveRefreshToken(user);

        ProfileEntity profile = profileRepository.findByUserId(user.getId())
                .orElse(new ProfileEntity(user, user.getUsername(), user.getUsername(), "avatar-1"));

        UserSummaryDto summary = new UserSummaryDto(
                user.getId(),
                user.getUsername(),
                user.getEmail(),
                profile.getDisplayName(),
                profile.getAvatar(),
                user.getRoles().stream().map(UserRole::name).collect(Collectors.toSet())
        );

        return AuthResponse.of(accessToken, refreshToken, summary);
    }

    @Transactional
    public AuthResponse refreshToken(TokenRefreshRequest request) {
        RefreshTokenEntity tokenEntity = refreshTokenRepository.findByToken(request.refreshToken())
                .orElseThrow(() -> new BadRequestException("Invalid refresh token", "INVALID_REFRESH_TOKEN"));

        if (tokenEntity.isRevoked() || tokenEntity.isExpired()) {
            throw new BadRequestException("Refresh token is expired or revoked. Please log in again.", "EXPIRED_REFRESH_TOKEN");
        }

        UserEntity user = tokenEntity.getUser();
        tokenEntity.setRevoked(true);
        refreshTokenRepository.save(tokenEntity);

        String newAccessToken = jwtTokenProvider.generateAccessToken(user.getUsername(), user.getRoles());
        String newRefreshToken = createAndSaveRefreshToken(user);

        ProfileEntity profile = profileRepository.findByUserId(user.getId())
                .orElse(new ProfileEntity(user, user.getUsername(), user.getUsername(), "avatar-1"));

        UserSummaryDto summary = new UserSummaryDto(
                user.getId(),
                user.getUsername(),
                user.getEmail(),
                profile.getDisplayName(),
                profile.getAvatar(),
                user.getRoles().stream().map(UserRole::name).collect(Collectors.toSet())
        );

        return AuthResponse.of(newAccessToken, newRefreshToken, summary);
    }

    @Transactional
    public void logout(String username) {
        userRepository.findByUsername(username).ifPresent(user -> {
            refreshTokenRepository.deleteByUserId(user.getId());
        });
    }

    @Transactional(readOnly = true)
    public PlayerProfileResponse getCurrentUserProfile(String username) {
        UserEntity user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User", "username", username));

        ProfileEntity profile = profileRepository.findByUserId(user.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Profile", "userId", user.getId()));

        PlayerStatsEntity stats = playerStatsRepository.findByUserId(user.getId())
                .orElse(new PlayerStatsEntity(user));

        PlayerProgressionEntity progression = playerProgressionRepository.findByUserId(user.getId())
                .orElse(new PlayerProgressionEntity(user));

        PlayerProfileResponse.PlayerStatsDto statsDto = new PlayerProfileResponse.PlayerStatsDto(
                stats.getRating(),
                stats.getWins(),
                stats.getLosses(),
                stats.getDraws(),
                stats.getWinStreak(),
                stats.getHighestRating()
        );

        PlayerProfileResponse.PlayerProgressionDto progressionDto = new PlayerProfileResponse.PlayerProgressionDto(
                progression.getLevel(),
                progression.getCurrentXp(),
                progression.getXpToNextLevel()
        );

        return new PlayerProfileResponse(
                user.getId(),
                user.getUsername(),
                user.getEmail(),
                profile.getDisplayName(),
                profile.getAvatar(),
                profile.getBio(),
                user.getRoles().stream().map(UserRole::name).collect(Collectors.toSet()),
                statsDto,
                progressionDto
        );
    }

    @Transactional
    public PlayerProfileResponse updateProfile(String username, UpdateProfileRequest request) {
        UserEntity user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User", "username", username));

        ProfileEntity profile = profileRepository.findByUserId(user.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Profile", "userId", user.getId()));

        if (request.displayName() != null && !request.displayName().isBlank()) {
            profile.setDisplayName(request.displayName().trim());
        }
        if (request.avatar() != null && !request.avatar().isBlank()) {
            profile.setAvatar(request.avatar().trim());
        }
        if (request.bio() != null) {
            profile.setBio(request.bio().trim());
        }

        profileRepository.save(profile);
        return getCurrentUserProfile(username);
    }

    private String createAndSaveRefreshToken(UserEntity user) {
        String token = jwtTokenProvider.generateRefreshToken(user.getUsername());
        long refreshExpirationMs = 604800000; // 7 days fallback
        if (jwtTokenProvider instanceof JwtTokenProviderImpl impl) {
            refreshExpirationMs = impl.getRefreshExpirationMs();
        }

        RefreshTokenEntity refreshTokenEntity = new RefreshTokenEntity(
                user,
                token,
                Instant.now().plusMillis(refreshExpirationMs)
        );
        refreshTokenRepository.save(refreshTokenEntity);
        return token;
    }
}
