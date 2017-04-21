package org.igov.service.business.object;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class ObjectPlaceTownUtil {
    private static final String URL_SERVICE_ROOT = "https://service-street.tech.igov.org.ua/AddressReference/address";
    private static final String URL_ITEM = URL_SERVICE_ROOT + "/item.do";
    private static final String URL_LIST_ADDRESS = URL_SERVICE_ROOT + "/listAddressByType.do";

    public static void main(String[] args) {
	Map<ObjectPlaceLang, List<Address>> mAddress = new HashMap<ObjectPlaceLang, List<Address>>();

	// Получаем области
	List<Address> lAddress = getOblastByLang(ObjectPlaceLang.UAN);
	mAddress.put(ObjectPlaceLang.UAN, lAddress);

	lAddress = getOblastByLang(ObjectPlaceLang.RUS);
	mAddress.put(ObjectPlaceLang.RUS, lAddress);

	// Получаем адреса по областям
	Map<ObjectPlaceLang, List<Address>> mTown = new HashMap<ObjectPlaceLang, List<Address>>();
	for (ObjectPlaceLang lang : mAddress.keySet()) {
	    lAddress = mAddress.get(lang);
	    System.out.printf("Язык: %s\n", lang);

	    mTown.put(lang, new ArrayList<>());

	    for (Address address : lAddress) {
		System.out.printf("Область: %s\n", address.code);
		mTown.get(lang).addAll(getListAddress(address));
	    }
	}

	// Вывод областей в файлы
	for (ObjectPlaceLang lang : mAddress.keySet()) {
	    WriteToCSV("ObjectPlaceOblast_", lang, mAddress.get(lang));
	}
	
	// Вывод адресов в файлы
	for (ObjectPlaceLang lang : mTown.keySet()) {
	    WriteToCSV("ObjectPlaceTown_", lang, mTown.get(lang));
	}

	System.out.println("Работа успешно завершена");
    }

    private static void WriteToCSV(String sFileNameRoot, ObjectPlaceLang lang, List<Address> lAddress) {
	String fileName = System.getProperty("user.home") + File.separator + sFileNameRoot + lang.name() + ".csv";
	System.out.printf("Вывод данных в файл: %s\n", fileName);

	// Определяем файл
	File file = new File(fileName);

	try {
	    // проверяем, что если файл не существует то создаем его
	    if (!file.exists()) {
		file.createNewFile();
	    }

	    try (PrintWriter out = new PrintWriter(file.getAbsoluteFile())) {
		out.print(
			"id;codeParent;code;desc;innerId;langCode;nodeTypeCode;nodeTypeId;nodeTypeName;nodeTypeSName;typeCodeId;zip\n");

		int i = 0;
		for (Address address : lAddress) {
		    i++;

		    StringBuffer sb = new StringBuffer();
		    sb.append(i);
		    sb.append(";");
		    sb.append(address.codeParent);
		    sb.append(";");
		    sb.append(address.code);
		    sb.append(";");
		    sb.append(address.desc);
		    sb.append(";");
		    sb.append(address.innerId);
		    sb.append(";");
		    sb.append(address.langCode);
		    sb.append(";");
		    sb.append(address.nodeTypeCode);
		    sb.append(";");
		    sb.append(address.nodeTypeId);
		    sb.append(";");
		    sb.append(address.nodeTypeName);
		    sb.append(";");
		    sb.append(address.nodeTypeSName);
		    sb.append(";");
		    sb.append(address.typeCodeId);
		    sb.append(";");
		    sb.append(address.zip);
		    sb.append("\n");

		    out.print(sb);
		}
	    }
	} catch (IOException e) {
	    throw new RuntimeException(e);
	}
    }

    // https://service-street.tech.igov.org.ua/AddressReference/address/listAddressByType.do?id=UA40773&type=4&&language=UAN
    private static List<Address> getListAddress(Address oblast) {
	String sUrl = URL_LIST_ADDRESS + "?id=" + oblast.code + "&language=" + oblast.langCode + "&type=4";
	String ret = "";

	HttpURLConnection oHttpURLConnection = null;
	try {
	    URL oURL = new URL(sUrl);
	    oHttpURLConnection = (HttpURLConnection) oURL.openConnection();
	    oHttpURLConnection.setRequestMethod("GET");
	    oHttpURLConnection.setRequestProperty("Content-Type", "text/xml; charset=UTF-8");

	    int responseCode = oHttpURLConnection.getResponseCode();
	    try (BufferedReader oBufferedReader = new BufferedReader(
		    new InputStreamReader(oHttpURLConnection.getInputStream()))) {

		StringBuilder os = new StringBuilder();
		String s;

		while ((s = oBufferedReader.readLine()) != null) {
		    os.append(s);
		}

		ret = os.toString();

	    } catch (java.io.FileNotFoundException e) {
		System.out.println(e);
	    }

	} catch (MalformedURLException e) {
	    System.out.println(e);
	} catch (IOException e) {
	    System.out.println(e);
	} finally {
	    if (oHttpURLConnection != null) {
		oHttpURLConnection.disconnect();
	    }
	}

	// System.out.printf("url=%s\n%s\n", sUrl, ret);

	return getListAddressFromString(oblast.code, ret);
    }

    private static List<Address> getListAddressFromString(String codeRoot, String sAddr) {
	List<Address> lAddress = new ArrayList<>();

	try {
	    DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
	    DocumentBuilder builder = factory.newDocumentBuilder();

	    StringBuilder xmlStringBuilder = new StringBuilder();
	    xmlStringBuilder.append(sAddr);
	    ByteArrayInputStream input = new ByteArrayInputStream(xmlStringBuilder.toString().getBytes("UTF-8"));

	    try {
		Document doc = builder.parse(input);

		NodeList nodes = ((org.w3c.dom.Document) doc).getElementsByTagName("address");
		for (int i = 0; i < nodes.getLength(); i++) {
		    Element element = (Element) nodes.item(i);

		    String code = element.getAttribute("code");
		    String desc = element.getAttribute("desc");
		    String innerId = element.getAttribute("innerId");
		    String langCode = element.getAttribute("langCode");
		    String nodeTypeCode = element.getAttribute("nodeTypeCode");
		    String nodeTypeId = element.getAttribute("nodeTypeId");
		    String nodeTypeName = element.getAttribute("nodeTypeName");
		    String nodeTypeSName = element.getAttribute("nodeTypeSName");
		    String typeCodeId = element.getAttribute("typeCodeId");
		    String zip = element.getAttribute("zip");

		    // Игнор Крыма и Севастополя
		    if (innerId.equals("UKR001") || innerId.equals("UKR00R")) {
			// System.out.printf("Путин ху#ло\n");
		    } else {
			// System.out.printf(
			// "code=%7s, desc=%20s, innerId=%s, langCode=%s,
			// nodeTypeCode=%s, nodeTypeId=%s, nodeTypeName=%8s,
			// nodeTypeSName=%8s, typeCodeId=%s, zip=%s\n",
			// code, desc, innerId, langCode, nodeTypeCode,
			// nodeTypeId, nodeTypeName, nodeTypeSName,
			// typeCodeId, zip);

			Address address = new Address(codeRoot, code, desc, innerId, langCode, nodeTypeCode, nodeTypeId,
				nodeTypeName, nodeTypeSName, typeCodeId, zip);
			lAddress.add(address);
		    }

		}

	    } catch (SAXException | IOException e) {
		System.out.println(e);
	    }

	} catch (ParserConfigurationException e) {
	    System.out.println(e);
	} catch (UnsupportedEncodingException e1) {
	    System.out.println(e1);
	}

	return lAddress;
    }

    private static List<Address> getOblastByLang(ObjectPlaceLang lang) {
	String sUrl = URL_ITEM + "?id=UA40773&language=" + lang + "&all=false&exclusions=false";
	String ret = "";

	HttpURLConnection oHttpURLConnection = null;
	try {
	    URL oURL = new URL(sUrl);
	    oHttpURLConnection = (HttpURLConnection) oURL.openConnection();
	    oHttpURLConnection.setRequestMethod("GET");
	    oHttpURLConnection.setRequestProperty("Content-Type", "text/xml; charset=UTF-8");

	    int responseCode = oHttpURLConnection.getResponseCode();
	    try (BufferedReader oBufferedReader = new BufferedReader(
		    new InputStreamReader(oHttpURLConnection.getInputStream()))) {

		StringBuilder os = new StringBuilder();
		String s;

		while ((s = oBufferedReader.readLine()) != null) {
		    os.append(s);
		}

		ret = os.toString();

	    } catch (java.io.FileNotFoundException e) {
		System.out.println(e);
	    }

	} catch (MalformedURLException e) {
	    System.out.println(e);
	} catch (IOException e) {
	    System.out.println(e);
	} finally {
	    if (oHttpURLConnection != null) {
		oHttpURLConnection.disconnect();
	    }
	}

//	System.out.printf("url=%s\n%s\n", sUrl, ret);

	return getListAddressFromString("UA40773", ret);
    }

}

class Address {
    String codeParent = null;

    String code = "";
    String desc = "";
    String innerId = "";
    String langCode = "";
    String nodeTypeCode = "";
    String nodeTypeId = "";
    String nodeTypeName = "";
    String nodeTypeSName = "";
    String typeCodeId = "";
    String zip = "";

    public Address(String codeRoot, String code, String desc, String innerId, String langCode, String nodeTypeCode,
	    String nodeTypeId, String nodeTypeName, String nodeTypeSName, String typeCodeId, String zip) {

	this.codeParent = codeRoot;
	this.code = code;
	this.desc = desc;
	this.innerId = innerId;
	this.langCode = langCode;
	this.nodeTypeCode = nodeTypeCode;
	this.nodeTypeId = nodeTypeId;
	this.nodeTypeName = nodeTypeName;
	this.nodeTypeSName = nodeTypeSName;
	this.typeCodeId = typeCodeId;
	this.zip = zip;

    }
}
