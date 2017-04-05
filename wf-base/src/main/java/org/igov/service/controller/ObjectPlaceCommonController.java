package org.igov.service.controller;

import org.igov.service.business.object.ObjectPlaceCommonService;
import org.igov.service.exception.CommonServiceException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;

@Controller
@Api(tags = { "ObjectPlaceController -- Обьекты мест (населенных пунктов и регионов) и стран" })
@RequestMapping(value = "/object/place/sub")
public class ObjectPlaceCommonController {
	private static final Logger LOG = LoggerFactory.getLogger(ObjectPlaceCommonController.class);

	private static final String JSON_TYPE = "Accept=application/json";
	public static final String APPLICATION_JSON_CHARSET_UTF_8 = "application/json;charset=UTF-8";

	@Autowired
	ObjectPlaceCommonService objectPlaceCommonService;

	@ApiOperation(value = "Получение списка улиц", notes = "Получем список улиц. Пример:\n"
		            + "https://alpha.test.igov.org.ua/wf/service/object/place/sub/PB/getSubPlaces_/?sID_SubPlace_PB=UA40773&sFind=южн\n\n"
		            + "Ответ:\n\n"
		            + "\n```json\n"
		            + "{"
		            + "  \"listAddress\":"
		            + "   [{"
		            + "      \"code\": \"23TFD62IDSDX00\","
		            + "	     \"desc\": \"1-я Южная\","
		            + "	     \"type\": \"улица\""
		            + "    }, {"
		            + "	     \"code\": \"23T149MQULAP00\","
		            + "	     \"desc\": \"Южная Сторона\","
		            + "	     \"type\": \"улица\""
		            + "    }"
		            + "   ]"
		            + "}"	            
		            + "\n```\n")
	@RequestMapping(value = "/PB/getSubPlaces_", method = RequestMethod.GET, headers = {
			JSON_TYPE }, produces = APPLICATION_JSON_CHARSET_UTF_8)
	public @ResponseBody String getSubPlaces_(
			@ApiParam(value = "sID_SubPlace_PB - код узла адреса)", required = true) @RequestParam(value = "sID_SubPlace_PB", required = true) String sID_SubPlace_PB,
			@ApiParam(value = "sFind - строка поиска (ищет по вхождению текста в название улицы)", required = false) @RequestParam(value = "sFind", required = false) String sFind)
			throws CommonServiceException {

		LOG.debug("sID_SubPlace_PB={}, sFind={}", sID_SubPlace_PB, sFind);

		return objectPlaceCommonService.getSubPlaces_(sID_SubPlace_PB, sFind);
	}

}
