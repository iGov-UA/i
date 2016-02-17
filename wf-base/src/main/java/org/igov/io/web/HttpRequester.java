package org.igov.io.web;

import org.igov.io.Log;
import org.igov.io.GeneralConfig;
import org.igov.util.ToolWeb;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.RequestMethod;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Map;

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
        String saParam = "";

        if (mParam != null) {
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
            String sUser = generalConfig.sAuthLogin();
            String sPassword = generalConfig.sAuthPassword();
            String sAuth = ToolWeb.base64_encode(sUser + ":" + sPassword);
            oConnection.setRequestProperty("authorization", "Basic " + sAuth);

            oConnection.setRequestMethod(RequestMethod.POST.name());
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
            new Log(this.getClass(), oException)
                    ._Head("[post]:BREAKED!")
                    ._Status(Log.LogStatus.ERROR)
                    //._StatusHTTP(nStatus)
                    ._Param("sURL", sURL)
                    ._Param("saParam", saParam)
                    ._SendThrow();
            LOG.error("BREAKED: {} (sURL={},saParam={})", oException.getMessage(), sURL, saParam);
            //oLogBig_Web.error("BREAKED: {} (sURL={},saParam={})",oException.getMessage(),sURL,saParam);
            LOG_BIG.error("BREAKED: {} (sURL={},saParam={})", oException.getMessage(), sURL, saParam);
            LOG_BIG.debug("BREAKED:", oException);
            throw oException; //return null;
        }
        if (nStatus != 200) {
            new Log(this.getClass(), null, LOG)
                    ._Head("[post]:nStatus!=200")
                    ._Status(Log.LogStatus.ERROR)
                    ._StatusHTTP(nStatus)
                    ._Param("nStatus", nStatus)
                    ._Param("sURL", sURL)
                    ._Param("saParam", saParam)
                    ._Param("osReturn", osReturn)
                    ._SendThrow();
            if (bExceptionOnNorSuccess) {
                throw new Exception("nStatus=" + nStatus + "sURL=" + sURL + "saParam=" + saParam + "osReturn=" + osReturn);
            }
        }
        return osReturn.toString();
    }

    public String getInside(String sURL, Map<String, String> mParam) throws Exception {
        URL oURL = new URL(getFullURL(sURL, mParam));
        InputStream oInputStream;
        BufferedReader oBufferedReader_InputStream;
        HttpURLConnection oConnection;
        Integer nStatus;
        StringBuilder osReturn = new StringBuilder();
        try {

            oConnection = (HttpURLConnection) oURL.openConnection();

            String sUser = generalConfig.sAuthLogin();
            String sPassword = generalConfig.sAuthPassword();
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
            oBufferedReader_InputStream = new BufferedReader(new InputStreamReader(oInputStream));
            String sLine;
            while ((sLine = oBufferedReader_InputStream.readLine()) != null) {
                osReturn.append(sLine);
            }
            oInputStream.close();

            LOG.info("FINISHED! (nStatus={},sURL={},mParam={},osReturn={})", nStatus, sURL, sCut(100, mParam.toString()), sCut(100, osReturn.toString()));
            LOG.debug("FINISHED! (nStatus={},sURL={},mParam={},osReturn={})", nStatus, sURL, mParam, osReturn);

        } catch (Exception oException) {
            new Log(this.getClass(), oException)
                    ._Head("[get]:BREAKED!")
                    ._Status(Log.LogStatus.ERROR)
                    //._StatusHTTP(nStatus)
                    ._Param("sURL", sURL)
                    ._Param("mParam", mParam)
                    ._Send();
            LOG.error("BREAKED: {} (sURL={},mParam={})", oException.getMessage(), sURL, mParam);
            //oLogBig_Web.error("BREAKED: {} (sURL={},mParam={})",oException.getMessage(),sURL,mParam);
            LOG_BIG.error("BREAKED: {} (sURL={},mParam={})", oException.getMessage(), sURL, mParam);
            LOG_BIG.debug("BREAKED:", oException);
            throw oException; //return null;
        }
        if (nStatus != 200) {
            new Log(this.getClass())
                    ._Head("[get]:nStatus!=200")
                    ._Status(Log.LogStatus.ERROR)
                    //._StatusHTTP(nStatus)
                    ._Param("nStatus", nStatus)
                    ._Param("sURL", sURL)
                    ._Param("mParam", mParam)
                    ._Param("osReturn", osReturn)
                    ._Send();
            if (bExceptionOnNorSuccess) {
                throw new Exception("nStatus=" + nStatus + "sURL=" + sURL + "mParam=" + mParam + "osReturn=" + osReturn);
            }
        }
        return osReturn.toString();
    }
    
    public byte[] getInsideBytes(String sURL, Map<String, String> mParam) throws Exception {
        URL oURL = new URL(getFullURL(sURL, mParam));
        InputStream oInputStream;
        byte[] res;
        HttpURLConnection oConnection;
        Integer nStatus;
        try {

            oConnection = (HttpURLConnection) oURL.openConnection();

            String sUser = generalConfig.sAuthLogin();
            String sPassword = generalConfig.sAuthPassword();
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
            new Log(this.getClass(), oException)
                    ._Head("[get]:BREAKED!")
                    ._Status(Log.LogStatus.ERROR)
                    //._StatusHTTP(nStatus)
                    ._Param("sURL", sURL)
                    ._Param("mParam", mParam)
                    ._Send();
            LOG.error("BREAKED: {} (sURL={},mParam={})", oException.getMessage(), sURL, mParam);
            //oLogBig_Web.error("BREAKED: {} (sURL={},mParam={})",oException.getMessage(),sURL,mParam);
            LOG_BIG.error("BREAKED: {} (sURL={},mParam={})", oException.getMessage(), sURL, mParam);
            LOG_BIG.debug("BREAKED:", oException);
            throw oException; //return null;
        }
        if (nStatus != 200) {
            new Log(this.getClass())
                    ._Head("[get]:nStatus!=200")
                    ._Status(Log.LogStatus.ERROR)
                    //._StatusHTTP(nStatus)
                    ._Param("nStatus", nStatus)
                    ._Param("sURL", sURL)
                    ._Param("mParam", mParam)
                    ._Param("bytes", res != null ? res.length : "null")
                    ._Send();
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
