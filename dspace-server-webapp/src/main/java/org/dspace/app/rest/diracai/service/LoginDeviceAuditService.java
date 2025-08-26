package org.dspace.app.rest.diracai.service;

import jakarta.annotation.PostConstruct;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.zookeeper.Login;
import org.dspace.app.rest.diracai.Repository.LoginDeviceAuditRepository;
import org.dspace.app.rest.diracai.Repository.UserSessionAuditRepository;
import org.dspace.content.Diracai.LoginDeviceAudit;
import org.dspace.core.Context;
import org.dspace.eperson.EPerson;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Component
public class LoginDeviceAuditService {

    @Autowired
    private LoginDeviceAuditRepository auditRepository;

    private static LoginDeviceAuditService instance;


    @PostConstruct
    public void registerInstance() {
        instance = this;
    }

    public static LoginDeviceAuditService getInstance() {
        return instance;
    }


    public void logLoginAttempt(EPerson eperson, String ip, String userAgent, String status) {
        UUID epersonUUID = (eperson != null) ? eperson.getID() : null;
        String deviceId = generateDeviceId(ip, userAgent);

        LoginDeviceAudit audit = auditRepository.findByDeviceId(deviceId)
                .orElse(new LoginDeviceAudit());

        audit.setEpersonUUID(epersonUUID);
        audit.setIpAddress(ip);
        audit.setUserAgent(userAgent);
        audit.setStatus(status);
        audit.setLoginTime(new Timestamp(System.currentTimeMillis()));
        audit.setDeviceId(deviceId);

        if ("FAILURE".equalsIgnoreCase(status)) {
            audit.setFailedAttempts(audit.getFailedAttempts()+1);
        } else {
            audit.setFailedAttempts(0);
        }
        auditRepository.save(audit);
    }

    private String generateDeviceId(String ip, String userAgent) {
        return DigestUtils.sha256Hex(ip + "|" + userAgent); // Generates consistent device ID
    }
}
