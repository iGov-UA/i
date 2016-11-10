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
import org.igov.model.core.BaseEntityDao;
import org.igov.model.subject.ChildSubjectGroup;
import org.igov.model.subject.ParentSubjectGroup;
import org.igov.model.subject.SubjectGroup;
import org.igov.model.subject.SubjectGroupResult;
import org.igov.model.subject.SubjectGroupTree;
import org.igov.model.subject.SubjectGroupTreeResult;
import org.igov.util.cache.CachedInvocationBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


/**
 *
 * @author inna
 */
@Service
public class SubjectGroupService {
	private static final Log LOG = LogFactory.getLog(SubjectGroupService.class);
	private static final long FAKE_ROOT_SUBJECT_ID = 0;
	 private static final String GET_SERVICE_SUBJECT_GROUP_CACHE_KEY = "SubjectGroupService.getSubjectGroupResult";

	@Autowired
	private BaseEntityDao<Long> baseEntityDao;

	@Autowired
	private CachedInvocationBean cachedInvocationBean;

	public SubjectGroupResult getSubjectGroupsByGroupActiviti(String sID_Group_Activiti,Long deepLevel) {
		//SubjectGroupTreeResult.setDeepLevelChildSubjectGroup(0L);
		//SubjectGroupTreeResult.setDeepLevelParentSubjectGroup(0L);
		//SubjectGroupTreeResult.setDeepLevelSubjectGroupResult(0L);
		List<SubjectGroupTree> subjectGroupRelations = new ArrayList<>(baseEntityDao.findAll(SubjectGroupTree.class));
	//	Map<SubjectGroup, SubjectGroupNode> subjectToNodeMap = new HashMap<>();
		
		List<ParentSubjectGroup> parentSubjectGroups = new ArrayList<>();
		
		for(SubjectGroupTree subjectGroupRelation : subjectGroupRelations) {
			final SubjectGroup parent = subjectGroupRelation.getoSubjectGroup_Parent();
	
			if (parent.getId() != FAKE_ROOT_SUBJECT_ID) {
				ParentSubjectGroup parentSubjectGroup = new ParentSubjectGroup(parent);
				
			final SubjectGroup child = subjectGroupRelation.getoSubjectGroup_Child();
				ChildSubjectGroup childSubjectGroup = new ChildSubjectGroup(child,deepLevel);
				parentSubjectGroup.addChildSubjectGroup(childSubjectGroup);
				parentSubjectGroups.add(parentSubjectGroup);
			}
		}

		/*Set<SubjectGroup> parentSubject = new LinkedHashSet<>();
		Set<SubjectGroup> childSubject = new HashSet<>();

		SubjectGroupNode parentNode = null;
		for (SubjectGroupTree subjectGroupRelation : subjectGroupRelations) {
			final SubjectGroup parent = subjectGroupRelation.getoSubjectGroup_Parent();
			LOG.info("SubjectGrouppppppparent "+ parent);
			final SubjectGroup child = subjectGroupRelation.getoSubjectGroup_Child();
			LOG.info("SubjectGrouppppppchild "+ child);

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
		
		LOG.info("parentSubjecttttttttttttttt "+ parentSubject);
		LOG.info("childSubjectttttttttttttttttt "+ childSubject);
		
		
		LOG.info("SubjectGrouppppppSettttt"+ rootTags);
		rootTags.removeAll(childSubject);
		
		final List<SubjectGroupNode> rootSubjectNodes = rootTags.stream().map(subjectToNodeMap::get)
				.collect(Collectors.toList());*/
		SubjectGroupResult subjectGroupResult = new SubjectGroupResult(sID_Group_Activiti);
		for(ParentSubjectGroup parentSubjectGroup:parentSubjectGroups) {
			subjectGroupResult.addParentSubjectGroup(parentSubjectGroup);
		}
    	
    	
    	SubjectGroupTreeResult subjectGroupTreeResult = new SubjectGroupTreeResult();
    	subjectGroupResult.accept(subjectGroupTreeResult);
		
		return subjectGroupResult;
	}

	
	
}
