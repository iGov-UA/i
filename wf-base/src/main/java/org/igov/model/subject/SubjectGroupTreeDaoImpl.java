package org.igov.model.subject;

import java.util.List;

import org.igov.model.core.GenericEntityDao;


public class SubjectGroupTreeDaoImpl extends GenericEntityDao<Long, SubjectGroupTree> implements SubjectGroupTreeDao{

    public SubjectGroupTreeDaoImpl() {
        super(SubjectGroupTree.class);
    }

    
    /**
     * Получаем список дочерних групп по id родителя
     */
	@Override
	public List<SubjectGroupTree> getSubjectChildByParentId(Long nID_SubjectGroup_Parent) {
		
		return findAllBy("nID_SubjectGroup_Parent", nID_SubjectGroup_Parent);
	}
    
    
}
