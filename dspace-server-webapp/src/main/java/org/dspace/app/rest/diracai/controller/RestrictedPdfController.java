package org.dspace.app.rest.diracai.controller;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.dspace.app.rest.utils.ContextUtil;
import org.dspace.authorize.ResourcePolicy;
import org.dspace.authorize.service.AuthorizeService;
import org.dspace.authorize.service.ResourcePolicyService;
import org.dspace.core.Context;
import org.dspace.eperson.EPerson;
import org.dspace.eperson.service.GroupService;
import org.dspace.services.RequestService;
import org.dspace.content.Bitstream;
import org.dspace.content.service.BitstreamService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.io.InputStream;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/custom/bitstreams")
public class RestrictedPdfController {

    @Autowired
    private BitstreamService bitstreamService;

    @Autowired
    private ResourcePolicyService resourcePolicyService;

    @Autowired
    private RequestService requestService;

    @Autowired
    private AuthorizeService authorizeService;

    @Autowired
    private GroupService groupService;


    @GetMapping("/{uuid}/filtered-content")
    public void getFilteredContent(@PathVariable UUID uuid,
                                   HttpServletRequest request,
                                   HttpServletResponse response) throws Exception {

        Context context = ContextUtil.obtainContext(request);
        Bitstream bitstream = bitstreamService.findByIdOrLegacyId(context, uuid.toString());

        if (bitstream == null) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND, "Bitstream not found.");
            return;
        }

        EPerson currentUser = context.getCurrentUser(); // null if anonymous

        // ✅ Full access for admin
        if (currentUser != null && authorizeService.isAdmin(context, currentUser)) {
            try (InputStream is = bitstreamService.retrieve(context, bitstream);
                 PDDocument doc = PDDocument.load(is)) {
                response.setContentType("application/pdf");
                doc.save(response.getOutputStream());
            } catch (IOException e) {
                response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Unable to read the PDF content.");
            }
            return;
        }

        List<ResourcePolicy> allPolicies = resourcePolicyService.find(context, bitstream);

        // ✅ Filter matching policies (user, group membership, or Anonymous)
        List<ResourcePolicy> matchedPolicies = allPolicies.stream()
                .filter(policy -> {
                    try {
                        return (currentUser != null && policy.getEPerson() != null && policy.getEPerson().equals(currentUser)) ||
                                (policy.getGroup() != null &&
                                        ("Anonymous".equals(policy.getGroup().getName()) ||
                                                (currentUser != null && groupService.isMember(context, currentUser, policy.getGroup())))
                                );
                    } catch (SQLException e) {
                        throw new RuntimeException(e);
                    }
                })
                .collect(Collectors.toList());

        // If no policy matched, allow full access
        if (matchedPolicies.isEmpty()) {
            try (InputStream is = bitstreamService.retrieve(context, bitstream);
                 PDDocument doc = PDDocument.load(is)) {
                response.setContentType("application/pdf");
                doc.save(response.getOutputStream());
            } catch (IOException e) {
                response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Unable to read the PDF content.");
            }
            return;
        }

        // ✅ Extract allowed page ranges
        List<int[]> allowedRanges = new ArrayList<>();
        for (ResourcePolicy policy : matchedPolicies) {
            Integer start = policy.getPageStart();
            Integer end = policy.getPageEnd();
            if (start != null && end != null && start > 0 && end >= start) {
                allowedRanges.add(new int[]{start, end});
            }
        }

        // ✅ Filter the PDF by allowed pages
        try (InputStream is = bitstreamService.retrieve(context, bitstream);
             PDDocument original = PDDocument.load(is);
             PDDocument filtered = new PDDocument()) {

            int total = original.getNumberOfPages();

            for (int i = 0; i < total; i++) {
                int pageNum = i + 1;
                boolean include = false;
                for (int[] range : allowedRanges) {
                    if (pageNum >= range[0] && pageNum <= range[1]) {
                        include = true;
                        break;
                    }
                }
                if (include) {
                    filtered.addPage(original.getPage(i));
                }
            }

            response.setContentType("application/pdf");
            filtered.save(response.getOutputStream());

        } catch (IOException e) {
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Unable to process PDF for page-level access.");
        }
    }

}
