package org.igov.model.subject;

import org.igov.model.core.GenericEntityDao;
import org.springframework.stereotype.Repository;

/**
 *
 * @author idenysenko
 */
@Repository
public class SubjectGroupTreeDaoImpl extends GenericEntityDao<Long, SubjectGroupTree> implements SubjectGroupTreeDao{

    public SubjectGroupTreeDaoImpl() {
        super(SubjectGroupTree.class);
    }
        
}
