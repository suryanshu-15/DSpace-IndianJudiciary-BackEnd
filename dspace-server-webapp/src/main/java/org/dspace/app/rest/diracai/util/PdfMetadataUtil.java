package org.dspace.app.rest.diracai.util;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDDocumentInformation;

import java.io.File;
import java.io.IOException;

public class PdfMetadataUtil {

    /**
     * Adds metadata to a PDF file and saves it to a new temporary file.
     *
     * @param pdfFile  Original PDF file
     * @param title    Title to embed (e.g., dc.title)
     * @param author   Author to embed (e.g., dc.contributor.author)
     * @param subject  Subject to embed (e.g., petitioner info)
     * @param keywords Keywords to embed (e.g., advocate info)
     * @return New File object pointing to the output PDF with metadata
     * @throws IOException if any IO error occurs during read/write
     */
    public static File addMetadata(File pdfFile,
                                   String title,
                                   String author,
                                   String subject,
                                   String keywords) throws IOException {

        // Output file path
        File outputFile = File.createTempFile("with-metadata-", ".pdf");

        try (PDDocument document = PDDocument.load(pdfFile)) {
            document.setAllSecurityToBeRemoved(true);
            // Create and populate metadata
            PDDocumentInformation info = new PDDocumentInformation();
            info.setTitle(title != null ? title : "Untitled Document");
            info.setAuthor(author != null ? author : "Unknown Author");
            info.setSubject(subject != null ? subject : "No Subject");
            info.setKeywords(keywords != null ? keywords : "No Keywords");
            info.setCreator("DSpace PDF Enhancer");
            info.setProducer("Apache PDFBox");

            document.setDocumentInformation(info);

            // Save to new file
            document.save(outputFile);
        }

        return outputFile;
    }
}
