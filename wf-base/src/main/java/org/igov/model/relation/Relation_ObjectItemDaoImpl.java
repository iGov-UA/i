package org.igov.model.relation;

import org.igov.model.core.GenericEntityDao;
import org.springframework.stereotype.Repository;

/**
 *
 * @author Kovilin
 */
@Repository
public class Relation_ObjectItemDaoImpl extends GenericEntityDao<Long, Relation_ObjectItem> implements Relation_ObjectItemDao {
    
    public Relation_ObjectItemDaoImpl() {
        super(Relation_ObjectItem.class);
    }
    
}
