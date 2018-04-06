package org.igov.service.business.address;

import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Type;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;
import java.util.stream.Collectors;
import org.igov.io.web.RestRequest;
import org.igov.model.ehealth.address.Settlement;
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
    private static final String PAGE_NUMBER_PROPERTY = "page";
    private static final String PAGE_SIZE_PROPERTY = "page_size";
    private static final Integer MAX_PAGE_SIZE = 500;
    private static final String API_REGIONS_RESOURCE = "regions";
    private static final String API_DISTRICTS_RESOURCE = "districts";
    private static final String API_SETTLEMENTS_RESOURCE = "settlements";

    private final JSONParser parser = new JSONParser();
    private final Gson gson = new Gson();

    public JSONArray getListRegions()
            throws InterruptedException, ExecutionException, TimeoutException, ParseException {
        JSONArray aJsonRegions = new JSONArray();
        getJSONResponse(API_REGIONS_RESOURCE, aJsonRegions, new HashMap());
        return aJsonRegions;
    }

    public JSONArray getListDistricts(String sRegion) throws ParseException {
        JSONArray aJsonDistricts = new JSONArray();
        Map<String, Object> properties = new HashMap();
        properties.put("region", sRegion);
        getJSONResponse(API_DISTRICTS_RESOURCE, aJsonDistricts, properties);
        return aJsonDistricts;
    }
    
    public List<Settlement> getListSettlements(String sRegion, String sDistrict, String sType, String sNameFilter) throws ParseException, IOException {
        JSONArray aJsonSettlements = new JSONArray();
        Map<String, Object> properties = new HashMap();
        properties.put("region", sRegion);
        properties.put("district", sDistrict);
        properties.put("type", sType);
        getJSONResponse(API_SETTLEMENTS_RESOURCE, aJsonSettlements, properties);
        
        Type type = new TypeToken<List<Settlement>>(){}.getType();
        List<Settlement> aoSettlements = gson.fromJson(aJsonSettlements.toJSONString(), type);
        return aoSettlements
                .stream()
                .filter(oSettlement -> sNameFilter.equalsIgnoreCase(oSettlement.getsName()))
                .collect(Collectors.toList());
    }

    private void getJSONResponse(String sURLResource, JSONArray aJsonResult, Map<String, Object> properties) throws ParseException, RestClientException {
        int pageNumber = 1; // default start page number
        int totalPages;
        boolean res;
        HttpHeaders headers = new HttpHeaders();
        headers.set("Content-Type", "application/json; charset=utf-8");
        do {
            String resp = new RestRequest().get(getCommonInfoURL(sURLResource, pageNumber, properties), null, StandardCharsets.UTF_8, String.class, headers);
            JSONObject oJSONObject = (JSONObject) parser.parse(resp);
            LOG.info("oJSONObject is {}", oJSONObject.toJSONString());
            JSONObject oJsonPaging = (JSONObject) oJSONObject.get("paging");
            pageNumber = Integer.parseInt(String.valueOf(oJsonPaging.get("page_number")));
            totalPages = Integer.parseInt(String.valueOf(oJsonPaging.get("total_pages")));
            LOG.info("pageNumber is {}", pageNumber);
            LOG.info("totalPages is {}", totalPages);
            res = pageNumber++ == totalPages;
            LOG.info("res is {}", res);
            aJsonResult.addAll((JSONArray) oJSONObject.get("data"));
        } while (res);
    }

    private String getCommonInfoURL(String apiUrlEnd, int pageNumber, Map<String, Object> properties) {
        String sURL = String.format("%s/%s?%s=%s&%s=%s",
                EHEALTH_URL,
                apiUrlEnd,
                PAGE_NUMBER_PROPERTY,
                pageNumber,
                PAGE_SIZE_PROPERTY,
                MAX_PAGE_SIZE);
        LOG.info("Formed URL for ehealth: " + sURL);
        if (!properties.isEmpty()) {
            sURL = addParamsToURL(sURL, properties);
        }
        LOG.info("Result URL for ehealth: " + sURL);
        return sURL;
    }

    public String addParamsToURL(String sURL, Map<String, Object> properties) {
        String saParam = "";
        for (Map.Entry<String, Object> entry : properties.entrySet()) {
            String entryValue = String.valueOf(entry.getValue());
            if (entryValue != "null" || entryValue != "") {
                try {
                    saParam += "&" + URLEncoder.encode(entry.getKey(), "UTF-8") + "="
                            + URLEncoder.encode(entryValue, "UTF-8");
                } catch (UnsupportedEncodingException e) {
                    LOG.error("ERROR={}, occured while parsing params: key={}, value={}",e.getMessage(), entry.getKey(), entryValue);
                }
            }
        }
        LOG.info("Properties: " + properties);
        return sURL + saParam;
    }
}
