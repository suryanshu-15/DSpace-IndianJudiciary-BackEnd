package org.dspace.content.Diracai;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.sql.Timestamp;
import java.util.UUID;

@Entity
@Table(name = "user_session_audit")
public class UserSessionAudit {

    @Id
    @Column(columnDefinition = "uuid")
    private UUID id;

    @Column(name = "user_id", nullable = false)
    private UUID userId;

    @Column(name = "email")
    private String email;

    @Column(name = "ip_address")
    private String ipAddress;

    @Column(name = "user_agent")
    private String userAgent;

    @Column(name = "event_type")
    private String eventType; // LOGIN / LOGOUT

    @Column(name = "timestamp")
    private Timestamp timestamp;

    // 🔽 NEW FIELDS
    @Column(name = "session_id")
    private String sessionId;

    @Column(name = "login_time")
    private Timestamp loginTime;

    @Column(name = "logout_time")
    private Timestamp logoutTime;

    @Column(name = "duration_seconds")
    private Long durationInSeconds;

    public UserSessionAudit() {}

    // Getters and Setters

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }

    public UUID getUserId() { return userId; }
    public void setUserId(UUID userId) { this.userId = userId; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getIpAddress() { return ipAddress; }
    public void setIpAddress(String ipAddress) { this.ipAddress = ipAddress; }

    public String getUserAgent() { return userAgent; }
    public void setUserAgent(String userAgent) { this.userAgent = userAgent; }

    public String getEventType() { return eventType; }
    public void setEventType(String eventType) { this.eventType = eventType; }

    public Timestamp getTimestamp() { return timestamp; }
    public void setTimestamp(Timestamp timestamp) { this.timestamp = timestamp; }

    public String getSessionId() { return sessionId; }
    public void setSessionId(String sessionId) { this.sessionId = sessionId; }

    public Timestamp getLoginTime() { return loginTime; }
    public void setLoginTime(Timestamp loginTime) { this.loginTime = loginTime; }

    public Timestamp getLogoutTime() { return logoutTime; }
    public void setLogoutTime(Timestamp logoutTime) { this.logoutTime = logoutTime; }

    public Long getDurationInSeconds() { return durationInSeconds; }
    public void setDurationInSeconds(Long durationInSeconds) { this.durationInSeconds = durationInSeconds; }
}
