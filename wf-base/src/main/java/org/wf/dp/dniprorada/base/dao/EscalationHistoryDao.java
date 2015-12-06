package org.wf.dp.dniprorada.base.dao;

import org.joda.time.DateTime;
import org.springframework.stereotype.Repository;
import org.wf.dp.dniprorada.base.model.EscalationHistory;

import java.util.List;

/**
 * @author NickVeremeichyk
 * @since 2015-12-05.
 */
public interface EscalationHistoryDao extends EntityDao<EscalationHistory> {
    List getAllByCriteria(Long nIdProcess,
                          Long nIdProcessRoot,
                          Long nIdUserTask,
                          DateTime sDateStart,
                          DateTime sDateEnd,
                          Integer nRowsMax);
}
