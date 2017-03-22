package org.igov.service.business.object;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import javax.annotation.PostConstruct;

import org.igov.io.GeneralConfig;
import org.igov.service.business.promin.ProminSession_Singleton;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

@Component("ObjectPlaceCommonService")
@Service
public class ObjectPlaceCommonService {
    private static final Logger LOG = LoggerFactory.getLogger(ObjectPlaceCommonService.class);

//    private static final String SUB_URL_ADDRESS_BY_TYPE = "/AddressReference/address/listAddressByType.do";
//    private static final String SUB_URL_ADDRESS_BY_NAME = "/AddressReference/address/searchByName.do";
    private static final String SUB_URL_ADDRESS_BY_TYPE = "/listAddressByType.do";
    private static final String SUB_URL_ADDRESS_BY_NAME = "/searchByName.do";
    private static final String NULL_RESPONSE = "{}";

    @Autowired
    GeneralConfig generalConfig;

    private String sURLSendAddressByType = null;
    private String sURLSendAddressByName = null;
    private String sAuth_sLogin = null;
    private String sAuth_sPassword = null;
    private String sAuth_sURL_GenerateSID = null;

    // Признак готовности сервиса к работе
    private boolean isReadyWork = false;

    /*
     * Проверяем заданы ли все параметры. Если нет то сервис не готов отсылать
     * сообщения.
     */
    @PostConstruct
    private void init() {
	String sURL_Send = null;
	if (generalConfig != null) {
	    sAuth_sLogin = generalConfig.getObjectSubPlace_Auth_sLogin();
	    sAuth_sPassword = generalConfig.getObjectSubPlace_Auth_sPassword();
	    sAuth_sURL_GenerateSID = generalConfig.getObjectSubPlace_Auth_sURL_GenerateSID();
	    sURL_Send = generalConfig.getObjectSubPlace_sURL_Send();
	}
	if (sURL_Send == null || sAuth_sLogin == null || sAuth_sPassword == null || sAuth_sURL_GenerateSID == null) {
	    LOG.warn(
		    "Сервис не готов к отсылке сообщений. Не заданы необходимые параметры. sURL_Send={}, sAuth_sLogin={}, sAuth_sPassword={}, sAuth_sURL_GenerateSID={}",
		    sURL_Send, sAuth_sLogin, sAuth_sPassword, sAuth_sURL_GenerateSID);
	    return;
	}

	LOG.debug("sURL_Send={}, sAuth_sLogin={}, sAuth_sPassword={}, sAuth_sURL_GenerateSID={}", sURL_Send,
		sAuth_sLogin, sAuth_sPassword, sAuth_sURL_GenerateSID);

	if (sURL_Send.startsWith("${") || sAuth_sLogin.startsWith("${") || sAuth_sPassword.startsWith("${")
		|| sAuth_sURL_GenerateSID.startsWith("${")) {
	    LOG.warn("Сервис не готов к отсылке сообщений. Не заданы необходимые параметры");
	    return;
	}
	sURLSendAddressByType = sURL_Send + SUB_URL_ADDRESS_BY_TYPE;
	sURLSendAddressByName = sURL_Send + SUB_URL_ADDRESS_BY_NAME;
	
	isReadyWork = true;
    }

    public String getSubPlaces_(String sID_SubPlace_PB, String sFind) {
	if (!isReadyWork) {
	    LOG.warn("Сервис не готов к работе.");
	    return "{}";
	}

	LOG.debug("sID_SubPlace_PB={}, sFind={}", sID_SubPlace_PB, sFind);

	if (sID_SubPlace_PB == null) {
	    LOG.error("Error sID_SubPlace_PB is null");
	    return NULL_RESPONSE;
	}

	if (sFind != null) {
	    return searchByName(sURLSendAddressByName, sID_SubPlace_PB, ObjectPlaceType.STREET, sFind,
		    ObjectPlaceLang.UAN);
	} else {
	    return listAddressByType(sURLSendAddressByType, sID_SubPlace_PB, ObjectPlaceType.STREET,
		    ObjectPlaceLang.UAN, null, null);
	}
    }

    private String listAddressByType(String sUrl, String idParent, ObjectPlaceType type, ObjectPlaceLang language,
	    String sTypeCode, String sFromId) {
	LOG.debug("sUrl={}, idParent={}, type={}, language={}, sTypeCode={}, sFromId={}", sUrl, idParent, type,
		language, sTypeCode, sFromId);

	String ret = "";
	HttpURLConnection oHttpURLConnection = null;
	try {
	    URL oURL = new URL(sUrl);
	    oHttpURLConnection = (HttpURLConnection) oURL.openConnection();
	    oHttpURLConnection.setRequestMethod("GET");
	    oHttpURLConnection.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
	    oHttpURLConnection.setRequestProperty("type", type.getIdString());
	    oHttpURLConnection.setRequestProperty("language", language.name());
	    if (sTypeCode != null) {
		oHttpURLConnection.setRequestProperty("typeCode", sTypeCode);
	    }
	    if (sFromId != null) {
		oHttpURLConnection.setRequestProperty("fromId", sFromId);
	    }

	    try (BufferedReader oBufferedReader = new BufferedReader(
		    new InputStreamReader(oHttpURLConnection.getInputStream()))) {
		StringBuilder os = new StringBuilder();
		String s;
		while ((s = oBufferedReader.readLine()) != null) {
		    os.append(s);
		}
		ret = os.toString();

	    } catch (java.io.FileNotFoundException e) {
		ret = NULL_RESPONSE;
		LOG.error("http code:{}\n", oHttpURLConnection.getResponseCode(), e);
	    }

	} catch (MalformedURLException e) {
	    LOG.error("Error:", e.getMessage());
	    ret = NULL_RESPONSE;
	} catch (IOException e) {
	    LOG.error("Error:", e.getMessage());
	    ret = NULL_RESPONSE;
	} finally {
	    if (oHttpURLConnection != null) {
		oHttpURLConnection.disconnect();
	    }
	}

	LOG.info("ResponseBody:\n{}", ret);

	return ret;

    }

    private String searchByName(String sUrl, String idParent, ObjectPlaceType type, String sName,
	    ObjectPlaceLang language) {
	LOG.debug("sUrl={}, idParent={}, type={}, sName={}, language={}", sUrl, idParent, type, sName, language);

	sName = sName.trim();

	String ret = "";
	HttpURLConnection oHttpURLConnection = null;
	try {
	    URL oURL = new URL(sUrl);
	    oHttpURLConnection = (HttpURLConnection) oURL.openConnection();
	    oHttpURLConnection.setRequestMethod("GET");
	    oHttpURLConnection.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
	    oHttpURLConnection.setRequestProperty("type", type.getIdString());
	    if (language != null) {
		oHttpURLConnection.setRequestProperty("language", language.name());
	    }
	    if (sName != null) {
		oHttpURLConnection.setRequestProperty("name", sName);
	    }

	    try (BufferedReader oBufferedReader = new BufferedReader(
		    new InputStreamReader(oHttpURLConnection.getInputStream()))) {
		StringBuilder os = new StringBuilder();
		String s;
		while ((s = oBufferedReader.readLine()) != null) {
		    os.append(s);
		}
		ret = os.toString();

	    } catch (java.io.FileNotFoundException e) {
		ret = NULL_RESPONSE;
		LOG.error("http code:{}\n", oHttpURLConnection.getResponseCode(), e);
	    }

	} catch (MalformedURLException e) {
	    LOG.error("Error:", e.getMessage());
	    ret = NULL_RESPONSE;
	} catch (IOException e) {
	    LOG.error("Error:", e.getMessage());
	    ret = NULL_RESPONSE;
	} finally {
	    if (oHttpURLConnection != null) {
		oHttpURLConnection.disconnect();
	    }
	}

	LOG.info("ResponseBody:\n{}", ret);

	return ret;
    }

}

// HTTP GET на
// {url}/AddressReference/address/listAddressByType.do?id={idParent}&type={type}&language={language}&typeCode={typeCode}&fromId={fromId}
// где {idParent} - внешний идентификатор адреса-предка,
// {type} - тип требуемого узла,
// {language} - язык отображения,
// {typeCode} - (необязательный параметр) код узла (для 'type=4':"T" - города,
// "V" - сёла),
// {fromId} - (необязательный параметр) начальный идентификатор с которого будет
// производиться поиск.

// HTTP GET на
// {url}/AddressReference/address/searchByName.do?idParent={idParent}&type={type}&name={name}&language={language}
// где {idParent} - внешний идентификатор адреса-предка,
// {type} - тип требуемого узла,
// {name} - наименование или часть наименования узла-потомка,
// {language} - (необязательный параметр, по умолчанию - RUS) язык отображения.
