package org.igov.run.schedule;

import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.igov.service.business.flow.FlowService;
import java.util.Date;

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

    private final static Logger LOG = LoggerFactory.getLogger(JobBuilderFlowSlots.class);
    @Autowired
    private FlowService oFlowService;

    //Maxline: TODO исправить потом на получение flowServiceData с признаком auto в названии
    //private static final long[] A_TESTS_ID_FLOW_SERVICE_DATA = {1L, 12L};
    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {
        
        try{
            LOG.info(" !In QuartzJob - executing JOB at {} by context.getTrigger().getName()={}",
                    new Date(), context.getTrigger().getName());
            oFlowService.buildFlowSlots();
            LOG.info(" !In QuartzJob - executing JOB at {} by context.getTrigger().getName()={} end!!!",
                    new Date(), context.getTrigger().getName());
        }
        catch(Exception ex){
            LOG.info("JobBuilderFlowSlots throws an error: {}", ex);
        }
    } 
}
