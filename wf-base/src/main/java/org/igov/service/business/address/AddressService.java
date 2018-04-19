package org.igov.service.business.address;

import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.igov.io.GeneralConfig;
import org.igov.io.web.RestRequest;
import org.igov.model.ehealth.address.Settlement;
import org.igov.model.ehealth.address.Street;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;

@Service
public class AddressService {
    
    @Autowired
    private GeneralConfig oGeneralConfig;

    private final static Logger LOG = LoggerFactory.getLogger(AddressService.class);

    private static final String PAGE_NUMBER_PROPERTY = "page";
    private static final String PAGE_SIZE_PROPERTY = "page_size";
    private static final Integer MAX_PAGE_SIZE = 500;
    
    private static final String API_REGIONS_RESOURCE = "regions";
    private static final String API_DISTRICTS_RESOURCE = "districts";
    private static final String API_SETTLEMENTS_RESOURCE = "settlements";
    private static final String API_STREET_RESOURCE = "streets";
    
    private final JSONParser parser = new JSONParser();
    private final Gson gson = new Gson();

    public JSONArray getListRegions() {
        JSONArray aJsonRegions = new JSONArray();
        getJSONResponse(API_REGIONS_RESOURCE, aJsonRegions, new HashMap());
        return aJsonRegions;
    }

    public JSONArray getListDistricts(String sRegion) {
        JSONArray aJsonDistricts = new JSONArray();
        Map<String, Object> properties = new HashMap();
        properties.put("region", sRegion);
        getJSONResponse(API_DISTRICTS_RESOURCE, aJsonDistricts, properties);
        return aJsonDistricts;
    }
    
    public List<Settlement> getListSettlements(String sRegion, String sDistrict, String sType, String sNameFilter) {
        JSONArray aJsonSettlements = new JSONArray();
        Map<String, Object> properties = new HashMap();
        properties.put("region", sRegion);
        properties.put("district", sDistrict);
        properties.put("type", sType);
        getJSONResponse(API_SETTLEMENTS_RESOURCE, aJsonSettlements, properties);
        
        Type type = new TypeToken<List<Settlement>>(){}.getType();
        List<Settlement> aoSettlements = gson.fromJson(aJsonSettlements.toJSONString(), type);
        List<Settlement> aoSettlementsFiltered =  aoSettlements
                .stream()
                .filter(oSettlement -> oSettlement.getName().startsWith(sNameFilter))
                .collect(Collectors.toList());
        return aoSettlementsFiltered;
    }
    
    public List<Street> getListStreets(String sID_Settlement, String sType, String sNameFilter) {
        JSONArray aJsonStreets = new JSONArray();
        Map<String, Object> properties = new HashMap();
        properties.put("settlement_id", sID_Settlement);
        properties.put("type", sType);
        getJSONResponse(API_STREET_RESOURCE, aJsonStreets, properties);
        
        Type type = new TypeToken<List<Street>>(){}.getType();
        List<Street> aoStreets = gson.fromJson(aJsonStreets.toJSONString(), type);
        return aoStreets
                .stream()
                .filter(oStreet -> sNameFilter.startsWith(oStreet.getName()))
                .collect(Collectors.toList());
    }

    private void getJSONResponse(String sURLResource, JSONArray aJsonResult, Map<String, Object> properties) {
        int pageNumber = 0; // default start page number
        int totalPages = 0;
        HttpHeaders headers = new HttpHeaders();
        headers.set("Content-Type", "application/json; charset=utf-8");
        do {
            try {
                String resp = new RestRequest().get(getCommonInfoURL(sURLResource, pageNumber, properties), null, StandardCharsets.UTF_8, String.class, headers);
                JSONObject oJSONObject = (JSONObject) parser.parse(resp);
                JSONObject oJsonPaging = (JSONObject) oJSONObject.get("paging");
                pageNumber = Integer.parseInt(String.valueOf(oJsonPaging.get("page_number")));
                totalPages = Integer.parseInt(String.valueOf(oJsonPaging.get("total_pages")));
                aJsonResult.addAll((JSONArray) oJSONObject.get("data"));
            } catch (RestClientException rx) {
                LOG.info("Error: {}, occured while getting result from ehealth service by URL:{} ", rx.getMessage(), sURLResource);
            } catch (ParseException ex) {
                LOG.info("Error: {}, occured while parsing json data from ehealth service by URL:{} ", ex.getMessage(), sURLResource);
            }
        } while (pageNumber++ < totalPages);
    }

    private String getCommonInfoURL(String apiUrlEnd, int pageNumber, Map<String, Object> properties) {
        String sURL = String.format("%s/%s?%s=%s&%s=%s",
                oGeneralConfig.getsURL_Ehealth_Api_Uaddresses(),
                apiUrlEnd,
                PAGE_NUMBER_PROPERTY,
                pageNumber,
                PAGE_SIZE_PROPERTY,
                MAX_PAGE_SIZE);
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
            if (!"null".equals(entryValue) && !"".equals(entryValue)) {
                    saParam += "&" + entry.getKey() + "=" + entryValue;
            }
        }
        return sURL + saParam;
    }
}
