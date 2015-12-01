package org.wf.dp.dniprorada.util.unisender.requests;

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
    private int generateText;
    private String tag;
    private Map<String, String> attachments = new HashMap<>();
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

    public Map<String, String> getAttachments() {
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

        public Builder setAttachment(String attachmentName, String attachmentContent) {
            CreateEmailMessageRequest.this.attachments.put(attachmentName, attachmentContent);
            return this;
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

        CreateEmailMessageRequest build() {
            return CreateEmailMessageRequest.this;
        }
    }
}
