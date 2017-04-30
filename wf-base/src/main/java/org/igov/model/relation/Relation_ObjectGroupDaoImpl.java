package org.igov.model.relation;

import java.util.List;
import org.hibernate.Criteria;
import org.hibernate.criterion.MatchMode;
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
        /*if(sFindChild != null){
            criteria.add(Restrictions.like("oObjectGroup.sName", sFindChild, MatchMode.ANYWHERE));
        }*/
        if (nID_Relation != null) {
            //criteria.add(Restrictions.eq("nID_Relation", nID_Relation));
            criteria.add(Restrictions.eq("oRelation.id", nID_Relation));
        }
        if (nID_Parent != null) {
            //criteria.add(Restrictions.eq("nID_ObjectGroup_Parent", nID_Parent));
            criteria.add(Restrictions.eq("oObjectGroup_Parent.id", nID_Parent));
        }
        
        return criteria.list();
    }
}
