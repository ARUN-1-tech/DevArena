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
    Optional<ProfileEntity> findByUsernameIgnoreCase(String username);
    boolean existsByUsername(String username);
    boolean existsByUsernameIgnoreCase(String username);

    @org.springframework.data.jpa.repository.Query("SELECT p FROM ProfileEntity p WHERE LOWER(p.username) LIKE LOWER(CONCAT('%', :query, '%')) OR LOWER(p.displayName) LIKE LOWER(CONCAT('%', :query, '%'))")
    java.util.List<ProfileEntity> searchProfiles(@org.springframework.data.repository.query.Param("query") String query, org.springframework.data.domain.Pageable pageable);
}
