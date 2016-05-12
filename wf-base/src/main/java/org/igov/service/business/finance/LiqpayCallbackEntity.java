package org.igov.service.business.finance;

import java.util.HashMap;
import java.util.Map;

public class LiqpayCallbackEntity {
    private String version;
    private String public_key;
    private String amount;
    private String currency;
    private String description;
    private String order_id;
    private String type;
    private String transaction_id;
    private String status;
    private transient String sender_phone;
    private transient String token;
    private static final transient Map<String, String> statuses = new HashMap();
    
    static{
        statuses.put("success", "Успешный платеж");
        statuses.put("failure", "Неуспешный платеж");
        statuses.put("error", "Неуспешный платеж. Некорректно заполнены данные");
        statuses.put("subscribed", "Подписка успешно оформлена");
        statuses.put("unsubscribed", "Подписка успешно деактивирована");
        statuses.put("reversed", "Платеж возвращен");
        statuses.put("sandbox", "Тестовый платеж");
    }
    
    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getPublic_key() {
        return public_key;
    }

    public void setPublic_key(String public_key) {
        this.public_key = public_key;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getOrder_id() {
        return order_id;
    }

    public void setOrder_id(String order_id) {
        this.order_id = order_id;
    }

    public String getTransaction_id() {
        return transaction_id;
    }

    public void setTransaction_id(String transaction_id) {
        this.transaction_id = transaction_id;
    }

    public String getSender_phone() {
        return sender_phone;
    }

    public void setSender_phone(String sender_phone) {
        this.sender_phone = sender_phone;
    }

    public String getStatus() {
        return status;
    }

    public String getStatusDescription() {
        return statuses.containsKey(status) ? statuses.get(status) : status;
    }
    
    public void setStatus(String status) {
        this.status = status;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}
