package org.dspace.app.rest.diracai.controller;

import org.dspace.app.rest.diracai.Repository.FileHashRecordRepository;
import org.dspace.app.rest.diracai.service.FileHashService;
import org.dspace.content.Diracai.FileHashRecord;
import org.dspace.core.Context;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/cnr")
public class FileHashController {

    @Autowired
    private FileHashService fileHashService;

    @Autowired
    private FileHashRecordRepository recordRepository;

    // POST: Generate ZIP + SHA256 + Store
    @PostMapping("/generate")
    public FileHashRecord generateAndStoreHash(@RequestParam("cnr") String cnr,@RequestParam("docType") String docType, Context context) throws IOException {
        return fileHashService.generateZipAndHash(cnr, context,docType);
    }


    @GetMapping("/records")
    public ResponseEntity<?> getAllHashes() {
        try {
            List<FileHashRecord> records = recordRepository.findAll(Sort.by(Sort.Order.desc("createdAt")));
            return new ResponseEntity<>(records, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>("Error fetching records: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


}
