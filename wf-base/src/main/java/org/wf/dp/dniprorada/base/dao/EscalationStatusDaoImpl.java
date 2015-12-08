package org.wf.dp.dniprorada.base.dao;

import org.springframework.stereotype.Repository;
import org.wf.dp.dniprorada.base.model.EscalationStatus;

/**
 * @author NickVeremeichyk
 * @since 2015-12-05.
 */
@Repository
public class EscalationStatusDaoImpl extends GenericEntityDao<EscalationStatus> implements EscalationStatusDao {
    protected EscalationStatusDaoImpl() {
        super(EscalationStatus.class);
    }
}
