package org.dspace.app.rest.diracai.service;

import jakarta.servlet.http.HttpServletRequest;
import org.dspace.app.rest.utils.ContextUtil;
import org.dspace.authorize.ResourcePolicy;
import org.dspace.authorize.service.AuthorizeService;
import org.dspace.authorize.service.ResourcePolicyService;
import org.dspace.content.Bitstream;
import org.dspace.content.service.BitstreamService;
import org.dspace.core.Context;
import org.dspace.eperson.EPerson;
import org.dspace.eperson.Group;
import org.dspace.eperson.service.EPersonService;
import org.dspace.eperson.service.GroupService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
public class BitstreamPolicyService {

    @Autowired
    private BitstreamService bitstreamService;

    @Autowired
    private AuthorizeService authorizeService;

    @Autowired
    private ResourcePolicyService resourcePolicyService;

    @Autowired
    private GroupService groupService;

    @Autowired
    private EPersonService ePersonService;

    public void applyPolicyToBitstreams(List<String> bitstreamIds,
                                        Map<String, Object> policyData,
                                        HttpServletRequest request) {
        Context context = ContextUtil.obtainContext(request);

        try {
            String epersonId = (String) policyData.get("epersonId");
            String groupId = (String) policyData.get("groupId");

            if ((epersonId != null && groupId != null) || (epersonId == null && groupId == null)) {
                throw new IllegalArgumentException("Exactly one of 'epersonId' or 'groupId' must be provided.");
            }

            EPerson eperson = null;
            Group group = null;

            if (epersonId != null) {
                eperson = ePersonService.find(context, UUID.fromString(epersonId));
                if (eperson == null) {
                    throw new IllegalArgumentException("EPerson not found: " + epersonId);
                }
            } else {
                group = groupService.find(context, UUID.fromString(groupId));
                if (group == null) {
                    throw new IllegalArgumentException("Group not found: " + groupId);
                }
            }

            int action = resolveActionConstant((String) policyData.get("action"));
            String name = (String) policyData.get("name");
            String description = (String) policyData.get("description");
            String policyType = (String) policyData.get("policyType");
            Integer pageStart = (Integer) policyData.get("pageStart");
            Integer pageEnd = (Integer) policyData.get("pageEnd");

            Date startDate = parseDate((String) policyData.get("startDate"));
            Date endDate = parseDate((String) policyData.get("endDate"));
            Boolean print = (Boolean) policyData.get("print");
            Boolean download = (Boolean) policyData.get("download");


            for (String idStr : bitstreamIds) {
                UUID bitstreamId = UUID.fromString(idStr);
                Bitstream bitstream = bitstreamService.find(context, bitstreamId);
                if (bitstream == null) continue;

                ResourcePolicy rp = resourcePolicyService.create(context, eperson, group);
                rp.setdSpaceObject(bitstream);
                rp.setAction(action);
                rp.setRpType(policyType);
                rp.setRpName(name);
                rp.setRpDescription(description);
                rp.setPageStart(pageStart);
                rp.setPageEnd(pageEnd);
                rp.setStartDate(startDate);
                rp.setEndDate(endDate);
                rp.setDownload(download);
                rp.setPrint(print);

                resourcePolicyService.update(context, rp);
            }

            context.commit();
        } catch (Exception e) {
            throw new RuntimeException("Failed to apply policy to bitstreams", e);
        }
    }

    private int resolveActionConstant(String action) {
        return switch (action.toUpperCase()) {
            case "READ" -> org.dspace.core.Constants.READ;
            case "WRITE" -> org.dspace.core.Constants.WRITE;
            case "DELETE" -> org.dspace.core.Constants.DELETE;
            case "ADD" -> org.dspace.core.Constants.ADD;
            case "REMOVE" -> org.dspace.core.Constants.REMOVE;
            case "ADMIN" -> org.dspace.core.Constants.ADMIN;
            default -> throw new IllegalArgumentException("Unknown action: " + action);
        };
    }

    private Date parseDate(String dateStr) {
        if (dateStr == null || dateStr.isBlank()) return null;
        try {
            return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(dateStr);
        } catch (Exception e) {
            throw new IllegalArgumentException("Invalid date format. Expected: yyyy-MM-dd HH:mm:ss", e);
        }
    }
}
