package org.igov.service.controller.interceptor;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.ParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

/**
 * User: goodg_000 
 * Date: 25.08.2015
 * Time: 21:01
 */
public final class JsonRequestDataResolver {

	private static final Logger LOG = LoggerFactory.getLogger(JsonRequestDataResolver.class);
    public static Map<String, String> getProperties(JSONObject requestJson) throws ParseException {

        Map<String, String> res = new HashMap<>();

        JSONArray jsonArray = (JSONArray) requestJson.get("properties");
        for (int i = 0; i < jsonArray.size(); ++i) {
            JSONObject property = (JSONObject) jsonArray.get(i);
            res.put(String.valueOf(property.get("id")), String.valueOf(property.get("value"))); //
        }
        LOG.info("result in JsonRequestDataResolver ={}", res);
        return res;
        
    }

    public static String getEmail(JSONObject requestJson) throws ParseException {
        return getProperties(requestJson).get("email");
    }

    public static String getPhone(JSONObject requestJson) throws ParseException {
        return getProperties(requestJson).get("phone");
    }
    public static String getBankIdFirstName(JSONObject requestJson) throws ParseException {
        return getProperties(requestJson).get("bankIdfirstName");
    }

    public static String getBankIdLastName(JSONObject requestJson) throws ParseException {
        return getProperties(requestJson).get("bankIdlastName");
    }
    public static String getsMailClerk (JSONObject requestJson) throws ParseException {
    	return getProperties(requestJson).get("sMailClerk");
    }
    public static String sMailClerk (JSONObject requestJson) throws ParseException {
    	return getProperties(requestJson).get("sMailClerk");
    }
}