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

import org.activiti.engine.IdentityService;
import org.activiti.engine.identity.User;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.igov.model.core.BaseEntityDao;
import org.igov.model.subject.SubjectGroup;
import org.igov.model.subject.SubjectGroupAndUser;
import org.igov.model.subject.SubjectGroupTree;
import org.igov.model.subject.SubjectUser;
import org.igov.model.subject.VSubjectGroupParentNode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.google.common.base.Function;
import com.google.common.collect.Collections2;
import com.google.common.collect.Lists;

/**
 * Сервис получения организационной иерархии
 *
 * @author inna
 */
@Service
public class SubjectGroupService {

	private static final Log LOG = LogFactory.getLog(SubjectGroupService.class);
	private static final long FAKE_ROOT_SUBJECT_ID = 0;

	@Autowired
	private BaseEntityDao<Long> baseEntityDao;

	@Autowired
	private IdentityService identityService;

	public SubjectGroupAndUser getCatalogSubjectGroups(String sID_Group_Activiti, Long deepLevel) {
		List<SubjectGroup> aChildResult = new ArrayList();
		List<SubjectGroupTree> subjectGroupRelations = new ArrayList<>(baseEntityDao.findAll(SubjectGroupTree.class));
		List<VSubjectGroupParentNode> parentSubjectGroups = new ArrayList<>();
		Map<Long, List<SubjectGroup>> subjToNodeMap = new HashMap<>();
		Map<String, Long> mapGroupActiviti = new HashMap<>();
		VSubjectGroupParentNode parentSubjectGroup = null;
		Set<Long> idParentList = new LinkedHashSet<>();
		for (SubjectGroupTree subjectGroupRelation : subjectGroupRelations) {
			final SubjectGroup parent = subjectGroupRelation.getoSubjectGroup_Parent();

			if (parent.getId() != FAKE_ROOT_SUBJECT_ID) {
				parentSubjectGroup = new VSubjectGroupParentNode();
				final SubjectGroup child = subjectGroupRelation.getoSubjectGroup_Child();
				if (!idParentList.contains(parent.getId())) {
					idParentList.add(parent.getId());
					// устанавливаем парентов
					parentSubjectGroup.setGroup(parent);
					// доавляем детей
					parentSubjectGroup.addChild(child);
					parentSubjectGroups.add(parentSubjectGroup);
					// мапа парент -ребенок
					subjToNodeMap.put(parent.getId(), parentSubjectGroup.getChildren());
					// мапа группа-ид парента
					mapGroupActiviti.put(parent.getsID_Group_Activiti(), parent.getId());
				} else {
					for (VSubjectGroupParentNode vSubjectGroupParentNode : parentSubjectGroups) {
						// убираем дубликаты
						if (vSubjectGroupParentNode.getGroup().getId().equals(parent.getId())) {
							// если дубликат парента-добавляем его детей к
							// общему списку
							vSubjectGroupParentNode.getChildren().add(child);
							// мапа парент-ребенок
							subjToNodeMap.put(parent.getId(), vSubjectGroupParentNode.getChildren());
							// мапа группа-ид парента
							mapGroupActiviti.put(parent.getsID_Group_Activiti(), parent.getId());
						}
					}
				}
			}

		}

		// Map<Long, List<SubjectGroup>> subjToNodeMapFiltr = new HashMap<>();
		// достаем ид sID_Group_Activiti которое на вход
		Long groupFiltr = mapGroupActiviti.get(sID_Group_Activiti);
		// детей его детей
		List<SubjectGroup> children = subjToNodeMap.get(groupFiltr);
		// children полный список первого уровня
		if (children != null && !children.isEmpty()) {

			// получаем только ид чилдренов полного списка детей первого уровня
			final List<Long> idChildren = Lists
					.newArrayList(Collections2.transform(children, new Function<SubjectGroup, Long>() {
						@Override
						public Long apply(SubjectGroup subjectGroup) {
							return subjectGroup.getId();
						}
					}));
			aChildResult.addAll(children);
			getChildren(children, idChildren, subjToNodeMap, idParentList, deepLevel.intValue(), 1, aChildResult);

			// subjToNodeMapFiltr.put(groupFiltr, aChildResult);
		}

		// Получаем орг иерархию и людей
		Map<SubjectGroup, List<SubjectUser>> subjUsers = new HashMap<>();
		if (aChildResult != null && !aChildResult.isEmpty()) {
			for (SubjectGroup subjectGroup : aChildResult) {
				List<SubjectUser> aSubjectUser = getUsersByGroupSubject(subjectGroup.getsID_Group_Activiti());
				subjUsers.put(subjectGroup, aSubjectUser);
			}
		}
		List<SubjectUser> userByGroup = Lists.newArrayList();
		Set<String> login = new HashSet<String>();
		if (subjUsers.values() != null && !subjUsers.values().isEmpty()) {
		List<List<SubjectUser>> mapValue = new ArrayList<List<SubjectUser>>(subjUsers.values());
			for (List<SubjectUser> lists : mapValue) {
				for (SubjectUser subjectUser : lists) {
					 if(login.add( subjectUser.getsLogin()) ) { //удаляем дубликаты
						 userByGroup.add(subjectUser);
					 }
				}
			}

		}
		SubjectGroupAndUser subjectGroupAndUser = new SubjectGroupAndUser();
		subjectGroupAndUser.setaSubjectGroup(aChildResult);
		subjectGroupAndUser.setaSubjectUser(userByGroup);

		return subjectGroupAndUser;

	}
	

	/**
	 * Метод структуру иерархии согласно заданной глубины и группы
	 *
	 * @param aChildLevel
	 *            результирующий список со всеми нужными нам детьми
	 * @param anID_ChildLevel
	 *            ид детей уровня на котором мы находимся
	 * @param subjToNodeMap
	 *            мапа соответствия всех ид перентов и список его детей
	 * @param anID_PerentAll
	 *            ид всех перентов
	 * @param deepLevelRequested
	 *            желаемая глубина
	 * @param deepLevelFact
	 *            фактическая глубина
	 * @param result
	 * @return
	 */
	public List<SubjectGroup> getChildren(List<SubjectGroup> aChildLevel, List<Long> anID_ChildLevel,
			Map<Long, List<SubjectGroup>> subjToNodeMap, Set<Long> anID_PerentAll, int deepLevelRequested,
			int deepLevelFact, List<SubjectGroup> result) {

		List<SubjectGroup> aChildLevel_Result = new ArrayList<>();
		List<Long> anID_ChildLevel_Result = new ArrayList<>();
		if (deepLevelRequested == 0) {
			deepLevelRequested = 1000;
		}
		LOG.info("aChildLevel: " + aChildLevel.size() + " anID_ChildLevel: " + anID_ChildLevel);
		if (deepLevelFact < deepLevelRequested) {
			for (Long nID_ChildLevel : anID_ChildLevel) {
				if (anID_PerentAll.contains(nID_ChildLevel)) {
					// LOG.info("nID_ChildLevel: " + nID_ChildLevel);
					// достаем детей детей
					aChildLevel_Result = subjToNodeMap.get(nID_ChildLevel);
					if (aChildLevel_Result != null && !aChildLevel_Result.isEmpty()) {
						LOG.info("nID_ChildLevel: " + nID_ChildLevel + " aChildLevel_Result: "
								+ aChildLevel_Result.size());
						// получаем только ид чилдренов
						anID_ChildLevel_Result = Lists.newArrayList(
								Collections2.transform(aChildLevel_Result, new Function<SubjectGroup, Long>() {
									@Override
									public Long apply(SubjectGroup subjectGroup) {
										return subjectGroup.getId();
									}
								}));
						LOG.info("nID_ChildLevel: " + nID_ChildLevel + " anID_ChildLevel_Result: "
								+ anID_ChildLevel_Result.size());
						// добавляем детей к общему списку детей
						result.addAll(aChildLevel_Result);
						LOG.info("result: " + result.size());
					}
				}
			}
			deepLevelFact++;
			LOG.info("deepLevelFact: " + deepLevelFact + " deepLevelRequested: " + deepLevelRequested);
			if (deepLevelFact < deepLevelRequested) {
				getChildren(aChildLevel_Result, anID_ChildLevel_Result, subjToNodeMap, anID_PerentAll,
						deepLevelRequested, deepLevelFact, result);
			}
		}
		return result;
	}

	/**
	 * Получение списка юзеров по ид группы 
	 * @param sID_Group_Activiti
	 * @return
	 */
	public List<SubjectUser> getUsersByGroupSubject(String sID_Group_Activiti) {

		List<SubjectUser> amsUsers = new ArrayList<>();
		List<User> aoUsers = sID_Group_Activiti != null
				? identityService.createUserQuery().memberOfGroup(sID_Group_Activiti).list()
				: identityService.createUserQuery().list();

		for (User oUser : aoUsers) {
			SubjectUser subjectUser = SubjectUser.BuilderHelper.buildSubjectUser(
					oUser.getId() == null ? "" : oUser.getId(),
					oUser.getFirstName() == null ? "" : oUser.getFirstName(),
					oUser.getLastName() == null ? "" : oUser.getLastName(),
					oUser.getEmail() == null ? "" : oUser.getEmail(), null);
			amsUsers.add(subjectUser);

		}

		return amsUsers;

	}

}
