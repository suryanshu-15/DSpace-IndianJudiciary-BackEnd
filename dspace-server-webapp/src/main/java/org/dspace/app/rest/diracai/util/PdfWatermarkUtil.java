package org.dspace.app.rest.diracai.util;
import org.apache.pdfbox.pdmodel.*;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;
import org.apache.pdfbox.pdmodel.graphics.state.PDExtendedGraphicsState;
import org.apache.pdfbox.cos.COSName;
import org.apache.pdfbox.pdmodel.PDPageContentStream;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.dspace.services.ConfigurationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.util.logging.Logger; // Using java.util.logging for simplicity here
@Component
public class PdfWatermarkUtil {

    private static final Logger log = Logger.getLogger(PdfWatermarkUtil.class.getName());

    @Autowired
    private ConfigurationService configurationService;

    public File addImageWatermark(File inputPdf) throws IOException {
        String dspaceDir = configurationService.getProperty("dspace.dir");
        File outputFolder = new File(dspaceDir, "generated");
        String outputPath = outputFolder.getPath() + "Recompiled.pdf";
        File imageFolder = new File(dspaceDir, "watermark");
        File[] files = imageFolder.listFiles((dir, name) -> name.toLowerCase().startsWith("image."));
        File imageFile = files[0];
        return addTransparentImageWatermark(inputPdf,outputPath,imageFile.toPath().toString());

    }


    public static File addTransparentImageWatermark(File inputPath, String outputPath, String imagePath) throws IOException {
        File imageFile = new File(imagePath);
        File outputFile = new File(outputPath);

        File inputFile = inputPath;
        try (PDDocument document = PDDocument.load(inputFile)) {
            PDImageXObject pdImage = PDImageXObject.createFromFileByContent(imageFile, document);

            for (PDPage page : document.getPages()) {
                PDRectangle mediaBox = page.getMediaBox();

                float scale = 0.4f;
                float width = pdImage.getWidth() * scale;
                float height = pdImage.getHeight() * scale;
                float centerX = (mediaBox.getWidth() - width) / 2;
                float centerY = (mediaBox.getHeight() - height) / 2;

                PDExtendedGraphicsState graphicsState = new PDExtendedGraphicsState();
                graphicsState.setNonStrokingAlphaConstant(0.3f); // 30% visible
                graphicsState.setAlphaSourceFlag(true);

                PDResources resources = page.getResources();
                if (resources == null) {
                    resources = new PDResources();
                    page.setResources(resources);
                }

                COSName gsName = COSName.getPDFName("TransparentGS");
                resources.put(gsName, graphicsState);

                try (PDPageContentStream contentStream = new PDPageContentStream(document, page,
                        PDPageContentStream.AppendMode.APPEND, true, true)) {
                    contentStream.setGraphicsStateParameters(graphicsState);
                    contentStream.drawImage(pdImage, centerX, centerY, width, height);
                }
            }

            PDDocumentInformation info = document.getDocumentInformation();
            info.setTitle("PDF with Transparent Watermark");
            info.setAuthor("Reuters Bot");
            info.setSubject("Watermarked with transparency");
            info.setKeywords("pdf, watermark, transparent, half-size");

            document.save(outputFile);
        }

        return outputFile;
    }

}