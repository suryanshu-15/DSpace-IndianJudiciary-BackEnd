
package org.dspace.app.rest.diracai.dto.Response;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.UUID;

public class BitstreamCommentResponse {
    private int id;
    private String text;
    private String commenterName;
    private LocalDateTime createdDate;
    private boolean deleted;

    public void setDeleted(boolean deleted) {
        this.deleted = deleted;
    }

    public boolean isDeleted() {
        return deleted;
    }

    public int getId() {
        return id;
    }

    public String getCommenterName() {
        return commenterName;
    }

    public LocalDateTime getCreatedDate() {
        return createdDate;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setCreatedDate(LocalDateTime createdDate) {
        this.createdDate = createdDate;
    }

    public void setCommenterName(String commenterName) {
        this.commenterName = commenterName;
    }

    @Override
    public String toString() {
        return "BitstreamCommentResponse{" +
                "id=" + id +
                ", text='" + text + '\'' +
                ", commenterName='" + commenterName + '\'' +
                ", createdDate=" + createdDate +
                ", deleted=" + deleted +
                '}';
    }

    public BitstreamCommentResponse() {

    }
    public BitstreamCommentResponse(int id ,LocalDateTime dateTime,String text,Boolean deleted,String commenterName) {
        this.id = id;
        this.commenterName = commenterName;
        this.text = text;
        this.deleted = deleted;
        this.createdDate=dateTime;
    }
}
