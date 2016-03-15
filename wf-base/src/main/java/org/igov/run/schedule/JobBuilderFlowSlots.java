package org.igov.run.schedule;

import org.igov.service.exception.CommonServiceException;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.igov.service.business.flow.FlowService;

import java.util.Date;
import org.joda.time.DateTime;

/**
 * Autogeneration flow slots. This job invokes method buildFlowSlots by crone.
 * <p/>
 * User: maxline Date: 26.02.2016 Time: 22:34
 */
public class JobBuilderFlowSlots extends IAutowiredSpringJob {

    private final static Logger LOG = LoggerFactory.getLogger(JobBuilderFlowSlots.class);
    @Autowired
    private FlowService oFlowService;

    //Maxline: TODO нормально ли таким образом получать список тестовых flow_ServiceData?
    private static final long[] A_TESTS_ID_FLOW_SERVICE_DATA = {1L, 12L};

    public void execute(JobExecutionContext context) throws JobExecutionException {
        //Maxline: TODO дописать реализацию вызова buildFlowSlots - автогенерация новых слотов
//        String sDateStart = "2015-06-01 00:00:00.000";
//        String sDateStop = "2015-06-07 00:00:00.000";
//
//        DateTime startDate = oFlowService.parseJsonDateTimeSerializer(sDateStart);
//        DateTime stopDate = oFlowService.parseJsonDateTimeSerializer(sDateStop);

        LOG.info("In QuartzJob - executing JOB at {} by context.getTrigger().getName()={}",
                new Date(), context.getTrigger().getName());
        
            //maxline: todo  nID_Flow_ServiceData получаем список всех тестовых услуг
            //maxline: startDate, StopDate - написать алгоритм  проверки - если свободных слотов нету, 
            //то генерить на больший период ,вплоть до полугодя, чтоб в запасе было слотов, на срок "около месяца"

//            for (long nID_Flow_ServiceData : A_TESTS_ID_FLOW_SERVICE_DATA) {
//
//                oFlowService.buildFlowSlots(nID_Flow_ServiceData, startDate, stopDate);
//            }

        
    }
}
