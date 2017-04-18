package org.igov.model.relation;

import org.igov.model.core.GenericEntityDao;
import org.springframework.stereotype.Repository;

/**
 *
 * @author Kovilin
 */
@Repository
public class RelationClassDaoImpl extends GenericEntityDao<Long, RelationClass> implements RelationClassDao {
    
    public RelationClassDaoImpl() {
        super(RelationClass.class);
    }
}
