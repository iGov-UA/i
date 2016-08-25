package org.igov.service.business.msg;

import java.sql.Timestamp;
import java.util.HashMap;
import java.util.Iterator;
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
//import com.pb.util.gsv.net.HTTPClient;

/**
 * 
 * @author kr110666kai & bw
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
 *         WR-IGOV_CONTROLLER_GETFUNCTION  - здесь точки заменены на знак подчеркивания, скобки заменяются то-же.
 *         123456789012345678901234567890 
 * 
 *         
 *         Для гибкой настройки программы может использоваться файл параметров AS.properties, где:
 * 
 *         general.Monitor.MSG.sLogin=login@gmail.com	// Логин которому разрешено добавлять сообщения
 *         general.Monitor.MSG.sBusinessId=TEST			// иденификатор Бизнес процесса
 *         general.Monitor.MSG.sTemplateId=HMXHVKM80002M0	// код пустого шаблона СООБЩЕНИЯ
 *         general.Monitor.MSG.sURL=http://msg.igov.org.ua/MSG	// url Сервиса Хранения Ошибок
 *         
 */
public class MsgSendImpl implements IMsgSend {
    private static final Logger LOG = LoggerFactory.getLogger(MsgSendImpl.class);
    
    private static final String MSG_DEFAULT = "DEFAULT";

    private static final int MSG_CODE_LENGTH = 30 - 3;
    
    // Символы разрешенные в коде СООБЩЕНИЯ
    private static final String ALLOWED_CHARS_MSG_CODE = "^[a-zA-Z0-9-_]+$";
    
    //public static final HTTPClient httpClient = new HTTPClient();

    private String sTemplateId_MSG = null;
    private String sBusinessId_MSG = null;
    private String sLogin_MSG = null;
    private String sURL_MSG = null;
    
    private String sCode_MSG = null;
    private MsgType oMsgType = null;
    private MsgLevel oMsgLevel = MsgLevel.DEVELOPER;
    private MsgLang oMsgLang = MsgLang.UKR;

    private String sHead = null;
    private String sBody = null;
    private String sError = null;
    private String sFunction = null;
    private Long nID_Subject = null;
    private Long nID_Server = null;

    private List<String> asParam = null;
    private String sDate = null;

    //private String smDataMisc = null;

    /**
     * @param sType - тип СООБЩЕНИЯ
     * 
     * @param sFunction - строка с именем функции где произошла ошибка
     * 
     */
    public MsgSendImpl(String sURL, String sBusId, String sType, String sFunction, String sTemplateId, String sLogin ) {
	LOG.debug("sMsgURL={}, sBusId={}, Type={}, sFunction={}, TemplateMsgId={}, MsgLogin={}", sURL, sBusId,
		sType, sFunction, sTemplateId, sLogin);

	if (sURL ==null || sBusId == null || sType == null || sFunction == null || sTemplateId == null || sLogin == null ) {
	    throw new IllegalArgumentException("parameters is null");
	}

	try {
	    oMsgType = MsgType.valueOf(sType.trim().toUpperCase());
	} catch (final IllegalArgumentException e) {
	    oMsgType = MsgType.INF_MESSAGE;
	}
	
	String sf = sFunction.trim().toUpperCase().replaceAll("[\\.\\(\\)]","_");
	LOG.debug("Modified sFunction={}", sf);
	
	if (!sf.matches(ALLOWED_CHARS_MSG_CODE)) {
	    throw new IllegalArgumentException("Недопустимые символы в sFunction. Разрешено использовать цифры и буквы латинского алфавита.");
	}
	
	if ( sf.length() > MSG_CODE_LENGTH ) {
	    sf = sf.substring(sf.length() - MSG_CODE_LENGTH);
	}

	this.sURL_MSG = sURL;
	this.sBusinessId_MSG = sBusId;
	this.sCode_MSG = oMsgType.getAbbr() + "-" + sf;
	this.sFunction = sFunction;
	this.sTemplateId_MSG = sTemplateId;
	this.sLogin_MSG = sLogin;

	LOG.debug("MsgCode={}", this.sCode_MSG);
    }

    @Override
    public IMsgSend _Head(String sHead) {
	this.sHead = sHead;
	//LOG.debug("sHead={}", this.sHead);
	return this;
    }

    @Override
    public IMsgSend _Body(String sBody) {
	this.sBody = sBody;
	//LOG.debug("sBody={}", this.sBody);
	return this;
    }

    @Override
    public IMsgSend _Error(String sError) {
	this.sError = sError;
	//LOG.debug("sError={}", this.sError);
	return this;
    }

    @Override
    public IMsgSend _SubjectID(Long nID_Subject) {
	this.nID_Subject = nID_Subject;
	//LOG.debug("nID_Subject={}", this.nID_Subject);
	return this;
    }

    @Override
    public IMsgSend _ServerID(Long nID_Server) {
	this.nID_Server = nID_Server;
	//LOG.debug("nID_Server={}", this.nID_Server);
	return this;
    }

    @Override
    public <T> IMsgSend _Params(Map<String, T > mParam) {//HashMap<String, T > mParam
	if ( mParam != null ) {
	    this.asParam = new LinkedList<String>();
            mParam.entrySet().stream().forEach((o) -> {
                this.asParam.add(o.getKey() + ": " + (o.getValue()==null?"NULL":o.getValue())+"");
            });
	}
	//LOG.debug("asParam={}", this.asParam);
	return this;
    }

    /*@Override
    public IMsgSend _DataMisc(String sm) {
	this.smDataMisc = s;
	//LOG.debug("smDataMisc={}", this.smDataMisc);
	return this;
    }*/

    
    private void addAttr(MAttrs mAttrs, String title, Object o) {
	if (o != null) {
	    mAttrs.add(title, o.toString());
	}
    }

    public IMsgSend addLangFilter(String lang) {
	try {
	    this.oMsgLang = MsgLang.valueOf(lang.trim().toUpperCase());
	} catch (final IllegalArgumentException e) {
	    this.oMsgLang = MsgLang.UKR;
	}
	LOG.debug("Received lang={}, set lang={}", lang, this.oMsgLang);
	return this;
    }

    @Override
    public IMsgSend _Level(MsgLevel msgLevel) {
	this.oMsgLevel = msgLevel;
	//LOG.debug("level={}", this.msgLevel);
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
	sb.append(this.sLogin_MSG);
	sb.append("\", \"s\":{\"Type\":\"");
	sb.append(oMsgType.name());
	sb.append("\",\"MsgCode\":\"");
	sb.append(sCode_MSG);
	sb.append("\",\"BusId\":\"");
	sb.append(sBusinessId_MSG);
	sb.append("\",\"Descr\":\"");
	sb.append(sFunction);
	sb.append("\",\"TemplateMsgId\":\"");
	sb.append(this.sTemplateId_MSG);
	sb.append("\", \"ext\":{\"LocalMsg\":[{\"Level\":\"");
	sb.append(this.oMsgLevel);
	sb.append("\",\"Lang\":\"");
	sb.append(this.oMsgLang.name());
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
	msgCreate.doReqest(this.sURL_MSG);
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

	if ( retMsg != null ) {
	    LOG.debug("Ответ:\n{}", retMsg);
	    
	    // Создать сообщение если его не было раньше
	    if (retMsg.getMsgCode().equals(MSG_DEFAULT) && !sCode_MSG.equals(MSG_DEFAULT)) {
		LOG.warn("Сообщение с кодом {} не найдено, попытка его создания.", this.sCode_MSG);
		createMsg();
		LOG.info("Созданно сообщение с кодом : {}", this.sCode_MSG);
	    
		// Cохранить данные во вновь созданном СООБЩЕНИИ
		retMsg = doMsg();
	    }
	} else {
	    LOG.warn("Ошибка работы с сервисом, сервис вернул ответ: null");
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
	    StringBuilder asParamSb = new StringBuilder();
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
	//addAttr(mAttrs, "smDataMisc", smDataMisc);

	MFilter oMFilter = new MFilter();
	oMFilter.setDTM(new Timestamp(System.currentTimeMillis()));
	oMFilter.setPatternReplaced(true)
                .setBusId(sBusinessId_MSG)
                .setMsgCode(sCode_MSG)
                .setAttrMode(MsgAttrMode.SEND)
            ;
	oMFilter.setLangFilter(oMsgLang.name());
	oMFilter.setAttrs(mAttrs);
	oMFilter.setStack(sError);
	oMFilter.setSource(sFunction);
	oMFilter.setLevelFilter(oMsgLevel.name());
	oMFilter.setMsgId(oMFilter.getMsgId());

	LOG.debug("oMFilter:\n{}", oMFilter);

	return Msg.getMsg(oMFilter);
        //return null;
    }

}
