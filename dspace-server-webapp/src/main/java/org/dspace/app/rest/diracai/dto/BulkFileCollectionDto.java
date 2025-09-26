package org.dspace.app.rest.diracai.dto;

import java.util.UUID;

public class BulkFileCollectionDto {
    private UUID collectionId;
    private String collectionName;

    public void setCollectionId(UUID collectionId) {
        this.collectionId = collectionId;
    }

    public UUID getCollectionId() {
        return collectionId;
    }

    public String getCollectionName() {
        return collectionName;
    }

    public void setCollectionName(String collectionName) {
        this.collectionName = collectionName;
    }

    @Override
    public String toString() {
        return "BulkFileCollectionDto{" +
                "collectionId=" + collectionId +
                ", collectionName='" + collectionName + '\'' +
                '}';
    }

}
