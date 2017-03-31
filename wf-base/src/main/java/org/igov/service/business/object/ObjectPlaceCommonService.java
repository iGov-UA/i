package org.igov.service.business.object;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
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
import org.igov.util.cache.CachedInvocationBean;
import org.springframework.http.HttpHeaders;

@Component("ObjectPlaceCommonService")
@Service
public class ObjectPlaceCommonService {
	private static final Logger LOG = LoggerFactory.getLogger(ObjectPlaceCommonService.class);
	private static final String GET_SERVICE_SUB_PLACES_CACHE_KEY = "ObjectPlaceCommonService.getSubPlaces_";

	private static final String SUB_URL_ADDRESS_BY_TYPE = "/AddressReference/address/listAddressByType.do";
	private static final String SUB_URL_ADDRESS_BY_NAME = "/AddressReference/address/searchByName.do";
	private static final String NULL_RESPONSE = "{}";

	@Autowired
	GeneralConfig generalConfig;

	@Autowired
	private CachedInvocationBean cachedInvocationBean;
	    
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

	/*
	 * Возвращает список адресов в виде
	 * 
	 * {"listAddress":[
	 * 	{"code":"23TFD62IDSDX00","desc":"1-я Южная","type":"улица"},
	 * 	{"code":"23TFDOBMJPIO00","desc":"2-я Южная","type":"улица"}
	 * ]}
	 * 
	 * Адреса ищуться по коду места sID_SubPlace_PB, опционально можно отфильтровать результат указав подстроку в наименовании адреса.
	 * 
	 * Программа кеширует объекты запросов. Объекты заносяться в кеш - sID_SubPlace_PB, там они привязаны к ключу sID_SubPlace_PB.
	 * 
	 * Данные в кеш добавляются только если не задан, ограничивающий запрос, параметр sFind.
	 * 
	 */
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

		List<ObjectAddress> lObjectAddress = getListAddressesCached(sID_SubPlace_PB, sFind);

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

		LOG.info("Response:\n{}\n", sb.toString());

		return sb.toString();
	}
	
	
	private List<ObjectAddress> getListAddressesCached(String sID_SubPlace_PB, String sFind) {
	        return cachedInvocationBean.invokeUsingCache(new CachedInvocationBean.Callback<List<ObjectAddress>>(
	        	GET_SERVICE_SUB_PLACES_CACHE_KEY, sID_SubPlace_PB, sFind) {
	            @Override
	            public List<ObjectAddress> execute() {
	                return getListAddresses(sID_SubPlace_PB, sFind);
	            }
	        });
	}

	private List<ObjectAddress> getListAddresses(String sID_SubPlace_PB, String sFind) {
	    	LOG.info("Start request, sID_SubPlace_PB={}, sFind={}", sID_SubPlace_PB, sFind);

		List<ObjectAddress> lObjectAddress = null;
	    
		try {
			String xmlString = getSubPlacesFromService(sID_SubPlace_PB, sFind);
			lObjectAddress = parseDataFromXMLString(xmlString);
		} catch (Exception e) {
			LOG.error("ошибка получения данных сервиса", e);
		}
		
		return lObjectAddress;
	}
	
	
	
	/*
	 * Получение данных из сервиса. Документация:
	 * 
	 * https://docs.google.com/document/d/1SppaZgDRNU9LFbE4ngOIrg9Ebu83WkyJD4wHPP6g7fc/edit#heading=h.4ovb807m5oaa 
	 */
	private String getSubPlacesFromService(String sID_SubPlace_PB, String sFind) {
		LOG.debug("sID_SubPlace_PB={}, sFind={}", sID_SubPlace_PB, sFind);
		
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

	
	private List<ObjectAddress> parseDataFromXMLString(String sListAddressXML) {
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
					String type = element.getAttribute("nodeTypeName");

					ObjectAddress objectAddress = new ObjectAddress(code, desc, type);
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
	
}
