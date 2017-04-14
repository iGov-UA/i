package org.igov.model.relation;

import org.igov.model.core.GenericEntityDao;
import org.springframework.stereotype.Repository;

/**
 *
 * @author Kovilin
 */
@Repository
public class ObjectGroupDaoImpl extends GenericEntityDao<Long, ObjectGroup> implements ObjectGroupDao {
    
    public ObjectGroupDaoImpl() {
        super(ObjectGroup.class);
    }
    
}
