/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.igov.service.business.subject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.igov.model.core.BaseEntityDao;
import org.igov.model.subject.SubjectGroup;
import org.igov.model.subject.SubjectGroupNode;
import org.igov.model.subject.SubjectGroupResult;
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
	private static final long FAKE_ROOT_SUBJECT_ID  = 0;

	 @Autowired
	    private BaseEntityDao<Long> baseEntityDao;

    public SubjectGroupResult getSubjectGroups(String sID_Group_Activiti, Integer nDeepLevel) {
       /* if(nDeepLevel == null || nDeepLevel == 0){
            nDeepLevel = 1000;
        }*/
    	List<SubjectGroupTree> subjectGroupRelations = new ArrayList<>(baseEntityDao.findAll(SubjectGroupTree.class));
    	Map<SubjectGroup, SubjectGroupNode> subjectToNodeMap = new HashMap<>();
    	
        Set<SubjectGroup> parentSubject = new LinkedHashSet<>();
        Set<SubjectGroup> childSubject = new HashSet<>();
        
        SubjectGroupNode parentNode = null;	
        for (SubjectGroupTree subjectGroupRelation : subjectGroupRelations) {
            final SubjectGroup parent = subjectGroupRelation.getoSubjectGroup_Parent();
            final SubjectGroup child = subjectGroupRelation.getoSubjectGroup_Child();
            
            if (parent.getId() != FAKE_ROOT_SUBJECT_ID) {
            	parentNode = subjectToNodeMap.get(parent);
                if (parentNode == null) {
                	parentSubject.add(parent);
                	parentNode = new SubjectGroupNode(parent);
                	subjectToNodeMap.put(parent, parentNode);
                }
            }
            
            SubjectGroupNode childNode = subjectToNodeMap.get(child);
            if (childNode == null) {
            	childSubject.add(child);
                childNode = new SubjectGroupNode(child);
                subjectToNodeMap.put(child, childNode);
            }

            if (parentNode != null) {
                parentNode.addChild(childNode);
            }
            
        }
        Set<SubjectGroup> rootTags = new LinkedHashSet<>(parentSubject);
        rootTags.removeAll(childSubject);
    //    LOG.info("parentTagssssssssssssssss  " + parentTags);
      //  LOG.info("childTagssssssssssssss  " + childTags);	
        final List<SubjectGroupNode> rootSubjectNodes = rootTags.stream().map(subjectToNodeMap::get).collect(
                Collectors.toList());
        return new SubjectGroupResult(rootSubjectNodes);
    }

}
