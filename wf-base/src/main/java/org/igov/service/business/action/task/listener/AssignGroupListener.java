/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.igov.service.business.action.task.listener;

import java.util.Arrays;
import org.activiti.engine.delegate.DelegateTask;
import org.activiti.engine.delegate.TaskListener;
import org.activiti.engine.impl.el.FixedValue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Ольга
 */
public class AssignGroupListener implements TaskListener{
    
    private static final transient Logger LOG = LoggerFactory.getLogger(AssignGroupListener.class);
    
    private FixedValue organ = null;

    @Override
    public void notify(DelegateTask task) {
        LOG.info("organ: " + organ);
        if(organ != null && organ.getExpressionText() != null 
                && !"".equals(organ.getExpressionText())){
            LOG.info("organText: " + organ.getExpressionText());
            task.addCandidateGroups(Arrays.asList(organ.getExpressionText().split(";")));
        }
    }
    
}
