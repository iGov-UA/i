/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.igov.service.business.subject;

import org.igov.model.subject.Subject;
import org.igov.model.subject.SubjectContact;
import org.igov.model.subject.SubjectContactDao;
import org.igov.model.subject.SubjectContactTypeDao;
import org.igov.model.subject.SubjectDao;
import org.igov.model.subject.SubjectHuman;
import org.igov.model.subject.SubjectHumanDao;
import org.igov.model.subject.SubjectHumanIdType;
import org.igov.model.subject.organ.SubjectOrganDao;
import org.springframework.beans.factory.annotation.Autowired;

/**
 *
 * @author Belyavtsev Vladimir Vladimirovich (BW)
 */
public class SubjectService {
    
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
    
    /* public SubjectContact syncContactMail(String sMail)
    {
        SubjectContact oSubjectContact = null;
        SubjectHuman oSubjectHuman = subjectHumanDao.getSubjectHuman(SubjectHumanIdType.Email, sMail);
        Subject oSubject = (oSubjectHuman != null)? oSubjectHuman.getoSubject() : null;
        
        if(oSubject != null)
        {
            oSubjectContact = subjectContactDao.findByExpected("sValue", sMail);
            if(oSubjectContact != null)
            {
               oSubjectContact.setSubject(oSubject);
               oSubjectContact.setsDate();
               subjectContactDao.saveOrUpdate(oSubjectContact);
            }
        }
        
        return oSubjectContact;
    }
    
    public SubjectContact syncContactMail(String sMail, Long nID_Subject)
    {
        SubjectContact oSubjectContact = null;
        Subject oSubject = subjectDao.getSubject(nID_Subject);
        SubjectHuman oSubjectHuman = (oSubject != null)? subjectHumanDao.findByExpected("oSubject", oSubject) : null;
       if(oSubject != null)
       {
         try
         {
          oSubjectContact = subjectContactDao.findByExpected("sValue", sMail);
         }
         catch(Exception e)
         {
         
         }
          if(oSubjectContact != null)
          {
             oSubjectContact.setSubject(oSubject);
             oSubjectContact.setsDate();
          }
          else
          {
             oSubjectContact = new SubjectContact();
             oSubjectContact.setSubject(oSubject);
             oSubjectContact.setsDate();
             oSubjectContact.setSubjectContactType(subjectContactTypeDao.getEmailType());
             oSubjectContact.setsValue(sMail);
             subjectContactDao.saveOrUpdate(oSubjectContact);
             if(oSubjectHuman != null)
             {
                oSubjectHuman.setDefaultEmail(oSubjectContact);
                subjectHumanDao.saveOrUpdate(oSubjectHuman);
             }
          }
       }
        
        return oSubjectContact;
    }*/
   
    
}
