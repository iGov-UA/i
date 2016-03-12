/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.igov.service.business.subject;

import java.util.ArrayList;
import java.util.List;
import org.igov.model.subject.Subject;
import org.igov.model.subject.SubjectContact;
import org.igov.model.subject.SubjectContactDao;
import org.igov.model.subject.SubjectContactType;
import org.igov.model.subject.SubjectContactTypeDao;
import org.igov.model.subject.SubjectDao;
import org.igov.model.subject.SubjectHuman;
import org.igov.model.subject.SubjectHumanDao;
import org.igov.model.subject.SubjectHumanIdType;
import org.igov.model.subject.organ.SubjectOrganDao;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 *
 * @author Belyavtsev Vladimir Vladimirovich (BW)
 */
@Service
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
    
     public List<SubjectContact> syncContactsService(String snID_Subject, String sMail, String sPhone)
    {
        LOG.info("(Вход в syncContactsService snID_Subject {}, sMail {})", snID_Subject, sMail);
        List<SubjectContact> listContacts = new ArrayList();
        
        Long nID_Subject = convertStringToLong(snID_Subject);
        LOG.info("(before getSubject nID_Subject {})", nID_Subject);
        Subject subject = null;
        Subject subjectsID_Mail = null;
        Subject subjectsID_Phone = null;
        SubjectHuman oSubjectHuman = null;
        if(nID_Subject != null)
        {
            subject = this.getSubjectObjectBynID(nID_Subject);
            if(subject != null)
              oSubjectHuman = getSubjectHuman(subject);
        }
        else
        {
           if(sMail != null)
           {
              String sID = this.getsID(sMail, null);
              subjectsID_Mail = this.getSubjectObject(sID, sMail);
           }
           if(sPhone != null)
           {
              String sID = this.getsID(null, sPhone);
              subjectsID_Phone = this.getSubjectObject(sID, sPhone);
           }  
            
             if(sMail != null && subjectsID_Mail == null)
             {
             if(subjectsID_Phone != null && !subjectsID_Phone.getsID().startsWith("_"))
             {
                 subjectsID_Mail = subjectsID_Phone;
             }
             else
             {
              String sID = this.getsID(sMail, null);
              subjectsID_Mail = this.createSubject(sID);
             }
             }
             if(sPhone != null && subjectsID_Phone == null)
             {
             if(subjectsID_Mail != null && !subjectsID_Mail.getsID().startsWith("_"))
             {
                 subjectsID_Phone = subjectsID_Mail;
             }
             else
             {
              String sID = this.getsID(null, sPhone);
              subjectsID_Phone = this.createSubject(sID);
             }
             }
           
           
        }
       
       
       if(subject != null)
       {
          List<SubjectContact> list_contacts = subjectContactDao.findContacts(subject);
          LOG.info("(получаем список контактов субьекта)");
          if(sMail != null)
          {
              List<SubjectContact> list_mail = subjectContactDao.findAllBy("sValue", sMail);
              LOG.info("(получаем список контактов по sMail {})", sMail);
              SubjectContactType typeContact = subjectContactTypeDao.getEmailType();
              SubjectContact oSubjectContact = this.synchronizationContacts(list_contacts, list_mail, subject, sMail, typeContact);
             if(oSubjectHuman != null)
             {
               oSubjectHuman.setDefaultEmail(oSubjectContact);
               subjectHumanDao.saveOrUpdateHuman(oSubjectHuman);
             }
              listContacts.add(oSubjectContact);
          }
          if(sPhone != null)
          {
              List<SubjectContact> list_phone = subjectContactDao.findAllBy("sValue", sPhone);
              LOG.info("(получаем список контактов по sPhone {})", sPhone);
              SubjectContactType typeContact = subjectContactTypeDao.getPhoneType();
              SubjectContact oSubjectContact = this.synchronizationContacts(list_contacts, list_phone, subject, sPhone, typeContact);
              listContacts.add(oSubjectContact);
              if(oSubjectHuman != null)
              {
                 oSubjectHuman.setDefaultPhone(oSubjectContact);
                 subjectHumanDao.saveOrUpdateHuman(oSubjectHuman);
              }

          }
       }
       else
       {
           
          if(sMail != null)
          {
             List<SubjectContact> list_contacts = subjectContactDao.findContacts(subjectsID_Mail);
             LOG.info("(получаем список контактов субьекта)");
             List<SubjectContact> list_mail = subjectContactDao.findAllBy("sValue", sMail);
             LOG.info("(получаем список контактов по sMail {})", sMail);
             SubjectContactType typeContact = subjectContactTypeDao.getEmailType();
             SubjectContact oSubjectContact = this.synchronizationContacts(list_contacts, list_mail, subjectsID_Mail, sMail, typeContact);
             listContacts.add(oSubjectContact);

          
          }
          if(sPhone != null)
          {
             List<SubjectContact> list_contacts = subjectContactDao.findContacts(subjectsID_Phone);
             LOG.info("(получаем список контактов субьекта)");
             List<SubjectContact> list_phone = subjectContactDao.findAllBy("sValue", sPhone);
             LOG.info("(получаем список контактов по sPhone {})", sPhone);
             SubjectContactType typeContact = subjectContactTypeDao.getPhoneType();
             SubjectContact oSubjectContact = this.synchronizationContacts(list_contacts, list_phone, subjectsID_Phone, sPhone, typeContact);
             listContacts.add(oSubjectContact);

         
          }
       }
                
       
       return listContacts;
    }
    private SubjectContact synchronizationContacts(List<SubjectContact> list_contacts_subject, List<SubjectContact> list_contacts, Subject subject, String sContact, SubjectContactType typeContact)
    {
       SubjectContact res = null;
       boolean bIsContact = this.isContact(list_contacts_subject, sContact);
       boolean bIsDataBase = this.isContact(list_contacts, sContact);
       if(bIsContact)
       {
            res = this.updateContact(subject, sContact);
            LOG.info("(апдейтим контакт в списке контактов субьекта)");
       }
       else
       {
           if(bIsDataBase)
           {
              res = this.updateContact(subject, sContact);
              LOG.info("(апдейтим контакт в списке контактов базы, переопределяя субьекта)");
           }
           else
           {
              res = this.createSubjectContact(sContact, subject, typeContact);
              LOG.info("(создаем контакт)");

           }
       }
       
       return res;  
    }
    private SubjectHuman getSubjectHuman(Subject subject)
    {
        return subjectHumanDao.findByExpected("oSubject", subject);
    }
      private SubjectContact createSubjectContact(String sContact, Subject subject, SubjectContactType typeContact)
    {
         SubjectContact contact = new SubjectContact();
         contact.setSubject(subject);
         contact.setSubjectContactType(typeContact);
         contact.setsDate();
         contact.setsValue(sContact);
         subjectContactDao.saveOrUpdate(contact);
         SubjectContact res = subjectContactDao.findByExpected("sValue", sContact);
         
         LOG.info("(создаем контакт subject Id {}, subject Label {}, subjectContact sValue {})", subject.getsID(), subject.getsLabel(), contact.getsValue());
         
         return res;
    }
    private SubjectContact updateContact(Subject subject, String sContact)
    {
        
         SubjectContact res = null;
       try
       {
         SubjectContact contact = subjectContactDao.findByExpected("sValue", sContact);
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
    private boolean isContact(List<SubjectContact> list, String sContact )
    {
        
         for(SubjectContact contact : list)
         {
             if(contact.getsValue().equals(sContact))
                 return true; 
             
         }
         
         return false;
    }
    private Subject getSubjectObject(String sID, String sContact)
    {
             
            LOG.info("(sID {})", sID);
            Subject subject = subjectDao.getSubject(sID);
            
               try
               {
                List<SubjectContact> listContact = subjectContactDao.findAllBy("sValue", sContact);
                for(SubjectContact oSubjectContact : listContact)
                {
                    if(oSubjectContact.getsValue().equals(sContact))
                    {
                       Subject subject_time = oSubjectContact.getSubject();
                       if(!subject_time.getsID().startsWith("_"))
                           subject = subject_time;
                       break;
                    }
                }
               }
               catch(Exception e)
               {
                   LOG.warn("({})", e.getMessage());
               }
            
           /* if(subject == null)
            {
               subject = new Subject();
               subject.setsID(sID);
               subjectDao.saveOrUpdateSubject(subject);
               subject = subjectDao.getSubject(sID);
               LOG.info("(Создаем subject Id {}, sID {})", subject.getId(), subject.getsID());
            }*/
        
        
        return subject;
    }
    private Subject createSubject(String sID)
    {
         Subject subject = new Subject();
         subject.setsID(sID);
         subjectDao.saveOrUpdateSubject(subject);
         subject = subjectDao.getSubject(sID);
         LOG.info("(Создаем subject Id {}, sID {})", subject.getId(), subject.getsID());
         
       return subject;
    }
    private String getsID(String sMail, String sPhone)
    {
        return (sMail != null)? SubjectHuman.getSubjectId(SubjectHumanIdType.Email, sMail) : ((sPhone != null)? SubjectHuman.getSubjectId(SubjectHumanIdType.Phone, sPhone) : null);
    }
    private Subject getSubjectObjectBynID(Long nID_Subject)
    {
         LOG.info("(subject Id {})", nID_Subject);
           if(subjectDao == null)
            LOG.info("(subjectDao null)");
           else
             LOG.info("(subjectDao not null)");
         return subjectDao.getSubject(nID_Subject);
          
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
