package org.dspace.app.rest.diracai.service.impl;

import org.dspace.app.rest.diracai.Repository.FileAccessLogRepository;
import org.dspace.app.rest.diracai.Repository.RoleAuditLogRepository;
import org.dspace.app.rest.diracai.Repository.UserSessionAuditRepository;
import org.dspace.app.rest.diracai.dto.Audit;
import org.dspace.app.rest.diracai.dto.UserActionLog;
import org.dspace.app.rest.diracai.service.AuditService;
import org.dspace.content.Diracai.FileAccessLog;
import org.dspace.content.Diracai.RoleAuditLog;
import org.dspace.content.Diracai.UserSessionAudit;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.*;

@Service
public class AuditServiceImpl implements AuditService {

    @Autowired
    private UserSessionAuditRepository userSessionAuditRepository;

    @Autowired
    private RoleAuditLogRepository roleAuditLogRepository;

    @Autowired
    private FileAccessLogRepository fileAccessLogRepository;

    @Override
    public ResponseEntity<?> getAllUsers() {
        List<UserSessionAudit> allAudits = Optional.ofNullable(userSessionAuditRepository.findAll())
                .orElse(Collections.emptyList());

        Map<UUID, UserSessionAudit> latestAuditMap = new HashMap<>();
        for (UserSessionAudit audit : allAudits) {
            UUID userId = audit.getUserId();
            if (!latestAuditMap.containsKey(userId)) {
                latestAuditMap.put(userId, audit);
            } else {
                Timestamp existingTs = latestAuditMap.get(userId).getTimestamp();
                if (audit.getTimestamp() != null && (existingTs == null || audit.getTimestamp().after(existingTs))) {
                    latestAuditMap.put(userId, audit);
                }
            }
        }

        List<Audit> auditList = new ArrayList<>();
        for (UserSessionAudit audit : latestAuditMap.values()) {
            Audit dto = new Audit();
            dto.setUserId(audit.getUserId());
            dto.setEmail(audit.getEmail() != null ? audit.getEmail() : "N/A");
            dto.setLoginTime(audit.getLoginTime() != null ? audit.getLoginTime() : audit.getTimestamp());
            dto.setLogoutTime(audit.getLogoutTime());
            dto.setDuration(audit.getDurationInSeconds() != null ? audit.getDurationInSeconds() : 0L);
            dto.setEventType(audit.getEventType() != null ? audit.getEventType() : "UNKNOWN");
            auditList.add(dto);
        }
        return ResponseEntity.ok(auditList);
    }

    @Override
    public ResponseEntity<?> getByUser(UUID userId) {
        List<UserActionLog> actionLogs = new ArrayList<>();

        // 1️⃣ Login/Logout session audits
        List<UserSessionAudit> sessionAudits = Optional.ofNullable(userSessionAuditRepository.findByUserId(userId))
                .orElse(Collections.emptyList());

        sessionAudits.stream()
                .filter(audit -> "LOGIN".equalsIgnoreCase(audit.getEventType()) || "LOGOUT".equalsIgnoreCase(audit.getEventType()))
                .forEach(audit -> {
                    UserActionLog log = new UserActionLog();
                    log.setAction(audit.getEventType() != null ? audit.getEventType() : "UNKNOWN");
                    log.setTimestamp(audit.getTimestamp() != null ? audit.getTimestamp() : new Timestamp(System.currentTimeMillis()));
                    log.setIpAddress(audit.getIpAddress() != null ? audit.getIpAddress() : "N/A");
                    log.setUserAgent(audit.getUserAgent() != null ? audit.getUserAgent() : "N/A");
                    log.setObjectId(null); // login/logout has no objectId
                    actionLogs.add(log);
                });

        // 2️⃣ Role/Permission audits
        List<RoleAuditLog> roleLogs = Optional.ofNullable(roleAuditLogRepository.findByAffectedUser(userId))
                .orElse(Collections.emptyList());

        Optional<RoleAuditLog> latestRoleChange = roleLogs.stream()
                .filter(log -> "ROLE_ASSIGN".equalsIgnoreCase(log.getAction()))
                .max(Comparator.comparing(RoleAuditLog::getTimestamp, Comparator.nullsLast(Comparator.naturalOrder())));

        Optional<RoleAuditLog> latestPermissionChange = roleLogs.stream()
                .filter(log -> "PERMISSION_GRANT".equalsIgnoreCase(log.getAction()))
                .max(Comparator.comparing(RoleAuditLog::getTimestamp, Comparator.nullsLast(Comparator.naturalOrder())));



        // Add latest role assign ONLY if found
        latestRoleChange.ifPresent(r -> {
            UserActionLog roleLog = new UserActionLog();
            roleLog.setAction("ROLE_ASSIGN");
            roleLog.setTimestamp(r.getTimestamp() != null ? r.getTimestamp() : new Timestamp(System.currentTimeMillis()));
            roleLog.setIpAddress(r.getIpAddress() != null ? r.getIpAddress() : "N/A");
            roleLog.setUserAgent(r.getUserAgent() != null ? r.getUserAgent() : "N/A");
            roleLog.setObjectId(r.getTarget() != null ? r.getTarget() : "N/A");
            actionLogs.add(roleLog);
        });


//


        // Add latest permission grant ONLY if found
        latestPermissionChange.ifPresent(p -> {
            UserActionLog permissionLog = new UserActionLog();
            permissionLog.setAction("PERMISSION_GRANT");
            permissionLog.setTimestamp(p.getTimestamp() != null ? p.getTimestamp() : new Timestamp(System.currentTimeMillis()));
            permissionLog.setIpAddress(p.getIpAddress() != null ? p.getIpAddress() : "N/A");
            permissionLog.setUserAgent(p.getUserAgent() != null ? p.getUserAgent() : "N/A");
            permissionLog.setObjectId(p.getTarget() != null ? p.getTarget() : "N/A");
            actionLogs.add(permissionLog);
        });


        // 3️⃣ File access logs (DOWNLOAD / VIEW)
        List<FileAccessLog> fileAccessLogs = Optional.ofNullable(fileAccessLogRepository.findByUserId(userId))
                .orElse(Collections.emptyList());

        for (FileAccessLog fLog : fileAccessLogs) {
            UserActionLog log = new UserActionLog();
            log.setAction(fLog.getAction() != null ? fLog.getAction() : "FILE_ACCESS");
            log.setTimestamp(fLog.getTimestamp() != null ? fLog.getTimestamp() : new Timestamp(System.currentTimeMillis()));
            log.setIpAddress(fLog.getIpAddress() != null ? fLog.getIpAddress() : "N/A");
            log.setUserAgent(fLog.getUserAgent() != null ? fLog.getUserAgent() : "N/A");
            log.setObjectId(fLog.getFileName() != null ? fLog.getFileName() : "Unknown File");
            actionLogs.add(log);
        }

        // 4️⃣ Sort all logs by timestamp
        actionLogs.sort(Comparator.comparing(UserActionLog::getTimestamp, Comparator.nullsLast(Comparator.naturalOrder())));

        return ResponseEntity.ok(actionLogs);
    }
}

