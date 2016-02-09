package org.igov.service.business.action.task.systemtask.doc.handler;

import org.activiti.engine.impl.util.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class UkrDocEventHandler {

	private static final Logger LOG = LoggerFactory.getLogger(UkrDocEventHandler.class);
	
	private String status;
	private String documentId;
	private String year;
	private String pkDocumentId;
	
	public void processEvent(String event){
		JSONObject eventJson = new JSONObject(event);
		Object dataObj = eventJson.get("data");

		Object docStateEvent = ((JSONObject)dataObj).get("docStateEvent"); 
		
		Object tables = ((JSONObject)docStateEvent).get("tables");
		if (tables != null){
			Object cardsDocument = ((JSONObject)tables).get("CardsDocument");
			if (cardsDocument != null){
				documentId = String.valueOf(((JSONObject)cardsDocument).get("CarIdDocument"));
			}
		}
		Object state = ((JSONObject)docStateEvent).get("state");
		if (state != null){
			status = String.valueOf(((JSONObject)state).get("current"));
		}
		
		Object pk = ((JSONObject)docStateEvent).get("pk");
		if (pk != null){
			year = String.valueOf(((JSONObject)pk).get("year"));
			pkDocumentId = String.valueOf(((JSONObject)pk).get("id"));
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

}
