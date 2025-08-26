package org.dspace.app.rest.diracai.Repository;

import org.dspace.content.Diracai.LoginDeviceAudit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.*;

@Repository
public interface LoginDeviceAuditRepository extends JpaRepository<LoginDeviceAudit, UUID> {

    Optional<LoginDeviceAudit> findByDeviceId(String deviceId);

    // Find all login attempts by epersonUUID and deviceId
    List<LoginDeviceAudit> findByEpersonUUIDAndDeviceId(UUID epersonUUID, String deviceId);
}
