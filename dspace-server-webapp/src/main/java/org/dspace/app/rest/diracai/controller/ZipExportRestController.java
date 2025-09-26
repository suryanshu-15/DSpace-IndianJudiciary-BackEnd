package org.dspace.app.rest.diracai.controller;

import org.dspace.app.rest.diracai.service.ZipExportService;
import org.dspace.app.rest.utils.ContextUtil;
import org.dspace.content.Item;
import org.dspace.content.service.ItemService;
import org.dspace.core.Context;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.File;
import java.util.UUID;

@RestController
@RequestMapping("/api/export")
public class ZipExportRestController {

    @Autowired
    private ZipExportService zipExportService;

    @Autowired
    private ItemService itemService;

    @PostMapping("/zip/{itemUUID}")
    public ResponseEntity<String> generateZip(@PathVariable UUID itemUUID) {
        Context context = null;
        try {
            context = ContextUtil.obtainCurrentRequestContext();
            Item item = itemService.find(context, itemUUID);
            if (item == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Item not found");
            }

            File zip = zipExportService.generateZipForItem(context, item);
            return ResponseEntity.ok("Zip generated: " + zip.getAbsolutePath());

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error: " + e.getMessage());
        } finally {
            if (context != null) {
                context.abort();
            }
        }
    }
}
