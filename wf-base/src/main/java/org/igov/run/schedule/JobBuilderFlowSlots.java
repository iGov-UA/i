package org.igov.run.schedule;

import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.igov.service.business.flow.FlowService;

import java.util.Date;
import java.util.List;
import org.igov.model.flow.FlowServiceDataDao;
import org.igov.model.flow.Flow_ServiceData;
import org.igov.service.business.flow.slot.Day;
import org.igov.service.business.flow.slot.Days;
import org.igov.service.business.flow.slot.FlowSlotVO;
import org.joda.time.DateTime;

/**
 * Autogeneration flow slots. This job invokes method buildFlowSlots by crone. 
 * maxline: Проверка запаса свободных слотов на срок - 20 рабочих дней. 
 * Eсли нет свободных слотов, то генеритcя на больший период, помесячно, до полугода.
 * <p/>
 * User: maxline Date: 26.02.2016 Time: 22:34
 */
public class JobBuilderFlowSlots extends IAutowiredSpringJob {
    public static final int DAYS_IN_MONTH = 30;
    public static final int WORK_DAYS_NEEDED = 20;
    public static final int DAYS_IN_HALF_YEAR = 180;
    
    private final static Logger LOG = LoggerFactory.getLogger(JobBuilderFlowSlots.class);
    @Autowired
    private FlowService oFlowService;

    @Autowired
    private FlowServiceDataDao flowServiceDataDao;

    //Maxline: TODO исправить потом на получение flowServiceData с признаком auto в названии
    private static final long[] A_TESTS_ID_FLOW_SERVICE_DATA = {1L, 12L};

    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {
        LOG.info("In QuartzJob - executing JOB at {} by context.getTrigger().getName()={}",
                new Date(), context.getTrigger().getName());

        // maxline: TODO запуск построения свободных слотов для всех очередей с признаком auto 
        //            for (long nID_Flow_ServiceData : A_TESTS_ID_FLOW_SERVICE_DATA) {
        //                строим слоты для всех очередей с признаком auto 
        //            }
        
        DateTime oDateStart = DateTime.now().withTimeAtStartOfDay();
        LOG.info(" oDateStart = {}", oDateStart);

        //Maxline: TODO добавить исключения
        long nID_Flow_ServiceData = 12L; //_test_queue_cancel
        Flow_ServiceData flow = flowServiceDataDao.findByIdExpected(nID_Flow_ServiceData);
        long nID_ServiceData = flow.getnID_ServiceData();   //nID_ServiceData = 358  _test_queue_cancel, nID_ServiceData = 63L Видача/заміна паспорта громадянина для виїзду за кордон
        LOG.info(" nID_Flow_ServiceData = {}, nID_ServiceData = {}", nID_Flow_ServiceData, nID_ServiceData);

        int nStartDay = 0;
        DateTime dateStart = oDateStart.plusDays(0);
        DateTime dateEnd;

        while (!isEnoughFreeDays(nID_ServiceData, oDateStart) && nStartDay < DAYS_IN_HALF_YEAR) {
            dateStart = dateStart.plusDays(nStartDay);
            dateEnd = dateStart.plusDays(nStartDay + DAYS_IN_MONTH);

            List<FlowSlotVO> resFlowSlotVO = oFlowService.buildFlowSlots(nID_Flow_ServiceData, dateStart, dateEnd);
            LOG.info(" resFlowSlotVO.size() = {}", resFlowSlotVO.size());

            nStartDay += DAYS_IN_MONTH;
        }

        boolean bEnoughFreeDays = nStartDay < DAYS_IN_HALF_YEAR;
        LOG.info(" bEnoughFreeDays = {}", bEnoughFreeDays);
    }

    private boolean isEnoughFreeDays(Long nID_ServiceData, DateTime oDateStart) {
        boolean bAll = false; //Получаем только свободные дни
        int nFreeWorkDaysFact;
        Long nID_Service = null; 
        Long nID_SubjectOrganDepartment = null;
        String sID_BP = null;

        DateTime oDateEnd = oDateStart.plusDays(DAYS_IN_HALF_YEAR);
        LOG.info(" oDateEnd = {}", oDateEnd);

        Days res = oFlowService.getFlowSlots(nID_Service, nID_ServiceData, sID_BP, nID_SubjectOrganDepartment,
                oDateStart, oDateEnd, bAll, WORK_DAYS_NEEDED);
        LOG.info(" Days = {}", res);

        nFreeWorkDaysFact = res.getaDay().size();
        LOG.info(" nFreeWorkDaysFact = {}, WORK_DAYS_NEEDED = {}", nFreeWorkDaysFact, WORK_DAYS_NEEDED);
        for (Day day : res.getaDay()) {
            LOG.info(" Day = {}, isbHasFree = {}", day.getsDate(), day.isbHasFree());
        }
        return nFreeWorkDaysFact >= WORK_DAYS_NEEDED;
    }
}
