/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.igov.service.business.subject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import org.activiti.engine.IdentityService;
import org.activiti.engine.identity.User;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.igov.model.core.BaseEntityDao;
import org.igov.model.subject.SubjectGroup;
import org.igov.model.subject.SubjectGroupResultTree;
import org.igov.model.subject.SubjectGroupTree;
import org.igov.model.subject.SubjectHuman;
import org.igov.model.subject.SubjectUser;
import org.igov.model.subject.VSubjectGroupParentNode;
import org.igov.model.subject.organ.SubjectOrgan;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.google.common.base.Function;
import com.google.common.base.Optional;
import com.google.common.base.Predicate;
import com.google.common.collect.Collections2;
import com.google.common.collect.Lists;
import org.igov.model.core.GenericEntityDao;
import org.igov.model.subject.Subject;
import org.igov.model.subject.SubjectHumanDao;
import org.igov.model.subject.organ.SubjectOrganDao;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

/**
 * Сервис получения полной организационной иерархии (родитель - ребенок)
 *
 * @author inna
 */
@Component("subjectGroupTreeService")
@Service
public class SubjectGroupTreeService {

    public static final String ORGAN = "Organ";
    public static final String HUMAN = "Human";
    /**
     * флаг определяющий, что на вход был конкрентный тип ORGAN или HUMAN
     */
    private static boolean isSubjectType = false;
    private static final Logger LOG = LoggerFactory.getLogger(SubjectGroupTreeService.class);

    private static final long FAKE_ROOT_SUBJECT_ID = 0;

    @Autowired
    private BaseEntityDao<Long> baseEntityDao;

    @Autowired
    private IdentityService identityService;

    @Autowired
    private SubjectHumanDao SubjectHumanDao;

    @Autowired
    private SubjectOrganDao SubjectOrganDao;

    @Autowired
    @Qualifier("subjectGroupDao")
    private GenericEntityDao<Long, SubjectGroup> subjectGroupDao;

    //Мапа для укладывания ид родителя и его детей в методе получения иерархии  getChildrenTree
    Map<Long, List<SubjectGroup>> getChildrenTreeRes = new HashMap<>();

    public SubjectGroupResultTree getCatalogSubjectGroupsTree(String sID_Group_Activiti,
            Long deepLevel, String sFind, Boolean bIncludeRoot, Long deepLevelWidth,
            String sSubjectType) {

        /**
         * Лист для ид Subject ORGAN или HUMAN для последующего анализа
         */
        List<Long> resSubjectTypeList = new ArrayList<>();
        List<SubjectGroup> aChildResult = new ArrayList<>();
        //get all SubjectGroupTree
        List<SubjectGroupTree> subjectGroupRelations = new ArrayList<>(baseEntityDao.findAll(SubjectGroupTree.class));
        LOG.info("subjectGroupRelations.size: " + subjectGroupRelations.size());
        SubjectGroupResultTree processSubjectResultTree = new SubjectGroupResultTree();
        if (!subjectGroupRelations.isEmpty()) {
            List<VSubjectGroupParentNode> parentSubjectGroups = new ArrayList<>();
            Map<Long, List<SubjectGroup>> subjToNodeMap = new HashMap<>();
            Map<SubjectGroup, List<SubjectGroup>> parentChildren = new HashMap<>();
            Map<String, Long> mapGroupActiviti = new HashMap<>();
            VSubjectGroupParentNode parentSubjectGroup = null;
            Set<Long> idParentList = new LinkedHashSet<>();
            List<SubjectHuman> subjectHumans = null;
            List<SubjectOrgan> subjectOrgans = null;

            //get all SubjectHuman
            if (HUMAN.equals(sSubjectType)) {
                subjectHumans = new ArrayList<>(baseEntityDao.findAll(SubjectHuman.class));
                LOG.info("subjectHumans.size: " + subjectHumans.size());
                isSubjectType = true;
                if (!subjectHumans.isEmpty()) {
                    List<Long> subjectHumansIdSubj = Lists
                            .newArrayList(Collections2.transform(subjectHumans, new Function<SubjectHuman, Long>() {
                                @Override
                                public Long apply(SubjectHuman subjectHuman) {
                                    return subjectHuman.getoSubject().getId();
                                }
                            }));
                    LOG.info("subjectHumansIdSubj.size: " + subjectHumansIdSubj.size());

                    subjectGroupRelations = Lists
                            .newArrayList(Collections2.filter(subjectGroupRelations, new Predicate<SubjectGroupTree>() {
                                @Override
                                public boolean apply(SubjectGroupTree subjectGroupTree) {
                                    // получить только отфильтрованный
                                    // список по Humans
                                    return Objects.nonNull(subjectGroupTree.getoSubjectGroup_Parent().getoSubject())
                                            && Objects.nonNull(subjectGroupTree.getoSubjectGroup_Child().getoSubject())
                                            && (subjectGroupTree.getoSubjectGroup_Parent().getsID_Group_Activiti().equals(sID_Group_Activiti)
                                            || subjectHumansIdSubj.contains(subjectGroupTree.getoSubjectGroup_Parent().getoSubject().getId()))
                                            && subjectHumansIdSubj.contains(subjectGroupTree.getoSubjectGroup_Child().getoSubject().getId());
                                }
                            }));
                    LOG.info("subjectGroupRelations.size: " + subjectGroupRelations.size());

                    resSubjectTypeList.addAll(subjectHumansIdSubj);
                    LOG.info("resSubjectTypeList.size HUMAN: " + resSubjectTypeList.size());
                }
            }

            //get all SubjectOrgan
            if (ORGAN.equals(sSubjectType)) {
                subjectOrgans = new ArrayList<>(baseEntityDao.findAll(SubjectOrgan.class));
                LOG.info("subjectOrgans.size: " + subjectOrgans.size());
                isSubjectType = true;
                if (!subjectOrgans.isEmpty()) {
                    List<Long> subjectOrgansIdSubj = Lists
                            .newArrayList(Collections2.transform(subjectOrgans, new Function<SubjectOrgan, Long>() {
                                @Override
                                public Long apply(SubjectOrgan subjectOrgan) {
                                    return subjectOrgan.getoSubject().getId();
                                }
                            }));
                    LOG.info("subjectOrgansIdSubj.size: " + subjectOrgansIdSubj.size());

                    subjectGroupRelations = Lists
                            .newArrayList(Collections2.filter(subjectGroupRelations, new Predicate<SubjectGroupTree>() {
                                @Override
                                public boolean apply(SubjectGroupTree subjectGroupTree) {
                                    // получить только отфильтрованный
                                    // список по Organs
                                    return Objects.nonNull(subjectGroupTree.getoSubjectGroup_Parent().getoSubject())
                                            && Objects.nonNull(subjectGroupTree.getoSubjectGroup_Child().getoSubject())
                                            && (subjectGroupTree.getoSubjectGroup_Parent().getsID_Group_Activiti().equals(sID_Group_Activiti)
                                            || subjectOrgansIdSubj.contains(subjectGroupTree.getoSubjectGroup_Parent().getoSubject().getId()))
                                            && subjectOrgansIdSubj.contains(subjectGroupTree.getoSubjectGroup_Child().getoSubject().getId());
                                }
                            }));
                    LOG.info("subjectGroupRelations.size: " + subjectGroupRelations.size());

                    resSubjectTypeList.addAll(subjectOrgansIdSubj);
                    LOG.info("subjectGroupRelations.size ORGAN: " + subjectGroupRelations.size());
                }
            }

            for (SubjectGroupTree subjectGroupRelation : subjectGroupRelations) {

                SubjectGroup parent = subjectGroupRelation.getoSubjectGroup_Parent();
                LOG.info("SubjectGroup parent" + parent);

                if (parent.getId() != FAKE_ROOT_SUBJECT_ID) {
                    LOG.info("SubjectGroup parent" + parent.getsID_Group_Activiti());
                    parentSubjectGroup = new VSubjectGroupParentNode();
                    final SubjectGroup child = subjectGroupRelation.getoSubjectGroup_Child();
                    LOG.info("SubjectGroup parent" + parent.getsID_Group_Activiti()
                            + " child: " + subjectGroupRelation.getoSubjectGroup_Child());
                    if (!idParentList.contains(parent.getId())) {
                        idParentList.add(parent.getId());
                        // устанавливаем парентов
                        parentSubjectGroup.setGroup(parent);
                        // доавляем детей
                        parentSubjectGroup.addChild(child);
                        parentSubjectGroups.add(parentSubjectGroup);
                        // мапа парент -ребенок
                        subjToNodeMap.put(parent.getId(), parentSubjectGroup.getChildren());
                        parentChildren.put(parent, parentSubjectGroup.getChildren());
                        // мапа группа-ид парента
                        mapGroupActiviti.put(parent.getsID_Group_Activiti(), parent.getId());
                        LOG.info("!add mapGroupActiviti: " + parent.getsID_Group_Activiti()
                                + "nID parent: " + parent.getId());
                    } else {
                        for (VSubjectGroupParentNode vSubjectGroupParentNode : parentSubjectGroups) {
                            // убираем дубликаты
                            if (vSubjectGroupParentNode.getGroup().getId().equals(parent.getId())) {
                                // если дубликат парента-добавляем его детей к
                                // общему списку
                                vSubjectGroupParentNode.getChildren().add(child);
                                // мапа парент-ребенок
                                subjToNodeMap.put(parent.getId(), vSubjectGroupParentNode.getChildren());
                                parentChildren.put(parent, parentSubjectGroup.getChildren());
                                // мапа группа-ид парента
                                mapGroupActiviti.put(parent.getsID_Group_Activiti(), parent.getId());
                                LOG.info("!!add mapGroupActiviti: " + parent.getsID_Group_Activiti()
                                        + "nID parent: " + parent.getId());
                            }
                        }
                    }
                }

            }

            // Map<Long, List<SubjectGroup>> subjToNodeMapFiltr = new HashMap<>();
            // достаем ид sID_Group_Activiti которое на вход
            Long groupFiltr = mapGroupActiviti.get(sID_Group_Activiti);
            LOG.info("sID_Group_Activiti index: " + groupFiltr);
            // детей его детей
            List<SubjectGroup> children = new ArrayList<>();

            List<Long> idChildren = new ArrayList<>();
            if (isDisplayRootElement(bIncludeRoot)) {
                SubjectGroup rootSubjectGroup = getRootSubjectGroup(parentChildren, groupFiltr);
                children.add(rootSubjectGroup);
            } else {
                // детей его детей
                children = subjToNodeMap.get(groupFiltr);
            }
            LOG.info("children.size: " + children.size());
            Map<Long, List<SubjectGroup>> hierarchyProcessSubject = new HashMap<>();
            // children полный список первого уровня
            if (children != null && !children.isEmpty()) {

                // получаем только ид чилдренов полного списка детей первого уровня
                idChildren = Lists
                        .newArrayList(Collections2.transform(children, new Function<SubjectGroup, Long>() {
                            @Override
                            public Long apply(SubjectGroup subjectGroup) {
                                return subjectGroup.getId();
                            }
                        }));
                aChildResult.addAll(children);
                LOG.info("idChildren.size: " + idChildren.size());
                hierarchyProcessSubject = getChildrenTree(children, idChildren, subjToNodeMap, idParentList, checkDeepLevel(deepLevel), 1, aChildResult);
                LOG.info("hierarchyProcessSubject" + hierarchyProcessSubject);
            }

            LOG.info("aChildResult {}", aChildResult);
            
            List<SubjectGroup> aChildResultByUser = new ArrayList<>();
            
            if(HUMAN.equals(sSubjectType)){
                aChildResultByUser = filtrChildResultByUser_Human(sFind, aChildResult);
            }
            if(ORGAN.equals(sSubjectType)){
                aChildResultByUser = filtrChildResultByUser_Organ(sFind, aChildResult);
            }
            
            LOG.info("aChildResultByUser {}", aChildResultByUser);

            List<SubjectGroup> resultTree;
            if (sFind != null && !sFind.isEmpty()) {
                resultTree = getSubjectGroupTree(hierarchyProcessSubject, aChildResultByUser);

            } else {
                resultTree = getSubjectGroupTree(hierarchyProcessSubject, aChildResult);
            }
            LOG.info("resultTree: " + resultTree);

            if (isDisplayRootElement(bIncludeRoot)) {
                if (checkDeepLevelWidth(deepLevelWidth) < resultTree.size()) {
                    if (resultTree != null && !resultTree.isEmpty()) {
                        List<SubjectGroup> result = new ArrayList<>();
                        result.add(resultTree.get(checkDeepLevelWidth(deepLevelWidth).intValue()));
                        processSubjectResultTree.setaSubjectGroupTree(result);
                    }
                }
            } else {
                processSubjectResultTree.setaSubjectGroupTree(resultTree);
            }

            LOG.info("processSubjectResultTree" + processSubjectResultTree);

            /**
             * isSubjectType =true- был на вход тип орган или хьман, лист не
             * пустой с ид Subject органа или хьманов, лист содержит groupFiltr
             * возвращаем ответ, иначе ничего не возвращаем
             */
            /*if (isSubjectType && !resSubjectTypeList.isEmpty() 
                    && resSubjectTypeList.contains(groupFiltr)) {
                LOG.info("processSubjectResultTree isSubjectType " + processSubjectResultTree);
                return processSubjectResultTree;
            } else if (!isSubjectType) {
                LOG.info("processSubjectResultTree !isSubjectType " + processSubjectResultTree);
                return processSubjectResultTree;
            } else {
                SubjectGroupResultTree processSubjectResultTreeRes = new SubjectGroupResultTree();
                return processSubjectResultTreeRes;
            }*/
        }

        return processSubjectResultTree;

    }

//------------------------------------------------------------------------------Дополнительные методы-----------------------------------------------------------------
    /**
     * Метод построения иерархии
     *
     * @param hierarchySubjectGroup
     * @param aChildResult
     * @return List<SubjectGroup> - результирующий иерархический список
     */
    private List<SubjectGroup> getSubjectGroupTree(Map<Long, List<SubjectGroup>> hierarchySubjectGroup,
            List<SubjectGroup> aChildResult) {
        for (SubjectGroup subjectGroup : aChildResult) {
            subjectGroup.setaUser(getUsersByGroupSubject(subjectGroup.getsID_Group_Activiti()));
            //получаем по ключу лист детей и устанавливаем 
            List<SubjectGroup> aChildResultByKey = hierarchySubjectGroup.get(subjectGroup.getId());
            if (aChildResultByKey != null && !aChildResultByKey.isEmpty()) {
                subjectGroup.setaSubjectGroup(aChildResultByKey);
            }
        }

        return aChildResult;
    }

    private List<SubjectGroup> filtrChildResultByUser_Organ(String sFind, List<SubjectGroup> aChildResult) {
        List<SubjectGroup> aChildResultByUser = new ArrayList<>();
        if (aChildResult != null) 
        {
            if (sFind != null && !sFind.isEmpty()) {
                for(SubjectGroup oSubjecetGroup : aChildResult){
                   if(oSubjecetGroup.getName() != null && oSubjecetGroup.getName().toLowerCase().contains(sFind.toLowerCase())){
                       aChildResultByUser.add(oSubjecetGroup);
                   } 
                } 
            }
        }
        
        return aChildResultByUser;
    }
    
    /**
     * Метод получения отфильтрованного списка объектов по заданному условию
     * поиска
     *
     * @param sFind - текст поиска в ФИО
     * @param aChildResult - результирующий лист, который фильтруем
     * @return List<SubjectGroup> - отфильтрованный список по строке поиска в
     * фио
     */
    private List<SubjectGroup> filtrChildResultByUser_Human(String sFind, List<SubjectGroup> aChildResult) {
        List<SubjectGroup> aChildResultByUser = new ArrayList<>();
        if (aChildResult != null && !aChildResult.isEmpty()) {
            if (sFind != null && !sFind.isEmpty()) {
                for (SubjectGroup subjectGroup : aChildResult) {
                    List<SubjectUser> aSubjectUser = getUsersByGroupSubject(
                            subjectGroup.getsID_Group_Activiti());
                    LOG.info("aSubjectUser {}", aSubjectUser);
                    final List<SubjectUser> subjectUserFiltr = Lists
                            .newArrayList(Collections2.filter(aSubjectUser, new Predicate<SubjectUser>() {
                                @Override
                                public boolean apply(SubjectUser subjectUser) {
                                    // получить только отфильтрованный
                                    // список по
                                    // sFind в фио
                                    return subjectUser.getsFirstName().toLowerCase().contains(sFind.toLowerCase());
                                }
                            }));
                    LOG.info("subjectUserFiltr {}", subjectUserFiltr);
                    // получаем только их логины
                    final List<String> sFindLogin = Lists.newArrayList(
                            Collections2.transform(subjectUserFiltr, new Function<SubjectUser, String>() {
                                @Override
                                public String apply(SubjectUser subjectUser) {
                                    return subjectUser.getsLogin();
                                }
                            }));

                    // и оставляем только processSubject чьи логины
                    // содержаться
                    // в отфильтрованном списке
                    LOG.info("sFindLogin {}", sFindLogin);
                    LOG.info("subjectGroup.getsID_Group_Activiti {}", subjectGroup.getsID_Group_Activiti());
                    
                    if (sFindLogin.contains(subjectGroup.getsID_Group_Activiti())) {
                        aChildResultByUser.add(subjectGroup);

                    }
                }
            }
        }
        return aChildResultByUser;
    }

    /**
     * проверяем входящий параметр deepLevel
     *
     * @param deepLevel
     * @return
     */
    private Long checkDeepLevel(Long deepLevel) {
        if (deepLevel == null || deepLevel.intValue() == 0) {
            return 1000L;
        }
        return deepLevel;
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
     * @return Map<Long, List<ProcessSubject>> - id-parent-->list child
     */
    public Map<Long, List<SubjectGroup>> getChildrenTree(List<SubjectGroup> aChildLevel, List<Long> anID_ChildLevel,
            Map<Long, List<SubjectGroup>> subjToNodeMap, Set<Long> anID_PerentAll, Long deepLevelRequested,
            int deepLevelFact, List<SubjectGroup> result) {
        List<SubjectGroup> aChildLevel_Result = new ArrayList<>();
        List<Long> anID_ChildLevel_Result = new ArrayList<>();
        if (deepLevelFact < deepLevelRequested.intValue()) {
            for (Long nID_ChildLevel : anID_ChildLevel) {
                if (anID_PerentAll.contains(nID_ChildLevel)) {
                    // достаем детей детей
                    aChildLevel_Result = subjToNodeMap.get(nID_ChildLevel);
                    if (aChildLevel_Result != null && !aChildLevel_Result.isEmpty()) {
                        // получаем только ид чилдренов
                        List<Long> anID_Child = Lists.newArrayList(
                                Collections2.transform(aChildLevel_Result, new Function<SubjectGroup, Long>() {
                                    @Override
                                    public Long apply(SubjectGroup subjectGroup) {
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
     *
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

    /**
     * Проверка флага на отображение рутового елемента:
     * <p>
     * <b>если null - устанавливать true для отображения по умолчанию</b>
     *
     * @param bIncludeRoot - флаг который прихоидит на вход (true - отображаем,
     * false - нет)
     * @return bIncludeRoot - фактическое значение флага
     */
    private static boolean isDisplayRootElement(Boolean bIncludeRoot) {
        if (bIncludeRoot == null) {
            return Boolean.TRUE;
        }
        return bIncludeRoot;
    }

    /**
     * метод возвращающий значение deepLevelWidth
     *
     * @param deepLevelWidth - ширина иерархии
     * @return deepLevelWidth - возвращается 1 (берем первый елемент из листа с
     * объектами по иерархии) если на вход передали null или 0
     */
    private Long checkDeepLevelWidth(Long deepLevelWidth) {
        if (deepLevelWidth == null || deepLevelWidth.intValue() == 0 || deepLevelWidth.intValue() == 1) {
            return 0L;
        }
        return deepLevelWidth - 1;
    }

    /**
     * Метод получения списка рутового елемента иерархии
     *
     * @param parentChildren - список парентов
     * @param groupFiltr - ид, по которому строится иерархия
     * @return ProcessSubject - рутовый елемент
     */
    private SubjectGroup getRootSubjectGroup(Map<SubjectGroup, List<SubjectGroup>> parentChildren,
            Long groupFiltr) {

        SubjectGroup rootElement = null;
        for (Map.Entry<SubjectGroup, List<SubjectGroup>> entry : parentChildren.entrySet()) {
            rootElement = entry.getKey();
            if (rootElement.getId().equals(groupFiltr)) {
                return rootElement;
            }
        }
        return rootElement;
    }

    public String getSubjectType(String sID_Group_Activiti) {
        try {
            SubjectGroup oSubjectGroup = subjectGroupDao.findByExpected("sID_Group_Activiti", sID_Group_Activiti);
            Subject oSubject = oSubjectGroup.getoSubject();
            LOG.info("oSubjectGroup in getSubjectType is " + oSubject.getId());

            Optional<SubjectHuman> oSubjectHuman = SubjectHumanDao.findBy("oSubject", oSubject);
            LOG.info("sID_Group_Activiti: {} oSubjectHuman isPresent: {}", sID_Group_Activiti, oSubjectHuman.isPresent());

            if (oSubjectHuman.isPresent()) {
                return HUMAN;
            } else {
                Optional<SubjectOrgan> oSubjectOrgan = SubjectOrganDao.findBy("oSubject", oSubject);
                LOG.info("sID_Group_Activiti: {} oSubjectOrgan isPresent: {}", sID_Group_Activiti, oSubjectOrgan.isPresent());
                if (oSubjectOrgan.isPresent()) {
                    return ORGAN;
                } else {
                    throw new RuntimeException("Can't find any SubjectHuman or SubjectOrgan for sID_Group_Activiti = "
                            + sID_Group_Activiti + " Subject = " + oSubject.getId());

                }
            }
        } catch (Exception oException) {
            LOG.error("ERROR:" + oException.getMessage() + " (sID_Group_Activiti=" + sID_Group_Activiti + ")");
            LOG.error("ERROR: ", oException);
            throw oException;
        }
    }

}
