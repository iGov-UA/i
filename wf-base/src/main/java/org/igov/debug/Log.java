/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.igov.debug;

import java.util.HashMap;
import org.igov.io.GeneralConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Belyavtsev Vladimir Vladimirovich (BW)
 */
public class Log {
    private final static Logger oLog = LoggerFactory.getLogger(GeneralConfig.class);
    
    public final static Logger oLogBig_Controller = LoggerFactory.getLogger(LogBig_Controller.class);
    public final static Logger oLogBig_Interceptor = LoggerFactory.getLogger(LogBig_Interceptor.class);
    public final static Logger oLogBig_Mail = LoggerFactory.getLogger(LogBig_Mail.class);
    public final static Logger oLogBig_Web = LoggerFactory.getLogger(LogBig_Web.class);
    
    
    public final static Logger oLog_Alert = LoggerFactory.getLogger(Log_Alert.class);
    public final static Logger oLog_Error = LoggerFactory.getLogger(Log_Error.class);
    public final static Logger oLog_Info = LoggerFactory.getLogger(Log_Info.class);
    public final static Logger oLog_Debug = LoggerFactory.getLogger(Log_Debug.class);
    
    public enum LogStatus{
        ERROR()
        ,WARN()
        ,INFO()
        ,DEBUG()
        ;
    }
    
    private Exception oException = null;
    private LogStatus oStatus = null;
    private Integer nStatusHTTP = null;
    private Class<?> oClass = null;
    private String sCase = null;
    private String sHead = null;
    private String sBody = null;
    private HashMap<String, Object> mParam = new HashMap();
    private Boolean bLogOnly = false;
    
    public Log(){};

    public Log(Exception o){
        _Exception(o);
    };

    public Log(Class o){
        _Class(o);
    };
    public Log(Class oClass, Exception oException){
        _Class(oClass);
        _Exception(oException);
    };

    
    public Log _Reset(){
        oException = null;
        oStatus = null;
        nStatusHTTP = null;
        oClass = null;
        sCase = null;
        sHead = null;
        sBody = null;
        mParam = new HashMap();
        bLogOnly = false;
        return this;
    }
    
    public Log _Send(){
        send();
        //_Reset();
        return this;
    }
    
    public String sText(){
        StringBuilder osText = new StringBuilder();
        osText.append(" | ").append(oStatus == null ? "" : oStatus.name()).append(" |");
        osText.append(" | ").append(oClass == null ? "" : oClass.getName()).append(" |");
        osText.append(" [").append(sCase == null ? "" : sCase).append("]");
        osText.append("{").append(nStatusHTTP == null ? "" : nStatusHTTP).append("}");
        osText.append("(").append(mParam == null ? "" : mParam).append(")");
        osText.append(":").append(sHead != null ? sHead + (oException!=null ? " " + oException.getMessage() : "") : oException!=null ? oException.getMessage() : "");
        if(sBody!=null){
            osText.append("\n").append(sBody);
        }
        return osText.toString();
    }

    public Exception oException(){
        if(oException!=null){
            return new Exception(sText(),oException);
        }else{
            return new Exception(sText());
        }
    }

    public Integer nStatusHTTP(){
        return nStatusHTTP;
    }
    
    public Log send(){
        try{
            String sText = sText();
            if(oStatus==LogStatus.ERROR){
                oLog_Alert.error(sText);
                if(oException!=null){
                    oLog_Error.error(sText,oException);
                    oLog_Debug.error(sText,oException);
                }else{
                    oLog_Error.error(sText);
                    oLog_Debug.error(sText);
                }
            }else if(oStatus==LogStatus.WARN){
                oLog_Alert.warn(sText);
                if(oException!=null){
                    oLog_Debug.warn(sText,oException);
                }else{
                    oLog_Debug.warn(sText);
                }
            }else if(oStatus==LogStatus.INFO){
                oLog_Info.info(sText);
                if(oException!=null){
                    oLog_Debug.info(sText,oException);
                }else{
                    oLog_Debug.info(sText);
                }
            }else{
                if(oException!=null){
                    oLog_Debug.debug(sText,oException);
                }else{
                    oLog_Debug.debug(sText);
                }
            }
            if(!bLogOnly){
                //TODO: SEND TO ERROR-LOGGING-SYSTEM
            }
        }catch(Exception oException0){
            oLog.error("[send]:",oException0);
        }
        return this;
    }

    public Log _Status(LogStatus o){
        oStatus = o;
        return this;
    }
    
    final public Log _Exception(Exception o){
        oException = o;
        return this;
    }

    final public Log _Class(Class o){
        oClass = o;
        return this;
    }

    public Log _Case(String s){
        sCase = s;
        return this;
    }
    
    public Log _Head(String s){
        sHead = s;
        return this;
    }

    public Log _Body(String s){
        sBody = s;
        return this;
    }

    public Log _StatusHTTP(Integer n){
        nStatusHTTP = n;
        return this;
    }
    
    public Log _Param(String sName, Object oValue){
        mParam.put(sHead, oValue);
        return this;
    }
    
    public Log _LogOnly(){
        bLogOnly = true;
        return this;
    }
    

}
