package org.igov.util;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.script.Invocable;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import static org.igov.util.Tool.sCut;
import static org.igov.util.Variable.bString;

import org.slf4j.Logger;import org.slf4j.LoggerFactory;

public class ToolJS {

    private static final Logger LOG = LoggerFactory.getLogger(ToolJS.class);

    public boolean getResultOfCondition(Map<String, Object> jsonData, Map<String, Object> taskData, String sCondition
        ) throws ClassNotFoundException, ScriptException, NoSuchMethodException {

        Object oReturn = getObjectResultOfCondition(jsonData, taskData, sCondition);
        Boolean bReturn = (Boolean) oReturn;
        LOG.debug("(bReturn={})", bReturn);
        return bReturn;
    }

    public Object getObjectResultOfCondition(Map<String, Object> mParamUncasted, Map<String, Object> mParamCasted, String sCondition
        ) throws ScriptException, NoSuchMethodException {
        ScriptEngineManager oScriptEngineManager = new ScriptEngineManager();
        ScriptEngine oScriptEngine = oScriptEngineManager.getEngineByName("JavaScript");
        //Map<String, Object> mParam = new HashMap();
        for (String sKey : mParamUncasted.keySet()) {
            Variable oVariable = new Variable(sKey, mParamUncasted.get(sKey));
            LOG.debug("{}={}", oVariable.getName(), oVariable.getValue());
            //oScriptEngine.put(oVariable.name, oVariable.castValue);
            //mParamUncasted.put(oVariable.name, oVariable.castValue);
            //mParam.put(oVariable.name, oVariable.castValue);
            oScriptEngine.put(oVariable.getName(), oVariable.getValue());
        }
        for (String sKey : mParamCasted.keySet()) {
            //mParam.put(sKey, mParamCasted.get(sKey));
            LOG.info("mParamCasted put: {}, {}", sKey, mParamCasted.get(sKey));
            oScriptEngine.put(sKey, mParamCasted.get(sKey));
        }
        LOG.info("sCondition is {}", sCondition);
        String sFunctionJS = getJavaScriptStr(sCondition);
        LOG.info("sFunctionJS is {}",sFunctionJS);
        oScriptEngine.eval(sFunctionJS);
        Invocable oInvocable = (Invocable) oScriptEngine;
        oInvocable.invokeFunction("getResult");
        Object oReturn = oInvocable.invokeFunction("getResult");
        return oReturn;
    }

    private String getJavaScriptStr(String sCondition) {//  "   sUserTask=='1' && (new Date()-new Date(sDateEdit))/1000/60/60/24 > nDays"
        return new StringBuilder("function getResult() { return ").append(sCondition).append(";}").toString();
    }

    public static String getCalculatedFormulaValue(String sFormulaOriginal, Map<String, Object> mParam) {//String
        String sReturn = null;
        String sFormula=sFormulaOriginal;
        if(sFormula==null || "".equals(sFormula.trim())){
            LOG.warn("(sFormula={},mParam(short)={})",sFormula, sCut(50, mParam.toString()));
            //oLogBig_Controller.warn("(sFormula={},mParam(short)={})",sFormula, mParam.toString());
        }else{
            for (Map.Entry<String, ?> oParam : mParam.entrySet()) {
                String sName = oParam.getKey();
                //LOG.info("sName",sName);
                if(sName != null){
                    //LOG.info("sName != null",true);
                    String sValue = oParam.getValue() == null ? "" : (String)oParam.getValue();
                    //LOG.info("sValue",sValue);
                    if(bString(sName)){
                        //LOG.info("(bString(sName)={})",true);
                        sValue = "'" + sValue + "'";
                        sFormula = sFormula.replaceAll("\\Q'["+sName+"]'\\E",sValue);
                        sFormula = sFormula.replaceAll("\\Q["+sName+"]\\E",sValue);
                    }else{
                        sFormula = sFormula.replaceAll("\\Q["+sName+"]\\E",sValue);
                    }
                    sFormula = sFormula.replaceAll("\\Q\n\\E","");
                    sFormula = sFormula.replaceAll("\\Q\r\\E","");
                }
            }
            sFormula=sFormula.substring(1);
            try{
                Map<String, Object> m = new HashMap<String, Object>();
                Object o = new ToolJS().getObjectResultOfCondition(m, mParam, sFormula); //getResultOfCondition
                sReturn = "" + o;
//                LOG.debug("(sFormulaOriginal={},sFormula={},mParam(cuted)={},sReturn={})",sFormulaOriginal,sFormula, sCut(50, mParam.toString()),sReturn);
                //oLogBig_Controller.info("(sFormulaOriginal={},sFormula={},mParam={},sReturn={})",sFormulaOriginal,sFormula, mParam,sReturn);
            }catch(Exception oException){
                LOG.error("FAIL: {} (sFormulaOriginal={},sFormula={},mParam(cuted)={},sReturn={})", oException.getMessage(), sFormulaOriginal, sFormula, sCut(50, mParam.toString()),sReturn);
                LOG.debug("FAIL:", oException);
                //oLogBig_Controller.error("FAIL: {} (sFormulaOriginal={},sFormula={},mParam(cuted)={},sReturn={})", oException.getMessage(), sFormulaOriginal, sFormula, mParam.toString(),sReturn);
            }
        }
        return sReturn;
    }
    
    
}
