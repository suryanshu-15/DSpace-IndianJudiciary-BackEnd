package org.dspace.app.rest.diracai.dto;

import java.sql.Timestamp;
import java.util.UUID;

public class Audit {
    private UUID userId;
    private String email;
    private Timestamp loginTime;
    private Timestamp logoutTime;
    private Long duration;

    public void setDuration(Long duration) {
        this.duration = duration;
    }

    public Long getDuration() {
        return duration;
    }

    public void setUserId(UUID userId) {
        this.userId = userId;
    }

    public UUID getUserId() {
        return userId;
    }

    public void setLoginTime(Timestamp loginTime) {
        this.loginTime = loginTime;
    }

    public Timestamp getLoginTime() {
        return loginTime;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getEmail() {
        return email;
    }

    public Timestamp getLogoutTime() {
        return logoutTime;
    }

    public void setLogoutTime(Timestamp logoutTime) {
        this.logoutTime = logoutTime;
    }
}
