/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.igov.service.business.subject;

import java.util.List;
import org.igov.model.subject.SubjectActionKVED;
import org.igov.model.subject.SubjectActionKVEDDao;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 *
 * @author Belyavtsev Vladimir Vladimirovich (BW)
 */
@Service
public class SubjectActionKVEDService {
    
     private static final Logger LOG = LoggerFactory.getLogger(SubjectActionKVEDService.class);

    @Autowired
    private SubjectActionKVEDDao subjectActionKVEDDao;

    public List<SubjectActionKVED> getSubjectActionKVED(String sID, String sNote) {
	return subjectActionKVEDDao.getSubjectActionKVED(sID, sNote);
    }
    
    public List<SubjectActionKVED> getSubjectActionKVED(String sFind ) {
	return subjectActionKVEDDao.getSubjectActionKVED(sFind);
    }
}
