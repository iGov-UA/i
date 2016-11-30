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
import org.springframework.stereotype.Component;

import com.google.common.base.Function;
import com.google.common.collect.Collections2;
import com.google.common.collect.Lists;
import org.activiti.engine.delegate.Expression;
import static org.igov.service.business.action.task.core.AbstractModelTask.getStringFromFieldExpression;

/**
 *
 * @author Ольга
 */
@Component("assignGroup")
public class AssignGroupListener implements TaskListener {

	private static final transient Logger LOG = LoggerFactory.getLogger(AssignGroupListener.class);
        private Expression group_prefix;
	// private Expression organ;

	@Override
	public void notify(DelegateTask delegateTask) {
		DelegateExecution execution = delegateTask.getExecution();
		// String organValue = getStringFromFieldExpression(organ, execution);
		String organValue = (String) execution.getVariable("organ");
		//String prefixValue = (String) execution.getVariable("group_prefix");
                String prefixValue = getStringFromFieldExpression(group_prefix, execution);
		LOG.info("organValue: " + organValue);
		LOG.info("prefixValue: " + prefixValue);
		Group group;
		try {
			if (organValue != null && !"".equals(organValue)) {
				if (prefixValue != null && !"".equals(prefixValue)) {
					List<String> aGroupOld = new ArrayList<>(Arrays.asList(organValue.replaceAll(" ", "").split(",")));
					/**
					 * Создаем коллекцию с organValue+prefixValue
					 */
					List<String> groupsNewWithPrefix = Lists.newArrayList(Collections2.transform(
							aGroupOld, new Function<String, String>() {
							    @Override
							    public String apply(String groupNew) {
							    	String groupWithPrefix = prefixValue + groupNew;
								return groupWithPrefix;
							    }
							}));
					IdentityService identityService = execution.getEngineServices().getIdentityService();
					for (String groupNew : groupsNewWithPrefix) {
						group = identityService.createGroupQuery().groupId(groupNew).singleResult();
						if (group == null) {
							group = identityService.newGroup(groupNew);
							group.setName(groupNew);
							group.setType("assignment");
							identityService.saveGroup(group);
							LOG.info("created group: " + organValue);
						}
					}
					Set<IdentityLink> groupsOld = delegateTask.getCandidates();
					groupsOld.stream().forEach((groupOld) -> {
						groupsNewWithPrefix.add(groupOld.getGroupId());
					});
					delegateTask.addCandidateGroups(groupsNewWithPrefix);
				}
			}
		} catch (Exception ex) {
			LOG.error("!!!!!!!!!!!!!!!!", ex);
		}
	}

}
