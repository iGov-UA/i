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
import java.util.Map.Entry;
import org.activiti.engine.FormService;
import org.activiti.engine.form.FormProperty;
import org.igov.service.business.action.task.form.QueueDataFormType;

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
    private GenericEntityDao<Long, FlowProperty> flowPropertyDao;

    @Autowired
    @Qualifier("flowPropertyClassDao")
    private GenericEntityDao<Long, FlowPropertyClass> flowPropertyClassDao;

    @Autowired
    @Qualifier("subjectOrganDepartmentDao")
    private GenericEntityDao<Long, SubjectOrganDepartment> subjectOrganDepartmentDao;

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

    @Autowired
    private FormService oFormService;
    
    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    public Days getFlowSlots(Long nID_Service, Long nID_ServiceData, String sID_BP, Long nID_SubjectOrganDepartment,
                             DateTime startDate, DateTime endDate, boolean bAll,
                             int nFreeDays, int nSlots) {

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

        List<List<FlowSlot>> slotsGroups = splitOnGroups(aFlowSlot, nSlots);

        A: for (List<FlowSlot> group : slotsGroups) {
            FlowSlotVO fistInGroup = null;

            if (group.size() != nSlots) {
                continue;
            }

            for (FlowSlot flowSlot : group) {
                FlowSlotVO flowSlotVO = new FlowSlotVO(flowSlot);
                if (fistInGroup == null) {
                    fistInGroup = flowSlotVO;
                }

                if (!flowSlotVO.isbFree()) {
                    if (!bAll) {
                        continue A;
                    }

                    fistInGroup.setbFree(false);
                }

            }

            DateTime currDate = group.get(0).getsDate().withTimeAtStartOfDay();

            Day day = daysMap.get(currDate);
            if (day == null) {
                day = new Day(currDate);
                daysMap.put(currDate, day);
            }

            day.getaSlot().add(fistInGroup);

            if (!day.isbHasFree() && fistInGroup.isbFree()) {
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

    private List<List<FlowSlot>> splitOnGroups(List<FlowSlot> flowSlots, int slotsInGroup) {
        List<List<FlowSlot>> res = new ArrayList<>();

        List<FlowSlot> currGroup = new ArrayList<>();
        DateTime nextStart = null;
        for (FlowSlot slot : flowSlots) {
            if (currGroup.size() == slotsInGroup || (nextStart != null && !slot.getsDate().isEqual(nextStart))) {
                res.add(currGroup);
                currGroup = new ArrayList<>();
            }

            currGroup.add(slot);
            nextStart = slot.getsDate().plusMinutes(ToolDuration.parseDuration(slot.getsDuration()).getMinutes());
        }
        res.add(currGroup);

        return res;
    }

    public Flow_ServiceData getFlowByLink(Long nID_Service, Long nID_SubjectOrganDepartment) {
        FlowLink flow = flowLinkDao.findLinkByService(nID_Service, nID_SubjectOrganDepartment);
        return flow != null ? flow.getFlow_ServiceData() : null;
    }

    public FlowSlotTicket saveFlowSlotTicket(Long first_nID_FlowSlot, Long nID_Subject,
                                             Long nID_Task_Activiti, Integer nSlots)
            throws Exception {

        FlowSlotTicket res = null;
        LOG.info("(first_nID_FlowSlot={}, nSlots={})", first_nID_FlowSlot, nSlots);
        List<FlowSlot> flowSlots = flowSlotDao.findFlowSlotsChain(first_nID_FlowSlot, nSlots);
        LOG.info("(flowSlots.size={})", flowSlots.size());
        if (flowSlots.size() != nSlots) {
            String sError = "Slots chain absent";
            LOG.error(sError);
            throw new Exception(sError);
        }

        FlowSlotTicket oFlowSlotTicket = oFlowSlotTicketDao.findFlowSlotTicket(first_nID_FlowSlot);
        if (oFlowSlotTicket == null) {
            LOG.info("oFlowSlotTicket is null!");
            oFlowSlotTicket = new FlowSlotTicket();
        } else {
            LOG.info("(oFlowSlotTicket_ID={})", oFlowSlotTicket.getId());
            if (FlowSlotVO.bBusyStatic(oFlowSlotTicket)) {
                LOG.info("(nID_Subject={} oFlowSlotTicket.getnID_Subject={} is bBusyStatic)", 
                        nID_Subject, oFlowSlotTicket.getnID_Subject());
                String sError = "FlowSlotTicket with nID_FlowSlot=" + first_nID_FlowSlot
                        + " is bBusyStatic by getnID_Task_Activiti()=" + oFlowSlotTicket.getnID_Task_Activiti();
                LOG.error(sError);
                throw new Exception(sError);
            } else if (FlowSlotVO.bBusyTemp(oFlowSlotTicket)) {
                LOG.info("(nID_Subject={} oFlowSlotTicket.getnID_Subject={} is bBusyTemp)", 
                        nID_Subject, oFlowSlotTicket.getnID_Subject());
                if (!nID_Subject.equals(oFlowSlotTicket.getnID_Subject())) {
                    String sError =
                            "FlowSlotTicket with nID_FlowSlot=" + first_nID_FlowSlot + " is bBusyTemp from getsDateEdit()="
                                    + oFlowSlotTicket.getsDateEdit();
                    LOG.error(sError);
                    throw new Exception(sError);
                }
            } else{
                LOG.info("(delete oFlowSlotTicket={}...)", oFlowSlotTicket.getId());
                FlowSlotTicket flowSlotTicket = oFlowSlotTicketDao.findByIdExpected(oFlowSlotTicket.getId());
                oFlowSlotTicketDao.delete(flowSlotTicket);
                LOG.info("(delete oFlowSlotTicket={} ok!)", oFlowSlotTicket.getId());
                oFlowSlotTicket = new FlowSlotTicket();
            }
        }
        
        oFlowSlotTicket.setnID_Subject(nID_Subject);
        oFlowSlotTicket.setnID_Task_Activiti(nID_Task_Activiti);

        oFlowSlotTicket.getaFlowSlot().addAll(flowSlots);

        oFlowSlotTicket.setsDateStart(flowSlots.get(0).getsDate());

        DateTime endDate = oFlowSlotTicket.getsDateStart();
        for (FlowSlot s : flowSlots) {
            Duration duration = ToolDuration.parseDuration(s.getsDuration());
            endDate.plusMinutes(duration.getMinutes());
        }
        oFlowSlotTicket.setsDateFinish(endDate);

        oFlowSlotTicket.setsDateEdit(DateTime.now());
        LOG.info("(saveOrUpdate oFlowSlotTicket={}...)", oFlowSlotTicket.getId());
        oFlowSlotTicketDao.saveOrUpdate(oFlowSlotTicket);
        LOG.info("(saveOrUpdate oFlowSlotTicket={} ok!)", oFlowSlotTicket.getId());
        return oFlowSlotTicket;
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
                    oFlowSlotTicket.getaFlowSlot().clear();
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
        currRes.put("nID_FlowSlot", !currFlowSlowTicket.getaFlowSlot().isEmpty() ?
                currFlowSlowTicket.getaFlowSlot().get(0).getId().toString() : "");
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
    
    public void addFlowSlowTicketToResult_DMS(List<Map<String, String>> res,
            SimpleDateFormat dateFormat, String sTicket_Number, String sTicket_Code, String sDate, //FlowSlotTicket currFlowSlowTicket
            Task tasksByActivitiID) {
        Map<String, String> currRes = new HashMap<String, String>();

        /*StringBuilder sb = new StringBuilder();
        sb.append("Adding flow slot ticket: ");
        sb.append(currFlowSlowTicket.getId());
        sb.append(":");
        sb.append(currFlowSlowTicket.getnID_Subject());
        sb.append(":");
        sb.append(currFlowSlowTicket.getsDateStart());
        sb.append(":");
        sb.append(currFlowSlowTicket.getsDateFinish());
        LOG.info("{}", sb.toString());*/

        currRes.put("nID", sTicket_Number);
        currRes.put("nID_FlowSlot", sTicket_Code);
        currRes.put("nID_Subject", "0");
        /*Date oDateStart = new Date(currFlowSlowTicket.getsDateStart().getMillis());
        currRes.put("sDateStart", dateFormat.format(oDateStart));
        Date oDateFinish = new Date(currFlowSlowTicket.getsDateFinish().getMillis());
        currRes.put("sDateFinish", dateFormat.format(oDateFinish));
        Date oDateEdit = new Date(currFlowSlowTicket.getsDateEdit().getMillis());
        currRes.put("sDateEdit", dateFormat.format(oDateEdit));*/
        currRes.put("sDateStart", sDate);
        currRes.put("sDateFinish", sDate);
        currRes.put("sDateEdit", sDate);
        
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
     * @param sDateSelect опциональный параметр в формате yyyy-MM-dd. Дата за которую выбирать тикеты. При выборке проверяется startDate тикета (без учета времени. только дата). Если день такой же как и у указанное даты - такой тикет добавляется в результат.
     * @return возвращает активные тикеты, отсортированные по startDate
     * @throws ParseException
     */
    public List<Map<String, String>> getFlowSlotTickets(String sLogin, Boolean bEmployeeUnassigned, String sDateSelect)
            throws ParseException {
        List<Map<String, String>> mReturn = new LinkedList<>();
        SimpleDateFormat oDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");

        List<Task> aTask = actionTaskService.getTasksForChecking(sLogin, bEmployeeUnassigned);

        Map<Long, Task> mTask = new HashMap<>();
        //Map<Long, List<FormProperty>> mTaskProperty = new HashMap<>();
        //Map<Long, List<FormProperty>> mTaskProperty_DMS = new HashMap<>();
        //Map<Long, Task> mTask = new HashMap<Long, Task>();
        for (Task oTask : aTask) {
            if (oTask.getProcessInstanceId() != null) {
                //mTask.put(Long.valueOf(oTask.getProcessInstanceId()), oTask);
                Boolean bQueue = false;
                Boolean bQueueDMS = false;
                List<FormProperty> aProperty = oFormService.getTaskFormData(oTask.getId()).getFormProperties();
                for (FormProperty oProperty : aProperty) {
                    if(oProperty.getType() instanceof QueueDataFormType){
                        bQueue=true;
                        String sValue = oProperty.getValue();
                        if(sValue!=null && !"".equals(sValue.trim()) && !"null".equals(sValue.trim())){
                            bQueueDMS=true;
                            LOG.info("sValue is present, so queue is filled");
                            //long nID_FlowSlotTicket = 0;
                            Map<String, Object> m = QueueDataFormType.parseQueueData(sValue);
                            String sDate = (String) m.get(QueueDataFormType.sDate);
                            LOG.info("(sDate={})", sDate);
                            String sID_Type = QueueDataFormType.get_sID_Type(m);
                            LOG.info("(sID_Type={})", sID_Type);
                            if("DMS".equals(sID_Type)){//Нет ни какой обработки т.к. это внешняя ЭО
                                Long nID_ServiceCustomPrivate = (Long) m.get("nID_ServiceCustomPrivate");
                                LOG.info("(nID_ServiceCustomPrivate={})", nID_ServiceCustomPrivate);
                                String sTicket_Number = (String) m.get("ticket_number");
                                LOG.info("(sTicket_Number={})", sTicket_Number);
                                String sTicket_Code = (String) m.get("ticket_code");
                                LOG.info("(sTicket_Code={})", sTicket_Code);
                            //}else if("iGov".equals(sID_Type)){
                                addFlowSlowTicketToResult_DMS(mReturn, oDateFormat, sTicket_Number, sTicket_Code, sDate, oTask);
                            }
                        }
                    }
                }
                if(bQueue&&!bQueueDMS){
                    Long nID_Task=Long.valueOf(oTask.getProcessInstanceId());
                    mTask.put(nID_Task, oTask);
                    ///mTaskProperty.put(nID_Task, aProperty);
                }
                //oTask.
        /*List<FormProperty> a = oFormService.getTaskFormData(nID_Task.toString()).getFormProperties();
        List<Map<String,Object>> aReturn = new LinkedList();
        Map<String,Object> mReturn;
        //a.get(1).getType().getInformation()
        for (FormProperty oProperty : a) {*/
                
                
                /*for (Entry<String,Object> oProperty : oTask.getTaskLocalVariables().entrySet()) {
                    //oProperty.getValue()
                }*/
                
            } else {
                LOG.info("Task with ID:{} has null process instance id value", oTask.getId());
            }
        }
        LOG.info("Will check tasks which belong to process definition IDs:{}", mTask.keySet());

        if(mTask.isEmpty()){
            LOG.info("mTask.isEmpty()");
        }else{
            List<FlowSlotTicket> aFlowSlowTicket = oFlowSlotTicketDao.findAll();
            LOG.info("Found {} flow slot tickets.", (aFlowSlowTicket != null ? aFlowSlowTicket.size() : 0));
            if (aFlowSlowTicket != null) {
                Collections.sort(aFlowSlowTicket, new Comparator<FlowSlotTicket>() {
                    @Override
                    public int compare(FlowSlotTicket ticket1, FlowSlotTicket ticket2) {
                        return ticket1.getsDateStart().compareTo(ticket2.getsDateStart());
                    }
                });

                Date oDateSelect = null;
                if (sDateSelect != null) {
                    LOG.info("Checking for flow spot tickets for the date:(sDate={}) ", sDateSelect);
                    oDateSelect = new SimpleDateFormat("yyyy-MM-dd").parse(sDateSelect);
                }

                for (FlowSlotTicket oFlowSlotTicket : aFlowSlowTicket) {
                    if (mTask.keySet().contains(oFlowSlotTicket.getnID_Task_Activiti())) {
                        Task oTask = mTask.get(oFlowSlotTicket.getnID_Task_Activiti());

                        if (oDateSelect != null) {
                            LOG.info("Comparing two dates:{} and {}", oFlowSlotTicket.getsDateStart().toDate(), oDateSelect);
                        }
                        if (oDateSelect == null || (DateUtils.isSameDay(oFlowSlotTicket.getsDateStart().toDate(), oDateSelect))) {
                            addFlowSlowTicketToResult(mReturn, oDateFormat, oFlowSlotTicket, oTask);
                        } else {
                            LOG.info("Skipping flowSlot {} for task:{} as they have not valid  start-end date {}:{}",
                                    oFlowSlotTicket.getId(), oFlowSlotTicket.getnID_Task_Activiti(),
                                    oFlowSlotTicket.getsDateStart().toString(), oFlowSlotTicket.getsDateFinish());
                        }
                    }
                }

                //mTaskProperty.put(nID_Task, aProperty);
            }            
        }
        return mReturn;
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
