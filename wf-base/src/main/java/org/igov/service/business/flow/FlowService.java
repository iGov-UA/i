package org.igov.service.business.flow;

import static java.lang.Math.toIntExact;
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
import org.activiti.engine.FormService;
import org.activiti.engine.form.FormProperty;
import org.igov.run.schedule.JobBuilderFlowSlots;
import org.igov.service.business.action.task.form.QueueDataFormType;
import org.igov.service.business.flow.handler.ExcludeDateRange;
import org.igov.util.JSON.JsonRestUtils;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.quartz.CronExpression;

/**
 * User: goodg_000 Date: 29.06.2015 Time: 18:11
 */
@Service
public class FlowService implements ApplicationContextAware {

    private static final Logger LOG = LoggerFactory.getLogger(FlowService.class);
    private static final Logger LOG_BIG = LoggerFactory.getLogger("FlowServiceBig");

    private static final long DEFAULT_FLOW_PROPERTY_CLASS = 1l;
    private static final String SUFFIX_AUTO = "auto";
    public static final int DAYS_IN_MONTH = 30;
    public static final int WORK_DAYS_NEEDED = 20;
    public static final int DAYS_IN_HALF_YEAR = 180;
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
    private FlowDao flowDao;

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
        Flow oFlow = null;
        if (nID_Service != null) {
            oFlow = getFlowByLink(nID_Service, nID_SubjectOrganDepartment);
        }
        if (oFlow != null) {
            aFlowSlot = flowSlotDao.findFlowSlotsByFlow(oFlow.getId(), startDate, endDate);
        } else if (nID_ServiceData != null) {
            aFlowSlot = flowSlotDao.findFlowSlotsByServiceData(nID_ServiceData, nID_SubjectOrganDepartment, startDate, endDate);
        } else if (sID_BP != null) {
            aFlowSlot = flowSlotDao.findFlowSlotsByBP(sID_BP, nID_SubjectOrganDepartment, startDate, endDate);
        } else {
            throw new IllegalArgumentException("nID_Service, nID_ServiceData, sID_BP are null!");
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

        A:
        for (List<FlowSlot> group : slotsGroups) {
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

    public Flow getFlowByLink(Long nID_Service, Long nID_SubjectOrganDepartment) {
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
                    String sError
                            = "FlowSlotTicket with nID_FlowSlot=" + first_nID_FlowSlot + " is bBusyTemp from getsDateEdit()="
                            + oFlowSlotTicket.getsDateEdit();
                    LOG.error(sError);
                    throw new Exception(sError);
                }
            } else {
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
     * Generates FlowSlots in given interval for specified flow. Slots will not
     * be generated if they are already exist.
     *
     * @param nID_Flow ID of flow
     * @param oDateStart start date of generation (inclusive)
     * @param oDateEnd stop date of generation (exclusive)
     * @return generated slots.
     */
    public List<FlowSlotVO> buildFlowSlots(Long nID_Flow, DateTime oDateStart, DateTime oDateEnd) throws ParseException {

        LOG.info("buildFlowSlots started nID_Flow = {}, oDateStart = {}, oDateEnd = {}", nID_Flow, oDateStart, oDateEnd);
        Flow oFlow = flowDao.findByIdExpected(nID_Flow);

        List<FlowSlotVO> result = new ArrayList<>();
        List<ExcludeDateRange> aoDateRange_Exclude = new ArrayList<>();
        CronExpression cronExpression = null;
        DateTimeFormatter format = DateTimeFormat.forPattern("dd.MM.yyyy HH:mm");
        SimpleDateFormat rangeformat = new SimpleDateFormat("dd.MM.yyyy");

        List<FlowProperty> aoFlowProperty = flowPropertyDao.findAllBy("sGroup", oFlow.getsGroup());
        LOG.info("nID_Flow = {}, aoFlowProperty = {}", nID_Flow, aoFlowProperty.size());
        for (FlowProperty oFlowProperty : aoFlowProperty) {
            if (oFlowProperty.getbExclude() && oFlowProperty.getsGroup() != null) {

                if ((oFlow.getsGroup() != null && oFlow.getsGroup().equals(oFlowProperty.getsGroup()))
                        || (oFlow.getnID_ServiceData().longValue() == oFlowProperty.getId().longValue())) {

                    List<DateTime> aCronExcludeRange = new ArrayList<>();
                    Map<String, String> configuration = JsonRestUtils.readObject(oFlowProperty.getsData(), Map.class);

                    for (Map.Entry<String, String> entry : configuration.entrySet()) {
                        DateTime currDateTime = new DateTime(format.parseDateTime(oFlowProperty.getsDateTimeAt()));
                        DateTime endDateTime = new DateTime(format.parseDateTime(oFlowProperty.getsDateTimeTo()));

                        String cronExpressionString = entry.getKey();

                        try {
                            cronExpression = new CronExpression(cronExpressionString);
                        } catch (ParseException e) {
                            LOG.info("There is no valid expression for cron parsing");
                        }

                        if (cronExpression != null) {
                            while (currDateTime.isBefore(endDateTime)) {

                                currDateTime = new DateTime(cronExpression.getNextValidTimeAfter(currDateTime.toDate()));

                                if (endDateTime.compareTo(currDateTime) <= 0) {
                                    break;
                                }

                                aCronExcludeRange.add(currDateTime);
                                LOG.info("currDateTime for exclude is : " + currDateTime.toString());
                            }
                        }
                    }

                    if (!aCronExcludeRange.isEmpty()) {

                        List<String> asPointDates = new ArrayList<>();

                        for (int i = 0; i < aCronExcludeRange.size(); i++) {
                            asPointDates.add(rangeformat.format(aCronExcludeRange.get(i).toDate()));
                        }

                        Set<String> asUniquePoindDates = new HashSet(asPointDates);
                        LOG.info("asUniquePoindDates: ", asUniquePoindDates);

                        Map<String, List<DateTime>> mDates = new HashMap<>();

                        for (Iterator<String> it = asUniquePoindDates.iterator(); it.hasNext();) {
                            String sUniqueDate = it.next();
                            List<DateTime> aDateRange = new ArrayList<>();
                            for (int i = 0; i < aCronExcludeRange.size(); i++) {
                                if (sUniqueDate.equals(rangeformat.format(aCronExcludeRange.get(i).toDate()))) {
                                    aDateRange.add(aCronExcludeRange.get(i));
                                }
                            }
                            mDates.put(sUniqueDate, aDateRange);
                        }

                        for (String sKey : mDates.keySet()) {
                            List<DateTime> aDateRange = mDates.get(sKey);
                            ExcludeDateRange oExcludeDateRange = new ExcludeDateRange();
                            oExcludeDateRange.setsDateTimeAt(aDateRange.get(0));
                            oExcludeDateRange.setsDateTimeTo(aDateRange.get(aDateRange.size() - 1));
                            aoDateRange_Exclude.add(oExcludeDateRange);

                            LOG.info("sKey cron exclude date: " + sKey);
                            LOG.info("start cron exclude date: " + oExcludeDateRange.getsDateTimeAt());
                            LOG.info("stop cron exclude date:: " + oExcludeDateRange.getsDateTimeTo());
                            LOG.info("---------------");
                        }
                    } else {
                        ExcludeDateRange oExcludeDateRange = new ExcludeDateRange();
                        oExcludeDateRange.setsDateTimeAt(format.parseDateTime(oFlowProperty.getsDateTimeAt()));
                        oExcludeDateRange.setsDateTimeTo(format.parseDateTime(oFlowProperty.getsDateTimeTo()));
                        aoDateRange_Exclude.add(oExcludeDateRange);
                    }

                    LOG.info("aDateRange_Exclude is: ", aoDateRange_Exclude);
                }
            }
        }
        LOG.info("nID_Flow = {}, aoFlowProperty = {} ok!!!", nID_Flow, aoFlowProperty.size());
        
        /*for (ExcludeDateRange oExcludeDateRange : aoDateRange_Exclude) {
            LOG.info("-----");
            LOG.info("start cron exclude date before apply: " + oExcludeDateRange.getsDateTimeAt());
            LOG.info("stop cron exclude date before apply: " + oExcludeDateRange.getsDateTimeTo());
            LOG.info("-----");
        }*/
        List<FlowProperty> aFlowProperty = oFlow.getFlowProperties();
        LOG.info("nID_Flow = {}, aoFlowProperty = {}", nID_Flow, aFlowProperty.size());
        for (FlowProperty oFlowProperty : aFlowProperty) {
            if (oFlowProperty.getbExclude() == null || !oFlowProperty.getbExclude()) {
                Class<FlowPropertyHandler> flowPropertyHandlerClass = getFlowPropertyHandlerClass(oFlowProperty);
                if (BaseFlowSlotScheduler.class.isAssignableFrom(flowPropertyHandlerClass)) {

                    BaseFlowSlotScheduler handler = getFlowPropertyHandlerInstance(
                            oFlowProperty.getoFlowPropertyClass().getsBeanName(), flowPropertyHandlerClass);

                    try {

                        if (oDateStart.isBefore(format.parseDateTime(oFlowProperty.getsDateTimeAt()))) //||startDate.isAfter(format.parseDateTime(flowProperty.getsDateTimeTo())))
                        {
                            oDateStart = format.parseDateTime(oFlowProperty.getsDateTimeAt());
                        }

                        if (oDateEnd.isAfter(format.parseDateTime(oFlowProperty.getsDateTimeTo()))) //||stopDate.isBefore(format.parseDateTime(flowProperty.getsDateTimeAt())))
                        {
                            oDateEnd = format.parseDateTime(oFlowProperty.getsDateTimeTo());
                        }

                        if (oDateEnd.isBefore(oDateStart)) {
                            continue;
                        }
                    } catch (IllegalArgumentException ex) {
                        LOG.info("Illegal date format in flowProperty");
                    }

                    handler.setStartDate(oDateStart);
                    handler.setEndDate(oDateEnd);
                    handler.setFlow(oFlow);
                    handler.setaDateRange_Exclude(aoDateRange_Exclude);
                    LOG.info("(startDate={}, stopDate={}, flowProperty.getsData()={})",
                            oDateStart, oDateEnd, oFlowProperty.getsData());

                    if (oFlowProperty.getsData() != null && !"".equals(oFlowProperty.getsData().trim())) {
                        List<FlowSlot> generatedSlots = handler.generateObjects(oFlowProperty.getsData());
                        /*List<FlowSlot> generatedSlots = handler.generateObjects(oFlowProperty.getsData());*/
                        for (FlowSlot slot : generatedSlots) {
                            result.add(new FlowSlotVO(slot));
                        }
                    }
                }
            }
        }
        LOG.info("nID_Flow = {}, aoFlowProperty = {} ok!!!", nID_Flow, aFlowProperty.size());
        
        LOG.info("nID_Flow = {}, aoDateRange_Exclude = {} ", nID_Flow, aoDateRange_Exclude.size());
        if (!aoDateRange_Exclude.isEmpty()) {
            List<FlowSlot> aFlowSlot = flowSlotDao.findFlowSlotsByFlow(nID_Flow, oDateStart, oDateEnd);
            List<FlowSlot> flowSlotsToDelete = new ArrayList<>();

            for (FlowSlot oFlowSlot : aFlowSlot) {

                Boolean bBusy = false;

                for (FlowSlotTicket oFlowSlotTicket : oFlowSlot.getFlowSlotTickets()) {
                    bBusy = bBusy || FlowSlotVO.bBusy(oFlowSlotTicket);
                }

                for (ExcludeDateRange oExcludeRange : aoDateRange_Exclude) {
                    if (((oFlowSlot.getsDate().isAfter(oExcludeRange.getsDateTimeAt())
                            && oFlowSlot.getsDate().isBefore(oExcludeRange.getsDateTimeTo()))
                            || oFlowSlot.getsDate().equals(oExcludeRange.getsDateTimeAt())
                            || oFlowSlot.getsDate().equals(oExcludeRange.getsDateTimeTo())) && (!bBusy)) {
                        LOG.info("oFlowSlot to delete id:" + oFlowSlot.getId());
                        LOG.info("oFlowSlot to delete name:" + oFlowSlot.getName());
                        LOG.info("oFlowSlot to delete date:" + oFlowSlot.getsDate());
                        flowSlotsToDelete.add(oFlowSlot);

                    }

                    if (((oFlowSlot.getsDate().isAfter(oExcludeRange.getsDateTimeAt())
                            && oFlowSlot.getsDate().isBefore(oExcludeRange.getsDateTimeTo()))
                            || oFlowSlot.getsDate().equals(oExcludeRange.getsDateTimeAt())
                            || oFlowSlot.getsDate().equals(oExcludeRange.getsDateTimeTo())) && (bBusy)) {
                        LOG.info("oFlowSlot to delete when it busy:" + oFlowSlot.getsDate());
                    }
                }
            }

            if (!flowSlotsToDelete.isEmpty()) {
                flowSlotDao.delete(flowSlotsToDelete);
            }
        }
        LOG.info("nID_Flow = {}, aoDateRange_Exclude = {} ok!!!", nID_Flow, aoDateRange_Exclude.size());
        return result;
    }

    public ClearSlotsResult clearFlowSlots(Long nID_Flow, DateTime startDate, DateTime stopDate,
            boolean bWithTickets) {
        LOG.info("clearFlowSlots started...");
        List<FlowSlot> aFlowSlot = flowSlotDao.findFlowSlotsByFlow(nID_Flow, startDate, stopDate);
        DateTime operationTime = DateTime.now();

        ClearSlotsResult res = new ClearSlotsResult();
        List<FlowSlot> flowSlotsToDelete = new ArrayList<>();
        for (FlowSlot oFlowSlot : aFlowSlot) {

            Boolean bBusy = false;
            for (FlowSlotTicket oFlowSlotTicket : oFlowSlot.getFlowSlotTickets()) {
                bBusy = bBusy || FlowSlotVO.bBusy(oFlowSlotTicket);
            }

            //if (bWithTickets || oFlowSlot.getFlowSlotTickets().isEmpty()) {
            if (bWithTickets || !bBusy) {
                flowSlotsToDelete.add(oFlowSlot);

                // detach existing tickets from slots
                for (FlowSlotTicket oFlowSlotTicket : oFlowSlot.getFlowSlotTickets()) {
                    oFlowSlotTicket.getaFlowSlot().clear();
                    oFlowSlotTicket.setsDateEdit(operationTime);
                }
                res.getaDeletedSlot().add(new FlowSlotVO(oFlowSlot));
            }

            //if (!oFlowSlot.getFlowSlotTickets().isEmpty()) {
            if (bBusy) {
                res.getaSlotWithTickets().add(new FlowSlotVO(oFlowSlot));
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

    public void addFlowSlowTicketToResult(List<Map<String, String>> amReturn, SimpleDateFormat oDateFormat, FlowSlotTicket oFlowSlotTicket, Task oTask) {
        Map<String, String> m = new HashMap<>();

        StringBuilder os = new StringBuilder();
        os.append("Adding flow slot ticket(oFlowSlotTicket): ");
        os.append("getId()=");
        os.append(oFlowSlotTicket.getId());
        os.append(",getnID_Subject()=");
        os.append(oFlowSlotTicket.getnID_Subject());
        os.append(",getsDateStart()=");
        os.append(oFlowSlotTicket.getsDateStart());
        os.append(",getsDateFinish()=");
        os.append(oFlowSlotTicket.getsDateFinish());
        LOG.info("{}", os.toString());

        m.put("nID", oFlowSlotTicket.getId().toString());
        m.put("nID_FlowSlot", !oFlowSlotTicket.getaFlowSlot().isEmpty()
                ? oFlowSlotTicket.getaFlowSlot().get(0).getId().toString() : "");
        m.put("nID_Subject", oFlowSlotTicket.getnID_Subject().toString());
        Date oDateStart = new Date(oFlowSlotTicket.getsDateStart().getMillis());
        m.put("sDateStart", oDateFormat.format(oDateStart));
        Date oDateFinish = new Date(oFlowSlotTicket.getsDateFinish().getMillis());
        m.put("sDateFinish", oDateFormat.format(oDateFinish));
        Date oDateEdit = new Date(oFlowSlotTicket.getsDateEdit().getMillis());
        m.put("sDateEdit", oDateFormat.format(oDateEdit));

        m.put("nID_Task_Activiti", oTask.getId());

        m.put("name", oTask.getName());
        m.put("id", oTask.getId());
        m.put("assignee", oTask.getAssignee());
        m.put("nID_Instance", oTask.getProcessInstanceId());

        m.put("sUserTaskName", oTask.getName());
        ProcessDefinition oProcessDefinition = repositoryService.createProcessDefinitionQuery()
                .processDefinitionId(oTask.getProcessDefinitionId()).singleResult();
        m.put("sNameBP", oProcessDefinition != null ? oProcessDefinition.getName() : "");
        m.put("sTaskDate", oDateFormat.format(oTask.getCreateTime()));
        amReturn.add(m);
    }

    public void addFlowSlowTicketToResult_DMS(List<Map<String, String>> amReturn, SimpleDateFormat oDateFormat, String sTicket_Number, String sTicket_Code, String sDate, Task oTask) {
        Map<String, String> m = new HashMap<>();

        StringBuilder os = new StringBuilder();
        os.append("Adding flow slot ticket: ");
        os.append("sTicket_Number=");
        os.append(sTicket_Number);
        os.append(",sTicket_Code=");
        os.append(sTicket_Code);
        os.append(",sDate=");
        os.append(sDate);
        LOG.info("{}", os.toString());

        m.put("nID", sTicket_Number);
        m.put("nID_FlowSlot", sTicket_Code);
        m.put("nID_Subject", "0");
        /*Date oDateStart = new Date(currFlowSlowTicket.getsDateStart().getMillis());
        currRes.put("sDateStart", dateFormat.format(oDateStart));
        Date oDateFinish = new Date(currFlowSlowTicket.getsDateFinish().getMillis());
        currRes.put("sDateFinish", dateFormat.format(oDateFinish));
        Date oDateEdit = new Date(currFlowSlowTicket.getsDateEdit().getMillis());
        currRes.put("sDateEdit", dateFormat.format(oDateEdit));*/
        m.put("sDateStart", sDate);
        m.put("sDateFinish", sDate);
        m.put("sDateEdit", sDate);

        m.put("nID_Task_Activiti", oTask.getId());

        m.put("name", oTask.getName());
        m.put("id", oTask.getId());
        m.put("assignee", oTask.getAssignee());
        m.put("nID_Instance", oTask.getProcessInstanceId());

        m.put("sUserTaskName", oTask.getName());
        ProcessDefinition processDefinition = repositoryService.createProcessDefinitionQuery()
                .processDefinitionId(oTask.getProcessDefinitionId()).singleResult();
        m.put("sNameBP", processDefinition != null ? processDefinition.getName() : "");
        m.put("sTaskDate", oDateFormat.format(oTask.getCreateTime()));
        amReturn.add(m);
    }

    public List<FlowProperty> getFilteredFlowPropertiesForFlow(Long nID_Flow,
            String sID_BP,
            Long nID_SubjectOrganDepartment,
            Boolean bExclude) throws Exception {

        if (nID_Flow == null) {
            if (sID_BP != null) {
                nID_Flow = flowDao.findFlowId(sID_BP, nID_SubjectOrganDepartment);
                LOG.info("(sID_BP={},nID_Flow={})", sID_BP, nID_Flow);
            } else {
                String sError = "nID_Flow==null and sID_BP==null";
                LOG.error(sError);
                throw new Exception(sError);
            }
        }
        if (nID_Flow == null) {
            String sError = "nID_Flow==null";
            LOG.error(sError);
            throw new Exception(sError);
        }

        LOG.info("(nID_Flow={})", nID_Flow);
        Flow flow = flowDao.findByIdExpected(nID_Flow);
        List<FlowProperty> res = new LinkedList<FlowProperty>();
        if (flow != null) {
            if (flow.getFlowProperties() != null && !flow.getFlowProperties().isEmpty()) {
                LOG.info("nID_Flow contains " + flow.getFlowProperties().size()
                        + " elements. Getting only with bExclude =" + bExclude.toString());
                for (FlowProperty flowProperty : flow.getFlowProperties()) {
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
            LOG.info("Have not found nID_Flow object with (ID={}) ", nID_Flow);
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
     *
     * @param sDate - дата в формате "2015-06-28 12:12:56.001"
     * @return - объект Joda DateTime
     */
    public DateTime parseJsonDateTimeSerializer(String sDate) {
        DateTime oDate = null;
        if (sDate != null) {
            oDate = JsonDateTimeSerializer.DATETIME_FORMATTER.parseDateTime(sDate);
        }
        return oDate;
    }

    /**
     * Проверить nID_Flow, и если (nID_Flow == null) -
 попытаемся его определить
     *
     * @param nID_Flow - номер-ИД потока
     * @param sID_BP - строка-ИД бизнес-процесса потока
     * @param nID_SubjectOrganDepartment - номер-ИН департамента
     * @return nID_Flow - номер-ИД потока
     * @throws RecordNotFoundException если <br> nID_Flow==null and
 sID_BP==null <br> или определить номер-ИД потока по заданному sID_BP - не
     * удалось
     */
    public Long determineFlow_ID(Long nID_Flow, String sID_BP, Long nID_SubjectOrganDepartment) throws RecordNotFoundException {
        if (nID_Flow == null) {
            if (sID_BP != null) {
                nID_Flow = flowDao.findFlowId(sID_BP, nID_SubjectOrganDepartment);
            } else {
                throw new RecordNotFoundException("nID_Flow==null and sID_BP==null");
            }
        }
        if (nID_Flow == null) {
            throw new RecordNotFoundException("nID_Flow==null");
        }
        LOG.info("(sID_BP={}, nID_Flow={})", sID_BP, nID_Flow);
        return nID_Flow;
    }

    /**
     * Добавление/изменение расписания
     *
     * @param nID - ИД-номер, если задан - редактирование
     * @param nID_Flow - номер-ИД потока (обязательный если нет
     * sID_BP)
     * @param sID_BP - строка-ИД бизнес-процесса потока (обязательный если нет
     * nID_Flow)
     * @param nID_SubjectOrganDepartment - номер-ИН департамента
     * @param sName - Строка-название ("Вечерний прием")
     * @param sRegionTime - Строка период времени ("14:16-16-30")
     * @param nLen - Число, определяющее длительность слота
     * @param sLenType - Строка определяющая тип длительности слота
     * @param sData - Строка с данными(выражением), описывающими формулу
     * расписания (например: {"0 0/30 9-12 ? * TUE-FRI":"PT30M"})
     * @param saRegionWeekDay - Массив дней недели ("su,mo,tu")
     * @param sDateTimeAt - Строка-дата начала(на) в формате YYYY-MM-DD hh:mm:ss
     * ("2015-07-31 19:00:00")
     * @param sDateTimeTo - Строка-дата конца(к) в формате YYYY-MM-DD hh:mm:ss
     * ("2015-07-31 23:00:00")
     * @param bExclude - <b>true</b> для оаботы с расписаниями исключений;
     * <b>false</b> для работы с расписаниями включений
     * @return ID of new FlowProperty
     * @throws Exception в случае невозможности определить номер-ИД потока
     */
    public FlowProperty setSheduleFlow(
            Long nID,
            Long nID_Flow,
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
        if (nID != null) {
            LOG.info("nID is not null. Updating existing FLowProperty with parameters");
            flowProperty = flowPropertyDao.findByIdExpected(nID);
            if (flowProperty != null) {
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
                nID_Flow = determineFlow_ID(nID_Flow, sID_BP, nID_SubjectOrganDepartment);
            } catch (RecordNotFoundException e) {
                LOG.error(e.getMessage());
                throw new Exception(e.getMessage());
            }
            LOG.info("Creating new flow property for the flow with ID: {}", nID_Flow);
            flowProperty = new FlowProperty();

            FlowPropertyClass flowPropertyClass = flowPropertyClassDao.findByIdExpected(DEFAULT_FLOW_PROPERTY_CLASS);
            LOG.info("Loaded flow propetry service class: {}", flowPropertyClass);
            Flow flow = flowDao.findByIdExpected(nID_Flow);
            LOG.info("Loaded flow service data class: ", flow);

            flowProperty = fillFlowProperty(sName, sRegionTime, saRegionWeekDay, sDateTimeAt, sDateTimeTo, nLen,
                    sLenType, sData, flowProperty);
            flowProperty.setoFlowPropertyClass(flowPropertyClass);
            flowProperty.setbExclude(bExclude);
            flowProperty.setoFlow_ServiceData(flow);

            flow.getFlowProperties().add(flowProperty);

            flowDao.saveOrUpdate(flow);
            LOG.info("Successfully updated flow with new FlowProperty.");
        }
        return flowProperty;
    }

    /**
     * Удаление расписания
     *
     * @param nID - ИД-номер
     * @param nID_Flow - номер-ИД потока
     * @param bExclude - <b>true</b> для оаботы с расписаниями исключений;
     * <b>false</b> для работы с расписаниями включений
     * @return Массив объектов сущности расписаний
     */
    public List<FlowProperty> removeSheduleFlow(Long nID, Long nID_Flow, Boolean bExclude) {

        Flow flow = flowDao.findByIdExpected(nID_Flow);

        Iterator<FlowProperty> iterator = flow.getFlowProperties().iterator();
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
                flow.getFlowProperties().size());

        return flow.getFlowProperties();
    }

    /**
     * Получение активных тикетов
     *
     * @param sLogin имя пользователя для которого необходимо вернуть тикеты
     * @param bEmployeeUnassigned опциональный параметр (false по умолчанию).
     * Если true - возвращать тикеты не заассайненые на пользователей
     * @param sDateFilter опциональный параметр в формате yyyy-MM-dd. Дата за
     * которую выбирать тикеты. При выборке проверяется startDate тикета (без
     * учета времени. только дата). Если день такой же как и у указанное даты -
     * такой тикет добавляется в результат.
     * @return возвращает активные тикеты, отсортированные по startDate
     * @throws ParseException
     */
    public List<Map<String, String>> getFlowSlotTickets(String sLogin, Boolean bEmployeeUnassigned, String sDateFilter)
            throws ParseException {

        List<Map<String, String>> mReturn = new LinkedList<>();
        try {

            SimpleDateFormat oDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
            Date oDateFilter = null;
            if (sDateFilter != null) {
                //LOG.info("Checking for flow spot tickets for the date:(sDate={}) ", sDateFilter);
                oDateFilter = new SimpleDateFormat("yyyy-MM-dd").parse(sDateFilter);
                //LOG.info("Checking for flow spot tickets for the date:(oDateSelect={}) ", oDateFilter);
                LOG.info("Converted sDateFilter to oDateFilter! (sDateFilter={},oDateFilter:{})", sDateFilter, oDateFilter);
            }

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
                        if (oProperty.getType() instanceof QueueDataFormType) {
                            String sValue = oProperty.getValue();
                            if (sValue != null && !"".equals(sValue.trim()) && !"null".equals(sValue.trim())) {
                                bQueue = true;
                                LOG.info("sValue is present, so queue is filled");
                                //long nID_FlowSlotTicket = 0;
                                Map<String, Object> m = QueueDataFormType.parseQueueData(sValue);
                                String sDate = (String) m.get(QueueDataFormType.sDate);
                                LOG.info("(sDate={})", sDate);
                                String sID_Type = QueueDataFormType.get_sID_Type(m);
                                LOG.info("(sID_Type={})", sID_Type);
                                if ("DMS".equals(sID_Type)) {//Нет ни какой обработки т.к. это внешняя ЭО
                                    bQueueDMS = true;
                                    LOG.info("Found DMS flow slot tickets.");
                                    String snID_ServiceCustomPrivate = m.get("nID_ServiceCustomPrivate") + "";
                                    LOG.info("(nID_ServiceCustomPrivate={})", snID_ServiceCustomPrivate);
                                    String sTicket_Number = (String) m.get("ticket_number");
                                    LOG.info("(sTicket_Number={})", sTicket_Number);
                                    String sTicket_Code = (String) m.get("ticket_code");
                                    LOG.info("(sTicket_Code={})", sTicket_Code);
                                    //}else if("iGov".equals(sID_Type)){
                                    Date oDate = null;
                                    if (sDate != null) {
                                        //LOG.info("Converting sDate to oDate! (sDate={}) ", sDate);
                                        oDate = new SimpleDateFormat("yyyy-MM-dd").parse(sDate);
                                        LOG.info("Converted sDate to oDate! (sDate={},oDate:{})", sDate, oDate);
                                    }
                                    //Filtering:
                                    if (oDate == null || oDateFilter == null || (DateUtils.isSameDay(oDate, oDateFilter))) {
                                        addFlowSlowTicketToResult_DMS(mReturn, oDateFormat, sTicket_Number, sTicket_Code, sDate, oTask);
                                    }
                                }
                            }
                        }
                    }
                    if (bQueue && !bQueueDMS) {
                        Long nID_Task = Long.valueOf(oTask.getProcessInstanceId());
                        mTask.put(nID_Task, oTask);
                        ///mTaskProperty.put(nID_Task, aProperty);
                    }
                } else {
                    LOG.info("Task with ID:{} has null process instance id value", oTask.getId());
                }
            }
            //LOG.info("Will check tasks which belong to process definition IDs:{}", mTask.keySet());

            if (mTask.isEmpty()) {
                LOG.info("mTask.isEmpty()");
            } else {
                List<FlowSlotTicket> aFlowSlowTicket = oFlowSlotTicketDao.findAll();
                LOG.info("Found {} flow slot tickets.", (aFlowSlowTicket != null ? aFlowSlowTicket.size() : 0));
                if (aFlowSlowTicket != null) {
                    Collections.sort(aFlowSlowTicket, new Comparator<FlowSlotTicket>() {
                        @Override
                        public int compare(FlowSlotTicket ticket1, FlowSlotTicket ticket2) {
                            return ticket1.getsDateStart().compareTo(ticket2.getsDateStart());
                        }
                    });

                    for (FlowSlotTicket oFlowSlotTicket : aFlowSlowTicket) {
                        if (mTask.keySet().contains(oFlowSlotTicket.getnID_Task_Activiti())) {
                            Task oTask = mTask.get(oFlowSlotTicket.getnID_Task_Activiti());
                            /*if (oDateFilter != null) {
                            LOG.info("Comparing two dates:{} and {}", oFlowSlotTicket.getsDateStart().toDate(), oDateFilter);
                        }*/
                            //Filtering:
                            if (oDateFilter == null || (DateUtils.isSameDay(oFlowSlotTicket.getsDateStart().toDate(), oDateFilter))) {
                                addFlowSlowTicketToResult(mReturn, oDateFormat, oFlowSlotTicket, oTask);
                                /*} else {
                            LOG.info("Skipping flowSlot {} for task:{} as they have not valid  start-end date {}:{}",
                                    oFlowSlotTicket.getId(), oFlowSlotTicket.getnID_Task_Activiti(),
                                    oFlowSlotTicket.getsDateStart().toString(), oFlowSlotTicket.getsDateFinish());*/
                            }
                        }
                    }
                }
            }
        } catch (Exception ex) {
            LOG.info("Error during gettion of flowservice: {}", ex.getMessage());
        }

        return mReturn;
    }

    /**
     * Получение массива объектов SubjectOrganDepartment по ID бизнес процесса
     *
     * @param sID_BP имя Activiti BP
     * @return
     */
    public SubjectOrganDepartment[] getSubjectOrganDepartments(String sID_BP) {
        List<Flow> serviceDataList = flowDao.findAllBy("sID_BP", sID_BP);
        SubjectOrganDepartment[] result = new SubjectOrganDepartment[serviceDataList.size()];
        for (int i = 0; i < serviceDataList.size(); i++) {
            Flow sd = serviceDataList.get(i);
            Long nID_SubjectOrganDepartment = sd.getnID_SubjectOrganDepartment();
            SubjectOrganDepartment subjectOrganDepartment = subjectOrganDepartmentDao
                    .findByIdExpected(nID_SubjectOrganDepartment);
            result[i] = subjectOrganDepartment;
        }
        return result;
    }

    public List<FlowSlotVO> buildFlowSlotsTest(Long nID_Flow, DateTime startDate, DateTime stopDate) throws ParseException {

        Flow flow = flowDao.findByIdExpected(nID_Flow);

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

    public void buildFlowSlots() {
        int i = 0;
        Logger LOG_TEMP = LoggerFactory.getLogger(JobBuilderFlowSlots.class);
        try {
            DateTime oDateStart = DateTime.now().withTimeAtStartOfDay();
            LOG.info(" oDateStart = {}", oDateStart);
            List<Flow> aFlow = flowDao.findAll();
            LOG_TEMP.info(" aFlow.size = {}", aFlow.size());
            for (Flow oFlow : aFlow) {
                try {
                    ++i;
                    if (oFlow.getsID_BP().endsWith(SUFFIX_AUTO) && oFlow.getnCountAutoGenerate() != null) {
                        checkAndBuildFlowSlots(oFlow, oDateStart);
                        LOG_TEMP.info("number: " + i + " Flow_ServiceData ID {}, sID_BP = {} end!!!", oFlow.getId(), oFlow.getsID_BP());
                    } else {
                        LOG_TEMP.info("number: " + i + " Flow_ServiceData ID {}, sID_BP = {} skip!!!", oFlow.getId(), oFlow.getsID_BP());
                    }
                } catch (Exception ex) {
                    LOG_TEMP.error("number: " + i + " buildFlowSlots fail: ", ex);
                }
            }
            LOG.info("number: " + i + " oDateStart = {} end!!!", oDateStart);
        } catch (Exception ex) {
            LOG_TEMP.error("number: " + i + " Error: ", ex);
        }
    }

    public void checkAndBuildFlowSlots(Flow oFlow, DateTime oDateStart) throws ParseException {
        //Maxline: TODO добавить исключения
        Long nID_Flow = oFlow.getId();
        Long nID_ServiceData = oFlow.getnID_ServiceData();   //nID_ServiceData = 358  _test_queue_cancel, nID_ServiceData = 63L Видача/заміна паспорта громадянина для виїзду за кордон
        Long nCountAutoGenerate = oFlow.getnCountAutoGenerate(); // nCountAutoGenerate данным параметром задаваем количество дней на которое генерируем
        Long nID_SubjectOrganDepartment = oFlow.getnID_SubjectOrganDepartment();
        //TODO: uncoment. Выполняется только заполнение колекции с исключениями. Не происходит работы с ней
        /*List<FlowProperty> aExcludeFlowProperty = new ArrayList<>();
        for (FlowProperty flowProperty : oFlow.getFlowProperties()) {
            if (flowProperty.getbExclude()) {
                if ((oFlow.getsGroup() != null && oFlow.getsGroup().equals(flowProperty.getsGroup()))
                        || (oFlow.getnID_ServiceData().longValue() == flowProperty.getId().longValue())) {
                    aExcludeFlowProperty.add(flowProperty);
                }
            }
        }*/

        int nStartDay = 0;
        DateTime oDateTimeStart = oDateStart.plusDays(nStartDay);// = oDateStart.plusDays(0); //maxline: todo удалить комментарий после тестирования
        int сountAutoGenerate = toIntExact(nCountAutoGenerate);
        DateTime oDateTimeEnd = oDateStart.plusDays(сountAutoGenerate);
        LOG.info("!*!*!* nCountAutoGenerate = {} nID_Flow = {}, nID_ServiceData = {}, nID_SubjectOrganDepartment = {}, dateStart = {}, dateEnd = {}",
                nCountAutoGenerate, nID_Flow, nID_ServiceData, nID_SubjectOrganDepartment, oDateTimeStart, oDateTimeEnd);
        List<FlowSlotVO> resFlowSlotVO = buildFlowSlots(nID_Flow, oDateTimeStart, oDateTimeEnd); // строит четко на месяц вперед (точнее dateStart - dateEnd) независимо от рабочих или нерабочих дней
        LOG.info("!*!*!* resFlowSlotVO.size() = {}", resFlowSlotVO.size());

        nStartDay += DAYS_IN_MONTH;
        boolean bEnoughFreeDays = nStartDay < DAYS_IN_HALF_YEAR;
        LOG.info(" bEnoughFreeDays = {}", bEnoughFreeDays);
    }
}
