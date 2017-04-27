package org.igov.run.schedule;

import org.quartz.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.igov.io.GeneralConfig; 

/**
 * User: goodg_000 Date: 27.08.2015 Time: 1:05
 */
public class JobsInitializer implements InitializingBean, ApplicationContextAware {

    private final static Logger LOG = LoggerFactory.getLogger(JobsInitializer.class);

    @Autowired
    GeneralConfig generalConfig;

    private static ApplicationContext applicationContext;
    private Scheduler scheduler;

    /**
     * @return used by {@link AutowiredSpringJob} to autowire property beans into jobs.
     */
    static ApplicationContext getApplicationContext() {
        return applicationContext;
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    public void setScheduler(Scheduler scheduler) {
        this.scheduler = scheduler;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        addEscalationJob(scheduler);
        addFeedBackJob(scheduler);
        addBuilderFlowSlotsJob(scheduler);
    }

    private void addEscalationJob(Scheduler scheduler) throws SchedulerException {
        JobDetail oJobDetail_Escalation_Standart = new JobDetail("oJobDetail_Escalation_Standart",
                "oJobDetail_Escalation_Group", JobEscalation.class);

        CronTrigger oCronTrigger_EveryNight_Deep = new CronTrigger("oCronTrigger_EveryNight_Deep",
                "oCronTrigger_EveryNight_EscalationGroup");
        try {
            LOG.info("oCronExpression__EveryNight_Deep...");
            CronExpression oCronExpression__EveryNight_Deep = new CronExpression("0 0 4 1/1 * ?");   //maxline: todo поменять обратно на 2 часа ночи с 4-х
            LOG.info("oCronExpression__EveryNight_Deep.setCronExpression...");
            oCronTrigger_EveryNight_Deep.setCronExpression(oCronExpression__EveryNight_Deep);
        } catch (Exception oException) {
            LOG.error("FAIL: ", oException.getMessage());
            LOG.debug("FAIL: ", oException);
        }
        if (!generalConfig.isSelfTest() && !generalConfig.isTest_Escalation() 
                && !"https://prod-double-region.tech.igov.org.ua".equalsIgnoreCase(generalConfig.getSelfHost())) {
            LOG.info("scheduleJob...");
            scheduler.scheduleJob(oJobDetail_Escalation_Standart, oCronTrigger_EveryNight_Deep);
        } else {
            LOG.info("scheduleJob... SKIPED(test)!");
        }
    }
    
    private void addFeedBackJob(Scheduler scheduler) throws SchedulerException {
        JobDetail oJobDetail_FeedBack_Standart = new JobDetail("oJobDetail_FeedBack_Standart",
                "oJobDetail_FeedBack_Group", JobFeedBack.class);

        CronTrigger oCronTrigger_EveryNight_Deep = new CronTrigger("oCronTrigger_EveryNight_Deep",
                "oCronTrigger_EveryNight_FeedBackGroup");
        try {
            LOG.info("oCronExpression__EveryNight_Deep...");
            CronExpression oCronExpression__EveryNight_Deep = new CronExpression("0 0 4 1/1 * ?");   // maxline: todo поменять обратно на 2 часа ночи с 4-х
            LOG.info("oCronExpression__EveryNight_Deep.setCronExpression...");
            oCronTrigger_EveryNight_Deep.setCronExpression(oCronExpression__EveryNight_Deep);
        } catch (Exception oException) {
            LOG.error("FAIL: ", oException.getMessage());
            LOG.debug("FAIL: ", oException);
        }
        //TODO:раскомментировать после тестирования
       // if (!generalConfig.isSelfTest() && !"https://prod-double-region.tech.igov.org.ua".equalsIgnoreCase(generalConfig.getSelfHost())) {
            LOG.info("scheduleJob...");
            scheduler.scheduleJob(oJobDetail_FeedBack_Standart, oCronTrigger_EveryNight_Deep);  
        /*} else {
            LOG.info("scheduleJob... SKIPED(test)!");
        }*/
    }

    private void addBuilderFlowSlotsJob(Scheduler scheduler) throws SchedulerException {
        JobDetail oJobDetail_BuilderFlowSlots_Standart = new JobDetail("oJobDetail_BuilderFlowSlots_Standart",
                "oJobDetail_BuilderFlowSlots_Group", JobBuilderFlowSlots.class);

        CronTrigger oCronTrigger_EveryNight_Deep = new CronTrigger("oCronTrigger_EveryNight_Deep",
                "oCronTrigger_EveryNight_BuilderFlowSlotsJobGroup");
        try {
            LOG.info("oCronExpression__EveryNight_Deep...");
            CronExpression oCronExpression__EveryNight_Deep = new CronExpression("0 30 6 1/1 * ?"); //0 0 2 1/1 * ?
            LOG.info("oCronExpression__EveryNight_Deep.setCronExpression...");
            oCronTrigger_EveryNight_Deep.setCronExpression(oCronExpression__EveryNight_Deep); 
        } catch (Exception oException) {
            LOG.error("FAIL: ", oException.getMessage());
            LOG.debug("FAIL: ", oException);
        }
        //TODO:раскомментировать после тестирования
        //if (true || !generalConfig.isSelfTest()) {
            LOG.info("scheduleJob...");
            scheduler.scheduleJob(oJobDetail_BuilderFlowSlots_Standart, oCronTrigger_EveryNight_Deep);
        /*} else {
            LOG.info("scheduleJob... SKIPED(test)!");
        }*/
    }
    
    private void addPaymentProcessorJob(Scheduler scheduler) throws SchedulerException {
        JobDetail oJobDetail_PaymentProcessor_Standart = new JobDetail("oJobDetail_PaymentProcessor_Standart",
                "oJobDetail_PaymentProcesor_Group", JobPaymentProcessor.class);

        CronTrigger oCronTrigger_EveryNight_Deep = new CronTrigger("oCronTrigger_EveryNight_Deep",
                "oCronTrigger_EveryNight_Group");
        try {
            LOG.info("oCronExpression__EveryNight_Deep...");
            CronExpression oCronExpression__EveryNight_Deep = new CronExpression("0 30 9 1/1 * ?");  
            LOG.info("oCronExpression__EveryNight_Deep.setCronExpression...");
            oCronTrigger_EveryNight_Deep.setCronExpression(oCronExpression__EveryNight_Deep);
        } catch (Exception oException) {
            LOG.error("FAIL: ", oException.getMessage());
            LOG.debug("FAIL: ", oException);
        }
        if (!generalConfig.isSelfTest() && !"https://prod-double-region.tech.igov.org.ua".equalsIgnoreCase(generalConfig.getSelfHost())) {
            LOG.info("scheduleJob...");
            scheduler.scheduleJob(oJobDetail_PaymentProcessor_Standart, oCronTrigger_EveryNight_Deep);
        } else {
            LOG.info("scheduleJob... SKIPED(test)!");
        }
    }

}
