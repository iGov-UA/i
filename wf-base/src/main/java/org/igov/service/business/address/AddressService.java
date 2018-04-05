package org.igov.service.business.address;

import java.nio.charset.StandardCharsets;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;
import org.igov.io.web.RestRequest;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;

@Service
public class AddressService {
    
    private final static Logger LOG = LoggerFactory.getLogger(AddressService.class);
    
    private static final String EHEALTH_URL = "https://api.ehealth.world/api/uaddresses";
    private static final String PAGE_PROPERTY = "page";
    private static final Integer MAX_PAGE_SIZE = 500;
    private static final String API_REGION = "regions";
    
    private final JSONParser parser = new JSONParser();

    public JSONArray getListRegions() 
            throws InterruptedException, ExecutionException, TimeoutException, ParseException {
        JSONArray aJsonRegions = new JSONArray();
        getJSONResponse(API_REGION, aJsonRegions);
        return aJsonRegions;
    }

    private void getJSONResponse(String sURLResource, JSONArray aJsonRegions) throws ParseException, RestClientException {
        int pageNumber = 1;
        int totalPages;
        HttpHeaders headers = new HttpHeaders();
        headers.set("Content-Type", "application/json; charset=utf-8");
        do {
            String resp = new RestRequest().get(getCommonInfoURL(sURLResource, pageNumber), null, StandardCharsets.UTF_8, String.class, headers);
            JSONObject oJSONObject = (JSONObject) parser.parse(resp);
            LOG.debug("oJSONObject is {}", oJSONObject.toJSONString());
            JSONObject oJsonPaging = (JSONObject) oJSONObject.get("paging");
            pageNumber = Integer.parseInt(String.valueOf(oJsonPaging.get("page_number")));
            totalPages = Integer.parseInt(String.valueOf(oJsonPaging.get("page_size")));
            aJsonRegions.addAll((JSONArray) oJSONObject.get("data"));
        } while (pageNumber++ == totalPages);
    }

    private String getCommonInfoURL(String apiUrlEnd, int pageNumber){
        String sURL = String.format("%s/%s?%s=%s", 
                EHEALTH_URL,
                apiUrlEnd,
                PAGE_PROPERTY,
                pageNumber);
        LOG.info("Formed URL for ehealth: " + sURL);
        return sURL;
    }
    
    
}
