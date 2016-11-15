/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.igov.service.business.subject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.igov.model.core.BaseEntityDao;
import org.igov.model.subject.SubjectGroup;
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

	@Autowired
	private BaseEntityDao<Long> baseEntityDao;

	public List<VSubjectGroupParentNode> getCatalogTreeSubjectGroups(String sID_Group_Activiti, Long deepLevel) {

		List<SubjectGroupTree> subjectGroupRelations = new ArrayList<>(baseEntityDao.findAll(SubjectGroupTree.class));

		List<VSubjectGroupParentNode> parentSubjectGroups = new ArrayList<>();
		VSubjectGroupParentNode parentSubjectGroup = null;

		for (SubjectGroupTree subjectGroupRelation : subjectGroupRelations) {
			final SubjectGroup parent = subjectGroupRelation.getoSubjectGroup_Parent();

			if (parent.getId() != FAKE_ROOT_SUBJECT_ID) {
				Set<Long>idList = new LinkedHashSet<>();
				idList.add(parent.getId());
				final SubjectGroup child = subjectGroupRelation.getoSubjectGroup_Child();
				if(!idList.contains(parent.getId())) {
				parentSubjectGroup = new VSubjectGroupParentNode(parent);
				parentSubjectGroup.addChild(child);
				}
				parentSubjectGroup.getChildren().add(child);
				parentSubjectGroups.add(parentSubjectGroup);
			}
		}
		
		Collections.sort(parentSubjectGroups, new Comparator() {
			@Override
			public int compare(Object vSubjectGroupParentNode, Object vSubjectGroupParentNodeTwo) {
				return ((VSubjectGroupParentNode) vSubjectGroupParentNode).getGroup().getId()
						.compareTo(((VSubjectGroupParentNode) vSubjectGroupParentNodeTwo).getGroup().getId());
			}
		});
		
		Map<SubjectGroup,List<SubjectGroup>>map = new HashMap<>();
		
		
		final List<VSubjectGroupParentNode> parentSubjectGroupsFilltr = Lists
				.newArrayList(Collections2.filter(parentSubjectGroups, new Predicate<VSubjectGroupParentNode>() {
					@Override
					public boolean apply(VSubjectGroupParentNode vSubjectGroupParentNode) {
						return vSubjectGroupParentNode.getGroup().getsID_Group_Activiti().equals(sID_Group_Activiti);
					}
				}));
		
		VSubjectGroupTreeResult subjectGroupTreeResult = new VSubjectGroupTreeResult();
		parentSubjectGroup.accept(subjectGroupTreeResult);
		return parentSubjectGroups;

	}

	/*public List<SubjectGroup> getFullResult(String sID_Group_Activiti, Long deepLevel,
			List<VSubjectGroupParentNode> parentSubjectGroups, VSubjectGroupParentNode parentSubjectGroup) {
		if ((deepLevel == null || deepLevel == 0) && (sID_Group_Activiti == null || sID_Group_Activiti.isEmpty())) {

			final List<List<SubjectGroup>> childrensParList = Lists.newArrayList(Collections2.transform(
					parentSubjectGroups, new Function<VSubjectGroupParentNode, List<SubjectGroup>>() {
						@Override
						public List<SubjectGroup> apply(VSubjectGroupParentNode vSubjectGroupParentNode) {
							return vSubjectGroupParentNode.getChildren();
						}
					}));

			final List<SubjectGroup> childrensByGroup = Lists.newArrayList(Collections2.transform(
					childrensParList, new Function<List<SubjectGroup>, SubjectGroup>() {
						@Override
						public SubjectGroup apply(
								List<SubjectGroup> vSubjectGroupChildrenNodeList) {
							return vSubjectGroupChildrenNodeList.get(0);
						}
					}));


			VSubjectGroupTreeResult subjectGroupTreeResult = new VSubjectGroupTreeResult();
			parentSubjectGroup.accept(subjectGroupTreeResult);

			return childrensByGroup;
		}
		*//**
		 * получить только отфильтрованные по sID_Group_Activiti
		 *//*
		final List<VSubjectGroupParentNode> parentSubjectGroupsFilltr = Lists
				.newArrayList(Collections2.filter(parentSubjectGroups, new Predicate<VSubjectGroupParentNode>() {
					@Override
					public boolean apply(VSubjectGroupParentNode vSubjectGroupParentNode) {
						return vSubjectGroupParentNode.getGroup().getsID_Group_Activiti().equals(sID_Group_Activiti);
					}
				}));

		*//**
		 * получаем лист детей отфильтрованного списка
		 *//*
		final List<List<VSubjectGroupChildrenNode>> childrensParList = Lists.newArrayList(Collections2.transform(
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
					public VSubjectGroupChildrenNode apply(
							List<VSubjectGroupChildrenNode> vSubjectGroupChildrenNodeList) {
						return vSubjectGroupChildrenNodeList.get(0);
					}
				}));

		final List<Long> nIdList = Lists
				.newArrayList(Collections2.transform(childrensByGroup, new Function<VSubjectGroupChildrenNode, Long>() {
					@Override
					public Long apply(VSubjectGroupChildrenNode vSubjectGroupChildrenNode) {
						return vSubjectGroupChildrenNode.getGroup().getId();
					}
				}));

		final List<VSubjectGroupParentNode> parentSubjectGroupsFilltrRes = Lists
				.newArrayList(Collections2.filter(parentSubjectGroups, new Predicate<VSubjectGroupParentNode>() {
					@Override
					public boolean apply(VSubjectGroupParentNode vSubjectGroupParentNode) {

						return nIdList.contains(vSubjectGroupParentNode.getGroup().getId());
					}
				}));

		List<VSubjectGroupParentNode> newList = new ArrayList<>(parentSubjectGroupsFilltrRes);
		newList.addAll(parentSubjectGroupsFilltr);

		Collections.sort(newList, new Comparator() {
			@Override
			public int compare(Object vSubjectGroupParentNode, Object vSubjectGroupParentNodeTwo) {
				return ((VSubjectGroupParentNode) vSubjectGroupParentNode).getGroup().getId()
						.compareTo(((VSubjectGroupParentNode) vSubjectGroupParentNodeTwo).getGroup().getId());
			}
		});

		final List<List<VSubjectGroupChildrenNode>> childrensParListRes = Lists.newArrayList(Collections2
				.transform(newList, new Function<VSubjectGroupParentNode, List<VSubjectGroupChildrenNode>>() {
					@Override
					public List<VSubjectGroupChildrenNode> apply(VSubjectGroupParentNode vSubjectGroupParentNode) {
						return vSubjectGroupParentNode.getChildren();
					}
				}));

		*//**
		 * только лист
		 *//*
		final List<VSubjectGroupChildrenNode> childrensByGroupRes = Lists.newArrayList(Collections2.transform(
				childrensParListRes, new Function<List<VSubjectGroupChildrenNode>, VSubjectGroupChildrenNode>() {
					@Override
					public VSubjectGroupChildrenNode apply(
							List<VSubjectGroupChildrenNode> vSubjectGroupChildrenNodeList) {
						return vSubjectGroupChildrenNodeList.get(0);
					}
				}));


		final List<SubjectGroup> childrens = Lists.newArrayList(
				Collections2.transform(childrensByGroupRes, new Function<VSubjectGroupChildrenNode, SubjectGroup>() {
					@Override
					public SubjectGroup apply(VSubjectGroupChildrenNode vSubjectGroupChildrenNodeList) {
						return vSubjectGroupChildrenNodeList.getGroup();
					}
				}));
		if ((deepLevel == null || deepLevel == 0) && (sID_Group_Activiti != null || !sID_Group_Activiti.isEmpty())) {
			return childrens;
		}

		VSubjectGroupTreeResult subjectGroupTreeResult = new VSubjectGroupTreeResult();
		parentSubjectGroup.accept(subjectGroupTreeResult);

		return childrens;
	}*/

}
