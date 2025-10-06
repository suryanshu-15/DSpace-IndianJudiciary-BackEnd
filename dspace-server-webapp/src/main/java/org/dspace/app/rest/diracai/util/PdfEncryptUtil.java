package org.dspace.app.rest.diracai.util;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.encryption.AccessPermission;
import org.apache.pdfbox.pdmodel.encryption.StandardProtectionPolicy;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.logging.Level;
import java.util.logging.Logger;

public class PdfEncryptUtil {

    private static final Logger log = Logger.getLogger(PdfEncryptUtil.class.getName());
    private static final String FIXED_OUTPUT_DIR = "/home/dspace/dspace/July_7th/water_mark/encrypt";

    public static File encryptPdf(File inputPdf, String ownerPassword, String userPassword) throws Exception {
        log.info("Starting PDF encryption for: " + inputPdf.getAbsolutePath());

        File tempEncryptedFile = File.createTempFile("encrypted", ".pdf");

        try (PDDocument document = PDDocument.load(inputPdf)) {
            document.setVersion(1.7f); // Ensure AES-256 supported

            AccessPermission ap = new AccessPermission();
            StandardProtectionPolicy spp = new StandardProtectionPolicy(ownerPassword, userPassword, ap);
            spp.setEncryptionKeyLength(256);
            spp.setPermissions(ap);
            spp.setPreferAES(true);

            document.protect(spp);
            document.save(tempEncryptedFile);
            log.info("Temporary encrypted PDF saved at: " + tempEncryptedFile.getAbsolutePath());
        } catch (Exception e) {
            log.log(Level.SEVERE, "Error during PDF encryption", e);
            throw e;
        }

        // Ensure output directory exists
        File outputDir = new File(FIXED_OUTPUT_DIR);
        if (!outputDir.exists()) {
            boolean created = outputDir.mkdirs();
            if (created) {
                log.info("Created output directory: " + FIXED_OUTPUT_DIR);
            } else {
                log.severe("Failed to create output directory: " + FIXED_OUTPUT_DIR);
                throw new IOException("Output directory creation failed.");
            }
        }

        // Copy to fixed output location
        String outputFileName = "encrypted_" + System.currentTimeMillis() + ".pdf";
        File finalFile = new File(outputDir, outputFileName);
        try {
            Files.copy(tempEncryptedFile.toPath(), finalFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
            log.info("Encrypted PDF copied to: " + finalFile.getAbsolutePath());
            if (finalFile.exists()) {
                log.info("✅ Final file exists: " + finalFile.getAbsolutePath());
            } else {
                log.severe("❌ Final file does NOT exist: " + finalFile.getAbsolutePath());
            }

        } catch (IOException e) {
            log.log(Level.SEVERE, "Failed to copy encrypted PDF to final location", e);
            throw e;
        } finally {
//            if (tempEncryptedFile.exists() && tempEncryptedFile.delete()) {
//                log.info("Deleted temporary encrypted file: " + tempEncryptedFile.getAbsolutePath());
//            }
            log.info("Preserved temporary encrypted file: " + tempEncryptedFile.getAbsolutePath());

        }

        return finalFile;
    }
}
