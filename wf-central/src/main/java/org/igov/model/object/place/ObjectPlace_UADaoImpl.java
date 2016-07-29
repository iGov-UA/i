package org.igov.model.object.place;

import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Restrictions;
import org.igov.model.core.GenericEntityDao;
import org.springframework.stereotype.Repository;

@Repository
public class ObjectPlace_UADaoImpl extends GenericEntityDao<Long, ObjectPlace_UA> implements ObjectPlace_UADao {

    public ObjectPlace_UADaoImpl() {
	super(ObjectPlace_UA.class);
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<ObjectPlace_UA> getObjectPlace_UA(String sID, String sName_UA) {
	Criteria criteria = getSession().createCriteria(ObjectPlace_UA.class);
	if (sID != null) {
	    criteria.add(Restrictions.ilike("sID", sID, MatchMode.ANYWHERE));
	}
	if (sName_UA != null) {
	    criteria.add(Restrictions.ilike("sName_UA", sName_UA, MatchMode.ANYWHERE));
	}
	
	if ( sID == null && sName_UA == null ) {
	    return null;
	} else {
	    return criteria.list();
	}
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<ObjectPlace_UA> getObjectPlace_UA(String sFind) {
	Criteria criteria = getSession().createCriteria(ObjectPlace_UA.class);
	if (sFind != null) {
	    criteria.add(Restrictions.or(Restrictions.ilike("sID", sFind, MatchMode.ANYWHERE),
		    Restrictions.ilike("sName_UA", sFind, MatchMode.ANYWHERE)));
	}

	if ( sFind == null ) {
	    return null;
	} else {
	    return criteria.list();
	}
    }

}
