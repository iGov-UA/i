package org.igov.service.controller;

import com.google.common.base.Optional;

import io.swagger.annotations.*;

import org.igov.model.action.event.HistoryEvent_Service;
import org.igov.model.action.event.HistoryEvent_ServiceDao;
import org.igov.model.core.BaseEntityDao;
import org.igov.model.object.place.*;
import org.igov.service.business.core.EntityService;
import org.igov.service.exception.CommonServiceException;
import org.igov.service.exception.EntityNotFoundException;
import org.igov.util.JSON.JsonRestUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;

import java.util.Arrays;
import java.util.List;

import static org.apache.commons.lang3.StringUtils.isNotBlank;
import static org.igov.service.business.object.ObjectPlaceService.regionsToJsonResponse;
import static org.igov.service.business.object.ObjectPlaceService.swap;
import org.igov.service.business.object.place.CommonPlaceService;
import static org.igov.util.Tool.bNullArgsAll;

/**
 * @author dgroup
 * @since 20.07.2015
 */
@Api(tags = { "ObjectPlaceController -- Обьекты мест (населенных пунктов и регионов) и стран" })
@Controller
@RequestMapping(value = "/object/place")
public class ObjectPlaceController {

    private static final Logger LOG = LoggerFactory.getLogger(ObjectPlaceController.class);

    private static final String JSON_TYPE = "Accept=application/json";

    @Autowired
    private PlaceDao placeDao;

    @Autowired
    private PlaceTypeDao placeTypeDao;
    
    @Autowired
    private PlaceTreeDao placeTreeDao;

    @Autowired
    private ObjectPlace_UADao objectPlace_UADao;

    @Autowired
    private BaseEntityDao<Long> baseEntityDao;
    @Autowired
    private EntityService entityService;

    @Autowired
    private CountryDao countryDao;
    
    @Autowired
    private CommonPlaceService oCommonPlaceService;
    
    @Autowired
    private HistoryEvent_ServiceDao historyEventServiceDao;

    private static boolean positive(Long value) {
        return value != null && value > 0;
    }

        @ApiOperation(value = "Получить иерархию объектов вниз начиная с указанного узла", notes = "Примеры\n"
            + "Параметыр nID\n\n"
                + "https://test.igov.org.ua/wf/service/object/place/getPlacesTree?nID=459\n"
            + "Ответ\n"
            + "\n```json\n"
            + "{\n"
            + "    \"nLevelArea\": null,\n"
            + "    \"nLevel\": 0,\n"
            + "    \"o\": {\n"
            + "        \"nID\": 459,\n"
            + "        \"sName\": \"Недригайлівський район/смт Недригайлів\",\n"
            + "        \"nID_PlaceType\": 2,\n"
            + "        \"sID_UA\": \"5923500000\",\n"
            + "        \"sNameOriginal\": \"\"\n"
            + "    },\n"
            + "    \"a\": [\n"
            + "        {\n"
            + "            \"nLevelArea\": null,\n"
            + "            \"nLevel\": 1,\n"
            + "            \"o\": {\n"
            + "                \"nID\": 460,\n"
            + "                \"sName\": \"Недригайлів\",\n"
            + "                \"nID_PlaceType\": 4,\n"
            + "                \"sID_UA\": \"5923555100\",\n"
            + "                \"sNameOriginal\": \"\"\n"
            + "            },\n"
            + "            \"a\": []\n"
            + "        }\n"
            + "    ]\n"
            + "}\n"
            + "\n```\n"
                + "Примечание: по умолчанию возвращаются иерархия с ограниченным уровнем вложенности детей (поле nDeep, по умолчанию равно 1)\n"
            + "Получить иерархию объектов вниз начиная с указанного узла (параметр nID) и количества уровней вниз (параметр nDeep).\n"
            + "https://test.igov.org.ua/wf/service/object/place/getPlacesTree?nID=459&nDeep=4\n"
            + "Ответ:\n"
            + "\n```json\n"
            + "{\n"
            + "    \"nLevelArea\": null,\n"
            + "    \"nLevel\": 0,\n"
            + "    \"o\": {\n"
            + "        \"nID\": 459,\n"
            + "        \"sName\": \"Недригайлівський район/смт Недригайлів\",\n"
            + "        \"nID_PlaceType\": 2,\n"
            + "        \"sID_UA\": \"5923500000\",\n"
            + "        \"sNameOriginal\": \"\"\n"
            + "    },\n"
            + "    \"a\": [\n"
            + "        {\n"
            + "            \"nLevelArea\": null,\n"
            + "            \"nLevel\": 1,\n"
            + "            \"o\": {\n"
            + "                \"nID\": 460,\n"
            + "                \"sName\": \"Недригайлів\",\n"
            + "                \"nID_PlaceType\": 4,\n"
            + "                \"sID_UA\": \"5923555100\",\n"
            + "                \"sNameOriginal\": \"\"\n"
            + "            },\n"
            + "            \"a\": [\n"
            + "                {\n"
            + "                    \"nLevelArea\": null,\n"
            + "                    \"nLevel\": 2,\n"
            + "                    \"o\": {\n"
            + "                        \"nID\": 458,\n"
            + "                        \"sName\": \"Вільшана\",\n"
            + "                        \"nID_PlaceType\": 5,\n"
            + "                        \"sID_UA\": \"5923584401\",\n"
            + "                        \"sNameOriginal\": \"\"\n"
            + "                    },\n"
            + "                    \"a\": []\n"
            + "                },\n"
            + "                {\n"
            + "                    \"nLevelArea\": null,\n"
            + "                    \"nLevel\": 2,\n"
            + "                    \"o\": {\n"
            + "                        \"nID\": 461,\n"
            + "                        \"sName\": \"Вакулки\",\n"
            + "                        \"nID_PlaceType\": 5,\n"
            + "                        \"sID_UA\": \"5923555101\",\n"
            + "                        \"sNameOriginal\": \"\"\n"
            + "                    },\n"
            + "                    \"a\": []\n"
            + "                },\n"
            + "                {\n"
            + "                    \"nLevelArea\": null,\n"
            + "                    \"nLevel\": 2,\n"
            + "                    \"o\": {\n"
            + "                        \"nID\": 462,\n"
            + "                        \"sName\": \"Віхове\",\n"
            + "                        \"nID_PlaceType\": 5,\n"
            + "                        \"sID_UA\": \"5923555102\",\n"
            + "                        \"sNameOriginal\": \"\"\n"
            + "                    },\n"
            + "                    \"a\": []\n"
            + "                }\n"
            + "            ]\n"
            + "        }\n"
            + "    ]\n"
            + "}\n"
            + "\n```\n"
            + "Получить иерархию объектов вниз начиная с указанного узла (параметр sUA_ID).\n"
            + "https://test.igov.org.ua/wf/service/object/place/getPlacesTree?sID_UA=5923500000\n"
            + "\n```json\n"
            + "{\n"
            + "    \"nLevelArea\": null,\n"
            + "    \"nLevel\": 0,\n"
            + "    \"o\": {\n"
            + "        \"nID\": 459,\n"
            + "        \"sName\": \"Недригайлівський район/смт Недригайлів\",\n"
            + "        \"nID_PlaceType\": 2,\n"
            + "        \"sID_UA\": \"5923500000\",\n"
            + "        \"sNameOriginal\": \"\"\n"
            + "    },\n"
            + "    \"a\": [\n"
            + "        {\n"
            + "            \"nLevelArea\": null,\n"
            + "            \"nLevel\": 1,\n"
            + "            \"o\": {\n"
            + "                \"nID\": 460,\n"
            + "                \"sName\": \"Недригайлів\",\n"
            + "                \"nID_PlaceType\": 4,\n"
            + "                \"sID_UA\": \"5923555100\",\n"
            + "                \"sNameOriginal\": \"\"\n"
            + "            },\n"
            + "            \"a\": []\n"
            + "        }\n"
            + "    ]\n"
            + "}\n"
            + "\n```\n"
            + "Получить иерархию объектов вниз начиная с указанного узла (параметр sUA_ID) и количества уровней вниз (параметр nDeep).\n"
            + "https://test.igov.org.ua/wf/service/object/place/getPlacesTree?sID_UA=5923500000&nDeep=3\n"
            + "Ответ:\n"
            + "\n```json\n"
            + "{\n"
            + "    \"nLevelArea\": null,\n"
            + "    \"nLevel\": 0,\n"
            + "    \"o\": {\n"
            + "        \"nID\": 459,\n"
            + "        \"sName\": \"Недригайлівський район/смт Недригайлів\",\n"
            + "        \"nID_PlaceType\": 2,\n"
            + "        \"sID_UA\": \"5923500000\",\n"
            + "        \"sNameOriginal\": \"\"\n"
            + "    },\n"
            + "    \"a\": [\n"
            + "        {\n"
            + "            \"nLevelArea\": null,\n"
            + "            \"nLevel\": 1,\n"
            + "            \"o\": {\n"
            + "                \"nID\": 460,\n"
            + "                \"sName\": \"Недригайлів\",\n"
            + "                \"nID_PlaceType\": 4,\n"
            + "                \"sID_UA\": \"5923555100\",\n"
            + "                \"sNameOriginal\": \"\"\n"
            + "            },\n"
            + "            \"a\": [\n"
            + "                {\n"
            + "                    \"nLevelArea\": null,\n"
            + "                    \"nLevel\": 2,\n"
            + "                    \"o\": {\n"
            + "                        \"nID\": 458,\n"
            + "                        \"sName\": \"Вільшана\",\n"
            + "                        \"nID_PlaceType\": 5,\n"
            + "                        \"sID_UA\": \"5923584401\",\n"
            + "                        \"sNameOriginal\": \"\"\n"
            + "                    },\n"
            + "                    \"a\": []\n"
            + "                },\n"
            + "                {\n"
            + "                    \"nLevelArea\": null,\n"
            + "                    \"nLevel\": 2,\n"
            + "                    \"o\": {\n"
            + "                        \"nID\": 461,\n"
            + "                        \"sName\": \"Вакулки\",\n"
            + "                        \"nID_PlaceType\": 5,\n"
            + "                        \"sID_UA\": \"5923555101\",\n"
            + "                        \"sNameOriginal\": \"\"\n"
            + "                    },\n"
            + "                    \"a\": []\n"
            + "                },\n"
            + "                {\n"
            + "                    \"nLevelArea\": null,\n"
            + "                    \"nLevel\": 2,\n"
            + "                    \"o\": {\n"
            + "                        \"nID\": 462,\n"
            + "                        \"sName\": \"Віхове\",\n"
            + "                        \"nID_PlaceType\": 5,\n"
            + "                        \"sID_UA\": \"5923555102\",\n"
            + "                        \"sNameOriginal\": \"\"\n"
            + "                    },\n"
            + "                    \"a\": []\n"
            + "                }\n"
            + "            ]\n"
            + "        }\n"
            + "    ]\n"
            + "}\n"
            + "\n```\n"
            + "Получить иерархию объектов вниз начиная с указанного узла (параметр nID или sUA_ID) c фильтрацией по типу (nID_PlaceType).\n"
            + "https://test.igov.org.ua/wf/service/object/place/getPlacesTree?nID=459&nDeep=3&nID_PlaceType=5\n\n"
            + "Получить иерархию объектов вниз начиная с указанного узла (параметр nID или sUA_ID) c фильтрацией по региону (bArea).\n"
            + "https://test.igov.org.ua/wf/service/object/place/getPlacesTree?nID=459&bArea=false&nDeep=3\n\n"
            + "Получить иерархию объектов вниз начиная с указанного узла (параметр nID или sUA_ID) c фильтрацией по корневому региону (bRoot).\n"
            + "https://test.igov.org.ua/wf/service/object/place/getPlacesTree?nID=459&bRoot=false&nDeep=3")
    @RequestMapping(value = "/getPlacesTree",
            method = RequestMethod.GET, headers = {JSON_TYPE})
    public @ResponseBody
    PlaceHierarchy getPlacesTree(
            @ApiParam(value = "id места", required = false) @RequestParam(value = "nID", required = false) Long placeId,
            @ApiParam(value = "строка-ИД места (в классификаторе Украины)", required = false) @RequestParam(value = "sID_UA", required = false) String uaId,
            @ApiParam(value = "номер-МД типа места (населенного пункта/региона)", required = false) @RequestParam(value = "nID_PlaceType", required = false) Long typeId,
                @ApiParam(value = "булевый флаг. если true -- только территория (район)", required = false) @RequestParam(value = "bArea", required = false) Boolean area,
                @ApiParam(value = "булевый флаг. если true -- только корень (область)", required = false) @RequestParam(value = "bRoot", required = false) Boolean root,
            @ApiParam(value = "число вложенных уровней", required = true) @RequestParam(value = "nDeep", defaultValue = "1") Long deep) {

        return placeDao.getTreeDown(new PlaceHibernateHierarchyRecord(placeId, typeId, uaId, area, root, deep));
    }

        @ApiOperation(value = "Получение иерархии объектов вверх начиная с указанного узла", notes = "Примеры"
            + "Получить иерархию объектов вверх начиная с указанного узла (параметр nID).\n\n"
            + "https://test.igov.org.ua/wf-central/service/object/place/getPlace?nID=462\n"
            + "Ответ\n"
            + "\n```json\n"
            + "{\n"
            + "    \"nLevelArea\": null,\n"
            + "    \"nLevel\": 0,\n"
            + "    \"o\": {\n"
            + "        \"nID\": 462,\n"
            + "        \"sName\": \"Віхове\",\n"
            + "        \"nID_PlaceType\": 5,\n"
            + "        \"sID_UA\": \"5923555102\",\n"
            + "        \"sNameOriginal\": \"\"\n"
            + "    },\n"
            + "    \"a\": []\n"
            + "}\n"
            + "\n```\n"
                + "Примечание: по умолчанию возвращаются иерархия с ограниченным уровнем (только 1 уровень)\n"
                + "Получить иерархию объектов вверх начиная с указанного узла (параметр nID). Для активации выборки по полной иерархии необходимо указать параметр bTree.\n"
            + "https://test.igov.org.ua/wf-central/service/object/place/getPlace?nID=462&bTree=true\n"
            + "Ответ (возвращает иерархию объектов снизу вверх, напр. от деревни к району/области)\n"
            + "\n```json\n"
            + "{\n"
            + "    \"nLevelArea\": null,\n"
            + "    \"nLevel\": 2,\n"
            + "    \"o\": {\n"
            + "        \"nID\": 459,\n"
            + "        \"sName\": \"Недригайлівський район/смт Недригайлів\",\n"
            + "        \"nID_PlaceType\": 2,\n"
            + "        \"sID_UA\": \"5923500000\",\n"
            + "        \"sNameOriginal\": \"\"\n"
            + "    },\n"
            + "    \"a\": [\n"
            + "        {\n"
            + "            \"nLevelArea\": null,\n"
            + "            \"nLevel\": 1,\n"
            + "            \"o\": {\n"
            + "                \"nID\": 460,\n"
            + "                \"sName\": \"Недригайлів\",\n"
            + "                \"nID_PlaceType\": 4,\n"
            + "                \"sID_UA\": \"5923555100\",\n"
            + "                \"sNameOriginal\": \"\"\n"
            + "            },\n"
            + "            \"a\": [\n"
            + "                {\n"
            + "                    \"nLevelArea\": null,\n"
            + "                    \"nLevel\": 0,\n"
            + "                    \"o\": {\n"
            + "                        \"nID\": 462,\n"
            + "                        \"sName\": \"Віхове\",\n"
            + "                        \"nID_PlaceType\": 5,\n"
            + "                        \"sID_UA\": \"5923555102\",\n"
            + "                        \"sNameOriginal\": \"\"\n"
            + "                    },\n"
            + "                    \"a\": []\n"
            + "                }\n"
            + "            ]\n"
            + "        }\n"
            + "    ]\n"
            + "}\n"
            + "\n```\n"
            + "Получить иерархию объектов вверх начиная с указанного узла (параметр sUA_ID).\n"
                + "https://test.igov.org.ua/wf-central/service/object/place/getPlace?sID_UA=5923555102\n"
            + "Получить иерархию объектов вверх начиная с указанного узла (параметр sUA_ID). Для активации выборки по полной иерархии необходимо указать параметр bTree.\n"
            + "https://test.igov.org.ua/wf-central/service/object/place/getPlace?sID_UA=5923555102&bTree=true")
    @RequestMapping(value = "/getPlace",
            method = RequestMethod.GET, headers = {JSON_TYPE})
    public @ResponseBody
    PlaceHierarchy getPlace(
            @ApiParam(value = "id места", required = false) @RequestParam(value = "nID", required = false) Long placeId,
            @ApiParam(value = "строка-ИД места (в классификаторе Украины)", required = false) @RequestParam(value = "sID_UA", required = false) String uaId,
                @ApiParam(value = "булевый флаг. если true -- в виде дерева", required = true) @RequestParam(value = "bTree", defaultValue = "false") Boolean tree) {

        return placeDao.getTreeUp(placeId, uaId, tree);
    }

        @ApiOperation(value = "Вставить новый объект Place", notes = "Примеры\n"
            + "https://test.igov.org.ua/wf/service/object/place/setPlace?sName=child_of_462&nID_PlaceType=5&sID_UA=90005000462&sNameOriginal=5000_462_child\n\n"
            + "Результат\n"
            + "HTTP code = 200 OK\n\n\n"
            + "GET запрос по адресу https://test.igov.org.ua/wf-central/service/object/place/getPlaceEntity?sID_UA=90005000462 должен вернуть вашу сущность.\n"
            + "\n```json\n"
            + "{\n"
            + "    \"sID_UA\": \"90005000462\",\n"
            + "    \"nID\": 22830,\n"
            + "    \"sName\": \"child_of_462\",\n"
            + "    \"nID_PlaceType\": 5,\n"
            + "    \"sNameOriginal\": \"5000_462_child\"\n"
            + "}\n"
            + "\n```\n"
            + "Примечание: Первичный ключ (параметр nID) мы не указываем, т.к. хибернейт обязан сам генерировать PK\n\n\n\n"
            + "Обновить объект Place (параметр для поиска sID_UA).\n"
            + "https://test.igov.org.ua/wf/service/object/place/setPlace?sName=child_of_462&nID_PlaceType=5&sID_UA=90005000462&sNameOriginal=5000_462_child\n"
            + "Результат\n"
            + "HTTP code = 200 OK\n\n"
            + "GET запрос по адресу https://test.igov.org.ua/wf-central/service/object/place/getPlaceEntity?sID_UA=90005000462 должен вернуть вашу сущность с обновленными параметрами:\n"
            + "\n```json\n"
            + "{\n"
            + "    \"sID_UA\": \"90005000462\",\n"
            + "    \"nID\": 22830,\n"
            + "    \"sName\": \"child_of_462\",\n"
            + "    \"nID_PlaceType\": 5,\n"
            + "    \"sNameOriginal\": \"5000_462_child\"\n"
            + "}\n"
            + "\n```\n"
            + "\n\n\n"
            + "Обновить объект Place (параметр для поиска nID, PK).\n"
            + "https://test.igov.org.ua/wf/service/object/place/setPlace?sName=The_child_of_462&nID_PlaceType=5&sNameOriginal=50000_462_child&nID=22830&sID_UA=90005000462\n"
            + "Результат\n"
            + "HTTP code = 200 OK\n\n"
            + "GET запрос по адресу https://test.igov.org.ua/wf-central/service/object/place/getPlaceEntity?nID=22830 должен вернуть вашу сущность с обновленными параметрами:\n"
            + "\n```json\n"
            + "{\n"
            + "    \"sID_UA\": \"90005000462\",\n"
            + "    \"nID\": 22830,\n"
            + "    \"sName\": \"child_of_462\",\n"
            + "    \"nID_PlaceType\": 5,\n"
            + "    \"sNameOriginal\": \"5000_462_child\"\n"
            + "}\n"
            + "\n```\n")
    @RequestMapping(value = "/setPlace",
            method = RequestMethod.POST, headers = {JSON_TYPE})
    public @ResponseBody
    void setPlace(
            @ApiParam(value = "id места", required = false) @RequestParam(value = "nID", required = false) Long placeId,
            @ApiParam(value = "строка имени", required = false) @RequestParam(value = "sName", required = false) String name,
            @ApiParam(value = "номер-МД типа места (населенного пункта/региона)", required = false) @RequestParam(value = "nID_PlaceType", required = false) Long typeId,
            @ApiParam(value = "строка-ИД места (в классификаторе Украины)", required = false) @RequestParam(value = "sID_UA", required = false) String uaId,
            @ApiParam(value = "строка имени оригинального", required = false) @RequestParam(value = "sNameOriginal", required = false) String originalName) {

        Place place = new Place(placeId, name, typeId, uaId, originalName);

        if (positive(placeId) && !swap(place, placeDao.findById(placeId), placeDao)) {
            throw new EntityNotFoundException(placeId);

        } else if (!swap(place, placeDao.findBy("sID_UA", uaId), placeDao)) {
            placeDao.saveOrUpdate(place);
        }
    }

        @ApiOperation(value = "Получение сущности Place", notes =
                "Пример: https://test.igov.org.ua/wf-central/service/object/place/getPlaceEntity?nID=22830\n"
            + "Ответ:"
            + "\n```json\n"
            + "{\n"
            + "    \"sID_UA\": \"90005000462\",\n"
            + "    \"nID\": 22830,\n"
            + "    \"sName\": \"child_of_462\",\n"
            + "    \"nID_PlaceType\": 5,\n"
            + "    \"sNameOriginal\": \"5000_462_child\"\n"
            + "}\n"
            + "\n```\n")
    @RequestMapping(value = "/getPlaceEntity",
            method = RequestMethod.GET, headers = {JSON_TYPE})
    public @ResponseBody
    Place getPlace(
            @ApiParam(value = "номер-ИД места", required = false) @RequestParam(value = "nID", required = false) Long placeId,
            @ApiParam(value = "строка-ИД места (в классификаторе Украины)", required = false) @RequestParam(value = "sID_UA", required = false) String uaId) {

        return positive(placeId)
                ? placeDao.findByIdExpected(placeId)
                : placeDao.findByExpected("sID_UA", uaId);
    }

        @ApiOperation(value = "Удаление объекта Place", notes = "Примеры\n"
            + "Удалить объект Place по первичному ключу (параметр nID).\n\n"
            + "https://test.igov.org.ua/wf-central/service/object/place/removePlace?nID=22830\n"
            + "Результат\n"
            + "HTTP code = 200 OK\n\n"
                + "GET запрос по адресу https://test.igov.org.ua/wf-central/service/object/place/getPlaceEntity?nID=22830 должен вернуть cообщение об ошибке:\n"
            + "\n```json\n"
            + "{\n"
            + "    \"code\": \"SYSTEM_ERR\",\n"
            + "    \"message\": \"Entity with id=22830 does not exist\"\n"
            + "}\n"
            + "\n\n\n"
            + "\n```\n"
            + "Удалить объект Place по уникальному UA id (параметр sID_UA).\n"
            + "https://test.igov.org.ua/wf-central/service/object/place/removePlace?sID_UA=90005000462\n"
            + "Результат\n"
            + "HTTP code = 200 OK\n\n"
            + "GET запрос по адресу https://test.igov.org.ua/wf-central/service/object/place/getPlaceEntity?nID=22830 должен вернуть cообщение об ошибке:\n"
            + "\n```json\n"
            + "{\n"
            + "    \"code\": \"SYSTEM_ERR\",\n"
            + "    \"message\": \"Entity with sID_UA='90005000462' not found\"\n"
            + "}\n"
            + "\n```\n")
    @RequestMapping(value = "/removePlace",
            method = RequestMethod.POST, headers = {JSON_TYPE})
    public @ResponseBody
    void removePlace(
            @ApiParam(value = "номер-ИД места", required = false) @RequestParam(value = "nID", required = false) Long placeId,
            @ApiParam(value = "строка-ИД места (в классификаторе Украины)", required = false) @RequestParam(value = "sID_UA", required = false) String uaId) {

        if (positive(placeId)) {
            placeDao.delete(placeId);

        } else if (isNotBlank(uaId)) {
            Optional<Place> place = placeDao.findBy("sID_UA", uaId);
            if (place.isPresent()) {
                placeDao.delete(place.get());
            }
        }
    }

        @ApiOperation(value = "Получение типа места", notes = "Примеры\n"
            + "Получить тип места (область)\n"
            + "https://test.igov.org.ua/wf-central/service/object/place/getPlaceTypes?bArea=true&bRoot=true\n"
            + "Ответ\n"
            + "\n```json\n"
            + "[\n"
            + "    {\n"
            + "        \"bArea\": true,\n"
            + "        \"bRoot\": true,\n"
            + "        \"nID\": 1,\n"
            + "        \"sName\": \"Область\",\n"
            + "        \"nOrder\": null\n"
            + "    }\n"
            + "]\n"
            + "\n\n\n"
            + "\n```\n"
            + "Получить тип места (район).\n"
            + "https://test.igov.org.ua/wf-central/service/object/place/getPlaceTypes?bArea=true&bRoot=false\n"
            + "Ответ\n"
            + "\n```json\n"
            + "[\n"
            + "    {\n"
            + "        \"bArea\": true,\n"
            + "        \"bRoot\": false,\n"
            + "        \"nID\": 2,\n"
            + "        \"sName\": \"Район\",\n"
            + "        \"nOrder\": null\n"
            + "    }\n"
            + "]\n"
            + "\n\n\n"
            + "\n```\n"
            + "Получить тип места (ПГТ, город, село).\n"
            + "https://test.igov.org.ua/wf-central/service/object/place/getPlaceTypes?bArea=false&bRoot=false\n"
            + "Ответ\n"
            + "\n```json\n"
            + "[\n"
            + "    {\n"
            + "        \"bArea\": false,\n"
            + "        \"bRoot\": false,\n"
            + "        \"nID\": 3,\n"
            + "        \"sName\": \"Город\",\n"
            + "        \"nOrder\": null\n"
            + "    },\n"
            + "    {\n"
            + "        \"bArea\": false,\n"
            + "        \"bRoot\": false,\n"
            + "        \"nID\": 4,\n"
            + "        \"sName\": \"ПГТ\",\n"
            + "        \"nOrder\": null\n"
            + "    },\n"
            + "    {\n"
            + "        \"bArea\": false,\n"
            + "        \"bRoot\": false,\n"
            + "        \"nID\": 5,\n"
            + "        \"sName\": \"Село\",\n"
            + "        \"nOrder\": null\n"
            + "    }\n"
            + "]\n"
            + "\n```\n")
    @RequestMapping(value = "/getPlaceTypes",
            method = RequestMethod.GET, headers = {JSON_TYPE})
    public @ResponseBody
    List<PlaceType> getPlaceTypes(
                @ApiParam(value = "булевый флаг. если true -- только территория (район)", required = true) @RequestParam(value = "bArea") Boolean area,
                @ApiParam(value = "булевый флаг. если true -- только корень (область)", required = true) @RequestParam(value = "bRoot") Boolean root) {

        return placeTypeDao.getPlaceTypes(area, root);
    }

        @ApiOperation(value = "Получение субъекта", notes = "Пример:\n"
            + "https://test.igov.org.ua/wf-central/service/object/place/getPlaceType?nID=1\n"
            + "Ответ\n"
            + "\n```json\n"
            + "[\n"
            + "    {\n"
            + "        \"bArea\": true,\n"
            + "        \"bRoot\": true,\n"
            + "        \"nID\": 1,\n"
            + "        \"sName\": \"Область\",\n"
            + "        \"nOrder\": null\n"
            + "    }\n"
            + "]\n"
            + "\n```\n")
    @RequestMapping(value = "/getPlaceType",
            method = RequestMethod.GET, headers = {JSON_TYPE})
    public @ResponseBody
    PlaceType getPlaceType(@ApiParam(value = "нет описания", required = true) @RequestParam(value = "nID") Long placeTypeId) {

        return placeTypeDao.findByIdExpected(placeTypeId);
    }

        @ApiOperation(value = "Cоздание нового тип места", notes = "Пример:\n"
            + "https://test.igov.org.ua/wf-central/service/object/place/setPlaceType?sName=Type_1&nOrder=2&bArea=false&bRoot=false\n"
            + "Результат\n"
            + "HTTP code = 200 OK\n\n"
            + "GET запрос по адресу https://test.igov.org.ua/wf-central/service/object/place/getPlaceTypes?bArea=false&bRoot=false\n\n\n"
            + "должен вернуть масив с вашей сущностью:\n"
            + "\n```json\n"
            + "[\n"
            + "    {\n"
            + "        \"bArea\": false,\n"
            + "        \"bRoot\": false,\n"
            + "        \"nID\": 3,\n"
            + "        \"sName\": \"Город\",\n"
            + "        \"nOrder\": null\n"
            + "    },\n"
            + "    {\n"
            + "        \"bArea\": false,\n"
            + "        \"bRoot\": false,\n"
            + "        \"nID\": 4,\n"
            + "        \"sName\": \"ПГТ\",\n"
            + "        \"nOrder\": null\n"
            + "    },\n"
            + "    {\n"
            + "        \"bArea\": false,\n"
            + "        \"bRoot\": false,\n"
            + "        \"nID\": 5,\n"
            + "        \"sName\": \"Село\",\n"
            + "        \"nOrder\": null\n"
            + "    },\n"
            + "    {\n"
            + "        \"bArea\": false,\n"
            + "        \"bRoot\": false,\n"
            + "        \"nID\": 23418,\n"
            + "        \"sName\": \"Type_1\",\n"
            + "        \"nOrder\": 2\n"
            + "    }\n"
            + "]\n"
            + "\n```\n")
    @RequestMapping(value = "/setPlaceType",
            method = RequestMethod.POST, headers = {JSON_TYPE})
    public @ResponseBody
    void setPlaceType(
            @ApiParam(value = "номер-ИД записи", required = false) @RequestParam(value = "nID", required = false) Long placeTypeId,
            @ApiParam(value = "строка имени", required = false) @RequestParam(value = "sName", required = false) String name,
            @ApiParam(value = "номер порядковый", required = false) @RequestParam(value = "nOrder", required = false) Long order,
                @ApiParam(value = "булевый флаг. если true -- только территория (район)", required = true) @RequestParam(value = "bArea", defaultValue = "false") Boolean area,
                @ApiParam(value = "булевый флаг. если true -- только корень (область)", required = true) @RequestParam(value = "bRoot", defaultValue = "false") Boolean root) {

        PlaceType placeType = new PlaceType(placeTypeId, name, order, area, root);

        if (positive(placeTypeId)) {
            swap(placeType, placeTypeDao.findById(placeTypeId), placeTypeDao);

        } else {
            placeTypeDao.saveOrUpdate(placeType);
        }
    }

        @ApiOperation(value = "Удаление типа места", notes = "Пример:\n"
            + "https://test.igov.org.ua/wf-central/service/object/place/removePlaceType?nID=23417\n"
            + "Результат\n"
            + "HTTP code = 200 OK\n\n"
            + "GET запрос по адресу https://test.igov.org.ua/wf-central/service/object/place/getPlaceTypes?bArea=false&bRoot=false\n\n\n"
            + "должен вернуть масив без вашей сущности:\n"
            + "\n```json\n"
            + "[\n"
            + "    {\n"
            + "        \"bArea\": false,\n"
            + "        \"bRoot\": false,\n"
            + "        \"nID\": 3,\n"
            + "        \"sName\": \"Город\",\n"
            + "        \"nOrder\": null\n"
            + "    },\n"
            + "    {\n"
            + "        \"bArea\": false,\n"
            + "        \"bRoot\": false,\n"
            + "        \"nID\": 4,\n"
            + "        \"sName\": \"ПГТ\",\n"
            + "        \"nOrder\": null\n"
            + "    },\n"
            + "    {\n"
            + "        \"bArea\": false,\n"
            + "        \"bRoot\": false,\n"
            + "        \"nID\": 5,\n"
            + "        \"sName\": \"Село\",\n"
            + "        \"nOrder\": null\n"
            + "    }\n"
            + "]\n"
            + "\n```\n")
    @RequestMapping(value = "/removePlaceType",
            method = RequestMethod.POST, headers = {JSON_TYPE})
    public @ResponseBody
    void removePlaceType(@ApiParam(value = "номер-ИД записи", required = true) @RequestParam(value = "nID") Long placeTypeId) {

        placeTypeDao.delete(placeTypeId);
    }

        @ApiOperation(value = "Получения дерева мест (регионов и городов)", notes = "Пример: \n"
            + "https://test.igov.org.ua/wf/service/object/place/getPlaces\n"
            + "Ответ:\n"
            + "\n```json\n"
            + "[\n"
            + "    {\n"
            + "        \"nID\": 1,\n"
            + "        \"sName\": \"Дніпропетровська\",\n"
            + "        \"aCity\": [\n"
            + "            {\n"
            + "                \"nID\": 1,\n"
            + "                \"sName\": \"Дніпропетровськ\"\n"
            + "            },\n"
            + "            {\n"
            + "                \"nID\": 2,\n"
            + "                \"sName\": \"Кривий Ріг\"\n"
            + "            }\n"
            + "        ]\n"
            + "    },\n"
            + "    {\n"
            + "        \"nID\": 2,\n"
            + "        \"sName\": \"Львівська\",\n"
            + "        \"aCity\": [\n"
            + "            {\n"
            + "                \"nID\": 3,\n"
            + "                \"sName\": \"Львів\"\n"
            + "            }\n"
            + "        ]\n"
            + "    },\n"
            + "    {\n"
            + "        \"nID\": 3,\n"
            + "        \"sName\": \"Івано-Франківська\",\n"
            + "        \"aCity\": [\n"
            + "            {\n"
            + "                \"nID\": 4,\n"
            + "                \"sName\": \"Івано-Франківськ\"\n"
            + "            },\n"
            + "            {\n"
            + "                \"nID\": 5,\n"
            + "                \"sName\": \"Калуш\"\n"
            + "            }\n"
            + "        ]\n"
            + "    },\n"
            + "    {\n"
            + "        \"nID\": 4,\n"
            + "        \"sName\": \"Миколаївська\",\n"
            + "        \"aCity\": []\n"
            + "    },\n"
            + "    {\n"
            + "        \"nID\": 5,\n"
            + "        \"sName\": \"Київська\",\n"
            + "        \"aCity\": [\n"
            + "            {\n"
            + "                \"nID\": 6,\n"
            + "                \"sName\": \"Київ\"\n"
            + "            }\n"
            + "        ]\n"
            + "    },\n"
            + "    {\n"
            + "        \"nID\": 6,\n"
            + "        \"sName\": \"Херсонська\",\n"
            + "        \"aCity\": [\n"
            + "            {\n"
            + "                \"nID\": 7,\n"
            + "                \"sName\": \"Херсон\"\n"
            + "            }\n"
            + "        ]\n"
            + "    },\n"
            + "    {\n"
            + "        \"nID\": 7,\n"
            + "        \"sName\": \"Рівненська\",\n"
            + "        \"aCity\": [\n"
            + "            {\n"
            + "                \"nID\": 8,\n"
            + "                \"sName\": \"Кузнецовськ\"\n"
            + "            }\n"
            + "        ]\n"
            + "    },\n"
            + "    {\n"
            + "        \"nID\": 8,\n"
            + "        \"sName\": \"Волинська\",\n"
            + "        \"aCity\": [\n"
            + "            {\n"
            + "                \"nID\": 9,\n"
            + "                \"sName\": \"Луцьк\"\n"
            + "            }\n"
            + "        ]\n"
            + "    }\n"
            + "]\n"
            + "\n```\n")
    @RequestMapping(value = "/getPlaces", method = RequestMethod.GET)
    public @ResponseBody
    ResponseEntity getPlaces() {
        List<Region> regions = baseEntityDao.findAll(Region.class);
        return regionsToJsonResponse(regions);
    }

        @ApiOperation(value = "Изменение дерева мест (регионов и городов)", notes = ""
            + "Можно менять регионы (не добавлять и не удалять) + менять/добавлять города (но не удалять), Передается json в теле POST запроса в том же формате, в котором он был в getPlaces.\n\n"
                + "Возвращает: HTTP STATUS 200 + json представление сервиса после изменения. Чаще всего то же, что было передано в теле POST запроса + "
                + "сгенерированные id-шники вложенных сущностей, если такие были.\n\n"
            + "Пример: \n"
            + "https://test.igov.org.ua/wf/service/object/place/setPlaces\n\n"
            + "\n```json\n"
            + "[\n"
            + "  {\n"
            + "    \"nID\": 1,\n"
            + "    \"sName\": \"Дніпропетровська\",\n"
            + "    \"aCity\":\n"
            + "    [\n"
            + "      {\n"
            + "        \"nID\": 1,\n"
            + "        \"sName\": \"Cічеслав\"\n"
            + "      },\n"
            + "      {\n"
            + "        \"nID\": 2,\n"
            + "        \"sName\": \"Кривий Ріг\"\n"
            + "      }\n"
            + "    ]\n"
            + "  }\n"
            + "]\n"
            + "\n```\n"
            + "Ответ: HTTP STATUS 200\n"
            + "\n```json\n"
            + "[\n"
            + "    {\n"
            + "        \"nID\": 1,\n"
            + "        \"sName\": \"Дніпропетровська\",\n"
            + "        \"aCity\": [\n"
            + "            {\n"
            + "                \"nID\": 1,\n"
            + "                \"sName\": \"Cічеслав\"\n"
            + "            },\n"
            + "            {\n"
            + "                \"nID\": 2,\n"
            + "                \"sName\": \"Кривий Ріг\"\n"
            + "            }\n"
            + "        ]\n"
            + "    }\n"
            + "]\n"
            + "\n```\n")
    @RequestMapping(value = "/setPlaces", method = RequestMethod.POST)
    public @ResponseBody
        ResponseEntity setPlaces(
                @ApiParam(value = "строка-json в том же формате, в котором он был в getPlaces") @RequestBody String jsonData) {

        List<Region> aRegion = Arrays.asList(JsonRestUtils.readObject(jsonData, Region[].class));
        List<Region> aRegionUpdated = entityService.update(aRegion);
        return regionsToJsonResponse(aRegionUpdated);
    }

        @ApiOperation(value = "Возвращает весь список стран", notes = ""
                + "(залит справочник согласно Википедии и Класифікації країн світу)\n\n"
            + "пример:\n"
            + "https://test.igov.org.ua/wf/service/object/place/getCountries")
    @RequestMapping(value = "/getCountries", method = RequestMethod.GET)
    public @ResponseBody
    List<Country> getCountries() {

        return countryDao.findAll();
    }

        @ApiOperation(value = "Возвращает объект Страны по первому из 4х ключей (nID, nID_UA, sID_Two, sID_Three)", notes =
                ""
                        + "Если нет ни одного параметра возвращает ошибку 403. required at least one of parameters (nID, nID_UA, sID_Two, sID_Three)!\n"
                        + "Eсли задано два ключа от разных записей -- вернется та, ключ который \"первее\" в таком порядке: nID, nID_UA, sID_Two, sID_Three.\n"
                        + "пример: https://test.igov.org.ua/wf/service/object/place/getCountry?nID_UA=123\n"
            + "Ответ:\n"
            + "\n```json\n"
            + "{\n"
            + "  \"nID_UA\":123,\n"
            + "  \"sID_Two\":\"AU\",\n"
            + "  \"sID_Three\":\"AUS\",\n"
            + "  \"sNameShort_UA\":\"Австралія\",\n"
            + "  \"sNameShort_EN\":\"Australy\",\n"
            + "  \"sReference_LocalISO\":\"ISO_3166-2:AU\",\n"
            + "  \"nID\":20340\n"
            + "}\n"
            + "\n```\n")
    @ApiResponses(value = {
        @ApiResponse(code = 403, message = "Не задано хотя бы одного ключа из: nID, nID_UA, sID_Two, sID_Three")})
    @RequestMapping(value = "/getCountry", method = RequestMethod.GET)
    public @ResponseBody
    ResponseEntity<String> getCountry(
            @ApiParam(value = "ИД-номер, идентификатор записи", required = false) @RequestParam(value = "nID", required = false) Long nID,
            @ApiParam(value = "ИД-номер Код, в Украинском классификаторе (уникальное)", required = false) @RequestParam(value = "nID_UA", required = false) Long nID_UA,
            @ApiParam(value = "ИД-строка Код-двухсимвольный, международный (уникальное, строка 2 символа)", required = false) @RequestParam(value = "sID_Two", required = false) String sID_Two,
            @ApiParam(value = "ИД-строка Код-трехсимвольный, международный (уникальное, строка 3 символа)", required = false) @RequestParam(value = "sID_Three", required = false) String sID_Three,
            HttpServletResponse response) {

        if (bNullArgsAll(nID, nID_UA, sID_Two, sID_Three)) {
            response.setStatus(HttpStatus.FORBIDDEN.value());
            response.setHeader("Reason", "required at least one of parameters "
                    + "(nID, nID_UA, sID_Two, sID_Three)!");
            return null;
        }
        ResponseEntity<String> result = null;
        try {
            Country country = countryDao.getByKey(nID, nID_UA, sID_Two, sID_Three);
            result = JsonRestUtils.toJsonResponse(country);
        } catch (RuntimeException e) {
            LOG.warn("Error: {}", e.getMessage());
            LOG.trace("FAIL:",  e);
            response.setStatus(HttpStatus.FORBIDDEN.value());
            response.setHeader("Reason", e.getMessage());
        }
        return result;
    }

        @ApiOperation(value = "Изменить объект \"Cтрана\"", notes = ""
                + "апдейтит элемент (если задан один из уникальных ключей) или вставляет (если не задан nID), и отдает экземпляр нового объекта.\n"
            + "Если нет ни одного параметра возвращает ошибку 403. All args are null! Если есть один из уникальных ключей, но запись"
            + " не найдена -- ошибка 403. Record not found! Если кидать \"новую\" запись с одним из уже существующих ключей nID_UA -- то обновится существующая запись по ключу nID_UA, если будет дублироваться другой ключ -- ошибка 403. Could not execute statement (из-за уникальных констрейнтов)")
    @ApiResponses(value = {
        @ApiResponse(code = 403, message = "Нет ни одного параметра / запись не найдена / дублируется один из ключей: nID, sID_Two, sID_Three")})
    @RequestMapping(value = "/setCountry", method = RequestMethod.GET)
    public @ResponseBody
    ResponseEntity<String> setCountry(
            @ApiParam(value = "ИД-номер, идентификатор записи", required = false) @RequestParam(value = "nID", required = false) Long nID,
            @ApiParam(value = "ИД-номер Код, в Украинском классификаторе (уникальное)", required = false) @RequestParam(value = "nID_UA", required = false) Long nID_UA,
            @ApiParam(value = "ИД-строка Код-двухсимвольный, международный (уникальное, строка 2 символа)", required = false) @RequestParam(value = "sID_Two", required = false) String sID_Two,
            @ApiParam(value = "ИД-строка Код-трехсимвольный, международный (уникальное, строка 3 символа)", required = false) @RequestParam(value = "sID_Three", required = false) String sID_Three,
                @ApiParam(value = "строка-Назва країни, коротка, Украинская (уникальное, строка до 100 символов)", required = false) @RequestParam(value = "sNameShort_UA", required = false) String sNameShort_UA,
                @ApiParam(value = "строка-Назва країни, коротка, англійською мовою (уникальное, строка до 100 символов)", required = false) @RequestParam(value = "sNameShort_EN", required = false) String sNameShort_EN,
            @ApiParam(value = "Ссылка на локальный ISO-стандарт, с названием (a-teg с href) (строка до 100 символов)", required = false) @RequestParam(value = "sReference_LocalISO", required = false) String sReference_LocalISO,
            HttpServletResponse response) {

        if (bNullArgsAll(nID, nID_UA, sID_Two, sID_Three,
                sNameShort_UA, sNameShort_EN)) {
            response.setStatus(HttpStatus.FORBIDDEN.value());
            response.setHeader("Reason", "required at least one of parameters "
                    + "(nID, nID_UA, sID_Two, sID_Three, sNameShort_UA, sNameShort_EN)!");
        }

        ResponseEntity<String> result = null;

        try {
            Country country = countryDao.setCountry(nID, nID_UA, sID_Two, sID_Three,
                    sNameShort_UA, sNameShort_EN, sReference_LocalISO);
            result = JsonRestUtils.toJsonResponse(country);
        } catch (RuntimeException e) {
            LOG.warn("Error: {}", e.getMessage());
            LOG.trace("FAIL:",  e);
            response.setStatus(HttpStatus.FORBIDDEN.value());
            response.setHeader("Reason", e.getMessage());
        }
        return result;
    }

        @ApiOperation(value = "Удалить обьект по одному из четырех ключей (nID, nID_UA, sID_Two, sID_Three)", notes = ""
            + "удаляет обьект по одному из четырех ключей (nID, nID_UA, sID_Two, sID_Three) или кидает ошибку 403. Record not found!.\n")
    @ApiResponses(value = {
        @ApiResponse(code = 403, message = "Record not found")})
    @RequestMapping(value = "/removeCountry", method = RequestMethod.GET)
    public @ResponseBody
    void removeCountry(
            @ApiParam(value = "ИД-номер, идентификатор записи", required = false) @RequestParam(value = "nID", required = false) Long nID,
            @ApiParam(value = "ИД-номер Код, в Украинском классификаторе (уникальное)", required = false) @RequestParam(value = "nID_UA", required = false) Long nID_UA,
            @ApiParam(value = "ИД-строка Код-двухсимвольный, международный (уникальное, строка 2 символа)", required = false) @RequestParam(value = "sID_Two", required = false) String sID_Two,
            @ApiParam(value = "ИД-строка Код-трехсимвольный, международный (уникальное, строка 3 символа)", required = false) @RequestParam(value = "sID_Three", required = false) String sID_Three,
            HttpServletResponse response) {

        if (bNullArgsAll(nID_UA, sID_Two, sID_Three)) {
            response.setStatus(HttpStatus.FORBIDDEN.value());
            response.setHeader("Reason", "required at least one of parameters "
                    + "(nID, nID_UA, sID_Two, sID_Three)!");
        }

        try {
            countryDao.removeByKey(nID, nID_UA, sID_Two, sID_Three);
        } catch (RuntimeException e) {
            LOG.warn("Error: {}", e.getMessage());
            LOG.trace("FAIL:",  e);
            response.setStatus(HttpStatus.FORBIDDEN.value());
            response.setHeader("Reason", e.getMessage());
        }
    }
        
        @ApiOperation(value = "Возвращает название сущности Place исходной заявки", notes = ""
                + "Возвращает название сущности Place исходной заявки или кидает ошибку 403. Record not found!.\n")
        @ApiResponses(value = {
            @ApiResponse(code = 403, message = "Record not found")})
        @RequestMapping(value = "/getPlaceByProcess", method = RequestMethod.GET)
        public @ResponseBody 
        Place getPlaceByProcess(
                @ApiParam(value = "ИД-номер, идентификатор заявки", required = true) @RequestParam(value = "nID_Process", required = true) Long nID_Process,
                @ApiParam(value = "ИД-номер сервера", required = true) @RequestParam(value = "nID_Server", required = false) Integer nID_Server,
                HttpServletResponse response) {
        	
            return oCommonPlaceService.getPlaceByProcess(nID_Process, nID_Server);
        }

        @ApiOperation(value = "Получение данных из справочника КОАТУУ", notes = "Получаем данные из справочника КОАТУУ. "
                + "Пример:\n"
                + "https://alpha.test.igov.org.ua/wf/service/object/place/getObjectPlace_UA?sFind=днепр\n\n"
                + "Ответ:\n"
                + "\n```json\n"
                + "[\n"
                + "  {\n"
                + "    \"sID\": \"00008000\",\n"
                + "    \"sName_UA\": \"Дніпропетровська обл.\",\n"
                + "    \"nID_PlaceType\": 1,\n"
                + "    \"nID\": 1\n"
                + "  }\n"
                + "]\n"
                + "\n```\n")
        @RequestMapping(value = "/getObjectPlace_UA", method = RequestMethod.GET, headers = {JSON_TYPE})
        public @ResponseBody
        List<ObjectPlace_UA> getObjectPlace_UA(
                @ApiParam(value = "sFind - кретерий поиска в sID или sName_UA (без учета регистра, в любой части текста)", required = true) @RequestParam(value = "sFind", required = true) String sFind)
            	    throws CommonServiceException {
    		return objectPlace_UADao.getObjectPlace_UA(sFind); 
        }

        @ApiOperation(value = "Получение данных из справочника КОАТУУ", notes = "Получаем данные из справочника КОАТУУ. "
                + "Пример:\n"
                + "https://alpha.test.igov.org.ua/wf/service/object/place/getObjectPlace_UA2?sID=09&sName_UA=дніпр\n\n"
                + "Ответ:\n"
                + "\n```json\n"
                + "[\n"
                + "  {\n"
                + "    \"sID\": \"00008000\",\n"
                + "    \"sName_UA\": \"Дніпропетровська обл.\",\n"
                + "    \"nID_PlaceType\": 1,\n"
                + "    \"nID\": 1\n"
                + "  }\n"
                + "]\n"
                + "\n```\n")
        @RequestMapping(value = "/getObjectPlace_UA2", method = RequestMethod.GET, headers = {JSON_TYPE})
        public @ResponseBody
        List<ObjectPlace_UA> getObjectPlace_UA2(
                @ApiParam(value = "sID - кретерий поиска в sID (без учета регистра, в любой части текста)", required = true) @RequestParam(value = "sID", required = false) String sID,
                @ApiParam(value = "sName_UA - кретерий поиска в sName_UA (без учета регистра, в любой части текста)", required = true) @RequestParam(value = "sName_UA", required = false) String sName_UA)
            	    throws CommonServiceException {
    		return objectPlace_UADao.getObjectPlace_UA(sID, sName_UA); 
        }

}
