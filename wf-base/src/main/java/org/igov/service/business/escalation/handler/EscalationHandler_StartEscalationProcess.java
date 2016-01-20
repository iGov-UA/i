package org.igov.service.business.escalation.handler;

import org.slf4j.Logger;import org.slf4j.LoggerFactory;
import org.igov.service.business.action.task.bp.handler.BpServiceHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;
import org.igov.io.GeneralConfig;

/**
 * @author OlgaPrylypko
 * @since 13.12.2015
 */
@Component("EscalationHandler_StartEscalationProcess")
public class EscalationHandler_StartEscalationProcess implements EscalationHandler {

    private static final Logger LOG = LoggerFactory.getLogger(EscalationHandler_StartEscalationProcess.class);

    @Autowired
    GeneralConfig oGeneralConfig;
    
    @Autowired
    private BpServiceHandler bpHandler;

    @Override
    public void execute(Map<String, Object> mParam, String[] asRecipientMail, String sPatternFile) throws Exception {
        String sHead = null;
        try {
            sHead = String
                    .format((oGeneralConfig.bTest() ? "(TEST)" : "") + "Зависла заявка № %s:%s ! Прийміть міри!",
                            mParam.get("sID_BP"),
                            mParam.get("nID_task_activiti").toString());

            LOG.info("Creating... (sHead={})", sHead);
            bpHandler.checkBpAndStartEscalationProcess(mParam);
        } catch (Exception e) {
            LOG.error("Can't make: {} (sHead={})", e.getMessage(), sHead);
            throw e;
        }        
    }
}
