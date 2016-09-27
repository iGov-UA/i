package org.igov.run.schedule;

import java.util.Date;

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

    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {
        LOG.info("In QuartzJob - executing JOB at {} by context.getTrigger().getName()={}",
                new Date(), context.getTrigger().getName());
        try {
            BpServiceHandler.setFeedBackCount(0L);
            escalationService.runEscalationAll();
        } catch (CommonServiceException oException) {
            LOG.error("Bad: ", oException.getMessage());
            LOG.debug("FAIL:", oException);
        }
    }
}
