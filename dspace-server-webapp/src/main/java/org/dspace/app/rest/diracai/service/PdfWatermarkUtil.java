package org.dspace.app.rest.diracai.service;
import org.apache.pdfbox.pdmodel.*;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;
import org.apache.pdfbox.pdmodel.graphics.state.PDExtendedGraphicsState;
import org.apache.pdfbox.cos.COSName;
import org.apache.pdfbox.pdmodel.PDPageContentStream;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;

import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger; // Using java.util.logging for simplicity here

public class PdfWatermarkUtil {

    private static final Logger log = Logger.getLogger(PdfWatermarkUtil.class.getName());

    public static File addImageWatermark(File inputPdf) throws IOException {
        String outputPath = "/home/dspace/dspace/July_7th/water_mark/pdfs/generated.pdf";
        String imagePath = "/home/dspace/dspace/July_7th/water_mark/image/highcourt.png";

        return addTransparentImageWatermark(inputPdf,outputPath,imagePath);
    }


    public static File addTransparentImageWatermark(File inputPath, String outputPath, String imagePath) throws IOException {
        File imageFile = new File(imagePath);
        File outputFile = new File(outputPath);

        File inputFile = inputPath;
        try (PDDocument document = PDDocument.load(inputFile)) {
            PDImageXObject pdImage = PDImageXObject.createFromFileByContent(imageFile, document);

            for (PDPage page : document.getPages()) {
                PDRectangle mediaBox = page.getMediaBox();

                float scale = 0.2f;
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