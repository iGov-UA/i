package org.wf.dp.dniprorada.util.unisender;

import com.mongodb.util.JSON;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created by Dmytro Tsapko on 11/28/2015.
 *
 */
public class UniSender {
    private final static Logger LOG = LoggerFactory.getLogger(UniSender.class);
    final private String apiKey;
    final private String lang;
    final static private String API_URL = "http://api.unisender.com/";
    final static private String SUBSCRIBE_URI = "/api/subscribe";
    final static private String AND = "&";

    /**
     *
     * @param apiKey - api_key - this is access key for UniSender API
     * @param lang - LANG language of UniSender API messages
     */
    public UniSender(String apiKey, String lang) {

        if(StringUtils.isBlank(apiKey) || StringUtils.isBlank(lang))
            throw new IllegalArgumentException("Neither the Api key nor lang can't be empty.");

        this.apiKey = apiKey;
        this.lang = lang;
    }

    /**
     *
     * @param apiKey - api_key - this is access key for UniSender API.
     *              LANG parameter will be "EN".
     *
     */
    public UniSender(String apiKey) {

        if(StringUtils.isBlank(apiKey))
            throw new IllegalArgumentException("Api key can't be empty.");

        this.apiKey = apiKey;
        this.lang = "en";
    }

    /**
     * rest resource described - https://support.unisender.com/index.php?/Knowledgebase/Article/View/57/0/subscribe---podpist-drest-n-odin-ili-neskolko-spiskov-rssylki
     * This method shall add user to mail list using UniSender service
     * @param listIds - list_ids
     * @param email
     * @param phone
     * @param tags
     * @param requestIp - request_ip
     * @param requestTime - request_time
     * @param doubleOptin - double_optin
     * @param confirmIp - confirm_ip
     * @param confirmTime - confirm_time
     * @param overwrite
     * @return
     */
    public UniResponse subscribe(
            List<String> listIds,
            String email,
            String phone,
            List<String> tags,
            String requestIp,
            Date requestTime,
            int doubleOptin,
            String confirmIp,
            Date confirmTime,
            int overwrite){

        if(listIds == null || listIds.isEmpty())
            throw new IllegalArgumentException("Mailing List ID can't be empty");
        if(StringUtils.isBlank(email) && StringUtils.isBlank(phone))
            throw new IllegalArgumentException("Email and Phone are empty. At least one argument can't be empty.");
        if (doubleOptin < 0 || doubleOptin > 3)
            throw new IllegalArgumentException("doubleOptin must be in range [0-3]");
        if (overwrite < 0 || overwrite > 3)
            throw new IllegalArgumentException("overwrite must be in range [0-3]");

        MultiValueMap<String, String> parametersMap = new LinkedMultiValueMap<String, String>();

        //mandatory part
        StringBuilder resultUrl = new StringBuilder(this.API_URL);
        resultUrl.append(this.lang);
        resultUrl.append(SUBSCRIBE_URI);
        parametersMap.add("format", "json");
        parametersMap.add("api_key", apiKey);
        parametersMap.add("list_ids", StringUtils.join(listIds, ","));
        //conditionally mandatory
        if(!StringUtils.isBlank(email)) parametersMap.add("fields[email]", email);
        if(!StringUtils.isBlank(phone)) parametersMap.add("fields[phone]", phone);
        //optional
        if(tags != null && !tags.isEmpty()) parametersMap.add("tags", StringUtils.join(tags, ","));
        if(!StringUtils.isBlank(requestIp)) parametersMap.add("request_ip", requestIp);
        if(requestTime != null) parametersMap.add("request_time", getFormattedDate(requestTime));
        parametersMap.add("double_optin", Integer.toString(doubleOptin));
        if(!StringUtils.isBlank(confirmIp)) parametersMap.add("confirm_ip", confirmIp);
        if(confirmTime != null) parametersMap.add("confirm_time", getFormattedDate(confirmTime));
        parametersMap.add("overwrite", Integer.toString(overwrite));

        LOG.info("result URL: {}", resultUrl.toString());
        LOG.info("result Parameters: {}", parametersMap);

        UniResponse uniResponse = sendRequest(parametersMap, resultUrl.toString());

        LOG.info("result uniResponse: {}", uniResponse);

        return uniResponse;
    }

    private UniResponse sendRequest(MultiValueMap<String, String> parametersMap, String resultUrl) {
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(MediaType.MULTIPART_FORM_DATA);
        httpHeaders.setAcceptCharset(Arrays.asList(new Charset[] { StandardCharsets.UTF_8 }));
        HttpEntity httpEntity = new HttpEntity(parametersMap, httpHeaders);
        String response = restTemplate.postForObject(resultUrl, httpEntity, String.class);

        Map<String, Object> resultMapFromJson = (Map<String, Object>) JSON.parse(response);
        Object error = resultMapFromJson.get("error");

        Map<String, String> errorMap = new HashMap<>();
        Map<String, Object> result = Collections.emptyMap();
        List<String> resultWarnings = new ArrayList<>();

        if(error != null){
            errorMap.put("error", error.toString());
            errorMap.put("code", resultMapFromJson.get("code").toString());
        }else {
            result = (Map<String, Object>) resultMapFromJson.get("result");
            if(result == null){
                result = Collections.emptyMap();
            }

            List<Map<String, String>> warnings= (List<Map<String, String>>) resultMapFromJson.get("warnings");
            if (warnings != null) {
                for(Map<String, String> warning : warnings){
                    if (warning != null && !warning.isEmpty()) {
                        resultWarnings.add(warning.get(warning.keySet().iterator().next()));
                    }
                }
            }
        }
        return new UniResponse(result, resultWarnings, errorMap);
    }

    private String getFormattedDate(Date requestTime) {
        if(requestTime == null) return StringUtils.EMPTY;

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(requestTime);
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        return dateFormat.format(calendar.getTime());
    }

    /**
     *  this method has double_optin equals to 3 and overwrite equals to 1.
     * @param listIds
     * @param email
     * @return
     */
    public UniResponse subscribe(List<String> listIds, String email){
        return subscribe(listIds, email, null, null, null, null, 3, null, null, 1);
    }


}


