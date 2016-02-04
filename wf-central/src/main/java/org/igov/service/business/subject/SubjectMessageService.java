/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.igov.service.business.subject;

import org.igov.model.core.EntityDao;
import org.igov.model.subject.*;
import org.igov.model.subject.message.SubjectMessage;
import org.igov.model.subject.message.SubjectMessageType;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 *
 * @author Belyavtsev Vladimir Vladimirovich (BW)
 */
@Service
public class SubjectMessageService {
    
    private static final Logger LOG = LoggerFactory.getLogger(SubjectMessageService.class);

    @Autowired
    private SubjectHumanDao subjectHumanDao;
    @Autowired
    private SubjectContactDao subjectContactDao;
    @Autowired
    private SubjectDao subjectDao;
    @Autowired
    private SubjectContactTypeDao subjectContactTypeDao;

    @Autowired
    @Qualifier("subjectMessageTypeDao")
    private EntityDao<SubjectMessageType> subjectMessageTypeDao;

    public static String sMessageHead(Long nID_SubjectMessageType, String sID_Order) {
        String sHead = "";
        if (nID_SubjectMessageType == -1l) {
            sHead = "";
        } else if (nID_SubjectMessageType == 0l) {
            sHead = "Прохання добавити послугу ";
        } else if (nID_SubjectMessageType == 1l) {
            sHead = "Оцінка по відпрацьованій послузі за заявою " + sID_Order;
        } else if (nID_SubjectMessageType == 2l) {
            sHead = "Відгук по відпрацьованій послузі за заявою " + sID_Order;
        } else if (nID_SubjectMessageType == 3l) {
            sHead = "Коментар по ескалації послуги за заявою " + sID_Order;
        } else if (nID_SubjectMessageType == 4l) {
            sHead = "Відповідь на зауваження по заяві " + sID_Order;
        } else if (nID_SubjectMessageType == 5l) {
            sHead = "Зауваження по заяві " + sID_Order;
        } else if (nID_SubjectMessageType == 6l) {
            sHead = "Уточнююча оцінка по відпрацьованій послузі за заявою " + sID_Order;
        } else if (nID_SubjectMessageType == 7l) {
            sHead = "Уточнюючий коментар клієнта по заяві " + sID_Order;
        } else if (nID_SubjectMessageType == 8l) {
            sHead = "Запитання/коментар клієнта по заяві " + sID_Order;
        } else if (nID_SubjectMessageType == 9l) {
            sHead = "Выдповідь/коментар роюітника по заяві " + sID_Order;
        }

        return sHead;
    }

    public SubjectMessage createSubjectMessage(String sHead, String sBody, Long nID_subject, String sMail,
                                               String sContacts, String sData, Long nID_subjectMessageType) {
        SubjectContact subjectContact = null;
        Subject subject = new Subject();

        if(sMail != null && !sMail.isEmpty())
        {
            if (nID_subject != null)
                subjectContact = syncMail(sMail, nID_subject);
            if (nID_subject == null)
                subjectContact = syncMail(sMail, subject);
        }

        SubjectMessage message = new SubjectMessage();
        message.setHead(sHead);
        message.setBody(sBody == null ? "" : sBody);
        message.setId_subject((nID_subject == null) ? ((subject.getId() == null) ? 0 : subject.getId()) : nID_subject);
        SubjectContact oSubjectContact = (subjectContact == null) ? null : subjectContact;
        message.setoMail(oSubjectContact);
        //if(oSubjectContact==null){
            message.setMail(sMail == null ? "" : sMail);
        //}
        message.setContacts((sContacts == null) ? "" : sContacts);
        message.setData((sData == null) ? "" : sData);
        message.setDate(new DateTime());
        if (nID_subjectMessageType != null) {
            SubjectMessageType subjectMessageType = subjectMessageTypeDao.findByIdExpected(nID_subjectMessageType);
            message.setSubjectMessageType(subjectMessageType);
        }
        return message;
    }

    //при параметре nID_Subject == null
    private SubjectContact syncMail(String sMail, Subject oSubject) {
        SubjectContact res = null;
        SubjectHuman oSubjectHuman = subjectHumanDao.getSubjectHuman(SubjectHumanIdType.Email, sMail);

        Subject subject = (oSubjectHuman != null) ? oSubjectHuman.getoSubject() : null;
        if (subject != null) {
            oSubject.setId(subject.getId());
            oSubject.setsID(subject.getsID());
            oSubject.setsLabel(subject.getsLabel());
            oSubject.setsLabelShort(subject.getsLabelShort());

            res = subjectContactDao.findByExpected("sValue", sMail);
            if (res != null) {
                res.setSubject(subject);
                res.setsDate();

                subjectContactDao.saveOrUpdate(res);
            }


        }

        return res;
    }
    
    //private void checkRate(String sID_Order, Long nID_Protected, Integer nID_Server, String sID_Rate)
    /*private void setServiceRate(String sID_Order, String sID_Rate)
            throws CommonServiceException {

        //if (nID_Protected == null && sID_Order == null && nID_Server == null && sID_Rate == null) {
        if (sID_Order == null || sID_Rate == null) {
            LOG.warn("Parameter(s) is absent! {sID_Order}, {sID_Rate}", sID_Order, sID_Rate);
            throw new CommonServiceException(404, "Incorrect value of sID_Rate! It isn't number.");
            //return;
        }
        if (!sID_Order.contains("-")) {
            LOG.warn("Incorrect parameter! {sID_Order}", sID_Order);
            throw new CommonServiceException(404, "Incorrect parameter! {sID_Order=" + sID_Order + "}");
        }
        
        if (sID_Rate != null && !sID_Rate.trim().equals("")) {
            Integer nRate;
            try {
                nRate = Integer.valueOf(sID_Rate);
            } catch (NumberFormatException ex) {
                LOG.warn("incorrect param sID_Rate (not a number): " + sID_Rate);
                throw new CommonServiceException(404, "Incorrect value of sID_Rate! It isn't number.");
            }
            if (nRate < 1 || nRate > 5) {
                LOG.warn("incorrect param sID_Rate (not in range[1..5]): " + sID_Rate);
                throw new CommonServiceException(404, "Incorrect value of sID_Rate! It is too short or too long number");
            }
            try {
                HistoryEvent_Service oHistoryEvent_Service;
                LOG.info("sID_Order: " + sID_Order + ", nRate: " + nRate);
                oHistoryEvent_Service = historyEventServiceDao.getOrgerByID(sID_Order);
                oHistoryEvent_Service.setnRate(nRate);
                LOG.info(String.format("set rate=%s to the task=%s, nID_Protected=%s", nRate,
                oHistoryEvent_Service.getnID_Task(), oHistoryEvent_Service.getnID_Protected()));
                historyEventServiceDao.saveOrUpdate(oHistoryEvent_Service);
                if (oHistoryEvent_Service.getnID_Proccess_Feedback() != null) {//issue 1006
                    String snID_Process = "" + oHistoryEvent_Service.getnID_Proccess_Feedback();
                    LOG.info(String.format("set rate=%s to the nID_Proccess_Feedback=%s", nRate, snID_Process));
                    List<Task> aTask = taskService.createTaskQuery().processInstanceId(snID_Process).list();
                    if (aTask.size() > 0) {//when process is not complete
                        runtimeService.setVariable(snID_Process, "nID_Rate", nRate);
                        LOG.info("Found " + aTask.size() + " tasks by nID_Proccess_Feedback...");
                        for (Task oTask : aTask) {
                            LOG.info("oTask;getName=" + oTask.getName() + "|getDescription=" + oTask.getDescription() + "|getId=" + oTask.getId());
                            taskService.setVariable(oTask.getId(), "nID_Rate", nRate);
                        }
                    }
                }
                LOG.info(String.format("set rate=%s to the task=%s, nID_Protected=%s Success!",
                        nRate, oHistoryEvent_Service.getnID_Task(), oHistoryEvent_Service.getnID_Protected()));
            } catch (CRCInvalidException e) {
                LOG.error(""+e.getMessage(), e);
            } catch (Exception e) {
                LOG.error("ex!", e);
            }
        }
    }*/

    //при параметре nID_Subject != null
    private SubjectContact syncMail(String sMail, Long nID_Subject) {

        Subject subject = subjectDao.getSubject(nID_Subject);
        SubjectHuman subjectHuman = null;
       try
       {
         subjectHuman = subjectHumanDao.findByExpected("oSubject", subject);
       }
       catch(Exception e)
       {
          LOG.error(e.getMessage(), e);
       }

       
        List<SubjectContact> subjectContacts = subjectContactDao.findContacts(subject);

        SubjectContact res = null;

        for (SubjectContact subjectContact : subjectContacts) {
            SubjectContactType sct = subjectContact.getSubjectContactType();
            if (sct.getsName_EN().equals("Email")) {
                if (subjectContact.getsValue().equals(sMail)) {
                    res = subjectContact;
                    res.setSubject(subject);
                    res.setsDate();
                    subjectContactDao.saveOrUpdate(res);
                    break;
                }

            }
        }

        if (res == null) {
            res = new SubjectContact();
            SubjectContactType subjectContactType = subjectContactTypeDao.getEmailType();
            res.setSubject(subject);
            res.setSubjectContactType(subjectContactType);
            res.setsValue(sMail);
            res.setsDate();
            subjectContactDao.saveOrUpdate(res);
           if(subjectHuman != null)
           {
            subjectHuman.setDefaultEmail(res);
            //subjectHuman.setSubjectHumanIdType(SubjectHumanIdType.Email);
            subjectHumanDao.saveOrUpdateHuman(subjectHuman);
           }
        }

        return res;
    }
}
