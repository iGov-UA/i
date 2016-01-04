package org.igov.model.escalation.handler;

import java.util.Map;

public interface EscalationHandler {
    public void execute(Map<String, Object> mParam, String[] asRecipientMail, String sPatternFile);
}
