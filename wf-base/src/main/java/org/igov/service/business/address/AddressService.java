package org.igov.service.business.address;

import java.nio.charset.StandardCharsets;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;
import org.igov.io.web.RestRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;

@Service
public class AddressService {
    
    private final static Logger LOG = LoggerFactory.getLogger(AddressService.class);
    
    private static final String EHEALTH_URL = "https://api.ehealth.world/api/uaddresses";
    private static final Integer MAX_PAGE_SIZE = 500;
    private static final String API_REGION = "regions";

    public String getListRegions() 
            throws InterruptedException, ExecutionException, TimeoutException {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Content-Type", "application/json; charset=utf-8");
        String resp = new RestRequest().get(getCommonInfoURL(API_REGION), null, StandardCharsets.UTF_8, String.class, headers);
        
        return resp;
    }

    private String getCommonInfoURL(String apiUrlEnd){
        String sURL = String.format("%s/%s", 
                EHEALTH_URL,
                apiUrlEnd);
        LOG.info("Formed URL for ehealth: " + sURL);
        return sURL;
    }
    
    
}
