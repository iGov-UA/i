/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.igov.service.business.document.access;

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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 *
 * @author ACER
 */
@Service
public class DocumentAccessService {
    
    private static final Logger LOG = LoggerFactory.getLogger(DocumentAccessService.class);
    
    @Autowired
    SubjectDao subjectDao;
    @Autowired
    SubjectHumanDao subjectHumanDao;
    @Autowired
    SubjectContactDao subjectContactDao;
    @Autowired
    SubjectContactTypeDao subjectContactTypeDao;
    
     private boolean isContact(List<SubjectContact> list, String sContact )
    {
        
         for(SubjectContact contact : list)
         {
             if(contact.getsValue().equals(sContact))
                return true; 
         }
         
         return false;
    }
    
    public void syncContacts(Long nID_Subject, String sMail, String sTelephone)
    {
        Subject subject = null;
        SubjectHuman subjectHuman = null;
        String sID_Mail = null;
        String sID_Phone = null;
        List<SubjectContact> list_mail = null;
        List<SubjectContact> list_phone = null;
       if(sMail != null)
           sID_Mail = SubjectHuman.getSubjectId(SubjectHumanIdType.Email, sMail);
       if(sTelephone != null)
           sID_Phone = SubjectHuman.getSubjectId(SubjectHumanIdType.Phone, sTelephone);
        
        subject = subjectDao.getSubject(nID_Subject);
        subjectHuman = subjectHumanDao.findByExpected("oSubject", subject);
        
        List<SubjectContact> list_contacts = subjectContactDao.findContacts(subject);
       if(sMail != null)
           list_mail = subjectContactDao.findAllBy("sValue", sMail);
       if(sTelephone != null)
           list_phone = subjectContactDao.findAllBy("sValue", sTelephone);
        
        if(list_mail.size() == 0 && list_phone.size() == 0)
        {
           if(sMail != null)
           {
              SubjectContact oSubjectContact = new SubjectContact();
              oSubjectContact.setSubject(subject);
              oSubjectContact.setSubjectContactType(subjectContactTypeDao.getEmailType());
              oSubjectContact.setsDate();
              oSubjectContact.setsValue(sMail);
              subjectContactDao.saveOrUpdate(oSubjectContact);
           }
           if(sTelephone != null)
           {
              SubjectContact oSubjectContact = new SubjectContact();
              oSubjectContact.setSubject(subject);
              oSubjectContact.setSubjectContactType(subjectContactTypeDao.getPhoneType());
              oSubjectContact.setsDate();
              oSubjectContact.setsValue(sTelephone);
              subjectContactDao.saveOrUpdate(oSubjectContact);

           }
        }
        else
        {
            boolean isMail = true;
            boolean isPhone = true;
            boolean isSubject_Phone = true;
            boolean isSubject_Mail = true;
            
           if(sMail != null)
             isMail = this.isContact(list_mail, sMail);
           if(sTelephone != null)
             isPhone = this.isContact(list_phone, sTelephone);
             isSubject_Phone = this.isContact(list_contacts, sTelephone);
             isSubject_Mail = this.isContact(list_contacts, sMail);
             
           if((isMail || isPhone) && (isSubject_Mail || isSubject_Phone))
           {
               for(SubjectContact contact : list_contacts)
               {
                  if(contact.getsValue().equals(sMail) || contact.getsValue().equals(sTelephone))
                  {
                   SubjectContact oSubjectContact = contact;
                   oSubjectContact.setsDate();
                   subjectContactDao.saveOrUpdate(oSubjectContact);
                  }
                  
               }
           }
           if((isMail || isPhone) && (!isSubject_Mail || !isSubject_Phone))
           {
               for(SubjectContact contact : list_mail)
               {
                  if(contact.getsValue().equals(sMail))
                  {
                    if(contact.getSubject().getsLabel() == null && contact.getSubject().getsLabelShort() == null)
                    {
                     SubjectContact oSubjectContact = contact;
                     oSubjectContact.setSubject(subject);
                     oSubjectContact.setsDate();
                     subjectContactDao.saveOrUpdate(oSubjectContact);
                    }
                  }
               }
               for(SubjectContact contact : list_phone)
               {
                   if(contact.getsValue().equals(sTelephone))
                  {
                    if(contact.getSubject().getsLabel() == null && contact.getSubject().getsLabelShort() == null)
                    {
                     SubjectContact oSubjectContact = contact;
                     oSubjectContact.setSubject(subject);
                     oSubjectContact.setsDate();
                     subjectContactDao.saveOrUpdate(oSubjectContact);
                    }
                  }
               }
           }
           
           if((!isMail || !isPhone)&&(!isSubject_Mail || !isSubject_Phone))
           {
              if(sMail != null)
             {
              SubjectContact oSubjectContact = new SubjectContact();
              oSubjectContact.setSubject(subject);
              oSubjectContact.setSubjectContactType(subjectContactTypeDao.getEmailType());
              oSubjectContact.setsDate();
              oSubjectContact.setsValue(sMail);
              subjectContactDao.saveOrUpdate(oSubjectContact);
             }
             if(sTelephone != null)
             {
              SubjectContact oSubjectContact = new SubjectContact();
              oSubjectContact.setSubject(subject);
              oSubjectContact.setSubjectContactType(subjectContactTypeDao.getPhoneType());
              oSubjectContact.setsDate();
              oSubjectContact.setsValue(sTelephone);
              subjectContactDao.saveOrUpdate(oSubjectContact);

             }
           }
           
           
        }
        
        
        
        /*
        List<SubjectContact> contacts = subjectContactDao.findContacts(subject);
        
        boolean subjphone = true;
        boolean subjmail = true;
        
        for(SubjectContact subcontact : contacts)
        {
           SubjectContactType sct = subcontact.getSubjectContactType();
           if(sct.getsName_EN().equals("Email") || sct.getsName_EN().equals("Phone"))
          {
            if(subcontact.getsValue().equals(sMail))
            {
                subjmail = false;
                subcontact.setsDate();
                subjectContactDao.saveOrUpdate(subcontact);
                continue;
            }
            if(subcontact.getsValue().equals(sTelephone))
            {
                subjphone = false;
                subcontact.setsDate();
                subjectContactDao.saveOrUpdate(subcontact);
                
            }
          }
          
        }
        
        
        if(subjmail)
        {
          if(sMail != null && !sMail.isEmpty())
          {
            SubjectContactType subjectContactType = subjectContactTypeDao.getEmailType();
            SubjectContact subjectContact = new SubjectContact();
            subjectContact.setSubject(subject);
            subjectContact.setSubjectContactType(subjectContactType);
            subjectContact.setsValue(sMail);
            subjectContact.setsDate();
            subjectContactDao.saveOrUpdate(subjectContact);
           try
           {
            subjectHuman = subjectHumanDao.findByExpected("oSubject", subject);
           }
           catch(Exception e)
           {
               LOG.error(e.getMessage(), e);
           }
           if(subjectHuman != null)
           {
            subjectHuman.setDefaultEmail(subjectContact);
           // subjectHuman.setSubjectHumanIdType(SubjectHumanIdType.Email);
            subjectHumanDao.saveOrUpdate(subjectHuman);
           }
          }
        }
        if(subjphone)
        {
          if(sTelephone != null && !sTelephone.isEmpty())
          {
             SubjectContactType subjectContactType = subjectContactTypeDao.getPhoneType();
             SubjectContact subjectContact = new SubjectContact();
             subjectContact.setSubject(subject);
             subjectContact.setSubjectContactType(subjectContactType);
             subjectContact.setsValue(sTelephone);
             subjectContact.setsDate();
             subjectContactDao.saveOrUpdate(subjectContact);
            try
            {
             subjectHuman = subjectHumanDao.findByExpected("oSubject", subject);
            }
            catch(Exception e)
            {
                LOG.error(e.getMessage(), e);
            }
            if(subjectHuman != null)
            {
             subjectHuman.setDefaultPhone(subjectContact);
             //subjectHuman.setSubjectHumanIdType(SubjectHumanIdType.Email);
             subjectHumanDao.saveOrUpdate(subjectHuman);
            }

          }
        }
      */
    }

}
