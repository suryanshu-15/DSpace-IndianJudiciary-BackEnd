//package org.dspace.app.rest.diracai.service;
//import org.dspace.content.Diracai.RoleAuditLog;
//import org.dspace.app.rest.diracai.Repository.RoleAuditLogRepository;
//import org.dspace.core.Context;
//import org.dspace.services.RequestService;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Service;
//
//import javax.servlet.http.HttpServletRequest;
//import java.sql.Timestamp;
//import java.util.UUID;
//
//@Service
//public class RoleAuditLogService {
//
//    @Autowired
//    private RoleAuditLogRepository repository;
//
//    @Autowired
//    private RequestService requestService;
//
//    public void logRoleChange(Context context, UUID affectedUserId, String action, String target) {
//        RoleAuditLog log = new RoleAuditLog();
//        log.setActedBy(context.getCurrentUser() != null ? context.getCurrentUser().getID() : null);
//        log.setAffectedUser(affectedUserId);
//        log.setAction(action);
//        log.setTarget(target);
//        log.setTimestamp(new Timestamp(System.currentTimeMillis()));
//
//        HttpServletRequest request = requestService.getCurrentRequest().getHttpServletRequest();
//        if (request != null) {
//            log.setIpAddress(request.getRemoteAddr());
//            log.setUserAgent(request.getHeader("User-Agent"));
//        }
//
//        repository.save(log);
//    }
//}
//
package org.dspace.app.rest.diracai.service;

import org.dspace.content.Diracai.RoleAuditLog;
import org.dspace.app.rest.diracai.Repository.RoleAuditLogRepository;
import org.dspace.core.Context;
import org.dspace.services.RequestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import jakarta.servlet.http.HttpServletRequest;  // ✅ UPDATED import
import java.sql.Timestamp;
import java.util.UUID;

@Service
public class RoleAuditLogService {

    @Autowired
    private RoleAuditLogRepository repository;

    @Autowired
    private RequestService requestService;

    public void logRoleChange(Context context, UUID affectedUserId, String action, String target) {
        RoleAuditLog log = new RoleAuditLog();
        log.setActedBy(context.getCurrentUser() != null ? context.getCurrentUser().getID() : null);
        log.setAffectedUser(affectedUserId);
        log.setAction(action);
        log.setTarget(target);
        log.setTimestamp(new Timestamp(System.currentTimeMillis()));

        HttpServletRequest request = requestService.getCurrentRequest().getHttpServletRequest();
        if (request != null) {
            log.setIpAddress(request.getRemoteAddr());
            log.setUserAgent(request.getHeader("User-Agent"));
        }

        repository.save(log);
    }
}
