package org.dspace.content.Diracai;

import jakarta.persistence.*;

import java.sql.Timestamp;
import java.util.UUID;

@Entity
@Table(name = "file_access_log")
public class FileAccessLog {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "file_access_log_seq")
    @SequenceGenerator(name = "file_access_log_seq", sequenceName = "file_access_log_id_seq", allocationSize = 1)
    private Long id;

    @Column(name = "file_name")
    private String fileName;

    private String action;

    @Column(name = "user_id")
    private UUID userId;

    @Column(name = "file_id")
    private UUID fileId;

    @Column(name = "user_email")
    private String userEmail;

    @Column(name = "ip_address")
    private String ipAddress;

    @Column(name = "user_agent")
    private String userAgent;

    @Temporal(TemporalType.TIMESTAMP)
    private Timestamp timestamp;

    private boolean suspicious;

    // ===== Getters and Setters =====

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public UUID getUserId() {
        return userId;
    }

    public void setUserId(UUID userId) {
        this.userId = userId;
    }

    public UUID getFileId() {
        return fileId;
    }

    public void setFileId(UUID fileId) {
        this.fileId = fileId;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
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

    public Timestamp getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Timestamp timestamp) {
        this.timestamp = timestamp;
    }

    public boolean isSuspicious() {
        return suspicious;
    }

    public void setSuspicious(boolean suspicious) {
        this.suspicious = suspicious;
    }
}

