package org.dspace.content.Diracai;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "file_hash_record")
public class FileHashRecord {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String fileName;
    private String hashValue;
    private LocalDateTime createdAt;
    private String ackId;
    private String zipStatus;
    private String postResponse;
    private String postStatus;
    private String getCheckResponse;
    private String getCheckStatus;
    private Integer fileCount;

    public Integer getFileCount() {
        return fileCount;
    }

    public void setFileCount(int fileCount) {
        this.fileCount = fileCount;
    }

    @PrePersist
    public void onCreate() {
        this.createdAt =  LocalDateTime.now();
    }


    public String getZipStatus() {
        return zipStatus;
    }

    public String getGetCheckResponse() {
        return getCheckResponse;
    }

    public String getGetCheckStatus() {
        return getCheckStatus;
    }

    public String getPostResponse() {
        return postResponse;
    }

    public String getPostStatus() {
        return postStatus;
    }

    public void setGetCheckResponse(String getCheckResponse) {
        this.getCheckResponse = getCheckResponse;
    }

    public void setGetCheckStatus(String getCheckStatus) {
        this.getCheckStatus = getCheckStatus;
    }

    public void setPostResponse(String postResponse) {
        this.postResponse = postResponse;
    }

    public void setPostStatus(String postStatus) {
        this.postStatus = postStatus;
    }

    public void setZipStatus(String zipStatus) {
        this.zipStatus = zipStatus;
    }

    public FileHashRecord() {}


    // Getters and setters

    public void setAckId(String ackId) {
        this.ackId = ackId;
    }

    public String getAckId() {
        return ackId;
    }

    public Long getId() { return id; }

    public void setId(Long id) { this.id = id; }

    public String getFileName() { return fileName; }

    public void setFileName(String fileName) { this.fileName = fileName; }

    public String getHashValue() { return hashValue; }

    public void setHashValue(String hashValue) { this.hashValue = hashValue; }

    public LocalDateTime getCreatedAt() { return createdAt; }

    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
