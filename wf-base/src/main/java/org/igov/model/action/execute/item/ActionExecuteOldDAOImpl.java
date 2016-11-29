package org.igov.model.action.execute.item;

import org.hibernate.Session;
import org.igov.model.core.GenericEntityDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

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
