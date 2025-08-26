package org.dspace.app.rest.diracai.util;

import jakarta.servlet.http.HttpServletRequest;
import org.dspace.app.rest.diracai.service.FileAccessLogService;
import org.dspace.core.Context;
import org.dspace.services.RequestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class FileAccessLogger {

    @Autowired
    FileAccessLogService fileAccessLogService;

    public void logAccess(Context context, UUID fileId, String action,
                          HttpServletRequest request) {

        String ip = request.getRemoteAddr();
        String userAgent = request.getHeader("User-Agent");

        UUID userId = context.getCurrentUser() != null ? context.getCurrentUser().getID() : null;
        fileAccessLogService.log(userId, fileId, action, ip, userAgent , request);
    }
}
