package org.dspace.app.rest.diracai.controller;

import jakarta.servlet.http.HttpServletRequest;
import org.dspace.app.rest.diracai.service.BitstreamPolicyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/custom/bitstreams")
public class BitstreamPolicyController {

    @Autowired
    private BitstreamPolicyService bitstreamPolicyService;

    @PostMapping("/apply-policy")
    public ResponseEntity<?> applyPolicyToBitstreams(@RequestBody Map<String, Object> payload,
                                                     HttpServletRequest request) {
        List<String> bitstreamIds = (List<String>) payload.get("bitstreamIds");
        Map<String, Object> policy = (Map<String, Object>) payload.get("policy");

        bitstreamPolicyService.applyPolicyToBitstreams(bitstreamIds, policy, request);

        return ResponseEntity.ok("Policy applied to all bitstreams.");
    }
}
