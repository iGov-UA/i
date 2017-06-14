package org.igov.model.arm;

import java.io.InputStream;
import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.igov.service.conf.AttachmetService;
import org.json.simple.parser.JSONParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Lists;
public class ValidationARM {
	
	private final static Logger LOG = LoggerFactory.getLogger(ValidationARM.class);
	
	/**
	 * часто используемы форматы дат
	 */
	private final static List<String> formats = Lists.newArrayList("yyyy-MM-dd", "dd-MM-yyyy", "yyyy/MM/dd", "dd/MM/yyyy", "yyyy.MM.dd",
			"dd.MM.yyyy", "yyyy-MM-dd HH:mm:ss", "yyyy/MM/dd HH:mm:ss", "yyyy.MM.dd HH:mm:ss", "EEE MMM dd HH:mm:ss zzz yyyy");
	
	
	/**
	 * метод проверки параметра на налл, пустоту
	 * @param pvalue
	 * @return
	 */
	public static boolean isValid(String pvalue) {
		  return StringUtils.isNotEmpty(pvalue) && Objects.nonNull(pvalue)
				  && !pvalue.equals("null");
		}

	
	/**
	 * проверка размера поля Prilog - временно
	 * @param data
	 * @return
	 */
	public static String isValidSizePrilog(String data) {
		String prilog = "";
		if (isValid(data)) {
			if (data.length() > 200) {
				prilog = data.substring(0, 150) + " ...";
			} else {
				prilog = data;
			}
		}
		return prilog;
	}
	
	public static String isValidSizeOne(String data) {
		String param = "";
		if (isValid(data)) {
			if (data.length() > 1) {
				param = data.substring(0, 1);
			} else {
				param = data;
			}
		}
		return param;
	}
	
	/**
	 * метод принимающий на вход массив форматов дат и парсит String to Date
	 * @param dateString
	 * @param formats
	 * @return
	 */
	public static Date parseDate(String dateString, List<String> formats) {
	  Date date = null;
	  boolean success = false;

	  for (String format:formats)
	  {
	    SimpleDateFormat dateFormat = new SimpleDateFormat(format);

	    try
	    {
	      date = dateFormat.parse(dateString);
	      success = true;
	      break;
	    }
	    catch(ParseException e)
	    {
	    	 return new Date();
	    }
	  }

	  return date;
	}
	
	/**
	 * метод парсинга строки которая приходит на вход бп арм
	 * @param soData_Value_Result
	 * @return
	 */
    public static Map<String, String> parseDataARM(String soData_Value_Result){ 
        Map<String, String> data = new HashMap<String, String>();
        String[] aDataSplit = soData_Value_Result.split(";;");
        String key, value;
        for (String dataSplit : aDataSplit) {
            String[] keyValue = dataSplit.split("::");
            if (keyValue != null && keyValue.length > 0) {
                key = keyValue[0];
                if (keyValue.length == 1) {
                    value = "";
                } else {
                    value = keyValue[1];
                }
                data.put(key, value);
            }
        }
        return data;
    }
    
/**
 * заполнение модели
 * @param soData_Value_Result
 * @return
 */
public static DboTkModel fillModel(String soData_Value_Result){
		
	Map<String, String> data = parseDataARM(soData_Value_Result);
	DboTkModel dataForTransferToArm = new DboTkModel();
	dataForTransferToArm.setIndustry(isValid(data.get("Industry")) ? data.get("Industry"):null);
	dataForTransferToArm.setPriznak(isValid(data.get("Priznak"))? data.get("Priznak"):null);
	dataForTransferToArm.setData_out(isValid(data.get("Data_out")) ? parseDate(data.get("Data_out"),formats):null);
    dataForTransferToArm.setDep_number(isValid(data.get("Dep_number")) ? data.get("Dep_number") : "");
    dataForTransferToArm.setData_in(isValid(data.get("Data_in")) ? parseDate(data.get("Data_in"),formats): null);
    dataForTransferToArm.setState(isValid(data.get("State")) ? data.get("State"):null);
    dataForTransferToArm.setName_object(isValid(data.get("Name_object")) ? data.get("Name_object"):null);
    dataForTransferToArm.setKod(isValid(data.get("Kod")) ? data.get("Kod"): null);
    dataForTransferToArm.setGruppa(isValid(data.get("Gruppa")) ? data.get("Gruppa"):null);
    dataForTransferToArm.setUndergroup(isValid(data.get("Undergroup"))? data.get("Undergroup"):null);
    dataForTransferToArm.setFinans(isValid(data.get("Finans"))? data.get("Finans"):null);
    dataForTransferToArm.setData_out_raz(isValid(data.get("Data_out_raz")) ? parseDate(data.get("Data_out_raz"),formats): null);
    dataForTransferToArm.setNumber_442(isValid(data.get("Number_442"))?Integer.valueOf(data.get("Number_442")):null);
    dataForTransferToArm.setNumber_441(isValid(data.get("Number_441"))?Integer.valueOf(data.get("Number_441")):null);
    dataForTransferToArm.setWinner(isValid(data.get("Winner"))? data.get("Winner"):null);
    dataForTransferToArm.setKod_okpo(isValid(data.get("Kod_okpo"))? data.get("Kod_okpo"):null);
    dataForTransferToArm.setPhone(isValid(data.get("Phone"))? data.get("Phone"):null);
    dataForTransferToArm.setSrok(isValid(data.get("Srok"))? data.get("Srok"):null);
    dataForTransferToArm.setExpert(isValid(data.get("Expert"))? data.get("Expert"):null);
    dataForTransferToArm.setSumma(isValid(data.get("Summa"))?new BigDecimal( data.get("Summa")):BigDecimal.ZERO);
    dataForTransferToArm.setuAN(isValid(data.get("UAN"))? data.get("UAN"):null);
    dataForTransferToArm.setIf_oplata(isValid(data.get("If_oplata"))? data.get("If_oplata"):null);
    dataForTransferToArm.setUslovie(isValid(data.get("Uslovie"))? data.get("Uslovie"):null);
    dataForTransferToArm.setBank(isValid(data.get("Bank"))? data.get("Bank"):null);
    dataForTransferToArm.setSmeta(isValid(data.get("Smeta"))? data.get("Smeta"):null);
    dataForTransferToArm.setDataEZ(isValid(data.get("DataEZ")) ? parseDate(data.get("DataEZ"),formats): null);
    dataForTransferToArm.setPrilog(isValid(data.get("Prilog"))? data.get("Prilog"):null);
    dataForTransferToArm.setUpdateData(isValid(data.get("UpdateData")) ? parseDate(data.get("UpdateData"),formats): null);
    dataForTransferToArm.setUpdOKBID(isValid(data.get("UpdOKBID"))?Integer.valueOf(data.get("UpdOKBID")):null);
    dataForTransferToArm.setNotes(isValid(data.get("Notes"))? data.get("Notes"):null);
    dataForTransferToArm.setArhiv(isValid(data.get("Arhiv"))? data.get("Arhiv"):null);
    dataForTransferToArm.setCreateDate(isValid(data.get("CreateDate")) ? parseDate(data.get("CreateDate"),formats): new Date());
    dataForTransferToArm.setZametki(isValid(data.get("Zametki"))? data.get("Zametki"):null);
    dataForTransferToArm.setDataBB(isValid(data.get("DataBB")) ? parseDate(data.get("DataBB"),formats): new Date());
    dataForTransferToArm.setPriemka(isValid(data.get("Priemka"))? data.get("Priemka"):null);
    dataForTransferToArm.setProckred(isValid(data.get("Prockred"))? data.get("Prockred"):null);
    dataForTransferToArm.setSumkred(isValid(data.get("Sumkred"))?new BigDecimal( data.get("Sumkred")):BigDecimal.ZERO);
    dataForTransferToArm.setSumzak(isValid(data.get("Sumzak"))?new BigDecimal( data.get("Sumzak")):BigDecimal.ZERO);
    dataForTransferToArm.setAuctionForm(isValid(data.get("AuctionForm"))? data.get("AuctionForm"):null);
    dataForTransferToArm.setProtocol_Number(isValid(data.get("Protocol_Number"))? data.get("Protocol_Number"):null);
    dataForTransferToArm.setCorrectionDoc(isValid(data.get("CorrectionDoc"))? data.get("CorrectionDoc"):null);
    dataForTransferToArm.setPrioritet(isValid(data.get("Prioritet"))? isValidSizeOne(data.get("Prioritet")):null);
    dataForTransferToArm.setLongterm(isValid(data.get("Longterm"))? isValidSizeOne(data.get("Longterm")):null);
    dataForTransferToArm.setId_corp(isValid(data.get("Id_corp"))? Integer.valueOf(data.get("Id_corp")):1);
    dataForTransferToArm.setOut_number(isValid(data.get("Out_number"))? data.get("Out_number"):"");

	return dataForTransferToArm;
		
	}


/**
 * Получение значения поля Prilog - должно одержать имена атачей прикрепленных
 * @param data
 * @param oAttachmetService
 * @return
 */
public static String getPrilog(String data, AttachmetService oAttachmetService) {
	String listStringPrilog = ", ";
	if (ValidationARM.isValid(data)) {
		org.json.simple.JSONObject oJSONObject = null;
		try {
			JSONParser parser = new JSONParser();

			org.json.simple.JSONObject oTableJSONObject = (org.json.simple.JSONObject) parser.parse(data);

			InputStream oAttachmet_InputStream = oAttachmetService.getAttachment(null, null,
					(String) oTableJSONObject.get("sKey"), (String) oTableJSONObject.get("sID_StorageType"))
					.getInputStream();

			oJSONObject = (org.json.simple.JSONObject) parser
					.parse(IOUtils.toString(oAttachmet_InputStream, "UTF-8"));
			LOG.info("oTableJSONObject in listener Transfer_ARM: " + oJSONObject.toJSONString());
			 org.json.simple.JSONArray aJsonRow = (org.json.simple.JSONArray) oJSONObject.get("aRow");

                if (aJsonRow != null) {
                	List<String> listPrilogName = new ArrayList<String>();
                    for (int i = 0; i < aJsonRow.size(); i++) {
                        org.json.simple.JSONObject oJsonField = (org.json.simple.JSONObject) aJsonRow.get(i);
                        LOG.info("oJsonField in {}", oJsonField);
                        if (oJsonField != null) {
                            org.json.simple.JSONArray aJsonField = (org.json.simple.JSONArray) oJsonField.get("aField");
                            LOG.info("aJsonField in getPrilog is {}", aJsonField);
                            if (aJsonField != null) {
                                for (int j = 0; j < aJsonField.size(); j++) {
                                    org.json.simple.JSONObject oJsonMap = (org.json.simple.JSONObject) aJsonField
                                            .get(j);
                                    LOG.info("oJsonMap in getPrilog is {}", oJsonMap);
                                    if (oJsonMap != null) {
                                        Object fileName = oJsonMap.get("fileName");
                                            if (fileName != null) {
                                                LOG.info("oValue in getPrilog is {}", fileName);
                                                listPrilogName.add((String) fileName);
                                            } else {
                                                LOG.info("oValue in getPrilog is null");
                                            }
                                    }
                                }
                            }
                        }
                    }
                    listStringPrilog = String.join(", ", listPrilogName);
                } else {
                    LOG.info("JSON array is null in getPrilog is null");
                }
		} catch (Exception e) {
			LOG.error("oTableJSONObject in listener Transfer_ARM: " + oJSONObject.toJSONString());
		}
	}
	return listStringPrilog;
}

/**
 * Получение значения поля Expert - которое содержит имена исполнителей
 * 
 * @param data
 * @param oAttachmetService
 * @return
 */
public static List<String> getAsExecutors(String data, AttachmetService oAttachmetService, String sID_FieldTable) {
	List<String> listExecutors = new ArrayList<String>();
	if (ValidationARM.isValid(data)) {
		org.json.simple.JSONObject oJSONObject = null;
		try {
			JSONParser parser = new JSONParser();

			org.json.simple.JSONObject oTableJSONObject = (org.json.simple.JSONObject) parser.parse(data);

			InputStream oAttachmet_InputStream = oAttachmetService.getAttachment(null, null,
					(String) oTableJSONObject.get("sKey"), (String) oTableJSONObject.get("sID_StorageType"))
					.getInputStream();

			oJSONObject = (org.json.simple.JSONObject) parser
					.parse(IOUtils.toString(oAttachmet_InputStream, "UTF-8"));
			LOG.info("oTableJSONObject in listener Update_ARM: " + oJSONObject.toJSONString());
			org.json.simple.JSONArray aJsonRow = (org.json.simple.JSONArray) oJSONObject.get("aRow");

			if (aJsonRow != null) {

				for (int i = 0; i < aJsonRow.size(); i++) {
					org.json.simple.JSONObject oJsonField = (org.json.simple.JSONObject) aJsonRow.get(i);
					LOG.info("oJsonField in  Update_ARM: {}", oJsonField);
					if (oJsonField != null) {
						org.json.simple.JSONArray aJsonField = (org.json.simple.JSONArray) oJsonField.get("aField");
						LOG.info("aJsonField in getExpert is {}", aJsonField);
						if (aJsonField != null) {
							for (int j = 0; j < aJsonField.size(); j++) {
								org.json.simple.JSONObject oJsonMap = (org.json.simple.JSONObject) aJsonField
										.get(j);
								LOG.info("oJsonMap in getExpert is {}", oJsonMap);
								if (oJsonMap != null) {
									Object oId = oJsonMap.get("id");
									if (((String) oId).equals(sID_FieldTable)) {
										Object oValue = oJsonMap.get("value");
										if (oValue != null) {
											LOG.info("oValue in cloneDocumentStepFromTable is {}", oValue);
											listExecutors.add((String) oValue);
										} else {
											LOG.info("oValue in cloneDocumentStepFromTable is null");
										}
									}
								}
							}
						}
					}
				}

			} else {
				LOG.info("JSON array is null in getExpert is null");
			}
		} catch (Exception e) {
			LOG.error("oTableJSONObject in listener Update_ARM: " + oJSONObject.toJSONString());
		}
	}
	return listExecutors;
}


}
