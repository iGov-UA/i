package org.igov.model.subject;

import org.hibernate.Criteria;
import org.hibernate.criterion.Restrictions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;
import org.igov.model.core.GenericEntityDao;

@Repository
public class SubjectRightBPDaoImpl extends GenericEntityDao<Long, SubjectRightBP> implements SubjectRightBPDao  {

    private static final Logger LOG = LoggerFactory.getLogger(SubjectRightBPDaoImpl.class);

    protected SubjectRightBPDaoImpl() {
        super(SubjectRightBP.class);
    }
        
    @Override
    public SubjectRightBP getSubjectRightBP(String sID_BP, String sID_Group_Referent){
        
        Criteria criteria = getSession().createCriteria(SubjectRightBP.class);
        if (sID_BP != null) {
            criteria.add(Restrictions.eq("sID_BP", sID_BP));
        }
        if (sID_Group_Referent != null) {
            criteria.add(Restrictions.eq("asID_Group_Export", sID_Group_Referent));
        }
        
        return (SubjectRightBP)criteria.uniqueResult();
    }    
	
}
