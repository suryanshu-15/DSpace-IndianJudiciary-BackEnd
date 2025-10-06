package org.dspace.app.rest.diracai.dto;

import java.util.Date;
import java.util.List;
import java.util.UUID;

public class BulkUploadRequestResponseDTO {
    private UUID requestId;
    private String filename;
    private String status;
    private Date uploadedDate;
    private UUID uploaderId;
    private List<BulkUploadItemDTO> items;

    public void setUploadedDate(Date uploadedDate) {
        this.uploadedDate = uploadedDate;
    }

    public Date getUploadedDate() {
        return uploadedDate;
    }

    public List<BulkUploadItemDTO> getItems() {
        return items;
    }

    public String getFilename() {
        return filename;
    }

    public UUID getRequestId() {
        return requestId;
    }

    public UUID getUploaderId() {
        return uploaderId;
    }

    public String getStatus() {
        return status;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    public void setRequestId(UUID requestId) {
        this.requestId = requestId;
    }

    public void setItems(List<BulkUploadItemDTO> items) {
        this.items = items;
    }

    public void setUploaderId(UUID uploaderId) {
        this.uploaderId = uploaderId;
    }

    public void setStatus(String status) {
        this.status = status;
    }

}
