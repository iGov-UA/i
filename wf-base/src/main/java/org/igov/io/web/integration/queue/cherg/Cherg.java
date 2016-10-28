package org.igov.io.web.integration.queue.cherg;

import org.apache.commons.lang3.StringUtils;
import org.igov.io.GeneralConfig;
import org.igov.io.web.HttpEntityCover;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.codec.Base64;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import javax.annotation.PostConstruct;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Iterator;

/**
 * Provides integration with Queue management system cherg.net
 * API specified at http://tapi.cherg.net/
 * Created by Dmytro Tsapko on 14.08.2016.
 */

@Component
public class Cherg {

    @Autowired
    GeneralConfig generalConfig;

    private String urlBasePart;
    private String urlFreeTime = "/freetime";
    private String urlSetReserve = "/set_reserve";
    private String urlConfirmReserve = "/confirm_reserve";
    private String urlWorkdays = "/workdays";
    private String login;
    private String password;
    private String basicAuthHeader;

    final static private Logger LOG = LoggerFactory.getLogger(Cherg.class);

    @PostConstruct
    public void initialize() {
        this.urlBasePart = generalConfig.getQueueManagementSystemAddress();
        if (StringUtils.isBlank(urlBasePart)) {
            LOG.error("Please check urlBasePart in cherg.net Queue management system integration properties");
        }
        this.login = generalConfig.getQueueManagementSystemLogin();
        if (StringUtils.isBlank(login)) {
            LOG.error("Please check login in cherg.net Queue management system integration properties");
        }
        this.password = generalConfig.getQueueManagementSystemPassword();
        if (StringUtils.isBlank(password)) {
            LOG.error("Please check password in cherg.net Queue management system integration properties");
        }

        String auth = this.login + ":" + this.password;
        byte[] authHeaderBytes = Base64.encode(auth.getBytes(StandardCharsets.UTF_8));
        this.basicAuthHeader = "Basic " + new String(authHeaderBytes);
    }

    public JSONArray getFreeTime(DateTime date, Integer serviceId) throws Exception {
        if (date == null || serviceId == null) {
            LOG.error("date=={}, service_id=={}", date, serviceId);
            throw new IllegalArgumentException("date or service_id is null");
        }

        DateTimeFormatter dateTimeFormatter = DateTimeFormat.forPattern("y-MM-dd");
        String dateFormatted = dateTimeFormatter.print(date);
        MultiValueMap<String, Object> mParam = new LinkedMultiValueMap<>();
        LOG.info("service_id={}, date={}", serviceId, dateFormatted);
        mParam.add("service_id", serviceId.toString());
        mParam.add("date", dateFormatted);

        HttpHeaders oHttpHeaders = new HttpHeaders();
        oHttpHeaders.setContentType(MediaType.MULTIPART_FORM_DATA);
        oHttpHeaders.set("Authorization", this.basicAuthHeader);
        oHttpHeaders.setAcceptCharset(Arrays.asList(new Charset[] { StandardCharsets.UTF_8 }));
        HttpEntityCover oHttpEntityCover = new HttpEntityCover(urlBasePart + urlFreeTime)
                ._Data(mParam)
                ._Header(oHttpHeaders)
                ._Send();
        String sReturn = oHttpEntityCover.sReturn();
        if (!oHttpEntityCover.bStatusOk()) {
            LOG.error("RESULT FAIL! (sURL={}, mParamObject={}, nReturn={}, sReturn(cuted)={})",
                    urlBasePart + urlFreeTime,
                    mParam.toString(), oHttpEntityCover.nStatus(), sReturn);
            throw new Exception("[sendRequest](sURL=" + urlBasePart + urlFreeTime + "): nStatus()="
                    + oHttpEntityCover.nStatus());
        }

        JSONParser parser = new JSONParser();
        JSONObject result = (JSONObject) parser.parse(sReturn);
        if (!result.get("status-code").equals("0")) {
            LOG.error("code=={}, detail=={}", result.get("status-code"), result.get("status-detail"));
            //skip all errors from queue management system and return just empty array for date
            return new JSONArray();
        }
        JSONArray dates = (JSONArray) result.get("data");

        LOG.info("Result:{}", dates);
        return dates;

    }

    public JSONArray getSlotFreeDaysArray(Integer nID_Service_Private) throws Exception {
        if (nID_Service_Private == null) {
            LOG.error("service_id=={}", nID_Service_Private);
            throw new IllegalArgumentException("nID_Service_Private is null");
        }

        MultiValueMap<String, Object> mParam = new LinkedMultiValueMap<>();
        LOG.debug("nID_Service_Private={}", nID_Service_Private);
        mParam.add("service_id", nID_Service_Private.toString());

        HttpHeaders oHttpHeaders = new HttpHeaders();
        oHttpHeaders.setContentType(MediaType.MULTIPART_FORM_DATA);
        oHttpHeaders.set("Authorization", this.basicAuthHeader);
        oHttpHeaders.setAcceptCharset(Arrays.asList(new Charset[] { StandardCharsets.UTF_8 }));
        HttpEntityCover oHttpEntityCover = new HttpEntityCover(urlBasePart + urlWorkdays)
                ._Data(mParam)
                ._Header(oHttpHeaders)
                ._Send();
        String sReturn = oHttpEntityCover.sReturn();
        if (!oHttpEntityCover.bStatusOk()) {
            LOG.error("RESULT FAIL! (sURL={}, mParamObject={}, nReturn={}, sReturn(cuted)={})",
                    urlBasePart + urlWorkdays,
                    mParam.toString(), oHttpEntityCover.nStatus(), sReturn);
            throw new Exception("[sendRequest](sURL=" + urlBasePart + urlWorkdays + "): nStatus()="
                    + oHttpEntityCover.nStatus());
        } else if ( sReturn == null ) {
            throw new Exception("Response is null for: [sendRequest](sURL=" + urlBasePart + urlWorkdays + "): nStatus()="
                    + oHttpEntityCover.nStatus());
        }

        JSONParser oJSONParser = new JSONParser();
        JSONObject oJSONObjectGot;
        JSONArray oaJSONArrayReturn = null;
	try {
	    oJSONObjectGot = (JSONObject) oJSONParser.parse(sReturn);
	
	    JSONArray oaJSONArray =  new JSONArray();
	    if (!oJSONObjectGot.get("status-code").equals("0")) {
	       LOG.error("code=={}, detail=={}", oJSONObjectGot.get("status-code"), oJSONObjectGot.get("status-detail"));
	    }else{
	       oaJSONArray = (JSONArray) oJSONObjectGot.get("data");
	       LOG.info("Workdays all days:{}", oaJSONArray);
	    }

	    oaJSONArrayReturn = new JSONArray();
	    for(Object o:oaJSONArray) {
	       JSONObject oJSONObject = (JSONObject) o;
	       String sDate = oJSONObject.get("date").toString();
	       String snDateType = oJSONObject.get("work_day").toString();
	       if ( snDateType.equals("1")) {
		   oaJSONArrayReturn.add(sDate);
	       }
	    }
	    
	} catch (ParseException e) {
            LOG.error("Error parsing response = {}", sReturn, e);
	}

        return oaJSONArrayReturn;
    }
    
    public String getSlotFreeDays(Integer nID_Service_Private) throws Exception {
        JSONArray oaJSONArray = getSlotFreeDaysArray(nID_Service_Private);
        
        if (oaJSONArray == null ) {
            throw new Exception("");            
        }
                
	JSONObject oJSONObjectReturn = new JSONObject();
	oJSONObjectReturn.put("aDate", oaJSONArray);
        LOG.info("Workdays only work days:{}", oJSONObjectReturn.toJSONString());
        
        return oJSONObjectReturn.toString();

    }

//    public static void main(String[] args) {
//	try {
//	    JSONParser parser = new JSONParser();
//	    JSONObject result;
//	    result = (JSONObject) parser.parse("{\"data\":[{    \"date\": \"2016-10-21\",    \"work_day\": 1  },  {    \"date\": \"2016-10-22\",    \"work_day\": 1  },]}");
//	    JSONArray dates = (JSONArray) result.get("data");
//
//	    JSONArray retJSONArray = new JSONArray();
//	    for(Object o:dates) {
//		JSONObject jo = (JSONObject) o;
//		String date = jo.get("date").toString();
//		String work_day = jo.get("work_day").toString();
//		
//		if ( work_day.equals("1")) {
//		    retJSONArray.add(date);
//		    System.out.println(date +" " +work_day);
//		}
//	    }
//	    JSONObject retJSON = new JSONObject();
//	    retJSON.put("aDate", retJSONArray);
//	    
//	    System.out.println(retJSON.toString());
//	    
//	    
//	} catch (Exception e) {
//	    e.printStackTrace();
//	}
//    }
    
    public JSONObject setReserve(String serviceId, String dateTime, String phone, String passport, String lastName,
            String name, String patronymic) throws Exception {

        MultiValueMap<String, Object> mParam = new LinkedMultiValueMap<>();

        mParam.add("service_id", serviceId);
        mParam.add("date_time", dateTime);
        mParam.add("phone", phone);
        mParam.add("passport", passport);
        mParam.add("lastname", lastName);
        mParam.add("name", name);
        mParam.add("patronymic", patronymic);

        HttpHeaders oHttpHeaders = new HttpHeaders();
        oHttpHeaders.setContentType(new MediaType("application", "x-www-form-urlencoded", StandardCharsets.UTF_8));
        oHttpHeaders.set("Authorization", this.basicAuthHeader);
        oHttpHeaders.setAcceptCharset(Arrays.asList(new Charset[] { StandardCharsets.UTF_8 }));
        HttpEntityCover oHttpEntityCover = new HttpEntityCover(urlBasePart + urlSetReserve)
                ._Data(mParam)
                ._Header(oHttpHeaders)
                ._Send();
        String sReturn = oHttpEntityCover.sReturn();
        if (!oHttpEntityCover.bStatusOk()) {
            LOG.error("RESULT FAIL! (sURL={}, mParamObject={}, nReturn={}, sReturn(cuted)={})",
                    urlBasePart + urlFreeTime,
                    mParam.toString(), oHttpEntityCover.nStatus(), sReturn);
            throw new Exception("[sendRequest](sURL=" + urlBasePart + urlFreeTime + "): nStatus()="
                    + oHttpEntityCover.nStatus());
        }

        JSONParser parser = new JSONParser();
        JSONObject response = (JSONObject) parser.parse(sReturn);
        if (!response.get("status-code").equals("0")) {
            LOG.error("code=={}, detail=={}", response.get("status-code"), response.get("status-detail"));
            throw new Exception("[sendRequest](sURL=" + urlBasePart + urlFreeTime + "): response=" +
                    response.get("status-code") + " " + response.get("status-detail"));
        }
        JSONArray dates = (JSONArray) response.get("data");
        JSONObject result;
        Iterator<JSONObject> datesIterator = dates.iterator();
        if (datesIterator.hasNext()) {
            result = datesIterator.next();
        } else {
            result = new JSONObject();
        }

        LOG.info("Result:{}", dates);
        return result;

    }

    public JSONObject confirmReserve(String nReservationId) throws Exception {
        MultiValueMap<String, Object> mParam = new LinkedMultiValueMap<>();

        mParam.add("reserve_id", nReservationId);

        HttpHeaders oHttpHeaders = new HttpHeaders();
        oHttpHeaders.setContentType(MediaType.MULTIPART_FORM_DATA);
        oHttpHeaders.set("Authorization", this.basicAuthHeader);
        oHttpHeaders.setAcceptCharset(Arrays.asList(new Charset[] { StandardCharsets.UTF_8 }));
        HttpEntityCover oHttpEntityCover = new HttpEntityCover(urlBasePart + urlConfirmReserve)
                ._Data(mParam)
                ._Header(oHttpHeaders)
                ._Send();
        String sReturn = oHttpEntityCover.sReturn();
        if (!oHttpEntityCover.bStatusOk()) {
            LOG.error("RESULT FAIL! (sURL={}, mParamObject={}, nReturn={}, sReturn(cuted)={})",
                    urlBasePart + urlFreeTime,
                    mParam.toString(), oHttpEntityCover.nStatus(), sReturn);
            throw new Exception("[sendRequest](sURL=" + urlBasePart + urlFreeTime + "): nStatus()="
                    + oHttpEntityCover.nStatus());
        }

        JSONParser parser = new JSONParser();
        JSONObject response = (JSONObject) parser.parse(sReturn);
        if (!response.get("status-code").equals("0")) {
            LOG.error("code=={}, detail=={}", response.get("status-code"), response.get("status-detail"));
            throw new Exception("[sendRequest](sURL=" + urlBasePart + urlFreeTime + "): response=" +
                    response.get("status-code") + " " + response.get("status-detail"));
        }
        JSONArray dates = (JSONArray) response.get("data");
        JSONObject result;
        Iterator<JSONObject> datesIterator = dates.iterator();
        if (datesIterator.hasNext()) {
            result = datesIterator.next();
        } else {
            result = new JSONObject();
        }

        LOG.info("Result:{}", dates);
        return result;

    }
}
