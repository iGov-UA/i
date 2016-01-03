package org.igov.model.escalation;

import org.igov.model.core.EntityDao;

public interface EscalationRuleDao extends EntityDao<EscalationRule> {
    EscalationRule saveOrUpdate(Long nID, String sID_BP, String sID_userTask,
            String sCondition, String soData, String sPatternFile,
            EscalationRuleFunction ruleFunction);
}
