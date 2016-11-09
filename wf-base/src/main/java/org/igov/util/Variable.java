/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.igov.util;

import java.util.List;
import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.delegate.Expression;

/**
 *
 * @author Belyavtsev Vladimir Vladimirovich (BW)
 */
public class Variable {
    
    private String sName;
    private String sClassName;
    private Object oValue = null;
    private Object oValueCasted = null;

    public Variable(String sName, Object oValue) {
        this.sName = sName;
        this.oValue = oValue;
        castValue(this);//oParameter
    }
    
    public String getName() {
        return sName;
    }
    public Object getValue() {
        return oValueCasted!=null ? oValueCasted : oValue;
    }
    
    private void castValue(Variable oVariable) {
        String sFieldName = oVariable.sName;
        if (sFieldName == null || sFieldName.length() < 1)
            throw new IllegalArgumentException("incorrect fieldName (empty)!");
        //get mark
        String sMark = sFieldName.substring(0, 1);
        if (sMark.toLowerCase().equals(sMark) && sFieldName.length() > 1) {
            String sAfterMark = sFieldName.substring(0, 2);
            if (sAfterMark.toLowerCase().equals(sAfterMark)) {
                sMark = sAfterMark;
            }
        }
        switch (sMark) {
        case "n":
            oVariable.sClassName = "Long";
            oVariable.oValueCasted = new Long(oVariable.oValue.toString());
            break;
        case "b":
            oVariable.sClassName = "Boolean";
            oVariable.oValueCasted = new Boolean(oVariable.oValue.toString());
            break;
        case "as":
            oVariable.sClassName = "String[]";//"[S"
            String[] strings = new String[((List) oVariable.oValue).size()];
            int i = 0;
            for (Object value : ((List) oVariable.oValue)) {
                strings[i++] = value.toString();
            }
            oVariable.oValueCasted = strings;
            break;
        case "an":
            oVariable.sClassName = "String[]";//"[L"
            Long[] longs = new Long[((List) oVariable.oValue).size()];
            int j = 0;
            for (Object value : ((List) oVariable.oValue)) {
                longs[j++] = new Long(value.toString());
            }
            oVariable.oValueCasted = longs;
            break;
        case "s":
        default:
            oVariable.sClassName = "String";//"[S"
            oVariable.oValueCasted = oVariable.oValue.toString();
            break;
        }
    }

    public static boolean bString(String sName) {
        //LOG.info("sName",sName);
        if(sName==null || sName.length() == 0){
            return false;
        }
        //LOG.info("sName.charAt(0)",sName.charAt(0));
        if("s".equals(sName.charAt(0)+"")){//sName.startsWith("s")
            //LOG.info("(\"s\".equals={})",true);
            if (sName.length() > 1){
                //LOG.info("(sName.length() > 1={})",true);
                Character s = sName.toCharArray()[1];
                if(Character.isDigit(s)){
                    return true;
                }else if(Character.isLetter(s)){
                    //LOG.info("(Character.isLetter(s)={})",true);
                    if(Character.isUpperCase(s)){
                        //LOG.info("(Character.isUpperCase(s)={})",true);
                        return true;
                    }else{
                        return false;
                    }
                }else{
                    return false;
                }
            }else{
                return true;
            }
        }else{
            return false;
        }
    }
    
}
