package org.igov.service.business.msg;

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

    private String sBusId;
    private String sTemplateMsgId;
    private String sMsgLogin;
    private String sServiceURL;

    @PostConstruct
    public void initIt() throws Exception {
	sBusId = generalConfig.getBusinessId_MSG_Monitor();
	sTemplateMsgId = generalConfig.getTemplateId_MSG_Monitor();
	sMsgLogin = generalConfig.getLogin_MSG_Monitor();

	sServiceURL = generalConfig.getURL_MSG_Monitor();
	System.setProperty("MsgURL", sServiceURL);

	LOG.debug("ServiceURL={}, BusID={}, sTemplateMsgId={}, sMsgLogin={}", sServiceURL, sBusId, sTemplateMsgId,
		sMsgLogin);
    }

    public IMsgObjR setEventSystem(String sType, Long nID_Subject, Long nID_Server, String sFunction, String sHead,
	    String sBody, String sError, String smData) throws Exception {

	IMsgObjR msg = new MsgSendImpl(sServiceURL, sBusId, sType, sFunction, sTemplateMsgId, sMsgLogin)
		.addnID_Server(nID_Server).addnID_Subject(nID_Subject).addsBody(sBody).addsError(sError).addsHead(sHead)
		.addsmData(smData).save();

	return msg;
    }

}
