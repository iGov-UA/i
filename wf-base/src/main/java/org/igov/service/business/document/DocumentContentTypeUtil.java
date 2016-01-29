package org.igov.service.business.document;

import org.igov.io.GeneralConfig;
import java.util.HashMap;
import java.util.Map;

import org.activiti.engine.impl.util.json.JSONArray;
import org.activiti.engine.impl.util.json.JSONObject;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.igov.io.web.HttpRequester;

public class DocumentContentTypeUtil {
	
	private static Map<String, String> documentContentTypesIdByName = new HashMap<String, String>();
	private static final Logger LOG = LoggerFactory.getLogger(DocumentContentTypeUtil.class);
	
	public static String getDocumentContentTypeIdByName(String typeName){
		synchronized (documentContentTypesIdByName){
			String cutTypeName = StringUtils.substringBefore(typeName, ";");
			if (!documentContentTypesIdByName.keySet().contains(cutTypeName)){
				LOG.info("document map doesn't contain value for the key {}", cutTypeName);
				return "";
			} 
			return documentContentTypesIdByName.get(cutTypeName);
		}
	}

	public static void init(GeneralConfig generalConfig, HttpRequester httpRequester) {
		synchronized (documentContentTypesIdByName){
			if (documentContentTypesIdByName.isEmpty()){
				String URI = "/wf/service/document/getDocumentContentTypes";
				LOG.info("Getting URL: {}", generalConfig.sHostCentral() + URI);
				try {
					String soJSON_DocumentTypes = httpRequester.getInside(generalConfig.sHostCentral() + URI, new HashMap<String, String>());
			        LOG.info("Received answer: {}", soJSON_DocumentTypes);
			        
			        JSONArray jsonArray = new JSONArray(soJSON_DocumentTypes);
			        for (int i = 0; i < jsonArray.length(); i++) {
			            JSONObject record = jsonArray.getJSONObject(i);
			            documentContentTypesIdByName.put(record.getString("sName"), record.getString("nID"));
			        }
			        LOG.info("Loaded map: {}", documentContentTypesIdByName);
				} catch (Exception e) {
					LOG.error("Error: {}, occured while loading list of document content types: ", e.getMessage());
					LOG.debug("FAIL:", e);
				}
			}
		}
            
	}
}
