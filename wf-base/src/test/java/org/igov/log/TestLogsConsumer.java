package org.igov.log;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * @author dgroup
 * @since  08.01.2016
 */
class TestLogsConsumer implements Consumer {

    private Map<String, Object[]> logs = new LinkedHashMap<>();

    @Override
    public void consume(String msg, Object... args) {
        logs.put(msg, args);
    }

    public Map<String, Object[]> logs(){
        return logs;
    }

    public String msgFirst() {
        return logs.keySet().iterator().next();
    }

    public String firstLogMessage() {
        return withoutMethodName( logs().keySet().iterator().next() );
    }

    String withoutMethodName(String msg){
        return msg.substring(msg.indexOf(']')+2); // Remove method name from string
    }
}