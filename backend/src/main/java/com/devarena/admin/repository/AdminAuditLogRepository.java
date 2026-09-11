package com.devarena.admin.repository;

import com.devarena.admin.model.AdminAuditLogEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface AdminAuditLogRepository extends JpaRepository<AdminAuditLogEntity, UUID> {
    Page<AdminAuditLogEntity> findAllByOrderByCreatedAtDesc(Pageable pageable);
}
