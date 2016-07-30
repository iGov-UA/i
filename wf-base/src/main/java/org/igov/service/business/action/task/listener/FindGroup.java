/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.igov.service.business.action.task.listener;

import com.vaadin.terminal.gwt.client.RenderInformation;
import java.util.ArrayList;
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
        execution.setVariable("group_pattern_last", group_pattern);
        LOG.info("group_pattern: " + group_pattern);
        // определение user'а
        String user = delegateTask.getAssignee();
        LOG.info("user: " + user);
        execution.setVariable("user_last", user);
        // определение списка групп у user
        List<Group> userGroup;
//        System.out.println("userGroup-1");
        userGroup = execution.getEngineServices().getIdentityService().createGroupQuery().groupMember(user).list();
//        System.out.println("userGroup-2");

        // определение размера списка групп
        userGroup.size();
//        LOG.info("userGroup.size: " + userGroup.size());

        List<Group> aGroupPatternFound = new ArrayList<Group>();
//for(Group group : userGroup){
// if (group.getId().contains(group_pattern)) {
// aGroupPatternFound.add(group);
// }
//}
        for (int i = 0; i < userGroup.size(); i++) {
            LOG.info("userGroup.get(i).getId(): " + userGroup.get(i).getId());

//            boolean containPattern;
            if (userGroup.get(i).getId().contains(group_pattern)) {
//                System.out.println("Искомая группа соотвутсвующая group_pattern: " + userGroup.get(i).getName());
//                containPattern = userGroup.get(i).getId().contains(group_pattern);// truee
//                System.out.println(containPattern); // true
//                aGroupPatternFound.size();
//                System.out.println("aGroupPatternFound.size(): " + aGroupPatternFound.size());
                aGroupPatternFound.add(userGroup.get(i));
//                aGroupPatternFound.size();
//                System.out.println("aGroupPatternFound.size()_after add: " + aGroupPatternFound.size());

//                aGroupPatternFound.add(i, (Group) userGroup);
//                System.out.println("aGroupPatternFound.add" + aGroupPatternFound);
            }
        }
        
            //Проверка количеcтва элементов в коллекции aGroupPatternFound. Если отлично от единицы, то передать в переменную usertask2 none. Иначе название группы
            if (aGroupPatternFound.size() != 1) {
                LOG.info("aGroupPatternFound.size(): " + aGroupPatternFound.size());
                execution.setVariable("group_found", "[none]");
                LOG.info("none");
                // execution.setVariable("group_found", userGroup.get(0).getId());  // передача ид-группы в значение group_found 
                // System.out.println("GroupName: " + userGroup.get(0).getName());
            } else if(aGroupPatternFound.size() > 1){
            execution.setVariable("group_found", "[many]");
            }else {
                // execution.setVariable("group_found", "none");
                // System.out.println("none");
                execution.setVariable("group_found", userGroup.get(0).getId());  // передача ид-группы в значение group_found 
                LOG.info("GroupID: " + userGroup.get(0).getId());
                LOG.info("GroupName: " + userGroup.get(0).getName());
            }

        
    }

}
