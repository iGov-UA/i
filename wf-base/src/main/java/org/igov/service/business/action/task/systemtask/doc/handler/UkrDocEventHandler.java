package org.igov.service.business.action.task.systemtask.doc.handler;

import java.util.Map;

import org.activiti.engine.impl.util.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class UkrDocEventHandler {

	private static final Logger LOG = LoggerFactory.getLogger(UkrDocEventHandler.class);
	
	private String status;
	private String documentId;
	
	public void processEvent(String event){
		JSONObject eventJson = new JSONObject(event);
		Object dataObj = eventJson.get("data");

		LOG.info("data element: " + dataObj);
		Object mapPk = ((JSONObject)dataObj).get("pk");
		if (mapPk != null){
			documentId = ((JSONObject)mapPk).get("id") + ":" + ((JSONObject)mapPk).get("year");
		}
		Object state = ((JSONObject)dataObj).get("state");
		if (state != null){
			documentId = String.valueOf(((JSONObject)state).get("current"));
		}
		LOG.info("Status {} document {}", status, documentId);
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getDocumentId() {
		return documentId;
	}

	public void setDocumentId(String documentId) {
		this.documentId = documentId;
	}
	
}
