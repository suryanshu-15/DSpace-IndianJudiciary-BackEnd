package org.dspace.app.rest.diracai.service.impl;

import org.dspace.app.rest.diracai.service.RoleAuditLogService;
import org.dspace.core.Context;
import org.dspace.eperson.Group;
import org.dspace.eperson.EPerson;
import org.dspace.eperson.GroupServiceImpl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

@Service
@Primary
public class CustomGroupServiceImpl extends GroupServiceImpl {

    @Autowired
    private RoleAuditLogService roleAuditLogService;

    @Override
    public void addMember(Context context, Group group, EPerson e) {
        if (isDirectMember(group, e)) {
            return;
        }

        super.addMember(context, group, e);
        roleAuditLogService.logRoleChange(context, e.getID(), "ROLE_ASSIGN", "Group: " + group.getName());
    }
}
