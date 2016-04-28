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
 * Создание СООБЩЕНИЯ на основании JSON запроса вида:
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
 *                "TemplateMsgId" : "${Id шаблона}",
 *                "ext": {
 *                         "LocalMsg": [{
 *                         	          "Level": "DEVELOPER",
 *                         	          "Lang": "UKR",
 *                                        "Text": "getMessageImpl",
 *                                        "FullText": ""
 *                                     }]
 *                       }
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

    public String doReqest(String sMsgURL) throws Exception {
	HttpURLConnection conn = null;
	StringBuffer ret = new StringBuffer(500);

	try {

	    URL url = new URL(sMsgURL);
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


	    if (conn.getResponseCode() != HttpURLConnection.HTTP_OK) {
		
		StringBuffer berr = new StringBuffer(500);
		berr.append("Ошибка при работе с сервисом ");
		berr.append(url);
		berr.append("\nBodyRequest:\n");
		berr.append(sBodyRequest);
		berr.append("\nResponseBody:\n");
		berr.append(ret);
		berr.append("\nHTTP error code : ");
		berr.append(conn.getResponseCode());

		LOG.error(berr.toString());
		
		throw new Exception(berr.toString());
	    }

	    LOG.debug("HTTP code:{}", conn.getResponseCode());
	    LOG.debug("\nResponseBody:{}\n", ret.toString());
	    
	} catch (MalformedURLException e) {
	    LOG.error("Ошибка при создании сообщения. Запрос:\n{}\n Ошибка:\n",sBodyRequest,e);
	} catch (IOException e) {
	    LOG.error("Ошибка при создании сообщения. Запрос:\n{}\n Ошибка:\n",sBodyRequest,e);
	} finally {
	    conn.disconnect();
	}

	return ret.toString();
    }

}
