package org.igov.io.web;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.RequestMethod;
import org.igov.io.liqpay.LiqBuyUtil;
import org.igov.io.GeneralConfig;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Map;
import org.igov.debug.Log;
import static org.igov.debug.Log.oLogBig_Web;
import static org.igov.util.Util.sCut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HttpRequester {

    static final transient Logger LOG = LoggerFactory.getLogger(HttpRequester.class);
    
    @Autowired
    GeneralConfig generalConfig;

    public String post(String sURL, Map<String, String> mParam)
            throws Exception {
        String saParam = "";

        if (mParam != null) {
            for (Map.Entry<String, String> entry : mParam.entrySet()) {
                if (entry.getValue() != null) {
                    saParam += entry.getKey() + "="
                            + URLEncoder.encode(entry.getValue(), "UTF-8") + "&";
                }
            }
        }

        URL oURL = new URL(sURL);
        Integer nStatus = null;
        StringBuilder osReturn = new StringBuilder();
        try{
            HttpURLConnection oConnection = (HttpURLConnection) oURL.openConnection();
            String sUser = generalConfig.sAuthLogin();
            String sPassword = generalConfig.sAuthPassword();
            String sAuth = LiqBuyUtil.base64_encode(sUser + ":" + sPassword);
            oConnection.setRequestProperty("authorization", "Basic " + sAuth);

            oConnection.setRequestMethod(RequestMethod.POST.name());
            oConnection.setDoOutput(false);
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
            
            if(nStatus!=200){
                new Log(this.getClass())
                        ._Head("[post]:nStatus!=200")
                        ._Status(Log.LogStatus.ERROR)
                        ._StatusHTTP(nStatus)
                        ._Param("nStatus", nStatus)
                        ._Param("sURL", sURL)
                        ._Param("saParam", saParam)
                        ._Param("osReturn", osReturn)
                        ._SendThrow()
                        ;
            }
            LOG.info("FINISHED! (nStatus={},sURL={},saParam(cuted)={},osReturn(cuted)={})",nStatus,sURL,sCut(100,saParam),sCut(100,osReturn.toString()));
            oLogBig_Web.info("FINISHED! (nStatus={},sURL={},saParam={},osReturn={})",nStatus,sURL,saParam,osReturn);
            return osReturn.toString();
        }catch(Exception oException){
            new Log(this.getClass(), oException)
                    ._Head("[post]:BREAKED!")
                    ._Status(Log.LogStatus.ERROR)
                    //._StatusHTTP(nStatus)
                    ._Param("sURL", sURL)
                    ._Param("saParam", saParam)
                    ._SendThrow()
                    ;
            LOG.error("BREAKED: {} (sURL={},saParam={})",oException.getMessage(),sURL,saParam);
            oLogBig_Web.error("BREAKED: {} (sURL={},saParam={})",oException.getMessage(),sURL,saParam);
            oLogBig_Web.trace("BREAKED:",oException);
            throw oException; //return null;
        }
    }

    public String get(String sURL, Map<String, String> mParam) throws Exception {
        URL oURL = new URL(getFullURL(sURL, mParam));
        InputStream oInputStream;
        BufferedReader oBufferedReader_InputStream;
        HttpURLConnection oConnection;
        Integer nStatus = null;
        StringBuilder osReturn = new StringBuilder();
        try{

            oConnection = (HttpURLConnection) oURL.openConnection();

            String sUser = generalConfig.sAuthLogin();
            String sPassword = generalConfig.sAuthPassword();
            String sAuth = LiqBuyUtil.base64_encode(sUser + ":" + sPassword);
            oConnection.setRequestProperty("authorization", "Basic " + sAuth);

            oConnection.setRequestMethod(RequestMethod.GET.name());
            oConnection.setDoInput(true);
            oConnection.setDoOutput(true);

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
            
            if(nStatus!=200){
                new Log(this.getClass())
                        ._Head("[get]:nStatus!=200")
                        ._Status(Log.LogStatus.ERROR)
                        //._StatusHTTP(nStatus)
                        ._Param("nStatus", nStatus)
                        ._Param("sURL", sURL)
                        ._Param("mParam", mParam)
                        ._Param("osReturn", osReturn)
                        ._Send()
                        ;
            }
            
            LOG.info("FINISHED! (nStatus={},sURL={},mParam={},osReturn={})",nStatus,sURL,sCut(100,mParam.toString()),sCut(100,osReturn.toString()));
            oLogBig_Web.info("FINISHED! (nStatus={},sURL={},mParam={},osReturn={})",nStatus,sURL,mParam,osReturn);
            return osReturn.toString();
        }catch(Exception oException){
            new Log(this.getClass(), oException)
                    ._Head("[post]:BREAKED!")
                    ._Status(Log.LogStatus.ERROR)
                    //._StatusHTTP(nStatus)
                    ._Param("sURL", sURL)
                    ._Param("mParam", mParam)
                    ._Send()
                    ;
            LOG.error("BREAKED: {} (sURL={},mParam={})",oException.getMessage(),sURL,mParam);
            oLogBig_Web.error("BREAKED: {} (sURL={},mParam={})",oException.getMessage(),sURL,mParam);
            oLogBig_Web.trace("BREAKED:",oException);
            throw oException; //return null;
        }
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
