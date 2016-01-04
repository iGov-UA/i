package org.igov.model.escalation;

import org.igov.model.core.EntityDao;

public interface EscalationRuleFunctionDao extends EntityDao<EscalationRuleFunction> {

    EscalationRuleFunction saveOrUpdate(Long nID, String sName, String sBeanHandler);
}
