package org.igov.service.business.msg;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.PostConstruct;

import org.igov.io.GeneralConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.pb.ksv.msgcore.data.IMsgObjR;
import java.util.LinkedList;
import java.util.List;
import org.igov.util.JSON.JsonRestUtils;

/**
 * 
 * @author kr110666kai & BW
 *
 * Сервис для отсылки сообщений в Сервис Хранения Ошибок http://msg.igov.org.ua/MSG
 */
@Service
public class MsgService {
    private static final Logger LOG = LoggerFactory.getLogger(MsgService.class);

    @Autowired
    GeneralConfig generalConfig;

    private static String sBusinessId_MSG;
    private static String sTemplateId_MSG;
    private static String sLogin_MSG;
    private static String sURL_MSG;
    private static Integer nID_Server;

    // Признак готовности сервиса отсылать сообщения
    private static boolean isReadySendMSG = false;

    @PostConstruct
    public void init() throws Exception {
	sBusinessId_MSG = generalConfig.getBusinessId_MSG_Monitor();
	sTemplateId_MSG = generalConfig.getTemplateId_MSG_Monitor();
	sLogin_MSG = generalConfig.getLogin_MSG_Monitor();
	sURL_MSG = generalConfig.getURL_MSG_Monitor();
	nID_Server = generalConfig.getSelfServerId();
	LOG.debug("ServiceURL={}, BusID={}, sTemplateId_MSG={}, sMsgLogin={}, nID_Server={}"
                , sURL_MSG, sBusinessId_MSG, sTemplateId_MSG, sLogin_MSG, nID_Server);	
	if (sBusinessId_MSG.startsWith("${") || sTemplateId_MSG.startsWith("${") || sLogin_MSG.startsWith("${") || sURL_MSG.startsWith("${")) {
	    LOG.warn("Сервис не готов к работе. Не заданы необходимые параметры");
	    return;
	}
	System.setProperty("MsgURL", sURL_MSG);
	LOG.info("Сервис готов к работе.");
	isReadySendMSG = true;
    }

    public static <T> IMsgObjR send(String sType, Long nID_Subject, Long nID_Server_Custom
            , String sFunction, String sHead, String sBody, String sError, Map<String, T> mParam) {
        return send(sType, nID_Subject, nID_Server_Custom, sFunction, sHead, sBody, sError, mParam, null);
    }
    public static <T> IMsgObjR send(String sType, Long nID_Subject, Long nID_Server_Custom
            , String sFunction, String sHead, String sBody, String sError, Map<String, T> mParam, String smData) {
	if (!isReadySendMSG) {
	    LOG.warn("Сервис не готов к отсылке сообщений.");
	    return null;
	}
        if(smData!=null){
            if(mParam!=null){
                mParam=new HashMap();
            }
            mParam.put("smData", (T) asParam(smData));
        }
	IMsgObjR oIMsgObjR = null;
	try {
	    oIMsgObjR = new MsgSendImpl(sURL_MSG, sBusinessId_MSG, sType, sFunction, sTemplateId_MSG, sLogin_MSG)
                ._Head(sHead)
                ._Body(sBody)
                ._Error(sError)
		._ServerID(nID_Server_Custom!=null?nID_Server_Custom:nID_Server)
                ._SubjectID(nID_Subject)
		._Params(mParam)
                    
                .save()
            ;
	} catch (Exception oException) {
	    LOG.warn("Can't send an error message to service MSG\n", oException);
	}
	return oIMsgObjR;
    }
    
    
    @SuppressWarnings("unchecked")
    /**
     * Разбирается структура smData с дополнительными данными по ошибке.
     * Соответствующие поля структуры запоминаются в отделных переменных, для
     * последующего сохранения в атрибутах Сервиса Хранения Ошибок
     * 
     * @param smData - JSON структура следующего формата:
     *	{
     *    "asParam": [
     *      "par1",
     *      "par2",
     *      "par3"
     *    ],
     *    "oResponse": {
     *      "sMessage": "value sMessage",
     *      "sCode": "value sCode",
     *      "soData": "value soData"
     *    },
     *    "sDate": "value sDate"
     *  }            
     */
    
    //public IMsgSend _Data(String smDataJSON) {
    //public Map<String, Map> mClient(String smDataJSON) {
    static public List<String> asParam(String smDataJSON) {//, List<String> asParam
        /*if(asParam==null){
            asParam = new LinkedList<String>();
        }
        List<String> asParamReturn = new LinkedList<>(asParam);*/
        List<String> asParamReturn = new LinkedList<>();
        LOG.debug("smData={}", smDataJSON);
	smDataJSON = smDataJSON.trim();
	if (smDataJSON == null || smDataJSON.isEmpty()) {
	    //LOG.debug("smData is Empty");
	    return asParamReturn;
	//}else{
        //    LOG.debug("smData=[{}]", smData);
        }
        
	String sResponseMessage = null;
	String sResponseCode = null;
	String soResponseData = null;
        String smDataMiscReturn = null;
	StringBuilder smDataMisc = new StringBuilder();
	if (smDataJSON != null) {
	    asParamReturn = new LinkedList<String>();
	    Map<String, Object> moData = null;
	    try {
		moData = JsonRestUtils.readObject(smDataJSON, Map.class);
	    } catch (Exception oException) {
		smDataMiscReturn = "Error parse JSON smData: "+oException.getMessage();
	    }

	    if (moData != null) {
		if (moData.containsKey("asParam")) {
		    asParamReturn = (List<String>) moData.get("asParam");
		}
		if (moData.containsKey("oResponse")) {
		    Map<String, Object> moResponse = (Map<String, Object>) moData.get("oResponse");
		    if (moResponse != null) {
			if (moResponse.containsKey("sMessage")) {
			    sResponseMessage = (String) moResponse.get("sMessage");
			    smDataMisc.append(sResponseMessage);
			    smDataMisc.append(" ");
			}
			if (moResponse.containsKey("sCode")) {
			    sResponseCode = (String) moResponse.get("sCode");
			    smDataMisc.append(sResponseCode);
			    smDataMisc.append(" ");
			}
			if (moResponse.containsKey("soData")) {
			    soResponseData = (String) moResponse.get("soData");
			    smDataMisc.append(soResponseData);
			    smDataMisc.append(" ");
			}
			smDataMiscReturn = smDataMisc.toString();
		    }
		}
		if (moData.containsKey("sDate")) {
		    String sDate = (String) moData.get("sDate");
                    asParamReturn.add("sDate: " + sDate);
		}
	    }
            //asParam.add(smDataMiscReturn);
            if(smDataMiscReturn!=null){
                asParamReturn.add("smDataMisc: " + smDataMiscReturn);
            }
	}
	//return this;
        return asParamReturn;
    }

    /*public static IMsgObjR setEventSystem(String sType, Long nID_Subject, Long nID_Server_Custom, String sFunction
            , String sHead, String sBody, String sError, String smData) {
	if (!isReadySendMSG) {
	    LOG.warn("Сервис не готов к отсылке сообщений.");
	    return null;
	}

	IMsgObjR oIMsgObjR = null;
	try {
	    oIMsgObjR = new MsgSendImpl(sURL_MSG, sBusinessId_MSG, sType, sFunction, sTemplateId_MSG, sLogin_MSG)
                ._Head(sHead)
                ._Body(sBody)
                ._Error(sError)
		._ServerID(nID_Server_Custom!=null?nID_Server_Custom:nID_Server)
                ._SubjectID(nID_Subject)
		._Data(smData)
                .save()
            ;
	} catch (Exception oException) {
	    LOG.warn("Can't send an error message to service MSG\n", oException);
	}
	return oIMsgObjR;
    }*/    
    
    /*public static <T> IMsgObjR setEventSystemWithParam(String sType, Long nID_Subject, Long nID_Server_Custom
            , String sFunction, String sHead, String sBody, String sError, HashMap<String, T> mParam) {
        
	if (!isReadySendMSG) {
	    LOG.warn("Сервис не готов к отсылке сообщений.");
	    return null;
	}

	IMsgObjR oIMsgObjR = null;
	try {
	    oIMsgObjR = new MsgSendImpl(sURL_MSG, sBusinessId_MSG, sType, sFunction, sTemplateId_MSG, sLogin_MSG)
                ._Head(sHead)
                ._Body(sBody)
                ._Error(sError)
		._ServerID(nID_Server_Custom!=null?nID_Server_Custom:nID_Server)
                ._SubjectID(nID_Subject)
		._Params(mParam)
                .save()
            ;
	} catch (Exception oException) {
	    LOG.error("Can't send an error message to service MSG\n", oException);
	}
	return oIMsgObjR;
    }*/
}
