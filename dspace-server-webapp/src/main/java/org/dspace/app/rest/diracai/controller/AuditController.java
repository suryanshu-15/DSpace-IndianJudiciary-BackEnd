package org.dspace.app.rest.diracai.controller;

import org.dspace.app.rest.diracai.service.AuditService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/audit")
public class AuditController {

    @Autowired
    private AuditService auditService;

    @GetMapping("/users")
    public ResponseEntity<?> getAllUsers(){
        return auditService.getAllUsers();
    }

    @GetMapping("/user")
    public ResponseEntity<?> getByUser(@RequestParam("userId") UUID userId) {
        return auditService.getByUser(userId);
    }



}


