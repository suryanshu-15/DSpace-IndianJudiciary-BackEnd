package org.dspace.content.Diracai;

import jakarta.persistence.*;
import org.hibernate.annotations.GenericGenerator;

import java.util.Date;
import java.util.UUID;

@Entity
@Table(name = "bulk_upload_request")
public class BulkUploadRequest {

    @Id
    @GeneratedValue
    @GenericGenerator(name = "uuid", strategy = "uuid2")
    @Column(name = "bulk_upload_id", columnDefinition = "uuid", updatable = false, nullable = false)
    private UUID bulkUploadId;

    @Column(nullable = false)
    private UUID uploaderId;

    @Column(nullable = false)
    private UUID collectionId;


    @Column(nullable = false)
    private UUID reviewerId;

    @Column(nullable = false)
    private String filename;

    @Column(nullable = false)
    private String status; // CLAIMED, APPROVED, REJECTED, PENDING

    @Column(nullable = false)
    private Date uploadedDate;

    @Column(nullable = false)
    private Date reviewedDate;

    public Date getReviewedDate() {
        return reviewedDate;
    }

    public void setReviewedDate(Date reviewedDate) {
        this.reviewedDate = reviewedDate;
    }

    public UUID getBulkUploadId() {
        return bulkUploadId;
    }

    public void setBulkUploadId(UUID bulkUploadId) {
        this.bulkUploadId = bulkUploadId;
    }

    public UUID getUploaderId() {
        return uploaderId;
    }

    public void setUploaderId(UUID uploaderId) {
        this.uploaderId = uploaderId;
    }

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Date getUploadedDate() {
        return uploadedDate;
    }

    public void setUploadedDate(Date uploadedDate) {
        this.uploadedDate = uploadedDate;
    }

    public UUID getReviewerId() {
        return reviewerId;
    }

    public void setReviewerId(UUID reviewerId) {
        this.reviewerId = reviewerId;
    }

    public UUID getCollectionId() {
        return collectionId;
    }

    public void setCollectionId(UUID collectionId) {
        this.collectionId = collectionId;
    }
}
