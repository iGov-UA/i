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

        DateTimeFormatter dateTimeFormatter = DateTimeFormat.forPattern("y-MM-d");
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
            throw new Exception("[sendRequest](sURL=" + urlBasePart + urlFreeTime + "): response=" +
                    result.get("status-code") + " " + result.get("status-detail"));
        }
        JSONArray dates = (JSONArray) result.get("data");

        LOG.info("Result:{}", dates);
        return dates;

    }

}
