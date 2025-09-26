package org.dspace.app.rest.diracai.service;

import org.dspace.app.rest.diracai.dto.AuthTokenPayload;
import org.dspace.app.rest.diracai.dto.BulkFileDto;
import org.dspace.app.rest.diracai.dto.BulkUploadRequestResponseDTO;
import org.dspace.content.Diracai.BulkUploadRequest;
import org.dspace.core.Context;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

public interface BulkUploadRequestService {
    BulkUploadRequest createRequest(Context context, MultipartFile file ,UUID uuid);
    BulkUploadRequest approveRequest(UUID uuid, Context context, AuthTokenPayload auth);
    BulkUploadRequest rejectRequest(UUID uuid, Context context);
    List<BulkUploadRequest> findAll();
    List<BulkFileDto> findByStatus(Context context , String status);
    BulkUploadRequestResponseDTO getFile(UUID uuid);
    List<BulkFileDto> getPooledTasksForReviewer(Context context, UUID reviewerId);
}
