package org.igov.service.business.escalation;

import com.mongodb.util.JSON;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;
import org.igov.service.business.escalation.handler.EscalationHandler;
import org.igov.util.ToolJS;
import com.mongodb.BasicDBList;

import javax.script.ScriptException;
import java.util.HashMap;
import java.util.Map;
import org.igov.io.GeneralConfig;
import org.springframework.beans.factory.annotation.Autowired;

@Component
public class EscalationHelper implements ApplicationContextAware {

    private static final Logger LOG = LoggerFactory.getLogger(EscalationHelper.class);

    @Autowired
    GeneralConfig oGeneralConfig;

    private ApplicationContext applicationContext;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    public void checkTaskOnEscalation(Map<String, Object> mTaskParam,
            String sCondition, String soData,
            String sPatternFile, String sBeanHandler) throws Exception {

        //1 -- result of condition
        Map<String, Object> mDataParam = parseJsonData(soData);//from json
        mTaskParam = mTaskParam != null ? mTaskParam : new HashMap<String, Object>();

        try {
            Boolean bConditionAccept = new ToolJS().getResultOfCondition(mDataParam, mTaskParam, sCondition);
            mTaskParam.putAll(mDataParam); //concat

            //2 - check beanHandler
            try {
                //LOG.info("(bConditionAccept={})", bConditionAccept);
                if (bConditionAccept) {
                    EscalationHandler oEscalationHandler = getHandlerClass(sBeanHandler);
                    if (oEscalationHandler != null) {
                        //LOG.info("(basicDBList={})", mTaskParam.get("asRecipientMail"));
                        String[] asRecipientMail = null;
                        BasicDBList basicDBList = (BasicDBList) mTaskParam.get("asRecipientMail");
                        if (basicDBList != null && !basicDBList.isEmpty()) {
                            asRecipientMail = new String[basicDBList.size()];
                            for (int i = 0; i < basicDBList.size(); i++) {
                                asRecipientMail[i] = (String)basicDBList.get(i);
                            }
                        } 
                        oEscalationHandler.execute(mTaskParam, asRecipientMail, sPatternFile);
                    }
                } else {
                    String sHead = String.format((oGeneralConfig.bTest() ? "(TEST)" : "") + "Заявка № %s:%s!",
                            mTaskParam.get("sID_BP"),
                            mTaskParam.get("nID_task_activiti") + "");
                    LOG.info("Escalation not need! (sBeanHandler={},sHead={},sCondition={})", sBeanHandler, sHead, sCondition);
                }
            } catch (Exception e) {
                LOG.error("Can't execute hendler: {} (mTaskParam={})", e.getMessage(), mTaskParam);
                throw e;
            }
        } catch (ClassNotFoundException e) {
            //LOG.error("Error: {}, wrong parameters!", e.getMessage());
            LOG.error("Can't calculate condition, because wrong parameters: {}", e.getMessage());
            LOG.error("!!!!!!Error: ", e);
            throw e;
        } catch (ScriptException e) {
            /*LOG.error("Error: {}, wrong sCondition or parameters! (condition={}, params_json={})",
             e.getMessage(), sCondition, soData);*/
            LOG.error("Can't calculate condition, because wrong sCondition or parameters: {} (sCondition={}, soData={}, mTaskParam={})",
                    e.getMessage(), sCondition, soData, mTaskParam);
            LOG.error("!!!!!!Error: ", e);
            throw e;
        } catch (NoSuchMethodException e) {
            //LOG.error("Error: {}, error in script", e.getMessage());
            LOG.error("Can't calculate condition, because error in script: {} (sCondition={}, soData={}, mTaskParam={})",
                    e.getMessage(), sCondition, soData, mTaskParam);
            LOG.error("!!!!!!Error: ", e);
            throw e;
        } catch (Exception e) {
            //LOG.error("Error: {}, wrong parameters!", e.getMessage());
            LOG.error("Can't calculate condition, because unknown error: {} (sCondition={}, soData={}, mTaskParam={})",
                    e.getMessage(), sCondition, soData, mTaskParam);
            LOG.error("!!!!!!Error: ", e);
            throw e;
        }
    }

    private EscalationHandler getHandlerClass(String sBeanHandler) {
        EscalationHandler oEscalationHandler = (EscalationHandler) applicationContext
                .getBean(sBeanHandler);//"EscalationHandler_SendMailAlert");
        //LOG.info("Retrieved EscalationHandler component : {}", oEscalationHandler);
        return oEscalationHandler;
    }

    private Map<String, Object> parseJsonData(String soData) {
        Map<String, Object> json = (Map<String, Object>) JSON.parse(soData);
        return json;
    }

}
