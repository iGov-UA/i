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
    
    
    
    public void syncContacts(Long nID_Subject, String sMail, String sTelephone)
    {
        Subject subject = null;
        SubjectHuman subjectHuman = null;
      
        subject = subjectDao.getSubject(nID_Subject);
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
    }

}
