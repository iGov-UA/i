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
		Object mapPk = ((Map)dataObj).get("pk");
		if (mapPk != null){
			documentId = ((Map<String,Integer>)mapPk).get("id") + ":" + ((Map<String,Integer>)mapPk).get("year");
		}
		Object state = ((Map)dataObj).get("state");
		if (state != null){
			documentId = String.valueOf(((Map<String,Integer>)state).get("current"));
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
