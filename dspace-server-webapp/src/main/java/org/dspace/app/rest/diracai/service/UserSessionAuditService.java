package org.dspace.app.rest.diracai.service;

import jakarta.annotation.PostConstruct;
import jakarta.servlet.http.HttpServletRequest;
import org.dspace.app.rest.diracai.Repository.UserSessionAuditRepository;
import org.dspace.content.Diracai.UserSessionAudit;
import org.dspace.eperson.EPerson;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.sql.Timestamp;
import java.util.UUID;

@Component
public class UserSessionAuditService {

    private static UserSessionAuditService instance;

    @Autowired
    private UserSessionAuditRepository auditRepo;

    @PostConstruct
    public void registerInstance() {
        instance = this;
    }

    public static UserSessionAuditService getInstance() {
        return instance;
    }

    public void log(String eventType, EPerson user, HttpServletRequest request) {
        UserSessionAudit audit = new UserSessionAudit();
        audit.setId(UUID.randomUUID());
        audit.setUserId(UUID.fromString(user.getID().toString()));
        audit.setEmail(user.getEmail());
        audit.setEventType(eventType);
        audit.setLoginTime((new Timestamp(System.currentTimeMillis())));
        audit.setIpAddress(request.getRemoteAddr());
        audit.setUserAgent(request.getHeader("User-Agent"));
        audit.setTimestamp(new Timestamp(System.currentTimeMillis()));
        auditRepo.save(audit);
    }

    public void logLogout(EPerson user, HttpServletRequest request, String sessionId) {

        UserSessionAudit audit1 = auditRepo.findTopByUserIdAndIpAddressOrderByTimestampDesc(user.getID(), request.getRemoteAddr()).get();
        Timestamp logoutTime = new Timestamp(System.currentTimeMillis());
        audit1.setLogoutTime(logoutTime);
        if (audit1.getLoginTime() != null) {
            long duration = (logoutTime.getTime() - audit1.getLoginTime().getTime()) / 1000;
            audit1.setDurationInSeconds(duration);
        }
        audit1.setEventType("LOGOUT");
        audit1.setTimestamp(new Timestamp(System.currentTimeMillis()));
        auditRepo.save(audit1);
    }
}
