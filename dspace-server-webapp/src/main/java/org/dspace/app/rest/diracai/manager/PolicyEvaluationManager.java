package org.dspace.app.rest.diracai.manager;

import org.dspace.authorize.ResourcePolicy;
import org.dspace.core.Context;
import org.dspace.eperson.EPerson;
import org.dspace.eperson.service.GroupService;
import org.dspace.app.rest.diracai.dto.BitstreamPolicyDTO;
import org.dspace.app.rest.diracai.mapper.ResourcePolicyMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.sql.SQLException;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class PolicyEvaluationManager {

    @Autowired
    private GroupService groupService;

    @Autowired
    private ResourcePolicyMapper policyMapper;

    public List<BitstreamPolicyDTO> evaluatePolicies(Context context, EPerson user, List<ResourcePolicy> policies) {
        Date now = new Date();

        return policies.stream()
                .filter(policy -> {
                    try {
                        return ((policy.getEPerson() != null && policy.getEPerson().equals(user)) ||
                                (policy.getGroup() != null &&
                                        ("Anonymous".equals(policy.getGroup().getName()) ||
                                                groupService.isMember(context, user, policy.getGroup())))
                        ) &&
                                (policy.getStartDate() == null || !now.before(policy.getStartDate())) &&
                                (policy.getEndDate() == null || !now.after(policy.getEndDate()));
                    } catch (SQLException e) {
                        throw new RuntimeException("Policy evaluation failed", e);
                    }
                })
                .map(policyMapper::toDTO)
                .collect(Collectors.toList());
    }
}
