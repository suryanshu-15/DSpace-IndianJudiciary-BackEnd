package org.dspace.app.rest.diracai.service;

import jakarta.annotation.PostConstruct;
import jakarta.servlet.http.HttpServletRequest;
import org.dspace.app.rest.diracai.Repository.UserSessionAuditRepository;
import org.dspace.content.Diracai.UserSessionAudit;
import org.dspace.eperson.EPerson;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import java.util.Optional;


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



    public void logLogout(EPerson user, HttpServletRequest request) {
        String sessionId = request.getSession().getId();
        Optional<UserSessionAudit> auditOpt = auditRepo
                .findTopByUserIdAndSessionIdAndLogoutTimeIsNullOrderByLoginTimeDesc(user.getID(), sessionId);

        if (auditOpt.isPresent()) {
            UserSessionAudit audit = auditOpt.get();
            Timestamp now = new Timestamp(System.currentTimeMillis());
            audit.setLogoutTime(now);
            audit.setDurationInSeconds(audit.getLoginTime() != null ?
                    (now.getTime() - audit.getLoginTime().getTime()) / 1000 : 0L);
            audit.setEventType("LOGOUT");
            audit.setTimestamp(now);
            auditRepo.save(audit);
        } else {
            System.out.println("⚠️ Warning: No active session found for user " + user.getID());
        }
    }





}




