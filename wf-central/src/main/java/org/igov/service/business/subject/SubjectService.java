/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.igov.service.business.subject;

import org.igov.model.subject.Subject;
import org.igov.model.subject.SubjectDao;
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
    
    public Subject syncSubject_Upload(String sID_Subject_Upload) {
        Subject subject_Upload = subjectDao.getSubject(sID_Subject_Upload);
        if (subject_Upload == null) {
            subject_Upload = subjectOrganDao.setSubjectOrgan(sID_Subject_Upload).getoSubject();
        }
        return subject_Upload;
    }
    
}
