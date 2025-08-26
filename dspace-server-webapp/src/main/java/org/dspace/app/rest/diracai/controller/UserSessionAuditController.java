package org.dspace.app.rest.diracai.controller;

import org.dspace.app.rest.diracai.Repository.UserSessionAuditRepository;
import org.dspace.content.Diracai.UserSessionAudit;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.sql.Timestamp;
import java.util.*;

@RestController
@RequestMapping("/api/diracai/sessions")
public class UserSessionAuditController {

    @Autowired
    private UserSessionAuditRepository sessionAuditRepository;

    @GetMapping
    public ResponseEntity<List<UserSessionAudit>> getAllSessions() {
        List<UserSessionAudit> audits = sessionAuditRepository.findAll();
        return ResponseEntity.ok(audits);
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserSessionAudit> getSessionById(@PathVariable UUID id) {
        return sessionAuditRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<UserSessionAudit>> getSessionsByUserId(@PathVariable UUID userId) {
        List<UserSessionAudit> audits = sessionAuditRepository.findByUserId(userId);
        return ResponseEntity.ok(audits);
    }

    @PostMapping
    public ResponseEntity<UserSessionAudit> createSessionAudit(@RequestBody UserSessionAudit audit) {
        audit.setId(UUID.randomUUID());
        audit.setTimestamp(new Timestamp(System.currentTimeMillis()));
        UserSessionAudit saved = sessionAuditRepository.save(audit);
        return ResponseEntity.ok(saved);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteAudit(@PathVariable UUID id) {
        if (sessionAuditRepository.existsById(id)) {
            sessionAuditRepository.deleteById(id);
            return ResponseEntity.ok().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}
