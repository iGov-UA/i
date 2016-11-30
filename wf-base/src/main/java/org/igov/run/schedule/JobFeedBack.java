package org.igov.run.schedule;

import java.time.LocalDate;
import java.util.Date;
import java.util.List;
import org.activiti.engine.HistoryService;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.history.HistoricProcessInstance;

import org.igov.service.business.action.task.bp.handler.BpServiceHandler;
import org.igov.service.business.escalation.EscalationService;
import org.igov.service.exception.CommonServiceException;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

public class JobFeedBack extends IAutowiredSpringJob {

    private final static Logger LOG = LoggerFactory.getLogger(JobFeedBack.class);
    @Autowired
    private EscalationService escalationService;
    @Autowired
    private HistoryService historyService;
    @Autowired
    private RuntimeService runtimeService;

    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {
        LOG.info("In QuartzJob - executing JOB at {} by context.getTrigger().getName()={}",
                new Date(), context.getTrigger().getName());
        try {
        	 LOG.info("getFeedBackCountBefore : "+BpServiceHandler.getFeedBackCount());
            BpServiceHandler.setFeedBackCount(0L);
            LOG.info("getFeedBackCountAfter : "+BpServiceHandler.getFeedBackCount());
            escalationService.runEscalationAll();
        } catch (CommonServiceException oException) {
            LOG.error("Bad: ", oException.getMessage());
            LOG.debug("FAIL:", oException);
        }
        LocalDate today = LocalDate.now();
        LOG.info("1111111today: "+today);
        LocalDate deadline = today.minusDays(20);
        LOG.info("2222222deadline: "+deadline);
        Date date = java.sql.Date.valueOf(deadline);
        LOG.info("2222222date: "+date);
        List<HistoricProcessInstance> feedbackProcces = historyService.createHistoricProcessInstanceQuery()
                .processDefinitionId(BpServiceHandler.PROCESS_FEEDBACK)
                .startedBefore(date).unfinished().list();
              LOG.info("3333333feedbackProcces: " + feedbackProcces);    
             LOG.info("44444444List feedbackProcces: " + feedbackProcces.size());   
        for (HistoricProcessInstance feedbackProcce : feedbackProcces) {
            LOG.info("555555555Delete feedbackProcce.getId(): " + feedbackProcce.getId());
            runtimeService.deleteProcessInstance(feedbackProcce.getId(), " deprecated");
            
        }
    }
}
