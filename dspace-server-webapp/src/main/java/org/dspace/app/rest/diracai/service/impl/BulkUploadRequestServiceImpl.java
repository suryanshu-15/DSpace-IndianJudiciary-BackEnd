package org.dspace.app.rest.diracai.service.impl;


import org.dspace.app.rest.converter.DSpaceRunnableParameterConverter;
import org.dspace.app.rest.diracai.Repository.BulkUploadItemRepository;
import org.dspace.app.rest.diracai.Repository.BulkUploadRequestRepository;
import org.dspace.app.rest.diracai.dto.*;
import org.dspace.app.rest.diracai.service.BulkUploadRequestService;
import org.dspace.app.rest.diracai.util.BulkUploadRequestUtil;
import org.dspace.app.rest.diracai.util.FileMultipartFile;
import org.dspace.app.rest.repository.ScriptRestRepository;
import org.dspace.authorize.service.AuthorizeService;
import org.dspace.content.Diracai.BulkUploadItem;
import org.dspace.content.Diracai.BulkUploadItemMetadata;
import org.dspace.content.Diracai.BulkUploadRequest;
import org.dspace.content.service.*;
import org.dspace.core.Context;
import org.dspace.eperson.EPerson;
import org.dspace.content.Collection;

import org.dspace.eperson.Group;
import org.dspace.eperson.service.EPersonService;
import org.dspace.eperson.service.GroupService;
import org.dspace.scripts.service.ScriptService;
import org.dspace.services.RequestService;
import org.dspace.xoai.services.api.context.ContextService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.dspace.services.ConfigurationService;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileNotFoundException;

import java.sql.SQLException;
import java.util.*;
import java.util.stream.Collectors;



@Service
public class BulkUploadRequestServiceImpl implements BulkUploadRequestService {

    @Autowired
    private ScriptService scriptService;

    @Autowired
    private ConfigurationService configurationService;

    @Autowired
    private BulkUploadRequestRepository bulkUploadRequestRepository;

    @Autowired
    private BulkUploadItemRepository bulkUploadItemRepository;

    @Autowired
    private ItemService itemService;

    @Autowired
    private ContextService contextService;

    @Autowired
    private BulkUploadRequestUtil bulkUploadRequestUtil;

    @Autowired
    private CollectionService collectionService;

    @Autowired
    private WorkspaceItemService workspaceItemService;

    @Autowired
    private InstallItemService installItemService;

    @Autowired
    private BitstreamService bitstreamService;

    @Autowired
    private BundleService bundleService;

    @Autowired
    private DSpaceRunnableParameterConverter parameterConverter;

    @Autowired
    private ScriptRestRepository scriptRestRepository;

    @Autowired
    private RequestService requestService;


    @Autowired
    private EPersonService epersonService;

    @Autowired
    private AuthorizeService authorizeService;

    @Autowired
    private GroupService groupService;


    private static final Logger logger = LoggerFactory.getLogger(BulkUploadRequestServiceImpl.class);

    private static final Logger log = LoggerFactory.getLogger(BulkUploadRequestServiceImpl.class);



    @Override
    public List<BulkFileDto> getPooledTasksForReviewer(Context context, UUID reviewerId) {
        List<BulkUploadRequest> bulkUploadRequests = new ArrayList<>();
        List<BulkFileDto> bulkFileDtos = new ArrayList<>();

        EPerson user;
        try {
            user = epersonService.find(context, reviewerId);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        if (user == null) {
            throw new RuntimeException("User not found: " + reviewerId);
        }

        boolean isAdmin;
        try {
            isAdmin = authorizeService.isAdmin(context, user);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        if (isAdmin) {
            bulkUploadRequests.addAll(bulkUploadRequestRepository.findAllByStatusNot("CLAIMED"));
        } else {
            bulkUploadRequests.addAll(bulkUploadRequestRepository.findByReviewerId(reviewerId));
            bulkUploadRequests.addAll(bulkUploadRequestRepository.findByUploaderId(reviewerId));
        }

        for (BulkUploadRequest bulkUploadRequest : bulkUploadRequests) {
            BulkFileDto bulkFileDto = new BulkFileDto();

            // Prepare collection info
            BulkFileCollectionDto bulkFileCollectionDto = new BulkFileCollectionDto();
            try {
                Collection collection = collectionService.find(context, bulkUploadRequest.getCollectionId());
                if (collection != null) {
                    bulkFileCollectionDto.setCollectionId(collection.getID());
                    bulkFileCollectionDto.setCollectionName(collection.getName());
                }
            } catch (SQLException e) {
                throw new RuntimeException("Error fetching collection", e);
            }

            // Prepare uploader info
            BulkFileUser uploader = new BulkFileUser();
            try {
                EPerson uploaderPerson = epersonService.find(context, bulkUploadRequest.getUploaderId());
                if (uploaderPerson != null) {
                    uploader.setUuid(uploaderPerson.getID());
                    uploader.setUserName(uploaderPerson.getName());
                    uploader.setDate(bulkUploadRequest.getUploadedDate());
                }
            } catch (SQLException e) {
                throw new RuntimeException("Error fetching uploader", e);
            }

            // Prepare reviewer info
            BulkFileUser reviewer = new BulkFileUser();
            try {
                EPerson reviewerPerson = epersonService.find(context, bulkUploadRequest.getReviewerId());
                if (reviewerPerson != null) {
                    reviewer.setUuid(reviewerPerson.getID());
                    reviewer.setUserName(reviewerPerson.getName());
                    reviewer.setDate(bulkUploadRequest.getReviewedDate());
                }
            } catch (SQLException e) {
                throw new RuntimeException("Error fetching reviewer", e);
            }

            // Assemble the DTO
            bulkFileDto.setBulkFileId(bulkUploadRequest.getBulkUploadId());
            bulkFileDto.setCollection(bulkFileCollectionDto);
            bulkFileDto.setFileName(bulkUploadRequest.getFilename());
            bulkFileDto.setStatus(bulkUploadRequest.getStatus());
            bulkFileDto.setUploader(uploader);
            bulkFileDto.setReviewer(reviewer);

            bulkFileDtos.add(bulkFileDto);
        }

        return bulkFileDtos;
    }





    @Override
    public BulkUploadRequest approveRequest(UUID uuid, Context context, AuthTokenPayload auth) {
        log.info("✅ Approving bulk upload request ID: {}", uuid);

        BulkUploadRequest req = bulkUploadRequestRepository.findById(uuid)
                .orElseThrow(() -> new RuntimeException("BulkUploadRequest not found: " + uuid));

        EPerson currentUser = context.getCurrentUser();
        if (currentUser == null) {
            throw new RuntimeException("No current user found in context");
        }

        boolean isAdmin;
        try {
            isAdmin = authorizeService.isAdmin(context);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        Group reviewerGroup;
        try {
            reviewerGroup = groupService.findByName(context, "reviewer");
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        boolean isReviewer;
        try {
            isReviewer = reviewerGroup != null && groupService.isMember(context, currentUser, reviewerGroup);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        if (!(isAdmin && isReviewer)) {
            log.warn("⛔ Access denied: User {} must be both Admin and Reviewer.", currentUser.getID());
            throw new RuntimeException("User must be both Admin and Reviewer to approve the request.");
        }

        req.setStatus("APPROVED");
        req.setReviewerId(currentUser.getID());
        req.setReviewedDate(new Date());
        bulkUploadRequestRepository.save(req);

        try {
            String dspaceDir = configurationService.getProperty("dspace.dir");
            File zipFile = new File(dspaceDir + "/bulk_upload/" + uuid + "/" + uuid + ".zip");

            if (!zipFile.exists()) {
                throw new FileNotFoundException("ZIP file not found: " + zipFile.getAbsolutePath());
            }

            MultipartFile multipartFile = new FileMultipartFile(zipFile);

            scriptRestRepository.startProcess(context, "import", List.of(multipartFile));
            log.info("✅ Import script triggered via startProcess for UUID: {}", uuid);

        } catch (Exception e) {
            log.error("❌ Script trigger failed for UUID {}: {}", uuid, e.getMessage(), e);
            throw new RuntimeException("Script trigger failed", e);
        }

        return req;
    }



    @Override
    public BulkUploadRequest rejectRequest(UUID uuid, Context context) {
        BulkUploadRequest req = bulkUploadRequestRepository.findById(uuid).orElseThrow();


        EPerson currentUser = context.getCurrentUser();
        if (currentUser == null) {
            throw new RuntimeException("No current user found in context");
        }

        boolean isAdmin;
        try {
            isAdmin = authorizeService.isAdmin(context);
        } catch (SQLException e) {
            throw new RuntimeException("Error checking admin role", e);
        }

        Group reviewerGroup;
        try {
            reviewerGroup = groupService.findByName(context, "reviewer");
            if (reviewerGroup == null) {
                throw new RuntimeException("Reviewer group not found");
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error retrieving Reviewer group", e);
        }

        boolean isReviewer;
        try {
            isReviewer = groupService.isMember(context, currentUser, reviewerGroup);
        } catch (SQLException e) {
            throw new RuntimeException("Error checking Reviewer group membership", e);
        }

        // ✅ Enforce both Admin AND Reviewer requirement
        if (!(isAdmin && isReviewer)) {
            log.warn("⛔ Access denied: User {} must be both Admin and Reviewer to reject.", currentUser.getID());
            throw new RuntimeException("User must be both Admin and Reviewer to reject the request.");
        }
        req.setStatus("REJECTED");
        req.setReviewerId(currentUser.getID());
        req.setReviewedDate(new Date());
        return bulkUploadRequestRepository.save(req);
    }


    @Override
    public BulkUploadRequest createRequest(Context context, MultipartFile file, UUID uuid) {
        EPerson currentUser = context.getCurrentUser();
        if (currentUser == null) {
            log.error("❌ No authenticated user in context during bulk upload creation.");
            throw new RuntimeException("No authenticated user in context");
        }

        // Check if user is Admin or in Uploader group
        boolean isAdmin = false;
        try {
            isAdmin = authorizeService.isAdmin(context);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        Group uploaderGroup;
        try {
            uploaderGroup = groupService.findByName(context, "uploader");
        } catch (Exception e) {
            log.error("❌ Could not find Uploader group.", e);
            throw new RuntimeException("Uploader group not found", e);
        }

        boolean isUploader = false;
        try {
            isUploader = uploaderGroup != null && groupService.isMember(context, currentUser, uploaderGroup);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        if (!(isAdmin && isUploader)) {
            log.warn("⛔ Access denied: User {} must be both Admin and Uploader.", currentUser.getID());
            throw new RuntimeException("User must be both Admin and Uploader to upload files.");
        }
        log.info("📥 Initiating bulk upload request creation by user: {}", currentUser.getID());
        log.info("📎 Received file: name='{}', size={} bytes", file.getOriginalFilename(), file.getSize());

        BulkUploadRequest req = new BulkUploadRequest();
        req.setUploaderId(currentUser.getID());
        req.setFilename(file.getOriginalFilename());
        req.setStatus("CLAIMED");
        req.setUploadedDate(new Date());
        req.setCollectionId(uuid);

        req = bulkUploadRequestRepository.save(req);
        log.info("📝 Created BulkUploadRequest with ID: {} for collection: {}", req.getBulkUploadId(), uuid);

        try {
            bulkUploadRequestUtil.handleZipUpload(context, file, req.getBulkUploadId());
            log.info("✅ Successfully extracted and processed ZIP for request ID: {}", req.getBulkUploadId());
        } catch (Exception e) {
            log.error("❌ Failed to extract ZIP file for request ID: {} — {}", req.getBulkUploadId(), e.getMessage(), e);
            throw new RuntimeException("Failed to extract zip file", e);
        }

        return req;
    }




    @Override
    public List<BulkUploadRequest> findAll() {
        return bulkUploadRequestRepository.findAll();
    }

    @Override
    public List<BulkFileDto> findByStatus(Context context , String status) {
        log.info("🔍 Fetching BulkUploadRequests with status: {}", status);

        List<BulkUploadRequest> bulkUploadRequests = bulkUploadRequestRepository.findByStatus(status);
        log.info("📄 Found {} requests with status '{}'", bulkUploadRequests.size(), status);

        List<BulkFileDto> bulkFileDtos = new ArrayList<>();

        for (BulkUploadRequest bulkUploadRequest : bulkUploadRequests) {
            log.debug("📦 Processing BulkUploadRequest ID: {}", bulkUploadRequest.getBulkUploadId());

            BulkFileDto bulkFileDto = new BulkFileDto();
            BulkFileCollectionDto bulkFileCollectionDto = new BulkFileCollectionDto();
            BulkFileUser uploaderUser = new BulkFileUser();
            BulkFileUser reviewerUser = new BulkFileUser();

            try {
                Collection collection = collectionService.find(context, bulkUploadRequest.getCollectionId());
                bulkFileCollectionDto.setCollectionId(bulkUploadRequest.getCollectionId());
                bulkFileCollectionDto.setCollectionName(collection != null ? collection.getName() : "Unknown");
                log.debug("📁 Collection resolved: {}", bulkFileCollectionDto);
            } catch (SQLException e) {
                log.error("❌ Failed to fetch collection for ID: {}", bulkUploadRequest.getCollectionId(), e);
                throw new RuntimeException(e);
            }

            try {
                EPerson uploader = epersonService.find(context, bulkUploadRequest.getUploaderId());
                if (uploader != null) {
                    uploaderUser.setUuid(uploader.getID());
                    uploaderUser.setUserName(uploader.getName());
                    uploaderUser.setDate(bulkUploadRequest.getUploadedDate());
                    log.debug("👤 Uploader info: {}", uploaderUser);
                }
            } catch (SQLException e) {
                log.error("❌ Failed to fetch uploader for ID: {}", bulkUploadRequest.getUploaderId(), e);
                throw new RuntimeException(e);
            }

            try {
                EPerson reviewer = epersonService.find(context, bulkUploadRequest.getReviewerId());
                if (reviewer != null) {
                    reviewerUser.setUuid(reviewer.getID());
                    reviewerUser.setUserName(reviewer.getName());
                    reviewerUser.setDate(bulkUploadRequest.getReviewedDate());
                    log.debug("👤 Reviewer info: {}", reviewerUser);
                }
            } catch (SQLException e) {
                log.error("❌ Failed to fetch reviewer for ID: {}", bulkUploadRequest.getReviewerId(), e);
                throw new RuntimeException(e);
            }

            bulkFileDto.setBulkFileId(bulkUploadRequest.getBulkUploadId());
            bulkFileDto.setCollection(bulkFileCollectionDto);
            bulkFileDto.setFileName(bulkUploadRequest.getFilename());
            bulkFileDto.setStatus(bulkUploadRequest.getStatus());
            bulkFileDto.setUploader(uploaderUser);
            bulkFileDto.setReviewer(reviewerUser);

            bulkFileDtos.add(bulkFileDto);
        }

        log.info("✅ Prepared {} BulkFileDto objects for response", bulkFileDtos.size());
        return bulkFileDtos;
    }



    @Override
    public BulkUploadRequestResponseDTO getFile(UUID uuid) {
        logger.info("Starting to process bulk upload file request for UUID: {}", uuid);

        logger.debug("Attempting to find bulk upload request in repository");
        BulkUploadRequest request = bulkUploadRequestRepository.findById(uuid)
                .orElseThrow(() -> {
                    logger.error("Bulk upload request not found for UUID: {}", uuid);
                    return new RuntimeException("Request not found: " + uuid);
                });
        logger.info("Found bulk upload request - ID: {}, Filename: {}, Status: {}",
                request.getBulkUploadId(), request.getFilename(), request.getStatus());

        logger.debug("Querying for associated items");
        List<BulkUploadItem> items = bulkUploadItemRepository.findWithMetadataByUploadRequest(uuid);
        logger.info("Found {} items associated with request UUID: {}", items.size(), uuid);

        List<BulkUploadItemDTO> itemDTOs = items.stream().map(item -> {
            BulkUploadItemDTO dto = new BulkUploadItemDTO();
            dto.setItemId(item.getUuid());
            dto.setItemFolder(item.getItemFolder());
            Map<String, String> metadataMap = item.getMetadata().stream()
                    .collect(Collectors.toMap(
                            BulkUploadItemMetadata::getKey,
                            BulkUploadItemMetadata::getValue,
                            (oldValue, newValue) -> newValue
                    ));
            dto.setMetadata(metadataMap);

            return dto;
        }).toList();


        BulkUploadRequestResponseDTO response = new BulkUploadRequestResponseDTO();
        response.setRequestId(request.getBulkUploadId());
        response.setFilename(request.getFilename());
        response.setStatus(request.getStatus());
        response.setUploaderId(request.getUploaderId());
        response.setUploadedDate(request.getUploadedDate());
        response.setItems(itemDTOs);

        return response;
    }

}
