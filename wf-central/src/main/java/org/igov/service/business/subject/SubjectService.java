/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.igov.service.business.subject;

import java.util.List;
import org.igov.model.subject.Subject;
import org.igov.model.subject.SubjectContact;
import org.igov.model.subject.SubjectContactDao;
import org.igov.model.subject.SubjectContactTypeDao;
import org.igov.model.subject.SubjectDao;
import org.igov.model.subject.SubjectHuman;
import org.igov.model.subject.SubjectHumanDao;
import org.igov.model.subject.SubjectHumanIdType;
import org.igov.model.subject.organ.SubjectOrganDao;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

/**
 *
 * @author Belyavtsev Vladimir Vladimirovich (BW)
 */
public class SubjectService {
    
     private static final Logger LOG = LoggerFactory.getLogger(SubjectService.class);

    
    @Autowired
    private SubjectDao subjectDao;
    @Autowired
    private SubjectOrganDao subjectOrganDao;
    @Autowired
    private SubjectContactDao subjectContactDao;
    @Autowired
    private SubjectHumanDao subjectHumanDao;
    @Autowired
    private SubjectContactTypeDao subjectContactTypeDao;
   
    
    public Subject syncSubject_Upload(String sID_Subject_Upload) {
        Subject subject_Upload = subjectDao.getSubject(sID_Subject_Upload);
        if (subject_Upload == null) {
            subject_Upload = subjectOrganDao.setSubjectOrgan(sID_Subject_Upload).getoSubject();
        }
        return subject_Upload;
    }
    
  public SubjectContact syncContactsService(String snID_Subject, String sMail)
    {
        LOG.info("(Вход в syncContactsService snID_Subject {}, sMail {})", snID_Subject, sMail);
        SubjectContact oSubjectContact = new SubjectContact();
        
        Long nID_Subject = convertStringToLong(snID_Subject);
        LOG.info("(before getSubject nID_Subject {})", nID_Subject);
        Subject subject = getSubject(nID_Subject, sMail);
        SubjectHuman oSubjectHuman = null;
       if(subject != null)
        oSubjectHuman = getSubjectHuman(subject);
       
       List<SubjectContact> list_contacts = subjectContactDao.findContacts(subject);
       LOG.info("(получаем список контактов субьекта)");
       List<SubjectContact> list_mail = subjectContactDao.findAllBy("sValue", sMail);
       LOG.info("(получаем список контактов по sMail {})", sMail);
       
       oSubjectContact = this.synchronizationContacts(list_contacts, list_mail, subject, sMail);
       
        if(oSubjectHuman != null)
       {
           oSubjectHuman.setDefaultEmail(oSubjectContact);
           subjectHumanDao.saveOrUpdateHuman(oSubjectHuman);
       }
       return oSubjectContact;
    }
    private SubjectContact synchronizationContacts(List<SubjectContact> list_contacts, List<SubjectContact> list_mail, Subject subject, String sMail)
    {
       SubjectContact res = null;
       boolean bIsContact = this.isContactByMail(list_contacts, sMail);
       boolean bIsDataBase = this.isContactByMail(list_mail, sMail);
       if(bIsContact)
       {
            res = this.updateContact(subject, sMail);
            LOG.info("(апдейтим контакт в списке контактов субьекта)");
       }
       else
       {
           if(bIsDataBase)
           {
              res = this.updateContact(subject, sMail);
              LOG.info("(апдейтим контакт в списке контактов базы, переопределяя субьекта)");
           }
           else
           {
              res = this.createSubjectContact(sMail, subject);
              LOG.info("(создаем контакт)");

           }
       }
       
       return res;  
    }
    private SubjectHuman getSubjectHuman(Subject subject)
    {
        return subjectHumanDao.findByExpected("oSubject", subject);
    }
      private SubjectContact createSubjectContact(String sMail, Subject subject)
    {
         SubjectContact contact = new SubjectContact();
         contact.setSubject(subject);
         contact.setSubjectContactType(subjectContactTypeDao.getEmailType());
         contact.setsDate();
         contact.setsValue(sMail);
         subjectContactDao.saveOrUpdate(contact);
         SubjectContact res = subjectContactDao.findByExpected("sValue", sMail);
         
         LOG.info("(создаем контакт subject Id {}, subject Label {}, subjectContact sValue {})", subject.getsID(), subject.getsLabel(), contact.getsValue());
         
         return res;
    }
    private SubjectContact updateContact(Subject subject, String sMail)
    {
        
         SubjectContact res = null;
       try
       {
         SubjectContact contact = subjectContactDao.findByExpected("sValue", sMail);
         contact.setSubject(subject);
         contact.setsDate();
         subjectContactDao.saveOrUpdate(contact);
         res = subjectContactDao.findByIdExpected(contact.getId());
         
         LOG.info("(апдейт контакта subject Id {}, subject Label {}, subjectContact sValue {})", subject.getsID(), subject.getsLabel(), contact.getsValue());
       }
       catch(Exception ex)
       {
          LOG.warn("(Fail update contact {})", ex.getMessage());
       }
         
         return res;
    }
    private boolean isContactByMail(List<SubjectContact> list, String sMail )
    {
        
         for(SubjectContact contact : list)
         {
             if(contact.getsValue().equals(sMail))
                 return true; 
             
         }
         
         return false;
    }
    private Subject getSubject(Long nID_Subject, String sMail)
    {
        Subject subject = null;
        if(nID_Subject == null)
        {
            String sID = SubjectHuman.getSubjectId(SubjectHumanIdType.Email, sMail);
            
            LOG.info("(sID {})", sID);
            subject = subjectDao.getSubject(sID);
            if(subject == null)
            {
               subject = new Subject();
               subject.setsID(sID);
               subjectDao.saveOrUpdateSubject(subject);
               subject = subjectDao.getSubject(sID);
               LOG.info("(Создаем subject Id {}, sID {})", subject.getId(), subject.getsID());
            }
        }
        else
        {
            LOG.info("(subject Id {})", nID_Subject);
            subject = subjectDao.getSubject(nID_Subject);
            LOG.info("(Извлекаем subject Id {}, sID {}, Label {}, shortLabel {})", subject.getId(), subject.getsID(), subject.getsLabel(), subject.getsLabelShort());
        }
        
        return subject;
    }
    private Long convertStringToLong(String snID)
    {
        Long nID = null;
       try
       {
           nID = Long.valueOf(snID);
           LOG.info("(convertStringToLong nID {}, snID {})", nID, snID);
       }
       catch(Exception ex)
       {
          LOG.warn("(Exception for converting string to long {})", ex.getMessage());
       }
       
       return nID;
    } 
    
}
