package org.wf.dp.dniprorada.util;

import java.util.HashMap;
import java.util.Map;

import org.activiti.engine.impl.util.json.JSONArray;
import org.activiti.engine.impl.util.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.wf.dp.dniprorada.rest.HttpRequester;

public class DocumentTypeUtil {
	
	private static Map<String, String> documentTypesIdByName = new HashMap<String, String>();
	private static final Logger LOG = LoggerFactory.getLogger(DocumentTypeUtil.class);
	
	public static String getDocumentTypeIdByName(String typeName){
		synchronized (documentTypesIdByName){
			if (!documentTypesIdByName.keySet().contains(typeName)){
				LOG.info("document map doesn't contain value for the key " + typeName);
				return "";
			} 
			return documentTypesIdByName.get(typeName);
		}
	}

	public static void init(GeneralConfig generalConfig, HttpRequester httpRequester) {
		synchronized (documentTypesIdByName){
			if (documentTypesIdByName.isEmpty()){
				String URI = "/wf/service/services/getDocumentTypes";
				LOG.info("Getting URL: " + generalConfig.sHostCentral() + URI);
				try {
					String soJSON_DocumentTypes = httpRequester.get(generalConfig.sHostCentral() + URI, new HashMap<String, String>());
			        LOG.info("Received answer: " + soJSON_DocumentTypes);
			        
			        JSONArray jsonArray = new JSONArray(soJSON_DocumentTypes);
			        for (int i = 0; i < jsonArray.length(); i++) {
			            JSONObject record = jsonArray.getJSONObject(i);
			            documentTypesIdByName.put(record.getString("sName"), record.getString("nID"));
			        }
				} catch (Exception e) {
					LOG.info("Error occured while loading list of document types: " + e.getMessage(), e);
				}
			}
		}
            
	}
}
