package org.igov.model.action.task.core.entity;

import org.igov.model.core.EntityDao;

/**
 * @author askosyr
 * @since 2016-07-31.
 */
public interface ActionProcessCountDao extends EntityDao<Long, ActionProcessCount> {

	ActionProcessCount getByCriteria(String sID_BP, Integer nID_Service, Integer nYear);

}
