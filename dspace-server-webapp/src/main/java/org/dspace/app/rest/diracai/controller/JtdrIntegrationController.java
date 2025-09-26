//package org.dspace.app.rest.diracai.controller;
//
//import jakarta.servlet.http.HttpServletResponse;
//import org.dspace.app.rest.diracai.dto.JtdrDetailedReportRow;
//import org.dspace.app.rest.diracai.service.JtdrIntegrationService;
//import org.dspace.core.Context;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.format.annotation.DateTimeFormat;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.*;
//import org.dspace.app.rest.utils.ContextUtil;
//
//import java.io.IOException;
//import java.io.PrintWriter;
//import java.time.LocalDate;
//import java.time.LocalDateTime;
//import java.util.List;
//import java.util.Map;
//
//@RestController
//@RequestMapping("/api/jtdr")
//public class JtdrIntegrationController {
//
//    @Autowired
//    private JtdrIntegrationService jtdrService;
//
//    /**
//     * Submit a case to JTDR using CNR and ZIP hash. No ZIP upload needed; ZIP will be read from local disk.
//     */
//    @PostMapping("/submit")
//    public Map<String, Object> submitCase(
//            @RequestParam("cnr") String cnr) {
//        Context context = ContextUtil.obtainCurrentRequestContext();
//        ;
//        return jtdrService.submitCase(context,cnr);
//    }
//
//    /**
//     * Check status of submitted case using JTDR Acknowledgement ID.
//     */
//
//    @GetMapping("/status/{ackId}")
//    public Map<String, Object> getStatus(@PathVariable String ackId) {
//        return jtdrService.checkStatus(ackId);
//    }
//
//    @GetMapping("/report")
//    public void report(
//            @RequestParam("start") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate start,
//            @RequestParam("end")   @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate end,
//            HttpServletResponse response
//    ) throws IOException {
//        LocalDateTime from = start.atStartOfDay();
//        LocalDateTime to   = end.atTime(23, 59, 59, 999_000_000);
//
//        List<JtdrDetailedReportRow> rows = jtdrService.getDetailedReport(from, to);
//
//        // Set response headers
//        response.setContentType("text/csv");
//        response.setHeader("Content-Disposition", "attachment; filename=detailed-report.csv");
//
//        // Write CSV directly to output stream
//        try (PrintWriter writer = response.getWriter()) {
//            // Header
//            writer.println("Sl No,Batch Name,Case Type,Case No,Upload Date,Upload Status,Zip Created At,Zip Created By,File Submitted Vy,Zip Status");
//
//            // Rows
//            for (JtdrDetailedReportRow row : rows) {
//                writer.print(row.slNumber); writer.print(",");
//                writer.print(safe(row.batchName)); writer.print(",");
//                writer.print(safe(row.caseType)); writer.print(",");
//                writer.print(safe(row.caseNo)); writer.print(",");
//                writer.print(row.uploadDate != null ? row.uploadDate : ""); writer.print(",");
//                writer.print(safe(row.uploadStatus)); writer.print(",");
//                writer.print(row.zipCreatedAt != null ? row.zipCreatedAt : ""); writer.print(",");
//                writer.print(row.createdBy); writer.print(",");
//                writer.print(row.uploadedBy); writer.print(",");
//                writer.print(safe(row.zipStatus));
//                writer.println();
//            }
//        }
//    }
//
//    private String safe(Object value) {
//        if (value == null) return "";
//        String str = value.toString();
//        // Escape commas/quotes by wrapping in quotes
//        if (str.contains(",") || str.contains("\"")) {
//            str = "\"" + str.replace("\"", "\"\"") + "\"";
//        }
//        return str;
//    }
//
//}


package org.dspace.app.rest.diracai.controller;

import jakarta.servlet.http.HttpServletResponse;
import org.dspace.app.rest.diracai.dto.JtdrDetailedReportRow;
import org.dspace.app.rest.diracai.service.JtdrIntegrationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

// PDFBox
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDType1Font;

@RestController
@RequestMapping("/api/jtdr")
public class JtdrIntegrationController {

    @Autowired
    private JtdrIntegrationService jtdrService;

    @PostMapping("/submit")
    public Map<String, Object> submitCase(@RequestParam("cnr") String cnr) {
        var context = org.dspace.app.rest.utils.ContextUtil.obtainCurrentRequestContext();
        return jtdrService.submitCase(context, cnr);
    }

    @GetMapping("/status/{ackId}")
    public Map<String, Object> getStatus(@PathVariable String ackId) {
        return jtdrService.checkStatus(ackId);
    }

    /** CSV version (Accept: text/csv) */
    @GetMapping(value = "/report", produces = "text/csv")
    public void reportCsv(
            @RequestParam("start") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate start,
            @RequestParam("end")   @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate end,
            HttpServletResponse response
    ) throws IOException {
        LocalDateTime from = start.atStartOfDay();
        LocalDateTime to   = end.atTime(23, 59, 59, 999_000_000);
        List<JtdrDetailedReportRow> rows = jtdrService.getDetailedReport(from, to);

        response.setContentType("text/csv");
        response.setHeader("Content-Disposition", "attachment; filename=detailed-report.csv");

        try (PrintWriter writer = response.getWriter()) {
            writer.println("Sl No,Batch Name,Case Type,Case No,Case Year,Zip Created At,Zip Created By,Upload Date,Upload Status,File Submitted By,Zip Status");
            for (JtdrDetailedReportRow row : rows) {
                writer.print(row.slNumber); writer.print(",");
                writer.print(csvSafe(row.batchName)); writer.print(",");
                writer.print(csvSafe(row.caseType)); writer.print(",");
                writer.print(csvSafe(row.caseNo)); writer.print(",");
                writer.print(csvSafe(row.caseYear)); writer.print(",");
                writer.print(row.zipCreatedAt != null ? row.zipCreatedAt : ""); writer.print(",");
                writer.print(csvSafe(row.createdBy)); writer.print(",");
                writer.print(row.uploadDate != null ? row.uploadDate : ""); writer.print(",");
                writer.print(csvSafe(row.uploadStatus)); writer.print(",");
                writer.print(csvSafe(row.uploadedBy)); writer.print(",");
                writer.print(csvSafe(row.zipStatus));
                writer.println();
            }
        }
    }

    /** PDF version (Accept: application/pdf) */
    @GetMapping(value = "/report", produces = "application/pdf")
    public void reportPdf(
            @RequestParam("start") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate start,
            @RequestParam("end")   @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate end,
            HttpServletResponse response
    ) throws IOException {
        LocalDateTime from = start.atStartOfDay();
        LocalDateTime to   = end.atTime(23, 59, 59, 999_000_000);
        List<JtdrDetailedReportRow> rows = jtdrService.getDetailedReport(from, to);

        response.setContentType("application/pdf");
        response.setHeader("Content-Disposition", "attachment; filename=detailed-report.pdf");

        try (PDDocument doc = new PDDocument()) {
            PDPage page = new PDPage(PDRectangle.A4);
            doc.addPage(page);

            float margin = 36; // 0.5 inch
            float y = page.getMediaBox().getHeight() - margin;
            float lineHeight = 14f;
            float left = margin;

            PDPageContentStream cs = new PDPageContentStream(doc, page);
            cs.setFont(PDType1Font.HELVETICA_BOLD, 12);
            cs.beginText();
            cs.newLineAtOffset(left, y);
            cs.showText("JTDR Detailed Report");
            cs.endText();

            y -= (lineHeight * 2);

            // Header row
            String[] headers = new String[] {
                    "Sl No","Batch Name","Case Type","Case No","Upload Date",
                    "Upload Status","Zip Created At","Zip Created By","File Submitted By","Zip Status"
            };

            cs.setFont(PDType1Font.HELVETICA_BOLD, 9);
            y = drawWrappedLine(cs, page, left, y, lineHeight, join(headers, " | "));

            cs.setFont(PDType1Font.HELVETICA, 9);
            for (JtdrDetailedReportRow r : rows) {
                String line = String.format(
                        "%s | %s | %s | %s | %s | %s | %s | %s | %s | %s",
                        safe(r.slNumber),
                        safe(r.batchName),
                        safe(r.caseType),
                        safe(r.caseNo),
                        safe(r.uploadDate),
                        safe(r.uploadStatus),
                        safe(r.zipCreatedAt),
                        safe(r.createdBy),
                        safe(r.uploadedBy),
                        safe(r.zipStatus)
                );
                y = drawWrappedLine(cs, page, left, y, lineHeight, line);
                if (y < margin + lineHeight) { // new page
                    cs.close();
                    page = new PDPage(PDRectangle.A4);
                    doc.addPage(page);
                    cs = new PDPageContentStream(doc, page);
                    cs.setFont(PDType1Font.HELVETICA, 9);
                    y = page.getMediaBox().getHeight() - margin;
                }
            }
            cs.close();

            doc.save(response.getOutputStream());
        }
    }

    private static String csvSafe(Object v) {
        if (v == null) return "";
        String s = v.toString();
        if (s.contains(",") || s.contains("\"")) {
            return "\"" + s.replace("\"", "\"\"") + "\"";
        }
        return s;
    }

    private static String safe(Object v) {
        return v == null ? "" : v.toString();
    }

    private static String join(String[] arr, String sep) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < arr.length; i++) {
            if (i > 0) sb.append(sep);
            sb.append(arr[i]);
        }
        return sb.toString();
    }

    /** Simple line writer with wrapping (approximate) */
    private static float drawWrappedLine(PDPageContentStream cs, PDPage page, float x, float y, float lh, String text) throws IOException {
        float maxWidth = page.getMediaBox().getWidth() - (x * 2);
        String[] words = text.split("\\s+");
        StringBuilder line = new StringBuilder();
        for (String w : words) {
            String trial = line.length() == 0 ? w : line + " " + w;
            float width = PDType1Font.HELVETICA.getStringWidth(trial) / 1000 * 9; // 9pt font
            if (width > maxWidth) {
                cs.beginText(); cs.newLineAtOffset(x, y); cs.showText(line.toString()); cs.endText();
                y -= lh;
                line = new StringBuilder(w);
            } else {
                line = new StringBuilder(trial);
            }
        }
        if (line.length() > 0) {
            cs.beginText(); cs.newLineAtOffset(x, y); cs.showText(line.toString()); cs.endText();
            y -= lh;
        }
        return y;
    }
}
