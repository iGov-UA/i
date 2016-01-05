package org.igov.service.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.igov.util.convert.JsonRestUtils;
import org.igov.model.MerchantDao;
import org.igov.model.SubjectOrganDao;
import org.igov.model.Merchant;
import org.igov.model.SubjectOrgan;
import org.igov.model.MerchantVO;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponses;
import io.swagger.annotations.ApiResponse;

import java.util.ArrayList;
import java.util.List;

@Controller
@Api(tags = { "ActivitiRestMerchantController" }, description = "Работа с мерчантами")
@RequestMapping(value = "/merchant")
public class MerchantController {
    ///////////////////////////////////////////////////////////////////////////////////////////////////////
    // Подробные описания сервисов для документирования в Swagger
    ///////////////////////////////////////////////////////////////////////////////////////////////////////
    private static final String noteCODE= "\n```\n";    
    private static final String noteCODEJSON= "\n```json\n";    
    private static final String noteController = "##### Работа с мерчантами. ";

    private static final String noteGetMerchants = noteController + "Получить весь список обьектов мерчантов #####\n\n"
        + "HTTP Context: https://server:port/wf/service/merchant/getMerchants\n\n\n"
        + "Response\n\n"
        + noteCODEJSON
        + "[\n"
        + "    {\n"
        + "        \"nID\":1\n"
        + "        ,\"sID\":\"Test_sID\"\n"
        + "        ,\"sName\":\"Test_sName\"\n"
        + "        ,\"sPrivateKey\":\"test_sPrivateKey\"\n"
        + "        ,\"sURL_CallbackStatusNew\":\"test_sURL_CallbackStatusNew\"\n"
        + "        ,\"sURL_CallbackPaySuccess\":\"test_sURL_CallbackPaySuccess\"\n"
        + "        ,\"nID_SubjectOrgan\":1\n"
        + "    }\n"
        + "    ,{\n"
        + "        \"nID\":2\n"
        + "        ,\"sID\":\"i10172968078\"\n"
        + "        ,\"sName\":\"igov test\"\n"
        + "        ,\"sPrivateKey\":\"BStHb3EMmVSYefW2ejwJYz0CY6rDVMj1ZugJdZ2K\"\n"
        + "        ,\"sURL_CallbackStatusNew\":\"test_sURL_CallbackStatusNew\"\n"
        + "        ,\"sURL_CallbackPaySuccess\":\"test_sURL_CallbackPaySuccess\"\n"
        + "        ,\"nID_SubjectOrgan\":1\n"
        + "    }\n"
        + "]\n"
        + noteCODE
        + "Пример:\n"
        + "https://test.igov.org.ua/wf/service/merchant/getMerchants";

    private static final String noteGetMerchant = noteController + "Получить обьект мерчанта #####\n\n"
        + "HTTP Context: https://server:port/wf/service/merchant/getMerchant\n\n\n"
        + "- sID - ID-строка мерчанта(публичный ключ)\n\n\n"
        + noteCODEJSON
        + "Response\n"
        + "{\n"
        + "    \"nID\":1\n"
        + "    ,\"sID\":\"Test_sID\"\n"
        + "    ,\"sName\":\"Test_sName\"\n"
        + "    ,\"sPrivateKey\":\"test_sPrivateKey\"\n"
        + "    ,\"sURL_CallbackStatusNew\":\"test_sURL_CallbackStatusNew\"\n"
        + "    ,\"sURL_CallbackPaySuccess\":\"test_sURL_CallbackPaySuccess\"\n"
        + "    ,\"nID_SubjectOrgan\":1\n"
        + "}\n"
        + noteCODE
        + "Пример:\n"
        + "https://test.igov.org.ua/wf/service/merchant/getMerchant?sID=i10172968078";

    private static final String noteRemoveMerchant = noteController + "Удаление мерчанта #####\n\n"
        + "HTTP Context: http://server:port/wf/service/merchant/removeMerchant\n\n\n"
        + "- sID - ID-строка мерчанта(публичный ключ)\n\n\n"
        + "Response\n"
        + "Status 200\n\n"
        + "Пример:\n"
        + "https://test.igov.org.ua/wf/service/merchant/removeMerchant?sID=i10172968078";

    private static final String noteSetMerchant = noteController + "Обновление информации мерчанта #####\n\n"
        + "HTTP Context: http://server:port/wf/service/merchant/setMerchant\n\n\n"
        + "- nID - ID-номер мерчанта(внутренний) //опциональный (если не задан или не найден - будет добавлена запись)\n"
        + "- sID - ID-строка мерчанта(публичный ключ) //опциональный (если не задан или не найден - будет добавлена запись)\n"
        + "- sName - строковое название мерчанта //опциональный (при добавлении записи - обязательный)\n"
        + "- sPrivateKey - приватный ключ мерчанта //опциональный (при добавлении записи - обязательный)\n"
        + "- nID_SubjectOrgan - ID-номер субьекта-органа мерчанта(может быть общий субьект у нескольких мерчантов) //опциональный\n"
        + "- sURL_CallbackStatusNew - строка-URL каллбэка, при новом статусе платежа(проведении проплаты) //опциональный\n"
        + "- sURL_CallbackPaySuccess - строка-URL каллбэка, после успешной отправки платежа //опциональный\n\n\n"
        + "Response\n"
        + noteCODEJSON
        + "{\n"
        + "    \"nID\":1\n"
        + "    ,\"sID\":\"Test_sID\"\n"
        + "    ,\"sName\":\"Test_sName22\"\n"
        + "    ,\"sPrivateKey\":\"test_sPrivateKey\"\n"
        + "    ,\"sURL_CallbackStatusNew\":\"test_sURL_CallbackStatusNew\"\n"
        + "    ,\"sURL_CallbackPaySuccess\":\"test_sURL_CallbackPaySuccess\"\n"
        + "    ,\"nID_SubjectOrgan\":1\n"
        + "}\n"
        + noteCODE
        + "Примеры обновления:\n"
        + "https://test.igov.org.ua/wf/service/merchant/setMerchant?sID=Test_sID&sName=Test_sName2\n"
        + "https://test.igov.org.ua/wf/service/merchant/setMerchant?nID=1&sName=Test_sName22\n\n"
        + "Пример добавления:\n"
        + "https://test.igov.org.ua/wf/service/merchant/setMerchant?sID=Test_sID3&sName=Test_sName3&sPrivateKey=121212";
    ///////////////////////////////////////////////////////////////////////////////////////////////////////

    
    @Autowired
    private MerchantDao merchantDao;

    @Autowired
    private SubjectOrganDao subjectOrganDao;

    /**
     * получить весь список обьектов мерчантов
     */
    @ApiOperation(value = "Получить весь список обьектов мерчантов", notes = noteGetMerchants )
    @RequestMapping(value = "/getMerchants", method = RequestMethod.GET)
    public
    @ResponseBody
    ResponseEntity getMerchants() {
        return JsonRestUtils.toJsonResponse(toVO(merchantDao.findAll()));
    }

    /**
     * получить обьект мерчанта
     * @param sID ID-строка мерчанта(публичный ключ)
     */
    @ApiOperation(value = "Получить обьект мерчанта", notes = noteGetMerchant )
    @RequestMapping(value = "/getMerchant", method = RequestMethod.GET)
    public
    @ResponseBody
    ResponseEntity getMerchant( @ApiParam(value = "ID-строка мерчанта(публичный ключ)", required = true) @RequestParam(value = "sID") String sID) {
        Merchant merchant = merchantDao.getMerchant(sID);
        if (merchant == null) {
            return new ResponseEntity("Merchant with sID=" + sID + " is not found!", HttpStatus.NOT_FOUND);
        }

        return JsonRestUtils.toJsonResponse(new MerchantVO(merchant));
    }

    /**
     * удалить мерчанта
     * @param id ID-строка мерчанта(публичный ключ)
     */
    @ApiOperation(value = "Удаление мерчанта", notes = noteRemoveMerchant )
    @RequestMapping(value = "/removeMerchant", method = RequestMethod.DELETE)
    public ResponseEntity deleteMerchant( @ApiParam(value = "ID-строка мерчанта(публичный ключ)", required = true) @RequestParam(value = "sID") String id) {
        return new ResponseEntity(merchantDao.deleteMerchant(id) ? HttpStatus.OK : HttpStatus.NOT_FOUND);
    }

    /**
     * обновить информацию мерчанта
     * @param nID ID-номер мерчанта(внутренний) //опциональный (если не задан или не найден - будет добавлена запись)
     * @param sID ID-строка мерчанта(публичный ключ) //опциональный (если не задан или не найден - будет добавлена запись)
     * @param sName строковое название мерчанта //опциональный (при добавлении записи - обязательный)
     * @param sPrivateKey приватный ключ мерчанта //опциональный (при добавлении записи - обязательный)
     * @param nID_SubjectOrgan ID-номер субьекта-органа мерчанта(может быть общий субьект у нескольких мерчантов) //опциональный
     * @param sURL_CallbackStatusNew строка-URL каллбэка, при новом статусе платежа(проведении проплаты) //опциональный
     * @param sURL_CallbackPaySuccess строка-URL каллбэка, после успешной отправки платежа //опциональный
     */
    @ApiOperation(value = "Обновление информации мерчанта", notes = noteSetMerchant )
   @RequestMapping(value = "/setMerchant", method = RequestMethod.POST)
    public ResponseEntity setMerchant(
	    @ApiParam(value = "ID-номер мерчанта(внутренний) (если не задан или не найден - будет добавлена запись)", required = false) @RequestParam(value = "nID", required = false) Long nID,
	    @ApiParam(value = "ID-строка мерчанта(публичный ключ) (если не задан или не найден - будет добавлена запись)", required = false) @RequestParam(value = "sID", required = false) String sID,
	    @ApiParam(value = "строковое название мерчанта (при добавлении записи - обязательный)", required = false) @RequestParam(value = "sName", required = false) String sName,
	    @ApiParam(value = "sPrivateKey приватный ключ мерчанта (при добавлении записи - обязательный)", required = false) @RequestParam(value = "sPrivateKey", required = false) String sPrivateKey,
	    @ApiParam(value = "ID-номер субьекта-органа мерчанта(может быть общий субьект у нескольких мерчантов)", required = false) @RequestParam(value = "nID_SubjectOrgan", required = false) Long nID_SubjectOrgan,
	    @ApiParam(value = "строка-URL каллбэка, при новом статусе платежа(проведении проплаты)", required = false) @RequestParam(value = "sURL_CallbackStatusNew", required = false) String sURL_CallbackStatusNew,
	    @ApiParam(value = "строка-URL каллбэка, после успешной отправки платежа", required = false) @RequestParam(value = "sURL_CallbackPaySuccess", required = false) String sURL_CallbackPaySuccess) {

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

        merchant = merchantDao.saveOrUpdate(merchant);
        return JsonRestUtils.toJsonResponse(new MerchantVO(merchant));
    }

    private List<MerchantVO> toVO(List<Merchant> merchants) {
        List<MerchantVO> res = new ArrayList<>();
        for (Merchant merchant : merchants) {
            res.add(new MerchantVO(merchant));
        }

        return res;
    }
}