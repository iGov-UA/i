package org.igov.service.controller;

import org.slf4j.Logger;import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.igov.util.convert.JsonRestUtils;
import org.igov.model.CountryDao;
import org.igov.model.Country;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponses;
import io.swagger.annotations.ApiResponse;

import javax.servlet.http.HttpServletResponse;
import java.util.List;
import static org.igov.util.Util.areAllArgsNull;

@Controller
@Api(tags = { "ActivitiRestCountryController" }, description = "Работа со странами")
@RequestMapping(value = "/services")
public class CountryController {
    private static final Logger LOG = LoggerFactory.getLogger(CountryController.class);

    ///////////////////////////////////////////////////////////////////////////////////////////////////////
    // Подробные описания сервисов для документирования в Swagger
    ///////////////////////////////////////////////////////////////////////////////////////////////////////
    private static final String noteCODE= "\n```\n";    
    private static final String noteCODEJSON= "\n```json\n";    
    private static final String noteController = "##### Работа со странами. ";

    private static final String noteGetCountries = noteController + "Возвращает весь список стран #####\n\n"
		+ "HTTP Context: https://server:port/wf/service/services/getCountries\n\n"
		+ "(залит справочник согласно Википеции и Класифікації країн світу)\n\n"
		+ "пример:\n"
		+ "https://test.igov.org.ua/wf/service/services/getCountries";


    private static final String noteGetCountry = noteController + "Возвращает объект Страны по первому из 4х ключей (nID, nID_UA, sID_Two, sID_Three) #####\n\n"
		+ "HTTP Context: https://server:port/wf/service/services/getCountry\n\n"
		+ "Если нет ни одного параметра возвращает ошибку 403. required at least one of parameters (nID, nID_UA, sID_Two, sID_Three)!\n\n"
		+ "Eсли задано два ключа от разных записей -- вернется та, ключ который \"первее\" в таком порядке: nID, nID_UA, sID_Two, sID_Three.\n\n"
		+ "пример: https://test.igov.org.ua/wf/service/services/getCountry?nID_UA=123\n\n"
		+ "Ответ:\n"
		+ noteCODEJSON
		+ "{\n"
		+ "  \"nID_UA\":123,\n"
		+ "  \"sID_Two\":\"AU\",\n"
		+ "  \"sID_Three\":\"AUS\",\n"
		+ "  \"sNameShort_UA\":\"Австралія\",\n"
		+ "  \"sNameShort_EN\":\"Australy\",\n"
		+ "  \"sReference_LocalISO\":\"ISO_3166-2:AU\",\n"
		+ "  \"nID\":20340\n"
		+ "}\n"
		+ noteCODE;

    private static final String noteSetCountry = noteController + "Изменить объект \"Cтрана\" #####\n\n"
		+ "HTTP Context: https://server:port/wf/service/services/setCountry\n\n"
		+ "апдейтит элемент (если задан один из уникальных ключей) или вставляет (если не задан nID), и отдает экземпляр нового объекта.\n\n"
		+ "Параметры:\n\n"
		+ "- nID - ИД-номер, идентификатор записи\n"
		+ "- nID_UA - ИД-номер Код, в Украинском классификаторе (уникальное)\n"
		+ "- sID_Two - ИД-строка Код-двухсимвольный, международный (уникальное, строка 2 символа)\n"
		+ "- sID_Three - ИД-строка Код-трехсимвольный, международный (уникальное, строка 3 символа)\n"
		+ "- sNameShort_UA - Назва країни, коротка, Украинская (уникальное, строка до 100 символов)\n"
		+ "- sNameShort_EN - Назва країни, коротка, англійською мовою (уникальное, строка до 100 символов)\n"
		+ "- sReference_LocalISO - Ссылка на локальный ISO-стандарт, с названием (a-teg с href) (строка до 100 символов)\n\n\n"
		+ "Если нет ни одного параметра возвращает ошибку 403. All args are null! Если есть один из уникальных ключей, но запись"
		+ " не найдена -- ошибка 403. Record not found! Если кидать \"новую\" запись с одним из уже существующих ключей nID_UA -- то обновится существующая запись по ключу nID_UA, если будет дублироваться другой ключ -- ошибка 403. Could not execute statement (из-за уникальных констрейнтов)";

    private static final String noteRemoveCountry = noteController + "Удалить обьект по одному из четырех ключей (nID, nID_UA, sID_Two, sID_Three) #####\n\n"
		+ "HTTP Context: https://server:port/wf/service/services/removeCountry\n"
		+ "удаляет обьект по одному из четырех ключей (nID, nID_UA, sID_Two, sID_Three) или кидает ошибку 403. Record not found!.\n";
    ///////////////////////////////////////////////////////////////////////////////////////////////////////

    
    @Autowired
    private CountryDao countryDao;

    /**
     * возвращает весь список стран (залит справочник согласно
     */
    @ApiOperation(value = "Возвращает весь список стран", notes = noteGetCountries )
    @RequestMapping(value = "/getCountries", method = RequestMethod.GET)
    public
    @ResponseBody
    List<Country> getCountries() {

        return countryDao.findAll();
    }

    /**
     * отдает элемент(по первому ненулевому из уникальных-ключей)
     *
     * @param nID_UA    (опциональный)
     * @param sID_Two   (опциональный)
     * @param sID_Three (опциональный)
     * @param response
     * @return list of countries according to filters
     */
    @ApiOperation(value = "Возвращает объект Страны по первому из 4х ключей (nID, nID_UA, sID_Two, sID_Three)", notes = noteGetCountry )
    @ApiResponses(value = { @ApiResponse(code = 403, message = "Не задано хотя бы одного ключа из: nID, nID_UA, sID_Two, sID_Three") } )
    @RequestMapping(value = "/getCountry", method = RequestMethod.GET)
    public
    @ResponseBody
    ResponseEntity<String> getCountry(
	    @ApiParam(value = "ИД-номер, идентификатор записи", required = false) @RequestParam(value = "nID", required = false) Long nID,
	    @ApiParam(value = "ИД-номер Код, в Украинском классификаторе (уникальное)", required = false) @RequestParam(value = "nID_UA", required = false) Long nID_UA,
	    @ApiParam(value = "ИД-строка Код-двухсимвольный, международный (уникальное, строка 2 символа)", required = false) @RequestParam(value = "sID_Two", required = false) String sID_Two,
	    @ApiParam(value = "ИД-строка Код-трехсимвольный, международный (уникальное, строка 3 символа)", required = false) @RequestParam(value = "sID_Three", required = false) String sID_Three,
            HttpServletResponse response) {

        if (areAllArgsNull(nID, nID_UA, sID_Two, sID_Three)) {
            response.setStatus(HttpStatus.FORBIDDEN.value());
            response.setHeader("Reason", "required at least one of parameters " +
                    "(nID, nID_UA, sID_Two, sID_Three)!");
            return null;
        }
        ResponseEntity<String> result = null;
        try {
            Country country = countryDao.getByKey(nID, nID_UA, sID_Two, sID_Three);
            result = JsonRestUtils.toJsonResponse(country);
        } catch (RuntimeException e) {
            LOG.warn(e.getMessage(), e);
            response.setStatus(HttpStatus.FORBIDDEN.value());
            response.setHeader("Reason", e.getMessage());
        }
        return result;
    }

    /**
     * апдейтит элемент(если задан один из уникальных-ключей)
     * или вставляет (если не задан nID), и отдает экземпляр нового объекта
     *
     * @param nID                 (опциональный, если другой уникальный-ключ задан и по нему найдена запись)
     * @param nID_UA              (опциональный, если другой уникальный-ключ задан и по нему найдена запись)
     * @param sID_Two             (опциональный, если другой уникальный-ключ задан и по нему найдена запись)
     * @param sID_Three           (опциональный, если другой уникальный-ключ задан и по нему найдена запись)
     * @param sNameShort_UA       (опциональный, если другой уникальный-ключ задан и по нему найдена запись)
     * @param sNameShort_EN       (опциональный, если другой уникальный-ключ задан и по нему найдена запись)
     * @param sReference_LocalISO (опциональный)
     * @param response
     * @return
     */
    @ApiOperation(value = "Изменить объект \"Cтрана\"", notes = noteSetCountry )
    @ApiResponses(value = { @ApiResponse(code = 403, message = "Нет ни одного параметра / запись не найдена / дублируется один из ключей: nID, sID_Two, sID_Three") } )
    @RequestMapping(value = "/setCountry", method = RequestMethod.GET)
    public
    @ResponseBody
    ResponseEntity<String> setCountry(
	    @ApiParam(value = "ИД-номер, идентификатор записи", required = false) @RequestParam(value = "nID", required = false) Long nID,
	    @ApiParam(value = "ИД-номер Код, в Украинском классификаторе (уникальное)", required = false) @RequestParam(value = "nID_UA", required = false) Long nID_UA,
	    @ApiParam(value = "ИД-строка Код-двухсимвольный, международный (уникальное, строка 2 символа)", required = false) @RequestParam(value = "sID_Two", required = false) String sID_Two,
	    @ApiParam(value = "ИД-строка Код-трехсимвольный, международный (уникальное, строка 3 символа)", required = false) @RequestParam(value = "sID_Three", required = false) String sID_Three,
	    @ApiParam(value = "Назва країни, коротка, Украинская (уникальное, строка до 100 символов)", required = false) @RequestParam(value = "sNameShort_UA", required = false) String sNameShort_UA,
	    @ApiParam(value = "Назва країни, коротка, англійською мовою (уникальное, строка до 100 символов)", required = false) @RequestParam(value = "sNameShort_EN", required = false) String sNameShort_EN,
	    @ApiParam(value = "Ссылка на локальный ISO-стандарт, с названием (a-teg с href) (строка до 100 символов)", required = false) @RequestParam(value = "sReference_LocalISO", required = false) String sReference_LocalISO,
            HttpServletResponse response) {

        if (areAllArgsNull(nID, nID_UA, sID_Two, sID_Three,
                sNameShort_UA, sNameShort_EN)) {
            response.setStatus(HttpStatus.FORBIDDEN.value());
            response.setHeader("Reason", "required at least one of parameters " +
                    "(nID, nID_UA, sID_Two, sID_Three, sNameShort_UA, sNameShort_EN)!");
        }

        ResponseEntity<String> result = null;

        try {
            Country country = countryDao.setCountry(nID, nID_UA, sID_Two, sID_Three,
                    sNameShort_UA, sNameShort_EN, sReference_LocalISO);
            result = JsonRestUtils.toJsonResponse(country);
        } catch (RuntimeException e) {
        	LOG.warn(e.getMessage(), e);
            response.setStatus(HttpStatus.FORBIDDEN.value());
            response.setHeader("Reason", e.getMessage());
        }
        return result;
    }

    /**
     * удаляет элемент(по одому из уникальных ключей)
     *
     * @param nID         -- опциональный, если другой уникальный-ключ задан и по нему найдена запись
     * @param nID_UA--    опциональный
     * @param sID_Two--   опциональный
     * @param sID_Three-- опциональный
     * @param response
     */
    @ApiOperation(value = "Удалить обьект по одному из четырех ключей (nID, nID_UA, sID_Two, sID_Three)", notes = noteRemoveCountry )
    @ApiResponses(value = { @ApiResponse(code = 403, message = "Record not found") } )
    @RequestMapping(value = "/removeCountry", method = RequestMethod.GET)
    public
    @ResponseBody
    void removeCountry(
	    @ApiParam(value = "ИД-номер, идентификатор записи", required = false) @RequestParam(value = "nID", required = false) Long nID,
	    @ApiParam(value = "ИД-номер Код, в Украинском классификаторе (уникальное)", required = false) @RequestParam(value = "nID_UA", required = false) Long nID_UA,
	    @ApiParam(value = "ИД-строка Код-двухсимвольный, международный (уникальное, строка 2 символа)", required = false) @RequestParam(value = "sID_Two", required = false) String sID_Two,
	    @ApiParam(value = "ИД-строка Код-трехсимвольный, международный (уникальное, строка 3 символа)", required = false) @RequestParam(value = "sID_Three", required = false) String sID_Three,
            HttpServletResponse response) {

        if (areAllArgsNull(nID_UA, sID_Two, sID_Three)) {
            response.setStatus(HttpStatus.FORBIDDEN.value());
            response.setHeader("Reason", "required at least one of parameters " +
                    "(nID, nID_UA, sID_Two, sID_Three)!");
        }

        try {
            countryDao.removeByKey(nID, nID_UA, sID_Two, sID_Three);
        } catch (RuntimeException e) {
            LOG.warn(e.getMessage(), e);
            response.setStatus(HttpStatus.FORBIDDEN.value());
            response.setHeader("Reason", e.getMessage());
        }
    }
}
