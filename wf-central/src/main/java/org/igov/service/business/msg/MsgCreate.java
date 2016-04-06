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

/**
 * 
 * @author kr110666kai
 * 
 * Создание шаблона сообщения на основании JSON запроса вида:
 * 
 * { "r" : 
 *   [ 
 *     { 
 *       "_type_comment" : "Создание сообщения", 
 *       "type" : "MSG_ADD", 
 *       "sid" : "${sid}",
 *        "s" : { 
 *                "Type" : "${Тип сообщения}",
 *                "MsgCode" : "${Код сообщения}",
 *                "BusId" : "${Id бизнеспроцесса}",
 *                "Descr" : "${Описание сообщения}",
 *                "TemplateMsgId" : "${Id шаблона}" 
 *               }
 *      }
 *   ]
 * }
 *
 */
public class MsgCreate {
    private static final Logger LOG = LoggerFactory.getLogger(MsgCreate.class);

    private String sBodyRequest = null;

    public MsgCreate(String sBodyRequest) {
	LOG.debug("reqest={}", sBodyRequest);

	this.sBodyRequest = sBodyRequest;
    }

    public String doReqest() throws Exception {
	HttpURLConnection conn = null;
	StringBuffer ret = new StringBuffer(500);

	try {

	    URL url = new URL(MsgSendImpl.MSG_URL);
	    conn = (HttpURLConnection) url.openConnection();
	    conn.setDoOutput(true);
	    conn.setRequestMethod("POST");
	    conn.setRequestProperty("Content-Type", "application/json");

	    OutputStream os = conn.getOutputStream();
	    os.write(sBodyRequest.getBytes());
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
			"Ошибка при создании шаблона сообщения : HTTP error code : " + conn.getResponseCode() + "\nResponseBody:\n" + ret.toString());
	    }

	    LOG.debug("response={}", conn.getResponseCode());
	    LOG.debug("nResponseBody={}", ret.toString());

	} catch (MalformedURLException e) {
	    LOG.error("Ошибка при создании шаблона сообщения. Тело запроса:\n{}\n Ошибка:{}",sBodyRequest,e.getMessage());
	    e.printStackTrace();
	} catch (IOException e) {
	    LOG.error("Ошибка при создании шаблона сообщения. Тело запроса:\n{}\n Ошибка:{}",sBodyRequest,e.getMessage());
	    e.printStackTrace();
	} finally {
	    conn.disconnect();
	}

	return ret.toString();
    }

}
