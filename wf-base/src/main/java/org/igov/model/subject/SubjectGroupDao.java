package org.igov.model.subject;

import java.util.List;

import org.igov.model.core.EntityDao;


public interface SubjectGroupDao extends EntityDao<Long, SubjectGroup>{
	
	
	SubjectGroup getSubjectGroupsByGroupActiviti(String sID_Group_Activiti);
	
	List<SubjectGroup> getSubjectGroupsByIdChild(List<String> idChild);
    
}
