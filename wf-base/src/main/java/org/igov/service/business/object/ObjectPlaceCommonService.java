package org.igov.service.business.object;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.StandardCharsets;

import javax.annotation.PostConstruct;

import org.igov.io.GeneralConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import org.igov.io.web.HttpRequester;
import org.igov.io.web.RestRequest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;

@Component("ObjectPlaceCommonService")
@Service
public class ObjectPlaceCommonService {
	private static final Logger LOG = LoggerFactory.getLogger(ObjectPlaceCommonService.class);

	private static final String SUB_URL_ADDRESS_BY_TYPE = "/AddressReference/address/listAddressByType.do";
	private static final String SUB_URL_ADDRESS_BY_NAME = "/AddressReference/address/searchByName.do";
	private static final String NULL_RESPONSE = "{}";

	@Autowired
	GeneralConfig generalConfig;

	private String sURLSendAddressByType = null;
	private String sURLSendAddressByName = null;

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
			sURL_Send = generalConfig.getObjectSubPlace_sURL_Send();
		}
		if (sURL_Send == null) {
			LOG.warn("Сервис не готов к отсылке сообщений. Не заданы необходимые параметры. sURL_Send={}", sURL_Send);
			return;
		}

		LOG.debug("sURL_Send={}", sURL_Send);

		if (sURL_Send.startsWith("${")) {
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

		LOG.info("sID_SubPlace_PB={}, sFind={}", sID_SubPlace_PB, sFind);

		if (sID_SubPlace_PB == null) {
			LOG.error("Error sID_SubPlace_PB is null");
			return NULL_RESPONSE;
		}

		HttpHeaders headers = new HttpHeaders();
		headers.set("Content-Type", "application/xml; charset=utf-8");

		if (sFind != null) {
			StringBuffer sb = new StringBuffer("?idParent=");
			sb.append(sID_SubPlace_PB);
			sb.append("&type=");
			sb.append(ObjectPlaceType.STREET.getIdString());
			sb.append("&language=");
			sb.append(ObjectPlaceLang.RUS.name());
			sb.append("&name=");
			sb.append(sFind);

			String resp = new RestRequest().get(sURLSendAddressByName + sb.toString(), null, StandardCharsets.UTF_8,
					String.class, headers);

			return resp;
		} else {
			StringBuffer sb = new StringBuffer("?id=");
			sb.append(sID_SubPlace_PB);
			sb.append("&type=");
			sb.append(ObjectPlaceType.STREET.getIdString());
			sb.append("&language=");
			sb.append(ObjectPlaceLang.RUS.name());

			String resp = new RestRequest().get(sURLSendAddressByType + sb.toString(), null, StandardCharsets.UTF_8,
					String.class, headers);

			return resp;

		}
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
