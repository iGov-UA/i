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

    public UniResponse createEmailMessage(CreateEmailMessageRequest createEmailMessageRequest) {

        MultiValueMap<String, Object> parametersMap = new LinkedMultiValueMap<String, Object>();
        MultiValueMap<String, ByteArrayResource> parametersFiles = new LinkedMultiValueMap<String, ByteArrayResource>();

        //mandatory part
        StringBuilder resultUrl = new StringBuilder(this.resultUrl);
        resultUrl.append(CREATE_EMAIL_MESSAGE_URI);
        parametersMap.add("format", "json");
        parametersMap.add("api_key", apiKey);
        parametersMap.add("sender_name", createEmailMessageRequest.getSenderName());
        parametersMap.add("sender_email", createEmailMessageRequest.getSenderEmail());
        parametersMap.add("subject", createEmailMessageRequest.getSubject());
        
        String sBody = createEmailMessageRequest.getSubject() + " | " +  createEmailMessageRequest.getBody();
        /*log.info("1)sBody(orig)="+sBody);
        try {
            //String utf8String= new String(sBody.getBytes("UTF-8"), "windows-1251");
            String sBody1 = new String(sBody.getBytes("UTF-8"), "windows-1251");
            log.info("1)sBody1="+sBody1);
            String sBody2 = new String(sBody.getBytes("windows-1251"), "UTF-8");
            log.info("1)sBody2="+sBody2);
            String sBody3 = new String(sBody.getBytes(), "UTF-8");
            log.info("1)sBody3="+sBody3);
            String sBody4 = new String(sBody.getBytes(), "windows-1251");
            log.info("1)sBody4="+sBody4);
            sBody=sBody1 + " | " + sBody2 + " | " + sBody3 + " | " + sBody4;
        } catch (UnsupportedEncodingException ex) {
            //java.util.logging.Logger.getLogger(Mail.class.getName()).log(Level.SEVERE, null, ex);
            log.error("1)sBody-convert-encoding", ex);
        }
        log.info("1)sBody(result)="+sBody);*/
        
        
        //parametersMap.add("body", sBody);//createEmailMessageRequest.getBody()
        parametersFiles.add("body", new ByteArrayResource(sBody.getBytes(StandardCharsets.UTF_8)));
        parametersMap.add("list_id", createEmailMessageRequest.getListId());
        //optional
        if (!StringUtils.isBlank(createEmailMessageRequest.getTextBody()))
            parametersMap.add("text_body", createEmailMessageRequest.getTextBody());
        //generate_text has default value == 0
        parametersMap.add("generate_text", Integer.toString(createEmailMessageRequest.getGenerateText()));

        if (!StringUtils.isBlank(createEmailMessageRequest.getTag()))
            parametersMap.add("tag", createEmailMessageRequest.getTag());

        Map<String, ByteArrayResource> attachments = createEmailMessageRequest.getAttachments();
        for (String fileName : attachments.keySet()) {
            ByteArrayResource fileContent = attachments.get(fileName);
            parametersFiles.add("attachments[" + fileName + "]", fileContent);
        }

        if (!StringUtils.isBlank(createEmailMessageRequest.getLang()))
            //parametersMap.add("lang", createEmailMessageRequest.getLang());
            parametersMap.add("lang", "ua");
        if (!StringUtils.isBlank(createEmailMessageRequest.getSeriesDay()))
            parametersMap.add("series_day", createEmailMessageRequest.getSeriesDay());
        if (!StringUtils.isBlank(createEmailMessageRequest.getSeriesTime()))
            parametersMap.add("series_time", createEmailMessageRequest.getSeriesTime());
        if (!StringUtils.isBlank(createEmailMessageRequest.getWrapType()))
            parametersMap.add("wrap_type", createEmailMessageRequest.getWrapType());
        if (!StringUtils.isBlank(createEmailMessageRequest.getCategories()))
            parametersMap.add("categories", createEmailMessageRequest.getCategories());

        log.info("result URL: {}", resultUrl.toString());
        log.info("result Parameters: {}", parametersMap);

        UniResponse uniResponse = sendRequest(parametersMap, resultUrl.toString(), parametersFiles);

        log.info("result uniResponse: {}", uniResponse);

        return uniResponse;
    }

    public UniResponse createCampaign(CreateCampaignRequest createCampaignRequest) {

        MultiValueMap<String, Object> parametersMap = new LinkedMultiValueMap<String, Object>();

        //mandatory part
        StringBuilder resultUrl = new StringBuilder(this.resultUrl);
        resultUrl.append(CREATE_CAMPAIGN_URI);
        parametersMap.add("format", "json");
        parametersMap.add("api_key", apiKey);
        parametersMap.add("message_id", createCampaignRequest.getMessageId());

        log.info("result URL: {}", resultUrl.toString());
        log.info("result Parameters: {}", parametersMap);

        UniResponse uniResponse = sendRequest(parametersMap, resultUrl.toString(), null);

        log.info("result uniResponse: {}", uniResponse);

        return uniResponse;
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
                HttpEntity<ByteArrayResource> bytesPart = new HttpEntity<ByteArrayResource>(bars.get(i), partHeaders);
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

