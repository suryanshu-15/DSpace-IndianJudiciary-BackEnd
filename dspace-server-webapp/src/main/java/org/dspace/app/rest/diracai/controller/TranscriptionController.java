package org.dspace.app.rest.diracai.controller;


import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.dspace.app.rest.diracai.service.TranscriptionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.UUID;

@RestController
@RequestMapping("/api/transcription")
public class TranscriptionController {

    @Autowired
    private TranscriptionService transcriptionService;

    @PostMapping("/upload/{itemUUID}")
    public ResponseEntity<String> transcribeAudio(@PathVariable("itemUUID") UUID itemUUID,
                                                  HttpServletRequest request) {
        try {
            String transcript = transcriptionService.transcribeAndSaveToMetadata(itemUUID, request);
            return ResponseEntity.ok(transcript);
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Transcription failed: " + e.getMessage());
        }
    }


    @GetMapping
    public ResponseEntity<String> checking() {
        return ResponseEntity.ok("✅ Transcription endpoint is working");
    }
}
