package org.igov.service.controller;

import io.swagger.annotations.*;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.igov.io.GeneralConfig;
import org.igov.model.action.item.Category;
import org.igov.model.action.item.Service;
import org.igov.model.action.item.ServiceData;
import org.igov.model.action.item.Subcategory;
import org.igov.model.core.BaseEntityDao;
import org.igov.model.core.AbstractEntity;
import org.igov.model.object.place.Place;
import org.igov.model.object.place.PlaceDao;
import org.igov.service.business.action.item.ServiceTagService;
import org.igov.service.business.action.item.ServiceTagTreeNodeVO;
import org.igov.service.business.action.item.ServiceTagTreeVO;
import org.igov.service.business.core.EntityService;
import org.igov.service.business.core.TableData;
import org.igov.service.business.core.TableDataService;
import org.igov.service.business.object.place.KOATUU;
import org.igov.util.JSON.JsonRestUtils;
import org.igov.util.cache.CachedInvocationBean;
import org.igov.util.cache.MethodCacheInterceptor;
import org.igov.util.cache.SerializableResponseEntity;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;
import org.apache.commons.collections.CollectionUtils;

import org.igov.model.action.item.ServiceTag;
import org.igov.model.action.item.ServiceTagLink;

import static org.igov.util.Tool.bFoundText;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import springfox.documentation.spring.web.json.Json;

@Controller
@Api(tags = {"ActionItemController - Предметы действий (каталог сервисов)"})
@RequestMapping(value = "/action/item")
public class ActionItemController {

    public static final String SERVICE_NAME_TEST_PREFIX = "_";
    public static final List<String> SUPPORTED_PLACE_IDS = new ArrayList<>();
    private static final String GET_SERVICES_TREE = "getServicesTree";
    private static final String GET_CATALOG_TREE_TAG_SERVICE = "getCatalogTreeTagService";
    private static final String GET_CATALOG_TREE_TAG = "getCatalogTreeTag";
    private static final Logger LOG = LoggerFactory.getLogger(ActionItemController.class);

    static {
        SUPPORTED_PLACE_IDS.add(String.valueOf(KOATUU.KYIVSKA_OBLAST.getId()));
        SUPPORTED_PLACE_IDS.add(String.valueOf(KOATUU.KYIV.getId()));
    }

    @Autowired
    GeneralConfig generalConfig;
    @Autowired
    private BaseEntityDao<Long> baseEntityDao;
    @Autowired
    private EntityService entityService;
    @Autowired
    private TableDataService tableDataService;
    @Autowired
    private CachedInvocationBean cachedInvocationBean;
    @Autowired(required = false)
    private MethodCacheInterceptor methodCacheInterceptor;
    @Autowired
    private PlaceDao placeDao;
    @Autowired
    private ServiceTagService serviceTagService;

    static boolean checkIdPlacesContainsIdUA(PlaceDao placeDao, Place place, List<String> asID_Place_UA) {
        boolean res = false;

        if (place != null) {
            if (asID_Place_UA.contains(place.getsID_UA())) {
                res = true;
            } else {
                Place root = placeDao.getRoot(place);

                if (root != null && asID_Place_UA.contains(root.getsID_UA())) {
                    res = true;
                }
            }
        }

        return res;
    }

    @ApiOperation(value = "Получение сервиса", notes = "#####Пример:\n"
            + "https://test.igov.org.ua/wf/service/action/item/getService?nID=1\n\n"
            + "\n```json\n"
            + "Ответ:\n"
            + "{\n"
            + "  \"sSubjectOperatorName\": \"МВС\",\n"
            + "  \"subjectOperatorName\": \"МВС\",\n"
            + "  \"nID\": 1,\n"
            + "  \"sName\": \"Отримати довідку про несудимість\",\n"
            + "  \"nOrder\": 1,\n"
            + "  \"aServiceData\": [\n"
            + "    {\n"
            + "      \"nID\": 1,\n"
            + "      \"nID_City\": {\n"
            + "        \"nID\": 2,\n"
            + "        \"sName\": \"Кривий Ріг\",\n"
            + "        \"nID_Region\": {\n"
            + "          \"nID\": 1,\n"
            + "          \"sName\": \"Дніпропетровська\"\n"
            + "        }\n"
            + "      },\n"
            + "      \"nID_ServiceType\": {\n"
            + "        \"nID\": 1,\n"
            + "        \"sName\": \"Внешняя\",\n"
            + "        \"sNote\": \"Пользователь переходит по ссылке на услугу, реализованную на сторонней платформе\"\n"
            + "      },\n"
            + "      \"oSubject_Operator\": {\n"
            + "        \"nID\": 1,\n"
            + "        \"oSubject\": {\n"
            + "          \"nID\": 1,\n"
            + "          \"sID\": \"ПАО\",\n"
            + "          \"sLabel\": \"ПАО ПриватБанк\",\n"
            + "          \"sLabelShort\": \"ПриватБанк\"\n"
            + "        },\n"
            + "        \"sOKPO\": \"093205\",\n"
            + "        \"sFormPrivacy\": \"ПАО\",\n"
            + "        \"sName\": \"ПриватБанк\",\n"
            + "        \"sNameFull\": \"Банк ПриватБанк\"\n"
            + "      },\n"
            + "      \"oData\": \"{}\",\n"
            + "      \"sURL\": \"https://null.igov.org.ua\",\n"
            + "      \"bHidden\": false\n"
            + "    }\n"
            + "  ],\n"
            + "  \"sInfo\": \"\",\n"
            + "  \"sFAQ\": \"\",\n"
            + "  \"sLaw\": \"\",\n"
            + "  \"nSub\": 0\n"
            + "}\n"
            + "\n```\n")
    @RequestMapping(value = "/getService", method = RequestMethod.GET)
    public @ResponseBody
    ResponseEntity getService(
            @ApiParam(value = "ИД-номер сервиса ", required = true) @RequestParam(value = "nID") Long nID) {
        Service oService = baseEntityDao.findById(Service.class, nID);
        return regionsToJsonResponse(oService);
    }

    @ApiOperation(value = "Изменение сервиса.", notes
            = "##### Можно менять/добавлять, но не удалять данные внутри сервиса, на разной глубине вложенности. "
            + "Передается json в теле POST запроса в том же формате, в котором он был в getService.\n\n"
            + "Вовращает: HTTP STATUS 200 + json представление сервиса после изменения. Чаще всего то же, что было передано "
            + "в теле POST запроса + сгенерированные id-шники вложенных сущностей, если такие были.\n\n"
            + "\n```json\n"
            + "Пример:\n"
            + "https://test.igov.org.ua/wf/service/action/item/setService\n"
            + "{\n"
            + "    \"sSubjectOperatorName\": \"МВС\",\n"
            + "    \"subjectOperatorName\": \"МВС\",\n"
            + "    \"nID\": 1,\n"
            + "    \"sName\": \"Отримати довідку про несудимість\",\n"
            + "    \"nOrder\": 1,\n"
            + "    \"aServiceData\": [\n"
            + "        {\n"
            + "            \"nID\": 1,\n"
            + "            \"nID_City\": {\n"
            + "                \"nID\": 2,\n"
            + "                \"sName\": \"Кривий Ріг\",\n"
            + "                \"nID_Region\": {\n"
            + "                    \"nID\": 1,\n"
            + "                    \"sName\": \"Дніпропетровська\"\n"
            + "                }\n"
            + "            },\n"
            + "            \"nID_ServiceType\": {\n"
            + "                \"nID\": 1,\n"
            + "                \"sName\": \"Внешняя\",\n"
            + "                \"sNote\": \"Пользователь переходит по ссылке на услугу, реализованную на сторонней платформе\"\n"
            + "            },\n"
            + "            \"oSubject_Operator\": {\n"
            + "                \"nID\": 1,\n"
            + "                \"oSubject\": {\n"
            + "                    \"nID\": 1,\n"
            + "                    \"sID\": \"ПАО\",\n"
            + "                    \"sLabel\": \"ПАО ПриватБанк\",\n"
            + "                    \"sLabelShort\": \"ПриватБанк\"\n"
            + "                },\n"
            + "                \"sOKPO\": \"093205\",\n"
            + "                \"sFormPrivacy\": \"ПАО\",\n"
            + "                \"sName\": \"ПриватБанк\",\n"
            + "                \"sNameFull\": \"Банк ПриватБанк\"\n"
            + "            },\n"
            + "            \"oData\": \"{}\",\n"
            + "            \"sURL\": \"https://null.igov.org.ua\",\n"
            + "            \"bHidden\": false\n"
            + "        }\n"
            + "    ],\n"
            + "    \"sInfo\": \"\",\n"
            + "    \"sFAQ\": \"\",\n"
            + "    \"sLaw\": \"\",\n"
            + "    \"nSub\": 0\n"
            + "}\n"
            + "\n```\n"
            + "Ответ:\n"
            + "\n```json\n"
            + "{\n"
            + "    \"sSubjectOperatorName\": \"МВС\",\n"
            + "    \"subjectOperatorName\": \"МВС\",\n"
            + "    \"nID\": 1,\n"
            + "    \"sName\": \"Отримати довідку про несудимість\",\n"
            + "    \"nOrder\": 1,\n"
            + "    \"aServiceData\": [\n"
            + "        {\n"
            + "            \"nID\": 1,\n"
            + "            \"nID_City\": {\n"
            + "                \"nID\": 2,\n"
            + "                \"sName\": \"Кривий Ріг\",\n"
            + "                \"nID_Region\": {\n"
            + "                    \"nID\": 1,\n"
            + "                    \"sName\": \"Дніпропетровська\"\n"
            + "                }\n"
            + "            },\n"
            + "            \"nID_ServiceType\": {\n"
            + "                \"nID\": 1,\n"
            + "                \"sName\": \"Внешняя\",\n"
            + "                \"sNote\": \"Пользователь переходит по ссылке на услугу, реализованную на сторонней платформе\"\n"
            + "            },\n"
            + "            \"oSubject_Operator\": {\n"
            + "                \"nID\": 1,\n"
            + "                \"oSubject\": {\n"
            + "                    \"nID\": 1,\n"
            + "                    \"sID\": \"ПАО\",\n"
            + "                    \"sLabel\": \"ПАО ПриватБанк\",\n"
            + "                    \"sLabelShort\": \"ПриватБанк\"\n"
            + "                },\n"
            + "                \"sOKPO\": \"093205\",\n"
            + "                \"sFormPrivacy\": \"ПАО\",\n"
            + "                \"sName\": \"ПриватБанк\",\n"
            + "                \"sNameFull\": \"Банк ПриватБанк\"\n"
            + "            },\n"
            + "            \"oData\": \"{}\",\n"
            + "            \"sURL\": \"https://null.igov.org.ua\",\n"
            + "            \"bHidden\": false\n"
            + "        }\n"
            + "    ],\n"
            + "    \"sInfo\": \"\",\n"
            + "    \"sFAQ\": \"\",\n"
            + "    \"sLaw\": \"\",\n"
            + "    \"nSub\": 0\n"
            + "}\n"
            + "\n```\n")
    @RequestMapping(value = "/setService", method = RequestMethod.POST)
    public @ResponseBody
    ResponseEntity setService(
            @ApiParam(value = "строка-json в том же формате, в котором он был в getService") @RequestBody String soData_JSON)
            throws IOException {

        Service oService = JsonRestUtils.readObject(soData_JSON, Service.class);

        Service oServiceUpdated = entityService.update(oService);

        return tryClearGetServicesCache(regionsToJsonResponse(oServiceUpdated));
    }

    private ResponseEntity tryClearGetServicesCache(ResponseEntity oResponseEntity) {
        if (methodCacheInterceptor != null && HttpStatus.OK.equals(oResponseEntity.getStatusCode())) {
            methodCacheInterceptor
                    .clearCacheForMethod(CachedInvocationBean.class, "invokeUsingCache", GET_SERVICES_TREE);
        }

        return oResponseEntity;
    }

    @ApiOperation(value = "Удаление сервиса", notes = ""
            + "Вовращает:\n\n"
            + "HTTP STATUS 200 - удаление успешно. HTTP STATUS 304 - не удалено.\n\n"
            + "Пример 1:\n"
            + "https://test.igov.org.ua/wf/service/action/item/removeService?nID=1\n\n"
            + "Ответ 1: HTTP STATUS 304\n\n"
            + "Пример 2: https://test.igov.org.ua/wf/service/action/item/removeService?nID=1&bRecursive=true\n"
            + "Ответ 2: HTTP STATUS 200\n"
            + "\n```json\n"
            + "{\n"
            + "    \"code\": \"success\",\n"
            + "    \"message\": \"class org.igov.activiti.common.Service id: 1 removed\"\n"
            + "}\n"
            + "\n```\n")
    @ApiResponses(value = @ApiResponse(code = 304, message = "Ошибка удаления"))
    @RequestMapping(value = "/removeService", method = RequestMethod.DELETE)
    public @ResponseBody
    ResponseEntity removeService(@ApiParam(value = "ИД-номер сервиса", required = true) @RequestParam(value = "nID") Long nID,
            @ApiParam(value = "булевый флаг. если true -- удалять рекурсивно все данные связанные с сервисом. "
                    + "Если false, то при наличии вложенных сущностей, ссылающихся на эту, сервис удален не будет", required = false) @RequestParam(value = "bRecursive", required = false) Boolean bRecursive) {
        bRecursive = (bRecursive == null) ? false : bRecursive;
        ResponseEntity response;
        if (bRecursive) {
            response = recursiveForceServiceDelete(Service.class, nID);
        } else {
            response = deleteEmptyContentEntity(Service.class, nID);
        }
        return tryClearGetServicesCache(response);
    }

    @ApiOperation(value = "Удаление сущности ServiceData", notes = ""
            + "Вовращает:\n"
            + "HTTP STATUS 200 - удаление успешно. HTTP STATUS 304 - не удалено.\n\n"
            + "Пример:\n"
            + "https://test.igov.org.ua/wf/service/action/item/removeServiceData?nID=1&bRecursive=true\n\n"
            + "Ответ: HTTP STATUS 200\n"
            + "\n```json\n"
            + "{\n"
            + "\"code\": \"success\",\n"
            + "\"message\": \"class org.igov.activiti.common.ServiceData id: 1 removed\"\n"
            + "}\n"
            + "\n```\n")
    @ApiResponses(value = {
        @ApiResponse(code = 304, message = "Ошибка удаления данных сервиса")})
    @RequestMapping(value = "/removeServiceData", method = RequestMethod.DELETE)
    public @ResponseBody
    ResponseEntity removeServiceData(
            @ApiParam(value = "номер-идентификатор ServiceData", required = true) @RequestParam(value = "nID") Long nID,
            @ApiParam(value = "булевый флаг. если true -- удалять рекурсивно все данные связанные с ServiceData. "
                    + "Если false, то при наличии вложенных сущностей, ссылающихся на эту, ServiceData удалена не будет", required = false) @RequestParam(value = "bRecursive", required = false) Boolean bRecursive) {
        bRecursive = (bRecursive == null) ? false : bRecursive;
        ResponseEntity response;
        if (bRecursive) {
            response = recursiveForceServiceDelete(ServiceData.class, nID);
        } else {
            response = deleteEmptyContentEntity(ServiceData.class, nID);
        }
        return tryClearGetServicesCache(response);
    }

    @ApiOperation(value = "Удаление подкатегории", notes = ""
            + "Вовращает:\n\n"
            + "HTTP STATUS 200 - удаление успешно. HTTP STATUS 304 - не удалено.\n\n"
            + "Пример 1:\n"
            + "https://test.igov.org.ua/wf/service/action/item/removeSubcategory?nID=1\n"
            + "Ответ 1: HTTP STATUS 304\n\n"
            + "Пример 2:\n"
            + "https://test.igov.org.ua/wf/service/action/item/removeSubcategory?nID=1&bRecursive=true\n\n"
            + "Ответ 2: HTTP STATUS 200\n"
            + "\n```json\n"
            + "{\n"
            + "    \"code\": \"success\",\n"
            + "    \"message\": \"class org.igov.activiti.common.Subcategory id: 1 removed\"\n"
            + "}\n"
            + "\n```\n")
    @ApiResponses(value = @ApiResponse(code = 304, message = "Ошибка удаления подкатегории"))
    @RequestMapping(value = "/removeSubcategory", method = RequestMethod.DELETE)
    public @ResponseBody
    ResponseEntity removeSubcategory(
            @ApiParam(value = "номер-идентификатор подкатегории", required = true) @RequestParam(value = "nID") Long nID,
            @ApiParam(value = "булевый флаг. если true -- удалять рекурсивно все данные связанные с подкатегорией. "
                    + "Если false, то при наличии вложенных сущностей, ссылающихся на эту, подкатегория удалена не будет", required = true) @RequestParam(value = "bRecursive", required = false) Boolean bRecursive) {
        bRecursive = (bRecursive == null) ? false : bRecursive;
        ResponseEntity response;
        if (bRecursive) {
            response = recursiveForceServiceDelete(Subcategory.class, nID);
        } else {
            response = deleteEmptyContentEntity(Subcategory.class, nID);
        }
        return tryClearGetServicesCache(response);
    }

    @ApiOperation(value = "Удаление категории", notes = ""
            + "Вовращает:\n\n"
            + "HTTP STATUS 200 - удаление успешно. HTTP STATUS 304 - не удалено.\n\n"
            + "Пример 1:\n"
            + "https://test.igov.org.ua/wf/service/action/item/removeCategory?nID=1\n"
            + "Ответ 1: HTTP STATUS 304\n\n"
            + "Пример 2:\n"
            + "https://test.igov.org.ua/wf/service/action/item/removeCategory?nID=1&bRecursive=true\n"
            + "Ответ 2: HTTP STATUS 200\n"
            + "\n```json\n"
            + "{\n"
            + "    \"code\": \"success\",\n"
            + "    \"message\": \"class org.igov.activiti.common.Category id: 1 removed\"\n"
            + "}\n"
            + "\n```\n")
    @ApiResponses(value = @ApiResponse(code = 304, message = "Ошибка удаления категории"))
    @RequestMapping(value = "/removeCategory", method = RequestMethod.DELETE)
    public @ResponseBody
    ResponseEntity removeCategory(
            @ApiParam(value = "номер-идентификатор категории", required = true) @RequestParam(value = "nID") Long nID,
            @ApiParam(value = "булевый флаг. если true -- удалять рекурсивно все данные связанные с категорией. Если false, то при наличии вложенных сущностей, ссылающихся на эту, категория удалена не будет.", required = false) @RequestParam(value = "bRecursive", required = false) Boolean bRecursive) {
        bRecursive = (bRecursive == null) ? false : bRecursive;
        ResponseEntity response;
        if (bRecursive) {
            response = recursiveForceServiceDelete(Category.class, nID);
        } else {
            response = deleteEmptyContentEntity(Category.class, nID);
        }
        return tryClearGetServicesCache(response);
    }

    private <T extends AbstractEntity> ResponseEntity deleteEmptyContentEntity(Class<T> entityClass, Long nID) {
        T entity = baseEntityDao.findById(entityClass, nID);
        if (entity.getClass() == Service.class) {
            if (((Service) entity).getServiceDataList().isEmpty()) {
                return deleteApropriateEntity(entity);
            }
        } else if (entity.getClass() == Subcategory.class) {
            if (((Subcategory) entity).getServices().isEmpty()) {
                return deleteApropriateEntity(entity);
            }
        } else if (entity.getClass() == Category.class) {
            if (((Category) entity).getSubcategories().isEmpty()) {
                return deleteApropriateEntity(entity);
            }
        } else if (entity.getClass() == ServiceData.class) {
            return deleteApropriateEntity(entity);
        }
        return JsonRestUtils.toJsonResponse(HttpStatus.NOT_MODIFIED, "error", "Entity isn't empty");
    }

    @ApiOperation(value = "Удаление всего дерева сервисов и категорий", notes = ""
            + "Вовращает:\n\n"
            + "HTTP STATUS 200 - удаление успешно.\n\n"
            + "Пример 1:\n"
            + "https://test.igov.org.ua/wf/service/action/item/removeServicesTree\n"
            + "\n```json\n"
            + "Ответ 1: HTTP STATUS 200\n"
            + "{\n"
            + "    \"code\": \"success\",\n"
            + "    \"message\": \"ServicesTree removed\"\n"
            + "}\n"
            + "\n```\n")
    @RequestMapping(value = "/removeServicesTree", method = RequestMethod.DELETE)
    public @ResponseBody
    ResponseEntity removeServicesTree() {
        List<Category> categories = new ArrayList<>(baseEntityDao.findAll(Category.class));
        for (Category category : categories) {
            baseEntityDao.delete(category);
        }
        return tryClearGetServicesCache(JsonRestUtils.toJsonResponse(HttpStatus.OK, "success", "ServicesTree removed"));
    }

    private <T extends AbstractEntity> ResponseEntity deleteApropriateEntity(T entity) {
        baseEntityDao.delete(entity);
        return JsonRestUtils.toJsonResponse(HttpStatus.OK, "success", entity.getClass() + " id: " + entity.getId() + " removed");
    }

    private <T extends AbstractEntity> ResponseEntity recursiveForceServiceDelete(Class<T> entityClass, Long nID) {
        T entity = baseEntityDao.findById(entityClass, nID);
        // hibernate will handle recursive deletion of all child entities
        // because of annotation: @OneToMany(mappedBy = "category",cascade = CascadeType.ALL, orphanRemoval = true)
        baseEntityDao.delete(entity);
        return JsonRestUtils.toJsonResponse(HttpStatus.OK, "success", entityClass + " id: " + nID + " removed");
    }

    private ResponseEntity regionsToJsonResponse(Service oService) {
        return JsonRestUtils.toJsonResponse(prepareServiceToView(oService, true));
    }

    private Service prepareServiceToView(Service oService, boolean withServiceData) {
        Service preparedService = new Service();
        preparedService.setId(oService.getId());
        preparedService.setName(oService.getName());
        preparedService.setFaq(oService.getFaq());
        preparedService.setInfo(oService.getInfo());
        preparedService.setLaw(oService.getLaw());
        preparedService.setOpenedLimit(oService.getOpenedLimit());
        preparedService.setOrder(oService.getOrder());

        List<ServiceData> serviceDataFiltered = oService.getServiceDataFiltered(generalConfig.isSelfTest());
        preparedService.setSub(serviceDataFiltered != null ? serviceDataFiltered.size() : 0);
        preparedService.setStatus(Service.calcStatusID(oService.getServiceDataList(), preparedService.getSub()));

        preparedService.setSubjectOperatorName(oService.getSubjectOperatorName());

        if (withServiceData) {
            List<ServiceData> dataList = new ArrayList<>();
            for (ServiceData serviceData : oService.getServiceDataList()) {
                ServiceData preparedData = new ServiceData();
                preparedData.setId(serviceData.getId());
                preparedData.setData(serviceData.getData());
                preparedData.setAsAuth(serviceData.getAsAuth());

                final Place place = serviceData.getoPlace();
                preparedData.setoPlace(place);
                if (place != null) {
                    Place root = placeDao.getRoot(place);
                    preparedData.setoPlaceRoot(root);
                }

                preparedData.setCity(serviceData.getCity());
                preparedData.setRegion(serviceData.getRegion());
                preparedData.setHidden(serviceData.isHidden());
                preparedData.setNote(serviceData.getNote());
                preparedData.setnID_Server(serviceData.getnID_Server());
                preparedData.setTest(serviceData.isTest());
                preparedData.setServiceType(serviceData.getServiceType());
                preparedData.setUrl(serviceData.getUrl());
                preparedData.setSubject_Operator(serviceData.getSubject_Operator());

                // TODO remove if below after migration to new approach (via Place)
                if (preparedData.getCity() != null) {
                    preparedData.getCity().getRegion().setCities(null);
                } else if (preparedData.getRegion() != null) {
                    preparedData.getRegion().setCities(null);
                }
                dataList.add(preparedData);
            }
            preparedService.setServiceDataList(dataList);
            preparedService.setServiceDataList(preparedService.getServiceDataFiltered(generalConfig.isSelfTest()));
        }

        return preparedService;
    }

    @ApiOperation(value = "Получение дерева сервисов", notes = "Дополнительно:\n"
            + "Если general.bTest = false, сервисы, имя которых начинается с \"_\", не вовращаются.\n\n"
            + "Пример: \n"
            + "https://test.igov.org.ua/wf/service/action/item/getServicesTree?asID_Place_UA=3200000000,8000000000\n\n"
            + "Ответ:\n"
            + "\n```json\n"
            + "[\n"
            + "  {\n"
            + "    \"nID\": 1,\n"
            + "    \"sID\": \"Citizen\",\n"
            + "    \"sName\": \"Громадянам\",\n"
            + "    \"nOrder\": 1,\n"
            + "    \"aSubcategory\": [\n"
            + "      {\n"
            + "        \"nID\": 1,\n"
            + "        \"sName\": \"Будівництво, нерухомість, земля\",\n"
            + "        \"sID\": \"Build\",\n"
            + "        \"nOrder\": 1,\n"
            + "        \"aService\": [\n"
            + "          {\n"
            + "            \"sSubjectOperatorName\": \"Міська Рада\",\n"
            + "            \"subjectOperatorName\": \"Міська Рада\",\n"
            + "            \"nID\": 6,\n"
            + "            \"sName\": \"Видача відомостей з документації, що включена до місцевого фонду документації із землеустрою.\",\n"
            + "            \"nOrder\": 6,\n"
            + "            \"nSub\": 1\n"
            + "          },\n"
            + "          {\n"
            + "            \"sSubjectOperatorName\": \"Міська Рада\",\n"
            + "            \"subjectOperatorName\": \"Міська Рада\",\n"
            + "            \"nID\": 8,\n"
            + "            \"sName\": \"Надання довідки про перебування на квартирному обліку при міськвиконкомі за місцем проживання та в житлово-будівельному кооперативі.\",\n"
            + "            \"nOrder\": 8,\n"
            + "            \"nSub\": 1\n"
            + "          },\n"
            + "          {\n"
            + "            \"sSubjectOperatorName\": \"Міська Рада\",\n"
            + "            \"subjectOperatorName\": \"Міська Рада\",\n"
            + "            \"nID\": 9,\n"
            + "            \"sName\": \"Надання довідки про перебування на обліку бажаючих отримати земельну ділянку під індивідуальне будівництво\",\n"
            + "            \"nOrder\": 9,\n"
            + "            \"nSub\": 0\n"
            + "          },\n"
            + "          {\n"
            + "            \"sSubjectOperatorName\": \"Міська Рада\",\n"
            + "            \"subjectOperatorName\": \"Міська Рада\",\n"
            + "            \"nID\": 10,\n"
            + "            \"sName\": \"Видача витягу з технічної документації про нормативну грошову оцінку земельної ділянки\",\n"
            + "            \"nOrder\": 10,\n"
            + "            \"nSub\": 2\n"
            + "          },\n"
            + "          {\n"
            + "            \"sSubjectOperatorName\": \"Міська Рада\",\n"
            + "            \"subjectOperatorName\": \"Міська Рада\",\n"
            + "            \"nID\": 11,\n"
            + "            \"sName\": \"Надання відомостей з Державного земельного кадастру у формі витягу з Державного земельного кадастру про земельну ділянку\",\n"
            + "            \"nOrder\": 11,\n"
            + "            \"nSub\": 0\n"
            + "          },\n"
            + "          {\n"
            + "            \"sSubjectOperatorName\": \"Міська Рада\",\n"
            + "            \"subjectOperatorName\": \"Міська Рада\",\n"
            + "            \"nID\": 12,\n"
            + "            \"sName\": \"Присвоєння поштової адреси об’єкту нерухомого майна\",\n"
            + "            \"nOrder\": 12,\n"
            + "            \"nSub\": 1\n"
            + "          },\n"
            + "          {\n"
            + "            \"sSubjectOperatorName\": \"Міська Рада\",\n"
            + "            \"subjectOperatorName\": \"Міська Рада\",\n"
            + "            \"nID\": 13,\n"
            + "            \"sName\": \"Видача довідок про перебування на квартирному обліку\",\n"
            + "            \"nOrder\": 13,\n"
            + "            \"nSub\": 0\n"
            + "          }\n"
            + "        ]\n"
            + "      }\n"
            + "    ]\n"
            + "  }\n"
            + "]\n"
            + "\n```\n")
    @RequestMapping(value = "/getServicesTree", method = RequestMethod.GET)
    public @ResponseBody
    ResponseEntity<String> getServicesTree(
            @ApiParam(value
                    = "строка-фильтр по имени сервиса. Если задано, то производится фильтрация данных - возвращаются только сервисы, "
                    + "в имени которых встречается значение этого параметра, без учета регистра.", required = false) @RequestParam(value = "sFind", required = false) final String sFind,
            @ApiParam(value
                    = "массив строк - фильтр по ID места (мест), где надается услуга. Поддерживаемие ID: 3200000000 (КИЇВСЬКА ОБЛАСТЬ/М.КИЇВ), 8000000000 (М.КИЇВ). "
                    + "Если указан другой ID, фильтр не применяется.", required = false) @RequestParam(value = "asID_Place_UA", required = false) final List<String> asID_Place_UA,
            @ApiParam(value = "булевый флаг. Возвращать или нет пустые категории и подкатегории (по умолчанию false)", required = true) @RequestParam(value = "bShowEmptyFolders", required = false, defaultValue = "false") final boolean bShowEmptyFolders) {

        final boolean bTest = generalConfig.isSelfTest();

        SerializableResponseEntity<String> entity = cachedInvocationBean
                .invokeUsingCache(new CachedInvocationBean.Callback<SerializableResponseEntity<String>>(
                                GET_SERVICES_TREE, sFind, asID_Place_UA, bTest) {
                                    @Override
                                    public SerializableResponseEntity<String> execute() {
                                        List<Category> aCategory = new ArrayList<>(baseEntityDao.findAll(Category.class));

                                        if (!bTest) {
                                            filterOutServicesByServiceNamePrefix(aCategory, SERVICE_NAME_TEST_PREFIX);
                                        }

                                        if (sFind != null) {
                                            filterServicesByServiceName(aCategory, sFind);
                                        }

                                        if (asID_Place_UA != null) {
                                            //TODO: Зачем это было добавлено?                    asID_Place_UA.retainAll(SUPPORTED_PLACE_IDS);
                                            if (!asID_Place_UA.isEmpty()) {
                                                filterServicesByPlaceIds(aCategory, asID_Place_UA);
                                            }
                                        }

                                        if (!bShowEmptyFolders) {
                                            hideEmptyFolders(aCategory);
                                        }

                                        return categoriesToJsonResponse(aCategory);
                                    }
                                });

        return entity.toResponseEntity();
    }

    private void filterOutServicesByServiceNamePrefix(List<Category> aCategory, String sPrefix) {
        for (Category oCategory : aCategory) {
            //for (Subcategory oSubcategory : oCategory.getSubcategories()) {
            for (Iterator<Subcategory> oSubcategoryIterator = oCategory.getSubcategories().iterator(); oSubcategoryIterator.hasNext();) {
                Subcategory oSubcategory = oSubcategoryIterator.next();
                if (oSubcategory.getName().startsWith(sPrefix)) {
                    oSubcategoryIterator.remove();
                } else {
                    for (Iterator<Service> oServiceIterator = oSubcategory.getServices().iterator(); oServiceIterator.hasNext();) {
                        Service oService = oServiceIterator.next();
                        if (oService.getName().startsWith(sPrefix)) {
                            oServiceIterator.remove();
                        }
                    }
                }
            }
        }
    }

    private void filterServicesByServiceName(List<Category> aCategory, String sFind) {
        for (Category oCategory : aCategory) {
            for (Subcategory oSubcategory : oCategory.getSubcategories()) {
                for (Iterator<Service> oServiceIterator = oSubcategory.getServices().iterator(); oServiceIterator
                        .hasNext();) {
                    Service oService = oServiceIterator.next();
                    if (!bFoundText(oService.getName(), sFind)) {
                        oServiceIterator.remove();
                    }
                }
            }
        }
    }

    private void filterServicesByPlaceIds(List<Category> aCategory, List<String> asID_Place_UA) {
        Set<Place> matchedPlaces = new HashSet<>(); // cache for optimization purposes

        for (Category oCategory : aCategory) {
            for (Subcategory oSubcategory : oCategory.getSubcategories()) {
                filterSubcategoryByPlaceIds(asID_Place_UA, oSubcategory, matchedPlaces);
            }
        }
    }

    private void filterSubcategoryByPlaceIds(List<String> asID_Place_UA, Subcategory oSubcategory,
            Set<Place> matchedPlaces) {
        for (Iterator<Service> oServiceIterator = oSubcategory.getServices().iterator(); oServiceIterator.hasNext();) {
            Service oService = oServiceIterator.next();
            boolean serviceMatchedToIds = false;
            boolean nationalService = false;

            //List<ServiceData> serviceDatas = service.getServiceDataFiltered(generalConfig.isSelfTest());
            List<ServiceData> aServiceData = oService.getServiceDataFiltered(true);
            if (aServiceData != null) {
                for (Iterator<ServiceData> oServiceDataIterator = aServiceData.iterator(); oServiceDataIterator
                        .hasNext();) {
                    ServiceData serviceData = oServiceDataIterator.next();

                    Place place = serviceData.getoPlace();

                    boolean serviceDataMatchedToIds = false;
                    if (place == null) {
                        nationalService = true;
                        continue;
                    }

                    serviceDataMatchedToIds = matchedPlaces.contains(place);

                    if (!serviceDataMatchedToIds) {
                        // heavy check because of additional queries
                        serviceDataMatchedToIds = checkIdPlacesContainsIdUA(placeDao, place, asID_Place_UA);
                    }

                    if (serviceDataMatchedToIds) {
                        matchedPlaces.add(place);
                        serviceMatchedToIds = true;
                        continue;
                    }

                    oServiceDataIterator.remove();
                }
            }
            if (!serviceMatchedToIds && !nationalService) {
                oServiceIterator.remove();
            } else {
                oService.setServiceDataList(aServiceData);
            }
        }
    }

    /**
     * Filter out empty categories and subcategories
     *
     * @param aCategory
     */
    private void hideEmptyFolders(List<Category> aCategory) {
        for (Iterator<Category> oCategoryIterator = aCategory.iterator(); oCategoryIterator.hasNext();) {
            Category oCategory = oCategoryIterator.next();

            for (Iterator<Subcategory> oSubcategoryIterator = oCategory.getSubcategories().iterator(); oSubcategoryIterator
                    .hasNext();) {
                Subcategory oSubcategory = oSubcategoryIterator.next();
                if (oSubcategory.getServices().isEmpty()) {
                    oSubcategoryIterator.remove();
                }
            }

            if (oCategory.getSubcategories().isEmpty()) {
                oCategoryIterator.remove();
            }
        }
    }

    @ApiOperation(value = "Изменение дерева категорий", notes = " Можно менять категории (не добавлять и не удалять) + "
            + "менять/добавлять (но не удалять) вложенные сущности, Передается json в теле POST запроса в том же формате, в котором он был в getServicesTree.\n\n"
            + "Возвращает: HTTP STATUS 200 + json представление сервиса после изменения. Чаще всего то же, что было передано в теле POST запроса + "
            + "сгенерированные id-шники вложенных сущностей, если такие были.\n\n"
            + "Пример: https://test.igov.org.ua/wf/service/action/item/setServicesTree\n"
            + "\n```json\n"
            + "[\n"
            + "  	{\n"
            + "  		\"nID\": 1,\n"
            + "    	\"sID\": \"Citizen\",\n"
            + "		\"sName\": \"Гражданин\",\n"
            + "    	\"nOrder\": 1,\n"
            + "    	\"aSubcategory\": [\n"
            + "            {\n"
            + "                \"nID\": 5,\n"
            + "                \"sName\": \"Праця2\",\n"
            + "                \"sID\": \"Work\",\n"
            + "                \"nOrder\": 3,\n"
            + "                \"aService\": [\n"
            + "                    {\n"
            + "                        \"nID\": 3,\n"
            + "                        \"sName\": \"Видача картки обліку об’єкта торговельного призначення, сфери послуг та з виробництва продуктів харчування\",\n"
            + "                        \"nOrder\": 3\n"
            + "                    },\n"
            + "                    {\n"
            + "                        \"nID\": 4,\n"
            + "                        \"sName\": \"Легалізація об’єднань громадян шляхом повідомлення\",\n"
            + "                        \"nOrder\": 4\n"
            + "                    }\n"
            + "                ]\n"
            + "            }\n"
            + "            ]\n"
            + "         }\n"
            + "]\n"
            + "\n```\n"
            + "Ответ: HTTP STATUS 200\n"
            + "\n```json\n"
            + "[\n"
            + "    {\n"
            + "        \"nID\": 1,\n"
            + "        \"sID\": \"Citizen\",\n"
            + "        \"sName\": \"Гражданин\",\n"
            + "        \"nOrder\": 1,\n"
            + "        \"aSubcategory\": [\n"
            + "            {\n"
            + "                \"nID\": 5,\n"
            + "                \"sName\": \"Праця2\",\n"
            + "                \"sID\": \"Work\",\n"
            + "                \"nOrder\": 3,\n"
            + "                \"aService\": [\n"
            + "                    {\n"
            + "                        \"nID\": 3,\n"
            + "                        \"sName\": \"Видача картки обліку об’єкта торговельного призначення, сфери послуг та з виробництва продуктів харчування\",\n"
            + "                        \"nOrder\": 3\n"
            + "                    },\n"
            + "                    {\n"
            + "                        \"nID\": 4,\n"
            + "                        \"sName\": \"Легалізація об’єднань громадян шляхом повідомлення\",\n"
            + "                        \"nOrder\": 4\n"
            + "                    }\n"
            + "                ]\n"
            + "            }\n"
            + "            ]\n"
            + "         }\n"
            + "]\n"
            + "\n```\n"
            + "Для добавления новой подкатегории нужно передать запрос вида:\n"
            + "\n```json\n"
            + "[\n"
            + "  {\n"
            + "    \"nID\": 1,\n"
            + "    \"aSubcategory\": [\n"
            + "      {\n"
            + "        \"sID\": \"Yd\",\n"
            + "        \"sName\": \"Yjdd\",\n"
            + "        \"nOrder\": \"1\",\n"
            + "        \"oCategory\": {\n"
            + "          \"nID\": 1\n"
            + "        }\n"
            + "      }\n"
            + "    ]\n"
            + "  }\n"
            + "]\n"
            + "\n```\n"
            + "Обязательно нужно указывать внутри подкатегории ссылку на категорию, с помощью\n"
            + "\n```json\n"
            + "\"oCategory\": {\n"
            + "  \"nID\": 1\n"
            + "}\n"
            + "\n```\n"
            + "Для добавления нового сервиса нужно передать запрос вида:\n"
            + "\n```json\n"
            + "[\n"
            + "  {\n"
            + "    \"nID\": 1,\n"
            + "    \"aSubcategory\": [\n"
            + "      {\n"
            + "        \"nID\": 3,\n"
            + "        \"aService\": [\n"
            + "          {\n"
            + "            \"sName\": \"service name\",\n"
            + "            \"nOrder\": 10,\n"
            + "            \"sSubjectOperatorName\": \"subjectOperatorName\",\n"
            + "            \"oSubcategory\": {\n"
            + "              \"nID\": 3\n"
            + "            },\n"
            + "            \"sInfo\": \"\",\n"
            + "            \"sFAQ\": \"\",\n"
            + "            \"sLaw\": \"\"\n"
            + "          }\n"
            + "        ]\n"
            + "      }\n"
            + "    ]\n"
            + "  }\n"
            + "]\n"
            + "\n```\n"
            + "Обязательно нужно указывать внутри сервиса ссылку на подкатегорию, с помощью\n"
            + "\n```json\n"
            + "\"oSubcategory\": {\n"
            + "  \"nID\": 3\n"
            + "}\n"
            + "\n```\n"
            + "А также обязательные поля \"sInfo\", \"sFAQ\", \"sLaw\" - можно с пустыми значениями.\n")
    @RequestMapping(value = "/setServicesTree", method = RequestMethod.POST)
    public @ResponseBody
    ResponseEntity setServicesTree(
            @ApiParam(value = "строка-json в том же формате, в котором он был в getService") @RequestBody String jsonData) {

        List<Category> aCategory = Arrays.asList(JsonRestUtils.readObject(jsonData, Category[].class));
        List<Category> aCategoryUpdated = entityService.update(aCategory);

        return tryClearGetServicesCache(categoriesToJsonResponse(aCategoryUpdated).toResponseEntity());
    }

    @ApiOperation(value = "Скачать данные в виде json", notes = "")
    @RequestMapping(value = "/getServicesAndPlacesTables", method = RequestMethod.GET)
    public @ResponseBody
    ResponseEntity getServicesAndPlacesTables() {
        List<TableData> aTableData = tableDataService.exportData(TableDataService.TablesSet.ServicesAndPlaces);
        return JsonRestUtils.toJsonResponse(aTableData);
    }

    @ApiOperation(value = "Загрузить в виде json (в теле POST запроса)", notes = "")
    @RequestMapping(value = "/setServicesAndPlacesTables", method = RequestMethod.POST)
    public @ResponseBody
    ResponseEntity setServicesAndPlacesTables(
            @ApiParam(value = "строка-json в том же формате, в котором он был в getService") @RequestBody String jsonData) {
        List<TableData> aTableData = Arrays.asList(JsonRestUtils.readObject(jsonData, TableData[].class));
        tableDataService.importData(TableDataService.TablesSet.ServicesAndPlaces, aTableData);
        return JsonRestUtils.toJsonResponse(HttpStatus.OK, "success", "Data successfully imported.");
    }

    @ApiOperation(value = "Скачать данные в json файле", notes = "")
    @RequestMapping(value = "/downloadServicesAndPlacesTables", method = RequestMethod.GET)
    public @ResponseBody
    void downloadServicesAndPlacesTables(HttpServletResponse response) throws IOException {
        List<TableData> aTableData = tableDataService.exportData(TableDataService.TablesSet.ServicesAndPlaces);

        String dateTimeString = DateTimeFormat.forPattern("yyyy-MM-dd_HH-mm-ss").print(new DateTime());

        String fileName = "igov.ua.catalog_" + dateTimeString + ".json";
        response.setHeader("Content-Disposition", "attachment; filename=\"" + fileName + "\"");
        JsonRestUtils.writeJsonToOutputStream(aTableData, response.getOutputStream());
    }

    @ApiOperation(value = "Загрузить из json файла", notes = "##### Пример страницы формы загрузки из файла:\n"
            + "\n```html\n"
            + "<html>\n"
            + "<body>\n"
            + "<form method=\"POST\" enctype=\"multipart/form-data\"\n"
            + "  action=\"http://localhost:8080/wf/service/action/item/uploadServicesAndPlacesTables\">\n"
            + "  File to upload: <input type=\"file\" name=\"file\"><br /> <input type=\"submit\"\n"
            + "  value=\"Upload\"> Press here to upload the file!\n"
            + "</form>\n"
            + "</body>\n"
            + "</html>\n"
            + "\n```\n")
    @RequestMapping(value = "/uploadServicesAndPlacesTables", method = RequestMethod.POST)
    public @ResponseBody
    ResponseEntity uploadServicesAndPlacesTables(
            @ApiParam(value = "файл для загрузки", required = true) @RequestParam("file") MultipartFile file)
            throws IOException {
        List<TableData> tableDataList = Arrays
                .asList(JsonRestUtils.readObject(file.getInputStream(), TableData[].class));

        tableDataService.importData(TableDataService.TablesSet.ServicesAndPlaces, tableDataList);
        return JsonRestUtils.toJsonResponse(HttpStatus.OK, "success", "Data successfully imported.");
    }

    private SerializableResponseEntity<String> categoriesToJsonResponse(List<Category> categories) {
        for (Category c : categories) {
            for (Subcategory sc : c.getSubcategories()) {
                sc.setCategory(null);

                for (Service service : sc.getServices()) {
                    service.setFaq(null);
                    service.setInfo(null);
                    service.setLaw(null);
                    //service.setSub(service.getServiceDataList().size());

                    List<ServiceData> serviceDataFiltered = service.getServiceDataFiltered(generalConfig.isSelfTest());
                    service.setSub(serviceDataFiltered != null ? serviceDataFiltered.size() : 0);
                    //service.setTests(service.getTestsCount());
                    //service.setStatus(service.getTests(); service.getTestsCount());
                    service.setStatus(service.getStatusID());
                    service.setServiceDataList(null);
                    service.setSubcategory(null);
                }
            }
        }

        return new SerializableResponseEntity<>(JsonRestUtils.toJsonResponse(categories));
    }

    @ApiOperation(value = "Получение дерева тегов и услуг", notes = "Дополнительно:\n"
            + "")
    @RequestMapping(value = "/getCatalogTreeTag", method = RequestMethod.GET)
    public @ResponseBody
    ResponseEntity<String> getCatalogTreeTag(
            @ApiParam(value
                    = "строка-фильтр по имени сервиса. Если задано, то производится фильтрация данных - возвращаются только сервисы, "
                    + "в имени которых встречается значение этого параметра, без учета регистра.", required = false)
            @RequestParam(value = "sFind", required = false) String sFind, @ApiParam(value
                    = "массив строк - фильтр по ID места (мест), где надается услуга. Поддерживаемие ID: 3200000000 (КИЇВСЬКА ОБЛАСТЬ/М.КИЇВ), 8000000000 (М.КИЇВ). "
                    + "Если указан другой ID, фильтр не применяется.", required = false)
            @RequestParam(value = "asID_Place_UA", required = false) final List<String> asID_Place_UA, 
            @ApiParam(value = "булевый флаг. Возвращать или нет пустые категории и подкатегории (по умолчанию false)", required = true)
            @RequestParam(value = "bShowEmptyFolders", required = false, defaultValue = "false") final boolean bShowEmptyFolders, @ApiParam(value = "ID категории", required = true)
            @RequestParam(value = "nID_Category", required = true) Long nID_Category, @ApiParam(value = "Новый формат ответа", required = false)
            @RequestParam(value = "bNew", required = false) Boolean bNew
    ) {
        boolean includeServices = StringUtils.isNotBlank(sFind);
        if(!includeServices && CollectionUtils.isNotEmpty(asID_Place_UA)){
            includeServices=true;
            sFind="";
        }
        
        if (includeServices) {
            nID_Category = null;
        }

        List<ServiceTagTreeNodeVO> res = serviceTagService.getCatalogTreeTag(nID_Category, sFind, asID_Place_UA,
                bShowEmptyFolders, includeServices, null, null);
        
        // (asID_Place_UA!=null&&asID_Place_UA.size()>0&&asID_Place_UA.get(0).trim().length()>0)
        if (includeServices) {
            res.forEach(n -> n.setaService(n.getaService().stream().map(
                        s -> prepareServiceToView(s, false)).collect(Collectors.toList())));
        }

        if (BooleanUtils.isTrue(bNew)) {
            return JsonRestUtils.toJsonResponse(toNewFormat(res));
        }
        
        return JsonRestUtils.toJsonResponse(res);
    }

    @ApiOperation(value = "Получение дерева тегов и услуг", notes = "Дополнительно:\n" + "")
    @RequestMapping(value = "/getCatalogTreeTagService", method = RequestMethod.GET)
    public @ResponseBody
    ResponseEntity<String> getCatalogTreeTagService(
            @ApiParam(value
                    = "строка-фильтр по имени сервиса. Если задано, то производится фильтрация данных - возвращаются только сервисы, "
                    + "в имени которых встречается значение этого параметра, без учета регистра.", required = false)
            @RequestParam(value = "sFind", required = false) final String sFind, @ApiParam(value
                    = "массив строк - фильтр по ID места (мест), где надается услуга. Значения ID перечисляются через запятую."
                    + "Если указан другой ID, фильтр не применяется.", required = false)
            @RequestParam(value = "asID_Place_UA", required = false) final List<String> asID_Place_UA, @ApiParam(value = "булевый флаг. Возвращать или нет пустые категории и подкатегории (по умолчанию false)", required = true)
            @RequestParam(value = "bShowEmptyFolders", required = false, defaultValue = "false") final boolean bShowEmptyFolders, @ApiParam(value = "ID категории", required = true)
            @RequestParam(value = "nID_Category", required = true) final Long nID_Category, @ApiParam(value = "ID корневого тега", required = false)
            @RequestParam(value = "nID_ServiceTag_Root", required = false) Long nID_ServiceTag_Root, @ApiParam(value = "ID корневого тега", required = false)
            @RequestParam(value = "nID_ServiceTag_Child", required = false) Long nID_ServiceTag_Child, @ApiParam(value = "Новый формат ответа", required = false)
            @RequestParam(value = "bNew", required = false) Boolean bNew
    ) {
        List<ServiceTagTreeNodeVO> res = serviceTagService.getCatalogTreeTag(nID_Category, sFind, asID_Place_UA,
                bShowEmptyFolders, true, nID_ServiceTag_Root, nID_ServiceTag_Child);
        res.forEach(n -> n.setaService(n.getaService().stream().map(
                s -> prepareServiceToView(s, false)).collect(Collectors.toList())));

        if (BooleanUtils.isTrue(bNew)) {
            return JsonRestUtils.toJsonResponse(toNewFormat(res));
        }

        return JsonRestUtils.toJsonResponse(res);
    }

    private ServiceTagTreeVO toNewFormat(List<ServiceTagTreeNodeVO> nodes) {
        ServiceTagTreeVO res = new ServiceTagTreeVO();

        Set<Service> uniqueServices = new LinkedHashSet<>();
        for (ServiceTagTreeNodeVO node : nodes) {
            if (node.getaService() != null) {
                uniqueServices.addAll(node.getaService());
                node.setaService(null);
            }
        }

        res.setaNode(nodes);
        res.setaService(new ArrayList<>(uniqueServices));

        return res;
    }

}
