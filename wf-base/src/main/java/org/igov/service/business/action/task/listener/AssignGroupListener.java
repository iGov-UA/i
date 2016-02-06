/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.igov.service.business.action.task.listener;

import java.util.Arrays;
import java.util.List;
import java.util.Set;
import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.delegate.DelegateTask;
import org.activiti.engine.delegate.TaskListener;
import org.activiti.engine.task.IdentityLink;
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

    @Override
    public void notify(DelegateTask task) {
        DelegateExecution execution = task.getExecution();
        String organ = (String) execution.getVariable("act_id_group.id_");
        LOG.info("organ: " + organ);
        if (organ != null && !"".equals(organ)) {
            List<String> groups = Arrays.asList(organ.split(","));
            Set<IdentityLink> groupOld = task.getCandidates();
            for (IdentityLink group : groupOld) {
                groups.add(group.getGroupId());
            }
            task.addCandidateGroups(groups);
        }
    }

}
