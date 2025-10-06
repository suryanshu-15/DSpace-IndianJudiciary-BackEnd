package org.dspace.app.rest.diracai.mapper;


import org.dspace.app.rest.diracai.dto.BitstreamPolicyDTO;
import org.dspace.app.rest.diracai.util.PermissionUtils;
import org.dspace.authorize.ResourcePolicy;
import org.springframework.stereotype.Component;

import java.text.SimpleDateFormat;

@Component
public class ResourcePolicyMapper {

    private static final SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    public BitstreamPolicyDTO toDTO(ResourcePolicy policy) {
        BitstreamPolicyDTO dto = new BitstreamPolicyDTO();
        dto.setName(policy.getRpName());
        dto.setDescription(policy.getRpDescription());
        dto.setPolicyType(policy.getRpType());
        dto.setAction(PermissionUtils.resolveActionName(policy.getAction()));
        dto.setStartDate(policy.getStartDate() != null ? formatter.format(policy.getStartDate()) : null);
        dto.setEndDate(policy.getEndDate() != null ? formatter.format(policy.getEndDate()) : null);
        dto.setPageStart(policy.getPageStart());
        dto.setPageEnd(policy.getPageEnd());
        dto.setPrint(policy.isPrint());
        dto.setDownload(policy.isDownload());
        return dto;
    }
}















