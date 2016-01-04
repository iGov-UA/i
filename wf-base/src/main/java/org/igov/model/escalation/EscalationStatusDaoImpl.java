package org.igov.model.escalation;

import org.igov.model.core.GenericEntityDao;
import org.springframework.stereotype.Repository;

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
