/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.igov.service.business.export;

import java.net.URLEncoder;
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
public class AgroholdingService {

    private static final Logger LOG = LoggerFactory.getLogger(AgroholdingService.class);
    
    private final static String documentVacation = "<?xml version='1.0' encoding='UTF-8'?>"
                + "<feed xmlns='http://www.w3.org/2005/Atom' xmlns:at='http://purl.org/atompub/tombstones/1.0' xmlns:d='http://schemas.microsoft.com/ado/2007/08/dataservices' xmlns:m='http://schemas.microsoft.com/ado/2007/08/dataservices/metadata' xml:base='http://spirit.mriya.ua:2011/trainingbase/odata/standard.odata/'>"
                + "<entry>"
                + "<id></id>"
                + "<category term='StandardODATA.Document_ОтпускаОрганизаций' scheme='http://schemas.microsoft.com/ado/2007/08/dataservices/scheme'/>"
                + "<title/>"
                + "<updated/>"
                + "<author/>"
                + "<summary/>"
                + "<content type='application/xml'>"
                + "<m:properties xmlns:d='http://schemas.microsoft.com/ado/2007/08/dataservices' xmlns:m='http://schemas.microsoft.com/ado/2007/08/dataservices/metadata'><d:DataVersion>AAAAAAABX8s=</d:DataVersion>"
                + "<d:DeletionMark>false</d:DeletionMark>"
                + "<d:Number/>"
                + "<d:Date>2017-03-01T23:22:43</d:Date>"
                + "<d:Posted>true</d:Posted>"
                + "<d:Организация_Key>a1257d8c-6fb0-11d9-ac6c-0080482454f7</d:Организация_Key>"
                + "<d:Комментарий/>"
                + "<d:Ответственный_Key>c82f6314-6faa-11d9-ac6c-0080482454f7</d:Ответственный_Key>"
                + "<d:КраткийСоставДокумента>Барсуков Г.В.</d:КраткийСоставДокумента>"
                + "<d:ВидОперации>Отпуск</d:ВидОперации>"
                + "<d:ДокументОснование_Key>00000000-0000-0000-0000-000000000000</d:ДокументОснование_Key>"
                + "<d:РасчитыватьОстаткиЗаВсеГода>false</d:РасчитыватьОстаткиЗаВсеГода>"
                + "<d:КоличествоЛет>0</d:КоличествоЛет>"
                + "<d:РаботникиОрганизации m:type='Collection(StandardODATA.Document_ОтпускаОрганизаций_РаботникиОрганизации_RowType)'>"
                + "<d:element m:type='StandardODATA.Document_ОтпускаОрганизаций_РаботникиОрганизации_RowType'>"
                + "<d:LineNumber>1</d:LineNumber>"
                + "<d:Сотрудник_Key>a807e909-abfb-11dc-aa58-00112f3000a2</d:Сотрудник_Key>"
                + "<d:ФизЛицо_Key>79c4fdca-71ed-11d9-ac6c-0080482454f7</d:ФизЛицо_Key>"
                + "<d:ОсвобождатьСтавку>false</d:ОсвобождатьСтавку>"
                + "<d:ДатаНачала>2017-03-02T00:00:00</d:ДатаНачала>"
                + "<d:ДатаОкончания>2017-03-15T00:00:00</d:ДатаОкончания>"
                + "<d:Основание/>"
                + "<d:КвоДней>0</d:КвоДней>"
                + "<d:РабочийГодС>0001-01-01T00:00:00</d:РабочийГодС>"
                + "<d:РабочийГодПо>0001-01-01T00:00:00</d:РабочийГодПо>"
                + "<d:НапомнитьПоЗавершении>false</d:НапомнитьПоЗавершении>"
                + "<d:ВидОтпуска_Key>e8d50a27-8506-4dec-9584-531961cf445d</d:ВидОтпуска_Key>"
                + "</d:element>"
                + "</d:РаботникиОрганизации>"
                + "<d:ИспользованиеЕжегодногоОтпуска m:type='Collection(StandardODATA.Document_ОтпускаОрганизаций_ИспользованиеЕжегодногоОтпуска_RowType)'/>"
                + "</m:properties>"
                + "</content>"
                + "</entry>"
                + "</feed>";

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
        
        String result = httpRequester.postInside(URLEncoder.encode(sURL, "UTF-8"), null, documentVacation, "application/atom+xml;type=entry;charset=utf-8");
        LOG.info("nResponseCode: " + httpRequester.getnResponseCode() + " result: " + result);
        

        return result;
    }
}

/*private final static String documentVacation = "<?xml version='1.0' encoding='UTF-8'?>"
                + "<feed xmlns='http://www.w3.org/2005/Atom' xmlns:at='http://purl.org/atompub/tombstones/1.0' xmlns:d='http://schemas.microsoft.com/ado/2007/08/dataservices' xmlns:m='http://schemas.microsoft.com/ado/2007/08/dataservices/metadata' xml:base='http://spirit.mriya.ua:2011/trainingbase/odata/standard.odata/'>"
                + "<entry>"
                + "<id>http://spirit.mriya.ua:2011/trainingbase/odata/standard.odata/Document_ОтпускаОрганизаций(guid'34b0dffb-fec5-11e6-98d3-005056b384b3')</id>"
                + "<category term='StandardODATA.Document_ОтпускаОрганизаций' scheme='http://schemas.microsoft.com/ado/2007/08/dataservices/scheme'/>"
                + "<title/>"
                + "<updated/>"
                + "<author/>"
                + "<summary/>"
                + "<content type='application/xml'>"
                + "<m:properties xmlns:d='http://schemas.microsoft.com/ado/2007/08/dataservices' xmlns:m='http://schemas.microsoft.com/ado/2007/08/dataservices/metadata'><d:DataVersion>AAAAAAABX8s=</d:DataVersion>"
                + "<d:DeletionMark>false</d:DeletionMark>"
                + "<d:Number/>"
                + "<d:Date>2017-03-01T23:22:43</d:Date>"
                + "<d:Posted>true</d:Posted>"
                + "<d:Организация_Key>a1257d8c-6fb0-11d9-ac6c-0080482454f7</d:Организация_Key>"
                + "<d:Комментарий/>"
                + "<d:Ответственный_Key>c82f6314-6faa-11d9-ac6c-0080482454f7</d:Ответственный_Key>"
                + "<d:КраткийСоставДокумента>Барсуков Г.В.</d:КраткийСоставДокумента>"
                + "<d:ВидОперации>Отпуск</d:ВидОперации>"
                + "<d:ДокументОснование_Key>00000000-0000-0000-0000-000000000000</d:ДокументОснование_Key>"
                + "<d:РасчитыватьОстаткиЗаВсеГода>false</d:РасчитыватьОстаткиЗаВсеГода>"
                + "<d:КоличествоЛет>0</d:КоличествоЛет>"
                + "<d:РаботникиОрганизации m:type='Collection(StandardODATA.Document_ОтпускаОрганизаций_РаботникиОрганизации_RowType)'>"
                + "<d:element m:type='StandardODATA.Document_ОтпускаОрганизаций_РаботникиОрганизации_RowType'>"
                + "<d:LineNumber>1</d:LineNumber>"
                + "<d:Сотрудник_Key>a807e909-abfb-11dc-aa58-00112f3000a2</d:Сотрудник_Key>"
                + "<d:ФизЛицо_Key>79c4fdca-71ed-11d9-ac6c-0080482454f7</d:ФизЛицо_Key>"
                + "<d:ОсвобождатьСтавку>false</d:ОсвобождатьСтавку>"
                + "<d:ДатаНачала>2017-03-02T00:00:00</d:ДатаНачала>"
                + "<d:ДатаОкончания>2017-03-15T00:00:00</d:ДатаОкончания>"
                + "<d:Основание/>"
                + "<d:КвоДней>0</d:КвоДней>"
                + "<d:РабочийГодС>0001-01-01T00:00:00</d:РабочийГодС>"
                + "<d:РабочийГодПо>0001-01-01T00:00:00</d:РабочийГодПо>"
                + "<d:НапомнитьПоЗавершении>false</d:НапомнитьПоЗавершении>"
                + "<d:ВидОтпуска_Key>e8d50a27-8506-4dec-9584-531961cf445d</d:ВидОтпуска_Key>"
                + "</d:element>"
                + "</d:РаботникиОрганизации>"
                + "<d:ИспользованиеЕжегодногоОтпуска m:type='Collection(StandardODATA.Document_ОтпускаОрганизаций_ИспользованиеЕжегодногоОтпуска_RowType)'/>"
                + "</m:properties>"
                + "</content>"
                + "</entry>"
                + "</feed>";*/
    /*private final static String documentVacation = "<?xml version='1.0' encoding='UTF-8'?>"
            + "<feed xmlns='http://www.w3.org/2005/Atom'"
            + " xmlns:at='http://purl.org/atompub/tombstones/1.0'"
            + " xmlns:d='http://schemas.microsoft.com/ado/2007/08/dataservices'"
            + " xmlns:m='http://schemas.microsoft.com/ado/2007/08/dataservices/metadata'"
            + " xml:base='http://spirit.mriya.ua:2011/trainingbase/odata/standard.odata/'>"
            + "<entry>"
            + "<id>http://spirit.mriya.ua:2011/trainingbase/odata/standard.odata/Document_ОтпускаОрганизаций(guid'34b0dffb-fec5-11e6-98d3-005056b384b3')</id>"
            + "<category term='StandardODATA.Document_ОтпускаОрганизаций' scheme='http://schemas.microsoft.com/ado/2007/08/dataservices/scheme'/>"
            + "<title/>"
            + "<updated/>"
            + "<author/>"
            + "<summary/>"
            + "<content type='application/xml'>"
            + "<m:properties xmlns:d='http://schemas.microsoft.com/ado/2007/08/dataservices' xmlns:m='http://schemas.microsoft.com/ado/2007/08/dataservices/metadata'>"
            + "<d:DataVersion>AAAAAAABX8s=</d:DataVersion>"
            + "<d:DeletionMark>false</d:DeletionMark>"
            + "<d:Number/>"
            + "<d:Date>2017-03-02T23:22:43</d:Date>"
            + "<d:Posted>true</d:Posted>"
            + "<d:Организация_Key>a1257d8c-6fb0-11d9-ac6c-0080482454f7</d:Организация_Key>"
            + "<d:Комментарий/>"
            + "<d:Ответственный_Key>c82f6314-6faa-11d9-ac6c-0080482454f7</d:Ответственный_Key>"
            + "<d:КраткийСоставДокумента>Барсуков Г.В.</d:КраткийСоставДокумента>"
            + "<d:ВидОперации>Отпуск</d:ВидОперации>"
            + "<d:ДокументОснование_Key>00000000-0000-0000-0000-000000000000</d:ДокументОснование_Key>"
            + "<d:РасчитыватьОстаткиЗаВсеГода>false</d:РасчитыватьОстаткиЗаВсеГода>"
            + "<d:КоличествоЛет>0</d:КоличествоЛет>"
            + "<d:РаботникиОрганизации m:type='Collection(StandardODATA.Document_ОтпускаОрганизаций_РаботникиОрганизации_RowType)'>"
            + "<d:element m:type='StandardODATA.Document_ОтпускаОрганизаций_РаботникиОрганизации_RowType'>"
            + "<d:LineNumber>1</d:LineNumber>"
            + "<d:Сотрудник_Key>a807e909-abfb-11dc-aa58-00112f3000a2</d:Сотрудник_Key>"
            + "<d:ФизЛицо_Key>79c4fdca-71ed-11d9-ac6c-0080482454f7</d:ФизЛицо_Key>"
            + "<d:ОсвобождатьСтавку>false</d:ОсвобождатьСтавку>"
            + "d:ДатаНачала>2017-04-01T00:00:00</d:ДатаНачала>"
            + "<d:ДатаОкончания>2017-04-15T00:00:00</d:ДатаОкончания>"
            + "<d:Основание/>"
            + "<d:КвоДней>15</d:КвоДней>"
            + "<d:РабочийГодС>0001-01-01T00:00:00</d:РабочийГодС>"
            + "<d:РабочийГодПо>0001-01-01T00:00:00</d:РабочийГодПо>"
            + "<d:НапомнитьПоЗавершении>false</d:НапомнитьПоЗавершении>"
            + "<d:ВидОтпуска_Key>e8d50a27-8506-4dec-9584-531961cf445d</d:ВидОтпуска_Key>"
            + "</d:element>"
            + "</d:РаботникиОрганизации>"
            + "<d:ИспользованиеЕжегодногоОтпуска m:type='Collection(StandardODATA.Document_ОтпускаОрганизаций_ИспользованиеЕжегодногоОтпуска_RowType)'/>"
            + "</m:properties>"
            + "</content>"
            + "</entry>"
            + "</feed>";*/