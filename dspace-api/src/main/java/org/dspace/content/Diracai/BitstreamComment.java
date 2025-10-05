package org.dspace.content.Diracai;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "bitstream_comment")
public class BitstreamComment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(nullable = false)
    private String text;

    @Column(name = "bitstream_id", nullable = false)
    private UUID bitstreamId;

    @Column(name = "commenter_id", nullable = false)
    private UUID commenterId;

    @Column(name = "comment_date", nullable = false)
    private LocalDateTime commentDate;

    @Column(name = "is_deleted", nullable = false)
    private boolean isDeleted;

    // Default constructor (required by JPA)
    public BitstreamComment() {}

    // Custom constructor for convenience
    public BitstreamComment(String text, UUID bitstreamId, UUID commenterId) {
        this.text = text;
        this.bitstreamId = bitstreamId;
        this.commenterId = commenterId;
        this.isDeleted = false; // Default to false
    }

    @PrePersist
    protected void onCreate() {
        this.commentDate = LocalDateTime.now();
    }

    // Getters and Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getText() { return text; }
    public void setText(String text) { this.text = text; }

    public UUID getBitstreamId() { return bitstreamId; }
    public void setBitstreamId(UUID bitstreamId) { this.bitstreamId = bitstreamId; }

    public UUID getCommenterId() { return commenterId; }
    public void setCommenterId(UUID commenterId) { this.commenterId = commenterId; }

    public LocalDateTime getCommentDate() { return commentDate; }
    public void setCommentDate(LocalDateTime commentDate) { this.commentDate = commentDate; }

    public boolean isDeleted() { return isDeleted; }
    public void setDeleted(boolean deleted) { isDeleted = deleted; }
}

