package org.igov.model.escalation;

import org.joda.time.DateTime;

import java.util.List;
import org.igov.model.core.EntityDao;

/**
 * @author NickVeremeichyk
 * @since 2015-12-05.
 */
public interface EscalationHistoryDao extends EntityDao<EscalationHistory> {

    List<EscalationHistory> getAllByCriteria(Long nIdProcess,
                          Long nIdProcessRoot,
                          Long nIdUserTask,
                          DateTime sDateStart,
                          DateTime sDateEnd,
                          Integer nRowsMax);

    EscalationHistory getByProcessId(Long nIdProcess);

}
