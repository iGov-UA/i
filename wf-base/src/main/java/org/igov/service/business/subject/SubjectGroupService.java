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
import org.igov.model.subject.SubjectGroupTreeResult;
import org.igov.util.cache.CachedInvocationBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.google.common.base.Predicate;
import com.google.common.collect.Collections2;
import com.google.common.collect.Lists;

/**
 *
 * @author inna
 */
@Service
public class SubjectGroupService {
	private static final Log LOG = LogFactory.getLog(SubjectGroupService.class);
	private static final long FAKE_ROOT_SUBJECT_ID = 0;
	 private static final String GET_SERVICE_SUBJECT_GROUP_CACHE_KEY = "SubjectGroupService.getSubjectGroupsByGroupActiviti";

	@Autowired
	private BaseEntityDao<Long> baseEntityDao;

	@Autowired
	private CachedInvocationBean cachedInvocationBean;

	public SubjectGroupResult getSubjectGroupsByGroupActiviti(String sID_Group_Activiti) {
		/*
		 * if(nDeepLevel == null || nDeepLevel == 0){ nDeepLevel = 1000; }
		 */
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
		
		final List<SubjectGroupNode> rootSubjectNodes = rootTags.stream().map(subjectToNodeMap::get)
				.collect(Collectors.toList());
		
/*		final List<SubjectGroupNode> rootSubjectNodesFiltr = Lists.newArrayList(Collections2
				.filter(rootTags.stream().map(subjectToNodeMap::get)
						.collect(Collectors.toList()),
						new Predicate<SubjectGroupNode>() {
					@Override
					public boolean apply(SubjectGroupNode subjectGroupNode) {
						// получить только отфильтрованные SubjectGroup по sID_Group_Activiti
						return subjectGroupNode.getGroup().getsID_Group_Activiti().equals(sID_Group_Activiti);
					}
				}));*/
		
		
		return new SubjectGroupResult(rootSubjectNodes);
	}

	
	
	 public List<SubjectGroupTreeResult> getSubjectGroupsByDeepLevel(String sID_Group_Activiti,Integer nDeepLevel) {
		 
		 List<SubjectGroupTreeResult> res = new ArrayList<>();
		 SubjectGroupResult subjectGroupResult = getSubjectGroupResultCached(sID_Group_Activiti);
		 
		 
		return res;
		 
	 }
	
	/**
	 * Кэш для SubjectGroupResult
	 * @param sID_Group_Activiti
	 * @param nDeepLevel
	 * @return
	 */
	 public SubjectGroupResult getSubjectGroupResultCached(String sID_Group_Activiti) {
		return cachedInvocationBean.invokeUsingCache(
				new CachedInvocationBean.Callback<SubjectGroupResult>(GET_SERVICE_SUBJECT_GROUP_CACHE_KEY, sID_Group_Activiti) {
					@Override
					public SubjectGroupResult execute() {
						return getSubjectGroupsByGroupActiviti(sID_Group_Activiti);
					}
				});
	}

}
