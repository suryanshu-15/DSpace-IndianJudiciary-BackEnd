package org.dspace.app.rest.diracai.dto;

import java.util.Map;
import java.util.UUID;

public class BulkUploadItemDTO {
    private UUID itemId;
    private String itemFolder;
    private Map<String, String> metadata;

    public void setItemFolder(String itemFolder) {
        this.itemFolder = itemFolder;
    }

    public String getItemFolder() {
        return itemFolder;
    }

    public UUID getItemId() {
        return itemId;
    }

    public void setItemId(UUID itemId) {
        this.itemId = itemId;
    }

    public Map<String, String> getMetadata() {
        return metadata;
    }

    public void setMetadata(Map<String, String> metadata) {
        this.metadata = metadata;
    }
}
