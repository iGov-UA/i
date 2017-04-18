package org.igov.model.relation;

import org.igov.model.core.GenericEntityDao;
import org.springframework.stereotype.Repository;

/**
 *
 * @author Kovilin
 */
@Repository
public class ObjectItemDaoImpl extends GenericEntityDao<Long, ObjectItem> implements ObjectItemDao {
    
    public ObjectItemDaoImpl() {
        super(ObjectItem.class);
    }
    
}
