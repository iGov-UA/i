package org.igov.model.relation;

import org.igov.model.core.GenericEntityDao;
import org.springframework.stereotype.Repository;

/**
 *
 * @author Kovilin
 */
@Repository
public class RelationDaoImpl extends GenericEntityDao<Long, Relation> implements RelationDao{
    
    public RelationDaoImpl() {
        super(Relation.class);
    }
}
