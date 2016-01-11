package org.igov.service.business.escalation.handler;

import org.slf4j.Logger;import org.slf4j.LoggerFactory;
import org.igov.service.business.action.task.bp.handler.BpServiceHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * @author OlgaPrylypko
 * @since 13.12.2015
 */
@Component("EscalationHandler_StartEscalationProcess")
public class EscalationHandler_StartEscalationProcess implements EscalationHandler {

    private static final Logger LOG = LoggerFactory.getLogger(EscalationHandler_StartEscalationProcess.class);

    @Autowired
    private BpServiceHandler bpHandler;

    @Override
    public void execute(Map<String, Object> mParam, String[] asRecipientMail, String sPatternFile) {
        //start escalation process (issue 981)
        LOG.info("start escalation process");
        bpHandler.checkBpAndStartEscalationProcess(mParam);
    }
}
