package org.dspace.app.rest.diracai.controller;

import org.dspace.app.rest.diracai.Repository.RoleAuditLogRepository;
import org.dspace.content.Diracai.RoleAuditLog;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/role-audit")
public class RoleAuditLogController {

    @Autowired
    private RoleAuditLogRepository repository;

    @GetMapping
    public List<RoleAuditLog> allLogs() {
        return repository.findAll();
    }
}
