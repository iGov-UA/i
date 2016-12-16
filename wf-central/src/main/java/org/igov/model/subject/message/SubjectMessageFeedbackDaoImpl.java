package org.igov.model.subject.message;

import com.google.common.base.Optional;
import org.hibernate.Criteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.igov.model.core.GenericEntityDao;
import org.igov.service.controller.ActionEventController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;
import org.springframework.util.Assert;

import java.util.List;

@Repository
public class SubjectMessageFeedbackDaoImpl extends GenericEntityDao<Long, SubjectMessageFeedback> implements SubjectMessageFeedbackDao {
    
    private static final Logger LOG = LoggerFactory.getLogger(ActionEventController.class);
    
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
        if (nRowsMax == null || nRowsMax > 1000 || nRowsMax <= 0){
            nRowsMax = null;
        }

        Criteria oCriteria = createCriteria();

        if (nRowsMax != null) {
            oCriteria.setMaxResults(nRowsMax);//100
        }

        oCriteria.add(Restrictions.eq("nID_Service", nID_service));

        if(nID__LessThen_Filter!=null && nID__LessThen_Filter!=0){
            oCriteria.add(Restrictions.lt("id", nID__LessThen_Filter));
        }
        
//        oCriteria.add(Restrictions.isNotNull("sBody"));
//        oCriteria.createAlias("oSubjectMessage", "sm").add(Restrictions.isNotNull("sm.body"));
        oCriteria.add(Restrictions.isNotNull("sAuthorFIO"));
        oCriteria.addOrder(Order.desc("id"));
        
        //List<SubjectMessageFeedback> a = oCriteria.list();
        return oCriteria.list();
    }

    @Override
    public SubjectMessageFeedback update(SubjectMessageFeedback subjectMessageFeedback) {
        getSession().update(subjectMessageFeedback);
        return subjectMessageFeedback;
    }

    @Override
    public SubjectMessageFeedback findByOrder(String sID_Order) {
        return findBy("sID_Order", sID_Order).orNull();
    }

}
