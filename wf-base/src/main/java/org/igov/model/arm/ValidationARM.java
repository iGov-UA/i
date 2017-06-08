package org.igov.model.arm;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.apache.commons.lang3.StringUtils;
import org.igov.model.arm.DboTkModel;

import com.google.common.collect.Lists;
public class ValidationARM {
	
	/**
	 * часто используемы форматы дат
	 */
	private final static List<String> formats = Lists.newArrayList("yyyy-MM-dd", "dd-MM-yyyy", "yyyy/MM/dd", "dd/MM/yyyy", "yyyy.MM.dd",
			"dd.MM.yyyy", "yyyyy-MM-dd HH:mm:ss", "yyyyy/MM/dd HH:mm:ss", "yyyyy.MM.dd HH:mm:ss");
	
	
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
    dataForTransferToArm.setPrilog(isValid(data.get("Prilog"))? isValidSizePrilog(data.get("Prilog")):null);
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
    dataForTransferToArm.setPrioritet(isValid(data.get("Prioritet"))? data.get("Prioritet"):null);
    dataForTransferToArm.setLongterm(isValid(data.get("Longterm"))? data.get("Longterm"):null);
    dataForTransferToArm.setId_corp(isValid(data.get("Id_corp"))? Integer.valueOf(data.get("Id_corp")):1);

	return dataForTransferToArm;
		
	}

}
