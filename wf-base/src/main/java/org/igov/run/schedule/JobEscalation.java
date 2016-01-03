package org.igov.run.schedule;

import org.igov.service.interceptor.exception.ActivitiRestException;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.igov.model.escalation.EscalationService;

import java.util.Date;

public class JobEscalation extends AutowiredSpringJob {

    private final static Logger LOG = LoggerFactory.getLogger(JobEscalation.class);

    @Autowired
    private EscalationService escalationService;

    public void execute(JobExecutionContext context) throws JobExecutionException {
        LOG.info("[execute]:In QuartzJob - executing JOB at " + new Date() + " by context.getTrigger().getName()="
                + context.getTrigger().getName());
        try {
            //TODO: ��� ����� �������� ����� ������� ���������!
            escalationService.runEscalationAll();
        } catch (ActivitiRestException ex) {
            LOG.info("[execute]:", ex);
        }
    }
}