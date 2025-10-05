package org.dspace.app.rest.diracai.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.io.FileUtils;
import org.dspace.app.rest.diracai.Repository.BulkUploadItemRepository;
import org.dspace.app.rest.diracai.Repository.BulkUploadRequestRepository;
import org.dspace.app.rest.diracai.service.BulkUploadItemService;
import org.dspace.app.rest.diracai.service.impl.BulkUploadRequestServiceImpl;
import org.dspace.content.Diracai.BulkUploadItem;
import org.dspace.content.Diracai.BulkUploadItemMetadata;
import org.dspace.content.Diracai.BulkUploadRequest;
import org.dspace.core.Context;
import org.dspace.services.ConfigurationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.sql.SQLException;
import java.util.*;

@Component
public class BulkUploadRequestUtil {

    @Autowired
    private ConfigurationService configurationService;

    @Autowired
    private BulkUploadRequestRepository bulkUploadRequestRepository;

    @Autowired
    private BulkUploadItemService bulkUploadItemService;

    @Autowired
    private BulkUploadItemRepository bulkUploadItemRepository;


    private static final Logger logger = LoggerFactory.getLogger(BulkUploadRequestUtil.class);

    public BulkUploadRequest handleZipUpload(Context context, MultipartFile file, UUID uuid) throws IOException, SQLException {
        logger.info("Starting ZIP upload handling for BulkUploadRequest UUID: {}", uuid);

        // Step 1: Save uploaded ZIP file to temp
        File tempZip = File.createTempFile("bulk-upload-", ".zip");
        logger.debug("Created temporary ZIP file at {}", tempZip.getAbsolutePath());
        file.transferTo(tempZip);
        logger.info("Transferred uploaded file to temporary location.");

        // Step 2: Define upload folder path
        String dspaceDir = configurationService.getProperty("dspace.dir");
        String uploadFolderPath = dspaceDir + File.separator + "bulk_upload" + File.separator + uuid;
        File uploadFolder = new File(uploadFolderPath);

        if (!uploadFolder.exists() && !uploadFolder.mkdirs()) {
            logger.error("Failed to create upload folder: {}", uploadFolderPath);
            throw new IOException("Could not create upload directory.");
        }
        logger.info("Upload folder created at: {}", uploadFolderPath);

        Files.copy(tempZip.toPath(), new File(uploadFolder, uuid.toString() + ".zip").toPath(), StandardCopyOption.REPLACE_EXISTING);


        // Step 3: Unzip the file
        ZipUtils.unzip(tempZip, uploadFolder);
        logger.info("Unzipped content into upload folder.");

        // Step 4: Get BulkUploadRequest
        Optional<BulkUploadRequest> optionalRequest = bulkUploadRequestRepository.findById(uuid);
        if (!optionalRequest.isPresent()) {
            logger.error("BulkUploadRequest not found for UUID: {}", uuid);
            throw new RuntimeException("BulkUploadRequest not found for UUID: " + uuid);
        }
        BulkUploadRequest request = optionalRequest.get();
        logger.info("Fetched BulkUploadRequest with ID: {}", request.getBulkUploadId());

        // Step 5: Process each item directory
        File[] itemDirs = uploadFolder.listFiles(File::isDirectory);
        if (itemDirs != null) {
            logger.info("Found {} item folders to process.", itemDirs.length);
            for (File itemDir : itemDirs) {
                logger.debug("Processing folder: {}", itemDir.getName());

                File dublinCore = new File(itemDir, "dublin_core.xml");
                if (!dublinCore.exists()) {
                    logger.warn("Missing dublin_core.xml in folder: {}", itemDir.getAbsolutePath());
                    continue;
                }

                // Create BulkUploadItem
                BulkUploadItem item = new BulkUploadItem();
                item.setUploadRequest(request.getBulkUploadId());
                item.setItemFolder(itemDir.getName());

                item = bulkUploadItemService.create(context, item);
                logger.info("Created BulkUploadItem for folder: {}, item ID: {}", itemDir.getName(), item.getUuid());

                // Extract and apply metadata
                try {
                    parseDublinCoreXML(dublinCore, item);
                    logger.debug("Parsed and applied Dublin Core metadata for item: {}", item.getUuid());
                } catch (Exception e) {
                    logger.error("Failed to parse Dublin Core for item: {} - {}", item.getUuid(), e.getMessage(), e);
                }
            }
        } else {
            logger.warn("No item folders found in uploaded ZIP.");
        }

        // Step 6: Clean up temporary file
        if (tempZip.exists() && !tempZip.delete()) {
            logger.warn("Failed to delete temporary zip file: {}", tempZip.getAbsolutePath());
        } else {
            logger.debug("Deleted temporary zip file: {}", tempZip.getAbsolutePath());
        }

        logger.info("Completed ZIP upload handling for BulkUploadRequest UUID: {}", uuid);
        return request;
    }



    private void parseDublinCoreXML(File xmlFile, BulkUploadItem item) {
        logger.debug("Metadata extractor called for item: {}", item.getUuid());

        List<BulkUploadItemMetadata> metadataList = new ArrayList<>();

        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document doc = builder.parse(xmlFile);

            NodeList nodes = doc.getElementsByTagName("dcvalue");

            for (int i = 0; i < nodes.getLength(); i++) {
                Element el = (Element) nodes.item(i);
                String element = el.getAttribute("element");
                String qualifier = el.getAttribute("qualifier");

                String key = "dc." + element;
                if (qualifier != null && !qualifier.isEmpty() && !"none".equalsIgnoreCase(qualifier)) {
                    key += "." + qualifier;
                }

                String value = el.getTextContent().trim();

                logger.debug("Extracted metadata key: {}, value: {}", key, value);

                BulkUploadItemMetadata meta = new BulkUploadItemMetadata();
                meta.setKey(key);
                meta.setValue(value);
                meta.setItem(item);
                metadataList.add(meta);
            }

            item.getMetadata().clear();
            item.getMetadata().addAll(metadataList);
            bulkUploadItemRepository.save(item);

        } catch (Exception e) {
            logger.error("Error parsing Dublin Core XML for item {}: {}", item.getUuid(), e.getMessage(), e);
        }
    }

}
