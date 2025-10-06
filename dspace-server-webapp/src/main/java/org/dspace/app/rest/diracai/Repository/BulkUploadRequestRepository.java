package org.dspace.app.rest.diracai.Repository;

import org.dspace.content.Diracai.BulkUploadRequest;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface BulkUploadRequestRepository extends JpaRepository<BulkUploadRequest, UUID> {
    List<BulkUploadRequest> findByStatus(String status);
    List<BulkUploadRequest> findAllByStatusNot(String status);
    List<BulkUploadRequest> findByReviewerId(UUID reviewerId);
    List<BulkUploadRequest> findByUploaderId(UUID uploaderId);

}
