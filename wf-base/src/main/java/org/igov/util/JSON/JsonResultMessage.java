package org.igov.util.JSON;

import java.io.Serializable;

public class JsonResultMessage implements Serializable {

    private static final long serialVersionUID = -1200531096759544234L;

    private String code;
    private String message;

    public JsonResultMessage(String code, String message) {
        this.code = code;
        this.message = message;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}