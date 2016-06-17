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

/**
 * 
 * @author kr110666kai
 *
 * Сервис для отсылки сообщений в Сервис Хранения Ошибок http://msg.igov.org.ua/MSG
 */
@Service
public class MsgService {
    private static final Logger LOG = LoggerFactory.getLogger(MsgService.class);

    @Autowired
    GeneralConfig generalConfig;

    private static String sBusId;
    private static String sTemplateMsgId;
    private static String sMsgLogin;
    private static String sServiceURL;

    // Признак готовности сервиса отсылать сообщения
    private static boolean isReadySendMSG = false;

    @PostConstruct
    public void init() throws Exception {
	sBusId = generalConfig.getBusinessId_MSG_Monitor();
	sTemplateMsgId = generalConfig.getTemplateId_MSG_Monitor();
	sMsgLogin = generalConfig.getLogin_MSG_Monitor();
	sServiceURL = generalConfig.getURL_MSG_Monitor();

	LOG.debug("ServiceURL={}, BusID={}, sTemplateMsgId={}, sMsgLogin={}", sServiceURL, sBusId, sTemplateMsgId,
		sMsgLogin);	
	
	if (sBusId.startsWith("${") || sTemplateMsgId.startsWith("${") || sMsgLogin.startsWith("${") || sServiceURL.startsWith("${")) {
	    LOG.warn("Сервис не готов к работе. Не заданы необходимые параметры");
	    return;
	}
	
	System.setProperty("MsgURL", sServiceURL);

	LOG.info("Сервис готов к работе.");
	isReadySendMSG = true;
    }

    public static IMsgObjR setEventSystem(String sType, Long nID_Subject, Long nID_Server, String sFunction, String sHead,
	    String sBody, String sError, String smData) {
	if (!isReadySendMSG) {
	    LOG.warn("Сервис не готов к отсылке сообщений.");
	    return null;
	}

	IMsgObjR msg = null;
	try {
	    msg = new MsgSendImpl(sServiceURL, sBusId, sType, sFunction, sTemplateMsgId, sMsgLogin)
		.addnID_Server(nID_Server).addnID_Subject(nID_Subject).addsBody(sBody).addsError(sError).addsHead(sHead)
		.addsmData(smData).save();
	} catch (Exception e) {
	    LOG.warn("Cann't send an error message to service MSG\n", e);
	}

	return msg;
    }

    public static <T> IMsgObjR setEventSystemWithParam(String sType, Long nID_Subject, Long nID_Server, String sFunction, String sHead,
	    String sBody, String sError, HashMap<String, T> mParam) {
	if (!isReadySendMSG) {
	    LOG.warn("Сервис не готов к отсылке сообщений.");
	    return null;
	}

	IMsgObjR msg = null;
	try {
	    msg = new MsgSendImpl(sServiceURL, sBusId, sType, sFunction, sTemplateMsgId, sMsgLogin)
		.addnID_Server(nID_Server).addnID_Subject(nID_Subject).addsBody(sBody).addsError(sError).addsHead(sHead)
		.addasParam(mParam).save();
	} catch (Exception e) {
	    LOG.warn("Cann't send an error message to service MSG\n", e);
	}

	return msg;
    }

    public static <T> IMsgObjR setEventSystemWithParam(String sType, Long nID_Subject, Long nID_Server, String sFunction, String sHead,
	    String sBody, String sError, Map<String, T> mParam) {
	if (!isReadySendMSG) {
	    LOG.warn("Сервис не готов к отсылке сообщений.");
	    return null;
	}

	IMsgObjR msg = null;
	try {
	    msg = new MsgSendImpl(sServiceURL, sBusId, sType, sFunction, sTemplateMsgId, sMsgLogin)
		.addnID_Server(nID_Server).addnID_Subject(nID_Subject).addsBody(sBody).addsError(sError).addsHead(sHead)
		.addasParam(mParam).save();
	} catch (Exception e) {
	    LOG.warn("Cann't send an error message to service MSG\n", e);
	}

	return msg;
    }
    
}
