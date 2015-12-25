package org.activiti.rest.controller;

import com.google.common.base.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.wf.dp.dniprorada.base.dao.EntityDao;
import org.wf.dp.dniprorada.base.dao.EntityNotFoundException;
import org.wf.dp.dniprorada.base.model.Entity;
import org.wf.dp.dniprorada.dao.PlaceDao;
import org.wf.dp.dniprorada.dao.PlaceTypeDao;
import org.wf.dp.dniprorada.dao.place.PlaceHibernateHierarchyRecord;
import org.wf.dp.dniprorada.dao.place.PlaceHierarchy;
import org.wf.dp.dniprorada.model.Place;
import org.wf.dp.dniprorada.model.PlaceType;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponses;
import io.swagger.annotations.ApiResponse;

import java.util.List;

import static org.apache.commons.lang3.StringUtils.isNotBlank;

/**
 * @author dgroup
 * @since 20.07.2015
 */
@Api(tags = { "PlaceController" }, description = "Работа с универсальной сущностью Place (области, районы, города, деревни)")
@Controller
public class PlaceController {
    private static final String JSON_TYPE = "Accept=application/json";

    ///////////////////////////////////////////////////////////////////////////////////////////////////////
    // Подробные описания сервисов для документирования в Swagger
    ///////////////////////////////////////////////////////////////////////////////////////////////////////
    private static final String noteCODE= "\n```\n";    
    private static final String noteCODEJSON= "\n```json\n";    
    private static final String noteController = "##### Работа с универсальной сущностью Place (области, районы, города, деревни) ";

    private static final String noteGetPlacesTree = noteController + "Получить иерархию объектов вниз начиная с указанного узла #####\n\n"
 		+ "Параметыр nID\n\n"
		+ "https://test.igov.org.ua/wf/service/getPlacesTree?nID=459\n\n\n"
		+ "Ответ\n"
		+ noteCODEJSON
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
		+ noteCODE
		+ "Примечание: по умолчанию возвращаются иерархия с ограниченным уровнем вложенности детей (поле nDeep, по умолчанию равно 1)\n\n"
		+ "Получить иерархию объектов вниз начиная с указанного узла (параметр nID) и количества уровней вниз (параметр nDeep).\n"
		+ "https://test.igov.org.ua/wf/service/getPlacesTree?nID=459&nDeep=4\n"
		+ "Ответ:\n"
		+ noteCODEJSON
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
		+ noteCODE
		+ "Получить иерархию объектов вниз начиная с указанного узла (параметр sUA_ID).\n"
		+ "https://test.igov.org.ua/wf/service/getPlacesTree?sID_UA=5923500000\n"
		+ noteCODEJSON
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
		+ noteCODE
		+ "Получить иерархию объектов вниз начиная с указанного узла (параметр sUA_ID) и количества уровней вниз (параметр nDeep).\n"
		+ "https://test.igov.org.ua/wf/service/getPlacesTree?sID_UA=5923500000&nDeep=3\n"
		+ "Ответ:\n"
		+ noteCODEJSON
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
		+ noteCODE
		+ "Получить иерархию объектов вниз начиная с указанного узла (параметр nID или sUA_ID) c фильтрацией по типу (nID_PlaceType).\n"
		+ "https://test.igov.org.ua/wf/service/getPlacesTree?nID=459&nDeep=3&nID_PlaceType=5\n"
		+ "Получить иерархию объектов вниз начиная с указанного узла (параметр nID или sUA_ID) c фильтрацией по региону (bArea).\n"
		+ "https://test.igov.org.ua/wf/service/getPlacesTree?nID=459&bArea=false&nDeep=3\n"
		+ "Получить иерархию объектов вниз начиная с указанного узла (параметр nID или sUA_ID) c фильтрацией по корневому региону (bRoot).\n"
		+ "https://test.igov.org.ua/wf/service/getPlacesTree?nID=459&bRoot=false&nDeep=3\n";


    private static final String noteGetPlace = noteController + "Получение иерархии объектов вверх начиная с указанного узла #####\n\n\n\n"
		+ "Получить иерархию объектов вверх начиная с указанного узла (параметр nID).\n\n"
		+ "https://test.igov.org.ua/wf-central/service/getPlace?nID=462\n"
		+ "Ответ\n"
		+ noteCODEJSON
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
		+ noteCODE
		+ "Примечание: по умолчанию возвращаются иерархия с ограниченным уровнем (только 1 уровень)\n\n"
		+ "Получить иерархию объектов вверх начиная с указанного узла (параметр nID). Для активации выборки по полной иерархии необходимо указать параметр bTree.\n\n"
		+ "https://test.igov.org.ua/wf-central/service/getPlace?nID=462&bTree=true\n"
		+ "Ответ (возвращает иерархию объектов снизу вверх, напр. от деревни к району/области)\n"
		+ noteCODEJSON
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
		+ noteCODE
		+ "Получить иерархию объектов вверх начиная с указанного узла (параметр sUA_ID).\n"
		+ "https://test.igov.org.ua/wf-central/service/getPlace?sID_UA=5923555102\n\n"
		+ "Получить иерархию объектов вверх начиная с указанного узла (параметр sUA_ID). Для активации выборки по полной иерархии необходимо указать параметр bTree.\n"
		+ "https://test.igov.org.ua/wf-central/service/getPlace?sID_UA=5923555102&bTree=true";


    private static final String noteSetPlace = noteController + "Вставить новый объект Place #####\n\n"
		+ "https://test.igov.org.ua/wf/service/setPlace?sName=child_of_462&nID_PlaceType=5&sID_UA=90005000462&sNameOriginal=5000_462_child\n\n"
		+ "Результат\n"
		+ "HTTP code = 200 OK\n\n\n"
		+ "GET запрос по адресу https://test.igov.org.ua/wf-central/service/getPlaceEntity?sID_UA=90005000462 должен вернуть вашу сущность.\n"
		+ noteCODEJSON
		+ "{\n"
		+ "    \"sID_UA\": \"90005000462\",\n"
		+ "    \"nID\": 22830,\n"
		+ "    \"sName\": \"child_of_462\",\n"
		+ "    \"nID_PlaceType\": 5,\n"
		+ "    \"sNameOriginal\": \"5000_462_child\"\n"
		+ "}\n"
		+ noteCODE
		+ "Примечание: Первичный ключ (параметр nID) мы не указываем, т.к. хибернейт обязан сам генерировать PK\n\n\n\n"
		+ "Обновить объект Place (параметр для поиска sID_UA).\n"
		+ "https://test.igov.org.ua/wf/service/setPlace?sName=child_of_462&nID_PlaceType=5&sID_UA=90005000462&sNameOriginal=5000_462_child\n"
		+ "Результат\n"
		+ "HTTP code = 200 OK\n\n"
		+ "GET запрос по адресу https://test.igov.org.ua/wf-central/service/getPlaceEntity?sID_UA=90005000462 должен вернуть вашу сущность с обновленными параметрами:\n"
		+ noteCODEJSON
		+ "{\n"
		+ "    \"sID_UA\": \"90005000462\",\n"
		+ "    \"nID\": 22830,\n"
		+ "    \"sName\": \"child_of_462\",\n"
		+ "    \"nID_PlaceType\": 5,\n"
		+ "    \"sNameOriginal\": \"5000_462_child\"\n"
		+ "}\n"
		+ noteCODE
		+ "\n\n\n"
		+ "Обновить объект Place (параметр для поиска nID, PK).\n"
		+ "https://test.igov.org.ua/wf/service/setPlace?sName=The_child_of_462&nID_PlaceType=5&sNameOriginal=50000_462_child&nID=22830&sID_UA=90005000462\n"
		+ "Результат\n"
		+ "HTTP code = 200 OK\n\n"
		+ "GET запрос по адресу https://test.igov.org.ua/wf-central/service/getPlaceEntity?nID=22830 должен вернуть вашу сущность с обновленными параметрами:\n"
		+ noteCODEJSON
		+ "{\n"
		+ "    \"sID_UA\": \"90005000462\",\n"
		+ "    \"nID\": 22830,\n"
		+ "    \"sName\": \"child_of_462\",\n"
		+ "    \"nID_PlaceType\": 5,\n"
		+ "    \"sNameOriginal\": \"5000_462_child\"\n"
		+ "}\n"
		+ noteCODE;
    
    private static final String noteGetPlaceEntity = noteController + "Получение сущности Place #####\n\n"
		+ "Пример: https://test.igov.org.ua/wf-central/service/getPlaceEntity?nID=22830\n\n\n"
		+ "Ответ:"
		+ noteCODEJSON
		+ "{\n"
		+ "    \"sID_UA\": \"90005000462\",\n"
		+ "    \"nID\": 22830,\n"
		+ "    \"sName\": \"child_of_462\",\n"
		+ "    \"nID_PlaceType\": 5,\n"
		+ "    \"sNameOriginal\": \"5000_462_child\"\n"
		+ "}\n"
		+ noteCODE;

    private static final String noteRemovePlace = noteController + "Удаление объекта Place #####\n\n"
		+ "Удалить объект Place по первичному ключу (параметр nID).\n\n"
		+ "https://test.igov.org.ua/wf-central/service/removePlace?nID=22830\n"
		+ "Результат\n"
		+ "HTTP code = 200 OK\n\n"
		+ "GET запрос по адресу https://test.igov.org.ua/wf-central/service/getPlaceEntity?nID=22830 должен вернуть cообщение об ошибке:\n\n"
		+ noteCODEJSON
		+ "{\n"
		+ "    \"code\": \"SYSTEM_ERR\",\n"
		+ "    \"message\": \"Entity with id=22830 does not exist\"\n"
		+ "}\n"
		+ "\n\n\n"
		+ noteCODE
		+ "Удалить объект Place по уникальному UA id (параметр sID_UA).\n"
		+ "https://test.igov.org.ua/wf-central/service/removePlace?sID_UA=90005000462\n"
		+ "Результат\n"
		+ "HTTP code = 200 OK\n\n"
		+ "GET запрос по адресу https://test.igov.org.ua/wf-central/service/getPlaceEntity?nID=22830 должен вернуть cообщение об ошибке:\n"
		+ noteCODEJSON
		+ "{\n"
		+ "    \"code\": \"SYSTEM_ERR\",\n"
		+ "    \"message\": \"Entity with sID_UA='90005000462' not found\"\n"
		+ "}\n"
		+ noteCODE;

    private static final String noteGetPlaceTypes = noteController + "Получение типа места #####\n\n"
		+ "Получить тип места (область)\n"
		+ "https://test.igov.org.ua/wf-central/service/getPlaceTypes?bArea=true&bRoot=true\n"
		+ "Ответ\n"
		+ noteCODEJSON
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
		+ noteCODE
		+ "Получить тип места (район).\n"
		+ "https://test.igov.org.ua/wf-central/service/getPlaceTypes?bArea=true&bRoot=false\n"
		+ "Ответ\n"
		+ noteCODEJSON
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
		+ noteCODE
		+ "Получить тип места (ПГТ, город, село).\n"
		+ "https://test.igov.org.ua/wf-central/service/getPlaceTypes?bArea=false&bRoot=false\n"
		+ "Ответ\n"
		+ noteCODEJSON
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
		+ noteCODE;

    private static final String noteGetPlaceType = noteController + "Получение субъекта #####\n\n"
		+ "Получить тип места (как сущность) по первичному ключу (параметр nID)\n"
		+ "https://test.igov.org.ua/wf-central/service/getPlaceType?nID=1\n"
		+ "Ответ\n"
		+ noteCODEJSON
		+ "[\n"
		+ "    {\n"
		+ "        \"bArea\": true,\n"
		+ "        \"bRoot\": true,\n"
		+ "        \"nID\": 1,\n"
		+ "        \"sName\": \"Область\",\n"
		+ "        \"nOrder\": null\n"
		+ "    }\n"
		+ "]\n"
		+ noteCODE;
    
    private static final String noteSetPlaceType = noteController + "Cоздание нового тип места #####\n\n"
		+ "Cоздать новый тип места.\n"
		+ "https://test.igov.org.ua/wf-central/service/setPlaceType?sName=Type_1&nOrder=2&bArea=false&bRoot=false\n"
		+ "Результат\n"
		+ "HTTP code = 200 OK\n\n"
		+ "GET запрос по адресу https://test.igov.org.ua/wf-central/service/getPlaceTypes?bArea=false&bRoot=false должен вернуть масив с вашей сущностью:\n"
		+ noteCODEJSON
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
		+ noteCODE;

    private static final String noteRemovePlaceType = noteController + "Удаление типа места #####\n\n"
		+ "Удалить тип места по первичному ключу (nID).\n"
		+ "https://test.igov.org.ua/wf-central/service/removePlaceType?nID=23417\n"
		+ "Результат\n"
		+ "HTTP code = 200 OK\n\n"
		+ "GET запрос по адресу https://test.igov.org.ua/wf-central/service/getPlaceTypes?bArea=false&bRoot=false должен вернуть масив без вашей сущности:\n"
		+ noteCODEJSON
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
		+ noteCODE;
    ///////////////////////////////////////////////////////////////////////////////////////////////////////
    
    @Autowired
    private PlaceDao placeDao;

    @Autowired
    private PlaceTypeDao placeTypeDao;

    private static boolean positive(Long value) {
        return value != null && value > 0;
    }

    @ApiOperation(value = "Получить иерархию объектов вниз начиная с указанного узла", notes = noteGetPlacesTree )
    @RequestMapping(value = "/getPlacesTree",
            method = RequestMethod.GET, headers = { JSON_TYPE })
    public
    @ResponseBody
    PlaceHierarchy getPlacesTree(
	    @ApiParam(value = "id места", required = false) @RequestParam(value = "nID", required = false) Long placeId,
	    @ApiParam(value = "нет описания", required = false) @RequestParam(value = "sID_UA", required = false) String uaId,
	    @ApiParam(value = "нет описания", required = false) @RequestParam(value = "nID_PlaceType", required = false) Long typeId,
	    @ApiParam(value = "нет описания", required = false) @RequestParam(value = "bArea", required = false) Boolean area,
	    @ApiParam(value = "нет описания", required = false) @RequestParam(value = "bRoot", required = false) Boolean root,
	    @ApiParam(value = "нет описания", required = true) @RequestParam(value = "nDeep", defaultValue = "1") Long deep) {

        return placeDao.getTreeDown(new PlaceHibernateHierarchyRecord(placeId, typeId, uaId, area, root, deep));
    }

    @ApiOperation(value = "Получение иерархии объектов вверх начиная с указанного узла", notes = noteGetPlace )
    @RequestMapping(value = "/getPlace",
            method = RequestMethod.GET, headers = { JSON_TYPE })
    public
    @ResponseBody
    PlaceHierarchy getPlace(
	    @ApiParam(value = "id места", required = false) @RequestParam(value = "nID", required = false) Long placeId,
	    @ApiParam(value = "нет описания", required = false) @RequestParam(value = "sID_UA", required = false) String uaId,
	    @ApiParam(value = "нет описания", required = true) @RequestParam(value = "bTree", defaultValue = "false") Boolean tree) {

        return placeDao.getTreeUp(placeId, uaId, tree);
    }

    @ApiOperation(value = "Вставить новый объект Place", notes = noteSetPlace )
    @RequestMapping(value = "/setPlace",
            method = RequestMethod.POST, headers = { JSON_TYPE })
    public
    @ResponseBody
    void setPlace(
	    @ApiParam(value = "id места", required = false) @RequestParam(value = "nID", required = false) Long placeId,
	    @ApiParam(value = "нет описания", required = false) @RequestParam(value = "sName", required = false) String name,
	    @ApiParam(value = "нет описания", required = false) @RequestParam(value = "nID_PlaceType", required = false) Long typeId,
	    @ApiParam(value = "нет описания", required = false) @RequestParam(value = "sID_UA", required = false) String uaId,
	    @ApiParam(value = "нет описания", required = false) @RequestParam(value = "sNameOriginal", required = false) String originalName) {

        Place place = new Place(placeId, name, typeId, uaId, originalName);

        if (positive(placeId) && !swap(place, placeDao.findById(placeId), placeDao)) {
            throw new EntityNotFoundException(placeId);

        } else if (!swap(place, placeDao.findBy("sID_UA", uaId), placeDao)) {
            placeDao.saveOrUpdate(place);
        }
    }

    @ApiOperation(value = "Получение сущности Place", notes = noteGetPlaceEntity )
    @RequestMapping(value = "/getPlaceEntity",
            method = RequestMethod.GET, headers = { JSON_TYPE })
    public
    @ResponseBody
    Place getPlace(
	    @ApiParam(value = "id места", required = false) @RequestParam(value = "nID", required = false) Long placeId,
	    @ApiParam(value = "нет описания", required = false) @RequestParam(value = "sID_UA", required = false) String uaId) {

        return positive(placeId)
                ? placeDao.findByIdExpected(placeId)
                : placeDao.findByExpected("sID_UA", uaId);
    }

    @ApiOperation(value = "Удаление объекта Place", notes = noteRemovePlace )
    @RequestMapping(value = "/removePlace",
            method = RequestMethod.POST, headers = { JSON_TYPE })
    public
    @ResponseBody
    void removePlace(
	    @ApiParam(value = "id места", required = false) @RequestParam(value = "nID", required = false) Long placeId,
	    @ApiParam(value = "нет описания", required = false) @RequestParam(value = "sID_UA", required = false) String uaId) {

        if (positive(placeId)) {
            placeDao.delete(placeId);

        } else if (isNotBlank(uaId)) {
            Optional<Place> place = placeDao.findBy("sID_UA", uaId);
            if (place.isPresent()) {
                placeDao.delete(place.get());
            }
        }
    }

    @ApiOperation(value = "Получение типа места", notes = noteGetPlaceTypes )
    @RequestMapping(value = "/getPlaceTypes",
            method = RequestMethod.GET, headers = { JSON_TYPE })
    public
    @ResponseBody
    List<PlaceType> getPlaceTypes(
	    @ApiParam(value = "нет описания", required = true) @RequestParam(value = "bArea") Boolean area,
	    @ApiParam(value = "нет описания", required = true) @RequestParam(value = "bRoot") Boolean root) {

        return placeTypeDao.getPlaceTypes(area, root);
    }

    @ApiOperation(value = "Получение субъекта", notes = noteGetPlaceType )
    @RequestMapping(value = "/getPlaceType",
            method = RequestMethod.GET, headers = { JSON_TYPE })
    public
    @ResponseBody
    PlaceType getPlaceType(@ApiParam(value = "нет описания", required = true) @RequestParam(value = "nID") Long placeTypeId) {

        return placeTypeDao.findByIdExpected(placeTypeId);
    }

    @ApiOperation(value = "Cоздание новый тип места", notes = noteSetPlaceType )
    @RequestMapping(value = "/setPlaceType",
            method = RequestMethod.POST, headers = { JSON_TYPE })
    public
    @ResponseBody
    void setPlaceType(
	    @ApiParam(value = "нет описания", required = false) @RequestParam(value = "nID", required = false) Long placeTypeId,
            @ApiParam(value = "нет описания", required = false) @RequestParam(value = "sName", required = false) String name,
            @ApiParam(value = "нет описания", required = false) @RequestParam(value = "nOrder", required = false) Long order,
            @ApiParam(value = "нет описания", required = true) @RequestParam(value = "bArea", defaultValue = "false") Boolean area,
            @ApiParam(value = "нет описания", required = true) @RequestParam(value = "bRoot", defaultValue = "false") Boolean root) {

        PlaceType placeType = new PlaceType(placeTypeId, name, order, area, root);

        if (positive(placeTypeId)) {
            swap(placeType, placeTypeDao.findById(placeTypeId), placeTypeDao);

        } else {
            placeTypeDao.saveOrUpdate(placeType);
        }
    }

    @ApiOperation(value = "Удаление типа места", notes = noteRemovePlaceType )
    @RequestMapping(value = "/removePlaceType",
            method = RequestMethod.POST, headers = { JSON_TYPE })
    public
    @ResponseBody
    void removePlaceType(@ApiParam(value = "нет описания", required = true) @RequestParam(value = "nID") Long placeTypeId) {

        placeTypeDao.delete(placeTypeId);
    }

    /**
     * This method allows to swap two entities by Primary Key (PK).
     *
     * @param entity          - entity with new parameters
     * @param persistedEntity - persisted entity with registered PK in DB
     * @param dao             - type-specific dao implementation
     **/
    @SuppressWarnings("unchecked")
    private <T extends Entity> boolean swap(T entity, Optional<T> persistedEntity, EntityDao dao) {
        if (persistedEntity.isPresent()) {
            entity.setId(persistedEntity.get().getId());
            dao.saveOrUpdate(entity);
            return true;
        }
        return false;
    }
}