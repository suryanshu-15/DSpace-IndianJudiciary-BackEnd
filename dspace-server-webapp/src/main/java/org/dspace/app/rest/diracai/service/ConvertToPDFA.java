//package org.dspace.app.rest.diracai.service;
//
//import org.apache.commons.io.IOUtils;
//import org.dspace.content.Bitstream;
//import org.dspace.content.BitstreamBuilder;
//import org.dspace.content.BitstreamFormat;
//import org.dspace.content.Bundle;
//import org.dspace.core.Context;
//
//import java.io.*;
//import java.util.List;
//
//public class ConvertToPDFA {
//
//    public static void convert(Context context, Bitstream bitstream) throws Exception {
//        if (!"application/pdf".equals(bitstream.getFormat(context).getMIMEType())) {
//            System.out.println("Bitstream is not a PDF. Skipping.");
//            return;
//        }
//
//        File inputFile = File.createTempFile("original", ".pdf");
//
//        // Step 1: Save the original PDF to a temp file
//        try (InputStream in = bitstream.retrieve();
//             OutputStream out = new FileOutputStream(inputFile)) {
//            IOUtils.copy(in, out);
//        }
//
//        // Step 2: Convert it to PDF/A using Ghostscript
//        File pdfaFile = PdfaConverter.convertToPdfA(inputFile);
//
//        // Step 3: Replace original bitstream in its bundle
//        try (InputStream pdfaStream = new FileInputStream(pdfaFile)) {
//            Bitstream newBitstream = BitstreamBuilder.create(context, pdfaStream)
//                    .withName("pdfa_" + bitstream.getName())
//                    .withFormat(BitstreamFormat.findByMIMEType(context, "application/pdf"))
//                    .build();
//
//            List<Bundle> bundles = bitstream.getBundles();
//            if (!bundles.isEmpty()) {
//                Bundle bundle = bundles.get(0);
//                bundle.removeBitstream(bitstream);
//                bundle.addBitstream(newBitstream);
//            }
//
//            context.dispatchEvents(); // Persist changes
//        }
//
//        // Step 4: Clean up
//        inputFile.delete();
//        pdfaFile.delete();
//    }
//}
package org.dspace.app.rest.diracai.service;

import org.apache.commons.io.IOUtils;
import org.dspace.content.*;
import org.dspace.content.factory.ContentServiceFactory;
import org.dspace.content.service.BitstreamFormatService;
import org.dspace.content.service.BitstreamService;
import org.dspace.content.service.BundleService;
import org.dspace.core.Context;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.List;

public class ConvertToPDFA {

    private static final Logger log = LoggerFactory.getLogger(ConvertToPDFA.class);

    private static final BitstreamService bitstreamService =
            ContentServiceFactory.getInstance().getBitstreamService();

    private static final BitstreamFormatService bitstreamFormatService =
            ContentServiceFactory.getInstance().getBitstreamFormatService();

    private static final BundleService bundleService =
            ContentServiceFactory.getInstance().getBundleService();

    public static void convert(Context context, Bitstream bitstream) throws Exception {
        if (bitstream == null) {
            log.warn("Provided bitstream is null.");
            return;
        }

        BitstreamFormat format = bitstream.getFormat(context);
        if (format == null || !"application/pdf".equals(format.getMIMEType())) {
            log.info("Bitstream is not a PDF. Skipping.");
            return;
        }

        File inputFile = File.createTempFile("original", ".pdf");
        File pdfaFile = null;

        // Step 1: Write original bitstream to local temp file
        try (InputStream in = bitstreamService.retrieve(context, bitstream);
             OutputStream out = new FileOutputStream(inputFile)) {
            IOUtils.copy(in, out);
            out.flush();
        }

        // Step 2: Convert to PDF/A
        pdfaFile = PdfaConverter.convertToPdfA(inputFile);

        // Step 3: Replace original bitstream in bundle
        try (InputStream pdfaStream = new FileInputStream(pdfaFile)) {
            BitstreamFormat pdfFormat = bitstreamFormatService.findByMIMEType(context, "application/pdf");

            List<Bundle> bundles = bitstream.getBundles();
            if (!bundles.isEmpty()) {
                Bundle bundle = bundles.get(0);

                Bitstream newBitstream = bitstreamService.create(context, bundle, pdfaStream);

                // Set only the format — retain default name
                bitstreamService.setFormat(context, newBitstream, pdfFormat);
                bitstreamService.update(context, newBitstream);

                // Remove and delete the original bitstream
                bundleService.removeBitstream(context, bundle, bitstream);
                bitstreamService.delete(context, bitstream);
            }

            context.dispatchEvents();
        } catch (Exception e) {
            log.error("Error during PDF/A conversion", e);
            throw e;
        } finally {
            if (inputFile.exists()) inputFile.delete();
            if (pdfaFile != null && pdfaFile.exists()) pdfaFile.delete();
        }
    }
}
