/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.igov.service.business.subject;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;
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
				if(!idChildrenList.contains(child)) {
					idChildrenList.add(child.getId());
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
		
		SortedSet<VSubjectGroupParentNode> nodeSet = new TreeSet<VSubjectGroupParentNode>(new Comparator<VSubjectGroupParentNode>() {
		    public int compare(VSubjectGroupParentNode node1, VSubjectGroupParentNode node2) {

		        if (node1.getGroup() == null) {
		            if (node2.getGroup() == null) {
		                return  node1.getGroup().getId().compareTo(node2.getGroup().getId());
		            }
		            return -1;
		        }

		        if (node2.getGroup() == null) return 1;

		        int parentCompare = node1.getGroup().getId()
		                .compareTo(node2.getGroup().getId());

		        if (parentCompare == 0)
		            return node1.getGroup().getId().compareTo(node2.getGroup().getId());

		        return parentCompare;
		    }
		});
		
		nodeSet.addAll(parentSubjectGroups);
		
		Map<SubjectGroup, List<SubjectGroup>> map = new HashMap<SubjectGroup, List<SubjectGroup>>();

		for(VSubjectGroupParentNode node : nodeSet)
		{
		    if(map.get(node.getGroup())==null)
		    {
		        map.put(node.getGroup(), new ArrayList<SubjectGroup>());
		    }
		    map.get(node.getGroup()).add(node.getGroup());
		    SubjectGroup parentNode = node.getGroup();
		    while(parentNode!=null)
		    {
		        map.get(parentNode).add(node.getGroup());
		        parentNode = node.getGroup();
		    }
		}
		
		List<List<SubjectGroup>> values = map.values().stream().collect(Collectors.toList());
		
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
		
		/*for (VSubjectGroupParentNode vSubjectGroupParentNode : parentSubjectGroups) {
			for (VSubjectGroupParentNode vSubjectGroupParentNodef : parentSubjectGroupsFilltr) {
				for (SubjectGroup subjectGroup : vSubjectGroupParentNodef.getChildren()) {
					if (subjectGroup.getId().equals(vSubjectGroupParentNode.getGroup().getId())) {
						countChild++;
						if (countChild.compareTo(deepLevel) < 0) {
							vSubjectGroupParentNodef.getChildren().add(vSubjectGroupParentNode.getChildren().get(countChild.intValue()));
						}
					}
				}
			}

		}*/

		VSubjectGroupTreeResult subjectGroupTreeResult = new VSubjectGroupTreeResult();
		parentSubjectGroup.accept(subjectGroupTreeResult);
		return values;

	}

}
