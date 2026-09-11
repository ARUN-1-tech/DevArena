package com.devarena.team.repository;

import com.devarena.team.model.TeamEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface TeamRepository extends JpaRepository<TeamEntity, UUID> {

    Optional<TeamEntity> findBySlug(String slug);

    Optional<TeamEntity> findByNameIgnoreCase(String name);

    boolean existsByNameIgnoreCase(String name);

    boolean existsBySlug(String slug);

    @Query("SELECT t FROM TeamEntity t WHERE LOWER(t.name) LIKE LOWER(CONCAT('%', :query, '%')) OR LOWER(t.description) LIKE LOWER(CONCAT('%', :query, '%'))")
    List<TeamEntity> searchTeams(@Param("query") String query, Pageable pageable);

    Page<TeamEntity> findAllByOrderByRatingDescWinsDesc(Pageable pageable);
}
