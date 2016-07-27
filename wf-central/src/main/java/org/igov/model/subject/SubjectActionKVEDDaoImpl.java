package org.igov.model.subject;

import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Restrictions;
import org.igov.model.core.GenericEntityDao;
import org.springframework.stereotype.Repository;

@Repository
public class SubjectActionKVEDDaoImpl extends GenericEntityDao<Long, SubjectActionKVED>
	implements SubjectActionKVEDDao {

    public SubjectActionKVEDDaoImpl() {
	super(SubjectActionKVED.class);
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<SubjectActionKVED> getSubjectActionKVED(String sID, String sNote) {
	Criteria criteria = getSession().createCriteria(SubjectActionKVED.class);
	if (sID != null) {
	    criteria.add(Restrictions.ilike("sID", sID, MatchMode.ANYWHERE));
	}
	if (sNote != null) {
	    criteria.add(Restrictions.ilike("sNote", sNote, MatchMode.ANYWHERE));
	}
	
	if ( sID == null && sNote == null ) {
	    return null;
	} else {
	    return criteria.list();
	}

    }

    @SuppressWarnings("unchecked")
    @Override
    public List<SubjectActionKVED> getSubjectActionKVED(String sFind) {
	Criteria criteria = getSession().createCriteria(SubjectActionKVED.class);
	if (sFind != null) {
	    criteria.add(Restrictions.or(Restrictions.ilike("sID", sFind, MatchMode.ANYWHERE),
		    Restrictions.ilike("sNote", sFind, MatchMode.ANYWHERE)));
	}

	if ( sFind == null ) {
	    return null;
	} else {
	    return criteria.list();
	}

    }

}
