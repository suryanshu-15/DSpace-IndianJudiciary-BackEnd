package org.dspace.content.Diracai;

import jakarta.persistence.*;
import org.hibernate.annotations.GenericGenerator;

import java.util.UUID;

@Entity
@Table(name = "bulk_upload_metadata")
public class BulkUploadItemMetadata {

    @Id
    @GeneratedValue
    @GenericGenerator(name = "uuid", strategy = "uuid2")
    @Column(name = "id", columnDefinition = "uuid", updatable = false, nullable = false)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "item_id", nullable = false)
    private BulkUploadItem item;


    @Column(name = "metadata_key", nullable = false)
    private String key;

    @Column(name = "metadata_value", nullable = false)
    private String value;

    // Getters and setters
    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }

    public BulkUploadItem getItem() { return item; }
    public void setItem(BulkUploadItem item) { this.item = item; }

    public String getKey() { return key; }
    public void setKey(String key) { this.key = key; }

    public String getValue() { return value; }
    public void setValue(String value) { this.value = value; }
}
