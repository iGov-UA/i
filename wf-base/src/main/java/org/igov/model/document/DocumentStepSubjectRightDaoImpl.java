package org.igov.model.document;

import java.util.List;
import org.hibernate.Criteria;
import org.hibernate.criterion.Restrictions;
import org.igov.model.core.GenericEntityDao;
import org.springframework.stereotype.Repository;

@Repository
public class DocumentStepSubjectRightDaoImpl extends GenericEntityDao<Long, DocumentStepSubjectRight> implements DocumentStepSubjectRightDao {

    public DocumentStepSubjectRightDaoImpl() {
        super(DocumentStepSubjectRight.class);
    }

    @Override
    public List<DocumentStepSubjectRight> findUnassignedUnprocessedDocument(String sLogin) {
        Criteria criteria = createCriteria();
        criteria.add(Restrictions.eq("sKey_GroupPostfix", sLogin));
        criteria.add(Restrictions.isNull("sDate"));
        criteria.add(Restrictions.isNotNull("bWrite"));

        return criteria.list();
    }

    @Override
    public List<DocumentStepSubjectRight> findOpenedUnassignedWithoutECPDocument(String sLogin) {
        Criteria criteria = createCriteria();
        criteria.add(Restrictions.eq("sLogin", sLogin));
        criteria.add(Restrictions.isNotNull("sDate"));
        criteria.add(Restrictions.isNull("sDateECP"));
        criteria.add(Restrictions.eq("bNeedECP", "true"));

        return criteria.list();
    }

}
