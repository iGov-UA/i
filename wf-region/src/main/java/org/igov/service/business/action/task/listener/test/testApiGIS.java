package org.igov.service.business.action.task.listener.test;

import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.pb.util.Base64Encoder;
import org.activiti.engine.ActivitiIllegalArgumentException;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.delegate.DelegateTask;
import org.activiti.engine.delegate.Expression;
import org.activiti.engine.delegate.TaskListener;
import org.activiti.engine.identity.Group;
import org.igov.io.GeneralConfig;
import org.igov.io.web.HttpRequester;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.*;


import static org.igov.service.business.action.task.core.AbstractModelTask.getStringFromFieldExpression;


@Component("getGisInfo")
public class testApiGIS implements TaskListener {

    private static final transient Logger LOG = LoggerFactory.getLogger(testApiGIS.class);

    @Autowired
    private RuntimeService runtimeService;

    @Autowired
    private GeneralConfig generalConfig;

    @Autowired
    private HttpRequester httpRequester;

    public Expression sStreet;
    public Expression sNumber;

    @Override
    public void notify(DelegateTask delegateTask) {

        DelegateExecution execution = delegateTask.getExecution();
        String sStreet = getStringFromFieldExpression(this.sStreet, execution);
        String sNumber = getStringFromFieldExpression(this.sNumber, execution);

        Properties config = new Properties();
        try {
            config.load(new FileInputStream("config.properties"));
        } catch (FileNotFoundException e) {
            System.out.println("File config.properties has not been found. Program will use default options");
        } catch (IOException e) {
            e.printStackTrace();
        }
        final String TOKEN = ((config == null) || (config.getProperty("TOKEN") == null)) ?
                "277be326eef77fdf1b58c474a3af62 eccdbb1fc788a38ba0779c78c54cea 6d0a4a4c372af8500c057c56a813b3 8960365ab956d1cd43720a5c3ee065 6863364f"
                : config.getProperty("TOKEN");

     //   runtimeService.setVariable(execution.getProcessInstanceId(), "name_of_Activiti_var", "value_of_Activiti_var");

        //**************************************************

        HttpRequests http = new HttpRequests();
        ObjectMapper mapper = new ObjectMapper();

///
        System.out.println("Testing 1 - Send Http GET token request");

        String getTokenRequest = "http://212.26.131.154:86/api/token/refresh?refresh_token=277be326eef77fdf1b58c474a3af62eccdbb1fc788a38ba0779c78c54cea6d0a4a4c372af8500c057c56a813b38960365ab956d1cd43720a5c3ee0656863364f";
        String tokenResult = null;
        try {
            tokenResult = http.sendGet(getTokenRequest, Collections.<String, String>emptyMap());
        } catch (Exception e) {
            e.printStackTrace();
        }

        ObjectNode node = null;
        try {
            node = mapper.readValue(tokenResult, ObjectNode.class);
        } catch (IOException e) {
            e.printStackTrace();
        }
        String token = node.get("token").asText();
        String refreshToken = node.get("refresh_token").asText();

        System.out.println("Got Token: " + token);
        System.out.println("Refresh Token: " + refreshToken);

        // Reuse Auth results
        Map<String, String> authParams = new HashMap<>();
        String tokenBearer = "Bearer " + token;
        authParams.put("Authorization", tokenBearer);

///
        System.out.println("\nTesting 2 - Send Http GET street request");
        String getStreetRequest = "http://212.26.131.154:86/api/search.json?s=" + sStreet + "&n=" + sNumber;

        String streetResult = "";

        try {
            streetResult = http.sendGet(getStreetRequest, authParams);
            execution.setVariable("streetResult", streetResult);
        } catch (Exception e) {
            e.printStackTrace();
        }

        JavaType type = mapper.getTypeFactory().constructCollectionType(List.class, StreetDto.class);
        List<StreetDto> streets = null;
        try {
            streets = mapper.readValue(streetResult, type);
        } catch (IOException e) {
            e.printStackTrace();
        }
///
        System.out.println("\nTesting 3 - Send Http POST message");

        String postMessageRequest = "http://212.26.131.154:86/api/msg.json";

        String postParams = "msg=" + streetResult; //!!!!!!!!!!!!

        System.out.println(postParams);

        //POST with full info
        String postResult = "";
        try {
            postResult = http.sendPost(postMessageRequest, postParams, authParams);
            execution.setVariable("postResult", postResult);
        } catch (Exception e) {
            e.printStackTrace();
        }

        System.out.println("\nPOST result is: " + postResult);

    }

}
