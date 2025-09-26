package org.dspace.app.rest.diracai.service.impl;

import jakarta.servlet.http.HttpServletRequest;
import org.dspace.app.rest.diracai.dto.BitstreamPolicyDTO;
import org.dspace.app.rest.diracai.exception.CustomException;
import org.dspace.app.rest.diracai.manager.PolicyEvaluationManager;
import org.dspace.app.rest.diracai.service.BitstreamPermissionService;
import org.dspace.app.rest.diracai.service.PdfAConversionService;
import org.dspace.app.rest.diracai.util.MetadataUtils;
import org.dspace.app.rest.utils.ContextUtil;
import org.dspace.authorize.ResourcePolicy;
import org.dspace.authorize.service.AuthorizeService;
import org.dspace.authorize.service.ResourcePolicyService;
import org.dspace.content.Bitstream;
import org.dspace.content.service.BitstreamService;
import org.dspace.core.Context;
import org.dspace.eperson.EPerson;
import org.dspace.eperson.service.GroupService;
import org.dspace.services.RequestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.SQLException;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
public class BitstreamPermissionServiceImpl implements BitstreamPermissionService {

    @Autowired
    private BitstreamService bitstreamService;

    @Autowired
    private RequestService requestService;

    @Autowired
    private AuthorizeService authorizeService;

    @Autowired
    private GroupService groupService;

    @Autowired
    private ResourcePolicyService resourcePolicyService;

    @Autowired
    private PolicyEvaluationManager evaluationManager;

    @Autowired
    private PdfAConversionService pdfAConversionService;

    @Autowired
    private MetadataUtils metadataUtils;



    @Override
    public Map<String, Object> retrievePermissions(UUID uuid, HttpServletRequest request) {
        Context context = ContextUtil.obtainContext(request);
        EPerson currentUser = context.getCurrentUser();

        if (currentUser == null) {
            throw new CustomException("Unauthorized", 401);
        }

        Bitstream bitstream = null;
        try {
            bitstream = bitstreamService.findByIdOrLegacyId(context, uuid.toString());
            if (!pdfAConversionService.isConvertedToPdfA(context, bitstream)) {
                Date disposalDate = metadataUtils.getDisposalDate(context, bitstream);
                if (disposalDate != null && new Date().after(disposalDate)) {
                    pdfAConversionService.convertToPdfA(context, bitstream);
                }
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        if (bitstream == null) {
            throw new CustomException("Bitstream not found", 404);
        }

        boolean isAdmin = false;
        try {
            isAdmin = authorizeService.isAdmin(context, currentUser);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        List<ResourcePolicy> policies = null;
        try {
            policies = resourcePolicyService.find(context, bitstream);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        List<BitstreamPolicyDTO> filtered = evaluationManager.evaluatePolicies(context, currentUser, policies);

        return Map.of(
                "bitstreamId", uuid.toString(),
                "userId", currentUser.getID(),
                "isAdmin", isAdmin,
                "policies", filtered
        );
    }
}
