package org.igov.service.business.object;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.igov.io.GeneralConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import org.igov.io.web.RestRequest;
import org.springframework.http.HttpHeaders;

@Component("ObjectPlaceCommonService")
@Service
public class ObjectPlaceCommonService {
	private static final Logger LOG = LoggerFactory.getLogger(ObjectPlaceCommonService.class);

	private static final String SUB_URL_ADDRESS_BY_TYPE = "/AddressReference/address/listAddressByType.do";
	private static final String SUB_URL_ADDRESS_BY_NAME = "/AddressReference/address/searchByName.do";
	private static final String NULL_RESPONSE = "{}";

	private HashMap<String, List<ObjectAddress>> mListAddress = new HashMap<String, List<ObjectAddress>>();

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

		sID_SubPlace_PB = sID_SubPlace_PB.trim();

		List<ObjectAddress> lObjectAddress = null;

		// Берем или из кеша, или запрашиваем сервис
		if (mListAddress.containsKey(sID_SubPlace_PB)) {
			LOG.debug("Берем данные из кеша");
			List<ObjectAddress> lObjectAddress2 = (List<ObjectAddress>) mListAddress.get(sID_SubPlace_PB);

			if (sFind != null) {
				lObjectAddress = new ArrayList<>();

				String sFind2 = sFind.toUpperCase();
				for (ObjectAddress objectAddress : lObjectAddress2) {
					if (objectAddress.getName().toUpperCase().contains(sFind2)) {
						lObjectAddress.add(objectAddress);
					}
				}
			}

		} else {
			
			LOG.debug("Берем данные из сервиса");
			
			try {
				String ret = getSubPlacesFromService(sID_SubPlace_PB, sFind);
				lObjectAddress = parseDataFromXML(ret);

				// В кеш добавляем только полный список адресов по коду
				if (sFind == null && lObjectAddress != null) {
					mListAddress.put(sID_SubPlace_PB, lObjectAddress);
					LOG.info("add cache, key={}", sID_SubPlace_PB);
				}

			} catch (Exception e) {
				LOG.error("ошибка получения данных сервиса", e);
			}
		}

		if (lObjectAddress == null) {
			return NULL_RESPONSE;

		}

		StringBuffer sb = new StringBuffer(lObjectAddress.size() * 50);
		sb.append("{\"listAddress\":[");
		int i = 0;
		for (ObjectAddress objectAddress : lObjectAddress) {
			sb.append(objectAddress.toString());

			i++;
			if (i < lObjectAddress.size()) {
				sb.append(",");
			}
		}
		sb.append("]}");

		return sb.toString();
	}

	private List<ObjectAddress> parseDataFromXML(String sListAddressXML) {
		List<ObjectAddress> lObjectAddress = null;

		try {
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			DocumentBuilder builder = factory.newDocumentBuilder();

			StringBuilder xmlStringBuilder = new StringBuilder();
			xmlStringBuilder.append(sListAddressXML);
			ByteArrayInputStream input = new ByteArrayInputStream(xmlStringBuilder.toString().getBytes("UTF-8"));

			try {
				Document doc = builder.parse(input);

				lObjectAddress = new ArrayList<>();

				NodeList nodes = ((org.w3c.dom.Document) doc).getElementsByTagName("address");
				for (int i = 0; i < nodes.getLength(); i++) {
					Element element = (Element) nodes.item(i);

					String code = element.getAttribute("code");
					String desc = element.getAttribute("desc");
					String name = element.getAttribute("nodeTypeName");

					ObjectAddress objectAddress = new ObjectAddress(code, desc, name);
					lObjectAddress.add(objectAddress);
				}

			} catch (SAXException | IOException e) {
				LOG.error("Ошибка обработки XML", e);
			}

		} catch (ParserConfigurationException e) {
			LOG.error("Ошибка обработки XML", e);
		} catch (UnsupportedEncodingException e1) {
			LOG.error("Ошибка обработки XML", e1);
		}

		return lObjectAddress;
	}

	private String getSubPlacesFromService(String sID_SubPlace_PB, String sFind) {
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
