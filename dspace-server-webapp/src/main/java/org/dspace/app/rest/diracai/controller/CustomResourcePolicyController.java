package org.dspace.app.rest.diracai.controller;

import jakarta.servlet.http.HttpServletRequest;
import org.dspace.app.rest.utils.ContextUtil;
import org.dspace.authorize.ResourcePolicy;
import org.dspace.authorize.service.ResourcePolicyService;
import org.dspace.core.Constants;
import org.dspace.core.Context;
import org.dspace.eperson.EPerson;
import org.dspace.eperson.Group;
import org.dspace.eperson.service.EPersonService;
import org.dspace.eperson.service.GroupService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/custom/resourcepolicies")
public class CustomResourcePolicyController {

    @Autowired
    private ResourcePolicyService resourcePolicyService;

    @Autowired
    private EPersonService epersonService;

    @Autowired
    private GroupService groupService;

    @PutMapping("/{id}/update-permissions")
    public ResponseEntity<?> updatePolicyPermissions(@PathVariable Integer id,
                                                     @RequestBody Map<String, Object> updates,
                                                     HttpServletRequest request) throws Exception {
        Context context = ContextUtil.obtainContext(request);

        try {
            ResourcePolicy policy = resourcePolicyService.find(context, id);
            if (policy == null) {
                return ResponseEntity.notFound().build();
            }

            // ✅ Update EPerson if provided
            if (updates.containsKey("eperson")) {
                String epersonUuid = (String) updates.get("eperson");
                EPerson eperson = epersonService.find(context, UUID.fromString(epersonUuid));
                if (eperson != null) {
                    policy.setEPerson(eperson);
                } else {
                    return ResponseEntity.badRequest().body("❌ Invalid eperson UUID: " + epersonUuid);
                }
            }

            // ✅ Update Group if provided
            if (updates.containsKey("group")) {
                String groupUuid = (String) updates.get("group");
                Group group = groupService.find(context, UUID.fromString(groupUuid));
                if (group != null) {
                    policy.setGroup(group);
                } else {
                    return ResponseEntity.badRequest().body("❌ Invalid group UUID: " + groupUuid);
                }
            }

            // ✅ Other fields
            if (updates.containsKey("name")) {
                policy.setRpName((String) updates.get("name"));
            }

            if (updates.containsKey("description")) {
                policy.setRpDescription((String) updates.get("description"));
            }

            if (updates.containsKey("policyType")) {
                policy.setRpType((String) updates.get("policyType"));
            }

            if (updates.containsKey("action")) {
                String action = (String) updates.get("action");
                int actionId = findActionID(action);
                if (actionId == -1) {
                    return ResponseEntity.badRequest().body("❌ Invalid action: " + action);
                }
                policy.setAction(actionId);
            }

            if (updates.containsKey("startDate")) {
                Object val = updates.get("startDate");
                if (val != null) {
                    policy.setStartDate(javax.xml.bind.DatatypeConverter.parseDateTime(val.toString()).getTime());
                } else {
                    policy.setStartDate(null);
                }
            }

            if (updates.containsKey("endDate")) {
                Object val = updates.get("endDate");
                if (val != null) {
                    policy.setEndDate(javax.xml.bind.DatatypeConverter.parseDateTime(val.toString()).getTime());
                } else {
                    policy.setEndDate(null);
                }
            }

            if (updates.containsKey("pageStart")) {
                Object pageStartVal = updates.get("pageStart");
                if (pageStartVal instanceof Number) {
                    policy.setPageStart(((Number) pageStartVal).intValue());
                }
            }

            if (updates.containsKey("pageEnd")) {
                Object pageEndVal = updates.get("pageEnd");
                if (pageEndVal instanceof Number) {
                    policy.setPageEnd(((Number) pageEndVal).intValue());
                }
            }

            if (updates.containsKey("print")) {
                policy.setPrint(Boolean.parseBoolean(updates.get("print").toString()));
            }

            if (updates.containsKey("download")) {
                policy.setDownload(Boolean.parseBoolean(updates.get("download").toString()));
            }

            // ✅ Persist changes
            resourcePolicyService.update(context, policy);
            context.complete();

            // ✅ Build response
            Map<String, Object> response = new HashMap<>();
            response.put("message", "✅ Policy updated successfully");
            response.put("policyId", policy.getID());
            response.put("name", policy.getRpName());
            response.put("description", policy.getRpDescription());
            response.put("policyType", policy.getRpType());
            response.put("action", Constants.actionText[policy.getAction()]);
            response.put("startDate", policy.getStartDate());
            response.put("endDate", policy.getEndDate());
            response.put("pageStart", policy.getPageStart());
            response.put("pageEnd", policy.getPageEnd());
            response.put("print", policy.isPrint());
            response.put("download", policy.isDownload());
            response.put("eperson", policy.getEPerson() != null ? policy.getEPerson().getID() : null);
            response.put("group", policy.getGroup() != null ? policy.getGroup().getID() : null);

            return ResponseEntity.ok(response);

        } finally {
            if (context != null && context.isValid()) {
                context.abort();
            }
        }
    }

    // ✅ Utility to resolve action name
    private int findActionID(String actionText) {
        for (int i = 0; i < Constants.actionText.length; i++) {
            if (Constants.actionText[i].equalsIgnoreCase(actionText)) {
                return i;
            }
        }
        return -1;
    }



}
