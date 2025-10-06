package org.dspace.content.Diracai;

import jakarta.persistence.*;
import java.util.UUID;
import java.sql.Timestamp;

@Entity
@Table(name = "login_device_audit")
public class LoginDeviceAudit {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @Column(name = "eperson_id", nullable = false)
    private UUID epersonUUID;

    @Column(name = "ip_address")
    private String ipAddress;

    @Column(name = "user_agent", columnDefinition = "TEXT")
    private String userAgent;

    @Column(name = "device_id")
    private String deviceId;

    @Column(name = "login_time")
    private Timestamp loginTime;

    @Column(name = "status")  // e.g. SUCCESS / FAILURE
    private String status;

    @Column(name = "failed_attempts")
    private int failedAttempts = 0;

    // Getters and Setters
    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public UUID getEpersonUUID() {
        return epersonUUID;
    }

    public void setEpersonUUID(UUID epersonUUID) {
        this.epersonUUID = epersonUUID;
    }

    public String getIpAddress() {
        return ipAddress;
    }

    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
    }

    public String getUserAgent() {
        return userAgent;
    }

    public void setUserAgent(String userAgent) {
        this.userAgent = userAgent;
    }

    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    public Timestamp getLoginTime() {
        return loginTime;
    }

    public void setLoginTime(Timestamp loginTime) {
        this.loginTime = loginTime;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public int getFailedAttempts() {
        return failedAttempts;
    }

    public void setFailedAttempts(int failedAttempts) {
        this.failedAttempts = failedAttempts;
    }
}
