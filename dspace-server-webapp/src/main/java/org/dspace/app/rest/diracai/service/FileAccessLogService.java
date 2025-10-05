package org.dspace.app.rest.diracai.service;

import jakarta.servlet.http.HttpServletRequest;
import org.dspace.app.rest.utils.ContextUtil;
import org.dspace.content.Bitstream;
import org.dspace.content.Diracai.FileAccessLog;
import org.dspace.app.rest.diracai.Repository.FileAccessLogRepository;
import org.dspace.content.service.BitstreamService;
import org.dspace.core.Context;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.UUID;

@Service
public class FileAccessLogService {

    @Autowired
    private FileAccessLogRepository repository;

    //    public void log(UUID userId, UUID fileId, String action, String ip, String userAgent) {
//        FileAccessLog log = new FileAccessLog();
//        log.setUserId(userId);
//        log.setFileId(fileId);
//        log.setAction(action);
//        log.setIpAddress(ip);
//        log.setUserAgent(userAgent);
//        log.setFileName();
//        log.setTimestamp(new Timestamp(System.currentTimeMillis()));
//        repository.save(log);
//    }
    @Autowired
    private BitstreamService bitstreamService;

    public void log(UUID userId, UUID fileId, String action, String ip, String userAgent, HttpServletRequest request) {
        FileAccessLog log = new FileAccessLog();
        log.setUserId(userId);
        log.setFileId(fileId);
        log.setAction(action);
        log.setIpAddress(ip);
        log.setUserAgent(userAgent);
        try {
            Context context = ContextUtil.obtainContext(request);
            Bitstream bitstream = bitstreamService.find(context, fileId);
            if (bitstream != null) {
                log.setFileName(bitstream.getName());
            } else {
                log.setFileName("Unknown File");
            }
        } catch (Exception e) {
            log.setFileName("Error Fetching Name");
            e.printStackTrace();
        }

        log.setTimestamp(new Timestamp(System.currentTimeMillis()));
        repository.save(log);
    }

}
