package org.igov.model.subject.message;

import java.util.HashMap;
import java.util.LinkedList;
import org.springframework.stereotype.Repository;
import org.igov.model.core.GenericEntityDao;

import java.util.List;
import java.util.Map;
import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.SQLQuery;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.igov.model.subject.SubjectContact;

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
        Criteria oCriteria = getSession().createCriteria(SubjectMessage.class);
//        oCriteria.add(Restrictions.eq("nID_HistoryEvent_Service", nID_HistoryEvent_Service));
        //oCriteria.addOrder(Order.desc("id"));
        oCriteria.setMaxResults(100);
        List<SubjectMessage> aSubjectMessage = (List<SubjectMessage>) oCriteria.list();
        if (aSubjectMessage == null) {
            aSubjectMessage = new LinkedList();
        }
        return aSubjectMessage;
//        return findAll();
    }

    @Override
    public List tranferDataFromMailToSubjectMail() {
        
       int resultParamQuery = -1;
       int resultUpdateNullMail = -1;
       String sMails = "SELECT nID, sMail FROM SubjectMessage";
       SQLQuery oQueryMails = getSession().createSQLQuery(sMails).addScalar("nID").addScalar("sMail");
       List listMails = oQueryMails.list();
       String sContacts = "SELECT * FROM SubjectContact";
       SQLQuery oQueryContacts = getSession().createSQLQuery(sContacts).addEntity(SubjectContact.class);
       List listContacts = oQueryContacts.list();
       
       Map<Long, Long> id_contact = new HashMap<Long, Long>();
       
       for(int i = 0; i < listMails.size(); i++)
       {
          Object[] obj = (Object[])listMails.get(i);
          for(int j = 0; j < listContacts.size(); j++)
          {
              SubjectContact subjectContact = (SubjectContact)listContacts.get(j);
              
              if(obj[1] != null && subjectContact.getsValue().equals((String)obj[1]))
              {
                 java.math.BigInteger big = (java.math.BigInteger)obj[0];
                 id_contact.put(big.longValue(), subjectContact.getId());
              }
             
           }
       }
       
       String sUpdate = "UPDATE SubjectMessage SET sMail = null, nID_SubjectContact_Mail = :idmail WHERE nID = :id";
       Query oQueryUpdate = getSession().createSQLQuery(sUpdate);
       for(Long key : id_contact.keySet())
       {
          oQueryUpdate.setLong("idmail", id_contact.get(key));
          oQueryUpdate.setLong("id", key);
          resultParamQuery = oQueryUpdate.executeUpdate();
       }
       
       String sMailNull = "UPDATE SubjectMessage SET sMail = null WHERE sMail IS NOT NULL";
       SQLQuery oQueryUpdateNullMail = getSession().createSQLQuery(sMailNull);
       resultUpdateNullMail = oQueryUpdateNullMail.executeUpdate();
       
       String sSelectSubjectMessages = "SELECT * FROM SubjectMessage LIMIT 100";
       SQLQuery oQuerySubjectMessages = getSession().createSQLQuery(sSelectSubjectMessages).addEntity(SubjectMessage.class);
       List listSubjectMessages = oQuerySubjectMessages.list();
       
       return listSubjectMessages;
    }
   
    @Override
    public List<SubjectMessage> getMessages(Long nID_HistoryEvent_Service) {
        Criteria oCriteria = getSession().createCriteria(SubjectMessage.class);
        oCriteria.add(Restrictions.eq("nID_HistoryEvent_Service", nID_HistoryEvent_Service));
        oCriteria.addOrder(Order.desc("date"));
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
