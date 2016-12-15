/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.igov.service.business.subject;

import org.apache.commons.lang.RandomStringUtils;
import org.apache.commons.lang.StringUtils;
import org.igov.model.core.EntityDao;
import org.igov.model.subject.*;
import org.igov.model.subject.message.*;
import org.igov.service.exception.CommonServiceException;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.List;

import static org.apache.commons.lang3.StringUtils.isEmpty;

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
    private SubjectMessageFeedbackDao subjectMessageFeedbackDao;
    @Autowired
    private SubjectMessagesDao subjectMessageDao;
    @Autowired
    private SubjectMessageFeedbackAnswerDao subjectMessageFeedbackAnswerDao;

    @Autowired
    @Qualifier("subjectMessageTypeDao")
    private EntityDao<Long, SubjectMessageType> subjectMessageTypeDao;

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
            sHead = "Відповідь/коментар робітника по заяві " + sID_Order;
        } else if (nID_SubjectMessageType == 11l) {
            sHead = "Коментар служби підтримки за результатами контакту з відповідальним посадовцем по заяві " + sID_Order;
        } else if (nID_SubjectMessageType == 10l) {
            sHead = "Відправлено листа";
        } else if (nID_SubjectMessageType == 11l) {
            sHead = "Отримано криптопакунок";
        }

        return sHead;
    }

    public SubjectMessage createSubjectMessage(String sHead, String sBody, Long nID_subject, String sMail,
            String sContacts, String sData, Long nID_subjectMessageType) throws CommonServiceException {
        SubjectContact subjectContact = null;
        Subject subject = new Subject();
        SubjectMessage message = null;

        if (sMail != null && !sMail.isEmpty()) {
            LOG.info("(createSubjectMessage: sMail{}, nID_subject{}) ", sMail, nID_subject);
            if (nID_subject != null) {
                LOG.info("(createSubjectMessage: nID_subject{}) ", nID_subject);

                try {
                    subjectContact = syncMail(sMail, nID_subject);
                } catch (Exception e) {
                    LOG.warn("Error occured while syncing mail {}", e.getMessage());
                }
                if (subjectContact != null) {
                    LOG.info("(syncMail with nID_Subject after calling method: SubjectContact ID{},nID_Subject{}, ContactType{}, Date{}, sValue{})",
                            subjectContact.getId(), subjectContact.getSubject().getId(), subjectContact.getSubjectContactType().getsName_EN(),
                            subjectContact.getsDate(), subjectContact.getsValue());
                } else {
                    LOG.info("(syncMail with nID_Subject after calling method: SubjectContact null)");
                }

            }
            if (nID_subject == null) {
                LOG.info("(createSubjectMessage: nID_subject{}) ", nID_subject);

                subjectContact = syncMail(sMail, subject);

                if (subjectContact != null) {
                    LOG.info("(syncMail without nID_Subject after calling method: SubjectContact ID{},nID_Subject{}, ContactType{}, Date{}, sValue{})",
                            subjectContact.getId(), subjectContact.getSubject().getId(), subjectContact.getSubjectContactType().getsName_EN(),
                            subjectContact.getsDate(), subjectContact.getsValue());
                } else {
                    LOG.info("(syncMail without nID_Subject after calling method: subjectContact null)");
                }

                if (subject != null) {
                    LOG.info("(syncMail without nID_Subject after calling method: oSubject ID{},sID{}, sLabel{}, sLabaleShort{})",
                            subject.getId(), subject.getsID(), subject.getsLabel(), subject.getsLabelShort());
                } else {
                    LOG.info("(syncMail without nID_Subject after calling method: subject null)");
                }

            }
        }

        message = new SubjectMessage();
        message.setHead(sHead);
        message.setBody(sBody == null ? "" : sBody);

        LOG.info("(createSubjectMessage: subject Id{})", subject.getId());

        message.setId_subject((nID_subject == null) ? ((subject.getId() == null) ? 0 : subject.getId()) : nID_subject);
        LOG.info("(createSubjectMessage: message subject Id{})", message.getId_subject());
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
        Long messageID = message.getId();
        /*
        LOG.info("Created message id={}, Mail={}", messageID, message.getMail());
        LOG.info("Created message id={}, Contacts={}", messageID, message.getContacts());
        LOG.info("Created message id={}, Data={}", messageID, message.getData());
        LOG.info("Created message id={}, Date={}", messageID, message.getDate());
        LOG.info("Created message id={}, Head={}", messageID, message.getHead());
        LOG.info("Created message id={}, Body={}", messageID, message.getBody());
        LOG.info("Created message id={}, Id_subject={}", messageID, message.getId_subject());
        LOG.info("Created message id={}, Id_subject={}", messageID, message.getsID_DataLink());
        LOG.info("Created message id={}, ID_HistoryEvent_Service={}", messageID, message.getnID_HistoryEvent_Service());
         */
        return message;
    }

    /*issue1215 Перегружен ради добавлениия нового параметра (sSubjectInfo) в /setTaskQuestions, чтобы не рушить
    существующие сервисыБ которые используют этот метод  */
    public SubjectMessage createSubjectMessage(String sHead, String sBody, Long nID_subject, String sMail,
            String sContacts, String sData, Long nID_subjectMessageType, String sSubjectInfo) throws CommonServiceException {
        SubjectContact subjectContact = null;
        Subject subject = new Subject();
        SubjectMessage message = null;

        if (sMail != null && !sMail.isEmpty()) {
            LOG.info("(createSubjectMessage: sMail{}, nID_subject{}) ", sMail, nID_subject);
            if (nID_subject != null) {
                LOG.info("(createSubjectMessage: nID_subject{}) ", nID_subject);

                subjectContact = syncMail(sMail, nID_subject);
                if (subjectContact != null) {
                    LOG.info("(syncMail with nID_Subject after calling method: SubjectContact ID{},nID_Subject{}, ContactType{}, Date{}, sValue{})",
                            subjectContact.getId(), subjectContact.getSubject().getId(), subjectContact.getSubjectContactType().getsName_EN(),
                            subjectContact.getsDate(), subjectContact.getsValue());
                } else {
                    LOG.info("(syncMail with nID_Subject after calling method: SubjectContact null)");
                }

            }
            if (nID_subject == null) {
                LOG.info("(createSubjectMessage: nID_subject{}) ", nID_subject);

                subjectContact = syncMail(sMail, subject);

                if (subjectContact != null) {
                    LOG.info("(syncMail without nID_Subject after calling method: SubjectContact ID{},nID_Subject{}, ContactType{}, Date{}, sValue{})",
                            subjectContact.getId(), subjectContact.getSubject().getId(), subjectContact.getSubjectContactType().getsName_EN(),
                            subjectContact.getsDate(), subjectContact.getsValue());
                } else {
                    LOG.info("(syncMail without nID_Subject after calling method: subjectContact null)");
                }

                if (subject != null) {
                    LOG.info("(syncMail without nID_Subject after calling method: oSubject ID{},sID{}, sLabel{}, sLabaleShort{})",
                            subject.getId(), subject.getsID(), subject.getsLabel(), subject.getsLabelShort());
                } else {
                    LOG.info("(syncMail without nID_Subject after calling method: subject null)");
                }

            }
        }

        message = new SubjectMessage();
        message.setHead(sHead);
        message.setBody(sBody == null ? "" : sBody);

        LOG.info("(createSubjectMessage: subject Id{})", subject.getId());

        message.setId_subject((nID_subject == null) ? ((subject.getId() == null) ? 0 : subject.getId()) : nID_subject);
        LOG.info("(createSubjectMessage: message subject Id{})", message.getId_subject());
        SubjectContact oSubjectContact = (subjectContact == null) ? null : subjectContact;
        message.setoMail(oSubjectContact);
        //if(oSubjectContact==null){
        message.setMail(sMail == null ? "" : sMail);
        //}
        message.setContacts((sContacts == null) ? "" : sContacts);
        message.setData((sData == null) ? "" : sData);
        message.setDate(new DateTime());
        message.setsSubjectInfo((sSubjectInfo == null) ? "" : sSubjectInfo);
        LOG.info("(createSubjectMessage: message sSubjectInfo{})", message.getsSubjectInfo());
        if (nID_subjectMessageType != null) {
            SubjectMessageType subjectMessageType = subjectMessageTypeDao.findByIdExpected(nID_subjectMessageType);
            message.setSubjectMessageType(subjectMessageType);
        }

        return message;
    }

    public SubjectMessageFeedback setSubjectMessageFeedback(String sID_Source, String sAuthorFIO, String sMail,
            String sHead, String sBody, String sPlace, String sEmployeeFIO,
            Long nID_Rate, Long nID_Service, String sAnswer, Long nId,
            Long nID_Subject, String sID_Order) {

        SubjectMessageFeedback messageFeedback;
        SubjectMessage subjectMessage;
        if (nId == null) {
            LOG.info("!!!nId is null sID_Order = " + sID_Order);
            messageFeedback = new SubjectMessageFeedback();
            messageFeedback.setsID_Source(sID_Source);
            messageFeedback.setsAuthorFIO(sAuthorFIO);
            messageFeedback.setsMail(sMail);
            messageFeedback.getoSubjectMessage().setHead(sHead);
            messageFeedback.getoSubjectMessage().setBody(sBody);
            messageFeedback.setsPlace(sPlace);
            messageFeedback.setsEmployeeFIO(sEmployeeFIO);
            messageFeedback.setnID_Rate(nID_Rate);
            messageFeedback.setnID_Service(nID_Service);
            messageFeedback.setsID_Token(RandomStringUtils.randomAlphanumeric(20));
            messageFeedback.setsAnswer(sAnswer);
            if(sID_Order != null){
                messageFeedback.setsID_Order(sID_Order);
            }
            if (!isEmpty(sHead) || !isEmpty(sBody)) {
                LOG.info("!!!sHead = " + sHead);
                SubjectMessageType subjectMessageType = subjectMessageTypeDao.findByIdExpected(2L);
                subjectMessage = new SubjectMessage();
                subjectMessage.setHead(sHead);
                subjectMessage.setBody(sBody);
                subjectMessage.setSubjectMessageType(subjectMessageType);
                subjectMessage.setDate(new DateTime());
                subjectMessage.setsSubjectInfo(sAuthorFIO);
                subjectMessage.setMail(sMail);
                subjectMessage.setId_subject(nID_Subject);
                subjectMessage = subjectMessageDao.saveOrUpdate(subjectMessage);
                messageFeedback.setoSubjectMessage(subjectMessage);
            }
            return subjectMessageFeedbackDao.save(messageFeedback);
        } else {
            messageFeedback = subjectMessageFeedbackDao.getSubjectMessageFeedbackById(nId);
            /*if(sID_Order != null){
                messageFeedback.setsID_Order(sID_Order);
            }*/
            LOG.info("!!!nId = " + nId + " sAnswer = " + sAnswer+ " sID_Order = " + messageFeedback.getsID_Order());
            if (StringUtils.isEmpty(sAnswer)) {
                messageFeedback.setsID_Source(sID_Source);
                messageFeedback.setsAuthorFIO(sAuthorFIO);
                messageFeedback.setsMail(sMail);
                messageFeedback.getoSubjectMessage().setHead(sHead);
                messageFeedback.getoSubjectMessage().setBody(sBody);
                messageFeedback.setsPlace(sPlace);
                messageFeedback.setsEmployeeFIO(sEmployeeFIO);
                messageFeedback.setnID_Rate(nID_Rate);
                messageFeedback.setnID_Service(nID_Service);
                messageFeedback.setsAnswer(sAnswer);
                subjectMessage = messageFeedback.getoSubjectMessage();
                if (subjectMessage == null) {
                    SubjectMessageType subjectMessageType = subjectMessageTypeDao.findByIdExpected(2L);
                    subjectMessage = new SubjectMessage();
                    subjectMessage.setHead(sHead == null ? "" : sHead);
                    subjectMessage.setBody(sBody == null ? "" : sBody);
                    subjectMessage.setSubjectMessageType(subjectMessageType);
                    subjectMessage.setMail(sMail == null ? "" : sMail);
                    subjectMessage.setId_subject(nID_Subject);
                }
                if (sHead != null) {
                    subjectMessage.setHead(sHead);
                }
                if (sBody != null) {
                    subjectMessage.setBody(sBody);
                }
                subjectMessage.setDate(new DateTime());
                subjectMessage.setsSubjectInfo(sAuthorFIO);
                if (sMail != null) {
                    subjectMessage.setMail(sMail);
//                    createSubjectContact(sMail, subjectMessage.getoSubject());
                    LOG.info("1TEST_sMail: " + sMail);
                    subjectContactDao.saveOrUpdate(createSubjectContact(sMail, subjectMessage.getoSubject()));
                    LOG.info("1sMail: " + subjectMessage.getMail());
                    LOG.info("2oMail: " + subjectMessage.getoMail().getsValue());
                }
                if (nID_Subject != null) {
                    subjectMessage.setId_subject(nID_Subject);
                }
                subjectMessage = subjectMessageDao.saveOrUpdate(subjectMessage);
                messageFeedback.setoSubjectMessage(subjectMessage);
            }
            return subjectMessageFeedbackDao.update(messageFeedback);
        }

    }

    public SubjectMessageFeedbackAnswer setSubjectMessageFeedbackAnswer(Long nID_SubjectMessageFeedback, String sBody,
            Long nID_Subject, Boolean bSelf, String sAuthorFIO) {

        SubjectMessageFeedbackAnswer answer = new SubjectMessageFeedbackAnswer();
        SubjectMessageFeedback messageFeedback = subjectMessageFeedbackDao
                .getSubjectMessageFeedbackById(nID_SubjectMessageFeedback);

        SubjectMessage subjectMessage = new SubjectMessage();
        subjectMessage.setBody(sBody);
        subjectMessage.setHead("Ответ на feedback");
        SubjectMessageType subjectMessageType = subjectMessageTypeDao.findByIdExpected(11L);
        subjectMessage.setSubjectMessageType(subjectMessageType);
        subjectMessage.setDate(new DateTime());
        subjectMessage.setId_subject(nID_Subject);
        subjectMessage.setsSubjectInfo(sAuthorFIO);

        answer.setoSubjectMessageFeedback(messageFeedback);
        answer.setbSelf(bSelf);
        answer.setoSubjectMessage(subjectMessage);

        return subjectMessageFeedbackAnswerDao.save(answer);
    }

    public SubjectMessageFeedback getSubjectMessageFeedbackById(Long nId) {
        return subjectMessageFeedbackDao.getSubjectMessageFeedbackById(nId);
    }

    public List<SubjectMessageFeedback> getAllSubjectMessageFeedbackBynID_Service(Long nID_service) {
        return subjectMessageFeedbackDao.getAllSubjectMessageFeedbackBynID_Service(nID_service);
    }

    public List<SubjectMessageFeedback> getAllSubjectMessageFeedback_Filtered(Long nID_service, Long nID__LessThen_Filter, Integer nRowsMax) {
        return subjectMessageFeedbackDao.getAllSubjectMessageFeedback_Filtered(nID_service, nID__LessThen_Filter, nRowsMax);
    }

    //при параметре nID_Subject == null
    private SubjectContact syncMail(String sMail, Subject oSubject) {

        Subject subject = this.getSubject(null, sMail);
        List<SubjectContact> list_contacts = subjectContactDao.findContacts(subject);
        List<SubjectContact> list_smail = subjectContactDao.findAllBy("sValue", sMail);
        SubjectContact res = this.contactsSinchronization(list_smail, list_contacts, sMail, subject);
        if (subject != null) {
            oSubject.setId(subject.getId());
            oSubject.setsID(subject.getsID());
            oSubject.setsLabel(subject.getsLabel());
            oSubject.setsLabelShort(subject.getsLabelShort());

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
                    if (!aTask.isEmpty()) {//when process is not complete
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
    private SubjectContact syncMail(String sMail, Long nID_Subject) throws CommonServiceException {

        //находим Subject по nID_Subject
        Subject subject = this.getSubject(nID_Subject, sMail);
        SubjectHuman subjectHuman = this.getSubjectHuman(subject);
        List<SubjectContact> list_contacts = subjectContactDao.findContacts(subject);
        List<SubjectContact> list_mail = subjectContactDao.findAllBy("sValue", sMail);
        SubjectContact res = this.contactsSinchronization(list_mail, list_contacts, sMail, subject);
        if (subjectHuman != null) {
            subjectHuman.setDefaultEmail(res);
            subjectHumanDao.saveOrUpdateHuman(subjectHuman);
        }

        return res;
    }

    private Subject getSubject(Long nID_Subject, String sMail) {
        Subject subject = null;
        if (nID_Subject == null) {
            String sID = SubjectHuman.getSubjectId(SubjectHumanIdType.Email, sMail);
            subject = subjectDao.getSubject(sID);

            List<SubjectContact> list_contacts = subjectContactDao.findAllBy("sValue", sMail);

            if (list_contacts.size() != 0) {
                for (SubjectContact contact : list_contacts) {
                    if (contact.getsValue().equals(sMail)) {
                        Subject subj = contact.getSubject();
                        if (!subj.getsID().startsWith("_")) {
                            subject = subj;
                        }
                        break;
                    }
                }

            }
            if (subject == null) {
                Subject subject_time = new Subject();
                subject_time.setsID(sID);
                subjectDao.saveOrUpdateSubject(subject_time);
                subject = subjectDao.getSubject(sID);

            }

        } else {
            subject = subjectDao.getSubject(nID_Subject);
        }

        if (subject != null) {
            LOG.info("(syncMail with nID_Subject: oSubject ID{},sID{}, sLabel{}, sLabaleShort{})",
                    subject.getId(), subject.getsID(), subject.getsLabel(), subject.getsLabelShort());
        } else {
            LOG.info("(syncMail with nID_Subject: subject null)");
        }

        return subject;
    }

    private SubjectHuman getSubjectHuman(Subject subject) {
        SubjectHuman subjectHuman = null;
        //по subject найдем запись относящуюся к SubjectHuman
        if (subject != null) {
            subjectHuman = subjectHumanDao.findByExpected("oSubject", subject);
        }
        if (subjectHuman != null) {
            LOG.info("(syncMail with nID_Subject: sINN{}, id {}, default_email{})",
                    subjectHuman.getsINN(), subjectHuman.getoSubject().getId(), subjectHuman.getDefaultEmail());
        }

        return subjectHuman;

    }

    private SubjectContact contactsSinchronization(List<SubjectContact> list_mail, List<SubjectContact> list_contacts, String sMail, Subject subject) {
        SubjectContact res = null;
        boolean bIsContact = this.isContactByMail(list_contacts, sMail);
        boolean bIsDataBase = this.isContactByMail(list_mail, sMail);
        if (bIsContact) {
            res = this.updateContact(subject, sMail);
        } else if (bIsDataBase) {
            res = this.updateContact(subject, sMail);
        } else {
            res = this.createSubjectContact(sMail, subject);
        }

        return res;
    }

    private SubjectContact createSubjectContact(String sMail, Subject subject) {
        SubjectContact contact = new SubjectContact();
        contact.setSubject(subject);
        contact.setSubjectContactType(subjectContactTypeDao.getEmailType());
        contact.setsDate();
        contact.setsValue(sMail);
        subjectContactDao.saveOrUpdate(contact);
        SubjectContact res = subjectContactDao.findByExpected("sValue", sMail);

        return res;
    }

    private SubjectContact updateContact(Subject subject, String sMail) {

        SubjectContact res = null;
        try {
            SubjectContact contact = subjectContactDao.findByExpected("sValue", sMail);
            contact.setSubject(subject);
            contact.setsDate();
            subjectContactDao.saveOrUpdate(contact);
            res = subjectContactDao.findByIdExpected(contact.getId());
        } catch (Exception ex) {
            LOG.warn("(Fail update contact {})", ex.getMessage());
        }

        return res;
    }

    private boolean isContactByMail(List<SubjectContact> list, String sMail) {

        for (SubjectContact contact : list) {
            if (contact.getsValue().equals(sMail)) {
                return true;
            }

        }

        return false;
    }
}
