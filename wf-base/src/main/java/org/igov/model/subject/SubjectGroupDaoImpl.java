package org.igov.model.subject;

import java.util.ArrayList;
import java.util.List;

import org.igov.model.core.GenericEntityDao;
import org.springframework.stereotype.Repository;

@Repository
public class SubjectGroupDaoImpl extends GenericEntityDao<Long, SubjectGroup> implements SubjectGroupDao {

    public SubjectGroupDaoImpl() {
        super(SubjectGroup.class);
    }

    /**
     * Получаем объект по sID_Group_Activiti
     */
	/*@Override
	public SubjectGroup getSubjectGroupsByGroupActiviti(String sID_Group_Activiti) {
		
		return findBy("sID_Group_Activiti", sID_Group_Activiti).orNull();
	}
	
	
	*//**
	 * Получаем список SubjectGroup по списку дочерних групп 
	 * nIDChilds - ид дочерних групп
	 *//*
	@Override
	public List<SubjectGroup> getSubjectGroupsByIdChild(List<String> nIDChilds) {
		
		List<SubjectGroup> subjectGroupList = new ArrayList<>();
		
		for(String nID:nIDChilds) {
			SubjectGroup subjectGroup = findBy("nID", nID).orNull();
			subjectGroupList.add(subjectGroup);
		}
		
		return subjectGroupList;
	}*/

}
