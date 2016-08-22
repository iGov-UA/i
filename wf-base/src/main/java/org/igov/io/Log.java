/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.igov.io;

import java.io.PrintWriter;
import java.io.StringWriter;
import org.igov.service.business.msg.MsgService;
import org.igov.service.business.msg.MsgType;
import org.igov.service.exception.CommonUtils;

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

    private MsgType oMsgType = null;
    
    private Logger oLog = null;
    private Exception oException = null;
    private LogStatus oStatus = null;
    private Integer nStatusHTTP = null;
    private Class<?> oClass = null;
    private String sCase = null;
    private String sHead = null;
    private String sBody = null;
    private HashMap<String, Object> mParam = new HashMap();
    private Boolean bLogOnly = null;
    private Boolean bLogTransit = false;
    private Boolean bLogTrace = false;
    private String sTextException=null;
    
    private Long nID_Subject = null;
    private Long nID_Server_Custom = null;
    
    public Log(){};

    public Log(Exception o){
        _Exception(o);
    };

    public Log(Class o){
        _Class(o);
    };
    
    public Log(Class oClass, Exception oException){
        _Exception(oException);
        _Class(oClass);
    };
    public Log(Class oClass, Exception oException, Logger oLog){
        this(oClass, oException);
        _Log(oLog);
    };

    public Log(Exception o, Logger oLog){
        _Exception(o);
        _Log(oLog);
    };

    public Log(Class oClass, Logger oLog){
        _Class(oClass);
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
        bLogOnly = null;
        bLogTransit = false;
        bLogTrace = false;
        sTextException="";
        nID_Subject = null;
        nID_Server_Custom = null;
        return this;
    }
    
    /*public Log _SaveThrow(){
        //saveThrow();
        _LogTransit();
        save();
        return this;
    }*/
    /*public Log _Save(){
        save();
        return this;
    }*/

    /*public void sendTransit(){
        _SendTransit();
    }*/
    
    public Class oClassByTrace(Exception oException){//StackTraceElement[] oStackTraceElement
        Class oClassReturn = null;
        String sTextLineFirst="";
        String sTextLine="";
        if(oException!=null){
            int n=0;
            for(StackTraceElement oStackTraceElement : oException.getStackTrace()){
                //String sPackage = oStackTraceElement.getClass().getPackage().getName();
                String sClass = oStackTraceElement.getClassName();
                String s = oStackTraceElement.toString();
                //String sClassCanonical = oStackTraceElement.getClass().getCanonicalName();
                //String sClassSimple = oStackTraceElement.getClass().getSimpleName();
                String sMethod = oStackTraceElement.getMethodName();
                String sFile = oStackTraceElement.getFileName();
                //LOG.info("sPackage={},sClass={},sClassCanonical={}, sClassSimple={},sMethod={},sFile={}", sPackage, sClass, sClassCanonical, sClassSimple, sMethod, sFile);
                LOG.info("sClass={},s={},sMethod={},sFile={}", sClass, s, sMethod, sFile);
                //if(sPackage!=null && sPackage.startsWith("org.igov")){
                if(s!=null && s.contains("org.igov.")){
                    break;
                }
                n++;
            }
            if(n>=oException.getStackTrace().length){
                n=0;
            }
            if(oException.getStackTrace().length>0){
                StackTraceElement oStackTrace = oException.getStackTrace()[n];
                if(oStackTrace!=null){
                    oClassReturn = oStackTrace.getClass();
                    int nLine = oStackTrace.getLineNumber();
                    String sClass = oStackTrace.getClassName();
                    String sFileName = oStackTrace.getFileName();
                    String sMethod = oStackTrace.getMethodName();
                    //LOG.error("Error:{}. REST API Exception", exception.getMessage());//
                    if(n>0){
                        StackTraceElement oStackTraceFirst = oException.getStackTrace()[0];
                        String sClassFirst = oStackTraceFirst.getClassName();
                        String sMethodFirst = oStackTraceFirst.getMethodName();
                        sTextLineFirst=new StringBuilder().append(sClassFirst).append(".").append(sMethodFirst).toString();
                    }
                    sTextLine=new StringBuilder(sTextLineFirst).append("/").append(n).append(")").append(sClass).append(".").append(sMethod).append("(").append(nLine).append("):").toString();
                    sTextException=new StringBuilder(sTextLine).append(oException.getMessage()).toString();
                    LOG.info("(sClass={},sMethod={},sFileName={}):{}",sClass,sMethod,sFileName, oException.getMessage());
                    LOG.info(sTextException);
                    //return oClass!=null?oClass:ExceptionCommonController.class; //0//this.getClass()
                }else{
                    sTextException="oStackTrace=null";
                    LOG.warn("oStackTrace!=null");
                }
            }else{
                sTextException="getStackTrace().length=0";
            }
        }
        return oClassReturn; //0//this.getClass()//!=null?oClass:ExceptionCommonController.class
    }

    
    public String sText(){
        StringBuilder osText = new StringBuilder();
        osText.append(" ").append(oStatus == null ? "" : oStatus.name()).append("");
        osText.append(" ").append(oClass == null || sTextException != null  ? "" : "_" + oClass.getName()).append(" ");
        osText.append(" [").append(sCase == null ? "" : sCase).append("]");
        osText.append("{").append(nStatusHTTP == null ? "" : nStatusHTTP).append("}");
        //TODO: do params for standart slf4j: "Object[] os" and (param1={}, param1={})
        osText.append("(").append(mParam == null ? "" : mParam).append(")");
        //osText.append(":").append(sHead != null ? sHead + (oException!=null ? " " + oException.getMessage() : "") : oException!=null ? oException.getMessage() : "");
        if(sTextException!=null){
            osText.append(":").append((sHead != null ? sHead + " " : "") + sTextException);
        }
        if(sBody!=null){
            osText.append("\n").append(sBody);
        }
        return osText.toString();
    }

    /*public String sTextException(){
        if(oException!=null){
            oClass = oClassByTrace(oException);
        }
    }*/
    
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
    
    /*private void saveThrow(){
        _LogTransit();
        save();
    }*/
    
    private void sendToMSG(MsgType msgType){
        HashMap<String, Object> m = new HashMap(mParam);
        //m.put("sNote", sTextSend());
        if(sTextException!=null){
            m.put("sException", sTextException);
        }
        if(nStatusHTTP!=null){
            m.put("nStatusHTTP", nStatusHTTP);
        }

	MsgService.send(oMsgType!=null ? oMsgType.name() : msgType.name()
                , nID_Subject
                , nID_Server_Custom
                , sCase!=null ? sCase : (oClass == null ? "NULL" : oClass.getName())
                , sHead
                , sBody
                , CommonUtils.getStringStackTrace(oException)
                , m //mParam
        );
    }
    
    public Log _MsgType(MsgType o){
         this.oMsgType = o;
         return this;
    }
    
    public Log save(){
        try{
            String sText = sText();
            if(oStatus==LogStatus.ERROR){
                oLog_Alert.error(sText);
                if(oException!=null && bLogTrace){
                    oLog_Error.error(sText,oException);
                    oLog_Debug.error(sText,oException);
                    if(oLog!=null){
                        //oLog.error("FAIL:", oException);
                        oLog.error(sText, oException);
                    }
                }else{
                    oLog_Error.error(sText);
                    oLog_Debug.error(sText);
                    if(oLog!=null){
                        oLog.error(sText);
                    }
                }
                if(bLogOnly==null){
                    bLogOnly=false;
                }
                if(!bLogOnly){
                    sendToMSG(MsgType.INTERNAL_ERROR);
                }
            }else if(oStatus==LogStatus.WARN && bLogTrace){
                oLog_Alert.warn(sText);
                if(oException!=null && bLogTrace){
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
                if(bLogOnly==null){
                    bLogOnly=false;
                }
        	//sendToMSG(MsgType.WARNING);
                if(!bLogOnly){
                    sendToMSG(MsgType.WARNING);
                }
            }else if(oStatus==LogStatus.INFO){
                oLog_Info.info(sText);
                if(oException!=null && bLogTrace){
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
                if(bLogOnly==null){
                    bLogOnly=true;
                }
                if(!bLogOnly){
                    sendToMSG(MsgType.INF_MESSAGE);
                }
            }else{
                if(oException!=null && bLogTrace){
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
            //if(!bLogOnly){
                //TODO: Include bTransit
                //TODO: SEND TO ERROR-LOGGING-SYSTEM
            //}
        }catch(Exception oException0){
            LOG.error("Ошибка логирования ошибки!!!",oException0);
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
        Class oClassNew = oClassByTrace(oException);
        if(oClass==null){
            oClass = oClassNew;
        }
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
    
    public Log _LogTransit(){
        bLogTransit = true;
        return this;
    }
    
    public Log _LogTrace(){
        bLogTrace = true;
        return this;
    }
    
    public Log _SubjectID(Long n){
        nID_Subject = n;
        return this;
    }
    public Log _ServerID_Custom(Long n){
        nID_Server_Custom = n;
        return this;
    }
    
}
