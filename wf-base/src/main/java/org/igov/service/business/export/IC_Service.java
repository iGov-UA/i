/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.igov.service.business.export;

import org.igov.io.GeneralConfig;
import org.igov.io.web.HttpRequester;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 *
 * @author olga
 */
@Service
public class IC_Service {

    private static final Logger LOG = LoggerFactory.getLogger(IC_Service.class);

    @Autowired
    HttpRequester httpRequester;

    @Autowired
    GeneralConfig generalConfig;
    
    public String transferDocument(String documentVacation, String URL_Ending) throws Exception {
        httpRequester.setsLogin(generalConfig.getsLogin_Auth_1C());
        httpRequester.setsPassword(generalConfig.getsPassword_Auth_1C());
        String sURL = generalConfig.getsURL_1C() + URL_Ending;
        LOG.info("sURL: " + sURL);
        //http://spirit.mriya.ua:2011/trainingbase/odata/standard.odata/Document_ОтпускаОрганизаций
        String result = httpRequester.postInside(sURL, null, documentVacation, "application/atom+xml;type=feed;charset=utf-8");
        LOG.info("nResponseCode: " + httpRequester.getnResponseCode() + " result: " + result);
        return result;
    }
}
