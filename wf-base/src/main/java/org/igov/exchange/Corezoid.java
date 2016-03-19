/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.igov.exchange;

import com.pb.crm.conveyorapiutils.entity.ConveyorMessage;
import com.pb.crm.conveyorapiutils.entity.ConveyorRequest;
import com.pb.crm.conveyorapiutils.entity.RequestOperation;
import com.pb.crm.conveyorapiutils.utils.HttpManager;
import java.util.Arrays;
import java.util.Map;
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
    
    public void sendToCorezoid(String nID_Corezoid, Map<String, Object> data) throws Exception {
            LOG.info("hostAddress: " + generalConfig.getsCorezoidServerAddress() + " conveyerID: " + nID_Corezoid + " data: " + data);
            ConveyorMessage mes = ConveyorMessage.request(generalConfig.getsCorezoidSecretKey(), Arrays.asList(RequestOperation.create(nID_Corezoid, null, data)));
            ConveyorRequest request = ConveyorRequest.getRequest(generalConfig.getsCorezoidServerAddress(), generalConfig.getsCorezoidUser(), mes);
            String answer = new HttpManager().send(request);
            LOG.info("answer: " + answer);
            Map<String, String> map = ConveyorMessage.parseAnswer(answer);
            LOG.info(map.toString());
    }
    
}
