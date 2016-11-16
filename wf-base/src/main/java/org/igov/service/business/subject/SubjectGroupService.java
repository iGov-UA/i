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
	private Long countChild = 0L;
	@Autowired
	private BaseEntityDao<Long> baseEntityDao;

	public List<VSubjectGroupParentNode> getCatalogTreeSubjectGroups(String sID_Group_Activiti, Long deepLevel) {

		List<SubjectGroupTree> subjectGroupRelations = new ArrayList<>(baseEntityDao.findAll(SubjectGroupTree.class));

		List<VSubjectGroupParentNode> parentSubjectGroups = new ArrayList<>();
		VSubjectGroupParentNode parentSubjectGroup = null;
		Set<Long> idParentList = new LinkedHashSet<>();
		for (SubjectGroupTree subjectGroupRelation : subjectGroupRelations) {
			final SubjectGroup parent = subjectGroupRelation.getoSubjectGroup_Parent();

			if (parent.getId() != FAKE_ROOT_SUBJECT_ID) {
				parentSubjectGroup = new VSubjectGroupParentNode();
				final SubjectGroup child = subjectGroupRelation.getoSubjectGroup_Child();
				if (!idParentList.contains(parent.getId())) {
					idParentList.add(parent.getId());
					parentSubjectGroup.setGroup(parent);
					parentSubjectGroup.addChild(child);
					parentSubjectGroups.add(parentSubjectGroup);
				} else {
					for (VSubjectGroupParentNode vSubjectGroupParentNode : parentSubjectGroups) {
						if (vSubjectGroupParentNode.getGroup().getId().equals(parent.getId())) {
							vSubjectGroupParentNode.getChildren().add(child);
						}
					}
				}

				for (VSubjectGroupParentNode vSubjectGroupParentNode : parentSubjectGroups) {
					if (vSubjectGroupParentNode.getGroup().getId().equals(child.getId())) {
					countChild++;
					if (countChild.compareTo(deepLevel) < 0) {
						vSubjectGroupParentNode.getChildren().add(child);
					}
					}
			}	

			}
		}
		

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
		return parentSubjectGroups;

	}

}
