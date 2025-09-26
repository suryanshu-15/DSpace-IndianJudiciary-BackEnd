package org.dspace.app.rest.diracai.service;


import org.dspace.app.rest.diracai.dto.JtdrDetailedReportRow;
import org.dspace.core.Context;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

public interface JtdrIntegrationService {
    Map<String, Object> submitCase(Context context, String cnr);
    Map<String, Object> checkStatus(String ackId);
    List<JtdrDetailedReportRow> getDetailedReport(LocalDateTime from, LocalDateTime to);
}