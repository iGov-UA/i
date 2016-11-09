package org.igov.model.subject;

import java.util.List;

import org.igov.model.core.GenericEntityDao;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

@Repository
public class SubjectGroupTreeDaoImpl extends GenericEntityDao<Long, SubjectGroupTree> implements SubjectGroupTreeDao{
	private static final Logger LOG = LoggerFactory.getLogger(SubjectGroupTreeDaoImpl.class);

    public SubjectGroupTreeDaoImpl() {
        super(SubjectGroupTree.class);
    }

    
    /**
     * Получаем список дочерних групп по id родителя
     */
	@Override
	public List<SubjectGroupTree> getSubjectChildByParentId(Long nID_SubjectGroup_Parent) {
		
		List<SubjectGroupTree> list = findAllBy("nID_SubjectGroup_Parent", nID_SubjectGroup_Parent);
		LOG.info("SubjectGroupTreeDaoImplllllllllllllll " + list);
		return list;
	}
    
    
}
