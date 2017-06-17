/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.igov.service.business.action;

import static org.igov.model.action.event.HistoryEvent_ServiceDaoImpl.DASH;
import static org.igov.service.business.action.task.core.ActionTaskService.amFieldMessageQuestion;
import static org.igov.service.business.subject.SubjectMessageService.sMessageHead;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.igov.io.web.HttpRequester;
import org.igov.model.action.event.HistoryEventDao;
import org.igov.model.action.event.HistoryEventMessage;
import org.igov.model.action.event.HistoryEventType;
import org.igov.model.action.event.HistoryEvent_Service;
import org.igov.model.action.event.HistoryEvent_ServiceDao;
import org.igov.model.action.event.HistoryEvent_Service_StatusType;
import org.igov.model.action.event.ServicesStatistics;
import org.igov.model.core.GenericEntityDao;
import org.igov.model.document.Document;
import org.igov.model.document.DocumentDao;
import org.igov.model.object.place.Region;
import org.igov.model.subject.message.SubjectMessage;
import org.igov.model.subject.message.SubjectMessagesDao;
import org.igov.service.business.action.event.HistoryEventService;
import org.igov.service.business.action.task.core.ActionTaskService;
import org.igov.service.business.subject.SubjectMessageService;
import org.igov.service.exception.CRCInvalidException;
import org.igov.service.exception.CommonServiceException;
import org.igov.util.ToolLuna;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import liquibase.util.csv.CSVWriter;

/**
 *
 * @author Belyavtsev Vladimir Vladimirovich (BW)
 */
@Service
public class ActionEventService {

    private static final Logger LOG = LoggerFactory.getLogger(ActionEventService.class);

    @Autowired
    HttpRequester httpRequester;

    @Autowired
    private HistoryEventService historyEventService;

    @Autowired
    private HistoryEventDao historyEventDao;

    @Autowired
    private DocumentDao documentDao;

    @Autowired
    private HistoryEvent_ServiceDao historyEventServiceDao;

    @Autowired
    private SubjectMessagesDao subjectMessagesDao;

    @Autowired
    private SubjectMessageService oSubjectMessageService;

    @Autowired
    @Qualifier("regionDao")
    private GenericEntityDao<Long, Region> regionDao;
    
    @Value("${asID_BpForStatisticsOfDnepr}")
    private String [] asID_BpForStatisticsOfDnepr;


    public void checkAuth(HistoryEvent_Service oHistoryEvent_Service, Long nID_Subject, String sToken) throws Exception {
        if (sToken != null) {
            if (sToken.equals(oHistoryEvent_Service.getsToken())) {
                nID_Subject = oHistoryEvent_Service.getnID_Subject();
            } else {
                LOG.warn("wrong sToken! (nID_Subject={},oHistoryEvent_Service.getnID_Subject()={},sToken={})", nID_Subject, oHistoryEvent_Service.getnID_Subject(), sToken);
            }
        }
        if (nID_Subject == null) {
            LOG.warn("nID_Subject is not owner of Order of messages and wrong sToken! (nID_Subject={},oHistoryEvent_Service.getnID_Subject()={},sToken={})", nID_Subject, oHistoryEvent_Service.getnID_Subject(), sToken);
            throw new Exception("nID_Subject is not Equal and wrong sToken!");
        } else if (!Objects.equals(nID_Subject, oHistoryEvent_Service.getnID_Subject())) {
            LOG.warn("nID_Subject is not owner of Order of messages! (nID_Subject={},oHistoryEvent_Service.getnID_Subject()={},sToken={})", nID_Subject, oHistoryEvent_Service.getnID_Subject(), sToken);
            throw new Exception("nID_Subject is not Equal!");
        }
    }

    public static Long addSomeServicesCount(Long nCount, Long nID_Service, Region region) {
        //currMapWithName.put("nCount", currMap.get("nCount"));
        /*https://igov.org.ua/service/661/general - 43
         https://igov.org.ua/service/655/generall - 75
         https://igov.org.ua/service/176/general - 546
         https://igov.org.ua/service/654/general - 307   */

        boolean magicID = "1200000000".equals(region.getsID_UA());
        if (nID_Service == 661) {
            if (magicID) {
                nCount += 43;
            }
        } else if (nID_Service == 665) {
            if (magicID) {
                nCount += 75;
            }
        } else if (nID_Service == 176) {
            if (magicID) {
                nCount += 546;
            }
        } else if (nID_Service == 654) {
            if (magicID) {
                nCount += 307;
            }
        } else if (nID_Service == 159) {
            /*https://igov.org.ua/service/159/general
             Днепропетровская область - 53
             Киевская область - 69
             1;Дніпропетровська;"1200000000"
             5;Київ;"8000000000"
             16;Київська;"3200000000"*/
            if (magicID) {
                nCount += 53;
            } else if ("8000000000".equals(region.getsID_UA()) || "3200000000".equals(region.getsID_UA())) {
                nCount += 69;
            }
        } else if (nID_Service == 1) {
            /*https://igov.org.ua/service/1/general
             Днепропетровская область - 812*/
 /*if("".equals(region.getsID_UA())){
             nCount+=53;
             }else if("".equals(region.getsID_UA())){
             nCount+=69;
             }*/
            if (magicID) {
                nCount += 812;
            }
        } else if (nID_Service == 772) {
            if (magicID) {
                nCount += 96;
            }
        } else if (nID_Service == 4) {
            /*
             https://igov.org.ua/service/4/general -
             Днепропетровская область - услуга временно приостановлена
             по иным регионам заявок вне было.
             */
            nCount += 0;
        } else if (nID_Service == 0) {
            nCount += 0;
            //region.getsID_UA()
        }
        return nCount;
    }

    public static Long[] getCountFromStatisticArrayMap(List<Map<String, Object>> am) {
        Long n = 0L;
        Long nRate = 0L;
        LOG.info("(am={})", am);
        if (am.size() > 0) {
            if (am.get(0).containsKey("nCount")) {
                String s = am.get(0).get("nCount") + "";
                if (!"null".equals(s)) {
                    n = new Long(s);
                    LOG.info("(n={})", n);
                }
            }
            if (am.get(0).containsKey("nRate")) {
                String s = am.get(0).get("nRate") + "";
                if (!"null".equals(s)) {
                    nRate = new Long(s);
                    LOG.info("(nRate={})", n);
                }
            }
        }
        return new Long[]{n, nRate * n};
    }

    public void createHistoryEvent(HistoryEventType eventType, Long documentId,
            String sFIO, String sPhone, Long nMs, String sEmail) {
        Map<String, String> values = new HashMap<>();
        Document oDocument = null;
        try {
            values.put(HistoryEventMessage.FIO, sFIO);
            values.put(HistoryEventMessage.TELEPHONE, sPhone);
            values.put(HistoryEventMessage.EMAIL, sEmail);
            values.put(HistoryEventMessage.DAYS, "" + TimeUnit.MILLISECONDS.toDays(nMs));

            oDocument = documentDao.getDocument(documentId);
            values.put(HistoryEventMessage.DOCUMENT_NAME, oDocument.getName());
            values.put(HistoryEventMessage.DOCUMENT_TYPE, oDocument.getDocumentType().getName());
            documentId = oDocument.getSubject().getId();
        } catch (Exception oException) {
            LOG.warn("Error: {}, can't get document info!", oException.getMessage());
            LOG.trace("FAIL:", oException);
        }
        try {
            String eventMessage = HistoryEventMessage.createJournalMessage(eventType, values);
            historyEventDao.setHistoryEvent(documentId, eventType.getnID(),
                    eventMessage, eventMessage, null, oDocument.getId(), null);
        } catch (IOException oException) {
            LOG.error("error: {}, during creating HistoryEvent", oException.getMessage());
            LOG.trace("FAIL:", oException);
        }
    }

    public void createHistoryEvent(HistoryEventType eventType,
            Long nID_Subject, String sSubjectName_Upload, Long nID_Document,
            Document document) {
        Map<String, String> values = new HashMap<>();
        Document oDocument = null;
        try {
            oDocument = document == null ? documentDao
                    .getDocument(nID_Document) : document;
            values.put(HistoryEventMessage.DOCUMENT_TYPE, oDocument
                    .getDocumentType().getName());
            values.put(HistoryEventMessage.DOCUMENT_NAME, oDocument.getName());
            values.put(HistoryEventMessage.ORGANIZATION_NAME,
                    sSubjectName_Upload);
        } catch (RuntimeException oException) {
            LOG.warn("Error: {}, can't get document info!", oException.getMessage());
            LOG.trace("FAIL:", oException);
        }
        try {
            String eventMessage = HistoryEventMessage.createJournalMessage(
                    eventType, values);
            historyEventDao.setHistoryEvent(nID_Subject, eventType.getnID(),
                    eventMessage, eventMessage, null, oDocument.getId(), null);
        } catch (IOException oException) {
            LOG.error("error: {}, during creating HistoryEvent", oException.getMessage());
            LOG.trace("FAIL:", oException);
        }
    }

    public String updateHistoryEvent_Service_Central(String sID_Order,
            String saField, String sBody, String sToken,
            String sUserTaskName) throws Exception {
        Map<String, String> mParam = new HashMap<>();
        mParam.put("soData", saField);
        mParam.put("sBody", sBody);
        mParam.put("sToken", sToken);
        return historyEventService.updateHistoryEvent(sID_Order, sUserTaskName,
                true, HistoryEvent_Service_StatusType.UNKNOWN, mParam);
    }

    public List<ServicesStatistics> getServicesStatistics(DateTime from, DateTime to) {
        List<ServicesStatistics> servicesStatistics = historyEventServiceDao.getServicesStatistics(from, to);
        return servicesStatistics;
    }
    
    /**
     * Сервис получения статистики по городу Днепр
     * @param sDate_from
     * @param sDate_to
     * @param httpResponse
     */
    public void getServicesStatisticsOfDnepr(String sDate_from, String sDate_to, HttpServletResponse httpResponse) {

    	//parse date to check that it has appropriate form
        DateTime from = DateTime.parse(sDate_from, DateTimeFormat.forPattern("y-MM-d HH:mm:ss"));
        DateTime to = DateTime.parse(sDate_to, DateTimeFormat.forPattern("y-MM-d HH:mm:ss"));

        List<ServicesStatistics> servicesStatistics = historyEventServiceDao.getServicesStatisticsOfDnepr(from, to);
        LOG.info("servicesStatistics " + servicesStatistics);

        String[] headingFields = {"nID_Service", "ServiceName", "SID_UA", "placeName", "nCountTotal", "nCountFeedback",
            "nCountEscalation", "averageRate", "averageTime"};
        List<String> headers = new ArrayList<>();
        headers.addAll(Arrays.asList(headingFields));

        httpResponse.setHeader("Content-disposition", "attachment; filename=" + "ServicesStatisticsOfDnepr.csv");
        httpResponse.setHeader("Content-Type", "text/csv; charset=UTF-8");

        /**
         * запись в файл
         */
        CSVWriter csvWriter;
        try {
            csvWriter = new CSVWriter(httpResponse.getWriter(), ';', CSVWriter.NO_QUOTE_CHARACTER);
            csvWriter.writeNext(headers.toArray(new String[headers.size()]));
            
            LOG.info("asID_BpForStatisticsOfDnepr " + Arrays.asList(asID_BpForStatisticsOfDnepr));

			for (ServicesStatistics item : servicesStatistics) {
				LOG.info("String.valueOf(item.getnID_Service()) " + String.valueOf(item.getnID_Service()));
				List<String> line = new LinkedList<>();
				if (Arrays.asList(asID_BpForStatisticsOfDnepr).contains(String.valueOf(item.getnID_Service()))) {
					LOG.info("String.valueOf(item.getnID_Service()) -->> " + String.valueOf(item.getnID_Service()));
					line.add(String.valueOf(item.getnID_Service()));
					line.add(item.getServiceName());
					line.add(String.valueOf(item.getSID_UA()));
					line.add(item.getPlaceName());
					line.add(item.getnCountTotal() == null ? "0" : item.getnCountTotal().toString());
					line.add(item.getnCountFeedback() == null ? "0" : item.getnCountFeedback().toString());
					line.add(item.getnCountEscalation() == null ? "0" : item.getnCountEscalation().toString());
					line.add(item.getAverageRate() == null ? "0" : item.getAverageRate().toString());
					// divide average time (mins) to 60 to get hours
					line.add(item.getAverageTime() == null ? "0"
							: String.valueOf(item.getAverageTime().floatValue() / 60f));
					LOG.info("Filling everi line>>>>>>>>>>>>>>> {}",line );
					csvWriter.writeNext(line.toArray(new String[line.size()]));
				}
			}
            csvWriter.close();
        } catch (Exception e) {
            LOG.error("Error occurred while creating CSV file {}", e.getMessage());
            LOG.error("stacktrace {}", ExceptionUtils.getStackTrace(e));
        }
    }

    public List<Map<String, Object>> getListOfHistoryEvents(Long nID_Service) {

        List<Map<String, Object>> aRowReturn = new LinkedList<>();
        List<Map<String, Long>> aRow = historyEventServiceDao
                .getHistoryEvent_ServiceBynID_Service(nID_Service);

        Map<String, Object> mCellReturn;
        for (Map<String, Long> mCell : aRow) {
            mCellReturn = new HashMap<>();

            Long nCount = mCell.get("nCount") == null ? 0L : mCell.get("nCount");

            String sName = "Вся країна";
            Long nID_Region = mCell.get("sName");
            if (nID_Region > 0) {
                Region oRegion = regionDao.findByIdExpected(nID_Region);
                sName = oRegion.getName();
                nCount = addSomeServicesCount(nCount, nID_Service, oRegion);
            }
            LOG.info("sName={}", sName);
            mCellReturn.put("sName", sName);

            Long nTimeMinutes = mCell.get("nTimeMinutes");
            Long nRate = mCell.get("nRate") == null ? 0L : mCell.get("nRate");

            if (nID_Service == 159) {//issue 750 + 777
                LOG.info("nID_Service={}", nID_Service);
                List<Map<String, Object>> am;
                Long[] arr;
                Long nSumRate = nRate * nCount;
                for (Long nID = 726L; nID < 734L; nID++) {
                    am = getListOfHistoryEvents(nID);
                    arr = getCountFromStatisticArrayMap(am);
                    nCount += arr[0];
                    nSumRate += arr[1];
                }
                LOG.info("nCount(summ)={}", nCount);
                nRate = nSumRate / nCount;
                LOG.info("nRAte(summ)={}", nRate);
            }
            LOG.info("nCount={}", nCount);
            mCellReturn.put("nCount", nCount);
            mCellReturn.put("nRate", nRate);
            mCellReturn.put("nTimeMinutes", nTimeMinutes != null ? nTimeMinutes : "0");
            aRowReturn.add(mCellReturn);
        }
        return aRowReturn;
    }

    public void setHistoryEvent(HistoryEventType eventType,
            Long nID_Subject, Map<String, String> mParamMessage, Long nID_HistoryEvent_Service, Long nID_Document,
            String sSubjectInfo, String eventMessage) {
        try {
            LOG.info("Method setHistoryEvent started");
            if(eventMessage == null){
                eventMessage = HistoryEventMessage.createJournalMessage(
                    eventType, mParamMessage);
            }
            LOG.info("Creating journal message ended");
            historyEventDao.setHistoryEvent(nID_Subject, eventType.getnID(),
                    eventMessage, eventMessage, nID_HistoryEvent_Service, nID_Document, sSubjectInfo);
        } catch (IOException e) {
            LOG.error("error: {}, during creating HistoryEvent", e.getMessage());
            LOG.trace("FAIL:", e);
        }
    }

    public HistoryEvent_Service getHistoryEventService(String sID_Order)
            throws CommonServiceException, CRCInvalidException {

        HistoryEvent_Service historyEventService;
        historyEventService = historyEventServiceDao.getOrgerByID(sID_Order);

        return historyEventService;
    }

    public HistoryEvent_Service updateActionStatus_Central(
            String sID_Order,
            String sUserTaskName,
            String soData,
            String sToken,
            //String sHead,
            String sBody,
            String nTimeMinutes,
            Long nID_Proccess_Feedback,
            Long nID_Proccess_Escalation,
            Long nID_StatusType,
            String sSubjectInfo,
            Long nID_Subject
    //  String sDateCreate,
    // String sDateClosed
    ) throws CommonServiceException {
        LOG.info("Mehtod updateActionStatus_Central started for task " + sID_Order);
        LOG.info("Status type is " + nID_StatusType);

        HistoryEvent_Service oHistoryEvent_Service = null;
        try {
            oHistoryEvent_Service = getHistoryEventService(sID_Order);
        } catch (CRCInvalidException e) {
            e.printStackTrace();
        }
        LOG.info("now we have got history event service");
        HistoryEvent_Service_StatusType oHistoryEvent_Service_StatusType = HistoryEvent_Service_StatusType.getInstance(nID_StatusType);
        LOG.info("checking conditions started");
        boolean isChanged = false;
        if (sUserTaskName != null && !sUserTaskName.equals(oHistoryEvent_Service.getsUserTaskName())) {
            oHistoryEvent_Service.setsUserTaskName(sUserTaskName);
            isChanged = true;
        }
        /*if (soData != null && !soData.equals(oHistoryEvent_Service.getSoData())) { //TODO: убрать после реализации задачи 1553
        	oHistoryEvent_Service.setSoData(soData);
            isChanged = true;
        }*/

        if (sBody != null && !sBody.equals(oHistoryEvent_Service.getsBody())) {
            oHistoryEvent_Service.setsBody(sBody);
            isChanged = true;
        }
        if (sToken == null || !sToken.equals(oHistoryEvent_Service.getsToken())) {
            oHistoryEvent_Service.setsToken(sToken);
            isChanged = true;
        }
        if (nTimeMinutes != null && !nTimeMinutes.isEmpty()) {
            Integer nMinutes;
            try {
                nMinutes = Integer.valueOf(nTimeMinutes);
            } catch (NumberFormatException ignored) {
                nMinutes = 0;
            }
            oHistoryEvent_Service.setnTimeMinutes(nMinutes);
            isChanged = true;
        }
        if (nID_Proccess_Feedback != null && !nID_Proccess_Feedback
                .equals(oHistoryEvent_Service.getnID_Proccess_Feedback())) {
            oHistoryEvent_Service.setnID_Proccess_Feedback(nID_Proccess_Feedback);
            isChanged = true;
        }
        if (nID_Proccess_Escalation != null && !nID_Proccess_Escalation
                .equals(oHistoryEvent_Service.getnID_Proccess_Escalation())) {
            oHistoryEvent_Service.setnID_Proccess_Escalation(nID_Proccess_Escalation);
            isChanged = true;
        }
        if (nID_Proccess_Escalation != null && nID_Proccess_Escalation == -1) {
            oHistoryEvent_Service.setnID_Proccess_Escalation(null);
            isChanged = true;
        }
        if (nID_StatusType != null && !nID_StatusType.equals(oHistoryEvent_Service.getnID_StatusType())) {
            oHistoryEvent_Service.setnID_StatusType(nID_StatusType);
        }
        oHistoryEvent_Service.setsID_Order(sID_Order);

        if (nID_Subject == null) {
            nID_Subject = oHistoryEvent_Service.getnID_Subject();
        }
        LOG.info("checking conditions ended");
        
        if ("TaskCancelByUser".equals(soData)){
            LOG.info("soData is TaskCancelByUser: " + soData);
            Map<String, String> mParamMessage = new HashMap<>();
            LOG.info("SERVICE_STATE: " + sUserTaskName);
            mParamMessage.put(HistoryEventMessage.SERVICE_STATE, sUserTaskName == null ? oHistoryEvent_Service_StatusType.getsName_UA() : sUserTaskName);
            mParamMessage.put(HistoryEventMessage.TASK_NUMBER, sID_Order);
            setHistoryEvent(HistoryEventType.ACTIVITY_STATUS_NEW, nID_Subject, mParamMessage, oHistoryEvent_Service.getId(),
                    null, sSubjectInfo, HistoryEventType.TASK_CANCELED.getsTemplate());
        }
        else if (soData == null || "[]".equals(soData)) { //My journal. change status of task
            LOG.info("soData is null or empty array: " + soData);
            Map<String, String> mParamMessage = new HashMap<>();
            LOG.info("SERVICE_STATE: " + sUserTaskName);
            mParamMessage.put(HistoryEventMessage.SERVICE_STATE, sUserTaskName == null ? oHistoryEvent_Service_StatusType.getsName_UA() : sUserTaskName);
            mParamMessage.put(HistoryEventMessage.TASK_NUMBER, sID_Order);
            setHistoryEvent(HistoryEventType.ACTIVITY_STATUS_NEW, nID_Subject, mParamMessage, oHistoryEvent_Service.getId(),
                    null, sSubjectInfo, null);
        } else {
            LOG.info("soData is not null or empty array: " + soData);
            //!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!! TODO: Move To Interceptor!!!
            StringBuilder osBody = new StringBuilder(sBody);
            Long nID_SubjectMessageType = null;
            HistoryEventType oHistoryEventType = null;
            Boolean bQuestion = null;
            LOG.info("checking status type");
            if (oHistoryEvent_Service_StatusType == HistoryEvent_Service_StatusType.OPENED_REMARK_CLIENT_ANSWER) {
                oHistoryEventType = HistoryEventType.SET_TASK_ANSWERS;
                bQuestion = true;
                nID_SubjectMessageType = 4L;
                LOG.info("oHistoryEvent_Service_StatusType is set to OPENED_REMARK_CLIENT_ANSWER");
                LOG.info("nID_SubjectMessageType is set to" + nID_SubjectMessageType);
                isChanged = true;
                oHistoryEvent_Service.setSoData("[]");
            } else if (oHistoryEvent_Service_StatusType == HistoryEvent_Service_StatusType.OPENED_REMARK_EMPLOYEE_QUESTION) {
                oHistoryEventType = HistoryEventType.SET_TASK_QUESTIONS;
                bQuestion = false;
                nID_SubjectMessageType = 5L;
                LOG.info("oHistoryEvent_Service_StatusType is set to OPENED_REMARK_EMPLOYEE_QUESTION");
                LOG.info("nID_SubjectMessageType is set to" + nID_SubjectMessageType);
                LOG.info("soData in oHistoryEvent_Service is 5l ={}", oHistoryEvent_Service.getSoData().toString());
                
            }

            if (nID_SubjectMessageType != null) {
                LOG.info("nID_SubjectMessageType is not null");
                List<Map<String, String>> amReturn = amFieldMessageQuestion(soData, bQuestion);//saField
                String soTable = ActionTaskService.createTable_TaskProperties(amReturn, bQuestion, false);

                osBody.append("<br/>").append(soTable).append("<br/>");//soData

                Map<String, String> mParamMessage = new HashMap<>();
                mParamMessage.put(HistoryEventMessage.TASK_NUMBER, sID_Order);
                mParamMessage.put(HistoryEventMessage.S_BODY, sBody == null ? "" : sBody);
              //  mParamMessage.put(HistoryEventMessage.TABLE_BODY, soTable);//TODO://soData - убрать после реализации задачи 1553
                setHistoryEvent(oHistoryEventType, nID_Subject, mParamMessage, oHistoryEvent_Service.getId(), null, sSubjectInfo, null);
                SubjectMessage oSubjectMessage = oSubjectMessageService.createSubjectMessage(sMessageHead(nID_SubjectMessageType,
                        sID_Order), osBody.toString(), nID_Subject, "", "", soData, nID_SubjectMessageType, sSubjectInfo,bQuestion);
                oSubjectMessage.setnID_HistoryEvent_Service(oHistoryEvent_Service.getId());
                LOG.info("setting message");
                subjectMessagesDao.setMessage(oSubjectMessage);
            }

        }
        if (isChanged) {
            LOG.info("updating oHistoryEvent_Service: {}", oHistoryEvent_Service);
            historyEventServiceDao.updateHistoryEvent_Service(oHistoryEvent_Service);
            LOG.info("soData in oHistoryEvent_Service is END ={}", oHistoryEvent_Service.getSoData());
        }
        LOG.info("Mehtod updateActionStatus_Central started for task " + sID_Order);
        return oHistoryEvent_Service;
    }

    public HistoryEvent_Service addActionStatus_Central(
            String sID_Order,
            Long nID_Subject,
            String sUserTaskName,
            //String sProcessInstanceName,
            Long nID_Service,
            Long nID_ServiceData,
            Long nID_Region,
            String sID_UA,
            String soData,
            String sToken,
            String sHead,
            String sBody,
            Long nID_Proccess_Feedback,
            Long nID_Proccess_Escalation,
            Long nID_StatusType,
            String sID_Public_SubjectOrganJoin
    ) {
        int dash_position = sID_Order.indexOf(DASH);
        int nID_Server = dash_position != -1 ? Integer.parseInt(sID_Order.substring(0, dash_position)) : 0;
        Long nID_Order = Long.valueOf(sID_Order.substring(dash_position + 1));
        Long nID_Process = ToolLuna.getOriginalNumber(nID_Order);

        HistoryEvent_Service oHistoryEvent_Service = new HistoryEvent_Service();
        oHistoryEvent_Service.setnID_Process(nID_Process);
        oHistoryEvent_Service.setsUserTaskName(sUserTaskName);
        oHistoryEvent_Service.setnID_StatusType(nID_StatusType);
        oHistoryEvent_Service.setnID_Subject(nID_Subject);
        oHistoryEvent_Service.setnID_Region(nID_Region);
        oHistoryEvent_Service.setnID_Service(nID_Service);
        oHistoryEvent_Service.setnID_ServiceData(nID_ServiceData);
        oHistoryEvent_Service.setsID_UA(sID_UA);
        oHistoryEvent_Service.setnRate(null);
        oHistoryEvent_Service.setSoData(soData);
        oHistoryEvent_Service.setsToken(sToken);
        //if(sHead==null){
        //    sHead = sProcessInstanceName;
        //}
        oHistoryEvent_Service.setsHead(sHead);
        oHistoryEvent_Service.setsBody(sBody);
        oHistoryEvent_Service.setnID_Server(nID_Server);
        oHistoryEvent_Service.setnID_Proccess_Feedback(nID_Proccess_Feedback);
        oHistoryEvent_Service.setnID_Proccess_Escalation(nID_Proccess_Escalation);
        oHistoryEvent_Service.setsID_Public_SubjectOrganJoin(sID_Public_SubjectOrganJoin);
        oHistoryEvent_Service = historyEventServiceDao.addHistoryEvent_Service(oHistoryEvent_Service);
        Map<String, String> mParamMessage = new HashMap<>();
        mParamMessage.put(HistoryEventMessage.SERVICE_NAME, sHead);//sProcessInstanceName
        mParamMessage.put(HistoryEventMessage.SERVICE_STATE, sUserTaskName);
        setHistoryEvent(HistoryEventType.GET_SERVICE, nID_Subject, mParamMessage, oHistoryEvent_Service.getId(), null, null, null);
        return oHistoryEvent_Service;
    }

}
