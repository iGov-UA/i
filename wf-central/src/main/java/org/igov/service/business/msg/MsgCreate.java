package org.igov.service.business.msg;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MsgCreate {
    private static final Logger LOG = LoggerFactory.getLogger(MsgCreate.class);

    private String reqest = null;
    private static final String MSG_URL_ADD = "http://msg.igov.org.ua/MSG";

    public MsgCreate(String reqest) {
	LOG.debug("reqest={}", reqest);

	this.reqest = reqest;
    }

    public String doReqest() throws Exception {
	HttpURLConnection conn = null;
	StringBuffer ret = new StringBuffer(500);

	try {

	    URL url = new URL(MSG_URL_ADD);
	    conn = (HttpURLConnection) url.openConnection();
	    conn.setDoOutput(true);
	    conn.setRequestMethod("POST");
	    conn.setRequestProperty("Content-Type", "application/json");

	    OutputStream os = conn.getOutputStream();
	    os.write(reqest.getBytes());
	    os.flush();

	    BufferedReader br = new BufferedReader(new InputStreamReader((conn.getInputStream())));
	    if (br != null) {
		String output;
		while ((output = br.readLine()) != null) {
		    ret.append(output);
		}
	    }

	    if (conn.getResponseCode() != HttpURLConnection.HTTP_CREATED) {
		throw new Exception(
			"Failed : HTTP error code : " + conn.getResponseCode() + "\nResponseBody:\n" + ret.toString());
	    }

	    LOG.debug("response={}", conn.getResponseCode());
	    LOG.debug("nResponseBody={}", ret.toString());
	    
	} catch (MalformedURLException e) {
	    e.printStackTrace();
	} catch (IOException e) {
	    e.printStackTrace();
	} finally {
	    conn.disconnect();
	}

	return ret.toString();
    }

}
