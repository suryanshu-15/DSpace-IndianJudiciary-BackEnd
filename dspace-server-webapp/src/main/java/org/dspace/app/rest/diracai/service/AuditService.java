package org.dspace.app.rest.diracai.service;

import org.springframework.http.ResponseEntity;

import java.util.UUID;

public interface AuditService {

    ResponseEntity<?> getAllUsers();

    ResponseEntity<?> getByUser(UUID userId);
}
