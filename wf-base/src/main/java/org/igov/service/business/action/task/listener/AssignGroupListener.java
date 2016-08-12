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
import org.activiti.engine.delegate.Expression;
import org.activiti.engine.delegate.TaskListener;
import org.activiti.engine.identity.Group;
import org.activiti.engine.task.IdentityLink;
import static org.igov.service.business.action.task.core.AbstractModelTask.getStringFromFieldExpression;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 *
 * @author Ольга
 */
@Component("assignGroup")
public class AssignGroupListener implements TaskListener {

    private static final transient Logger LOG = LoggerFactory.getLogger(AssignGroupListener.class);
    private Expression sOrgan;

    @Override
    public void notify(DelegateTask delegateTask) {
        DelegateExecution execution = delegateTask.getExecution();
        String organValue = getStringFromFieldExpression(this.sOrgan, execution);
        LOG.info("!!!!!!!!!!!!!!!!!!!!!!!!!!!!!organValue: " + organValue);
        Group group;
        try {
            if (organValue != null && !"".equals(organValue)) {
                List<String> groupsNew = new ArrayList<>(Arrays.asList(organValue.replaceAll(" ", "").split(",")));
                IdentityService identityService = execution.getEngineServices().getIdentityService();
                for (String groupNew : groupsNew) {
                    group = identityService.createGroupQuery().groupId(groupNew).singleResult();
                    if (group == null) {
                        group = identityService.newGroup(groupNew);
                        group.setName(groupNew);
                        group.setType("assignment");
                        identityService.saveGroup(group);
                        LOG.info("!!!!!!!!!!!!!!!!!!!!!!!!!!!!!created group: " + organValue);
                    }
                }
                Set<IdentityLink> groupsOld = delegateTask.getCandidates();
                groupsOld.stream().forEach((groupOld) -> {
                    groupsNew.add(groupOld.getGroupId());
                });
                delegateTask.addCandidateGroups(groupsNew);
            }
        } catch (Exception ex) {
            LOG.error("!!!!!!!!!!!!!!!!", ex);
        }
    }

}
