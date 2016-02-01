package org.igov.service.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.igov.util.Tool;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import java.util.List;
import org.igov.model.finance.Merchant;
import org.igov.model.finance.MerchantDao;
import org.igov.service.business.finance.MerchantVO;
import org.igov.model.subject.organ.SubjectOrgan;
import org.igov.model.subject.organ.SubjectOrganDao;
import org.igov.service.business.finance.Currency;
import org.igov.service.business.object.Language;
import org.igov.model.finance.CurrencyDao;
import static org.igov.service.business.finance.FinanceService.toVO;
import org.igov.service.business.finance.Liqpay;
import org.igov.service.exception.CommonServiceException;
import org.igov.util.JSON.JsonRestUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

@Api(tags = {"FinanceCentralController"}, description = "Финансовые и смежные сущности")
@Controller
@RequestMapping(value = "/finance")
public class FinanceCentralController {

    private final Logger LOG = LoggerFactory.getLogger(FinanceCentralController.class);

    @Autowired
    private Liqpay oLiqpay;

    @Autowired
    private MerchantDao merchantDao;

    @Autowired
    private SubjectOrganDao subjectOrganDao;

    @Autowired
    private CurrencyDao currencyDao;

    private StringBuffer sb = new StringBuffer();

    /*@ApiOperation(value = "Установка статуса платежа Ликпея", notes = "Устанавливает статус платежа Ликпея")
    @RequestMapping(value = "/setPaymentNewStatus_Liqpay", method = RequestMethod.GET, headers = {
        "Accept=application/json"})
    public @ResponseBody
    @Deprecated
    String setPaymentNewStatus_Liqpay(
            @ApiParam(value = "строка-ИД заявки", required = true) @RequestParam String sID_Order,
            @ApiParam(value = "строка хоста", required = true) @RequestParam String sHost) {
        sb.append(sHost);
        String data = "data"; // вместо "data" подставить ответ вызова API
        String t = "";            // liqpay
        sb.append("sID_Order=");
        sb.append(sID_Order);
        sb.append("&sData=");
        sb.append(data);
        sb.append("&sID_PaymentSystem=Liqpay");
        try {
            if (sID_Order.startsWith("TaskActiviti_")) {
                t = setPaymentStatus_TaskActiviti(sHost, sb.toString(), data);
            }
        } catch (Exception e) {
            LOG.error("HttpAnswer error:", e);
        }
        return t + "/";
    }*/

    /**
     * @param sID_Merchant ид меранта
     * @param sSum сумма оплаты
     * @param oID_Currency валюта
     * @param oLanguage язык
     * @param sDescription описание
     * @param sID_Order ид заказа
     * @param sURL_CallbackStatusNew URL для отправки статуса
     * @param sURL_CallbackPaySuccess URL для отправки ответа
     * @param nID_Subject ид субъекта
     * @param bTest тестовый вызов или нет
     */
    /*@ApiOperation(value = "Получение кнопки для оплаты через LiqPay", notes = "##### FinanceController - Финансовые и смежные сущности. Получение кнопки для оплаты через LiqPay #####\n\n"
            + "HTTP Context: https://server:port/wf/service/finance/getPayButtonHTML_LiqPay\n\n\n"
            + "Пример:\n"
            + "https://test.igov.org.ua/wf/service/finance/getPayButtonHTML_LiqPay?sID_Merchant=i10172968078&sSum=55,00&oID_Currency=UAH&oLanguage=RUSSIAN&sDescription=test&sID_Order=12345&sURL_CallbackStatusNew=&sURL_CallbackPaySuccess=&nID_Subject=1&bTest=true\n")
    @RequestMapping(value = "/getPayButtonHTML_LiqPay", method = RequestMethod.GET)
    public @ResponseBody
    @Deprecated
    String getPayButtonHTML_LiqPay(
            @ApiParam(value = "ид мерчанта", required = true) @RequestParam(value = "sID_Merchant", required = true) String sID_Merchant,
            @ApiParam(value = "сумма оплаты", required = true) @RequestParam(value = "sSum", required = true) String sSum,
            @ApiParam(value = "валюта", required = true) @RequestParam(value = "oID_Currency", required = true) Currency oID_Currency,
            @ApiParam(value = "язык", required = true) @RequestParam(value = "oLanguage", required = true) Language oLanguage,
            @ApiParam(value = "описание", required = true) @RequestParam(value = "sDescription", required = true) String sDescription,
            @ApiParam(value = "ид заказа", required = true) @RequestParam(value = "sID_Order", required = true) String sID_Order,
            @ApiParam(value = "URL для отправки статуса", required = false) @RequestParam(value = "sURL_CallbackStatusNew", required = false) String sURL_CallbackStatusNew,
            @ApiParam(value = "URL для отправки ответа", required = false) @RequestParam(value = "sURL_CallbackPaySuccess", required = false) String sURL_CallbackPaySuccess,
            @ApiParam(value = "ид субъекта", required = true) @RequestParam(value = "nID_Subject", required = true) Long nID_Subject,
            @ApiParam(value = "тестовый вызов или нет", required = true) @RequestParam(value = "bTest", required = true) boolean bTest) throws Exception {

        return oLiqpay.getPayButtonHTML_LiqPay(sID_Merchant, sSum,
                oID_Currency, oLanguage, sDescription, sID_Order,
                sURL_CallbackStatusNew, sURL_CallbackStatusNew,
                nID_Subject, true);
    }*/

    /*private String setPaymentStatus_TaskActiviti(String sHost, String url, String sData) throws Exception {
        return Tool.httpAnswer(sb.toString(), sData);
    }*/

    /**
     * получить весь список обьектов мерчантов
     */
    @ApiOperation(value = "Получить весь список обьектов мерчантов", notes = "##### FinanceController - Финансовые и смежные сущности. Получение всего списка обьектов мерчантов #####\n\n"
            + "HTTP Context: https://server:port/wf/service/finance/getMerchants\n\n\n"
            + "Response\n\n"
            + "\n```json\n"
            + "[\n"
            + "    {\n"
            + "        \"nID\":1\n"
            + "        ,\"sID\":\"Test_sID\"\n"
            + "        ,\"sName\":\"Test_sName\"\n"
            + "        ,\"sPrivateKey\":\"test_sPrivateKey\"\n"
            + "        ,\"sURL_CallbackStatusNew\":\"test_sURL_CallbackStatusNew\"\n"
            + "        ,\"sURL_CallbackPaySuccess\":\"test_sURL_CallbackPaySuccess\"\n"
            + "        ,\"nID_SubjectOrgan\":1\n"
            + "        ,\"sID_Currency\":\"UAH\"\n"
            + "    }\n"
            + "    ,{\n"
            + "        \"nID\":2\n"
            + "        ,\"sID\":\"i10172968078\"\n"
            + "        ,\"sName\":\"igov test\"\n"
            + "        ,\"sPrivateKey\":\"BStHb3EMmVSYefW2ejwJYz0CY6rDVMj1ZugJdZ2K\"\n"
            + "        ,\"sURL_CallbackStatusNew\":\"test_sURL_CallbackStatusNew\"\n"
            + "        ,\"sURL_CallbackPaySuccess\":\"test_sURL_CallbackPaySuccess\"\n"
            + "        ,\"nID_SubjectOrgan\":1\n"
            + "        ,\"sID_Currency\":\"UAH\"\n"
            + "    }\n"
            + "]\n"
            + "\n```\n"
            + "Пример:\n"
            + "https://test.igov.org.ua/wf/service/finance/getMerchants")
    @RequestMapping(value = "/getMerchants", method = RequestMethod.GET)
    public @ResponseBody
    ResponseEntity getMerchants() {
        return JsonRestUtils.toJsonResponse(toVO(merchantDao.findAll()));
    }

    /**
     * получить обьект мерчанта
     *
     * @param sID ID-строка мерчанта(публичный ключ)
     */
    @ApiOperation(value = "Получить обьект мерчанта", notes = "##### FinanceController - Финансовые и смежные сущности. Получение обьекта мерчанта #####\n\n"
            + "HTTP Context: https://server:port/wf/service/finance/getMerchant\n\n\n"
            + "\n```json\n"
            + "Response\n"
            + "{\n"
            + "    \"nID\":1\n"
            + "    ,\"sID\":\"Test_sID\"\n"
            + "    ,\"sName\":\"Test_sName\"\n"
            + "    ,\"sPrivateKey\":\"test_sPrivateKey\"\n"
            + "    ,\"sURL_CallbackStatusNew\":\"test_sURL_CallbackStatusNew\"\n"
            + "    ,\"sURL_CallbackPaySuccess\":\"test_sURL_CallbackPaySuccess\"\n"
            + "    ,\"nID_SubjectOrgan\":1\n"
            + "    ,\"sID_Currency\":\"UAH\"\n"
            + "}\n"
            + "\n```\n"
            + "Пример:\n"
            + "https://test.igov.org.ua/wf/service/finance/getMerchant?sID=i10172968078")
    @RequestMapping(value = "/getMerchant", method = RequestMethod.GET)
    public @ResponseBody
    ResponseEntity getMerchant(@ApiParam(value = "ID-строка мерчанта(публичный ключ)", required = true) @RequestParam(value = "sID") String sID) {
        Merchant merchant = merchantDao.getMerchant(sID);
        if (merchant == null) {
            return new ResponseEntity("Merchant with sID=" + sID + " is not found!", HttpStatus.NOT_FOUND);
        }

        return JsonRestUtils.toJsonResponse(new MerchantVO(merchant));
    }

    /**
     * удалить мерчанта
     *
     * @param id ID-строка мерчанта(публичный ключ)
     */
    @ApiOperation(value = "Удаление мерчанта", notes = "##### FinanceController - Финансовые и смежные сущности. Удаление мерчанта #####\n\n"
            + "HTTP Context: http://server:port/wf/service/finance/removeMerchant\n\n\n"
            + "Response\n"
            + "Status 200\n\n"
            + "Пример:\n"
            + "https://test.igov.org.ua/wf/service/finance/removeMerchant?sID=i10172968078")
    @RequestMapping(value = "/removeMerchant", method = RequestMethod.DELETE)
    public ResponseEntity removeMerchant(@ApiParam(value = "ID-строка мерчанта(публичный ключ)", required = true) @RequestParam(value = "sID") String id) {
        return new ResponseEntity(merchantDao.deleteMerchant(id) ? HttpStatus.OK : HttpStatus.NOT_FOUND);
    }

    /**
     * обновить информацию мерчанта
     *
     * @param nID ID-номер мерчанта(внутренний) //опциональный (если не задан
     * или не найден - будет добавлена запись)
     * @param sID ID-строка мерчанта(публичный ключ) //опциональный (если не
     * задан или не найден - будет добавлена запись)
     * @param sName строковое название мерчанта //опциональный (при добавлении
     * записи - обязательный)
     * @param sPrivateKey приватный ключ мерчанта //опциональный (при добавлении
     * записи - обязательный)
     * @param nID_SubjectOrgan ID-номер субьекта-органа мерчанта(может быть
     * общий субьект у нескольких мерчантов) //опциональный
     * @param sURL_CallbackStatusNew строка-URL каллбэка, при новом статусе
     * платежа(проведении проплаты) //опциональный
     * @param sURL_CallbackPaySuccess строка-URL каллбэка, после успешной
     * отправки платежа //опциональный
     */
    @ApiOperation(value = "Обновление информации мерчанта", notes = "##### FinanceController - Финансовые и смежные сущности. Обновление информации мерчанта #####\n\n"
            + "HTTP Context: http://server:port/wf/service/finance/setMerchant\n\n\n"
            + "Response\n"
            + "\n```json\n"
            + "{\n"
            + "    \"nID\":1\n"
            + "    ,\"sID\":\"Test_sID\"\n"
            + "    ,\"sName\":\"Test_sName22\"\n"
            + "    ,\"sPrivateKey\":\"test_sPrivateKey\"\n"
            + "    ,\"sURL_CallbackStatusNew\":\"test_sURL_CallbackStatusNew\"\n"
            + "    ,\"sURL_CallbackPaySuccess\":\"test_sURL_CallbackPaySuccess\"\n"
            + "    ,\"nID_SubjectOrgan\":1\n"
            + "    ,\"sID_Currency\":\"UAH\"\n"
            + "}\n"
            + "\n```\n"
            + "Примеры обновления:\n"
            + "https://test.igov.org.ua/wf/service/finance/setMerchant?sID=Test_sID&sName=Test_sName2\n"
            + "https://test.igov.org.ua/wf/service/finance/setMerchant?nID=1&sName=Test_sName22\n\n"
            + "Пример добавления:\n"
            + "https://test.igov.org.ua/wf/service/finance/setMerchant?sID=Test_sID3&sName=Test_sName3&sPrivateKey=121212")
    @RequestMapping(value = "/setMerchant", method = RequestMethod.POST)
    public ResponseEntity setMerchant(
            @ApiParam(value = "ID-номер мерчанта(внутренний) (если не задан или не найден - будет добавлена запись)", required = false) @RequestParam(value = "nID", required = false) Long nID,
            @ApiParam(value = "ID-строка мерчанта(публичный ключ) (если не задан или не найден - будет добавлена запись)", required = false) @RequestParam(value = "sID", required = false) String sID,
            @ApiParam(value = "строковое название мерчанта (при добавлении записи - обязательный)", required = false) @RequestParam(value = "sName", required = false) String sName,
            @ApiParam(value = "sPrivateKey приватный ключ мерчанта (при добавлении записи - обязательный)", required = false) @RequestParam(value = "sPrivateKey", required = false) String sPrivateKey,
            @ApiParam(value = "ID-номер субьекта-органа мерчанта(может быть общий субьект у нескольких мерчантов)", required = false) @RequestParam(value = "nID_SubjectOrgan", required = false) Long nID_SubjectOrgan,
            @ApiParam(value = "строка-URL каллбэка, при новом статусе платежа(проведении проплаты)", required = false) @RequestParam(value = "sURL_CallbackStatusNew", required = false) String sURL_CallbackStatusNew,
            @ApiParam(value = "строка-URL каллбэка, после успешной отправки платежа", required = false) @RequestParam(value = "sURL_CallbackPaySuccess", required = false) String sURL_CallbackPaySuccess,
            @ApiParam(value = "международный строковой трехсимвольный код валюты", required = false) @RequestParam(value = "sID_Currency", required = false) String sID_Currency) {


        Merchant merchant = nID != null ? merchantDao.findById(nID).orNull() : new Merchant();

        if (merchant == null) {
            merchant = new Merchant();
        }

        if (sID != null) {
            merchant.setsID(sID);
        }

        if (sName != null) {
            merchant.setName(sName);
        }

        if (sPrivateKey != null) {
            merchant.setsPrivateKey(sPrivateKey);
        }

        if (nID_SubjectOrgan != null) {
            SubjectOrgan subjectOrgan = subjectOrganDao.findByIdExpected(nID_SubjectOrgan);
            merchant.setOwner(subjectOrgan);
        }

        if (sURL_CallbackStatusNew != null) {
            merchant.setsURL_CallbackStatusNew(sURL_CallbackStatusNew);
        }

        if (sURL_CallbackPaySuccess != null) {
            merchant.setsURL_CallbackPaySuccess(sURL_CallbackPaySuccess);
        }

        if (sID_Currency != null) {
            merchant.setsID_Currency(sID_Currency);
        }

        merchant = merchantDao.saveOrUpdate(merchant);
        return JsonRestUtils.toJsonResponse(new MerchantVO(merchant));
    }

    /**
     * отдает список объектов сущности, подпадающих под критерии параметры.
     *
     * @param sID_UA (опциональный)
     * @param sName_UA (опциональный)
     * @param sName_EN (опциональный)
     * @return список Currency согласно фильтрам
     */
    @ApiOperation(value = "Возвращает список валют, подпадающих под параметры", notes = "##### FinanceController - Финансовые и смежные сущности. Возврат списка валют, подпадающих под параметры #####\n\n"
            + "HTTP Context: https://server:port/wf/service/finance/getCurrencies\n\n"
            + "http://search.ligazakon.ua/l_doc2.nsf/link1/FIN14565.html[Источник данных]\n\n"
            + "Пример запроса: https://test.igov.org.ua/wf/service/finance/getCurrencies?sID_UA=004\n\n"
            + "Пример ответа:\n\n"
            + "\n```json\n"
            + "{\n"
            + "    \"sID_UA\"       : \"004\",\n"
            + "    \"sName_UA\"     : \"Афґані\",\n"
            + "    \"sName_EN\"     : \"Afghani\",\n"
            + "    \"nID\"          : 1\n"
            + "    \"sID_Currency\" : \"AFA\"\n"
            + "}\n"
            + "\n```\n")
    @RequestMapping(value = "/getCurrencies", method = RequestMethod.GET)
    public @ResponseBody
    List<org.igov.model.finance.Currency> getCurrencies(
            @ApiParam(value = "ИД-номер Код, в украинском классификаторе", required = false) @RequestParam(value = "sID_UA", required = false) String sID_UA,
            @ApiParam(value = "Название на украинском", required = false) @RequestParam(value = "sName_UA", required = false) String sName_UA,
            @ApiParam(value = "Название на английском", required = false) @RequestParam(value = "sName_EN", required = false) String sName_EN,
            @ApiParam(value = "Международный строковой трехсимвольный код валюты", required = false) @RequestParam(value = "sID_Currency", required = false) String sID_Currency) {

        return currencyDao.getCurrencies(sID_UA, sName_UA, sName_EN, sID_Currency);
    }

    /**
     * обновляет элемент (если задан один из уникальных-ключей) или вставляет
     * (если не задан nID), и отдает экземпляр нового объекта.
     *
     * @param nID (опциональный, если другой уникальный-ключ задан и по нему
     * найдена запись)
     * @param sID_UA (опциональный, если другой уникальный-ключ задан и по нему
     * найдена запись)
     * @param sName_UA (опциональный, если nID задан и по нему найдена запись)
     * @param sName_EN (опциональный, если nID задан и по нему найдена запись)
     * @return обновленный/вставленный обьект
     */
    @ApiOperation(value = "обновляет запись валюты", notes = "##### FinanceController - Финансовые и смежные сущности. Обновление записи валюты #####\n\n"
            + "HTTP Context: https://server:port/wf/service/finance/setCurrency\n\n"
            + "обновляет запись (если задан один из параметров: nID, sID_UA; и по нему найдена запись) или вставляет (если не задан nID), и отдает экземпляр нового объекта\n\n"
            + "http://search.ligazakon.ua/l_doc2.nsf/link1/FIN14565.html[Источник данных]\n\n"
            + "Пример добавления записи:\n\n"
            + "https://test.igov.org.ua/wf/service/finance/setCurrency?sID_UA=050&sName_UA=Така&sName_EN=Taka&sID_Currency=BDT\n\n"
            + "Пример обновления записи:\n\n"
            + "https://test.igov.org.ua/wf/service/finance/setCurrency?sID_UA=050&sName_UA=Така\n\n")
    @RequestMapping(value = "/setCurrency", method = RequestMethod.GET)
    public @ResponseBody
    org.igov.model.finance.Currency setCurrency(
            @ApiParam(value = "внутренний ИД-номер (уникальный; если sID_UA задан и по нему найдена запись)", required = false) @RequestParam(value = "nID", required = false) Long nID,
            @ApiParam(value = "ИД-номер Код, в украинском классификаторе (уникальный; если nID задан и по нему найдена запись)", required = false) @RequestParam(value = "sID_UA", required = false) String sID_UA,
            @ApiParam(value = "название на украинском (уникальный; если nID задан и по нему найдена запись)", required = false) @RequestParam(value = "sName_UA", required = false) String sName_UA,
            @ApiParam(value = "название на английском (уникальный; если nID задан и по нему найдена запись)", required = false) @RequestParam(value = "sName_EN", required = false) String sName_EN,
            @ApiParam(value = "международный строковой трехсимвольный код валюты (уникальный; если nID задан и по нему найдена запись)", required = false) @RequestParam(value = "sID_Currency", required = false) String sID_Currency)
            throws CommonServiceException {

        try {
            org.igov.model.finance.Currency currency = null;
            if (nID != null) {
                currency = currencyDao.findByIdExpected(nID);
            }
            if (sID_UA != null) {
                currency = currencyDao.findBy("sID_UA", sID_UA).orNull();
            }
            if (currency == null) {
                if (sID_UA == null || sName_UA == null || sName_EN == null || sID_Currency == null) {
                    throw new IllegalArgumentException(
                            "Currency by key params was not founded. "
                            + "Not enough params to insert.");
                }
                currency = new org.igov.model.finance.Currency();
            }
            if (sID_UA != null) {
                currency.setsID_UA(sID_UA);
            }
            if (sName_UA != null) {
                currency.setsName_UA(sName_UA);
            }
            if (sName_EN != null) {
                currency.setsName_EN(sName_EN);
            }
            if (sID_Currency != null) {
                currency.setsID_Currency(sID_Currency);
            }
            return currencyDao.saveOrUpdate(currency);

        } catch (Exception e) {
            LOG.warn("Error: {}", e.getMessage());
            LOG.trace("FAIL:",  e);
            throw new CommonServiceException(
                    "SYSTEM_ERR",
                    e.getMessage(),
                    e,
                    HttpStatus.FORBIDDEN);
        }
    }

    /**
     * удаляет элемент (по обязательно заданому одному из уникальных-ключей).
     *
     * @param nID (опциональный, если другой уникальный-ключ задан и по нему
     * найдена запись)
     * @param sID_UA (опциональный, если другой уникальный-ключ задан и по нему
     * найдена запись)
     */
    @ApiOperation(value = "Удаление элемента по обязательно заданному одному из параметров", notes = "##### FinanceController - Финансовые и смежные сущности. Удаление элемента по обязательно заданному одному из параметров #####\n\n"
            + "HTTP Context: https://server:port/wf/service/finance/removeCurrency\n\n"
            + "http://search.ligazakon.ua/l_doc2.nsf/link1/FIN14565.html[Источник данных]\n\n"
            + "Пример запроса:\n"
            + "https://test.igov.org.ua/wf/service/finance/removeCurrency?sID_UA=050\n")
    @RequestMapping(value = "/removeCurrency", method = RequestMethod.GET)
    public @ResponseBody
    void removeCurrency(
            @ApiParam(value = "внутренний ИД-номер (уникальный; если sID_UA задан и по нему найдена запись)", required = false) @RequestParam(value = "nID", required = false) Long nID,
            @ApiParam(value = "ИД-номер Код, в украинском классификаторе (уникальный; если nID задан и по нему найдена запись)", required = false) @RequestParam(value = "sID_UA", required = false) String sID_UA)
            throws CommonServiceException {
        try {

            if (nID == null && sID_UA == null) {
                throw new IllegalArgumentException("Key param was not specified");
            }
            if (nID != null && sID_UA != null) {
                throw new IllegalArgumentException("Too many params");
            }
            if (nID != null) {
                currencyDao.delete(nID);
            } else {
                currencyDao.deleteBy("sID_UA", sID_UA);
            }

        } catch (Exception e) {
            LOG.warn("Error: {}", e.getMessage());
            LOG.trace("FAIL:",  e);
            throw new CommonServiceException(
                    "SYSTEM_ERR",
                    e.getMessage(),
                    e,
                    HttpStatus.FORBIDDEN);
        }
    }

}
