/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.igov.service.business.action.task.listener;

import java.util.List;
import org.activiti.engine.FormService;
import org.activiti.engine.HistoryService;
import org.activiti.engine.IdentityService;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.TaskService;
import org.activiti.engine.delegate.DelegateExecution;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.activiti.engine.delegate.DelegateTask;
import org.activiti.engine.delegate.TaskListener;
import org.activiti.engine.identity.Group;
import org.igov.io.mail.Mail;
import org.igov.model.action.task.core.entity.ActionTaskLinkDao;
import org.igov.service.business.action.event.HistoryEventService;
import org.igov.service.business.action.task.core.ActionTaskService;
import org.igov.service.business.subject.message.MessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 *
 * @author HS
 */
@Component("FindGroup")
public class FindGroup implements TaskListener {

    private static final transient Logger LOG = LoggerFactory.getLogger(FindGroup.class);

    @Autowired
    private RuntimeService runtimeService;
    @Autowired
    private HistoryService historyService;
    @Autowired
    private FormService formService;
    @Autowired
    private IdentityService identityService;
    @Autowired
    private TaskService taskService;
    @Autowired
    private RepositoryService repositoryService;
    @Autowired
    private HistoryEventService historyEventService;
    @Autowired
    private ActionTaskService oActionTaskService;
    @Autowired
    private ActionTaskLinkDao actionTaskLinkDao;
    @Autowired
    private MessageService oMessageService;
    @Autowired
    private Mail oMail;

    @Override
    public void notify(DelegateTask delegateTask) {
        DelegateExecution execution = delegateTask.getExecution();
        String group_pattern = (String) execution.getVariable("group_pattern");
        System.out.println("group_pattern: " + group_pattern + "123456");
        // определение user'а
        String user = delegateTask.getAssignee();
        System.out.println("user: " + user);
        // определение списка групп у user
        List<Group> userGroup;
        System.out.println("userGroup-1");
        userGroup = execution.getEngineServices().getIdentityService().createGroupQuery().groupMember(user).list();
        System.out.println("userGroup-2 " + userGroup);

        // определение размера списка групп
        userGroup.size();
        System.out.println("userGroup.size: " + userGroup.size());

              
        try {
            //Проверка количеcтва элементов в коллекции userGroup. Если отлично от единицы, то передать в переменную usertask2 none. Иначе название группы
        if (userGroup.size() == 1)  {
            System.out.println("none");
            execution.setVariable("group_found", userGroup.get(0).getId()); 
            System.out.println("GroupName: " + userGroup.get(0).getName());
        } else {
            execution.setVariable("group_found", "none");
           System.out.println("none"); 
        }
            System.out.println("The end");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
