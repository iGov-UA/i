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

import org.activiti.engine.IdentityService;
import org.activiti.engine.identity.User;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.igov.model.core.BaseEntityDao;
import org.igov.model.process.ProcessSubject;
import org.igov.model.process.ProcessSubjectParentNode;
import org.igov.model.process.ProcessSubjectResultTree;
import org.igov.model.process.ProcessSubjectTree;
import org.igov.model.process.ProcessUser;
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
public class ProcessSubjectTreeService {

    private static final Log LOG = LogFactory.getLog(ProcessSubjectTreeService.class);
    private static final long FAKE_ROOT_PROCESS_ID = 0;

    @Autowired
    private BaseEntityDao<Long> baseEntityDao;

    @Autowired
    private IdentityService identityService;


    //Мапа для укладывания ид родителя и его детей в методе получения иерархии  getChildrenTree
    Map<Long, List<ProcessSubject>> getChildrenTreeRes = new HashMap<>();
    
    
    /**
     * Сервис получения полной иерархии (родитель - ребенок)
     *
     * @param snID_Process_Activiti - ид процесса
     * @param deepLevel - глубина выборки
     * @param sFind - текст поиска (искать в ФИО, по наличию вхождения текста в
     * ФИО)
     * @return
     */
    public ProcessSubjectResultTree getCatalogProcessSubjectTree(String snID_Process_Activiti, Long deepLevel, String sFind, Boolean bIncludeRoot, Long deepLevelWidth) {

        List<ProcessSubject> aChildResult = new ArrayList<>();
        List<ProcessSubjectTree> processSubjectRelations = new ArrayList<>(baseEntityDao.findAll(ProcessSubjectTree.class));
        List<ProcessSubjectParentNode> parentProcessSubjects = new ArrayList<>();
        Map<Long, List<ProcessSubject>> subjToNodeMap = new HashMap<>();
        Map<ProcessSubject, List<ProcessSubject>> parentChildren = new HashMap<>();
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
                    // мапа парент_id -ребенок
                    subjToNodeMap.put(parent.getId(), parentProcessSubject.getChildren());
                    parentChildren.put(parent, parentProcessSubject.getChildren());
                    // мапа группа-ид парента
                    mapGroupActiviti.put(parent.getSnID_Process_Activiti(), parent.getId());
                } else {
                    for (ProcessSubjectParentNode processSubjectParentNode : parentProcessSubjects) {
                        // убираем дубликаты
                        if (processSubjectParentNode.getGroup().getId().equals(parent.getId())) {
                            // если дубликат парента-добавляем его детей к
                            // общему списку
                            processSubjectParentNode.getChildren().add(child);
                            // мапа парент_id -ребенок
                            subjToNodeMap.put(parent.getId(), processSubjectParentNode.getChildren());
                            parentChildren.put(parent, parentProcessSubject.getChildren());
                            // мапа группа-ид парента
                            mapGroupActiviti.put(parent.getSnID_Process_Activiti(), parent.getId());
                        }
                    }
                }
            }

        }
        // достаем ид snID_Process_Activiti которое на вход
       Long groupFiltr = mapGroupActiviti.get(snID_Process_Activiti);
       //получаем его детей
        List<ProcessSubject> children = new ArrayList<>();
        
        List<Long> idChildren = new ArrayList<>();
		if (isDisplayRootElement(bIncludeRoot)) {
			ProcessSubject rootProcessSubject = getRootProcessSubject(parentChildren, groupFiltr);
			children.add(rootProcessSubject);
		} else {
			// детей его детей
			children = subjToNodeMap.get(groupFiltr);
		}

        Map<Long, List<ProcessSubject>> hierarchyProcessSubject = new HashMap<>();
        
        // children полный список первого уровня
        if (children != null && !children.isEmpty()) {
        	idChildren = Lists
                    .newArrayList(Collections2.transform(children, new Function<ProcessSubject, Long>() {
                        @Override
                        public Long apply(ProcessSubject subjectGroup) {
                            return subjectGroup.getId();
                        }
                    }));
            
            
          aChildResult.addAll(children);
          hierarchyProcessSubject = getChildrenTree(children, idChildren, subjToNodeMap, idParentList, checkDeepLevel(deepLevel), 1, aChildResult);

            LOG.info("subjToNodeMap " + subjToNodeMap);
            LOG.info("aChildResult " + aChildResult);

        }

        List<ProcessSubject> aChildResultByUser = filtrChildResultByUser(sFind, aChildResult);

        ProcessSubjectResultTree processSubjectResultTree = new ProcessSubjectResultTree();
        List<ProcessSubject> resultTree = new ArrayList<>();
        if (sFind != null && !sFind.isEmpty()) {
        	resultTree = getProcessSubjectTree(hierarchyProcessSubject, aChildResultByUser);
        	
        }else {
        	resultTree = getProcessSubjectTree(hierarchyProcessSubject, aChildResult);
        }
        
		if (checkDeepLevelWidth(deepLevelWidth) < resultTree.size()) {
			if (resultTree != null && !resultTree.isEmpty()) {
				processSubjectResultTree
						.setaProcessSubject(resultTree.get(checkDeepLevelWidth(deepLevelWidth).intValue()));
			}
		}
        return processSubjectResultTree;

    }



	



//=====================================================Дополнительные методы=============================================================================

    
    /**
     * Метод построения иерархии
     * @param hierarchyProcessSubject
     * @param aChildResult
     * @return List<ProcessSubject> - результирующий иерархический список
     */
    public List<ProcessSubject> getProcessSubjectTree(Map<Long, List<ProcessSubject>> hierarchyProcessSubject,
			List<ProcessSubject> aChildResult) {
		for (ProcessSubject processSubject : aChildResult) {
            processSubject.setaUser(getUsersByGroupSubject(processSubject.getsLogin()));
            //получаем по ключу лист детей и устанавливаем 
            List<ProcessSubject> aChildResultByKey = hierarchyProcessSubject.get(processSubject.getId());
            if (aChildResultByKey != null && !aChildResultByKey.isEmpty()) {
                processSubject.setaProcessSubj(aChildResultByKey);
            }
        }
		return aChildResult;
	}

    /**
     * Проверка флага на отображение рутового елемента:
     * 
     *  <b>если null - устанавливать true для отображения по умолчанию</b>
     * @param bIncludeRoot - флаг который прихоидит на вход (true - отображаем, false - нет)
     * @return bIncludeRoot - фактическое значение флага
     */

    public static boolean isDisplayRootElement(Boolean bIncludeRoot) {
		if (bIncludeRoot == null) {
			return Boolean.TRUE;
        }
		return bIncludeRoot;
	}
    
    
    /**
     * Метод получения отфильтрованного списка объектов по заданному условию поиска
     * @param sFind - текст поиска в ФИО
     * @param aChildResult - результирующий лист, который фильтруем
     * @return List<ProcessSubject> - отфильтрованный список по строке поиска в фио
     */
	public List<ProcessSubject> filtrChildResultByUser(String sFind, List<ProcessSubject> aChildResult) {
		List<ProcessSubject> aChildResultByUser = new ArrayList<>();
        if (aChildResult != null && !aChildResult.isEmpty()) {
            if (sFind != null && !sFind.isEmpty()) {
                for (ProcessSubject processSubject : aChildResult) {
                    List<ProcessUser> aSubjectUser = getUsersByGroupSubject(
                            processSubject.getSnID_Process_Activiti());
                    final List<ProcessUser> processUserFiltr = Lists
                            .newArrayList(Collections2.filter(aSubjectUser, new Predicate<ProcessUser>() {
                                @Override
                                public boolean apply(ProcessUser processUser) {
                                    // получить только отфильтрованный
                                    // список по
                                    // sFind в фио
                                    return processUser.getsFirstName().toLowerCase().contains(sFind.toLowerCase());
                                }
                            }));
                    // получаем только их логины
                    final List<String> sFindLogin = Lists.newArrayList(
                            Collections2.transform(processUserFiltr, new Function<ProcessUser, String>() {
                                @Override
                                public String apply(ProcessUser processUser) {
                                    return processUser.getsLogin();
                                }
                            }));

                    // и оставляем только processSubject чьи логины
                    // содержаться
                    // в отфильтрованном списке
                    if (sFindLogin.contains(processSubject.getsLogin())) {
                        aChildResultByUser.add(processSubject);

                    }
                }
            }
        }
		return aChildResultByUser;
	}

   /**
    * Метод получения списка рутового елемента иерархии
    * @param parentChildren - список парентов
    * @param groupFiltr - ид, по которому строится иерархия 
    * @return ProcessSubject - рутовый елемент
    */
    public ProcessSubject getRootProcessSubject(Map<ProcessSubject, List<ProcessSubject>> parentChildren,
            Long groupFiltr) {

    	ProcessSubject rootElement = null;
        for (Map.Entry<ProcessSubject, List<ProcessSubject>> entry : parentChildren.entrySet()) {
        	rootElement = entry.getKey();
            if (rootElement.getId().equals(groupFiltr)) {
            	return rootElement;
            }
        }
        return rootElement;
    }


    /**
     * метод возвращающий значение deepLevel
     * @param deepLevel - глубина иерархии
     * @return deepLevel - возвращается 1000 если на вход передали null или 0
     */
    public Long checkDeepLevel(Long deepLevel) {
        if (deepLevel == null || deepLevel.intValue() == 0) {
            return 1000L;
        }
        return deepLevel;
    }
    
    /**
     * метод возвращающий значение deepLevelWidth
     * @param deepLevelWidth - ширина иерархии
     * @return deepLevelWidth - возвращается 1 если на вход передали null или 0
     */
    public Long checkDeepLevelWidth(Long deepLevelWidth) {
        if (deepLevelWidth == null || deepLevelWidth.intValue() == 0 || deepLevelWidth.intValue()==1) {
            return 0L;
        }
        return deepLevelWidth-1;
    }


    /**
     * Метод структуру иерархии согласно заданной глубины и группы
     *
     * @param aChildLevel результирующий список со всеми нужными нам детьми
     * @param anID_ChildLevel ид детей уровня на котором мы находимся
     * @param subjToNodeMap мапа соответствия всех ид перентов и список его
     * детей
     * @param anID_PerentAll ид всех перентов
     * @param deepLevelRequested желаемая глубина
     * @param deepLevelFact фактическая глубина
     * @param result
     * @return Map<Long, List<ProcessSubject>>  - id-parent-->list child
     */
    
    public Map<Long, List<ProcessSubject>> getChildrenTree(List<ProcessSubject> aChildLevel, List<Long> anID_ChildLevel,
            Map<Long, List<ProcessSubject>> subjToNodeMap, Set<Long> anID_PerentAll, Long deepLevelRequested,
            int deepLevelFact, List<ProcessSubject> result) {
    	 List<ProcessSubject> aChildLevel_Result = new ArrayList<>();
        List<Long> anID_ChildLevel_Result = new ArrayList<>();
        if (deepLevelFact < deepLevelRequested.intValue()) {
            for (Long nID_ChildLevel : anID_ChildLevel) {
                if (anID_PerentAll.contains(nID_ChildLevel)) {
                    // достаем детей детей
                	aChildLevel_Result = subjToNodeMap.get(nID_ChildLevel);
                    if (aChildLevel_Result != null && !aChildLevel_Result.isEmpty()) {
                        // получаем только ид чилдренов
                        List<Long> anID_Child = Lists.newArrayList(
                                Collections2.transform(aChildLevel_Result, new Function<ProcessSubject, Long>() {
                                    @Override
                                    public Long apply(ProcessSubject subjectGroup) {
                                        return subjectGroup.getId();
                                    }
                                }));
                        //если anID_ChildLevel больше 1, то всех ид складываем в лист
                        anID_ChildLevel_Result.addAll(anID_Child);
                        // добавляем детей к общему списку детей
                        result.addAll(aChildLevel_Result);
                        getChildrenTreeRes.put(nID_ChildLevel, aChildLevel_Result);
                    }
                }
            }
            
            deepLevelFact++;
            if (deepLevelFact < deepLevelRequested.intValue()) {
                getChildrenTree(aChildLevel_Result, anID_ChildLevel_Result, subjToNodeMap, anID_PerentAll,
                        checkDeepLevel(deepLevelRequested), deepLevelFact, result);
            }
           
        }
        return getChildrenTreeRes;
    }

   /**
    * Получение списка юзеров по ид группы
    * @param snID_Process_Activiti -ид группы
    * @return List<ProcessUser> - список юзеров
    */
    public List<ProcessUser> getUsersByGroupSubject(String snID_Process_Activiti) {

        List<ProcessUser> amsUsers = new ArrayList<>();
        List<User> aoUsers = snID_Process_Activiti != null
                ? identityService.createUserQuery().memberOfGroup(snID_Process_Activiti).list()
                : identityService.createUserQuery().list();

        for (User oUser : aoUsers) {
            ProcessUser processUser = ProcessUser.BuilderHelper.buildSubjectUser(
                    oUser.getId() == null ? "" : oUser.getId(),
                    oUser.getFirstName() == null ? "" : oUser.getFirstName(),
                    oUser.getLastName() == null ? "" : oUser.getLastName(),
                    oUser.getEmail() == null ? "" : oUser.getEmail(), null);
            amsUsers.add(processUser);

        }

        return amsUsers;

    }


}
