package org.dspace.app.rest.diracai.dto;
import java.sql.Timestamp;

public class UserActionLog {
    private String action;
    private Timestamp timestamp;
    private String ipAddress;
    private String userAgent;
    private String objectId; // For things like policy change or role target

    // Getters and Setters

    public String getAction() { return action; }
    public void setAction(String action) { this.action = action; }

    public Timestamp getTimestamp() { return timestamp; }
    public void setTimestamp(Timestamp timestamp) { this.timestamp = timestamp; }

    public String getIpAddress() { return ipAddress; }
    public void setIpAddress(String ipAddress) { this.ipAddress = ipAddress; }

    public String getUserAgent() { return userAgent; }
    public void setUserAgent(String userAgent) { this.userAgent = userAgent; }

    public String getObjectId() { return objectId; }
    public void setObjectId(String objectId) { this.objectId = objectId; }
}
