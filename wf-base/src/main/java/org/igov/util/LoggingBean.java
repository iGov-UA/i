package org.igov.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Required;

/**
 * Used to log message on bean creation
 * User: goodg_000
 * Date: 12.05.2016
 * Time: 22:22
 */
public class LoggingBean implements InitializingBean {

    private final static Logger LOG = LoggerFactory.getLogger(LoggingBean.class);

    private String message;

    @Required
    public void setMessage(String message) {
        this.message = message;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        LOG.warn(message);
    }
}
