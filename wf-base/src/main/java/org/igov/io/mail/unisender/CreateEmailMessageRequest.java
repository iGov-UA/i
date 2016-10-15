package org.igov.io.mail.unisender;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.core.io.ByteArrayResource;

import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.activation.URLDataSource;
import java.io.*;
import java.net.URL;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Dmytro Tsapko on 11/30/2015.
 */
public class CreateEmailMessageRequest {
    private CreateEmailMessageRequest() {
    }

    private String apiKey;
    private String language;

    private String senderName;
    private String senderEmail;
    private String subject;
    private String body;
    private String listId;
    private String textBody;
    private int generateText = 0; //default value the same as onUniSender API
    private String tag;
    private Map<String, ByteArrayResource> attachments = new HashMap<>();
    private String lang;
    private String seriesDay;
    private String seriesTime;
    private String wrapType;
    private String categories;

    public String getSenderName() {
        return senderName;
    }

    public String getSenderEmail() {
        return senderEmail;
    }

    public String getSubject() {
        return subject;
    }

    public String getBody() {
        return body;
    }

    public String getListId() {
        return listId;
    }

    public String getTextBody() {
        return textBody;
    }

    public int getGenerateText() {
        return generateText;
    }

    public String getTag() {
        return tag;
    }

    public Map<String, ByteArrayResource> getAttachments() {
        return attachments;
    }

    public String getLang() {
        return lang;
    }

    public String getSeriesDay() {
        return seriesDay;
    }

    public String getSeriesTime() {
        return seriesTime;
    }

    public String getWrapType() {
        return wrapType;
    }

    public String getCategories() {
        return categories;
    }

    public static Builder getBuilder(String apiKey, String language) {
        return new CreateEmailMessageRequest().new Builder(apiKey, language);
    }

    public class Builder {

        private Builder(String apiKey, String language) {
            CreateEmailMessageRequest.this.apiKey = apiKey;
            CreateEmailMessageRequest.this.language = language;
        }

        public Builder setSenderName(String senderName) {
            CreateEmailMessageRequest.this.senderName = senderName;
            return this;
        }

        public Builder setSenderEmail(String senderEmail) {
            CreateEmailMessageRequest.this.senderEmail = senderEmail;
            return this;
        }

        public Builder setSubject(String subject) {
            CreateEmailMessageRequest.this.subject = subject;
            return this;
        }

        public Builder setBody(String body) {
            CreateEmailMessageRequest.this.body = body;
            return this;
        }

        public Builder setListId(String listId) {
            CreateEmailMessageRequest.this.listId = listId;
            return this;
        }

        public Builder setTextBody(String textBody) {
            CreateEmailMessageRequest.this.textBody = textBody;
            return this;
        }

        public Builder setGenerateText(int generateText) {
            CreateEmailMessageRequest.this.generateText = generateText;
            return this;
        }

        public Builder setTag(String tag) {
            CreateEmailMessageRequest.this.tag = tag;
            return this;
        }

        public Builder setAttachment(String attachmentName, ByteArrayResource attachmentContent) {
            CreateEmailMessageRequest.this.attachments.put(attachmentName, attachmentContent);
            return this;
        }

        public Builder setAttachment(String attachmentName, String attachmentContent) {
            ByteArrayResource bar = new ByteArrayResource(attachmentContent.getBytes(Charset.forName("UTF-8")));
            CreateEmailMessageRequest.this.attachments.put(attachmentName, bar);
            return this;
        }

        public Builder setAttachment(String attachmentName, URL url) throws IOException {

            DataSource oDataSource = new URLDataSource(url);
            BufferedInputStream is = new BufferedInputStream(oDataSource.getInputStream()); //buffered reading increase speed
            byte[] array = IOUtils.toByteArray(is);

            ByteArrayResource bar = new ByteArrayResource(array);

            CreateEmailMessageRequest.this.attachments.put(attachmentName, bar);
            return this;
        }


        public Builder setAttachment(String attachmentName, DataSource dataSource) throws IOException {

            BufferedInputStream is = new BufferedInputStream(dataSource.getInputStream()); //buffered reading increase speed
            byte[] array = IOUtils.toByteArray(is);

            ByteArrayResource bar = new ByteArrayResource(array);

            CreateEmailMessageRequest.this.attachments.put(attachmentName, bar);
            return this;
        }

        public Builder setAttachment(String attachmentName, InputStream inputStream) throws IOException {

            BufferedInputStream is = new BufferedInputStream(inputStream); //buffered reading increase speed
            byte[] array = IOUtils.toByteArray(is);

            ByteArrayResource bar = new ByteArrayResource(array);

            CreateEmailMessageRequest.this.attachments.put(attachmentName, bar);
            return this;
        }

        public Builder setAttachment(String attachmentName, File file) throws IOException {
            return setAttachment(attachmentName, new FileDataSource(file));
        }





        public Builder setLang(String lang) {
            CreateEmailMessageRequest.this.lang = lang;
            return this;
        }

        public Builder setSeriesDay(String seriesDay) {
            CreateEmailMessageRequest.this.seriesDay = seriesDay;
            return this;
        }

        public Builder setSeriesTime(String seriesTime) {
            CreateEmailMessageRequest.this.seriesTime = seriesTime;
            return this;
        }

        public Builder setWrapType(String wrapType) {
            CreateEmailMessageRequest.this.wrapType = wrapType;
            return this;
        }

        public Builder setCategories(String categories) {
            CreateEmailMessageRequest.this.categories = categories;
            return this;
        }

        public CreateEmailMessageRequest build() {

            if(StringUtils.isBlank(getSenderName()))
                throw new IllegalArgumentException("Sender name can't be blank.");
            if(StringUtils.isBlank(getSenderEmail()))
                throw new IllegalArgumentException("Sender email can't be blank.");
            if(StringUtils.isBlank(getSubject()))
                throw new IllegalArgumentException("Subject can't be blank.");
            if(StringUtils.isBlank(getBody()))
                throw new IllegalArgumentException("Body can't be empty.");
            if(StringUtils.isBlank(getListId()))
                throw new IllegalArgumentException("list_id can't be empty.");

            return CreateEmailMessageRequest.this;
        }
    }
}
