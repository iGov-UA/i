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
import org.wf.dp.dniprorada.util.unisender.requests.SubscribeRequest;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created by Dmytro Tsapko on 11/28/2015.
 *
 */
public class UniSender {
    final static private Logger LOG = LoggerFactory.getLogger(UniSender.class);
    final static private String API_URL = "https://api.unisender.com/";
    final static private String SUBSCRIBE_URI = "/api/subscribe";
    final static private String AND = "&";
    final private String apiKey;
    final private String lang;
    private StringBuilder resultUrl;

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

        this.resultUrl = new StringBuilder(this.API_URL);
        resultUrl.append(this.lang);
    }

    /**
     *
     * @param apiKey - api_key - this is access key for UniSender API.
     *              LANG parameter will be "EN".
     *
     */
    public UniSender(String apiKey) {
        this(apiKey, "en");
    }

    /**
     * rest resource described - https://support.unisender.com/index.php?/Knowledgebase/Article/View/57/0/subscribe---podpist-drest-n-odin-ili-neskolko-spiskov-rssylki
     * This method shall add user to mail list using UniSender service
     * @return
     */
    private UniResponse subscribe(SubscribeRequest subscribeRequest){

        MultiValueMap<String, String> parametersMap = new LinkedMultiValueMap<String, String>();

        //mandatory part
        StringBuilder resultUrl = new StringBuilder(this.resultUrl);
        resultUrl.append(SUBSCRIBE_URI);
        parametersMap.add("format", "json");
        parametersMap.add("api_key", apiKey);
        parametersMap.add("list_ids", StringUtils.join(subscribeRequest.getListIds(), ","));
        //conditionally mandatory
        if(!StringUtils.isBlank(subscribeRequest.getEmail())) parametersMap.add("fields[email]", subscribeRequest.getEmail());
        if(!StringUtils.isBlank(subscribeRequest.getPhone())) parametersMap.add("fields[phone]", subscribeRequest.getPhone());
        //optional
        if(subscribeRequest.getTags() != null && !subscribeRequest.getTags().isEmpty()) parametersMap.add("tags", StringUtils.join(
                subscribeRequest.getTags(), ","));
        if(!StringUtils.isBlank(subscribeRequest.getRequestIp())) parametersMap.add("request_ip", subscribeRequest.getRequestIp());
        if(subscribeRequest.getRequestTime() != null) parametersMap.add("request_time", getFormattedDate(
                subscribeRequest.getRequestTime()));
        parametersMap.add("double_optin", Integer.toString(subscribeRequest.getDoubleOptin()));
        if(!StringUtils.isBlank(subscribeRequest.getConfirmIp())) parametersMap.add("confirm_ip", subscribeRequest.getConfirmIp());
        if(subscribeRequest.getConfirmTime() != null) parametersMap.add("confirm_time", getFormattedDate(
                subscribeRequest.getConfirmTime()));
        parametersMap.add("overwrite", Integer.toString(subscribeRequest.getOverwrite()));

        LOG.info("result URL: {}", resultUrl.toString());
        LOG.info("result Parameters: {}", parametersMap);

        UniResponse uniResponse = sendRequest(parametersMap, resultUrl.toString());

        LOG.info("result uniResponse: {}", uniResponse);

        return uniResponse;
    }

    /**
     *  this method has double_optin equals to 3 and overwrite equals to 1.
     * @param listIds
     * @param email
     * @return
     */
    public UniResponse subscribe(List<String> listIds, String email){

        SubscribeRequest subscribeRequest = SubscribeRequest.getBuilder(this.apiKey, this.lang)
                .setListIds(listIds)
                .setEmail(email)
                .setDoubleOptin(3)
                .setOverwrite(1).build();

        return subscribe(subscribeRequest);

    }

    private UniResponse sendRequest(MultiValueMap<String, String> parametersMap, String resultUrl) {
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(MediaType.MULTIPART_FORM_DATA);
        httpHeaders.setAcceptCharset(Arrays.asList(new Charset[] { StandardCharsets.UTF_8 }));
        HttpEntity httpEntity = new HttpEntity(parametersMap, httpHeaders);
        String response = restTemplate.postForObject(resultUrl, httpEntity, String.class);
        LOG.info("result JSON response from UniSender: {}", response);
        return getUniResponse(response);
    }

    private UniResponse getUniResponse(String response) {
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



}

class X {
    public static void main(String[] args) {
        new UniSender("5dyw56a7yw6rooh4pimnbezxg3f7pzjpmp6yaiby", "en").
                subscribe(Collections.singletonList("6018070"), "zzayass@bigmir.net");
    }
}

