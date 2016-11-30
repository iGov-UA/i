package org.igov.model.action.execute.item;

import org.activiti.engine.impl.util.json.JSONArray;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;
import org.igov.model.core.GenericEntityDao;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public class ActionExecuteOldDAOImpl extends GenericEntityDao<Long, ActionExecuteOld> implements ActionExecuteOldDAO {

    @Autowired
    ActionExecuteDAO actionExecuteDAO;

    protected ActionExecuteOldDAOImpl() {
        super(ActionExecuteOld.class);
    }

    @Override
    public ActionExecuteOld getActionExecute(Long id) {
        return findById(id).orNull();
    }

    @Override
    public List<ActionExecuteOld> getAllActionExecutes() {
        return findAll();
    }


    @Override
    public Session getSessionForService() {
        return this.getSession();
    }
}
