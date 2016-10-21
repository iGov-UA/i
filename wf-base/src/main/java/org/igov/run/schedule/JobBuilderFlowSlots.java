package org.igov.run.schedule;

import static java.lang.Math.toIntExact;
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
import org.igov.service.business.flow.slot.FlowSlotVO;
import org.joda.time.DateTime;

/**
 * Autogeneration flow slots. This job invokes method buildFlowSlots by crone.
 * Проверка запаса свободных слотов в очередях. Если свободных слотов, меньше,
 * чем 20 рабочих дней, то генерируются новые слоты на срок от месяця до
 * полугода вперед, пока не получим свободных слотов на 20 рабочих дней.
 * <p/>
 * Считаем что: - в месяце всего 30 календарных дней; - в месяце 20 рабочих
 * дней; - полгода - 180 календарных дней Для функции автогенерации новых слотов
 * в очереди, такое округление допустимо.
 *
 * Автогенерация запускается по шедулеру ежедневно в 2 часа ночи. Автогенерация
 * выполняется только для очередей, у которых в названии бизнес процесса есть
 * суффикс auto. Пример: Услуга: "176;Видача/заміна паспорта громадянина для
 * виїзду за кордон" Очередь: "4; ДМС, Днепр, Поля 1; 63; 11;
 * dnepr_dms_passport_auto"
 *
 * Принцип работы. Алгоритм проверяет набирается ли свободных 20 рабочих дней на
 * периоде (текущая дата, текущая дата + 180 дней). Если нет, то запускает
 * построитель слотов на периоде (текущая дата, тукущая дата + 30 дней),
 * последний день в период не входит. Проверяет появилось ли 20 свободных
 * рабочих дней, если да - то финиш, если нет - запускаем построитель слотов на
 * периоде(текущая дата + 30 дней, текущая дата + 60 дней) последний день в
 * период не входит. Если построили слотов на 180, и так и не набралось 20
 * свободных рабочих дней - то финиш.
 *
 * @author maxline User: maxline Date: 26.02.2016 Time: 22:34
 */
public class JobBuilderFlowSlots extends IAutowiredSpringJob {

    public static final int DAYS_IN_MONTH = 30;
    public static final int WORK_DAYS_NEEDED = 20;
    public static final int DAYS_IN_HALF_YEAR = 180;
    private static final String SUFFIX_AUTO = "auto";

    private final static Logger LOG = LoggerFactory.getLogger(JobBuilderFlowSlots.class);
    @Autowired
    private FlowService oFlowService;

    @Autowired
    private FlowServiceDataDao flowServiceDataDao;

    //Maxline: TODO исправить потом на получение flowServiceData с признаком auto в названии
    //private static final long[] A_TESTS_ID_FLOW_SERVICE_DATA = {1L, 12L};
    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {
        LOG.info(" !In QuartzJob - executing JOB at {} by context.getTrigger().getName()={}",
                new Date(), context.getTrigger().getName());
        oFlowService.buildFlowSlots();
        
    } 
 /*@Deprecated
    private void checkAndBuildFlowSlots_old(Flow_ServiceData flow, DateTime oDateStart) {
        //Maxline: TODO добавить исключения
        Long nID_Flow_ServiceData = flow.getId();
        Long nID_ServiceData = flow.getnID_ServiceData();   //nID_ServiceData = 358  _test_queue_cancel, nID_ServiceData = 63L Видача/заміна паспорта громадянина для виїзду за кордон
        
        Long nID_SubjectOrganDepartment = flow.getnID_SubjectOrganDepartment();
        LOG.info(" nID_Flow_ServiceData = {}, nID_ServiceData = {}, nID_SubjectOrganDepartment = {}",
                nID_Flow_ServiceData, nID_ServiceData, nID_SubjectOrganDepartment);
        
        int nStartDay = 0;
        DateTime dateStart;// = oDateStart.plusDays(0); //maxline: todo удалить комментарий после тестирования
        DateTime dateEnd;
        
        while (!isEnoughFreeDays(nID_ServiceData, nID_SubjectOrganDepartment, oDateStart)
                && nStartDay < DAYS_IN_HALF_YEAR) {
            dateStart = oDateStart.plusDays(nStartDay);
            LOG.info("dateStart: "+dateStart);
            dateEnd = oDateStart.plusDays((int) (nStartDay + DAYS_IN_HALF_YEAR));
            LOG.info("dateStart = {}, dateEnd = {}", dateStart, dateEnd);
            
            List<FlowSlotVO> resFlowSlotVO = oFlowService.buildFlowSlots(nID_Flow_ServiceData,
                    dateStart, dateEnd); // строит четко на месяц вперед (точнее dateStart - dateEnd) независимо от рабочих или нерабочих дней
            LOG.info(" resFlowSlotVO.size() = {}", resFlowSlotVO.size());
            
            nStartDay += DAYS_IN_MONTH;
        }
        
        boolean bEnoughFreeDays = nStartDay < DAYS_IN_HALF_YEAR;
        LOG.info(" bEnoughFreeDays = {}", bEnoughFreeDays);
    }*/

 /*private boolean isEnoughFreeDays(Long nID_ServiceData, Long nID_SubjectOrganDepartment, DateTime oDateStart) {
        boolean bAll = false; //Получаем только свободные дни
        int nFreeWorkDaysFact;
        Long nID_Service = null; 
        String sID_BP = null; 

        DateTime oDateEnd = oDateStart.plusDays(DAYS_IN_HALF_YEAR);
        LOG.info(" oDateEnd = {}", oDateEnd);

        Days res = oFlowService.getFlowSlots(nID_Service, nID_ServiceData, sID_BP, nID_SubjectOrganDepartment,
                oDateStart, oDateEnd, bAll, WORK_DAYS_NEEDED, 1); //WORK_DAYS_NEEDED
        LOG.info(" Days = {}", res);

        nFreeWorkDaysFact = res.getaDay().size();
        LOG.info(" nFreeWorkDaysFact = {}, WORK_DAYS_NEEDED = {}", nFreeWorkDaysFact, WORK_DAYS_NEEDED);
        for (Day day : res.getaDay()) {
            LOG.info(" Day = {}, isbHasFree = {}", day.getsDate(), day.isbHasFree());
        }
        return nFreeWorkDaysFact >= WORK_DAYS_NEEDED;
    }*/
}
