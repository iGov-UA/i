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
	LOG.debug("BodyRequest:\n{}", sBodyRequest);

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
//	    conn.setRequestProperty("Content-Type", "application/json");
	    conn.setRequestProperty("Content-Type", "application/xml");
	    
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

	    LOG.debug("HTTP code:{}", conn.getResponseCode());
	    LOG.debug("\nResponseBody:{}\n", ret.toString());

	    if (conn.getResponseCode() != HttpURLConnection.HTTP_CREATED) {
		
		StringBuffer berr = new StringBuffer(500);
		berr.append("Ошибка при работе с сервисом :");
		berr.append(url);
		berr.append("\nBodyRequest:\n");
		berr.append(sBodyRequest);
		berr.append("\nResponseBody:\n");
		berr.append(ret);
		berr.append("\nHTTP error code : ");
		berr.append(conn.getResponseCode());
		
		throw new Exception(berr.toString());
	    }

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
