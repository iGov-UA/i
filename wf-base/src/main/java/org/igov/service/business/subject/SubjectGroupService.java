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
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.igov.model.core.BaseEntityDao;
import org.igov.model.subject.SubjectGroup;
import org.igov.model.subject.SubjectGroupTree;
import org.igov.model.subject.VSubjectGroupParentNode;
import org.igov.model.subject.VSubjectGroupTreeResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.google.common.base.Predicate;
import com.google.common.collect.Collections2;
import com.google.common.collect.Lists;
import com.google.common.base.Function;

/**
 *
 * @author inna
 */
@Service
public class SubjectGroupService {
	private static final Log LOG = LogFactory.getLog(SubjectGroupService.class);
	private static final long FAKE_ROOT_SUBJECT_ID = 0;
	private Long countChild = 0L;
	@Autowired
	private BaseEntityDao<Long> baseEntityDao;

	public List<List<SubjectGroup>> getCatalogTreeSubjectGroups(String sID_Group_Activiti, Long deepLevel) {
		countChild = 0L;
		List<SubjectGroupTree> subjectGroupRelations = new ArrayList<>(baseEntityDao.findAll(SubjectGroupTree.class));

		List<VSubjectGroupParentNode> parentSubjectGroups = new ArrayList<>();
		Map<Long, List<SubjectGroup>> subjToNodeMap = new HashMap<>();
		VSubjectGroupParentNode parentSubjectGroup = null;
		Set<Long> idParentList = new LinkedHashSet<>();
		Set<Long> idChildrenList = new LinkedHashSet<>();
		for (SubjectGroupTree subjectGroupRelation : subjectGroupRelations) {
			final SubjectGroup parent = subjectGroupRelation.getoSubjectGroup_Parent();

			if (parent.getId() != FAKE_ROOT_SUBJECT_ID) {
				parentSubjectGroup = new VSubjectGroupParentNode();
				final SubjectGroup child = subjectGroupRelation.getoSubjectGroup_Child();
				if (!idChildrenList.contains(child)) {
					if (subjToNodeMap.get(child) != null && !subjToNodeMap.get(child).isEmpty()) {
						idChildrenList.add(child.getId());
					}
				}
				if (!idParentList.contains(parent.getId())) {
					idParentList.add(parent.getId());
					parentSubjectGroup.setGroup(parent);
					parentSubjectGroup.addChild(child);
					parentSubjectGroups.add(parentSubjectGroup);
					subjToNodeMap.put(parent.getId(), parentSubjectGroup.getChildren());
				} else {
					for (VSubjectGroupParentNode vSubjectGroupParentNode : parentSubjectGroups) {
						if (vSubjectGroupParentNode.getGroup().getId().equals(parent.getId())) {
							vSubjectGroupParentNode.getChildren().add(child);
							subjToNodeMap.put(parent.getId(), vSubjectGroupParentNode.getChildren());
						}
					}
				}
			}

		}

		Map<Long, List<SubjectGroup>> subjToNodeMapFiltr = new HashMap<>();
		for (Long parentId : idParentList) {
			List<SubjectGroup> children = subjToNodeMap.get(parentId);

			final List<Long> idChildren = Lists.newArrayList(
					Collections2.transform(subjToNodeMap.get(parentId), new Function<SubjectGroup, Long>() {
						@Override
						public Long apply(SubjectGroup subjectGroup) {
							return subjectGroup.getId();
						}
					}));

			for (Long chid : idChildren) {
				List<SubjectGroup> child = subjToNodeMap.get(chid);
				if (subjToNodeMap.get(chid) != null && !subjToNodeMap.get(chid).isEmpty()) {
					children.addAll(child);
				}
			}
			subjToNodeMapFiltr.put(parentId, children);
		}
		
		List<List<SubjectGroup>> valuesRes = subjToNodeMapFiltr.values().stream().collect(Collectors.toList());

		Collections.sort(parentSubjectGroups, new Comparator() {
			@Override
			public int compare(Object vSubjectGroupParentNode, Object vSubjectGroupParentNodeTwo) {
				return ((VSubjectGroupParentNode) vSubjectGroupParentNode).getGroup().getId()
						.compareTo(((VSubjectGroupParentNode) vSubjectGroupParentNodeTwo).getGroup().getId());
			}
		});

		final List<VSubjectGroupParentNode> parentSubjectGroupsFilltr = Lists
				.newArrayList(Collections2.filter(parentSubjectGroups, new Predicate<VSubjectGroupParentNode>() {
					@Override
					public boolean apply(VSubjectGroupParentNode vSubjectGroupParentNode) {
						return vSubjectGroupParentNode.getGroup().getsID_Group_Activiti().equals(sID_Group_Activiti);
					}
				}));

		VSubjectGroupTreeResult subjectGroupTreeResult = new VSubjectGroupTreeResult();
		parentSubjectGroup.accept(subjectGroupTreeResult);
		return valuesRes;

	}

}
