package org.dspace.app.rest.diracai.dto;

import java.util.UUID;

public class BulkFileDto {
    private UUID BulkFileId;
    private BulkFileCollectionDto collection;
    private BulkFileUser uploader;
    private String status;
    private String fileName;
    private BulkFileUser reviewer;

    public BulkFileUser getReviewer() {
        return reviewer;
    }

    public BulkFileUser getUploader() {
        return uploader;
    }

    public void setReviewer(BulkFileUser reviewer) {
        this.reviewer = reviewer;
    }

    public void setUploader(BulkFileUser uploader) {
        this.uploader = uploader;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public UUID getBulkFileId() {
        return BulkFileId;
    }

    public void setBulkFileId(UUID bulkFileId) {
        BulkFileId = bulkFileId;
    }

    public BulkFileCollectionDto getCollection() {
        return collection;
    }

    public void setCollection(BulkFileCollectionDto collection) {
        this.collection = collection;
    }

}
