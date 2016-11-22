package org.igov.model.action.execute.item;

import org.igov.model.core.GenericEntityDao;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class ActionExecuteDAOImpl extends GenericEntityDao<Long, ActionExecute> implements ActionExecuteDAO {

    protected ActionExecuteDAOImpl() {
        super(ActionExecute.class);
    }

    @Override
    public ActionExecute getActionExecute(Long id) {
        return findById(id).orNull();
    }

    @Override
    public List<ActionExecute> getAllActionExecutes() {
        return findAll();
    }
}
