/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.igov.service.business.subject;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.igov.model.core.BaseEntityDao;
import org.igov.model.subject.SubjectGroup;
import org.igov.model.subject.SubjectGroupDao;
import org.igov.model.subject.SubjectGroupTree;
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
	    private BaseEntityDao<Long> baseEntityDao;

    public List<SubjectGroup> getSubjectGroups(String sID_Group_Activiti, Integer nDeepLevel) {
       /* if(nDeepLevel == null || nDeepLevel == 0){
            nDeepLevel = 1000;
        }*/
        
        List<SubjectGroup>subjectGroupList =new ArrayList<>(baseEntityDao.findAll(SubjectGroup.class));
        
        LOG.info("SubjectGrouppppppppppppppppppp  " + subjectGroupList);
        	
        Set<SubjectGroup> parentTags = new LinkedHashSet<>();
        Set<SubjectGroup> childTags = new HashSet<>();
        
        List<SubjectGroupTree> subjectGroupRelations = new ArrayList<>(baseEntityDao.findAll(SubjectGroupTree.class));
        	
        for (SubjectGroupTree subjectGroupRelation : subjectGroupRelations) {
            final SubjectGroup parent = subjectGroupRelation.getoSubjectGroup_Parent();
            parentTags.add(parent);
            final SubjectGroup child = subjectGroupRelation.getoSubjectGroup_Child();
            childTags.add(child);

        }
        LOG.info("parentTagssssssssssssssss  " + parentTags);
        LOG.info("childTagssssssssssssss  " + childTags);	
        //получить по группе сабджектгрупп и по нему получ
        //если nDeepLevel ноль или нал, то делаем его равного 1000
        //из перентов получаем список детей. идем в цикле по детям и получаем список детей пока не получим ситуацию, когда ребенок не имеет родителя
        return subjectGroupList;
    }

}
