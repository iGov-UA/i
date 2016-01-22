/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.igov.service.business.action;

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

import static org.igov.service.business.action.task.core.ActionTaskService.createTable_TaskProperties;

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
        try {
            values.put(HistoryEventMessage.FIO, sFIO);
            values.put(HistoryEventMessage.TELEPHONE, sPhone);
            values.put(HistoryEventMessage.EMAIL, sEmail);
            values.put(HistoryEventMessage.DAYS, "" + TimeUnit.MILLISECONDS.toDays(nMs));

            Document oDocument = documentDao.getDocument(documentId);
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
                    eventMessage, eventMessage);
        } catch (IOException oException) {
            LOG.error("error: {}, during creating HistoryEvent", oException.getMessage());
            LOG.trace("FAIL:", oException);
        }
    }

    public void createHistoryEvent(HistoryEventType eventType,
            Long nID_Subject, String sSubjectName_Upload, Long nID_Document,
            Document document) {
        Map<String, String> values = new HashMap<>();
        try {
            Document oDocument = document == null ? documentDao
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
                    eventMessage, eventMessage);
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
                true, mParam);
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
            Long nID_Subject, Map<String, String> mParamMessage) {
        try {
            String eventMessage = HistoryEventMessage.createJournalMessage(
                    eventType, mParamMessage);
            historyEventDao.setHistoryEvent(nID_Subject, eventType.getnID(),
                    eventMessage, eventMessage);
        } catch (IOException e) {
            LOG.error("error: {}, during creating HistoryEvent", e.getMessage());
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

    public void createHistoryEventForTaskQuestions(HistoryEventType eventType, String soData, String sBody,
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
        
    }

}
