package org.dspace.app.rest.diracai.controller;

import org.dspace.app.rest.diracai.service.JtdrIntegrationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/jtdr")
public class JtdrIntegrationController {

    @Autowired
    private JtdrIntegrationService jtdrService;

    /**
     * Submit a case to JTDR using CNR and ZIP hash. No ZIP upload needed; ZIP will be read from local disk.
     */
    @PostMapping("/submit")
    public Map<String, Object> submitCase(
            @RequestParam("cnr") String cnr) {
        return jtdrService.submitCase(cnr);
    }

    /**
     * Check status of submitted case using JTDR Acknowledgement ID.
     */

    @GetMapping("/status/{ackId}")
    public Map<String, Object> getStatus(@PathVariable String ackId) {
        return jtdrService.checkStatus(ackId);
    }

    // @PostMapping
}
