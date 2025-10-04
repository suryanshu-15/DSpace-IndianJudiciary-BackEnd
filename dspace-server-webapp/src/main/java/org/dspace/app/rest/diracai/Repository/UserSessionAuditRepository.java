package org.dspace.app.rest.diracai.Repository;

import org.dspace.content.Diracai.UserSessionAudit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

import java.util.List;
import java.util.UUID;

@Repository
public interface UserSessionAuditRepository extends JpaRepository<UserSessionAudit, UUID> {
    List<UserSessionAudit> findByUserId(UUID userId); // ✅ Add this line
    Optional<UserSessionAudit> findTopByUserIdAndIpAddressOrderByTimestampDesc(UUID userId, String ipAddress);

    Optional<UserSessionAudit> findTopByUserIdAndSessionIdAndEventTypeOrderByTimestampDesc(UUID userId, String sessionId, String eventType);


    // Find the latest active session (LOGIN without logout) for a user + session
    Optional<UserSessionAudit> findTopByUserIdAndSessionIdAndLogoutTimeIsNullOrderByLoginTimeDesc(UUID userId, String sessionId);


}


