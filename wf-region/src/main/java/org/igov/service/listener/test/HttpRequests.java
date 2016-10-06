package org.igov.service.listener.test;

import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

//  https://www.mkyong.com/java/how-to-send-http-request-getpost-in-java/

public class HttpRequests {

    private final String USER_AGENT = "Mozilla/5.0";

    /*

    public static void main(String[] args) throws Exception {

        HttpRequests http = new HttpRequests();
        ObjectMapper mapper = new ObjectMapper();

///
        System.out.println("Testing 1 - Send Http GET token request");

        String getTokenRequest = "http://212.26.131.154:86/api/token/refresh?refresh_token=277be326eef77fdf1b58c474a3af62eccdbb1fc788a38ba0779c78c54cea6d0a4a4c372af8500c057c56a813b38960365ab956d1cd43720a5c3ee0656863364f";
        String tokenResult = null;
        try {
            tokenResult = http.sendGet(getTokenRequest, Collections.<String, String>emptyMap());
        } catch (Exception e) {
            e.printStackTrace();
        }

        // http://stackoverflow.com/questions/26190851/get-single-field-from-json-using-jackson
        ObjectNode node = null;
        try {
            node = mapper.readValue(tokenResult, ObjectNode.class);
        } catch (IOException e) {
            e.printStackTrace();
        }
        String token = node.get("token").asText();
        String refreshToken = node.get("refresh_token").asText();

        //System.out.println("Got Token: " + token);
        System.out.println("Refresh Token: " + refreshToken);

        // Reuse Auth results
        Map<String, String> authParams = new HashMap<>();
        String tokenBearer = "Bearer " + token;
        authParams.put("Authorization", tokenBearer);

///
        System.out.println("\nTesting 2 - Send Http GET street request");
        //String getStreetRequest = "http://212.26.131.154:86/api/search.json?s=" + sStreet + "&n=" + sNumber;
        String getStreetRequest = "http://212.26.131.154:86/api/search.json?s=okz" + "&n=1";


        [
  {
    "name_en": "Vokzalna",
    "addrNumber": "1",
    "id": 23945
  },
  {
    "name_en": "Vokzalna",
    "addrNumber": "1",
    "id": 33388
  },
  {
    "name_en": "Pryvokzalna",
    "addrNumber": "1",
    "id": 15718
  },
  {
    "name_en": "Pryvokzalna",
    "addrNumber": "1",
    "id": 32772
  }
]


        String streetResult = "";

        try {
            streetResult = http.sendGet(getStreetRequest, authParams);
        } catch (Exception e) {
            e.printStackTrace();
        }


        JavaType type = mapper.getTypeFactory().constructCollectionType(List.class, StreetDto.class);
        List<StreetDto> streets = null;
        try {
            streets = mapper.readValue(streetResult, type);
        } catch (IOException e) {
            e.printStackTrace();
        }

      //  System.out.println(streets.get(0).getId());

        //System.out.println("\nGET street result: " + streetResult);
///
        System.out.println("\nTesting 3 - Send Http POST message");

        String postMessageRequest = "http://212.26.131.154:86/api/msg.json";

        String postParams = "msg="; // + streetResult;  //!!!!!!!!!!!!
        postParams += streets.get(0).toString() + streets.get(1).toString();
        postParams = postParams.substring(0,postParams.length() - 1);

        //for (StreetDto dto : streets )
            //  postParams += "{" + dto.getId() + ",OK}";
            //  postParams += dto.toString();

        //postParams = postParams.substring(0,postParams.length() - 1);
        System.out.println(postParams);
        String postResult = "";
        try {
            postResult = http.sendPost(postMessageRequest, postParams, authParams);
        } catch (Exception e) {
            e.printStackTrace();
        }
///
        postParams = "msg=" + streetResult;
        //msg=[{"name_en":"Vokzalna","addrNumber":"1","id":23945},{"name_en":"Vokzalna","addrNumber":"1","id":33388},{"name_en":"Pryvokzalna","addrNumber":"1","id":15718},{"name_en":"Pryvokzalna","addrNumber":"1","id":32772}]
        //postParams += streets.get(0).toString() + streets.get(1).toString();

        //for (StreetDto dto : streets )
        //  postParams += "{" + dto.getId() + ",OK}";
        //  postParams += dto.toString();

        //postParams = postParams.substring(0,postParams.length() - 1);
        System.out.println(postParams);

        try {
            postResult = http.sendPost(postMessageRequest, postParams, authParams);
        } catch (Exception e) {
            e.printStackTrace();
        }

        System.out.println("\nPOST result is: " + postResult);

    }


    public static void main(String[] args) throws Exception {
        HttpRequests http = new HttpRequests();
        ObjectMapper mapper = new ObjectMapper();

///
        System.out.println("Testing 1 - Send Http GET token request");

        String getTokenRequest = "http://212.26.131.154:86/api/token/refresh?refresh_token=277be326eef77fdf1b58c474a3af62eccdbb1fc788a38ba0779c78c54cea6d0a4a4c372af8500c057c56a813b38960365ab956d1cd43720a5c3ee0656863364f";
        String tokenResult = http.sendGet(getTokenRequest, Collections.<String, String>emptyMap());

        // http://stackoverflow.com/questions/26190851/get-single-field-from-json-using-jackson
        ObjectNode node = mapper.readValue(tokenResult, ObjectNode.class);
        String token = node.get("token").asText();
        String refreshToken = node.get("refresh_token").asText();

        System.out.println("Got Token: " + token);
        System.out.println("Refresh Token: " + refreshToken);

        // Reuse Auth results
        Map<String, String> authParams = new HashMap<>();
        String tokenBearer = "Bearer " + token;
        authParams.put("Authorization", tokenBearer);

///
        System.out.println("\nTesting 2 - Send Http GET street request");

        String getStreetRequest = "http://212.26.131.154:86/api/search.json?s=kor&n=1";
        String streetResult = http.sendGet(getStreetRequest, authParams);


///
        System.out.println("\nTesting 3 - Send Http POST message");

        String postMessageRequest = "http://212.26.131.154:86/api/msg.json";
        String postParams = "msg=testMessage";

        String postResult = http.sendPost(postMessageRequest, postParams, authParams);
    }

    */

    // HTTP GET request
    public String sendGet(String url, Map<String, String> headers) throws Exception {

        URL obj = new URL(url);
        HttpURLConnection connection = (HttpURLConnection) obj.openConnection();

        connection.setRequestMethod("GET");
        connection.setRequestProperty("User-Agent", USER_AGENT);

        // Set Headers
        for (Map.Entry<String, String> entry : headers.entrySet()) {
            connection.addRequestProperty(entry.getKey(), entry.getValue());
        }

        int responseCode = connection.getResponseCode();
        System.out.println("\nSending 'GET' request to URL : " + url);
        System.out.println("Response Code : " + responseCode);

        BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
        String inputLine;
        StringBuilder response = new StringBuilder();
        while ((inputLine = in.readLine()) != null) {
            response.append(inputLine);
        }
        in.close();

        //print result
        System.out.println(response.toString());
        return response.toString();
    }

    // HTTP POST request
    protected String sendPost(String url, String requestParameters, Map<String, String> headers) throws Exception {

        URL obj = new URL(url);
        HttpURLConnection connection = (HttpURLConnection) obj.openConnection();

        byte[] postData = requestParameters.getBytes(StandardCharsets.UTF_8);
        int postDataLength = postData.length;

        connection.setRequestMethod("POST");
        connection.setRequestProperty("User-Agent", USER_AGENT);
        connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
        connection.setRequestProperty("charset", "utf-8");
        connection.setRequestProperty("Content-Length", Integer.toString(postDataLength));
        connection.setDoOutput(true);

        // Set Headers
        for (Map.Entry<String, String> entry : headers.entrySet()) {
            connection.addRequestProperty(entry.getKey(), entry.getValue());
        }

        // Send post request
        DataOutputStream wr = new DataOutputStream(connection.getOutputStream());
        wr.write(postData);
        wr.flush();
        wr.close();

        int responseCode = connection.getResponseCode();
        System.out.println("\nSending 'POST' request to URL : " + url);
        System.out.println("Post parameters : " + requestParameters);
        System.out.println("Response Code : " + responseCode);

        BufferedReader in = new BufferedReader(
                new InputStreamReader(connection.getInputStream()));
        String inputLine;
        StringBuilder response = new StringBuilder();
        while ((inputLine = in.readLine()) != null) {
            response.append(inputLine);
        }
        in.close();

        //print result
        System.out.println(response.toString());
        return response.toString();
    }

}
