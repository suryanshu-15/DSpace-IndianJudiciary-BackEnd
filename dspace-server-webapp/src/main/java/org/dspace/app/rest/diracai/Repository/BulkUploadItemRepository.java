package org.dspace.app.rest.diracai.Repository;


import org.dspace.content.Diracai.BulkUploadItem;
import org.dspace.content.Diracai.BulkUploadRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

public interface BulkUploadItemRepository extends JpaRepository<BulkUploadItem, UUID> {

    @Query("SELECT i FROM BulkUploadItem i LEFT JOIN FETCH i.metadata WHERE i.uploadRequest = :uploadRequestId")
    List<BulkUploadItem> findWithMetadataByUploadRequest(@Param("uploadRequestId") UUID uploadRequestId);

}
