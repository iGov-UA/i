package org.igov.io.web;

import org.igov.io.Log;
import org.igov.io.GeneralConfig;
import org.igov.util.ToolWeb;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.RequestMethod;

import groovy.json.StringEscapeUtils;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.security.SecureRandom;
import java.security.cert.X509Certificate;
import java.util.HashMap;
import java.util.Map;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import org.activiti.engine.impl.util.json.JSONObject;
import org.apache.commons.io.IOUtils;

import static org.igov.util.Tool.sCut;

public class HttpRequester {

    static final transient Logger LOG = LoggerFactory.getLogger(HttpRequester.class);
    private static final Logger LOG_BIG = LoggerFactory.getLogger("WebBig");

    @Autowired
    GeneralConfig generalConfig;

    private final boolean bExceptionOnNorSuccess = true;

    public String postInside(String sURL, Map<String, Object> mParam)
            throws Exception {
        return postInside(sURL, mParam, null, null);
    }

    public String postInside(String sURL, Map<String, Object> mParam, String sParam, String contentType)
            throws Exception {

        boolean bSkipValidationSSL = generalConfig.isSelfTest();
        simplifySSLConnection(bSkipValidationSSL);

        String saParam = "";
        if (sParam != null) {
            saParam = sParam;
        } else if (mParam != null) {
            for (Map.Entry<String, Object> entry : mParam.entrySet()) {
                if (entry.getValue() != null) {
                    String entryValue;
                    if (entry.getValue() instanceof String) {
                        entryValue = (String) entry.getValue();
                    } else {
                        entryValue = new JSONObject(entry.getValue()).toString();
                    }
                    saParam += URLEncoder.encode(entry.getKey(), "UTF-8") + "="
                            + URLEncoder.encode(entryValue, "UTF-8") + "&";
                }
            }
        }

        URL oURL = new URL(sURL);
        Integer nStatus = null;
        StringBuilder osReturn = new StringBuilder();
        try {
            HttpURLConnection oConnection = (HttpURLConnection) oURL.openConnection();
            String sUser = generalConfig.getAuthLogin();
            String sPassword = generalConfig.getAuthPassword();
            String sAuth = ToolWeb.base64_encode(sUser + ":" + sPassword);
            oConnection.setRequestProperty("authorization", "Basic " + sAuth);

            oConnection.setRequestMethod(RequestMethod.POST.name());
            if(contentType != null){
                oConnection.setRequestProperty("Content-Type", contentType);
            } else{
                oConnection.setRequestProperty("Content-Type", "text/plain");
            }
            
            oConnection.setDoOutput(true);
            DataOutputStream oDataOutputStream = new DataOutputStream(oConnection.getOutputStream());
            // Send post request
            oDataOutputStream.writeBytes(saParam);
            oDataOutputStream.flush();
            oDataOutputStream.close();

            InputStream oInputStream;
            if (oConnection.getResponseCode() >= HttpStatus.BAD_REQUEST.value()) {
                oInputStream = oConnection.getErrorStream();
            } else {
                oInputStream = oConnection.getInputStream();
            }
            BufferedReader oBufferedReader_InputStream = new BufferedReader(new InputStreamReader(oInputStream));
            nStatus = oConnection.getResponseCode();
            String sLine;

            while ((sLine = oBufferedReader_InputStream.readLine()) != null) {
                osReturn.append(sLine);
            }
            oBufferedReader_InputStream.close();

            LOG.info("FINISHED! (nStatus={},sURL={},saParam(cuted)={},osReturn(cuted)={})", nStatus, sURL, sCut(100, saParam), sCut(100, osReturn.toString()));
            LOG_BIG.debug("FINISHED! (nStatus={},sURL={},saParam={},osReturn={})", nStatus, sURL, saParam, osReturn);
        } catch (Exception oException) {
            new Log(oException, LOG)
                    ._Case("Web_PostSelf")
                    ._Head("[post]:BREAKED!")
                    //                    ._Body(oException.getMessage())
                    ._Status(Log.LogStatus.ERROR)
                    //._StatusHTTP(nStatus)
                    ._Param("sURL", sURL)
                    ._Param("saParam", saParam)
                    ._LogTransit()
                    ._LogTrace()
                    .save();
            LOG.error("BREAKED: {} (sURL={},saParam={})", oException.getMessage(), sURL, saParam);
            //oLogBig_Web.error("BREAKED: {} (sURL={},saParam={})",oException.getMessage(),sURL,saParam);
            LOG_BIG.error("BREAKED: {} (sURL={},saParam={})", oException.getMessage(), sURL, saParam);
            LOG_BIG.debug("BREAKED:", oException);
            throw oException; //return null;
        }
        if (nStatus != 200) {
            new Log(this.getClass(), LOG)
                    ._Case("Web_PostSelfNo200")
                    ._Head("[post]:nStatus!=200")
                    ._Status(Log.LogStatus.ERROR)
                    ._StatusHTTP(nStatus)
                    ._Param("nStatus", nStatus)
                    ._Param("sURL", sURL)
                    ._Param("saParam", saParam)
                    ._Param("osReturn", osReturn)
                    ._LogTransit()
                    .save();
            if (bExceptionOnNorSuccess) {
                throw new Exception("nStatus=" + nStatus + "sURL=" + sURL + "saParam=" + saParam + "osReturn=" + osReturn);
            }
        }
        return osReturn.toString();
    }

    public String getInside(String sURL, Map<String, String> mParam) throws Exception {

        boolean bSkipValidationSSL = generalConfig.isSelfTest();
        simplifySSLConnection(bSkipValidationSSL);

        String requestMethod = RequestMethod.GET.name();
        if (mParam.containsKey("RequestMethod")) {
            requestMethod = mParam.get("RequestMethod");
            mParam.remove("RequestMethod");
        }
        URL oURL = null;
        if (RequestMethod.GET.name().equals(requestMethod)) {
            oURL = new URL(getFullURL(sURL, mParam));
        } else {
            Map<String, String> params = new HashMap<String, String>();
            params.put("sID_Order", mParam.remove("sID_Order"));
            params.put("nID_SubjectMessageType", mParam.remove("nID_SubjectMessageType"));
            params.put("sBody", mParam.remove("sBody"));
            oURL = new URL(getFullURL(sURL, params));
        }
        InputStream oInputStream;
        BufferedReader oBufferedReader_InputStream;
        HttpURLConnection oConnection;

        //HttpsURLConnection.verify(
        Integer nStatus;
        StringBuilder osReturn = new StringBuilder();
        try {

            URLConnection oConnectAbstract = oURL.openConnection();
            /*if (oConnectAbstract instanceof HttpsURLConnection) {
                //simplifySSLConnection(bSkipValidationSSL ? null : (HttpsURLConnection) oConnectAbstract);
                simplifySSLConnection(bSkipValidationSSL, (HttpsURLConnection) oConnectAbstract);
            }*/

            //oConnection = (HttpURLConnection) oURL.openConnection();
            oConnection = (HttpURLConnection) oConnectAbstract;

            String sUser = generalConfig.getAuthLogin();
            String sPassword = generalConfig.getAuthPassword();
            String sAuth = ToolWeb.base64_encode(sUser + ":" + sPassword);
            oConnection.setRequestProperty("authorization", "Basic " + sAuth);
            if (RequestMethod.POST.name().equals(requestMethod)) {
                for (Map.Entry<String, String> curr : mParam.entrySet()) {
                    oConnection.setRequestProperty(curr.getKey(), StringEscapeUtils.escapeJava(curr.getValue()));
                }
            }

            oConnection.setRequestMethod(requestMethod);
            oConnection.setDoInput(true);
            oConnection.setDoOutput(true);
            nStatus = oConnection.getResponseCode();//???
            if (oConnection.getResponseCode() >= HttpStatus.BAD_REQUEST.value()) {
                oInputStream = oConnection.getErrorStream();
            } else {
                oInputStream = oConnection.getInputStream();
            }
            oBufferedReader_InputStream = new BufferedReader(new InputStreamReader(oInputStream));
            String sLine;
            while ((sLine = oBufferedReader_InputStream.readLine()) != null) {
                osReturn.append(sLine);
            }
            oInputStream.close();

            LOG.info("FINISHED! (nStatus={},sURL={},mParam={},osReturn={})", nStatus, sURL, sCut(100, mParam.toString()), sCut(100, osReturn.toString()));
            LOG.debug("FINISHED! (nStatus={},sURL={},mParam={},osReturn={})", nStatus, sURL, mParam, osReturn);

        } catch (Exception oException) {
            new Log(oException, LOG)
                    ._Case("Web_GetSelf")
                    ._Head("[get]:BREAKED!")
                    //                    ._Body(oException.getMessage())
                    ._Status(Log.LogStatus.ERROR)
                    //._StatusHTTP(nStatus)
                    ._Param("sURL", sURL)
                    ._Param("mParam", mParam)
                    ._LogTrace()
                    .save();
            LOG.error("BREAKED: {} (sURL={},mParam={})", oException.getMessage(), sURL, mParam);
            //oLogBig_Web.error("BREAKED: {} (sURL={},mParam={})",oException.getMessage(),sURL,mParam);
            LOG_BIG.error("BREAKED: {} (sURL={},mParam={})", oException.getMessage(), sURL, mParam);
            LOG_BIG.debug("BREAKED:", oException);
            throw oException; //return null;
        }
        if (nStatus != 200) {
            new Log(this.getClass(), LOG)
                    ._Case("Web_GetSelfNo200")
                    ._Head("[get]:nStatus!=200")
                    ._Status(Log.LogStatus.ERROR)
                    //._StatusHTTP(nStatus)
                    ._Param("nStatus", nStatus)
                    ._Param("sURL", sURL)
                    ._Param("mParam", mParam)
                    ._Param("osReturn", osReturn)
                    .save();
            if (bExceptionOnNorSuccess) {
                throw new Exception("nStatus=" + nStatus + "sURL=" + sURL + "mParam=" + mParam + "osReturn=" + osReturn);
            }
        }
        return osReturn.toString();
    }

    /**
     * Веривикация сертификата при HTTPS-соединении.
     *
     * @param oConnectHTTPS соединение (если null, то верификация будет
     * пропущенна)
     */
    //public void simplifySSLConnection(HttpsURLConnection oConnectHTTPS) {
    //public void simplifySSLConnection(boolean bSkip, HttpsURLConnection oConnectHTTPS) {
    public void simplifySSLConnection(boolean bSkip) {
        if (bSkip) {
            LOG.info("Skip Sertificate!");
            /*javax.net.ssl.HttpsURLConnection.setDefaultHostnameVerifier(
                new javax.net.ssl.HostnameVerifier(){

                    public boolean verify(String hostname,
                            javax.net.ssl.SSLSession sslSession) {
                        //if (hostname.equals("localhost")) {
                            return true;
                        //}
                        //return false;
                    }
                });            
             */
 /*oConnectHTTPS.setHostnameVerifier(new HostnameVerifier() {
                @Override
                public boolean verify(String arg0, SSLSession arg1) {
                    return true;
                }
            });*/

            TrustManager[] trustAllCerts = {
                new X509TrustManager() {
                    public X509Certificate[] getAcceptedIssuers() {
                        return null;
                    }

                    public void checkClientTrusted(X509Certificate[] certs, String authType) {
                    }

                    public void checkServerTrusted(X509Certificate[] certs, String authType) {
                    }
                }
            };
            try {
                SSLContext oSSLContext = SSLContext.getInstance("SSL");
                oSSLContext.init(null, trustAllCerts, new SecureRandom());
                HttpsURLConnection.setDefaultSSLSocketFactory(oSSLContext.getSocketFactory());
            } catch (Exception oException) {
                //_RiseWarn(oException, "simplifySSLConnection", "", "Fail getting SSLContext");
                LOG.warn("simplifySSLConnection. Fail getting SSLContext: " + oException.getMessage());
            }
            HostnameVerifier oHostnameVerifier = new HostnameVerifier() {
                public boolean verify(String urlHostName, SSLSession session) {
                    //_RiseWarn("simplifySSLConnection", "URL Host(urlHostName)=" + urlHostName, " vs. " + session.getPeerHost());
                    LOG.warn("simplifySSLConnection." + "(URL Host(urlHostName)=" + urlHostName, " vs. " + session.getPeerHost() + "): ");
                    return true;
                }
            };
            HttpsURLConnection.setDefaultHostnameVerifier(oHostnameVerifier);

        }
        /*if (oConnectHTTPS != null) {
            //HttpsURLConnection oConnectHTTPS = (HttpsURLConnection) oConnectAbstract;
            oConnectHTTPS.setHostnameVerifier(new HostnameVerifier() {
                @Override
                public boolean verify(String arg0, SSLSession arg1) {
                    return true;
                }
            });
        } else {
            TrustManager[] trustAllCerts = {
                new X509TrustManager() {
                    public X509Certificate[] getAcceptedIssuers() {
                        return null;
                    }

                    public void checkClientTrusted(X509Certificate[] certs, String authType) {
                    }

                    public void checkServerTrusted(X509Certificate[] certs, String authType) {
                    }
                }
            };
            try {
                SSLContext oSSLContext = SSLContext.getInstance("SSL");
                oSSLContext.init(null, trustAllCerts, new SecureRandom());
                HttpsURLConnection.setDefaultSSLSocketFactory(oSSLContext.getSocketFactory());
            } catch (Exception oException) {
                _RiseWarn(oException, "simplifySSLConnection", "", "Fail getting SSLContext");
            }
            HostnameVerifier oHostnameVerifier = new HostnameVerifier() {
                public boolean verify(String urlHostName, SSLSession session) {
                    _RiseWarn("simplifySSLConnection", "URL Host(urlHostName)=" + urlHostName, " vs. " + session.getPeerHost());
                    return true;
                }
            };
            HttpsURLConnection.setDefaultHostnameVerifier(oHostnameVerifier);
        }*/
    }

    public byte[] getInsideBytes(String sURL, Map<String, String> mParam) throws Exception {
        URL oURL = new URL(getFullURL(sURL, mParam));
        InputStream oInputStream;
        byte[] res;
        HttpURLConnection oConnection;
        Integer nStatus;
        try {

            oConnection = (HttpURLConnection) oURL.openConnection();

            String sUser = generalConfig.getAuthLogin();
            String sPassword = generalConfig.getAuthPassword();
            String sAuth = ToolWeb.base64_encode(sUser + ":" + sPassword);
            oConnection.setRequestProperty("authorization", "Basic " + sAuth);

            oConnection.setRequestMethod(RequestMethod.GET.name());
            oConnection.setDoInput(true);
            oConnection.setDoOutput(true);
            nStatus = oConnection.getResponseCode();//???
            if (oConnection.getResponseCode() >= HttpStatus.BAD_REQUEST.value()) {
                oInputStream = oConnection.getErrorStream();
            } else {
                oInputStream = oConnection.getInputStream();
            }
            res = IOUtils.toByteArray(oInputStream);
            oInputStream.close();

            LOG.info("FINISHED! (nStatus={},sURL={},mParam={},bytes size={})", nStatus, sURL, sCut(100, mParam.toString()), res.length);
            LOG.debug("FINISHED! (nStatus={},sURL={},mParam={},bytes size={})", nStatus, sURL, mParam, res.length);

        } catch (Exception oException) {
            new Log(oException, LOG)
                    ._Case("Web_GetSelf")
                    ._Head("[get]:BREAKED!")
                    //                    ._Body(oException.getMessage())
                    ._Status(Log.LogStatus.ERROR)
                    //._StatusHTTP(nStatus)
                    ._Param("sURL", sURL)
                    ._Param("mParam", mParam)
                    ._LogTrace()
                    .save();
            LOG.error("BREAKED: {} (sURL={},mParam={})", oException.getMessage(), sURL, mParam);
            //oLogBig_Web.error("BREAKED: {} (sURL={},mParam={})",oException.getMessage(),sURL,mParam);
            LOG_BIG.error("BREAKED: {} (sURL={},mParam={})", oException.getMessage(), sURL, mParam);
            LOG_BIG.debug("BREAKED:", oException);
            throw oException; //return null;
        }
        if (nStatus != 200) {
            new Log(this.getClass(), LOG)
                    ._Case("Web_GetSelfNo200")
                    ._Head("[get]:nStatus!=200")
                    ._Status(Log.LogStatus.ERROR)
                    //._StatusHTTP(nStatus)
                    ._Param("nStatus", nStatus)
                    ._Param("sURL", sURL)
                    ._Param("mParam", mParam)
                    ._Param("bytes", res != null ? res.length : "null")
                    .save();
            if (bExceptionOnNorSuccess) {
                throw new Exception("nStatus=" + nStatus + "sURL=" + sURL + "mParam=" + mParam + "bytes=" + res.length);
            }
        }
        return res;
    }

    public String getFullURL(String sURL, Map<String, String> mParam) throws UnsupportedEncodingException {
        String saParam = "";
        if (mParam != null) {
            for (Map.Entry<String, String> entry : mParam.entrySet()) {
                if (entry.getValue() != null) {
                    saParam += entry.getKey() + "="
                            + URLEncoder.encode(entry.getValue(), "UTF-8") + "&";
                }
            }
        }

        String sFullURL = sURL;
        if (saParam.length() > 0) {
            sFullURL += "?" + saParam;
        }
        return sFullURL;
    }
}
