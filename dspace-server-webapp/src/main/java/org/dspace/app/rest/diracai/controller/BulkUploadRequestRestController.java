package org.dspace.app.rest.diracai.controller;

import jakarta.servlet.http.HttpServletRequest;
import org.dspace.app.rest.diracai.dto.AuthTokenPayload;
import org.dspace.app.rest.diracai.dto.BulkFileDto;
import org.dspace.app.rest.diracai.dto.BulkUploadRequestResponseDTO;
import org.dspace.app.rest.diracai.service.BulkUploadRequestService;
import org.dspace.app.rest.diracai.service.impl.BulkUploadRequestServiceImpl;
import org.dspace.app.rest.utils.ContextUtil;
import org.dspace.content.Diracai.BulkUploadRequest;
import org.dspace.core.Context;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/bulk-upload")
public class BulkUploadRequestRestController {

    @Autowired
    private BulkUploadRequestService service;

    private static final Logger logger = LoggerFactory.getLogger(BulkUploadRequestRestController.class);

    @PostMapping("/upload/{uuid}")
    public ResponseEntity<BulkUploadRequest> upload(HttpServletRequest httpRequest,
                                                    @RequestParam("file") MultipartFile file,
                                                    @PathVariable UUID uuid) {
        try {
            Context context = ContextUtil.obtainContext(httpRequest);

            logger.info("📎 File received: name='{}', size={} bytes", file.getOriginalFilename(), file.getSize());

            BulkUploadRequest request = service.createRequest(context, file, uuid);

            logger.info("✅ Upload successful for BulkUploadRequest UUID: {}", request.getBulkUploadId());
            return ResponseEntity.ok(request);

        } catch (Exception e) {
            logger.error("❌ Failed to upload bulk file for UUID: {} — {}", uuid, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }



    @PostMapping("/approve/{uuid}")
    public ResponseEntity<?> approve(@PathVariable UUID uuid, HttpServletRequest httpRequest) {
        try {
            Context context = ContextUtil.obtainContext(httpRequest);
            String authHeader = httpRequest.getHeader("Authorization");
            String csrfToken = httpRequest.getHeader("X-CSRF-TOKEN");

            AuthTokenPayload auth = new AuthTokenPayload();
            auth.setAuthorization(authHeader);
            auth.setCsrfToken(csrfToken);

            BulkUploadRequest result = service.approveRequest(uuid, context, auth);
            return ResponseEntity.ok(result);

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.GONE)
                    .body(Map.of("error", e.getClass().getSimpleName(), "message", e.getMessage()));
        }
    }


    @PostMapping("/reject/{uuid}")
    public ResponseEntity<BulkUploadRequest> reject(@PathVariable UUID uuid, HttpServletRequest httpRequest) {
        Context context = ContextUtil.obtainContext(httpRequest);
        return ResponseEntity.ok(service.rejectRequest(uuid, context));
    }

    @GetMapping("/all")
    public ResponseEntity<List<BulkUploadRequest>> all() {
        return ResponseEntity.ok(service.findAll());
    }

    @GetMapping("/status/{status}")
    public ResponseEntity<List<BulkFileDto>> byStatus(@PathVariable String status, HttpServletRequest httpRequest) {
        Context context = ContextUtil.obtainContext(httpRequest);
        return ResponseEntity.ok(service.findByStatus(context,status));
    }

    @GetMapping("/pooled")
    public ResponseEntity<List<BulkFileDto>> getPooledTasks(HttpServletRequest httpRequest) {
        try {
            Context context = ContextUtil.obtainContext(httpRequest);

            UUID reviewerId = context.getCurrentUser().getID(); // Authenticated user

            List<BulkFileDto> pooledTasks = service.getPooledTasksForReviewer(context, reviewerId);
            return ResponseEntity.ok(pooledTasks);
        } catch (Exception e) {
            logger.error("❌ Failed to retrieve pooled tasks — {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @GetMapping("/{uuid}")
    public ResponseEntity<?> getFile(@PathVariable UUID uuid) {
        try {
            return ResponseEntity.ok(service.getFile(uuid));
        } catch (Exception e) {
            e.printStackTrace(); // Temporary for debugging
            return ResponseEntity.status(500).body(Map.of(
                    "message", "Failed to fetch bulk upload file",
                    "error", e.getMessage()
            ));
        }
    }

}
