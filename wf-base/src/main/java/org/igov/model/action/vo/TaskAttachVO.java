/*
{
"sID_StorageType": "Redis" //тип хранилища
, "sKey": "jHG986-7gOO-ho&H07-Hdfgy4-554yws" //ключ контента
, "sVersion": "2016-12-16" //строка-версия
, "sDateTime": "2016-12-16 23:23:48.001" //строка-дата и время (загрузки)
, "sFileNameExt": "passport.pdf" //строка названия и расширения файла
, "sContentType": "html/text" //строка с типом контента (по стандартам)
, "nBytes": 2345235 //Чисол байтов, которые занимает контент
, "bSigned": true //наложено или не наложено ЭЦП
, "aAttribute": [ //массив атрибутов
   {
      sID:"sVar1" //строка-ИД атрибута
      , sValue:"value1" //строка значения атрибута
   }
   , {
      sID:"nCount" //строка-ИД атрибута
      , sValue:"12" //строка значения атрибута
   }
]
} */
package org.igov.model.action.vo;

import java.util.List;
import java.util.Map;

/**
 *
 * @author Kovilin
 */
public class TaskAttachVO {
    
    private String sID_StorageType;
    private String sKey;
    private String sVersion;
    private String sDateTime;
    private String sFileNameExt;
    private String sContentType;
    private String nBytes;
    private boolean bSigned;
    
    private List<Map<String, Object>> aAttribute;

    public TaskAttachVO() {
    }

    public String getsID_StorageType() {
        return sID_StorageType;
    }

    public String getsKey() {
        return sKey;
    }

    public String getsVersion() {
        return sVersion;
    }

    public String getsDateTime() {
        return sDateTime;
    }

    public String getsFileNameExt() {
        return sFileNameExt;
    }

    public String getsContentType() {
        return sContentType;
    }

    public String getnBytes() {
        return nBytes;
    }

    public boolean isbSigned() {
        return bSigned;
    }

    public List<Map<String, Object>> getaAttribute() {
        return aAttribute;
    }

    public void setsID_StorageType(String sID_StorageType) {
        this.sID_StorageType = sID_StorageType;
    }

    public void setsKey(String sKey) {
        this.sKey = sKey;
    }

    public void setsVersion(String sVersion) {
        this.sVersion = sVersion;
    }

    public void setsDateTime(String sDateTime) {
        this.sDateTime = sDateTime;
    }

    public void setsFileNameExt(String sFileNameExt) {
        this.sFileNameExt = sFileNameExt;
    }

    public void setsContentType(String sContentType) {
        this.sContentType = sContentType;
    }

    public void setnBytes(String nBytes) {
        this.nBytes = nBytes;
    }

    public void setbSigned(boolean bSigned) {
        this.bSigned = bSigned;
    }

    public void setaAttribute(List<Map<String, Object>> aAttribute) {
        this.aAttribute = aAttribute;
    }
    
    
    
    
}
