/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.igov.service.business.document.access;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
        List<SubjectContact> list_mail = null;
        List<SubjectContact> list_phone = null;
        
        subject = getSubject(nID_Subject);
        subjectHuman = getSubjectHuman(subject);
        
        List<SubjectContact> list_contacts = subjectContactDao.findContacts(subject);
        if(sMail != null)
           list_mail = subjectContactDao.findAllBy("sValue", sMail);
        if(sTelephone != null)
           list_phone = subjectContactDao.findAllBy("sValue", sTelephone);
        Map<String, SubjectContact> mapContacts = contactsSynchronization(list_mail, list_phone,
                list_contacts, subject, sMail, sTelephone);
        if(mapContacts.get("mail") != null)
            subjectHuman.setDefaultEmail(mapContacts.get("mail"));
       
        if(mapContacts.get("phone") != null)
            subjectHuman.setDefaultPhone(mapContacts.get("phone"));
        
            subjectHumanDao.saveOrUpdate(subjectHuman);
        
   }
    private Subject getSubject(Long nID_Subject)
    {
        return subjectDao.getSubject(nID_Subject);
    }
    private SubjectHuman getSubjectHuman(Subject subject)
    {
        return subjectHumanDao.findByExpected("oSubject", subject);
    }
    private Map<String, SubjectContact> contactsSynchronization(List<SubjectContact> list_mail, List<SubjectContact> list_phone,
            List<SubjectContact> list_contacts, Subject subject, String sMail, String sPhone)
    {
       Map<String, SubjectContact> mapContacts = new HashMap<String, SubjectContact>();
       SubjectContact mailContact = null;
       SubjectContact phoneContact = null;
       
      if(sMail != null)
     {
       boolean bIsContactMail = isContact(list_contacts, sMail);
       boolean bIsMail = isContact(list_mail, sMail);
       if(bIsContactMail)
       {
          mailContact = updateSubjectContact(sMail, subject);
       }
         if(!bIsContactMail)
       {
           if(bIsMail)
           {
              mailContact = updateSubjectContact(sMail, subject);
           }
           else
           {
             mailContact = createSubjectContact(sMail, subject, subjectContactTypeDao.getEmailType());
           }
       }
     }
     if(sPhone != null)
     {
       boolean bIsContactPhone = isContact(list_contacts, sPhone);
       boolean bIsPhone = isContact(list_phone, sPhone);
       if(bIsContactPhone)
       {
          phoneContact = updateSubjectContact(sPhone, subject);
       }
       if(!bIsContactPhone)
       {
           if(bIsPhone)
           {
              phoneContact = updateSubjectContact(sPhone, subject);
           }
           else
           {
             phoneContact = createSubjectContact(sPhone, subject, subjectContactTypeDao.getPhoneType());
           }
       }
     }
        mapContacts.put("mail", mailContact);
        mapContacts.put("phone", phoneContact);
           
        return mapContacts;
    }
    private SubjectContact updateSubjectContact(String contact, Subject subject)
    {
        SubjectContact res = subjectContactDao.findByExpected("sValue", contact);
        res.setSubject(subject);
        res.setsDate();
        subjectContactDao.saveOrUpdate(res);
        res = subjectContactDao.findByIdExpected(res.getId());
        
        return res;
    }
    private SubjectContact createSubjectContact(String contact, Subject subject, SubjectContactType typeContact)
    {
        SubjectContact res = new SubjectContact();
        res.setSubject(subject);
        res.setsValue(contact);
        res.setSubjectContactType(typeContact);
        res.setsDate();
        subjectContactDao.saveOrUpdate(res);
        res = subjectContactDao.findByExpected("sValue", contact);
        
        return res;
    }
}
