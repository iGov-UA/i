/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.igov.exchange;

import com.corezoid.sdk.entity.CorezoidMessage;
import com.corezoid.sdk.entity.RequestOperation;
import com.corezoid.sdk.utils.HttpManager;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Random;
import net.sf.json.JSONObject;
import org.igov.io.GeneralConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 *
 * @author olya
 */
@Component("Corezoid")
public class Corezoid {
    
    private final static Logger LOG = LoggerFactory.getLogger(Corezoid.class);
    
    @Autowired
    GeneralConfig generalConfig;
    
    public String sendToCorezoid(String nID_Corezoid, Map<String, Object> data) throws Exception {
            LOG.info("conveyerID: " + nID_Corezoid + " user: " + generalConfig.getUser_Coreziod_Exchange() 
                    + " secretKey: " + generalConfig.getSecretKey_Coreziod_Exchange() + " data: " + data);
            JSONObject requestData = new JSONObject();
            requestData.accumulateAll(data);
            LOG.info("requestData: " + requestData);
            String ref = "iGov" + System.currentTimeMillis() + new Random(System.currentTimeMillis()).nextInt(1000);
            RequestOperation operation = RequestOperation.create(nID_Corezoid, ref, requestData);
            List<RequestOperation> ops = Arrays.asList(operation);
            CorezoidMessage message = CorezoidMessage.request(generalConfig.getSecretKey_Coreziod_Exchange(), generalConfig.getUser_Coreziod_Exchange(), ops);
            HttpManager http = new HttpManager();
            String resut = http.send(message);
            LOG.info("resut: " + resut);
            return resut;
    }
    
    public String sendToCorezoid(String nID_Corezoid, String user, String secretKey, Map<String, Object> data) throws Exception {
            LOG.info("conveyerID: " + nID_Corezoid + " user: " + generalConfig.getUser_Coreziod_Exchange() 
                    + " secretKey: " + generalConfig.getSecretKey_Coreziod_Exchange() + " data: " + data);
            JSONObject requestData = new JSONObject();
            requestData.accumulateAll(data);
            LOG.info("requestData: " + requestData);
            String ref = "iGov" + System.currentTimeMillis() + new Random(System.currentTimeMillis()).nextInt(1000);
            RequestOperation operation = RequestOperation.create(nID_Corezoid, ref, requestData);
            List<RequestOperation> ops = Arrays.asList(operation);
            CorezoidMessage message = CorezoidMessage.request(secretKey, user, ops);
            HttpManager http = new HttpManager();
            String resut = http.send(message);
            LOG.info("resut: " + resut);
            return resut;
    }
    
}
