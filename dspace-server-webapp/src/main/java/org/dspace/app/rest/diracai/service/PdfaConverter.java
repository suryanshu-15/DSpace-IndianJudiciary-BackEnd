package org.dspace.app.rest.diracai.service;

import java.io.*;

public class PdfaConverter {

    public static File convertToPdfA(File originalPdf) throws IOException, InterruptedException {
        String pdfaOutputPath = originalPdf.getAbsolutePath().replace(".pdf", "_pdfa.pdf");
        File pdfaFile = new File(pdfaOutputPath);

        ProcessBuilder pb = new ProcessBuilder(
                "gs",
                "-dPDFA=2",
                "-dBATCH",
                "-dNOPAUSE",
                "-sDEVICE=pdfwrite",
                "-sColorConversionStrategy=UseDeviceIndependentColor",
                "-dPDFACompatibilityPolicy=1",
                "-sOutputFile=" + pdfaFile.getAbsolutePath(),
                originalPdf.getAbsolutePath()
        );

        pb.redirectErrorStream(true);
        Process process = pb.start();

        // Optional: Debug CLI output
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
            String line;
            while ((line = reader.readLine()) != null) {
                System.out.println("[Ghostscript] " + line);
            }
        }

        int exitCode = process.waitFor();
        if (exitCode != 0 || !pdfaFile.exists()) {
            throw new IOException("Ghostscript conversion failed. Exit code: " + exitCode);
        }

        return pdfaFile;
    }
}

