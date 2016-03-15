/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.igov.service.business.action;

import io.swagger.annotations.ApiParam;
import org.igov.io.web.HttpRequester;
import org.igov.model.action.event.*;
import org.igov.model.core.GenericEntityDao;
import org.igov.model.document.Document;
import org.igov.model.document.DocumentDao;
import org.igov.model.object.place.Region;
import org.igov.service.business.action.event.HistoryEventService;
import org.igov.service.controller.ExceptionCommonController;
import org.igov.service.exception.CRCInvalidException;
import org.igov.service.exception.CommonServiceException;
import org.igov.service.exception.EntityNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.TimeUnit;
import static org.igov.model.action.event.HistoryEvent_ServiceDaoImpl.DASH;
import org.igov.model.action.event.HistoryEvent_Service_StatusType;

import org.igov.model.subject.message.SubjectMessage;
import org.igov.model.subject.message.SubjectMessagesDao;
import org.igov.service.business.action.task.core.ActionTaskService;
import static org.igov.service.business.action.task.core.ActionTaskService.createTable_TaskProperties;
import org.igov.service.business.subject.SubjectMessageService;
import static org.igov.service.business.subject.SubjectMessageService.sMessageHead;
import org.igov.util.ToolLuna;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

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
    private GenericEntityDao<Region> regionDao;

    //@Autowired
    //private HistoryEvent_ServiceDao historyEventServiceDao;
    
    public void checkAuth (HistoryEvent_Service oHistoryEvent_Service, Long nID_Subject, String sToken) throws Exception{
        if(sToken!=null){
            if(sToken.equals(oHistoryEvent_Service.getsToken())){
                nID_Subject = oHistoryEvent_Service.getnID_Subject();
            }else{
                LOG.warn("wrong sToken! (nID_Subject={},oHistoryEvent_Service.getnID_Subject()={},sToken={})", nID_Subject, oHistoryEvent_Service.getnID_Subject(),sToken);
            }
        }
        if(nID_Subject==null){
            LOG.warn("nID_Subject is not owner of Order of messages and wrong sToken! (nID_Subject={},oHistoryEvent_Service.getnID_Subject()={},sToken={})", nID_Subject, oHistoryEvent_Service.getnID_Subject(),sToken);
            throw new Exception("nID_Subject is not Equal and wrong sToken!");
        }else if(!Objects.equals(nID_Subject, oHistoryEvent_Service.getnID_Subject())){
            LOG.warn("nID_Subject is not owner of Order of messages! (nID_Subject={},oHistoryEvent_Service.getnID_Subject()={},sToken={})", nID_Subject, oHistoryEvent_Service.getnID_Subject(),sToken);
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
        return new Long[] { n, nRate * n };
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
                    eventMessage, eventMessage, null, oDocument.getId());
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
                    eventMessage, eventMessage, null, oDocument.getId());
        } catch (IOException oException) {
            LOG.error("error: {}, during creating HistoryEvent", oException.getMessage());
            LOG.trace("FAIL:", oException);
        }
    }

    public String updateHistoryEvent_Service_Central(String sID_Order,
            String saField, String sBody, String sToken,
            String sUserTaskName) throws Exception {
        Map<String, String> mParam = new HashMap<>();
//        params.put("sID_Order", sID_Order);
        mParam.put("soData", saField);
//        params.put("sHead", sHead);
        mParam.put("sBody", sBody);
        mParam.put("sToken", sToken);
//        params.put("sUserTaskName", sUserTaskName);
        return historyEventService.updateHistoryEvent(sID_Order, sUserTaskName,
                true, HistoryEvent_Service_StatusType.UNKNOWN, mParam);
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
            Long nID_Subject, Map<String, String> mParamMessage, Long nID_HistoryEvent_Service, Long nID_Document) {
        try {
            String eventMessage = HistoryEventMessage.createJournalMessage(
                    eventType, mParamMessage);
            historyEventDao.setHistoryEvent(nID_Subject, eventType.getnID(),
                    eventMessage, eventMessage, nID_HistoryEvent_Service, nID_Document);
        } catch (IOException e) {            
            LOG.error("error: {}, during creating HistoryEvent", e.getMessage());
            LOG.trace("FAIL:", e);
        }
    }

    public HistoryEvent_Service getHistoryEventService(String sID_Order) throws CommonServiceException {

        HistoryEvent_Service historyEventService;
        try {
            historyEventService = historyEventServiceDao.getOrgerByID(sID_Order);
        } catch (CRCInvalidException | EntityNotFoundException e) {
            throw new CommonServiceException(
                    ExceptionCommonController.BUSINESS_ERROR_CODE,
                    e.getMessage(), e,
                    HttpStatus.FORBIDDEN);
        }
        return historyEventService;
    }

    /*public void createHistoryEventForTaskQuestions(HistoryEventType eventType, String soData, String sBody,
            String sID_Order, Long nID_Subject) {
        try {
            Map<String, String> mParamMessage = new HashMap<>();
            if (soData != null && !"[]".equals(soData)) {
                mParamMessage.put(HistoryEventMessage.TASK_NUMBER, sID_Order);
                mParamMessage.put(HistoryEventMessage.S_BODY, sBody == null ? "" : sBody);
                mParamMessage.put(HistoryEventMessage.TABLE_BODY, createTable_TaskProperties(soData, true));
                setHistoryEvent(eventType, nID_Subject, mParamMessage);
            }
        } catch (Exception e) {
            LOG.error("FAIL:", e);
        }
        
    }*/

    
    
    
    
    
    
    
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
            Long nID_StatusType
    ) throws CommonServiceException {

        //TODO: Remove lete (for back compatibility)
        /*if (sID_Order.indexOf(DASH) <= 0) {
            sID_Order = "0-" + sID_Order;
            LOG.warn("Old format of parameter! (sID_Order={})",sID_Order);
        }*/
        HistoryEvent_Service oHistoryEvent_Service = getHistoryEventService(sID_Order);
        
        HistoryEvent_Service_StatusType oHistoryEvent_Service_StatusType = HistoryEvent_Service_StatusType.getInstance(nID_StatusType);

        boolean isChanged = false;
        if (sUserTaskName != null && !sUserTaskName.equals(oHistoryEvent_Service.getsUserTaskName())) {
            oHistoryEvent_Service.setsUserTaskName(sUserTaskName);
            isChanged = true;
        }
        if (soData != null && !soData.equals(oHistoryEvent_Service.getSoData())) {
            oHistoryEvent_Service.setSoData(soData);
            isChanged = true;
            //if (sHead == null) {
            //    sHead = "Необхідно уточнити дані";
            //}
        }
        //if (sHead == null && sUserTaskName != null) {
        //    sHead = sUserTaskName;
        //}
        //if (sHead != null && !sHead.equals(historyEventService.getsHead())) {
        //    historyEventService.setsHead(sHead);
        //    isChanged = true;
        //}
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
        if (nID_StatusType != null && !nID_StatusType.equals(oHistoryEvent_Service.getnID_StatusType())) {
            oHistoryEvent_Service.setnID_StatusType(nID_StatusType);
        }
        oHistoryEvent_Service.setsID_Order(sID_Order);

        
        Long nID_Subject = oHistoryEvent_Service.getnID_Subject();
        if (soData == null || "[]".equals(soData)) { //My journal. change status of task
            Map<String, String> mParamMessage = new HashMap<>();
            mParamMessage.put(HistoryEventMessage.SERVICE_STATE, sUserTaskName);
            mParamMessage.put(HistoryEventMessage.TASK_NUMBER, sID_Order);
            setHistoryEvent(HistoryEventType.ACTIVITY_STATUS_NEW, nID_Subject, mParamMessage, oHistoryEvent_Service.getId(), null);
        }else{ //My journal. setTaskQuestions (issue 808, 809)
            
            //!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!! TODO: Move To Interceptor!!!
            /*
            oActionEventService.createHistoryEventForTaskQuestions(
                    sToken != null ? HistoryEventType.SET_TASK_QUESTIONS : HistoryEventType.SET_TASK_ANSWERS,
                    soData, sBody, sID_Order, nID_Subject);
            */
            //!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!! TODO: Move To Interceptor!!!
            
            StringBuilder osBody = new StringBuilder(sBody) ;
            Long nID_SubjectMessageType = null;
            HistoryEventType oHistoryEventType = null;
            Boolean bQuestion = null;
                    
            if(oHistoryEvent_Service_StatusType==HistoryEvent_Service_StatusType.OPENED_REMARK_CLIENT_ANSWER){
                oHistoryEventType = HistoryEventType.SET_TASK_ANSWERS;
                bQuestion=true;
                nID_SubjectMessageType = 4L;
                isChanged=true;
                oHistoryEvent_Service.setSoData("[]");
            }else if(oHistoryEvent_Service_StatusType==HistoryEvent_Service_StatusType.OPENED_REMARK_EMPLOYEE_QUESTION){
                oHistoryEventType = HistoryEventType.SET_TASK_QUESTIONS;
                bQuestion=false;
                nID_SubjectMessageType = 5L;
            }
            
            if(nID_SubjectMessageType!=null){
                osBody.append("<br/>").append(ActionTaskService.createTable_TaskProperties(soData, bQuestion)).append("<br/>");
                
                //oActionEventService.createHistoryEventForTaskQuestions(oHistoryEventType, soData, sBody, sID_Order, nID_Subject);
                //public void createHistoryEventForTaskQuestions(HistoryEventType eventType, String soData, String sBody,
                //String sID_Order, Long nID_Subject) {
                //try {
                    Map<String, String> mParamMessage = new HashMap<>();
                    mParamMessage.put(HistoryEventMessage.TASK_NUMBER, sID_Order);
                    mParamMessage.put(HistoryEventMessage.S_BODY, sBody == null ? "" : sBody);
                    mParamMessage.put(HistoryEventMessage.TABLE_BODY, createTable_TaskProperties(soData, true));
                    setHistoryEvent(oHistoryEventType, nID_Subject, mParamMessage, oHistoryEvent_Service.getId(), null);
                    //oActionEventService.setHistoryEvent(HistoryEventType.ACTIVITY_STATUS_NEW, nID_Subject, mParamMessage);
                /*} catch (Exception e) {
                    LOG.error("FAIL:", e);
                }*/
                
                SubjectMessage oSubjectMessage = oSubjectMessageService.createSubjectMessage(sMessageHead(nID_SubjectMessageType,
                            sID_Order), osBody.toString(), nID_Subject, "", "", soData, nID_SubjectMessageType);
                    oSubjectMessage.setnID_HistoryEvent_Service(oHistoryEvent_Service.getId());
                    subjectMessagesDao.setMessage(oSubjectMessage);
                    
                    //oHistoryEvent_Service.setSoData(soData);
                    //historyEventServiceDao.updateHistoryEvent_Service(oHistoryEvent_Service);
            }
                
            
            /*
            nID;sName;sDescription
            0;ServiceNeed;Просьба добавить услугу
            1;ServiceRate;Оценка услуги
            2;ServiceFeedback;Отзыв о услуге
            3;ServiceEscalationFeedback;Отзыв о эскалации по услуге
            4;ServiceCommentClient;Клиентский уточнение-комментарий по услуге
           5;ServiceCommentEmployee;Работника замечание-комментарий по услуге
            6;ServiceRate_Indirectly;Уточняющая оценка услуги
            7;ServiceFeedback_Indirectly;Уточняющий отзыв об услуге
            8;ServiceCommentClientQuestion;Клиентский вопрос/комментарий по услуге
            9;ServiceCommentEmployeeAnswer;Работника ответ/комментарий по услуге
            */
            
        }
        if (isChanged) {
            historyEventServiceDao.updateHistoryEvent_Service(oHistoryEvent_Service);
        }
        
        return oHistoryEvent_Service;
    }
    
    
    
    
    public HistoryEvent_Service addActionStatus_Central(
            String sID_Order,
            Long nID_Subject,
            String sUserTaskName,
            //String sProcessInstanceName,
            Long nID_Service,
            Long nID_Region,
            String sID_UA,
            String soData,
            String sToken,
            String sHead,
            String sBody,
            Long nID_Proccess_Feedback,
            Long nID_Proccess_Escalation,
            Long nID_StatusType            
    ) {

        //TODO: Remove lete (for back compatibility)
        /*if(sID_Order.indexOf(DASH)<=0){
            sID_Order = "0-" + sID_Order;
            LOG.warn("Old format of parameter! (sID_Order={})",sID_Order);
        }*/
        int dash_position = sID_Order.indexOf(DASH);
        int nID_Server = dash_position != -1 ? Integer.parseInt(sID_Order.substring(0, dash_position)) : 0;
        Long nID_Order = Long.valueOf(sID_Order.substring(dash_position + 1));
        Long nID_Process = ToolLuna.getOriginalNumber(nID_Order);
        
        HistoryEvent_Service oHistoryEvent_Service = new HistoryEvent_Service();
        oHistoryEvent_Service.setnID_Task(nID_Process);
        oHistoryEvent_Service.setsUserTaskName(sUserTaskName);
         oHistoryEvent_Service.setnID_StatusType(nID_StatusType);
        oHistoryEvent_Service.setnID_Subject(nID_Subject);
        oHistoryEvent_Service.setnID_Region(nID_Region);
        oHistoryEvent_Service.setnID_Service(nID_Service);
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
        oHistoryEvent_Service = historyEventServiceDao.addHistoryEvent_Service(oHistoryEvent_Service);
        
        //get_service history event
        Map<String, String> mParamMessage = new HashMap<>();
        mParamMessage.put(HistoryEventMessage.SERVICE_NAME, sHead);//sProcessInstanceName
        mParamMessage.put(HistoryEventMessage.SERVICE_STATE, sUserTaskName);
        setHistoryEvent(HistoryEventType.GET_SERVICE, nID_Subject, mParamMessage, oHistoryEvent_Service.getId(), null);
        /*
        //My journal. setTaskQuestions (issue 808)
        oActionEventService.createHistoryEventForTaskQuestions(HistoryEventType.SET_TASK_QUESTIONS, soData, sBody,
                sID_Order, nID_Subject);//event_service.getnID_Protected()
        */
        return oHistoryEvent_Service;
    }    
    
    
}
