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
        List<UserSessionAudit> allAudits = userSessionAuditRepository.findAll();
        Map<UUID, UserSessionAudit> latestAuditMap = new HashMap<>();
        for (UserSessionAudit audit : allAudits) {
            UUID userId = audit.getUserId();
            if (!latestAuditMap.containsKey(userId)) {
                latestAuditMap.put(userId, audit);
            } else {
                Timestamp existingLoginTime = latestAuditMap.get(userId).getTimestamp();
                if (audit.getTimestamp() != null && audit.getTimestamp().after(existingLoginTime)) {
                    latestAuditMap.put(userId, audit);
                }
            }
        }
        List<Audit> auditList = new ArrayList<>();
        for (UserSessionAudit audit : latestAuditMap.values()) {
            Audit dto = new Audit();
            dto.setEmail(audit.getEmail());
            dto.setLoginTime(audit.getLoginTime());
            dto.setLogoutTime(audit.getLogoutTime());
            dto.setDuration(audit.getDurationInSeconds());
            dto.setUserId(audit.getUserId());
            auditList.add(dto);
        }
        return ResponseEntity.ok(auditList);
    }



    @Override
    public ResponseEntity<?> getByUser(UUID userId) {
        List<UserActionLog> actionLogs = new ArrayList<>();

        List<UserSessionAudit> sessionAudits = userSessionAuditRepository.findByUserId(userId);
        sessionAudits.stream()
                .filter(audit -> "LOGIN".equalsIgnoreCase(audit.getEventType()) || "LOGOUT".equalsIgnoreCase(audit.getEventType()))
                .forEach(audit -> {
                    UserActionLog log = new UserActionLog();
                    log.setAction(Optional.ofNullable(audit.getEventType()).orElse(null));
                    log.setTimestamp(Optional.ofNullable(audit.getTimestamp()).orElse(null));
                    log.setIpAddress(Optional.ofNullable(audit.getIpAddress()).orElse(null));
                    log.setUserAgent(Optional.ofNullable(audit.getUserAgent()).orElse(null));
                    actionLogs.add(log);
                });

        List<RoleAuditLog> auditLogs = roleAuditLogRepository.findByAffectedUser(userId);

        Optional<RoleAuditLog> latestRoleChange = auditLogs.stream()
                .filter(log -> "ROLE_ASSIGN".equalsIgnoreCase(log.getAction()))
                .max(Comparator.comparing(RoleAuditLog::getTimestamp, Comparator.nullsLast(Comparator.naturalOrder())));

        Optional<RoleAuditLog> latestPermissionChange = auditLogs.stream()
                .filter(log -> "PERMISSION_GRANT".equalsIgnoreCase(log.getAction()))
                .max(Comparator.comparing(RoleAuditLog::getTimestamp, Comparator.nullsLast(Comparator.naturalOrder())));

        UserActionLog roleLog = new UserActionLog();
        roleLog.setAction("ROLE_ASSIGN");
        roleLog.setTimestamp(latestRoleChange.map(RoleAuditLog::getTimestamp).orElse(null));
        roleLog.setIpAddress(latestRoleChange.map(RoleAuditLog::getIpAddress).orElse(null));
        roleLog.setUserAgent(latestRoleChange.map(RoleAuditLog::getUserAgent).orElse(null));
        actionLogs.add(roleLog);

        UserActionLog permissionLog = new UserActionLog();
        permissionLog.setAction("PERMISSION_GRANT");
        permissionLog.setTimestamp(latestPermissionChange.map(RoleAuditLog::getTimestamp).orElse(null));
        permissionLog.setIpAddress(latestPermissionChange.map(RoleAuditLog::getIpAddress).orElse(null));
        permissionLog.setUserAgent(latestPermissionChange.map(RoleAuditLog::getUserAgent).orElse(null));
        actionLogs.add(permissionLog);

        List<FileAccessLog> fileAccessLogs = fileAccessLogRepository.findByUserId(userId);


        Optional<FileAccessLog> latestDownload = fileAccessLogs.stream()
                .filter(log -> "DOWNLOAD".equalsIgnoreCase(log.getAction()))
                .max(Comparator.comparing(FileAccessLog::getTimestamp, Comparator.nullsLast(Comparator.naturalOrder())));

        if (latestDownload.isPresent()) {
            FileAccessLog log = latestDownload.get();
            UserActionLog downloadLog = new UserActionLog();
            downloadLog.setAction("DOWNLOAD");
            downloadLog.setTimestamp(log.getTimestamp());
            downloadLog.setIpAddress(log.getIpAddress());
            downloadLog.setUserAgent(log.getUserAgent());
            downloadLog.setObjectId(log.getFileName());
            actionLogs.add(downloadLog);
        }



        Optional<FileAccessLog> latestView = fileAccessLogs.stream()
                .filter(log -> "VIEW".equalsIgnoreCase(log.getAction()))
                .max(Comparator.comparing(FileAccessLog::getTimestamp, Comparator.nullsLast(Comparator.naturalOrder())));

        if (latestView.isPresent()) {
            FileAccessLog log = latestView.get();
            UserActionLog viewLog = new UserActionLog();
            viewLog.setAction("VIEW");
            viewLog.setTimestamp(log.getTimestamp());
            viewLog.setIpAddress(log.getIpAddress());
            viewLog.setUserAgent(log.getUserAgent());
            viewLog.setObjectId(log.getFileName());
            actionLogs.add(viewLog);
        }



        actionLogs.sort(Comparator.comparing(UserActionLog::getTimestamp, Comparator.nullsLast(Comparator.naturalOrder())));
        return ResponseEntity.ok(actionLogs);
    }

}
