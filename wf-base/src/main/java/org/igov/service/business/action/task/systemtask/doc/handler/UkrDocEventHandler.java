package org.igov.service.business.action.task.systemtask.doc.handler;

import org.activiti.engine.impl.util.json.JSONArray;
import org.activiti.engine.impl.util.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class UkrDocEventHandler {

    private static final Logger LOG = LoggerFactory.getLogger(UkrDocEventHandler.class);

    private String status;
    private String documentId;
    private String year;
    private String pkDocumentId;
    private String nID_DocumentTemplate;
    private boolean bFile;

    public void processEvent(String event) {
        JSONObject eventJson = new JSONObject(event);

        Object dataObj = eventJson.get("data");

        Object docStateEvent = ((JSONObject) dataObj).get("docStateEvent");

        Object tables = ((JSONObject) docStateEvent).get("tables");
        if (tables != null) {
            Object cardsDocument = ((JSONObject) tables).get("CardsDocument");
            if (cardsDocument != null) {
                documentId = String.valueOf(((JSONObject) cardsDocument).get("CarIdDocument"));
                nID_DocumentTemplate = String.valueOf(((JSONObject) cardsDocument).get("IdXMLT"));
            }
        }

        Object extensions = ((JSONObject) docStateEvent).get("extensions");
        if (extensions != null) {
            Object files = ((JSONObject) extensions).get("files");
            if (files != null && ((JSONArray) files).length() > 0) {
                bFile = true;
            } else {
                bFile = false;
            }
        }

        Object state = ((JSONObject) docStateEvent).get("state");
        if (state != null) {
            status = String.valueOf(((JSONObject) state).get("current"));
        }

        Object pk = ((JSONObject) docStateEvent).get("pk");
        if (pk != null) {
            year = String.valueOf(((JSONObject) pk).get("year"));
            pkDocumentId = String.valueOf(((JSONObject) pk).get("id"));
        }

        LOG.info("Status {} document {} year {} pkDocumentId {}", status, documentId, year, pkDocumentId);
    }

    public String getStatus() {
        return status;
    }

    public String getDocumentId() {
        return documentId;
    }

    public String getYear() {
        return year;
    }

    public String getPkDocumentId() {
        return pkDocumentId;
    }

    public String getnID_DocumentTemplate() {
        return nID_DocumentTemplate;
    }

    public boolean isbFile() {
        return bFile;
    }
}
