package org.igov.service.business.msg;

import java.sql.Timestamp;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

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
 *         Реализация Интерфеса отсылки сообщений в Сервис Хранения Ошибок http://msg.igov.org.ua/MSG
 * 
 *         Пример использования:
 * 
 *         IMsgObjR msg = new MsgSendImpl("http://msg.igov.org.ua/MSG", "TEST", "WARNING", "sFunction", "HMXHVKM80002M0", "name@gmail.com").
 *         			addnID_Server(1L).
 *         			addnID_Subject(1L).
 *         			addsBody("sBody").
 *         	         	addsError("sError").
 *         			addsHead("sHead").
 *         			addsmData(smData).
 *         			save();
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
 *         Если тип сообщения указать некорректно (например WARNING2 ), то принимается тип INF_MESSAGE
 * 
 *         sFunction - строка с именем функции где произошла ошибка
 *         
 *         
 *              В Сервисе Хранения Ошибок все отсылаемые данные привязываются к определенному СООБЩЕНИЮ, которое должно быть
 *         заведено на сервисе заранее ( в нашем случае это делает данная программа). 
 *         
 *              СООБЩЕНИЕ - это сущность, содержащая в себе информацию, включающую атрибуты сообщения и набор представлений 
 *         (для данного языка и уровня сообщения). Характеризуется Кодом СООБЩЕНИЯ который может быть определен пользователем 
 *         при создании сообщения. Код уникален в рамках бизнес процесса и не может быть изменен после создания сообщения.
 *         
 *              Все данные в Сервисе Хранения Ошибок привязываются к СООБЩЕНИЮ по его коду. Код СООБЩЕНИЯ - набор латинских 
 *         символов и цифр, например: IGOV-MAIN77.
 *          
 *              Если СООБЩЕНИЯ с заданным кодом нет, то передаваемые данные привязываются к СООБЩЕНИЮ с кодом DEFAULT. 
 *         В этом случае, программа попытается создать на сервисе новое СООБЩЕНИЕ с новым кодом. 
 *                 
 *             Для задач igov мы приняли следующий шаблон формирования Кода СООБЩЕНИЯ:  АББРЕВИАТУРА_ТИПА_СООБЩЕНИЯ-ИМЯ_ФУНКЦИИ, 
 *         длина кода сообщения не более 30символов. При превышении этой длины название функции обрезается слева.
 *         Например при вызове MsgSendImpl("WARNING","org.igov.controller.getFunction"), Код СООБЩЕНИЯ будет: 
 *         WR-IGOV_CONTROLLER_GETFUNCTION  - здесь точки заменены на знак подчеркивания
 *         123456789012345678901234567890 
 * 
 *         
 *         Для гибкой настройки программы может использоваться файл параметров msg.properties, где:
 * 
 *         MsgURL=http://msg.igov.org.ua/MSG  	// url Сервиса Хранения Ошибок 
 *         BusId=TEST 				// иденификатор Бизнес процесса
 *         TemplateMsgId=HMXHVKM70002M0		// код пустого шаблона СООБЩЕНИЯ
 *         
 */
public class MsgSendImpl implements MsgSend {
    private static final Logger LOG = LoggerFactory.getLogger(MsgSendImpl.class);
    
    private static final String MSG_DEFAULT = "DEFAULT";

    private static final int MSG_CODE_LENGTH = 30 - 3;
    
    // Символы разрешенные в коде СООБЩЕНИЯ
    private static final String ALLOWED_CHARS_MSG_CODE = "^[a-zA-Z0-9-_]+$";
    
    public static final HTTPClient httpClient = new HTTPClient();

    private String sTemplateMsgId = null;
    private String sMsgLogin = null;

    private String sMsgURL = null;
    private String sBusId = null;
    private String sMsgCode = null;
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
     * @param sType - тип СООБЩЕНИЯ
     * 
     * @param sFunction - строка с именем функции где произошла ошибка
     * 
     */
    public MsgSendImpl(String sMsgURL, String sBusId, String sType, String sFunction, String sTemplateMsgId, String sMsgLogin ) {
	LOG.debug("sMsgURL={}, sBusId={}, Type={}, Function={}, TemplateMsgId={}, MsgLogin={}", sMsgURL, sBusId,
		sType, sFunction, sTemplateMsgId, sMsgLogin);

	if (sMsgURL ==null || sBusId == null || sType == null || sFunction == null || sTemplateMsgId == null || sMsgLogin == null ) {
	    throw new IllegalArgumentException("parameters is null");
	}

	try {
	    msgType = MsgType.valueOf(sType.trim().toUpperCase());
	} catch (final IllegalArgumentException e) {
	    msgType = MsgType.INF_MESSAGE;
	}
	
	String sf = sFunction.trim().toUpperCase().replaceAll("[\\.\\(\\)]","_");
	LOG.debug("Modified sFunction={}", sf);
	
	if (!sf.matches(ALLOWED_CHARS_MSG_CODE)) {
	    throw new IllegalArgumentException("Недопустимые символы в sFunction. Разрешено использовать цифры и буквы латинского алфавита.");
	}
	
	if ( sf.length() > MSG_CODE_LENGTH ) {
	    sf = sf.substring(sf.length() - MSG_CODE_LENGTH);
	}

	this.sMsgURL = sMsgURL;
	this.sBusId = sBusId;
	this.sMsgCode = msgType.getAbbr() + "-" + sf;
	this.sFunction = sFunction;
	this.sTemplateMsgId = sTemplateMsgId;
	this.sMsgLogin = sMsgLogin;

	LOG.debug("MsgCode={}", this.sMsgCode);
    }

    public MsgSend addsHead(String sHead) {
	this.sHead = sHead;
	LOG.debug("set sHead={}", this.sHead);
	return this;
    }

    public MsgSend addsBody(String sBody) {
	this.sBody = sBody;
	LOG.debug("set sBody={}", this.sBody);
	return this;
    }

    public MsgSend addsError(String sError) {
	this.sError = sError;
	LOG.debug("set sError={}", this.sError);
	return this;
    }

    public MsgSend addnID_Subject(Long nID_Subject) {
	this.nID_Subject = nID_Subject;
	LOG.debug("set nID_Subject={}", this.nID_Subject);
	return this;
    }

    public MsgSend addnID_Server(Long nID_Server) {
	this.nID_Server = nID_Server;
	LOG.debug("set nID_Server={}", this.nID_Server);
	return this;
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
    public MsgSend addsmData(String smData) {
	smData = smData.trim();

	LOG.debug("set smData=[{}]", smData);

	if (smData.isEmpty()) {
	    LOG.debug("smData is Empty");
	    return this;
	}

	String sResponseMessage = null;
	String sResponseCode = null;
	String soResponseData = null;
	StringBuffer smDataMisc = new StringBuffer();
	if (smData != null) {

	    asParam = new LinkedList<String>();
	    Map<String, Object> moData = null;
	    try {
		moData = JsonRestUtils.readObject(smData, Map.class);
	    } catch (Exception e1) {
		this.smDataMisc = "Error parse JSON smData";
	    }

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
	LOG.debug("Received lang={}, set lang={}", lang, this.msgLang);
	return this;
    }

    public MsgSend addMsgLevel(MsgLevel msgLevel) {
	this.msgLevel = msgLevel;
	LOG.debug("set level={}", this.msgLevel);
	return this;
    }

    /**
     * Формирование JSON структуры для создание СООБЩЕНИЯ с новым кодом.
     * 
     *{
     *  "r": [
     *    {
     *      "_type_comment": "Создание сообщения",
     *      "type": "MSG_ADD",
     *      "sid": "",
     *      "s": {
     *        "Type": "WARNING",
     *        "MsgCode": "WR-GETMESSAGEIMPL",
     *        "BusId": "TEST",
     *        "Descr": "getMessageImpl",
     *        "TemplateMsgId": "HMXHVKM80002M0",
     *        "ext": {
     *                "LocalMsg": [{
     *              	            "Level": "DEVELOPER",
     *                         	    "Lang": "UKR",
     *                              "Text": "getMessageImpl",
     *                              "FullText": ""
     *                             }]
     *               }
     *       }
     *    }
     *  ]
     *}
     * 
     * @return Возвращает JSON структуру создания шаблона сообщения в виде
     *         строки
     */
    private String buildJSON() {
	StringBuilder sb = new StringBuilder(500);
	sb.append("{\"r\":[{\"_type_comment\" : \"Создание сообщения\",\"type\":\"MSG_ADD\",\"sid\" : \"\", \"login\":\"");
	sb.append(this.sMsgLogin);
	sb.append("\", \"s\":{\"Type\":\"");
	sb.append(msgType.name());
	sb.append("\",\"MsgCode\":\"");
	sb.append(sMsgCode);
	sb.append("\",\"BusId\":\"");
	sb.append(sBusId);
	sb.append("\",\"Descr\":\"");
	sb.append(sFunction);
	sb.append("\",\"TemplateMsgId\":\"");
	sb.append(this.sTemplateMsgId);
	sb.append("\", \"ext\":{\"LocalMsg\":[{\"Level\":\"");
	sb.append(this.msgLevel);
	sb.append("\",\"Lang\":\"");
	sb.append(this.msgLang.name());
	sb.append("\",\"Text\":\"");
	sb.append(sFunction);
	sb.append("\",\"FullText\":\"\"}]}}}]}");

	LOG.trace("MSG JSON={}", sb.toString());

	return sb.toString();
    }

    /**
     * Создание шаблона сообщения с новым кодом
     * @throws Exception 
     */
    private void createMsg(  ) throws Exception {
	MsgCreate msgCreate = new MsgCreate(buildJSON());
	msgCreate.doReqest(this.sMsgURL);
    }

//     public static void main(String[] args)  {
//         try {
//             MsgSendImpl msg = new MsgSendImpl("", "", "WARNING", "org.getFunction(2)", "", "");
//	} catch (Exception e) {
//	    e.printStackTrace();
//	}
//     }

    @Override
    /**
     * Сохранение данных. Если в Сервисе Хранения Ошибок нет СООБЩЕНИЯ с заданным кодом, то передаваемые данные привязываются
     *  к СООБЩЕНИЮ с кодом DEFAULT. В этом случае, программа попытается создать на сервисе новое СООБЩЕНИЕ с новым кодом. 
     */
    public IMsgObjR save() throws Exception {
	IMsgObjR retMsg = doMsg();
	LOG.debug("Ответ:\n{}", retMsg);

	// Создать сообщение если его не было раньше
	if (retMsg.getMsgCode().equals(MSG_DEFAULT) && !sMsgCode.equals(MSG_DEFAULT)) {
	    LOG.warn("Сообщение с кодом {} не найдено, попытка его создания.", this.sMsgCode);
	    createMsg();
	    LOG.info("Созданно сообщение с кодом : {}", this.sMsgCode);
	    
	    // Cохранить данные во вновь созданном СООБЩЕНИИ
	    retMsg = doMsg();
	}

	return retMsg;
    }

    // Запрос на сохранение СООБЩЕНИЯ
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
	filter.setLevelFilter(msgLevel.name());
	filter.setMsgId(filter.getMsgId());

	LOG.debug("filter:\n{}", filter);

	return Msg.getMsg(filter);
    }

}
