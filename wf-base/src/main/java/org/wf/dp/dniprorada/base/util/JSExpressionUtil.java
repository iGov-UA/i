package org.wf.dp.dniprorada.base.util;

import java.util.List;
import java.util.Map;

import javax.script.Invocable;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

import org.apache.log4j.Logger;

public class JSExpressionUtil {

	private static final Logger log = Logger.getLogger(JSExpressionUtil.class);
	
	public boolean getResultOfCondition(Map<String, Object> jsonData,
            Map<String, Object> taskData,
            String sCondition)
            throws ClassNotFoundException, ScriptException, NoSuchMethodException {

        Object res = getObjectResultOfCondition(jsonData, taskData, sCondition);
        Boolean result = (Boolean) res;
        log.info(">>>>------SCRIPT RESULT=" + result);
        if (!result.booleanValue()){
        	log.info("jsonData:" + jsonData);
        }
        return result;
    }

	public Object getObjectResultOfCondition(Map<String, Object> jsonData,
			Map<String, Object> taskData, String sCondition)
			throws ScriptException, NoSuchMethodException {
		ScriptEngineManager manager = new ScriptEngineManager();
        ScriptEngine engine = manager.getEngineByName("JavaScript");
        //----put parameters---
        log.info("json parameter:");
        for (String key : jsonData.keySet()) {
            //chaeck are present in sCondition??
            Parameter parameter = new Parameter(key, jsonData.get(key));
            castValue(parameter);
            log.info(parameter.name + "=" + parameter.castValue);
            engine.put(parameter.name, parameter.castValue);
            jsonData.put(parameter.name, parameter.castValue);
        }
        for (String key : taskData.keySet()) {
            engine.put(key, taskData.get(key));
        }
        ///---eval script and invoke result----
        String script = getJavaScriptStr(sCondition);
        log.info(">>>>------SCRIPT:");
        log.info(script);
        engine.eval(script);
        Invocable inv = (Invocable) engine;
        inv.invokeFunction("getResult");
        Object res = inv.invokeFunction("getResult");
		return res;
	}

    private String getJavaScriptStr(String sCondition) {
        return "function getResult() { " +
                "   return "
                + sCondition //  "   sUserTask=='1' && (new Date()-new Date(sDateEdit))/1000/60/60/24 > nDays"
                + ";}";
    }

    private void castValue(Parameter parameter) {
        String fieldName = parameter.name;
        if (fieldName == null || fieldName.length() < 1)
            throw new IllegalArgumentException("incorrect fieldName (empty)!");
        //get mark
        String mark = fieldName.substring(0, 1);
        if (mark.toLowerCase().equals(mark) && fieldName.length() > 1) {
            String mark_2 = fieldName.substring(0, 2);
            if (mark_2.toLowerCase().equals(mark_2)) {
                mark = mark_2;
            }
        }
        switch (mark) {
        case "n":
            parameter.className = "Long";
            parameter.castValue = new Long(parameter.value.toString());
            break;
        case "b":
            parameter.className = "Boolean";
            parameter.castValue = new Boolean(parameter.value.toString());
            break;
        case "as":
            parameter.className = "String[]";//"[S"
            String[] strings = new String[((List) parameter.value).size()];
            int i = 0;
            for (Object value : ((List) parameter.value)) {
                strings[i++] = value.toString();
            }
            parameter.castValue = strings;
            break;
        case "an":
            parameter.className = "String[]";//"[L"
            Long[] longs = new Long[((List) parameter.value).size()];
            int j = 0;
            for (Object value : ((List) parameter.value)) {
                longs[j++] = new Long(value.toString());
            }
            parameter.castValue = longs;
            break;
        case "s":
        default:
            parameter.className = "String";//"[S"
            parameter.castValue = parameter.value.toString();
            break;
        }
    }

    class Parameter {
        String name;
        String className;
        Object value;
        Object castValue;

        public Parameter(String name, Object value) {
            this.name = name;
            this.value = value;
        }
    }
	
}
