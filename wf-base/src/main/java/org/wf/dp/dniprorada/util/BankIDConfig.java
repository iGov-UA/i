package org.wf.dp.dniprorada.util;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

/**
 * @author askosyr
 */
@Component("bankIDConfig")
@Configuration
public class BankIDConfig {

    @Value("${bankId_clientId}")
    private String clientId;

    @Value("${bankId_clientSecret}")
    private String clientSecret;

    public String sClientId() {
        return clientId != null ? clientId : "testIgov";
    }

    public String sClientSecret() {
        return clientSecret != null ? clientSecret : "testIgovSecret";
    }

}
