package org.igov.io.mail.unisender;

import java.util.List;
import java.util.Map;

/**
 * Created by Dmytro Tsapko on 11/28/2015.
 */
public class UniResponse {

    private Map<String, Object> result;
    private List<String> warnings;
    /**
     * this map should has two k/v pairs with keys: error and code.
     */
    private Map<String, String> error;

    public Map<String, Object> getResult() {
        return result;
    }

    public List<String> getWarnings() {
        return warnings;
    }

    public Map<String, String> getError() {
        return error;
    }

    public UniResponse(Map<String, Object> result, List<String> warnings, Map<String, String> error) {
        this.result = result;
        this.warnings = warnings;
        this.error = error;
    }

    @Override
    public String toString() {
        return "UniResponse{" +
                "result=" + result +
                ", warnings=" + warnings +
                ", error=" + error +
                '}';
    }
}
