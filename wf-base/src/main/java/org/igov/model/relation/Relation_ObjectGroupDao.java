package org.igov.model.relation;

import java.util.List;
import org.igov.model.core.EntityDao;

/**
 *
 * @author Kovilin
 */
public interface Relation_ObjectGroupDao extends EntityDao<Long, Relation_ObjectGroup>{
    
    public List<Relation_ObjectGroup> getRelation_ObjectGroups(Long nID_Relation, Long nID_Parent);
}
