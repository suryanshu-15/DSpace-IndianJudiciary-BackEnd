package org.dspace.content.Diracai;

import jakarta.persistence.*;
import org.hibernate.annotations.GenericGenerator;

import java.util.*;

@Entity
@Table(name = "bulk_upload_item")
public class BulkUploadItem {

    @Id
    @GeneratedValue
    @GenericGenerator(name = "uuid", strategy = "uuid2")
    @Column(name = "bulk_upload_id", columnDefinition = "uuid", updatable = false, nullable = false)
    private UUID uuid;

    @Column(name = "upload_request_id", nullable = false, columnDefinition = "uuid")
    private UUID uploadRequest;

    @Column(name = "item_folder", nullable = false)
    private String itemFolder;

    @OneToMany(mappedBy = "item", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<BulkUploadItemMetadata> metadata = new ArrayList<>();

    // Getters and setters
    public UUID getUuid() { return uuid; }
    public void setUuid(UUID uuid) { this.uuid = uuid; }

    public UUID getUploadRequest() { return uploadRequest; }
    public void setUploadRequest(UUID uploadRequest) { this.uploadRequest = uploadRequest; }

    public String getItemFolder() { return itemFolder; }
    public void setItemFolder(String itemFolder) { this.itemFolder = itemFolder; }

    public List<BulkUploadItemMetadata> getMetadata() { return metadata; }
    public void setMetadata(List<BulkUploadItemMetadata> metadata) { this.metadata = metadata; }
}
