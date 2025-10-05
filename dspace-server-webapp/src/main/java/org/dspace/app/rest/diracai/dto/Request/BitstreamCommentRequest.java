package org.dspace.app.rest.diracai.dto.Request;

import java.util.UUID;

public class BitstreamCommentRequest {
    private String comment;
    private UUID bitstreamId;

    public String getComment() { return comment; }
    public void setComment(String comment) { this.comment = comment; }

    public UUID getBitstreamId() { return bitstreamId; }
    public void setBitstreamId(UUID bitstreamId) { this.bitstreamId = bitstreamId; }
}
