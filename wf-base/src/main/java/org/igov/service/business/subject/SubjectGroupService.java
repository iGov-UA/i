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
import org.igov.model.subject.VSubjectGroupChildrenNode;
import org.igov.model.subject.VSubjectGroupParentNode;
import org.igov.model.subject.VSubjectGroupTreeResult;
import org.igov.util.cache.CachedInvocationBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.google.common.base.Function;
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
	private static final String GET_SERVICE_SUBJECT_GROUP_CACHE_KEY = "SubjectGroupService.getCatalogTreeSubjectGroups";
	private static Long deepLevelChildSubjectGroup = 0L;

	@Autowired
	private BaseEntityDao<Long> baseEntityDao;

	@Autowired
	private CachedInvocationBean cachedInvocationBean;

	public List<VSubjectGroupParentNode> getCatalogTreeSubjectGroups(String sID_Group_Activiti, Long deepLevel) {

		List<SubjectGroupTree> subjectGroupRelations = new ArrayList<>(baseEntityDao.findAll(SubjectGroupTree.class));
		
		List<VSubjectGroupParentNode> parentSubjectGroups = new ArrayList<>();
		VSubjectGroupParentNode parentSubjectGroup =null;
		
		for(SubjectGroupTree subjectGroupRelation : subjectGroupRelations) {
			final SubjectGroup parent = subjectGroupRelation.getoSubjectGroup_Parent();
	
			if (parent.getId() != FAKE_ROOT_SUBJECT_ID) {
				parentSubjectGroup = new VSubjectGroupParentNode(parent);
				
			final SubjectGroup child = subjectGroupRelation.getoSubjectGroup_Child();
			VSubjectGroupChildrenNode childSubjectGroup = new VSubjectGroupChildrenNode(child);
				parentSubjectGroup.addChild(childSubjectGroup);
				parentSubjectGroups.add(parentSubjectGroup);
			}
		}
		
		return getFullResult(sID_Group_Activiti, deepLevel, parentSubjectGroups, parentSubjectGroup);
		
	
		
	}

	public List<VSubjectGroupParentNode> getFullResult(String sID_Group_Activiti, Long deepLevel,
			List<VSubjectGroupParentNode> parentSubjectGroups, VSubjectGroupParentNode parentSubjectGroup) {
		if((deepLevel==null || deepLevel==0) || (sID_Group_Activiti==null || sID_Group_Activiti.isEmpty())){
			/**
			 * получаем лист детей
			 */
			final List<List<VSubjectGroupChildrenNode>> childrensParList = Lists.newArrayList(Collections2.transform(
					parentSubjectGroups, new Function<VSubjectGroupParentNode, List<VSubjectGroupChildrenNode>>() {
						@Override
						public List<VSubjectGroupChildrenNode> apply(VSubjectGroupParentNode vSubjectGroupParentNode) {
							return vSubjectGroupParentNode.getChildren();
						}
					}));
			
			/**
			 * только лист
			 */
			final List<VSubjectGroupChildrenNode> childrensByGroup = Lists.newArrayList(Collections2.transform(
					childrensParList, new Function<List<VSubjectGroupChildrenNode>, VSubjectGroupChildrenNode>() {
						@Override
						public VSubjectGroupChildrenNode apply(List<VSubjectGroupChildrenNode> vSubjectGroupChildrenNodeList) {
							return vSubjectGroupChildrenNodeList.get(0);
						}
					}));
			
			VSubjectGroupTreeResult subjectGroupTreeResult = new VSubjectGroupTreeResult();
			parentSubjectGroup.accept(subjectGroupTreeResult);
			
			return parentSubjectGroups;
		}
		/**
		 * получить только отфильтрованные по sID_Group_Activiti
		 */
		final List<VSubjectGroupParentNode> parentSubjectGroupsFilltr = Lists.newArrayList(Collections2
				.filter(parentSubjectGroups,
						new Predicate<VSubjectGroupParentNode>() {
					@Override
					public boolean apply(VSubjectGroupParentNode vSubjectGroupParentNode) {
						List<VSubjectGroupChildrenNode> ch = new ArrayList<>();
						if(vSubjectGroupParentNode.getGroup().getsID_Group_Activiti().equals(sID_Group_Activiti)) {
						for(VSubjectGroupChildrenNode vSubjectGroupChildrenNode: vSubjectGroupParentNode.getChildren()) {
							if(vSubjectGroupChildrenNode.getGroup().getId().equals(vSubjectGroupParentNode.getGroup().getId())){
								ch.add(vSubjectGroupChildrenNode);
							}
							
						}
						vSubjectGroupParentNode.setChildren(ch);
						return !vSubjectGroupParentNode.getChildren().isEmpty() && vSubjectGroupParentNode.getChildren()!=null;
						}
						
						
						return vSubjectGroupParentNode.getGroup().getsID_Group_Activiti().equals(sID_Group_Activiti);					
						}
				}));
		
		LOG.info("parentSubjectGroupssssssssss "+parentSubjectGroups);
		/**
		 * получаем лист детей отфильтрованного списка
		 */
		/*final List<List<VSubjectGroupChildrenNode>> childrensParList = Lists.newArrayList(Collections2.transform(
				parentSubjectGroupsFilltr, new Function<VSubjectGroupParentNode, List<VSubjectGroupChildrenNode>>() {
					@Override
					public List<VSubjectGroupChildrenNode> apply(VSubjectGroupParentNode vSubjectGroupParentNode) {
						return vSubjectGroupParentNode.getChildren();
					}
				}));
		
		*//**
		 * только лист
		 *//*
		final List<VSubjectGroupChildrenNode> childrensByGroup = Lists.newArrayList(Collections2.transform(
				childrensParList, new Function<List<VSubjectGroupChildrenNode>, VSubjectGroupChildrenNode>() {
					@Override
					public VSubjectGroupChildrenNode apply(List<VSubjectGroupChildrenNode> vSubjectGroupChildrenNodeList) {
						return vSubjectGroupChildrenNodeList.get(0);
					}
				}));*/
		
		//Set<VSubjectGroupParentNode> rootTags = new HashSet<>(parentSubjectGroupsFilltr);
	//	List<VSubjectGroupParentNode> parentSubjectGroupsFilltrRes = new ArrayList<>(rootTags);
		
		VSubjectGroupTreeResult subjectGroupTreeResult = new VSubjectGroupTreeResult();
		parentSubjectGroup.accept(subjectGroupTreeResult);
	
		return parentSubjectGroupsFilltr;
	}

	public SubjectGroupResult getSubjectGroupsByGroupActiviti(String sID_Group_Activiti, Long deepLevel) {
		List<SubjectGroupTree> subjectGroupRelations = new ArrayList<>(baseEntityDao.findAll(SubjectGroupTree.class));
		Map<SubjectGroup, SubjectGroupNode> subjectToNodeMap = new HashMap<>();

		Set<SubjectGroup> parentSubject = new LinkedHashSet<>();
		Set<SubjectGroup> childSubject = new HashSet<>();

		SubjectGroupNode parentNode = null;
		for (SubjectGroupTree subjectGroupRelation : subjectGroupRelations) {
			final SubjectGroup parent = subjectGroupRelation.getoSubjectGroup_Parent();
			LOG.info("SubjectGrouppppppparent " + parent);
			final SubjectGroup child = subjectGroupRelation.getoSubjectGroup_Child();
			LOG.info("SubjectGrouppppppchild " + child);

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

		LOG.info("parentSubjecttttttttttttttt " + parentSubject);
		LOG.info("childSubjectttttttttttttttttt " + childSubject);

		LOG.info("SubjectGrouppppppSettttt" + rootTags);
	//	rootTags.removeAll(childSubject);

		final List<SubjectGroupNode> rootSubjectNodes = rootTags.stream().map(subjectToNodeMap::get)
				.collect(Collectors.toList());
		
		SubjectGroupResult subjectGroupResult=new SubjectGroupResult(rootSubjectNodes);
		
		LOG.info("subjectGroupResultttttttttttttttt " + subjectGroupResult);
		return subjectGroupResult;

	}
	
	public static Long getDeepLevelChildSubjectGroup() {
		return deepLevelChildSubjectGroup;
	}

	public static void setDeepLevelChildSubjectGroup(Long deepLevelChildSubjectGroup) {
		SubjectGroupService.deepLevelChildSubjectGroup = deepLevelChildSubjectGroup;
	}

	/**
	 * Кэш для SubjectGroupResult
	 * @param sID_Group_Activiti
	 * @param nDeepLevel
	 * @return
	 */
	 public SubjectGroupResult getSubjectGroupResultCached(String sID_Group_Activiti,Long deepLevel) {
		return cachedInvocationBean.invokeUsingCache(
				new CachedInvocationBean.Callback<SubjectGroupResult>(GET_SERVICE_SUBJECT_GROUP_CACHE_KEY, sID_Group_Activiti) {
					@Override
					public SubjectGroupResult execute() {
						return getSubjectGroupsByGroupActiviti(sID_Group_Activiti,deepLevel);
					}
				});
	}

}
