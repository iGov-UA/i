package org.igov.model.action.execute.item;

import org.igov.model.core.GenericEntityDao;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class ActionExecuteOldDAOImpl extends GenericEntityDao<Long, ActionExecuteOld> implements ActionExecuteOldDAO {

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

}
