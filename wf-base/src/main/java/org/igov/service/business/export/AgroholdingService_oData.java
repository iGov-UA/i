/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.igov.service.business.export;

import com.google.common.io.Files;
import java.io.File;
import java.nio.charset.Charset;
import org.igov.io.GeneralConfig;
import org.igov.io.fs.FileSystemData;
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
public class AgroholdingService_oData {
    
    private static final Logger LOG = LoggerFactory.getLogger(AgroholdingService.class);

    @Autowired
    HttpRequester httpRequester;

    @Autowired
    GeneralConfig generalConfig;

    public String transferDocumentVacation() throws Exception {
        httpRequester.setsLogin(generalConfig.getsLogin_Auth_Agroholding());
        httpRequester.setsPassword(generalConfig.getsPassword_Auth_Agroholding());
        String sURL = generalConfig.getsURL_Agroholding() + "/Document_ОтпускаОрганизаций";
        LOG.info("sURL: " + sURL);
        //http://spirit.mriya.ua:2011/trainingbase/odata/standard.odata/Document_ОтпускаОрганизаций
        //String result = httpRequester.postInside(sURL, null, documentVacation, "application/atom+xml;type=entry;charset=utf-8");
        File oFile = FileSystemData.getFile(FileSystemData.SUB_PATH_XML, "DocumentVacation_Agroholding.xml");
        String documentVacation = Files.toString(oFile, Charset.defaultCharset());
        LOG.info("Created document: {}", documentVacation);
        String result = "none";
        result = httpRequester.postInside(sURL, null, documentVacation, "application/atom+xml;type=entry;charset=utf-8");
        LOG.info("nResponseCode: " + httpRequester.getnResponseCode() + " result: " + result);
        return result;
    }
}


    /*private static final Logger LOG = LoggerFactory.getLogger(AgroholdingService_oData.class);

    @Autowired
    GeneralConfig generalConfig;

    public static String transferDocumentVacation() throws Exception {
        String sLogin = "odata";
        String sPassword = "123457";
        String sURL = "http://spirit.mriya.ua:2011/trainingbase/odata/standard.odata";
        LOG.info("sURL: " + sURL);
        //http://spirit.mriya.ua:2011/trainingbase/odata/standard.odata/
        //http://localhost:8080/mydataserver/dsl.svc/
        //String url = "https://api.datamarket.azure.com/Data.ashx/UnitedNations/MDG/";
       

        //ODataConsumer c = ODataConsumers.create(sURL); //dataMarket(sURL, null);
        OClientBehavior basicAuth = OClientBehaviors.basicAuth(sLogin, sPassword);
        ODataConsumer c = ODataJerseyConsumer.newBuilder(sURL).setClientBehaviors(basicAuth).build();

        OEntity firstDataSeries = c.getEntities("Document_ОтпускаОрганизаций").top(1).execute().first();
        System.out.println("firstDataSeries: " + firstDataSeries.getProperties().toString());
        //String filter = String.format("DataSeriesId eq '%s'", firstDataSeries.getProperty("Id").getValue());
        //(firstDataSeries.getProperty("Name", String.class).getValue(), c.getEntities("Values").filter(filter).top(10).execute());
        return "";
    }

    public static void main(String arg[]) throws Exception {
        transferDocumentVacation();
    }*/

