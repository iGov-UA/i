package org.igov.service.business.access;

import org.igov.service.controller.ActionTaskCommonController;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpEntity;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.ByteArrayBody;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.OutputStream;
import java.io.StringWriter;
import java.net.*;
import java.util.List;
import org.igov.io.GeneralConfig;

public class BankIDUtils {

    private static final String EMPTY_JSON = "{}";
    
    private static final Logger LOG = LoggerFactory.getLogger(ActionTaskCommonController.class);
    
    public static String checkECP(GeneralConfig generalConfig, byte[] fileByteArray,
            String fileName) {
        
        String clientId = generalConfig.getLogin_BankID_PB_Auth();
        String clientSecret = generalConfig.getPassword_BankID_PB_Auth();
        String redirectUrl = generalConfig.getSelfHostCentral();
                        
        LOG.info("(clientID={}, clientSecret={}, redirectUrl={})", clientId, clientSecret,  redirectUrl);

        try {
            HttpClientContext context = HttpClientContext.create();

            CloseableHttpClient httpClient = HttpClientBuilder.create().build();

            String code = doAuthorizeCall(generalConfig, clientId, clientSecret, redirectUrl, context, httpClient);

            String accessToken = doGetAccessToken(generalConfig, clientId, clientSecret, redirectUrl, context, httpClient, code);

            if (accessToken != null) {
                String json = submitDocumentForCheckingECP(generalConfig, fileByteArray, fileName, accessToken);

                if (json != null) {
                    JSONParser parser = new JSONParser();
                    JSONObject ecpJson = (JSONObject) parser.parse(json);
                    if (ecpJson.containsKey("state") && ecpJson.get("state").equals("ok")) {
                        // correct ecp
                        return json;
                    }
                }
                LOG.info("ecp is not found. returning empty json string");
                return EMPTY_JSON;
            }

        } catch (IOException | URISyntaxException | ParseException oException) {
            LOG.error("Error occured while checking ECP: ", oException.getMessage());
            LOG.error("Error occured while checking ECP-: ", oException);
            LOG.debug("FAIL:", oException);
        }

        return EMPTY_JSON;
    }

    protected static String submitDocumentForCheckingECP(GeneralConfig generalConfig, byte[] fileByteArray,
            String fileName, String accessToken) throws MalformedURLException,
            IOException, ProtocolException {
        StringWriter writer;
        String sURL = generalConfig.getURL_ResourceSignature_BankID_PB_Auth();//"https://bankid.privatbank.ua/ResourceService/checked/signatureData"
        
        URL urlAddr = new URL(sURL);
        HttpURLConnection connection = (HttpURLConnection) urlAddr
                .openConnection();
        connection.setRequestMethod("POST");
        connection.setDoOutput(true);
        connection.setDoInput(true);

        ByteArrayBody fileBody = new ByteArrayBody(fileByteArray, fileName);
        HttpEntity multiPartEntity = MultipartEntityBuilder.create().addPart("file", fileBody).build();

        connection.setRequestProperty("Content-Type", multiPartEntity.getContentType().getValue());
        connection.setRequestProperty("Authorization", "Bearer " + accessToken + ", Id testIgov");
        connection.setRequestProperty("Accept", "application/json");

        OutputStream out = connection.getOutputStream();
        multiPartEntity.writeTo(out);
        out.close();

        writer = new StringWriter();
        IOUtils.copy(connection.getInputStream(), writer,
                connection.getContentEncoding() == null ? "UTF-8" : connection.getContentEncoding());
        String json = writer.toString();
        LOG.info("JSON with ECP: (json={})", json);
        return json;
    }

    protected static String doGetAccessToken(GeneralConfig generalConfig, String clientID, String clientSecret, String redirectUrl,
            HttpClientContext context,
            CloseableHttpClient httpClient, String code)
            throws URISyntaxException, IOException, ClientProtocolException, ParseException {
        String sha1 = DigestUtils.sha1Hex(clientID + clientSecret + code);

        URI uri2 = getGettingAccessToeknURI(generalConfig, clientID, redirectUrl, code, sha1);

        HttpGet getRequestAccessToken = new HttpGet(uri2);

        CloseableHttpResponse response1 = httpClient.execute(getRequestAccessToken, context);

        HttpEntity entity = response1.getEntity();

        StringWriter writer = new StringWriter();
        IOUtils.copy(entity.getContent(), writer, "UTF-8");
        String responseString = writer.toString();

        JSONParser parser = new JSONParser();
        JSONObject accessTokenJson = (JSONObject) parser.parse(responseString);
        String accessToken = null;
        if (accessTokenJson.containsKey("access_token")) {
            accessToken = (String) accessTokenJson.get("access_token");
            LOG.info("Successfully received access token");
        } else if (accessTokenJson.containsKey("error")) {
            LOG.error("Error occurred while getting access token (accessTokenJson={})", accessTokenJson.get("error"));
        }
        return accessToken;
    }

    protected static String doAuthorizeCall(GeneralConfig generalConfig, String clientID, String clientSecret, String redirectUrl,
            HttpClientContext context,
            CloseableHttpClient httpClient) throws URISyntaxException, IOException, ClientProtocolException {

        URI uri = createAuthorizeURI(generalConfig, clientID, clientSecret, redirectUrl);

        HttpGet getAuthorizeRequest = new HttpGet(uri);

        httpClient.execute(getAuthorizeRequest, context);

        List<URI> redirectLocations = context.getRedirectLocations();

        String code = null;
        for (URI uriCurr : redirectLocations) {
            if (uriCurr.getQuery().contains("code=")) {
                code = StringUtils.substringAfter(uriCurr.getQuery(), "code=");
                break;
            }
        }
        LOG.info("Successfully received code from bank ID");
        return code;
    }

    protected static URI getGettingAccessToeknURI(GeneralConfig generalConfig, String clientID,
            String redirectUrl, String code, String sha1)
            throws URISyntaxException {
        URI uri2 = new URIBuilder().setScheme("https")
                .setHost(generalConfig.getHost_AccessToken_BankID_PB_Auth())//"bankid.privatbank.ua"
                .setPath(generalConfig.getPath_AccessToken_BankID_PB_Auth())//"/DataAccessService/oauth/token"
                .setParameter("grant_type", "authorization_code")
                .setParameter("client_id", clientID)
                .setParameter("client_secret", sha1)
                .setParameter("code", code)
                .setParameter("edsVerify", "true")
                .setParameter("redirect_uri", redirectUrl).build();
        return uri2;
    }

    protected static URI createAuthorizeURI(GeneralConfig generalConfig, String clientID,
            String clientSecret, String redirectUrl) throws URISyntaxException {
        URI uri = new URIBuilder().setScheme("https")
                .setHost(generalConfig.getHost_Authorize_BankID_PB_Auth())//"bankid.privatbank.ua"
                .setPath(generalConfig.getPath_Authorize_BankID_PB_Auth())//"/DataAccessService/das/authorize"
                .setParameter("response_type", "code")
                .setParameter("client_id", clientID)
                .setParameter("client_secret", clientSecret)
                .setParameter("edsVerify", "true")
                .setParameter("redirect_uri", redirectUrl).build();
        return uri;
    }

}
