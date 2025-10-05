package org.dspace.app.rest.diracai.dto;

public class EncryptRequest {
    private String userId;
    private String bitstreamId;

    public String getUserId() {
        return userId;
    }

    public String getBitstreamId() {
        return bitstreamId;
    }

    public void setBitstreamId(String bitstreamId) {
        this.bitstreamId = bitstreamId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }
}
