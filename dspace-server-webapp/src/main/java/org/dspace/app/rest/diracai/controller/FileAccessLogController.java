package org.dspace.app.rest.diracai.controller;

import org.dspace.app.rest.diracai.Repository.FileAccessLogRepository;
import org.dspace.content.Diracai.FileAccessLog;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/file-access")
public class FileAccessLogController {

    @Autowired
    private FileAccessLogRepository repository;

    @GetMapping("/user/{uuid}")
    public List<FileAccessLog> getByUser(@PathVariable UUID uuid) {
        return repository.findByUserId(uuid);
    }

    @GetMapping("/file/{uuid}")
    public List<FileAccessLog> getByFile(@PathVariable UUID uuid) {
        return repository.findByFileId(uuid);
    }
}
