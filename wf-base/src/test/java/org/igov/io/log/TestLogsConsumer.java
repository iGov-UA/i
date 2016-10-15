package org.igov.io.log;

import java.util.ArrayList;
import java.util.List;

/**
 * @author dgroup
 * @since  08.01.2016
 */
@SuppressWarnings("PMD")
class TestLogsConsumer implements Consumer {

    private final List<String> logs = new ArrayList<>();

    public void consume(String msg) {
        logs.add(msg);
    }


    public List<String> logs(){
        return logs;
    }

    public String firstLogMessage() {
        return logs.iterator().next();
    }
}