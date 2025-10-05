package org.dspace.app.rest.diracai.Repository;


import org.dspace.content.Diracai.RoleAuditLog;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface RoleAuditLogRepository extends JpaRepository<RoleAuditLog, UUID> {
    List<RoleAuditLog> findByAffectedUser(UUID affectedUser);
}


