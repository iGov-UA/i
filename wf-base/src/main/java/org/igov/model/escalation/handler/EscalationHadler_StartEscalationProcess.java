package org.igov.model.escalation.handler;

import org.apache.log4j.Logger;
import org.igov.activiti.bp.BpHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * @author OlgaPrylypko
 * @since 13.12.2015
 */
@Component("EscalationHadler_StartEscalationProcess")
public class EscalationHadler_StartEscalationProcess implements EscalationHandler {

    private static final Logger LOG = Logger.getLogger(EscalationHadler_StartEscalationProcess.class);

    @Autowired
    private BpHandler bpHandler;

    @Override
    public void execute(Map<String, Object> mParam, String[] asRecipientMail, String sPatternFile) {
        //start escalation process (issue 981)
        LOG.info("start escalation process");
        bpHandler.checkBpAndStartEscalationProcess(mParam);
    }
}
