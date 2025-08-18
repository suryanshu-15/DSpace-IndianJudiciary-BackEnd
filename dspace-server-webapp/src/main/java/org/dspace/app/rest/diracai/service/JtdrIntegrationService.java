package org.dspace.app.rest.diracai.service;


import org.springframework.web.multipart.MultipartFile;
import java.util.Map;

public interface JtdrIntegrationService {
    Map<String, Object> submitCase(String cnr);
    Map<String, Object> checkStatus(String ackId);
}