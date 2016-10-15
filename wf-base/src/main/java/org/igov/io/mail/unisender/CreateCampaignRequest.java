package org.igov.io.mail.unisender;

import org.apache.commons.lang3.StringUtils;

/**
 * Created by Dmytro Tsapko on 12/1/2015.
 */
public class CreateCampaignRequest {
    private String apiKey;
    private String language;

    private String messageId;

    private CreateCampaignRequest() {
    }

    public String getMessageId() {
        return messageId;
    }

    public static Builder getBuilder(String apiKey, String language){
        return new CreateCampaignRequest().new Builder(apiKey, language);
    }

    public class Builder {
        private Builder(String apiKey, String language) {
            CreateCampaignRequest.this.apiKey = apiKey;
            CreateCampaignRequest.this.language = language;
        }

        public Builder setMessageId(String messageId){
            CreateCampaignRequest.this.messageId = messageId;
            return this;
        }

        public CreateCampaignRequest build(){

            if(StringUtils.isBlank(getMessageId()))
                throw new IllegalArgumentException("Message ID can't be blank.");


            return CreateCampaignRequest.this;
        }
    }
}
