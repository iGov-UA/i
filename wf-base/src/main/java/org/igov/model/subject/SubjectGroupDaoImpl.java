package org.igov.model.subject;


import org.igov.model.core.GenericEntityDao;
import org.springframework.stereotype.Repository;

/**
 *
 * @author idenysenko
 */
@Repository
public class SubjectGroupDaoImpl extends GenericEntityDao<Long, SubjectGroup> implements SubjectGroupDao {

    public SubjectGroupDaoImpl() {
        super(SubjectGroup.class);
    }
    
}
