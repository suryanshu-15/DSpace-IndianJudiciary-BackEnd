package org.dspace.content.Diracai;

import jakarta.persistence.*;
import java.sql.Timestamp;
import java.util.UUID;

@Entity
@Table(name = "role_audit_log")
public class RoleAuditLog {

    @Id
    @GeneratedValue
    private UUID id;

    @Column(name = "acted_by") // who performed the change
    private UUID actedBy;

    @Column(name = "affected_user")
    private UUID affectedUser;

    @Column(name = "action") // e.g., ROLE_ASSIGN, PERMISSION_GRANT
    private String action;

    @Column(name = "target") // target group/policy/item name/id
    private String target;

    @Column(name = "timestamp")
    private Timestamp timestamp;

    @Column(name = "ip_address")
    private String ipAddress;

    @Column(name = "user_agent", length = 512)
    private String userAgent;

    public void setUserAgent(String userAgent) {
        this.userAgent = userAgent;
    }

    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
    }

    public String getUserAgent() {
        return userAgent;
    }

    public UUID getId() {
        return id;
    }

    public String getIpAddress() {
        return ipAddress;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getAction() {
        return action;
    }

    public Timestamp getTimestamp() {
        return timestamp;
    }

    public String getTarget() {
        return target;
    }

    public UUID getActedBy() {
        return actedBy;
    }

    public UUID getAffectedUser() {
        return affectedUser;
    }

    public void setActedBy(UUID actedBy) {
        this.actedBy = actedBy;
    }

    public void setTarget(String target) {
        this.target = target;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public void setAffectedUser(UUID affectedUser) {
        this.affectedUser = affectedUser;
    }

    public void setTimestamp(Timestamp timestamp) {
        this.timestamp = timestamp;
    }
}

