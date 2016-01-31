package org.igov.service.business.flow;

import org.activiti.engine.RepositoryService;
import org.activiti.engine.repository.ProcessDefinition;
import org.activiti.engine.task.Task;
import org.apache.commons.lang3.time.DateUtils;
import org.igov.model.core.GenericEntityDao;
import org.igov.model.flow.*;
import org.igov.model.subject.SubjectOrganDepartment;
import org.igov.service.business.action.task.core.ActionTaskService;
import org.igov.service.business.flow.handler.BaseFlowSlotScheduler;
import org.igov.service.business.flow.handler.FlowPropertyHandler;
import org.igov.service.business.flow.slot.ClearSlotsResult;
import org.igov.service.business.flow.slot.Day;
import org.igov.service.business.flow.slot.Days;
import org.igov.service.business.flow.slot.FlowSlotVO;
import org.igov.service.exception.RecordNotFoundException;
import org.igov.util.ToolDuration;
import org.igov.util.JSON.JsonDateTimeSerializer;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import javax.xml.datatype.Duration;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * User: goodg_000
 * Date: 29.06.2015
 * Time: 18:11
 */
@Service
public class FlowService implements ApplicationContextAware {

    private static final Logger LOG = LoggerFactory.getLogger(FlowService.class);
    private static final Logger LOG_BIG = LoggerFactory.getLogger("FlowServiceBig");
    
    private static final long DEFAULT_FLOW_PROPERTY_CLASS = 1l;

    @Autowired
    private FlowSlotDao flowSlotDao;

    @Autowired
    @Qualifier("flowPropertyDao")
    private GenericEntityDao<FlowProperty> flowPropertyDao;

    @Autowired
    @Qualifier("flowPropertyClassDao")
    private GenericEntityDao<FlowPropertyClass> flowPropertyClassDao;

    @Autowired
    @Qualifier("subjectOrganDepartmentDao")
    private GenericEntityDao<SubjectOrganDepartment> subjectOrganDepartmentDao;

    @Autowired
    private RepositoryService repositoryService;
    
    @Autowired
    private FlowServiceDataDao flowServiceDataDao;

    @Autowired
    private FlowSlotTicketDao oFlowSlotTicketDao;

    @Autowired
    private FlowLinkDao flowLinkDao;

    private ApplicationContext applicationContext;

    @Autowired
    private ActionTaskService actionTaskService;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    public Days getFlowSlots(Long nID_Service, Long nID_ServiceData, String sID_BP, Long nID_SubjectOrganDepartment,
            DateTime startDate, DateTime endDate, boolean bAll,
            int nFreeDays) {

        List<FlowSlot> aFlowSlot;
        Flow_ServiceData oFlow = null;
        if (nID_Service != null) {
            oFlow = getFlowByLink(nID_Service, nID_SubjectOrganDepartment);
        }
        if (oFlow != null) {
            aFlowSlot = flowSlotDao.findFlowSlotsByFlow(oFlow.getId(), startDate, endDate);
        } else {
            if (nID_ServiceData != null) {
                aFlowSlot = flowSlotDao.findFlowSlotsByServiceData(nID_ServiceData, nID_SubjectOrganDepartment, startDate, endDate);
            } else if (sID_BP != null) {
                aFlowSlot = flowSlotDao.findFlowSlotsByBP(sID_BP, nID_SubjectOrganDepartment, startDate, endDate);
            } else {
                throw new IllegalArgumentException("nID_Service, nID_ServiceData, sID_BP are null!");
            }
        }

        Map<DateTime, Day> daysMap = new TreeMap<>();
        if (bAll) {
            DateTime currDate = startDate;
            while (currDate.isBefore(endDate)) {
                Day day = new Day(currDate);
                daysMap.put(currDate, day);
                currDate = currDate.plusDays(1);
            }
        }

        for (FlowSlot flowSlot : aFlowSlot) {
            DateTime currDate = flowSlot.getsDate().withTimeAtStartOfDay();
            FlowSlotVO flowSlotVO = new FlowSlotVO(flowSlot);
            if (!bAll && !flowSlotVO.isbFree()) {
                continue;
            }

            Day day = daysMap.get(currDate);
            if (day == null) {
                day = new Day(currDate);
                daysMap.put(currDate, day);
            }

            day.getaSlot().add(flowSlotVO);

            if (!day.isbHasFree() && flowSlotVO.isbFree()) {
                day.setbHasFree(true);
            }
        }

        Days res = new Days();
        int freeDaysCount = 0;

        for (Map.Entry<DateTime, Day> entry : daysMap.entrySet()) {
            Day day = entry.getValue();
            if (bAll || day.isbHasFree()) {
                res.getaDay().add(day);
            }

            if (day.isbHasFree()) {
                freeDaysCount++;
                if (freeDaysCount >= nFreeDays) {
                    break;
                }
            }
        }

        return res;
    }

    public Flow_ServiceData getFlowByLink(Long nID_Service, Long nID_SubjectOrganDepartment) {
        FlowLink flow = flowLinkDao.findLinkByService(nID_Service, nID_SubjectOrganDepartment);
        return flow != null ? flow.getFlow_ServiceData() : null;
    }

    public FlowSlotTicket saveFlowSlotTicket(Long nID_FlowSlot, Long nID_Subject, Long nID_Task_Activiti)
            throws Exception {

        FlowSlotTicket oFlowSlotTicket = oFlowSlotTicketDao.findFlowSlotTicket(nID_FlowSlot);
        if (oFlowSlotTicket == null) {
            oFlowSlotTicket = new FlowSlotTicket();
        } else {
            //if(oFlowSlotTicket.getnID_Task_Activiti()!=null){
            if (FlowSlotVO.bBusyStatic(oFlowSlotTicket)) {
                //oFlowSlotTicket.getnID_Subject(nID_Subject);
                String sError = "FlowSlotTicket with nID_FlowSlot=" + nID_FlowSlot
                        + " is bBusyStatic by getnID_Task_Activiti()=" + oFlowSlotTicket.getnID_Task_Activiti();
                LOG.error(sError);
                throw new Exception(sError);
            } else if (FlowSlotVO
                    .bBusyTemp(oFlowSlotTicket)) {//oFlowSlotTicket.getsDateEdit(). <oFlowSlotTicket.getsDateEdit()
                //bBusyStatic
                LOG.info("(nID_Subject={})", nID_Subject);
                LOG.info("(getnID_Subject()={})", oFlowSlotTicket.getnID_Subject());
                if (!nID_Subject.equals(oFlowSlotTicket.getnID_Subject())) {
                    String sError =
                            "FlowSlotTicket with nID_FlowSlot=" + nID_FlowSlot + " is bBusyTemp from getsDateEdit()="
                                    + oFlowSlotTicket.getsDateEdit();
                    LOG.error(sError);
                    throw new Exception(sError);
                }
            }
        }

        //oFlowSlotTicket

        oFlowSlotTicket.setnID_Subject(nID_Subject);
        oFlowSlotTicket.setnID_Task_Activiti(nID_Task_Activiti);

        FlowSlot flowSlot = flowSlotDao.findByIdExpected(nID_FlowSlot);

        oFlowSlotTicket.setoFlowSlot(flowSlot);
        oFlowSlotTicket.setsDateStart(flowSlot.getsDate());

        Duration duration = ToolDuration.parseDuration(flowSlot.getsDuration());
        DateTime finishDateTime = flowSlot.getsDate().plusMinutes(duration.getMinutes());
        oFlowSlotTicket.setsDateFinish(finishDateTime);

        oFlowSlotTicket.setsDateEdit(DateTime.now());

        return oFlowSlotTicketDao.saveOrUpdate(oFlowSlotTicket);
    }

    /**
     * Generates FlowSlots in given interval for specified flow. Slots will not be generated if they are already exist.
     *
     * @param nID_Flow_ServiceData ID of flow
     * @param startDate            start date of generation (inclusive)
     * @param stopDate             stop date of generation (exclusive)
     * @return generated slots.
     */
    public List<FlowSlotVO> buildFlowSlots(Long nID_Flow_ServiceData, DateTime startDate, DateTime stopDate) {

        Flow_ServiceData flow = flowServiceDataDao.findByIdExpected(nID_Flow_ServiceData);

        List<FlowSlotVO> res = new ArrayList<>();

        for (FlowProperty flowProperty : flow.getFlowProperties()) {
            if (flowProperty.getbExclude() == null || !flowProperty.getbExclude()) {
                Class<FlowPropertyHandler> flowPropertyHandlerClass = getFlowPropertyHandlerClass(flowProperty);
                if (BaseFlowSlotScheduler.class.isAssignableFrom(flowPropertyHandlerClass)) {

                    BaseFlowSlotScheduler handler = getFlowPropertyHandlerInstance(
                            flowProperty.getoFlowPropertyClass().getsBeanName(), flowPropertyHandlerClass);
                    handler.setStartDate(startDate);
                    handler.setEndDate(stopDate);
                    handler.setFlow(flow);

                    LOG.info("(startDate={}, stopDate={}, flowProperty.getsData()={})",
                            startDate, stopDate, flowProperty.getsData());

                    if (flowProperty.getsData() != null && !"".equals(flowProperty.getsData().trim())) {
                        List<FlowSlot> generatedSlots = handler.generateObjects(flowProperty.getsData());
                        for (FlowSlot slot : generatedSlots) {
                            res.add(new FlowSlotVO(slot));
                        }
                    }
                }
            }
        }

        return res;
    }

    public ClearSlotsResult clearFlowSlots(Long nID_Flow_ServiceData, DateTime startDate, DateTime stopDate,
            boolean bWithTickets) {

        List<FlowSlot> flowSlots = flowSlotDao.findFlowSlotsByFlow(nID_Flow_ServiceData, startDate, stopDate);
        DateTime operationTime = DateTime.now();

        ClearSlotsResult res = new ClearSlotsResult();
        List<FlowSlot> flowSlotsToDelete = new ArrayList<>();
        for (FlowSlot slot : flowSlots) {
            if (bWithTickets || slot.getFlowSlotTickets().isEmpty()) {
                flowSlotsToDelete.add(slot);

                // detach existing tickets from slots
                for (FlowSlotTicket oFlowSlotTicket : slot.getFlowSlotTickets()) {
                    oFlowSlotTicket.setoFlowSlot(null);
                    oFlowSlotTicket.setsDateEdit(operationTime);
                }
                res.getaDeletedSlot().add(new FlowSlotVO(slot));
            }

            if (!slot.getFlowSlotTickets().isEmpty()) {
                res.getaSlotWithTickets().add(new FlowSlotVO(slot));
            }
        }

        flowSlotDao.delete(flowSlotsToDelete);
        return res;
    }

    private Class<FlowPropertyHandler> getFlowPropertyHandlerClass(FlowProperty flowProperty) {
        String fullClassName = flowProperty.getoFlowPropertyClass().getsPath();
        try {
            return (Class<FlowPropertyHandler>) Class.forName(fullClassName);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("Can't find class of controller: " + fullClassName, e);
        }
    }

    private <T extends FlowPropertyHandler> T getFlowPropertyHandlerInstance(String beanName,
            Class flowPropertyHandlerClass) {
        Object bean = null;
        if (beanName != null) {
            bean = applicationContext.getBean(beanName);
        } else {
            bean = applicationContext.getBean(flowPropertyHandlerClass);
        }
        Assert.isTrue(flowPropertyHandlerClass.isAssignableFrom(flowPropertyHandlerClass));
        return (T) bean;
    }
    

    
    
    public void addFlowSlowTicketToResult(List<Map<String, String>> res,
            SimpleDateFormat dateFormat, FlowSlotTicket currFlowSlowTicket,
            Task tasksByActivitiID) {
        Map<String, String> currRes = new HashMap<String, String>();

        StringBuilder sb = new StringBuilder();
        sb.append("Adding flow slot ticket: ");
        sb.append(currFlowSlowTicket.getId());
        sb.append(":");
        sb.append(currFlowSlowTicket.getnID_Subject());
        sb.append(":");
        sb.append(currFlowSlowTicket.getsDateStart());
        sb.append(":");
        sb.append(currFlowSlowTicket.getsDateFinish());
        LOG.info("{}", sb.toString());

        currRes.put("nID", currFlowSlowTicket.getId().toString());
        currRes.put("nID_FlowSlot", currFlowSlowTicket.getoFlowSlot() != null ?
                currFlowSlowTicket.getoFlowSlot().getId().toString() : "");
        currRes.put("nID_Subject", currFlowSlowTicket.getnID_Subject().toString());
        Date startDate = new Date(currFlowSlowTicket.getsDateStart().getMillis());
        currRes.put("sDateStart", dateFormat.format(startDate));
        Date finishDate = new Date(currFlowSlowTicket.getsDateFinish().getMillis());
        currRes.put("sDateFinish", dateFormat.format(finishDate));
        Date editDate = new Date(currFlowSlowTicket.getsDateEdit().getMillis());
        currRes.put("sDateEdit", dateFormat.format(editDate));

        currRes.put("nID_Task_Activiti", tasksByActivitiID.getId());

        currRes.put("name", tasksByActivitiID.getName());
        currRes.put("id", tasksByActivitiID.getId());
        currRes.put("assignee", tasksByActivitiID.getAssignee());
        currRes.put("nID_Instance", tasksByActivitiID.getProcessInstanceId());

        currRes.put("sUserTaskName", tasksByActivitiID.getName());
        ProcessDefinition processDefinition = repositoryService.createProcessDefinitionQuery()
                .processDefinitionId(tasksByActivitiID.getProcessDefinitionId()).singleResult();
        currRes.put("sNameBP", processDefinition != null ? processDefinition.getName() : "");
        currRes.put("sTaskDate", dateFormat.format(tasksByActivitiID.getCreateTime()));
        res.add(currRes);
    }

    public List<FlowProperty> getFilteredFlowPropertiesForFlowServiceData(Long nID_Flow_ServiceData,
            String sID_BP,
            Long nID_SubjectOrganDepartment,
            Boolean bExclude) throws Exception {

        if (nID_Flow_ServiceData == null) {
            if (sID_BP != null) {
                nID_Flow_ServiceData = flowServiceDataDao.findFlowId(sID_BP, nID_SubjectOrganDepartment);
                LOG.info("(sID_BP={},nID_Flow_ServiceData={})",sID_BP,nID_Flow_ServiceData);
            } else {
                String sError = "nID_Flow_ServiceData==null and sID_BP==null";
                LOG.error(sError);
                throw new Exception(sError);
            }
        }
        if (nID_Flow_ServiceData == null) {
            String sError = "nID_Flow_ServiceData==null";
            LOG.error(sError);
            throw new Exception(sError);
        }

        LOG.info("(nID_Flow_ServiceData={})", nID_Flow_ServiceData);
        Flow_ServiceData flowServiceData = flowServiceDataDao.findByIdExpected(nID_Flow_ServiceData);
        List<FlowProperty> res = new LinkedList<FlowProperty>();
        if (flowServiceData != null) {
            if (flowServiceData.getFlowProperties() != null && !flowServiceData.getFlowProperties().isEmpty()) {
                LOG.info("nID_Flow_ServiceData contains " + flowServiceData.getFlowProperties().size()
                        + " elements. Getting only with bExclude =" + bExclude.toString());
                for (FlowProperty flowProperty : flowServiceData.getFlowProperties()) {
                    LOG.info("flowProperty {}:{}", flowProperty.getId(), flowProperty.getsName(), flowProperty
                            .getbExclude());
                    if (flowProperty.getbExclude() != null && flowProperty.getbExclude().equals(bExclude)) {
                        res.add(flowProperty);
                    }
                }
                LOG.info("Found {} flow properties with bExclude={}. Results:{}",
                        res.size(), bExclude, res.toString());
            } else {
                LOG.info("Flow service data contains empty list of FlowProperty");
            }
        } else {
            LOG.info("Have not found nID_Flow_ServiceData object with (ID={}) ", nID_Flow_ServiceData);
        }
        return res;
    }

    public FlowProperty fillFlowProperty(String sName, String sRegionTime,
            String saRegionWeekDay, String sDateTimeAt, String sDateTimeTo,
            Integer nLen,
            String sLenType,
            String sData,
            FlowProperty flowProperty) {
        flowProperty.setbExclude(false);
        flowProperty.setsName(sName);
        flowProperty.setsRegionTime(sRegionTime);
        flowProperty.setSaRegionWeekDay(saRegionWeekDay);
        if (nLen != null) {
            flowProperty.setLen(nLen);
        }
        if (sLenType != null) {
            flowProperty.setLenType(sLenType);
        }
        if (sData != null) {
            flowProperty.setsData(sData);
        }
        flowProperty.setsDateTimeAt(sDateTimeAt);
        flowProperty.setsDateTimeTo(sDateTimeTo);
        return flowProperty;
    }

    /**
     * Парсинг JSON-строки, содержащей информацию о дате и времени
     * @param sDate - дата в формате "2015-06-28 12:12:56.001"
     * @return - объект Joda DateTime
     */
    public DateTime parseJsonDateTimeSerializer(String sDate){
        DateTime oDate = null;
        if (sDate != null) {
            oDate = JsonDateTimeSerializer.DATETIME_FORMATTER.parseDateTime(sDate);
        }
        return oDate;
    }

    /**
     * Проверить nID_Flow_ServiceData, и если (nID_Flow_ServiceData == null) - попытаемся его определить
     * @param nID_Flow_ServiceData - номер-ИД потока
     * @param sID_BP - строка-ИД бизнес-процесса потока
     * @param nID_SubjectOrganDepartment - номер-ИН департамента
     * @return nID_Flow_ServiceData - номер-ИД потока
     * @throws RecordNotFoundException если <br> nID_Flow_ServiceData==null and sID_BP==null <br> или определить номер-ИД потока по заданному  sID_BP - не удалось
     */
    public Long determineFlowServiceDataID(Long nID_Flow_ServiceData, String sID_BP, Long nID_SubjectOrganDepartment) throws RecordNotFoundException {
        if (nID_Flow_ServiceData == null){
            if (sID_BP != null){
                nID_Flow_ServiceData = flowServiceDataDao.findFlowId(sID_BP, nID_SubjectOrganDepartment);
            } else {
                throw new RecordNotFoundException("nID_Flow_ServiceData==null and sID_BP==null");
            }
        }
        if (nID_Flow_ServiceData == null){
            throw new RecordNotFoundException("nID_Flow_ServiceData==null");
        }
        LOG.info("(sID_BP={}, nID_Flow_ServiceData={})", sID_BP, nID_Flow_ServiceData);
        return nID_Flow_ServiceData;
    }

    /**
     * Добавление/изменение расписания
     *
     * @param nID - ИД-номер, если задан - редактирование
     * @param nID_Flow_ServiceData - номер-ИД потока (обязательный если нет sID_BP)
     * @param sID_BP - строка-ИД бизнес-процесса потока (обязательный если нет nID_Flow_ServiceData)
     * @param nID_SubjectOrganDepartment - номер-ИН департамента
     * @param sName - Строка-название ("Вечерний прием")
     * @param sRegionTime - Строка период времени ("14:16-16-30")
     * @param nLen - Число, определяющее длительность слота
     * @param sLenType - Строка определяющая тип длительности слота
     * @param sData - Строка с данными(выражением), описывающими формулу расписания (например: {"0 0/30 9-12 ? * TUE-FRI":"PT30M"})
     * @param saRegionWeekDay - Массив дней недели ("su,mo,tu")
     * @param sDateTimeAt - Строка-дата начала(на) в формате YYYY-MM-DD hh:mm:ss ("2015-07-31 19:00:00")
     * @param sDateTimeTo - Строка-дата конца(к) в формате YYYY-MM-DD hh:mm:ss ("2015-07-31 23:00:00")
     * @param bExclude - <b>true</b> для оаботы с расписаниями исключений; <b>false</b> для работы с расписаниями включений
     * @return ID of new FlowProperty
     * @throws Exception в случае невозможности определить номер-ИД потока
     */
    public FlowProperty setSheduleFlow(
            Long nID,
            Long nID_Flow_ServiceData,
            String sID_BP,
            Long nID_SubjectOrganDepartment,
            String sName,
            String sRegionTime,
            Integer nLen,
            String sLenType,
            String sData,
            String saRegionWeekDay,
            String sDateTimeAt,
            String sDateTimeTo,
            Boolean bExclude) throws Exception {

        FlowProperty flowProperty = null;
        if (nID != null){
            LOG.info("nID is not null. Updating existing FLowProperty with parameters");
            flowProperty = flowPropertyDao.findByIdExpected(nID);
            if (flowProperty != null){
                flowProperty = fillFlowProperty(sName, sRegionTime, saRegionWeekDay,
                        sDateTimeAt, sDateTimeTo, nLen, sLenType, sData, flowProperty);
                flowProperty.setbExclude(bExclude);
                flowPropertyDao.saveOrUpdate(flowProperty);
                LOG.info("nID is not null. Updating existing FLowProperty with parameters");
            } else {
                LOG.info("Have not found FlowProperty object with ID:(nID={})", nID);
            }
        } else {
            try {
                nID_Flow_ServiceData = determineFlowServiceDataID(nID_Flow_ServiceData, sID_BP, nID_SubjectOrganDepartment);
            } catch (RecordNotFoundException e) {
                LOG.error(e.getMessage());                
                throw new Exception(e.getMessage());
            }
            LOG.info("Creating new flow property for the flow with ID: {}", nID_Flow_ServiceData);
            flowProperty = new FlowProperty();

            FlowPropertyClass flowPropertyClass = flowPropertyClassDao.findByIdExpected(DEFAULT_FLOW_PROPERTY_CLASS);
            LOG.info("Loaded flow propetry service class: {}", flowPropertyClass);
            Flow_ServiceData flowServiceData = flowServiceDataDao.findByIdExpected(nID_Flow_ServiceData);
            LOG.info("Loaded flow service data class: ", flowServiceData);

            flowProperty = fillFlowProperty(sName, sRegionTime, saRegionWeekDay, sDateTimeAt, sDateTimeTo, nLen,
                    sLenType, sData, flowProperty);
            flowProperty.setoFlowPropertyClass(flowPropertyClass);
            flowProperty.setbExclude(bExclude);
            flowProperty.setoFlow_ServiceData(flowServiceData);

            flowServiceData.getFlowProperties().add(flowProperty);

            flowServiceDataDao.saveOrUpdate(flowServiceData);
            LOG.info("Successfully updated flow with new FlowProperty.");
        }
        return flowProperty;
    }

    /**
     * Удаление расписания
     *
     * @param nID - ИД-номер
     * @param nID_Flow_ServiceData - номер-ИД потока
     * @param bExclude - <b>true</b> для оаботы с расписаниями исключений; <b>false</b> для работы с расписаниями включений
     * @return Массив объектов сущности расписаний
     */
    public List<FlowProperty> removeSheduleFlow(Long nID, Long nID_Flow_ServiceData, Boolean bExclude){

        Flow_ServiceData flowServiceData = flowServiceDataDao.findByIdExpected(nID_Flow_ServiceData);

        Iterator<FlowProperty> iterator = flowServiceData.getFlowProperties().iterator();
        while (iterator.hasNext()) {
            FlowProperty curr = iterator.next();
            LOG.info("Processing flow property with ID={} and bexclude={}", nID, curr.getbExclude());

            if (curr.getId().equals(nID) && curr.getbExclude() != null && curr.getbExclude()
                    .equals(bExclude.booleanValue())) {
                iterator.remove();
                flowPropertyDao.delete(curr.getId());

                LOG.info("Removed flow property with ID={} and bexclude=true", nID);
                break;
            }
        }

        LOG.info("Updated flow data. Removed FlowProperty schedules with bExlclude=true. Returning list without removed item:{}",
                flowServiceData.getFlowProperties().size());

        return flowServiceData.getFlowProperties();
    }

    /**
     * Получение активных тикетов
     *
     * @param sLogin имя пользователя для которого необходимо вернуть тикеты
     * @param bEmployeeUnassigned опциональный параметр (false по умолчанию). Если true - возвращать тикеты не заассайненые на пользователей
     * @param sDate опциональный параметр в формате yyyy-MM-dd. Дата за которую выбирать тикеты. При выборке проверяется startDate тикета (без учета времени. только дата). Если день такой же как и у указанное даты - такой тикет добавляется в результат.
     * @return возвращает активные тикеты, отсортированные по startDate
     * @throws ParseException
     */
    public List<Map<String, String>> getFlowSlotTickets(String sLogin, Boolean bEmployeeUnassigned, String sDate)
            throws ParseException {
        List<Map<String, String>> res = new LinkedList<Map<String, String>>();

        List<Task> tasks = actionTaskService.getTasksForChecking(sLogin, bEmployeeUnassigned);

        Map<Long, Task> taskActivityIDsMap = new HashMap<Long, Task>();
        for (Task task : tasks) {
            if (task.getProcessInstanceId() != null) {
                taskActivityIDsMap.put(Long.valueOf(task.getProcessInstanceId()), task);
            } else {
                LOG.info("Task with ID:{} has null process instance id value", task.getId());
            }
        }

        LOG.info("Will check tasks which belong to process definition IDs:{}", taskActivityIDsMap.keySet());

        List<FlowSlotTicket> allFlowSlowTickets = oFlowSlotTicketDao.findAll();
        LOG.info("Found {} flow slot tickets.", (allFlowSlowTickets != null ? allFlowSlowTickets.size() : 0));
        if (allFlowSlowTickets != null) {

            Collections.sort(allFlowSlowTickets, new Comparator<FlowSlotTicket>() {
                @Override
                public int compare(FlowSlotTicket ticket1, FlowSlotTicket ticket2) {
                    return ticket1.getsDateStart().compareTo(ticket2.getsDateStart());
                }
            });

            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");

            Date dateOfTasks = null;
            if (sDate != null) {
                LOG.info("Checking for flow spot tickets for the date:(sDate={}) ", sDate);
                dateOfTasks = new SimpleDateFormat("yyyy-MM-dd").parse(sDate);
            }
            for (FlowSlotTicket currFlowSlotTicket : allFlowSlowTickets) {
                if (taskActivityIDsMap.keySet().contains(currFlowSlotTicket.getnID_Task_Activiti())) {
                    Task tasksByActivitiID = taskActivityIDsMap.get(currFlowSlotTicket.getnID_Task_Activiti());

                    if (dateOfTasks != null) {
                        LOG.info("Comparing two dates:{} and {}", currFlowSlotTicket.getsDateStart().toDate(), dateOfTasks);
                    }
                    if (dateOfTasks == null || (DateUtils
                            .isSameDay(currFlowSlotTicket.getsDateStart().toDate(), dateOfTasks))) {
                        addFlowSlowTicketToResult(res, dateFormat, currFlowSlotTicket, tasksByActivitiID);
                    } else {
                        LOG.info("Skipping flowSlot {} for task:{} as they have not valid  start-end date {}:{}",
                                currFlowSlotTicket.getId(), currFlowSlotTicket.getnID_Task_Activiti(),
                                currFlowSlotTicket.getsDateStart().toString(), currFlowSlotTicket.getsDateFinish());
                    }
                }
            }
        }
        return res;
    }

    /**
     * Получение массива объектов SubjectOrganDepartment по ID бизнес процесса
     * @param sID_BP имя Activiti BP
     * @return
     */
    public SubjectOrganDepartment[] getSubjectOrganDepartments(String sID_BP) {
        List<Flow_ServiceData> serviceDataList = flowServiceDataDao.findAllBy("sID_BP", sID_BP);
        SubjectOrganDepartment[] result = new SubjectOrganDepartment[serviceDataList.size()];
        for (int i = 0; i < serviceDataList.size(); i++) {
            Flow_ServiceData sd = serviceDataList.get(i);
            Long nID_SubjectOrganDepartment = sd.getnID_SubjectOrganDepartment();
            SubjectOrganDepartment subjectOrganDepartment = subjectOrganDepartmentDao
                    .findByIdExpected(nID_SubjectOrganDepartment);
            result[i] = subjectOrganDepartment;
        }
        return result;
    }
}
