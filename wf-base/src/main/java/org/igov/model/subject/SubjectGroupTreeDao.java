package org.igov.model.subject;

import java.util.List;

import org.igov.model.core.EntityDao;

public interface SubjectGroupTreeDao extends EntityDao<Long, SubjectGroupTree>{
	
	List<SubjectGroupTree> getSubjectChildByParentId(Long nID_SubjectGroup_Parent);

}
