/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.igov.run.schedule;

import org.quartz.*;
import org.quartz.impl.StdSchedulerFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author inna
 */
public class ScheduleInitializerFeedBack {

    private final static Logger LOG = LoggerFactory.getLogger(ScheduleInitializerFeedBack.class);
    
    public void init() throws SchedulerException {
        LOG.info("Schedule Factory...");
        SchedulerFactory oSchedulerFactory = new StdSchedulerFactory();
        LOG.info("getScheduler...");
        Scheduler oScheduler = oSchedulerFactory.getScheduler();

        long nNowMS = System.currentTimeMillis();
        LOG.info("(nNowMS={})", nNowMS);

        JobDetail oJobDetail_FeedBack_Standart = new JobDetail("oJobDetail_FeedBack_Standart",
                "oJobDetail_FeedBack_Standart", JobFeedBack.class);
        CronTrigger oCronTrigger_EveryNight_Deep = new CronTrigger("oCronTrigger_EveryNight_Deep",
                "oCronTrigger_EveryNight_Group");
        try {
            LOG.info("oCronExpression__EveryNight_Deep...");
            CronExpression oCronExpression__EveryNight_Deep = new CronExpression(
                    "0 0 2 1/1 * ?");
            LOG.info("oCronExpression__EveryNight_Deep.setCronExpression...");
            oCronTrigger_EveryNight_Deep.setCronExpression(oCronExpression__EveryNight_Deep);
        } catch (Exception oException) {
            LOG.error("Bad: ", oException.getMessage());
            LOG.debug("FAIL:", oException);
            //oException.printStackTrace();
        }
        LOG.info("scheduleJob...");
        oScheduler.scheduleJob(oJobDetail_FeedBack_Standart, oCronTrigger_EveryNight_Deep);

        LOG.info("start...");
        oScheduler.start();
        LOG.info("Ok!!");

    }

}


