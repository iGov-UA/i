/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.igov.service.business.action.task.listener;

import org.activiti.engine.delegate.DelegateTask;
import org.activiti.engine.delegate.TaskListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.igov.service.business.action.event.CloseTaskEvent;
import org.json.simple.parser.ParseException;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Elena
 */
@Component("DoWorkOnCloseTaskEvent")
public class DoWorkOnCloseTaskEvent implements TaskListener {

    private final static org.slf4j.Logger LOG = LoggerFactory.getLogger(DoWorkOnCloseTaskEvent.class);
        
    @Autowired
    private CloseTaskEvent closeTaskEvent;

    @Override
    public void notify(DelegateTask delegateTask) {

        LOG.info("DoWorkOnCloseTaskEvent start..." + delegateTask.getProcessInstanceId());

        try {
            closeTaskEvent.doWorkOnCloseTaskEvent(true, delegateTask.getId(), null, false);
        } catch (ParseException e) {
           LOG.info("DoWorkOnCloseTaskEvent throws an errorrrrrrrrrrrrr", e);
            throw new RuntimeException(e);

        }
    }
} 
    
