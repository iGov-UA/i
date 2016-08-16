package org.igov.run.schedule;

import java.util.Date;

import org.igov.service.business.finance.PaymentProcessorService;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

public class JobPaymentProcessor extends IAutowiredSpringJob {
	
	private final static Logger LOG = LoggerFactory.getLogger(JobPaymentProcessor.class);
	
	@Autowired
	private PaymentProcessorService paymentProcessorService;

    public void execute(JobExecutionContext context) throws JobExecutionException {
        LOG.info("In QuartzJob - executing JOB at {} by context.getTrigger().getName()={}",
                new Date(), context.getTrigger().getName());
        
        paymentProcessorService.loadPaymentInformation();
    }

	
}