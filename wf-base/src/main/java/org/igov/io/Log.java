/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.igov.io;

import java.util.HashMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Belyavtsev Vladimir Vladimirovich (BW)
 */
public class Log {
    private final static Logger LOG = LoggerFactory.getLogger(GeneralConfig.class);
    
    //public final static Logger oLogBig_Controller = LoggerFactory.getLogger(LogBig_Controller.class);
    //public final static Logger oLogBig_Interceptor = LoggerFactory.getLogger(LogBig_Interceptor.class);
    //public final static Logger oLogBig_Mail = LoggerFactory.getLogger(LogBig_Mail.class);
    //public final static Logger oLogBig_Web = LoggerFactory.getLogger(LogBig_Web.class);
    
    
    public final static Logger oLog_Alert = LoggerFactory.getLogger(Log_Alert.class);
    public final static Logger oLog_Error = LoggerFactory.getLogger(Log_Error.class);
    public final static Logger oLog_Info = LoggerFactory.getLogger(Log_Info.class);
    public final static Logger oLog_Debug = LoggerFactory.getLogger(Log_Debug.class);

    public final static Logger oLog_External = LoggerFactory.getLogger(Log_External.class);
    
    public enum LogStatus{
        ERROR()
        ,WARN()
        ,INFO()
        ,DEBUG()
        ;
    }
    
    private Logger oLog = null;
    private Exception oException = null;
    private LogStatus oStatus = null;
    private Integer nStatusHTTP = null;
    private Class<?> oClass = null;
    private String sCase = null;
    private String sHead = null;
    private String sBody = null;
    private HashMap<String, Object> mParam = new HashMap();
    private Boolean bLogOnly = false;
    private Boolean bThrow = false;
    
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
    public Log(Class oClass, Exception oException, Logger oLog){
        this(oClass, oException);
        _Log(oLog);
    };

    
    public Log _Reset(){
        oLog = null;
        oException = null;
        oStatus = null;
        nStatusHTTP = null;
        oClass = null;
        sCase = null;
        sHead = null;
        sBody = null;
        mParam = new HashMap();
        bLogOnly = false;
        bThrow = false;
        return this;
    }
    
    public Log _SendThrow(){
        sendThrow();
        return this;
    }
    public Log _Send(){
        send();
        return this;
    }

    /*public void sendTransit(){
        _SendTransit();
    }*/
    
    
    public String sText(){
        StringBuilder osText = new StringBuilder();
        osText.append(" | ").append(oStatus == null ? "" : oStatus.name()).append(" |");
        osText.append(" | ").append(oClass == null ? "" : oClass.getName()).append(" |");
        osText.append(" [").append(sCase == null ? "" : sCase).append("]");
        osText.append("{").append(nStatusHTTP == null ? "" : nStatusHTTP).append("}");
        //TODO: do params for standart slf4j: "Object[] os" and (param1={}, param1={})
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
    
    private void sendThrow(){
        _Transit();
        send();
    }
    
    public Log send(){
        try{
            String sText = sText();
            if(oStatus==LogStatus.ERROR){
                oLog_Alert.error(sText);
                if(oException!=null){
                    oLog_Error.error(sText,oException);
                    oLog_Debug.error(sText,oException);
                    if(oLog!=null){
                        oLog.error(sText,oException);
                    }
                }else{
                    oLog_Error.error(sText);
                    oLog_Debug.error(sText);
                    if(oLog!=null){
                        oLog.error(sText);
                    }
                }
            }else if(oStatus==LogStatus.WARN){
                oLog_Alert.warn(sText);
                if(oException!=null){
                    oLog_Debug.warn(sText,oException);
                    if(oLog!=null){
                        oLog.warn(sText,oException);
                    }
                }else{
                    oLog_Debug.warn(sText);
                    if(oLog!=null){
                        oLog.warn(sText);
                    }
                }
            }else if(oStatus==LogStatus.INFO){
                oLog_Info.info(sText);
                if(oException!=null){
                    oLog_Debug.info(sText,oException);
                    if(oLog!=null){
                        oLog.info(sText,oException);
                    }
                }else{
                    oLog_Debug.info(sText);
                    if(oLog!=null){
                        oLog.info(sText);
                    }
                }
            }else{
                if(oException!=null){
                    oLog_Debug.debug(sText,oException);
                    if(oLog!=null){
                        oLog.debug(sText,oException);
                    }
                }else{
                    oLog_Debug.debug(sText);
                    if(oLog!=null){
                        oLog.debug(sText);
                    }
                }
            }
            if(!bLogOnly){
                //TODO: Include bTransit
                //TODO: SEND TO ERROR-LOGGING-SYSTEM
            }
        }catch(Exception oException0){
            LOG.error("",oException0);
        }
        //_Reset();
        return this;
    }

    public Log _Status(LogStatus o){
        oStatus = o;
        return this;
    }
    
    
    final public Log _Log(Logger oLog){
        this.oLog = oLog;
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
        mParam.put(sName, oValue);
        return this;
    }
    
    public Log _LogOnly(){
        bLogOnly = true;
        return this;
    }
    
    private Log _Transit(){
        bThrow = true;
        return this;
    }

    
}
