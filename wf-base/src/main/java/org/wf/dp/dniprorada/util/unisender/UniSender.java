package org.wf.dp.dniprorada.util.unisender;

import com.mongodb.util.JSON;
import java.io.UnsupportedEncodingException;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.FormHttpMessageConverter;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.ResourceHttpMessageConverter;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.wf.dp.dniprorada.util.unisender.requests.CreateCampaignRequest;
import org.wf.dp.dniprorada.util.unisender.requests.CreateEmailMessageRequest;
import org.wf.dp.dniprorada.util.unisender.requests.SubscribeRequest;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created by Dmytro Tsapko on 11/28/2015.
 */
public class UniSender {
    final static private Logger log = LoggerFactory.getLogger(UniSender.class);
    final static private String API_URL = "http://api.unisender.com/";
    final static private String SUBSCRIBE_URI = "/api/subscribe";
    final static private String CREATE_EMAIL_MESSAGE_URI = "/api/createEmailMessage";
    final static private String CREATE_CAMPAIGN_URI = "/api/createCampaign";
    final static private String AND = "&";
    final private String apiKey;
    final private String lang;
    private StringBuilder resultUrl;

    /**
     * @param apiKey - api_key - this is access key for UniSender API
     * @param lang   - LANG language of UniSender API messages
     */
    public UniSender(String apiKey, String lang) {

        if (StringUtils.isBlank(apiKey) || StringUtils.isBlank(lang))
            throw new IllegalArgumentException("Neither the Api key nor lang can't be empty.");

        this.apiKey = apiKey;
        this.lang = lang;

        this.resultUrl = new StringBuilder(this.API_URL);
        resultUrl.append(this.lang);
    }

    /**
     * @param apiKey - api_key - this is access key for UniSender API.
     *               LANG parameter will be "EN".
     */
    public UniSender(String apiKey) {
        this(apiKey, "en");
    }

    /**
     * rest resource described - https://support.unisender.com/index.php?/Knowledgebase/Article/View/57/0/subscribe---podpist-drest-n-odin-ili-neskolko-spiskov-rssylki
     * This method shall add user to mail list using UniSender service
     *
     * @return
     */
    public UniResponse subscribe(SubscribeRequest subscribeRequest) {

        MultiValueMap<String, Object> parametersMap = new LinkedMultiValueMap<String, Object>();

        //mandatory part
        StringBuilder resultUrl = new StringBuilder(this.resultUrl);
        resultUrl.append(SUBSCRIBE_URI);
        parametersMap.add("format", "json");
        parametersMap.add("api_key", apiKey);
        parametersMap.add("list_ids", StringUtils.join(subscribeRequest.getListIds(), ","));
        //conditionally mandatory
        if (!StringUtils.isBlank(subscribeRequest.getEmail()))
            parametersMap.add("fields[email]", subscribeRequest.getEmail());
        if (!StringUtils.isBlank(subscribeRequest.getPhone()))
            parametersMap.add("fields[phone]", subscribeRequest.getPhone());
        //optional
        if (subscribeRequest.getTags() != null && !subscribeRequest.getTags().isEmpty())
            parametersMap.add("tags", StringUtils.join(
                    subscribeRequest.getTags(), ","));
        if (!StringUtils.isBlank(subscribeRequest.getRequestIp()))
            parametersMap.add("request_ip", subscribeRequest.getRequestIp());
        if (subscribeRequest.getRequestTime() != null)
            parametersMap.add("request_time", getFormattedDate(
                    subscribeRequest.getRequestTime()));
        parametersMap.add("double_optin", Integer.toString(subscribeRequest.getDoubleOptin()));
        if (!StringUtils.isBlank(subscribeRequest.getConfirmIp()))
            parametersMap.add("confirm_ip", subscribeRequest.getConfirmIp());
        if (subscribeRequest.getConfirmTime() != null)
            parametersMap.add("confirm_time", getFormattedDate(
                    subscribeRequest.getConfirmTime()));
        parametersMap.add("overwrite", Integer.toString(subscribeRequest.getOverwrite()));

        log.info("result URL: {}", resultUrl.toString());
        log.info("result Parameters: {}", parametersMap);

        UniResponse uniResponse = sendRequest(parametersMap, resultUrl.toString(), null);

        log.info("result uniResponse: {}", uniResponse);

        return uniResponse;
    }

    /**
     * this method has double_optin equals to 3 and overwrite equals to 1.
     *
     * @param listIds
     * @param email
     * @return
     */
    public UniResponse subscribe(List<String> listIds, String email) {

        SubscribeRequest subscribeRequest = SubscribeRequest.getBuilder(this.apiKey, this.lang)
                .setListIds(listIds)
                .setEmail(email)
                .setDoubleOptin(3)
                .setOverwrite(1).build();

        return subscribe(subscribeRequest);

    }

    public UniResponse createEmailMessage(String senderName, String senderEmail, String subject, String body,
            String listId) {

        CreateEmailMessageRequest createEmailMessageRequest = CreateEmailMessageRequest
                .getBuilder(this.apiKey, this.lang)
                .setSenderName(senderName)
                .setSenderEmail(senderEmail)
                .setSubject(subject)
                .setBody(body)
                .setListId(listId)
                .build();
        return createEmailMessage(createEmailMessageRequest);
    }

    public UniResponse createEmailMessage(CreateEmailMessageRequest oCreateEmailMessageRequest) {

        MultiValueMap<String, Object> mParamObject = new LinkedMultiValueMap<String, Object>();
        MultiValueMap<String, ByteArrayResource> mParamByteArray = new LinkedMultiValueMap<String, ByteArrayResource>();

        //mandatory part
        StringBuilder osURL = new StringBuilder(this.resultUrl);
        osURL.append(CREATE_EMAIL_MESSAGE_URI);
        mParamObject.add("format", "json");
        mParamObject.add("api_key", apiKey);
        mParamObject.add("sender_name", oCreateEmailMessageRequest.getSenderName());
        mParamObject.add("sender_email", oCreateEmailMessageRequest.getSenderEmail());
        //parametersMap.add("subject", createEmailMessageRequest.getSubject());
        //String subject = createEmailMessageRequest.getSubject() == null || "".equals(createEmailMessageRequest.getSubject()) ? " " : createEmailMessageRequest.getSubject();
        mParamByteArray.add("subject", new ByteArrayResource(oCreateEmailMessageRequest.getSubject().getBytes(StandardCharsets.UTF_8)));
        String sBody = oCreateEmailMessageRequest.getSubject() + " | " +  oCreateEmailMessageRequest.getBody();
        mParamByteArray.add("body", new ByteArrayResource(sBody.getBytes(StandardCharsets.UTF_8)));
        mParamObject.add("list_id", oCreateEmailMessageRequest.getListId());
        //optional
        if (!StringUtils.isBlank(oCreateEmailMessageRequest.getTextBody()))
            mParamObject.add("text_body", oCreateEmailMessageRequest.getTextBody());
        //generate_text has default value == 0
        mParamObject.add("generate_text", Integer.toString(oCreateEmailMessageRequest.getGenerateText()));

        if (!StringUtils.isBlank(oCreateEmailMessageRequest.getTag()))
            mParamObject.add("tag", oCreateEmailMessageRequest.getTag());

        Map<String, ByteArrayResource> mAttachment = oCreateEmailMessageRequest.getAttachments();
        for (String sFileName : mAttachment.keySet()) {
            ByteArrayResource oAttachment = mAttachment.get(sFileName);
            mParamByteArray.add("oAttachment[" + sFileName + "]", oAttachment);
        }

        if (!StringUtils.isBlank(oCreateEmailMessageRequest.getLang()))
            //parametersMap.add("lang", createEmailMessageRequest.getLang());
            mParamObject.add("lang", "ua");
        if (!StringUtils.isBlank(oCreateEmailMessageRequest.getSeriesDay()))
            mParamObject.add("series_day", oCreateEmailMessageRequest.getSeriesDay());
        if (!StringUtils.isBlank(oCreateEmailMessageRequest.getSeriesTime()))
            mParamObject.add("series_time", oCreateEmailMessageRequest.getSeriesTime());
        if (!StringUtils.isBlank(oCreateEmailMessageRequest.getWrapType()))
            mParamObject.add("wrap_type", oCreateEmailMessageRequest.getWrapType());
        if (!StringUtils.isBlank(oCreateEmailMessageRequest.getCategories()))
            mParamObject.add("categories", oCreateEmailMessageRequest.getCategories());

        log.info("RESULT osURL: {}", osURL.toString());
        log.info("RESULT mParamObject: {}", mParamObject);

        UniResponse oUniResponse = sendRequest(mParamObject, osURL.toString(), mParamByteArray);

        log.info("RESULT oUniResponse: {}", oUniResponse);

        return oUniResponse;
    }

    public UniResponse createCampaign(CreateCampaignRequest createCampaignRequest) {

        MultiValueMap<String, Object> mParam = new LinkedMultiValueMap<String, Object>();

        //mandatory part
        StringBuilder osURL = new StringBuilder(this.resultUrl);
        osURL.append(CREATE_CAMPAIGN_URI);
        mParam.add("format", "json");
        mParam.add("api_key", apiKey);
        mParam.add("message_id", createCampaignRequest.getMessageId());

        log.info("RESULT osURL: {}", osURL.toString());
        log.info("RESULT mParam: {}", mParam);

        UniResponse oUniResponse = sendRequest(mParam, osURL.toString(), null);

        log.info("RESULT oUniResponse: {}", oUniResponse);

        return oUniResponse;
    }
    
    private UniResponse sendRequest(MultiValueMap<String, Object> parametersMap, String resultUrl,
            MultiValueMap<String, ByteArrayResource> parametersFiles) {

        StringHttpMessageConverter stringConverter = new StringHttpMessageConverter();
        HttpMessageConverter<Resource> resource = new ResourceHttpMessageConverter();
        FormHttpMessageConverter formHttpMessageConverter = new FormHttpMessageConverter();
        formHttpMessageConverter.addPartConverter(resource);

        RestTemplate restTemplate = new RestTemplate(
                Arrays.asList(stringConverter, resource, formHttpMessageConverter));
        //restTemplate.getMessageConverters().add(0, new StringHttpMessageConverter(Charset.forName("UTF-8"))); //._HeaderItem("charset", "utf-8")
        //let's construct main HTTP entity
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(MediaType.MULTIPART_FORM_DATA);
        httpHeaders.setAcceptCharset(Arrays.asList(new Charset[] { StandardCharsets.UTF_8 }));

        //Let's construct attachemnts HTTP entities
        if (parametersFiles != null) {
            Iterator<String> iterator = parametersFiles.keySet().iterator();
            for (int i = 0; iterator.hasNext(); i++) {
                String fileName = iterator.next();
                HttpHeaders partHeaders = new HttpHeaders();
                partHeaders.setContentType(new MediaType("application", "octet-stream", StandardCharsets.UTF_8));
                //headers.add("Content-type","application/octet-stream;charset=utf-8");
                //partHeaders.setContentType(MediaType.APPLICATION_OCTET_STREAM);
                List<ByteArrayResource> bars = parametersFiles.get(fileName);
                HttpEntity<ByteArrayResource> bytesPart = new HttpEntity<ByteArrayResource>(bars.get(0), partHeaders); //HttpEntity<ByteArrayResource> bytesPart = new HttpEntity<ByteArrayResource>(bars.get(i), partHeaders);
                parametersMap.add(fileName, bytesPart);
            }
        }
        //result HTTP Request httpEntity
        HttpEntity httpEntity = new HttpEntity(parametersMap, httpHeaders);
        ResponseEntity<String> jsonResponse = restTemplate.postForEntity(resultUrl, httpEntity, String.class);
        log.info("url == {}, result JSON response : {}", resultUrl, jsonResponse);
        return getUniResponse(jsonResponse.getBody());
    }

    private UniResponse getUniResponse(String response) {
        Map<String, Object> resultMapFromJson = (Map<String, Object>) JSON.parse(response);
        Object error = resultMapFromJson.get("error");

        Map<String, String> errorMap = new HashMap<>();
        Map<String, Object> result = Collections.emptyMap();
        List<String> resultWarnings = new ArrayList<>();

        if (error != null) {
            errorMap.put("error", error.toString());
            errorMap.put("code", resultMapFromJson.get("code").toString());
        } else {
            result = (Map<String, Object>) resultMapFromJson.get("result");
            if (result == null) {
                result = Collections.emptyMap();
            }

            List<Map<String, String>> warnings = (List<Map<String, String>>) resultMapFromJson.get("warnings");
            if (warnings != null) {
                for (Map<String, String> warning : warnings) {
                    if (warning != null && !warning.isEmpty()) {
                        resultWarnings.add(warning.get(warning.keySet().iterator().next()));
                    }
                }
            }
        }
        return new UniResponse(result, resultWarnings, errorMap);
    }

    private String getFormattedDate(Date requestTime) {
        if (requestTime == null)
            return StringUtils.EMPTY;

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(requestTime);
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        return dateFormat.format(calendar.getTime());
    }
}

