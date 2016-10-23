package org.igov.model.subject.message;

import com.google.common.base.Optional;
import java.util.Collection;
import org.igov.model.core.GenericEntityDao;
import org.igov.service.exception.EntityNotFoundException;
import org.springframework.stereotype.Repository;

import java.util.List;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.Criteria;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import static org.hibernate.criterion.Restrictions.eq;
import static org.hibernate.criterion.Restrictions.in;
import org.springframework.util.Assert;

@Repository
public class SubjectMessageFeedbackDaoImpl extends GenericEntityDao<Long, SubjectMessageFeedback> implements SubjectMessageFeedbackDao {

    protected SubjectMessageFeedbackDaoImpl() {
        super(SubjectMessageFeedback.class);
    }

    @Override
    public SubjectMessageFeedback save(SubjectMessageFeedback subjectMessageFeedback) {
        getSession().save(subjectMessageFeedback);
        return subjectMessageFeedback;
    }

    @Override
    public SubjectMessageFeedback getSubjectMessageFeedbackById(Long nId) {
        Optional<SubjectMessageFeedback> feedback = findById(nId);
        if (feedback.isPresent()) {
          return feedback.get();
        }
        return null;
    }

    @Override
    public List<SubjectMessageFeedback> getAllSubjectMessageFeedbackBynID_Service(Long nID_service) {
        return findAllBy("nID_Service", nID_service);
    }

    @Override
    public List<SubjectMessageFeedback> getAllSubjectMessageFeedback_Filtered(Long nID_service, Long nID__LessThen_Filter, Integer nRowsMax) {//nRowsSkip
        Assert.notNull(nID_service, "Specify value");//sFieldValue
        if(nRowsMax==null||nRowsMax>1000){
            nRowsMax=null;
        }

        Criteria oCriteria = createCriteria();
        oCriteria.setMaxResults(nRowsMax);//100
        
        Criterion oCriterion = Restrictions.eq("nID_Service", nID_service);
        oCriteria.add(oCriterion);

        if(nID__LessThen_Filter!=null && nID__LessThen_Filter!=0){
            oCriterion = Restrictions.lt("nID", nID__LessThen_Filter);
            oCriteria.add(oCriterion);
        }
        
        oCriterion = Restrictions.isNotNull("sBody");//, sFieldValue
        oCriteria.add(oCriterion);

        oCriterion = Restrictions.isNotNull("sAuthorFIO");//, sFieldValue
        oCriteria.add(oCriterion);
        
        oCriteria.addOrder(Order.desc("nID"));
        
        //List<SubjectMessageFeedback> a = oCriteria.list();
        return oCriteria.list();
    }

    @Override
    public SubjectMessageFeedback update(SubjectMessageFeedback subjectMessageFeedback) {
        getSession().update(subjectMessageFeedback);
        return subjectMessageFeedback;
    }
}
