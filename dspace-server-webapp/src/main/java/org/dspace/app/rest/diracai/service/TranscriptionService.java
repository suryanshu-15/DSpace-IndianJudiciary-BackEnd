//package org.dspace.app.rest.diracai.service;
//
//import jakarta.servlet.http.HttpServletRequest;
//import org.dspace.app.rest.diracai.util.PdfMetadataUtil;
//import org.dspace.app.rest.utils.ContextUtil;
//import org.dspace.content.Bitstream;
//import org.dspace.content.Bundle;
//import org.dspace.content.Item;
//import org.dspace.content.service.BitstreamService;
//import org.dspace.content.service.BundleService;
//import org.dspace.content.service.ItemService;
//import org.dspace.core.Context;
//import org.dspace.xoai.services.api.context.ContextService;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Service;
//import java.io.*;
//import java.util.UUID;
//
//
//@Service
//public class TranscriptionService {
//
//    @Autowired
//    private BitstreamService bitstreamService;
//
//    @Autowired
//    private ItemService itemService;
//
//    @Autowired
//    private BundleService bundleService;
//
//    @Autowired
//    private ContextService contextService;
//    private static final String WATERMARKED_PDF_STORAGE_DIR = "/home/diracai/dspace_watermarked_pdfs/";
//
//    public String transcribeAndSaveToMetadata(UUID itemUuid, HttpServletRequest request) throws Exception {
//        Context context = ContextUtil.obtainContext(request);
//        Item item = itemService.find(context, itemUuid);
//        if (item == null) {
//            throw new RuntimeException("Item not found for UUID: " + itemUuid);
//        }
//
//        StringBuilder metadataBuilder = new StringBuilder();
//
//        for (Bundle bundle : item.getBundles()) {
//            for (Bitstream bitstream : bundle.getBitstreams()) {
//                String fileName = bitstream.getName().toLowerCase();
//                if (!fileName.endsWith(".pdf")) {
//                    metadataBuilder.append("❌ Skipped non-PDF bitstream: ").append(fileName).append("\n");
//                    continue;
//                }
//                File inputFile = File.createTempFile("input-", ".pdf");
//                File pdfaFile = null;
//                File watermarkedPdf = null;
//                File metaPdf = null;
//                try (InputStream in = bitstreamService.retrieve(context, bitstream);
//                     OutputStream out = new FileOutputStream(inputFile)) {
//                    in.transferTo(out);
////                    pdfaFile = PdfaConverter.convertToPdfA(inputFile);
//                    watermarkedPdf = PdfWatermarkUtil.addImageWatermark(inputFile);
//                    storedWatermarkedPdf = new File(WATERMARKED_PDF_STORAGE_DIR, watermarkedPdf);
//                    String title = itemService.getMetadataFirstValue(item, "dc", "title", null, Item.ANY);
//                    String author = itemService.getMetadataFirstValue(item, "dc", "contributor", "author", Item.ANY);
//                    String petitioner = itemService.getMetadataFirstValue(item, "dc", "pname", null, Item.ANY);
//                    String advocate = itemService.getMetadataFirstValue(item, "dc", "raname", null, Item.ANY);
//                    metaPdf = PdfMetadataUtil.addMetadata(
//                            watermarkedPdf,
//                            title != null ? title : "Untitled",
//                            author != null ? author : "Unknown",
//                            petitioner != null ? petitioner : "Unknown Petitioner",
//                            advocate != null ? advocate : "Unknown Advocate"
//                    );
//                    replaceBitstreamContent(context, bitstream, metaPdf);
//                    metadataBuilder.append("✅ Converted: ").append(fileName).append("\n");
//                } catch (Exception e) {
//                    metadataBuilder.append("❌ Failed to convert: ").append(fileName)
//                            .append(" → ").append(e.getMessage()).append("\n");
//                } finally {
//                    inputFile.delete();
//                    if (pdfaFile != null) pdfaFile.delete();
//                    if (watermarkedPdf != null) watermarkedPdf.delete();
//                    if (metaPdf != null) metaPdf.delete();
//                }
//            }
//        }
//
//        String metadataValue = metadataBuilder.toString().trim();
//        itemService.addMetadata(context, item, "dc", "description", "transcript", null, metadataValue);
//        itemService.update(context, item);
//        context.commit();
//
//        return metadataValue;
//    }
//
//    private void replaceBitstreamContent(Context context, Bitstream bitstream, File newFile) throws Exception {
//        try (InputStream newStream = new FileInputStream(newFile)) {
//            bitstreamService.updateContents(context, bitstream, newStream);
//            bitstream.setSizeBytes(newFile.length());
//            bitstreamService.setFormat(context, bitstream, bitstream.getFormat(context));
//            bitstreamService.update(context, bitstream);
//        }
//    }
//}


package org.dspace.app.rest.diracai.service;

import jakarta.servlet.http.HttpServletRequest;
import org.dspace.app.rest.diracai.util.PdfEncryptUtil;
import org.dspace.app.rest.diracai.util.PdfMetadataUtil;

import org.dspace.app.rest.diracai.util.PdfWatermarkUtil;
import org.dspace.app.rest.utils.ContextUtil;
import org.dspace.content.Bitstream;
import org.dspace.content.Bundle;
import org.dspace.content.Item;
import org.dspace.content.service.BitstreamService;
import org.dspace.content.service.BundleService;
import org.dspace.content.service.ItemService;
import org.dspace.core.Context;
import org.dspace.xoai.services.api.context.ContextService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.io.*;
import java.nio.file.Files; // Needed for Files.createDirectories and Files.copy
import java.nio.file.Path;   // Needed for Path
import java.nio.file.Paths;  // Needed for Paths.get
import java.nio.file.StandardCopyOption; // Needed for StandardCopyOption.REPLACE_EXISTING
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

@Service
public class TranscriptionService {

    private static final Logger log = Logger.getLogger(TranscriptionService.class.getName());

    private static final String WATERMARKED_PDF_STORAGE_DIR = "/home/diracai/dspace_watermarked_pdfs/";

    @Autowired
    private BitstreamService bitstreamService;

    @Autowired
    private ItemService itemService;

    @Autowired
    private BundleService bundleService;

    @Autowired
    private ContextService contextService;

    @Autowired
    private PdfWatermarkUtil pdfWatermarkUtil;

    public String transcribeAndSaveToMetadata(UUID itemUuid, HttpServletRequest request) throws Exception {
        Context context = ContextUtil.obtainContext(request);
        Item item = itemService.find(context, itemUuid);
        if (item == null) {
            throw new RuntimeException("Item not found for UUID: " + itemUuid);
        }

        Path storageDirPath = Paths.get(WATERMARKED_PDF_STORAGE_DIR);
        if (!Files.exists(storageDirPath)) {
            try {
                Files.createDirectories(storageDirPath);
                log.info("Created watermarked PDF storage directory: " + WATERMARKED_PDF_STORAGE_DIR);
            } catch (IOException e) {
                log.log(Level.SEVERE, "Failed to create watermarked PDF storage directory: " + WATERMARKED_PDF_STORAGE_DIR, e);
                throw new RuntimeException("Could not create storage directory for watermarked PDFs.", e);
            }
        }

        StringBuilder metadataBuilder = new StringBuilder();

        for (Bundle bundle : item.getBundles()) {
            for (Bitstream bitstream : bundle.getBitstreams()) {
                String fileName = bitstream.getName().toLowerCase();
                if (!fileName.endsWith(".pdf")) {
                    metadataBuilder.append("❌ Skipped non-PDF bitstream: ").append(fileName).append("\n");
                    continue;
                }

                File inputFile = null;
                File tempWatermarkedPdf = null;
                File storedWatermarkedPdf = null;
                File finalMetaPdf = null;
                File encryptedPdf = null;

                try {
                    inputFile = File.createTempFile("input-", ".pdf");
                    try (InputStream in = bitstreamService.retrieve(context, bitstream);
                         OutputStream out = new FileOutputStream(inputFile)) {
                        in.transferTo(out);
                    }
                    log.fine("Created temporary input file: " + inputFile.getAbsolutePath());

//                    tempWatermarkedPdf = pdfWatermarkUtil.addImageWatermark(inputFile);


                    String uniqueFileName = UUID.randomUUID().toString() + "_" + fileName;
                    storedWatermarkedPdf = new File(WATERMARKED_PDF_STORAGE_DIR, uniqueFileName);
//                    Files.copy(tempWatermarkedPdf.toPath(), storedWatermarkedPdf.toPath(), StandardCopyOption.REPLACE_EXISTING);
                    log.info("Stored watermarked PDF: " + storedWatermarkedPdf.getAbsolutePath());
                    String title = itemService.getMetadataFirstValue(item, "dc", "title", null, Item.ANY);
                    String author = itemService.getMetadataFirstValue(item, "dc", "contributor", "author", Item.ANY);
                    String petitioner = itemService.getMetadataFirstValue(item, "dc", "pname", null, Item.ANY);
                    String advocate = itemService.getMetadataFirstValue(item, "dc", "raname", null, Item.ANY);

                    finalMetaPdf = PdfMetadataUtil.addMetadata(
                            storedWatermarkedPdf,
                            title != null ? title : "Untitled",
                            author != null ? author : "Unknown",
                            petitioner != null ? petitioner : "Unknown Petitioner",
                            advocate != null ? advocate : "Unknown Advocate"
                    );
                    log.fine("Created final PDF with metadata: " + finalMetaPdf.getAbsolutePath());

                    // replaceBitstreamContent(context, bitstream, finalMetaPdf);
//                    encryptedPdf = PdfEncryptUtil.encryptPdf(finalMetaPdf, "1111", "1111");  // user password left empty
                    replaceBitstreamContent(context, bitstream, finalMetaPdf);

                    metadataBuilder.append("✅ Processed, Watermarked, and Stored: ").append(fileName)
                            .append(" (Stored at: ").append(storedWatermarkedPdf.getName()).append(")").append("\n");

                } catch (Exception e) {
                    log.log(Level.SEVERE, "Failed to process PDF bitstream: " + fileName, e);
                    metadataBuilder.append("❌ Failed to process: ").append(fileName)
                            .append(" → ").append(e.getMessage()).append("\n");
                } finally {
                    if (inputFile != null) {
                        inputFile.delete();
                        log.fine("Deleted temporary input file: " + inputFile.getAbsolutePath());
                    }
                    if (tempWatermarkedPdf != null) {
                        tempWatermarkedPdf.delete();
                        log.fine("Deleted temporary watermarked PDF: " + tempWatermarkedPdf.getAbsolutePath());
                    }
                    if (finalMetaPdf != null) {
                        finalMetaPdf.delete();
                        log.fine("Deleted temporary final metadata PDF: " + finalMetaPdf.getAbsolutePath());
                    }
                    if (encryptedPdf != null) {
                        encryptedPdf.delete();
                        log.fine("Deleted temporary encrypted PDF: " + encryptedPdf.getAbsolutePath());
                    }

                }
            }
        }

        String metadataValue = metadataBuilder.toString().trim();
        itemService.addMetadata(context, item, "dc", "description", "transcript", null, metadataValue);
        itemService.update(context, item);

        context.commit();

        return metadataValue;
    }

    private void replaceBitstreamContent(Context context, Bitstream bitstream, File newFile) throws Exception {
        try (InputStream newStream = new FileInputStream(newFile)) {
            bitstreamService.updateContents(context, bitstream, newStream);
            bitstream.setSizeBytes(newFile.length());
            bitstreamService.setFormat(context, bitstream, bitstream.getFormat(context));
            bitstreamService.update(context, bitstream);
            log.fine("Replaced bitstream content for: " + bitstream.getName());
        }
    }
}