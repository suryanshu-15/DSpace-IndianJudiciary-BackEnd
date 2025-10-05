package org.dspace.app.rest.diracai.handler;

import jakarta.servlet.http.HttpServletRequest;
import org.dspace.app.rest.diracai.service.BitstreamPermissionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.UUID;

@Component
public class BitstreamPermissionHandler {

    @Autowired
    private BitstreamPermissionService service;

    public Map<String, Object> handle(UUID uuid, HttpServletRequest request) {
        return service.retrievePermissions(uuid, request);
    }
}
