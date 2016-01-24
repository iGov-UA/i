package org.igov.service.business.escalation.handler;

import java.util.Map;

public interface EscalationHandler {
    public void execute(Map<String, Object> mParam, String[] asRecipientMail, String sPatternFile) throws Exception;
}
