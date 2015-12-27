package org.wf.dp.dniprorada.dao;

import java.util.LinkedList;
import org.springframework.stereotype.Repository;
import org.wf.dp.dniprorada.base.dao.GenericEntityDao;
import org.wf.dp.dniprorada.model.SubjectMessage;

import java.util.List;
import org.hibernate.Criteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;

@Repository
public class SubjectMessagesDaoImpl extends GenericEntityDao<SubjectMessage> implements SubjectMessagesDao {

    protected SubjectMessagesDaoImpl() {
        super(SubjectMessage.class);
    }

    @Override
    public void setMessage(SubjectMessage message) {
        saveOrUpdate(message);
    }

    @Override
    public List<SubjectMessage> getMessages() {
        return findAll();
    }

    @Override
    public List<SubjectMessage> getMessages(Long nID_HistoryEvent_Service) {
        Criteria oCriteria = getSession().createCriteria(SubjectMessage.class);
        oCriteria.add(Restrictions.eq("nID_HistoryEvent_Service", nID_HistoryEvent_Service));
        //oCriteria.addOrder(Order.desc("id"));
        oCriteria.setMaxResults(100);
        List<SubjectMessage> aSubjectMessage = (List<SubjectMessage>) oCriteria.list();
        if (aSubjectMessage == null) {
            aSubjectMessage = new LinkedList();
        }
        return aSubjectMessage;
    }

    @Override
    public SubjectMessage getMessage(Long nID) {
        return findById(nID).orNull();
    }
}
