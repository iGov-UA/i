package org.igov.io.mail.unisender;

import org.apache.commons.lang3.StringUtils;

import java.util.Date;
import java.util.List;

/**
 * Created by Dmytro Tsapko on 11/30/2015.
 */
public class SubscribeRequest {
    private String apiKey;
    private String lang;

    private List<String> listIds;
    private String email;
    private String phone;
    private List<String> tags;
    private String requestIp;
    private Date requestTime;
    private int doubleOptin;
    private String confirmIp;
    private Date confirmTime;
    private int overwrite;

    public List<String> getListIds() {
        return listIds;
    }

    public String getEmail() {
        return email;
    }

    public String getPhone() {
        return phone;
    }

    public List<String> getTags() {
        return tags;
    }

    public String getRequestIp() {
        return requestIp;
    }

    public Date getRequestTime() {
        return requestTime;
    }

    public int getDoubleOptin() {
        return doubleOptin;
    }

    public String getConfirmIp() {
        return confirmIp;
    }

    public Date getConfirmTime() {
        return confirmTime;
    }

    public int getOverwrite() {
        return overwrite;
    }

    public static Builder getBuilder(String apiKey, String lang) {
        return new SubscribeRequest().new Builder(apiKey, lang);
    }

    public class Builder {

        private Builder(String apiKey, String lang) {
            SubscribeRequest.this.apiKey = apiKey;
            SubscribeRequest.this.lang = lang;
        }

        public Builder setListIds(List<String> listIds) {
            SubscribeRequest.this.listIds = listIds;
            return this;
        }

        public Builder setEmail(String email) {
            SubscribeRequest.this.email = email;
            return this;
        }

        public Builder setPhone(String phone) {
            SubscribeRequest.this.phone = phone;
            return this;
        }

        public Builder setTags(List<String> tags) {
            SubscribeRequest.this.tags = tags;
            return this;
        }

        public Builder setRequestIp(String requestIp) {
            SubscribeRequest.this.requestIp = requestIp;
            return this;
        }

        public Builder setRequestTime(Date requestTime) {
            SubscribeRequest.this.requestTime = requestTime;
            return this;
        }

        public Builder setDoubleOptin(int doubleOptin) {
            SubscribeRequest.this.doubleOptin = doubleOptin;
            return this;
        }

        public Builder setConfirmIpt(String confirmIp) {
            SubscribeRequest.this.confirmIp = confirmIp;
            return this;
        }

        public Builder setConfirmTime(Date confirmTime) {
            SubscribeRequest.this.confirmTime = confirmTime;
            return this;
        }

        public Builder setOverwrite(int overwrite) {
            SubscribeRequest.this.overwrite = overwrite;
            return this;
        }

        public SubscribeRequest build() {

            if(getListIds() == null || getListIds().isEmpty())
                throw new IllegalArgumentException("Mailing List ID can't be empty");
            if(StringUtils.isBlank(getEmail()) && StringUtils.isBlank(getPhone()))
                throw new IllegalArgumentException("Email and Phone are empty. At least one argument can't be empty.");
            if (getDoubleOptin() < 0 || getDoubleOptin() > 3)
                throw new IllegalArgumentException("doubleOptin must be in range [0-3]");
            if (getOverwrite() < 0 || getOverwrite() > 3)
                throw new IllegalArgumentException("overwrite must be in range [0-3]");

            return SubscribeRequest.this;
        }
    }
}
