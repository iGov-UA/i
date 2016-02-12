/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.igov.service.business.action.task.listener;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import org.activiti.engine.IdentityService;
import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.delegate.DelegateTask;
import org.activiti.engine.delegate.TaskListener;
import org.activiti.engine.identity.Group;
import org.activiti.engine.task.IdentityLink;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 *
 * @author Ольга
 */
@Component("assignGroup")
public class AssignGroupListener implements TaskListener {

    private static final transient Logger LOG = LoggerFactory.getLogger(AssignGroupListener.class);

    @Override
    public void notify(DelegateTask task) {
        DelegateExecution execution = task.getExecution();
        String organ = (String) execution.getVariable("organ");
        LOG.info("!!!!!!!!!!!!!!!!!!!!!!!!!!!!!organ: " + organ);
        Group group;
        try {
            if (organ != null && !"".equals(organ)) {
                List<String> groupsNew = new ArrayList<String>(Arrays.asList(organ.split(",")));
                IdentityService identityService = execution.getEngineServices().getIdentityService();
                for (String groupNew : groupsNew) {
                    group = identityService.createGroupQuery().groupId(groupNew).singleResult();
                    if (group == null) {
                        group = identityService.newGroup(organ);
                        group.setName(organ);
                        group.setType("assignment");
                        identityService.saveGroup(group);
                        LOG.info("!!!!!!!!!!!!!!!!!!!!!!!!!!!!!created group: " + organ);
                    }
                }
                Set<IdentityLink> groupsOld = task.getCandidates();
                for (IdentityLink groupOld : groupsOld) {
                    groupsNew.add(groupOld.getGroupId());
                }
                task.addCandidateGroups(groupsNew);
            }
        } catch (Exception ex) {
            LOG.error("!!!!!!!!!!!!!!!!", ex);
        }
    }

}
