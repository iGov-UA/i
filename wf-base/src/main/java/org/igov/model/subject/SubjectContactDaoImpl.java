package org.igov.model.subject;

import com.google.common.base.Optional;
import org.springframework.stereotype.Repository;
import org.igov.model.core.GenericEntityDao;

import java.util.List;
import org.hibernate.Criteria;
import org.hibernate.criterion.Restrictions;

/**
 * User: goodg_000
 * Date: 27.12.2015
 * Time: 18:20
 */
@Repository
public class SubjectContactDaoImpl extends GenericEntityDao<Long, SubjectContact> implements SubjectContactDao {

    public SubjectContactDaoImpl() {
        super(SubjectContact.class);
    }
    
    @Override
    public List<SubjectContact> findContactsByCriteria(Subject subject, String sMail) {
        Criteria criteria = createCriteria();
        
        criteria.add(Restrictions.eq("sValue", sMail));
        criteria.add(Restrictions.eq("subject", subject));
        
        return criteria.list();
    }
    
    @Override
    public List<SubjectContact> findContacts(Subject subject) {
        return findAllBy("subject", subject);
    }

    @Override
    public List<SubjectContact> findoMail(SubjectContact oMail) {
        return findAllBy("oMail", oMail);
    }
}
