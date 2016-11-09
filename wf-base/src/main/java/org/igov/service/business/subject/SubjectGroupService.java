/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.igov.service.business.subject;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.igov.model.subject.SubjectGroup;
import org.igov.model.subject.SubjectGroupDao;
import org.igov.model.subject.SubjectGroupTree;
import org.igov.model.subject.SubjectGroupTreeDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 *
 * @author inna
 */
@Service
public class SubjectGroupService {
	private static final Log LOG = LogFactory.getLog(SubjectGroupService.class);

	@Autowired
    private SubjectGroupDao subjectGroupDao;

    @Autowired
    private SubjectGroupTreeDao  subjectGroupTreeDao;

    public List<SubjectGroup> getSubjectGroups(String sID_Group_Activiti, Integer nDeepLevel) {
        if(nDeepLevel == null || nDeepLevel == 0){
            nDeepLevel = 1000;
        }
        
        List<SubjectGroup>subjectGroupList = new ArrayList<>();
        
        SubjectGroup subjectGroup = subjectGroupDao.getSubjectGroupsByGroupActiviti(sID_Group_Activiti);
        LOG.info("SubjectGrouppppppppppppppppppp  " + subjectGroup.toString());
        	
        	List<SubjectGroupTree> subjectGroupTreeList = subjectGroupTreeDao.getSubjectChildByParentId(subjectGroup.getId());
        	
        	for(SubjectGroupTree subjectGroupTree:subjectGroupTreeList) {
        		subjectGroupList.add(subjectGroupTree.getoSubjectGroup_Child());
        	}
 
        	
        LOG.info("SubjectGrouppppppppppppppp  " + subjectGroupList);
        //получить по группе сабджектгрупп и по нему получ
        //если nDeepLevel ноль или нал, то делаем его равного 1000
        //из перентов получаем список детей. идем в цикле по детям и получаем список детей пока не получим ситуацию, когда ребенок не имеет родителя
        return subjectGroupList;
    }

}
