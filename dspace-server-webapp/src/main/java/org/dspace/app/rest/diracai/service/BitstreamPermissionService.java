package org.dspace.app.rest.diracai.service;

import jakarta.servlet.http.HttpServletRequest;
import java.util.Map;
import java.util.UUID;

public interface BitstreamPermissionService {
    Map<String, Object> retrievePermissions(UUID uuid, HttpServletRequest request);
}
