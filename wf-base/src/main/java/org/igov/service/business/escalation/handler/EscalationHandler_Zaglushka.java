package org.igov.service.business.escalation.handler;

import org.slf4j.Logger;import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;
import org.igov.io.GeneralConfig;

import java.util.Map;

@Component("EscalationHandler_Zaglushka")
public class EscalationHandler_Zaglushka implements EscalationHandler {

    private static final Logger LOG = LoggerFactory.getLogger(EscalationHandler_SendMailAlert.class);
    
    @Autowired
    GeneralConfig oGeneralConfig;
    @Autowired
    private ApplicationContext context;

    @Override
    public void execute(Map<String, Object> mParam, String[] asRecipientMail, String sPatternFile) throws Exception {
        LOG.info("Skiped... (mParam={})", mParam);
    }

}
