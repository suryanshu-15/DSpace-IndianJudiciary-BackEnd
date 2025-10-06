package org.dspace.app.rest.diracai.service.impl;

import org.dspace.app.rest.diracai.service.PdfAConversionService;
import org.dspace.app.rest.diracai.service.PdfaConverter;
import org.dspace.content.Bitstream;
import org.dspace.content.service.BitstreamService;
import org.dspace.core.Context;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.*;


@Service
public class PdfAConversionServiceImpl implements PdfAConversionService {


    @Autowired
    private BitstreamService bitstreamService;

    @Override
    public boolean isConvertedToPdfA(Context context, Bitstream bitstream) {
        String name = bitstream.getName();
        if (name == null || !name.toLowerCase().endsWith(".pdf")) {
            return false;
        }
        return name.toLowerCase().endsWith("_pdfa.pdf");
    }


    @Override
    public void convertToPdfA(Context context, Bitstream bitstream) {
        if (bitstream == null || bitstream.getName() == null ||
                !bitstream.getName().toLowerCase().endsWith(".pdf")) {
            return;
        }

        File inputFile = null;
        File pdfaFile = null;

        try {
            inputFile = File.createTempFile("original-", ".pdf");
            try (InputStream input = bitstreamService.retrieve(context, bitstream);
                 OutputStream output = new FileOutputStream(inputFile)) {
                input.transferTo(output);
            }

            pdfaFile = PdfaConverter.convertToPdfA(inputFile);

            try (InputStream pdfaInput = new FileInputStream(pdfaFile)) {
                bitstreamService.updateContents(context, bitstream, pdfaInput);
                bitstream.setSizeBytes(pdfaFile.length());
                bitstreamService.setFormat(context, bitstream, bitstream.getFormat(context));
                bitstreamService.update(context, bitstream);
            }

        } catch (Exception e) {
            throw new RuntimeException("PDF/A conversion failed: " + e.getMessage(), e);
        } finally {
            if (inputFile != null) inputFile.delete();
            if (pdfaFile != null) pdfaFile.delete();
        }
    }

}
