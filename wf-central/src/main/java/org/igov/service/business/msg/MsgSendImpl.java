package org.igov.service.business.msg;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Timestamp;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.igov.util.JSON.JsonRestUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pb.ksv.msgcore.data.IMsgObjR;
import com.pb.ksv.msgcore.data.MAttrs;
import com.pb.ksv.msgcore.data.MFilter;
import com.pb.ksv.msgcore.data.enums.MsgAttrMode;
import com.pb.ksv.msgcore.data.enums.MsgLevel;
import com.pb.ksv.msgcore.user.Msg;
import com.pb.util.gsv.net.HTTPClient;

/**
 * 
 * @author kr110666kai
 *
 *         Реализация Интерфеса отсылки сообщений в Сервис Хранения Ошибок
 *         http://msg.igov.org.ua
 * 
 *         Пример использования:
 * 
 *         IMsgObjR msg = new MsgSendImpl(sType, sFunction).addnID_Server(nID_Server).
 *                addnID_Subject(nID_Server).addsBody(sBody).addsError(sError).addsHead(sHead).
 *                addsmData(smData).save();
 * 
 *         Обязательные параметры: sType и sFunction
 * 
 *         sType - тип сообщения, может принимать значения:
 *          
 *           ACCES_DENIED_ERROR - Ошибка доступа(авторизация) 
 *           EXTERNAL_ERROR - Внешняя ошибка
 *           INF_MESSAGE - Информационное сообщение 
 *           INTERNAL_ERROR - Внутренняя ошибка 
 *           VALIDATION_ERROR - Ошибка валидации входящих данных 
 *           WARNING - Предупреждение
 * 
 *         Если тип сообщения указать некорректно (например WARNING2 ), то
 *         принимается тип INF_MESSAGE
 * 
 *         sFunction - строка с именем функции где произошла ошибка
 * 
 * 
 *         На основании типа сообщения и имени функции формируется код шаблона
 *         сообщения в Сервисе Хранения Ошибок При вызове MsgSendImpl("WARNING",
 *         "getFunctionMeat") код будет WR_GETFUNCTIONMEAT
 * 
 *         Примечание: длина кода сообщения 25 символов, т.е. имя функции
 *         желательно уместить в 22 символа, т.к. остальные символы будут
 *         игнорироваться
 * 
 * 
 *         Для гибкой настройки может используется файл параметров
 *         msg.properties, где:
 * 
 *         MsgURL=MsgURL=http://msg.igov.org.ua/MSG // url Сервиса Хранения Ошибок 
 *         sBusId=TEST // иденификатор Бизнес процесса
 * 
 */
public class MsgSendImpl implements MsgSend {
    private static final Logger LOG = LoggerFactory.getLogger(MsgSendImpl.class);

    public static final String MSG_URL;
    private static final String TemplateMsgId = "HMXHVKM80002M0";
    private static final String TemplateMsgIdJSON = "\",\"TemplateMsgId\":\"" + TemplateMsgId + "\"}}]}";

    private static final Properties prop = new Properties();

    private static InputStream inputStream = MsgSendImpl.class.getClassLoader().getResourceAsStream("msg.properties");

    private static final String sBusId_DEFAULT;

    static {
	String MsgURL;
	String sBusId;
	try {
	    prop.load(inputStream);
	    
	    MsgURL = prop.getProperty("MsgURL",  "http://msg.igov.org.ua/MSG");
	    sBusId = prop.getProperty("sBusId", "TEST");
	} catch (IOException e) {
	    MsgURL =  "http://msg.igov.org.ua/MSG";
	    sBusId = "TEST";
	}

	MSG_URL = MsgURL; 
	sBusId_DEFAULT = sBusId;
	System.setProperty("MsgURL", MSG_URL);

    }

    public static final HTTPClient httpClient = new HTTPClient();

    private String sBusId = sBusId_DEFAULT;
    private String sMsgCode = null;
    private String MSG_DEFAULT = "DEFAULT";
    private MsgType msgType = null;
    private MsgLevel msgLevel = MsgLevel.DEVELOPER;
    private MsgLang msgLang = MsgLang.UKR;

    private String sHead = null;
    private String sBody = null;
    private String sError = null;
    private String sFunction = null;
    private Long nID_Subject = null;
    private Long nID_Server = null;

    private List<String> asParam = null;
    private String sDate = null;

    private String smDataMisc = null;

    /**
     * @param sType - тип сообщения, может принимать значения:
     *           ACCES_DENIED_ERROR - Ошибка доступа(авторизация) 
     *           EXTERNAL_ERROR - Внешняя ошибка
     *           INF_MESSAGE - Информационное сообщение 
     *           INTERNAL_ERROR - Внутренняя ошибка 
     *           VALIDATION_ERROR - Ошибка валидации входящих данных 
     *           WARNING - Предупреждение
     * 
     * @param sFunction - строка с именем функции где произошла ошибка
     * 
     */
    public MsgSendImpl(String sType, String sFunction) {
	LOG.debug("Send message sType={}, sFunction={}", sType, sFunction);

	if (sType == null || sFunction == null) {
	    throw new IllegalArgumentException("Constructor parameters: sType=" + sType + ", sFunction=" + sFunction);
	}

	try {
	    msgType = MsgType.valueOf(sType.trim().toUpperCase());
	} catch (final IllegalArgumentException e) {
	    msgType = MsgType.INF_MESSAGE;
	}

	this.sMsgCode = msgType.getAbbr() + "-" + sFunction.trim().toUpperCase();
	this.sFunction = sFunction;

	LOG.debug("Send message sMsgCode={}", this.sMsgCode);
    }

    public MsgSend addBusId(String sBusId) {
	this.sBusId = sBusId;
	return this;
    }

    public MsgSend addsHead(String sHead) {
	this.sHead = sHead;
	return this;
    }

    public MsgSend addsBody(String sBody) {
	this.sBody = sBody;
	return this;
    }

    public MsgSend addsError(String sError) {
	this.sError = sError;
	return this;
    }

    public MsgSend addnID_Subject(Long nID_Subject) {
	this.nID_Subject = nID_Subject;
	return this;
    }

    public MsgSend addnID_Server(Long nID_Server) {
	this.nID_Server = nID_Server;
	return this;
    }

    @SuppressWarnings("unchecked")
    /**
     * Разбирается структура smData с дополнительными данными по ошибке.
     * Соответствующие поля структуры запоминаются в отделных переменных, для
     * последующего сохранения в атрибутах Сервиса Хранения Ошибок
     * 
     * @param smData - JSON структура следующего фоормата:
     * 
     *  { "asParam": ["par1", "par2", "par3"], "oResponse": {
     *  "sMessage": "value sMessage", "sCode": "value sCode",
     *  "soData": "value soData" }, "sDate": "value sDate" }
     */
    public MsgSend addsmData(String smData) {
	String sResponseMessage = null;
	String sResponseCode = null;
	String soResponseData = null;
	StringBuffer smDataMisc = new StringBuffer();
	if (smData != null) {
	    asParam = new LinkedList<String>();

	    Map<String, Object> moData = JsonRestUtils.readObject(smData, Map.class);
	    if (moData != null) {
		if (moData.containsKey("asParam")) {
		    asParam = (List<String>) moData.get("asParam");
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
			this.smDataMisc = smDataMisc.toString();
		    }
		}
		if (moData.containsKey("sDate")) {
		    sDate = (String) moData.get("sDate");
		}
	    }
	}

	return this;
    }

    private void addAttr(MAttrs mAttrs, String title, Object o) {
	if (o != null) {
	    mAttrs.add(title, o.toString());
	}
    }

    public MsgSend addLangFilter(String lang) {
	try {
	    this.msgLang = MsgLang.valueOf(lang.trim().toUpperCase());
	} catch (final IllegalArgumentException e) {
	    this.msgLang = MsgLang.UKR;
	}
	return this;
    }

    public MsgSend addMsgLevel(MsgLevel msgLevel) {
	this.msgLevel = msgLevel;
	return this;
    }

    /**
     * Формирование JSON структуры для создание шаблона сообщения с новым кодом.
     * 
     * Вид структуры: 
     * { "r" : [ { _type_comment" : "Создание сообщения", "type"
     * : "MSG_ADD", "sid" : "${sid}", "s" : { "Type" : "${Тип сообщения}",
     * "MsgCode" : "${Код сообщения}", "BusId" : "${Id бизнеспроцесса}", "Descr"
     * : "${Описание сообщения}", "TemplateMsgId" : "${Id шаблона}" } }] }
     * 
     * @return Возвращает JSON структуру создания шаблона сообщения в
     *         виде строки
     */
    private String buildJSON() {
	StringBuilder sb = new StringBuilder(500);
	sb.append(
		"{\"r\":[{\"_type_comment\" : \"Создание сообщения\",\"type\":\"MSG_ADD\",\"sid\" : \"\",\"s\":{\"Type\":\"");
	sb.append(msgType.name());
	sb.append("\",\"MsgCode\":\"");
	sb.append(sMsgCode);
	sb.append("\",\"BusId\":\"");
	sb.append(sBusId);
	sb.append("\",\"Title\":\"");
	sb.append(sFunction);
	sb.append("\",\"Descr\":\"");
	sb.append(sFunction);
	sb.append("\",\"Text\":\"");
	sb.append(sFunction);
	sb.append("\",\"FullText\":\"");
	sb.append(sFunction);
	sb.append(TemplateMsgIdJSON);

	return sb.toString();
    }

    /**
     * Создание шаблона сообщения с новым кодом
     */
    private IMsgObjR createMsg() {
	MsgCreate msgCreate = new MsgCreate(buildJSON());
	try {
	    msgCreate.doReqest();
	} catch (Exception e) {
	    e.printStackTrace();
	}

	return null;
    }

    // public static void main(String[] args) throws IOException {
    // MsgSend msgSend = new MsgSendImpl("warning", "function");
    // IMsgObjR msg =
    // msgSend.addnID_Server(1L).addnID_Subject(1L).addsBody("text
    // body").addsError("text error")
    // .addsHead("text head 22").save();
    //
    // System.out.println("msg = " + msg);
    //
    // }

    @Override
    /**
     * Сохранение сообщения. Если в Сервисе Хранения Ошибок нет шаблона
     * сообщения с таким кодом, то оно сохраняется в шаблоне сообщении с кодом
     * DEFAULT В этом случае программа пытается добавить новый шаблон сообщения
     * с этим кодом в Сервис Хранения Ошибок
     */
    public IMsgObjR save() {
	IMsgObjR retMsg = doMsg();
	LOG.debug("retMsg={}", retMsg);

	// Создать сообщение если его не было раньше
	if (retMsg.getMsgCode().equals(MSG_DEFAULT) && !sMsgCode.equals(MSG_DEFAULT)) {
	    LOG.debug("Сообщения с кодом {} не найдено. Попытка его создать.", this.sMsgCode);
	    createMsg();
	}

	return retMsg;
    }

    // Запрос на сохранение сообщения
    // Вынес из save на тот случай, если понадобиться повторный вызов после
    // создания нового шаблона сообщения
    private IMsgObjR doMsg() {
	MAttrs mAttrs = new MAttrs();

	addAttr(mAttrs, "sHead", sHead);
	addAttr(mAttrs, "sBody", sBody);
	addAttr(mAttrs, "nID_Subject", nID_Subject);
	addAttr(mAttrs, "nID_Server", nID_Server);
	addAttr(mAttrs, "sDate", sDate);

	if (asParam != null) {
	    StringBuffer asParamSb = new StringBuffer();
	    asParamSb.append("[");
	    boolean isNotFirst = false;
	    for (String param : asParam) {
		if (isNotFirst) {
		    asParamSb.append(",");
		}
		asParamSb.append("{").append(param).append("}");
		isNotFirst = true;
	    }
	    asParamSb.append("]");
	    addAttr(mAttrs, "asParam", asParamSb.toString());
	}
	addAttr(mAttrs, "smDataMisc", smDataMisc);

	MFilter filter = new MFilter();
	filter.setDTM(new Timestamp(System.currentTimeMillis()));
	filter.setPatternReplaced(true).setBusId(sBusId).setMsgCode(sMsgCode).setAttrMode(MsgAttrMode.SEND);
	filter.setLangFilter(msgLang.name());
	filter.setAttrs(mAttrs);
	filter.setStack(sError);
	filter.setSource(sFunction);
	filter.setLevelFilter(msgLevel);
	filter.setMsgId(filter.getMsgId());

	LOG.debug("filter={}", filter);

	return Msg.getMsg(filter);
    }

}
