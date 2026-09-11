package com.devarena.user.repository;

import com.devarena.user.model.ProfileEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface ProfileRepository extends JpaRepository<ProfileEntity, UUID> {
    Optional<ProfileEntity> findByUserId(UUID userId);
    Optional<ProfileEntity> findByUsername(String username);
    boolean existsByUsername(String username);
}
