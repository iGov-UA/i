package org.igov.model.relation;

import java.util.List;
import org.hibernate.Criteria;
import org.hibernate.criterion.Restrictions;
import org.igov.model.core.GenericEntityDao;
import org.springframework.stereotype.Repository;

/**
 *
 * @author Kovilin
 */
@Repository
public class Relation_ObjectGroupDaoImpl extends GenericEntityDao<Long, Relation_ObjectGroup> implements Relation_ObjectGroupDao {
    
    public Relation_ObjectGroupDaoImpl() {
        super(Relation_ObjectGroup.class);
    }
    
    @Override
    public List<Relation_ObjectGroup> getRelation_ObjectGroups(Long nID_Relation, Long nID_Parent) {
        Criteria criteria = createCriteria();
        if (nID_Relation != null) {
            criteria.add(Restrictions.eq("nID_Relation", nID_Relation));
        }
        if (nID_Parent != null) {
            criteria.add(Restrictions.eq("nID_ObjectGroup_Parent", nID_Parent));
        }
        
        return criteria.list();
    }
}
