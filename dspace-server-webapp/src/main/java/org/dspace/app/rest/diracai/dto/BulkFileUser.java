package org.dspace.app.rest.diracai.dto;

import java.util.Date;
import java.util.UUID;

public class BulkFileUser {
    private UUID uuid;
    private String userName;
    private Date date;

    public void setUuid(UUID uuid) {
        this.uuid = uuid;
    }

    public UUID getUuid() {
        return uuid;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    @Override
    public String toString() {
        return "BulkFileUser{" +
                "uuid=" + uuid +
                ", userName='" + userName + '\'' +
                ", date=" + date +
                '}';
    }
}
