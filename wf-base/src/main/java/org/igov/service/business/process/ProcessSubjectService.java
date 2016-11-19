/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.igov.service.business.process;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.igov.model.core.BaseEntityDao;
import org.igov.model.process.ProcessSubject;
import org.igov.model.process.ProcessSubjectDao;
import org.igov.model.process.ProcessSubjectParentNode;
import org.igov.model.process.ProcessSubjectResult;
import org.igov.model.process.ProcessSubjectTree;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.google.common.base.Function;
import com.google.common.collect.Collections2;
import com.google.common.collect.Lists;

/**
 *
 * @author inna
 */
@Service
public class ProcessSubjectService {
	private static final Log LOG = LogFactory.getLog(ProcessSubjectService.class);
	private static final long FAKE_ROOT_PROCESS_ID = 0;
	
	@Autowired
	private BaseEntityDao<Long> baseEntityDao;
	
	 @Autowired
	 private ProcessSubjectDao processSubjectDao;
	
	public ProcessSubjectResult getCatalogProcessSubject(String snID_Process_Activiti, Long deepLevel) {
		
		List<ProcessSubject> aChildResult = new ArrayList();
		List<ProcessSubjectTree> processSubjectRelations = new ArrayList<>(baseEntityDao.findAll(ProcessSubjectTree.class));
		List<ProcessSubjectParentNode> parentProcessSubjects = new ArrayList<>();
		Map<Long, List<ProcessSubject>> subjToNodeMap = new HashMap<>();
		Map<String, Long> mapGroupActiviti = new HashMap<>();
		ProcessSubjectParentNode parentProcessSubject = null;
		Set<Long> idParentList = new LinkedHashSet<>();
		for (ProcessSubjectTree processSubjectTree : processSubjectRelations) {
			final ProcessSubject parent = processSubjectTree.getProcessSubjectParent();

			if (parent.getId() != FAKE_ROOT_PROCESS_ID) {
				parentProcessSubject = new ProcessSubjectParentNode();
				final ProcessSubject child = processSubjectTree.getProcessSubjectChild();
				if (!idParentList.contains(parent.getId())) {
					idParentList.add(parent.getId());
					// устанавливаем парентов
					parentProcessSubject.setGroup(parent);
					// доавляем детей
					parentProcessSubject.addChild(child);
					parentProcessSubjects.add(parentProcessSubject);
					// мапа парент -ребенок
					subjToNodeMap.put(parent.getId(), parentProcessSubject.getChildren());
					// мапа группа-ид парента
					mapGroupActiviti.put(parent.getID_Process_Activiti(), parent.getId());
				} else {
					for (ProcessSubjectParentNode processSubjectParentNode : parentProcessSubjects) {
						// убираем дубликаты
						if (processSubjectParentNode.getGroup().getId().equals(parent.getId())) {
							// если дубликат парента-добавляем его детей к
							// общему списку
							processSubjectParentNode.getChildren().add(child);
							// мапа парент-ребенок
							subjToNodeMap.put(parent.getId(), processSubjectParentNode.getChildren());
							// мапа группа-ид парента
							mapGroupActiviti.put(parent.getID_Process_Activiti(), parent.getId());
						}
					}
				}
			}

		}

		// достаем ид snID_Process_Activiti которое на вход
		Long groupFiltr = mapGroupActiviti.get(snID_Process_Activiti);
		// детей его детей
		List<ProcessSubject> children = subjToNodeMap.get(groupFiltr);
		// children полный список первого уровня
		if (children != null && !children.isEmpty()) {

			// получаем только ид чилдренов полного списка детей первого уровня
			final List<Long> idChildren = Lists
					.newArrayList(Collections2.transform(children, new Function<ProcessSubject, Long>() {
						@Override
						public Long apply(ProcessSubject subjectGroup) {
							return subjectGroup.getId();
						}
					}));
			aChildResult.addAll(children);
			getChildren(children, idChildren, subjToNodeMap, idParentList, deepLevel.intValue(), 1, aChildResult);

		}
		ProcessSubjectResult processSubjectResult = new ProcessSubjectResult();
		processSubjectResult.setaProcessSubject(aChildResult);
		return processSubjectResult;
		
	}
	
	
	/**
	 * Сохранить сущность
	 * @param snID_Process_Activiti__Parent
	 * @param sLogin
	 * @param sDatePlan
	 * @param nOrder
	 * @return
	 */
	 public Long setProcessSubject(String snID_Process_Activiti, String sLogin, String sDatePlan, Long nOrder){
		 
		 DateTimeFormatter formatter = DateTimeFormat.forPattern("dd-MM-yyyy");
		 DateTime dtDatePlan = formatter.parseDateTime(sDatePlan);
		 
		return processSubjectDao.setProcessSubject(snID_Process_Activiti, sLogin, dtDatePlan, nOrder);
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
	public List<ProcessSubject> getChildren(List<ProcessSubject> aChildLevel, List<Long> anID_ChildLevel,
			Map<Long, List<ProcessSubject>> subjToNodeMap, Set<Long> anID_PerentAll, int deepLevelRequested,
			int deepLevelFact, List<ProcessSubject> result) {

		List<ProcessSubject> aChildLevel_Result = new ArrayList<>();
		List<Long> anID_ChildLevel_Result = new ArrayList<>();
		if (deepLevelRequested == 0) {
			deepLevelRequested = 1000;
		}
		LOG.info("aChildLevel: " + aChildLevel.size() + " anID_ChildLevel: " + anID_ChildLevel);
		if (deepLevelFact < deepLevelRequested) {
			for (Long nID_ChildLevel : anID_ChildLevel) {
				if (anID_PerentAll.contains(nID_ChildLevel)) {
					// достаем детей детей
					aChildLevel_Result = subjToNodeMap.get(nID_ChildLevel);
					if (aChildLevel_Result != null && !aChildLevel_Result.isEmpty()) {
						LOG.info("nID_ChildLevel: " + nID_ChildLevel + " aChildLevel_Result: "
								+ aChildLevel_Result.size());
						// получаем только ид чилдренов
						anID_ChildLevel_Result = Lists.newArrayList(
								Collections2.transform(aChildLevel_Result, new Function<ProcessSubject, Long>() {
									@Override
									public Long apply(ProcessSubject subjectGroup) {
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
     * Задать логин
     *
     * @param snID_Process_Activiti
     * @param sLogin
     * @return
     */
    public ProcessSubject setProcessSubjectLogin(String snID_Process_Activiti, String sLogin) {
        return processSubjectDao.setProcessSubjectLogin(snID_Process_Activiti, sLogin);
    }

    /**
     * Задать номер заявки
     *
     * @param snID_Process_Activiti
     * @param nOrder
     * @return
     */
    public ProcessSubject setProcessSubjectOrder(String snID_Process_Activiti, Long nOrder) {
        return processSubjectDao.setProcessSubjectOrder(snID_Process_Activiti, nOrder);
    }

    /**
     * Задать статус
     *
     * @param snID_Process_Activiti
     * @param nID_ProcessSubjectStatus
     * @return
     */
    public ProcessSubject setProcessSubjectStatus(String snID_Process_Activiti, Long nID_ProcessSubjectStatus) {
        return processSubjectDao.setProcessSubjectStatus(snID_Process_Activiti, nID_ProcessSubjectStatus);
    }

    /**
     * Задать дату
     *
     * @param snID_Process_Activiti
     * @param sDatePlan
     * @return
     */
    public ProcessSubject setProcessSubjectDatePlan(String snID_Process_Activiti, String sDatePlan) {
        
        DateTimeFormatter formatter = DateTimeFormat.forPattern("dd-MM-yyyy");
        DateTime dtDatePlan = formatter.parseDateTime(sDatePlan);
        
        return processSubjectDao.setProcessSubjectDatePlan(snID_Process_Activiti, dtDatePlan);
    }

 
}
