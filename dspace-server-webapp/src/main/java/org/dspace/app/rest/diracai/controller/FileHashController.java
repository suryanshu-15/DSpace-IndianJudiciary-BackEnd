//package org.dspace.app.rest.diracai.controller;
//
//import org.dspace.app.rest.diracai.Repository.FileHashRecordRepository;
//import org.dspace.app.rest.diracai.service.FileHashService;
//import org.dspace.content.Diracai.FileHashRecord;
//import org.dspace.core.Context;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.data.domain.Page;
//import org.springframework.data.domain.PageRequest;
//import org.springframework.data.domain.Pageable;
//import org.springframework.data.domain.Sort;
//import org.springframework.http.HttpStatus;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.*;
//
//import java.io.IOException;
//import java.util.ArrayList;
//import java.util.List;
//
//@RestController
//@RequestMapping("/api/cnr")
//public class FileHashController {
//
//    @Autowired
//    private FileHashService fileHashService;
//
//    @Autowired
//    private FileHashRecordRepository recordRepository;
//
//    // POST: Generate ZIP + SHA256 + Store
//    @PostMapping("/generate")
//    public FileHashRecord generateAndStoreHash(@RequestParam("cnr") String cnr,@RequestParam("docType") String docType, Context context) throws IOException {
//        return fileHashService.generateZipAndHash(cnr, context,docType);
//    }
//
//    @GetMapping("/records")
//    public ResponseEntity<Page<FileHashRecord>> getAllHashes(
//            @RequestParam(defaultValue = "0") int page,
//            @RequestParam(defaultValue = "10") int size,
//            @RequestParam(defaultValue = "createdAt") String sortBy,
//            @RequestParam(defaultValue = "desc") String sortDir,
//            @RequestParam(required = false) String submitted   // NEW PARAM
//    ) {
//        try {
//            Sort sort = sortDir.equalsIgnoreCase("asc")
//                    ? Sort.by(sortBy).ascending()
//                    : Sort.by(sortBy).descending();
//
//            Pageable pageable = PageRequest.of(page, size, sort);
//            Page<FileHashRecord> result;
//
//            if ("submit".equalsIgnoreCase(submitted)) {
//                result = recordRepository.findByAckIdIsNotNullAndAckIdNot(pageable, "");
//            } else if ("notSubmitted".equalsIgnoreCase(submitted)) {
//                result = recordRepository.findByAckIdIsNullOrAckId(pageable, "");
//            } else {
//                result = recordRepository.findAll(pageable);
//            }
//
//            return ResponseEntity.ok(result);
//        } catch (Exception e) {
//            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
//        }
//    }
//
//
//}



package org.dspace.app.rest.diracai.controller;

import org.dspace.app.rest.diracai.Repository.FileHashRecordRepository;
import org.dspace.app.rest.diracai.service.FileHashService;
import org.dspace.content.Diracai.FileHashRecord;
import org.dspace.core.Context;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@RestController
@RequestMapping("/api/cnr")
public class FileHashController {

    @Autowired
    private FileHashService fileHashService;

    @Autowired
    private FileHashRecordRepository recordRepository;

    // POST: Generate ZIP + SHA256 + Store
    @PostMapping("/generate")
    public FileHashRecord generateAndStoreHash(@RequestParam("cnr") String cnr,
                                               @RequestParam("docType") String docType,
                                               Context context) throws IOException {
        return fileHashService.generateZipAndHash(cnr, context, docType);
    }

    @GetMapping("/records")
    public ResponseEntity<Page<FileHashRecord>> getAllHashes(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir,
            @RequestParam(required = false) String submitted
    ) {
        try {
            Sort sort = sortDir.equalsIgnoreCase("asc")
                    ? Sort.by(sortBy).ascending()
                    : Sort.by(sortBy).descending();

            Pageable pageable = PageRequest.of(page, size, sort);
            Page<FileHashRecord> result;

            if ("submit".equalsIgnoreCase(submitted)) {
                result = recordRepository.findByAckIdIsNotNullAndAckIdNot(pageable, "");
            } else if ("notSubmitted".equalsIgnoreCase(submitted)) {
                result = recordRepository.findByAckIdIsNullOrAckId(pageable, "");
            } else {
                result = recordRepository.findAll(pageable);
            }

            return ResponseEntity.ok(result);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /** DELETE: remove the generated zip and its DB record by fileName */
    @DeleteMapping("/records/{fileName}")
    public ResponseEntity<Void> deleteGeneratedRecord(@PathVariable String fileName) {
        try {
            FileHashService.DeleteResult result = fileHashService.deleteZipAndRecord(fileName);
            if (result == FileHashService.DeleteResult.NOT_FOUND) {
                return ResponseEntity.notFound().build();
            }
            return ResponseEntity.noContent().build(); // 204
        } catch (IOException ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /** Optional alias: DELETE /api/cnr/zip/{fileName} */
    @DeleteMapping("/zip/{fileName}")
    public ResponseEntity<Void> deleteGeneratedZip(@PathVariable String fileName) {
        try {
            FileHashService.DeleteResult result = fileHashService.deleteZipAndRecord(fileName);
            if (result == FileHashService.DeleteResult.NOT_FOUND) {
                return ResponseEntity.notFound().build();
            }
            return ResponseEntity.noContent().build();
        } catch (IOException ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
