package org.igov.service.controller;

import liquibase.integration.spring.SpringLiquibase;
import org.igov.model.action.item.Category;
import org.igov.model.action.item.Service;
import org.igov.model.action.item.ServiceData;
import org.igov.model.action.item.Subcategory;
import org.igov.model.object.place.PlaceDao;
import org.igov.service.business.action.item.ServiceTagTreeNodeVO;
import org.igov.service.business.action.item.ServiceTagTreeVO;
import org.igov.service.business.core.TableData;
import org.igov.service.business.core.TableDataService;
import org.igov.util.JSON.JsonRestUtils;
import org.igov.util.db.DbManager;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.util.Arrays;

import static org.hamcrest.Matchers.*;
import static org.igov.service.business.action.ActionItemService.SUPPORTED_PLACE_IDS;
import static org.igov.service.business.action.ActionItemService.checkIdPlacesContainsIdUA;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebAppConfiguration
@RunWith(SpringJUnit4ClassRunner.class)
@ActiveProfiles("default")
@ContextConfiguration(classes = IntegrationTestsApplicationConfiguration.class)
public class ActionItemControllerScenario {
    public static final String APPLICATION_JSON_CHARSET_UTF_8 = "application/json;charset=UTF-8";
    @Autowired
    private WebApplicationContext webApplicationContext;

    private MockMvc mockMvc;

    @Autowired
    private PlaceDao placeDao;

    @Autowired
    private DbManager dbManager;

    @Before
    public void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
    }

    @Test
    public void shouldSuccessfullyGetAndSetServicesAndPlacesTables() throws Exception {
        String jsonData = mockMvc.perform(get("/action/item/getServicesAndPlacesTables")).
                andExpect(status().isOk()).
                andExpect(content().contentType(APPLICATION_JSON_CHARSET_UTF_8)).
                andExpect(jsonPath("$", not(empty()))).
                andReturn().getResponse().getContentAsString();
        TableData[] tableDataList = JsonRestUtils.readObject(jsonData, TableData[].class);
        Assert.assertEquals(TableDataService.TablesSet.ServicesAndPlaces.getEntityClasses().length,
                tableDataList.length);

        mockMvc.perform(post("/action/item/setServicesAndPlacesTables").content(jsonData).
                contentType(APPLICATION_JSON_CHARSET_UTF_8)).
                andExpect(status().isOk()).
                andExpect(content().contentType(APPLICATION_JSON_CHARSET_UTF_8));
    }

    @Test
    public void shouldSuccessfullyGetAndSetServicesTree() throws Exception {
        String jsonData = mockMvc.perform(get("/action/item/getServicesTree")).
                andExpect(status().isOk()).
                andExpect(content().contentType(APPLICATION_JSON_CHARSET_UTF_8)).
                andExpect(jsonPath("$", not(empty()))).
                andReturn().getResponse().getContentAsString();
        Category[] categoriesBeforeChange = JsonRestUtils.readObject(jsonData, Category[].class);

        String categoryName = "CategoryName438";
        String subcategoryName = "SubcategoryName9873";
        categoriesBeforeChange[0].setName(categoryName);
        categoriesBeforeChange[0].getSubcategories().get(0).setName(subcategoryName);
        String serviceName = categoriesBeforeChange[0].getSubcategories().get(0).getServices().get(0).getName();

        mockMvc.perform(post("/action/item/setServicesTree").content(JsonRestUtils.toJson(categoriesBeforeChange)).
                contentType(APPLICATION_JSON_CHARSET_UTF_8).
                accept(MediaType.APPLICATION_JSON)).
                andExpect(status().isOk()).
                andExpect(content().contentType(APPLICATION_JSON_CHARSET_UTF_8)).
                andExpect(jsonPath("$[0].sName", is(categoryName)));

        jsonData = mockMvc.perform(get("/action/item/getServicesTree").
                param("sFind", serviceName).
                contentType(APPLICATION_JSON_CHARSET_UTF_8)).
                andExpect(status().isOk()).
                andExpect(content().contentType(APPLICATION_JSON_CHARSET_UTF_8)).
                andReturn().getResponse().getContentAsString();
        Category[] categoriesAfterChange = JsonRestUtils.readObject(jsonData, Category[].class);
        Assert.assertEquals(categoryName, categoriesAfterChange[0].getName());
        Assert.assertEquals(subcategoryName, categoriesAfterChange[0].getSubcategories().get(0).getName());
    }

    @Test
    public void shouldSuccessfullyFilterServicesTreeByPlaceId() throws Exception {
        for (String supportedPlaceId : SUPPORTED_PLACE_IDS) {
            String jsonData = mockMvc
                    .perform(get("/action/item/getServicesTree").param("asID_Place_UA", supportedPlaceId))
                    .andExpect(status().isOk()).andExpect(content().contentType(APPLICATION_JSON_CHARSET_UTF_8))
                    .andReturn().getResponse().getContentAsString();
            Category[] categories = JsonRestUtils.readObject(jsonData, Category[].class);
            if (categories.length == 0) {
                continue;
            }

            for (int i = 0; i < categories.length; i++) {
                Category category = categories[i];
                for (Subcategory subcategory : category.getSubcategories()) {
                    for (Service service : subcategory.getServices()) {
                        String serviceJsonData = mockMvc
                                .perform(get("/action/item/getService").param("nID", service.getId().toString()))
                                .andExpect(status().isOk())
                                .andExpect(content().contentType(APPLICATION_JSON_CHARSET_UTF_8)).andReturn()
                                .getResponse().getContentAsString();
                        Service serviceWithServiceData = JsonRestUtils.readObject(serviceJsonData, Service.class);
                        if (serviceWithServiceData.getServiceDataList() != null) {
                            boolean hasPlaceId = false;
                            boolean nationalService = false;

                            int totalServiceDataCount = 0;
                            for (ServiceData serviceData : serviceWithServiceData.getServiceDataList()) {
                                if (serviceData.getoPlace() == null) {
                                    nationalService = true;
                                    totalServiceDataCount++;
                                    continue; // national service
                                }

                                boolean dataHasPlaceId = checkIdPlacesContainsIdUA(
                                        placeDao, serviceData.getoPlace(), Arrays.asList(supportedPlaceId));

                                if (dataHasPlaceId) {
                                    hasPlaceId = true;
                                    totalServiceDataCount++;
                                }
                            }

                            if (!hasPlaceId && !nationalService) {
                                Assert.assertTrue(String.format("Service [%s] is wrong!", service.getName()) , false);
                            }

                            Assert.assertEquals(service.getSub(), totalServiceDataCount);
                        }
                    }
                }
            }
            break;
        }
    }

    @Test
    public void shouldSuccessfullyGetAndSetService() throws Exception {
        String jsonData = mockMvc.perform(get("/action/item/getService").
                param("nID", "1")).
                andExpect(status().isOk()).
                andExpect(content().contentType(APPLICATION_JSON_CHARSET_UTF_8)).
                andExpect(jsonPath("$.nID", is(1))).
                andExpect(jsonPath("$.sName", not(empty()))).
                andReturn().getResponse().getContentAsString();
        Service serviceBeforeChange = JsonRestUtils.readObject(jsonData, Service.class);

        String serviceName = "ServiceName123";
        String serviceUrl = "ServiceDataUrl7483";
        serviceBeforeChange.setName(serviceName);
        serviceBeforeChange.getServiceDataList().get(0).setUrl(serviceUrl);

        mockMvc.perform(post("/action/item/setService").content(JsonRestUtils.toJson(serviceBeforeChange)).
                contentType(APPLICATION_JSON_CHARSET_UTF_8).
                accept(MediaType.APPLICATION_JSON)).
                andExpect(status().isOk()).
                andExpect(content().contentType(APPLICATION_JSON_CHARSET_UTF_8)).
                andExpect(jsonPath("$.nID", is(1))).
                andExpect(jsonPath("$.sName", is(serviceName)));

        jsonData = mockMvc.perform(get("/action/item/getService").
                param("nID", "1").
                contentType(APPLICATION_JSON_CHARSET_UTF_8)).
                andExpect(status().isOk()).
                andExpect(content().contentType(APPLICATION_JSON_CHARSET_UTF_8)).
                andReturn().getResponse().getContentAsString();
        Service serviceAfterChange = JsonRestUtils.readObject(jsonData, Service.class);
        Assert.assertEquals(serviceName, serviceAfterChange.getName());
        Assert.assertEquals(serviceUrl, serviceAfterChange.getServiceDataList().get(0).getUrl());
    }

    @Test
    public void getServiceShouldResolveConcreteFileForFieldsWithSmartPaths() throws Exception {
        testGetSetServiceField("{\"nID\":1, \"sInfo\":\"[/test.html]\"}", "$.sInfo",
                "<html><body><span>info</span></body></html>");
        testGetSetServiceField("{\"nID\":1, \"sFAQ\":\"[/test.html]\"}", "$.sFAQ",
                "<html><body><span>faq</span></body></html>");
        testGetSetServiceField("{\"nID\":1, \"sLaw\":\"[/test.html]\"}", "$.sLaw",
                "<html><body><span>law</span></body></html>");
    }

    @Test
    public void getServiceShouldResolveFileByIdForFieldsWithSmartPaths() throws Exception {
        testGetSetServiceField("{\"nID\":1, \"sInfo\":\"[*]\"}", "$.sInfo",
                "<html><body><span>info</span></body></html>");
        testGetSetServiceField("{\"nID\":1, \"sFAQ\":\"[*]\"}", "$.sFAQ", "<html><body><span>faq</span></body></html>");
        testGetSetServiceField("{\"nID\":1, \"sLaw\":\"[*]\"}", "$.sLaw", "<html><body><span>law</span></body></html>");
    }

    @Test
    public void getServiceShouldResolveInitialValueForFieldsWithoutSmartPaths() throws Exception {
        testGetSetServiceField("{\"nID\":1, \"sInfo\":\"somefile.[asdf]info\"}", "$.sInfo", "somefile.[asdf]info");
        testGetSetServiceField("{\"nID\":1, \"sFAQ\":\"somefile.[asdf]faq\"}", "$.sFAQ", "somefile.[asdf]faq");
        testGetSetServiceField("{\"nID\":1, \"sLaw\":\"somefile.[asdf]law\"}", "$.sLaw", "somefile.[asdf]law");
    }

    @Test
    public void setServiceShouldReturnErrorIfContentFilesCannotBeFoundForFieldsWithSmartPaths() throws Exception {
        testGetSetServiceField("{\"nID\":1, \"sInfo\":\"[/some.file]\"}", "$.sInfo", "[/some.file]");
        testGetSetServiceField("{\"nID\":1, \"sFAQ\":\"[/some.file]\"}", "$.sFAQ", "[/some.file]");
        testGetSetServiceField("{\"nID\":1, \"sLaw\":\"[/some.file]\"}", "$.sLaw", "[/some.file]");
    }

    // region File Pattern Service Helpers

    private void testGetSetServiceField(String service, String jsonPath, String expected) throws Exception {
        assertServiceFieldExpected(performSetService(service), jsonPath, expected);
        assertServiceFieldExpected(performGetService((long) 1), jsonPath, expected);
    }

    //endregion

    @Test
    public void recursiveCompletelyDeletedService() throws Exception {

        int serviceId = 4;
        String jsonData = mockMvc.perform(get("/action/item/getService").
                param("nID", "" + serviceId)).
                andExpect(status().isOk()).
                andExpect(content().contentType(APPLICATION_JSON_CHARSET_UTF_8)).
                andExpect(jsonPath("$.nID", is(serviceId))).
                andExpect(jsonPath("$.aServiceData", not(empty()))).
                andExpect(jsonPath("$.sName", not(empty()))).
                andReturn().getResponse().getContentAsString();
        Service actualService = JsonRestUtils.readObject(jsonData, Service.class);

        jsonData = mockMvc.perform(delete("/action/item/removeService").
                param("nID", String.valueOf(actualService.getId()))).
                andExpect(status().isNotModified()).
                andExpect(content().contentType(APPLICATION_JSON_CHARSET_UTF_8)).
                andReturn().getResponse().getContentAsString();
        Assert.assertTrue(jsonData.contains("error"));

        jsonData = mockMvc.perform(delete("/action/item/removeService").
                param("nID", String.valueOf(actualService.getId())).
                param("bRecursive", "true")).
                andExpect(status().isOk()).
                andExpect(content().contentType(APPLICATION_JSON_CHARSET_UTF_8)).
                andReturn().getResponse().getContentAsString();
        Assert.assertTrue(jsonData.contains("success"));
    }

    @Test
    public void deletedServiceById() throws Exception {
        String jsonData = mockMvc.perform(get("/action/item/getService").
                param("nID", "215")).
                andExpect(status().isOk()).
                andExpect(content().contentType(APPLICATION_JSON_CHARSET_UTF_8)).
                andExpect(jsonPath("$.nID", is(215))).
                andExpect(jsonPath("$.aServiceData", is(empty()))).
                andExpect(jsonPath("$.sName", not(empty()))).
                andReturn().getResponse().getContentAsString();
        Service actualService = JsonRestUtils.readObject(jsonData, Service.class);

        jsonData = mockMvc.perform(delete("/action/item/removeService").
                param("nID", String.valueOf(actualService.getId()))).
                andExpect(status().isOk()).
                andExpect(content().contentType(APPLICATION_JSON_CHARSET_UTF_8)).
                andReturn().getResponse().getContentAsString();
        Assert.assertTrue(jsonData.contains("success"));
    }

    @Test
    public void recursiveCompletelyDeletedSubcategory() throws Exception {
        String jsonData = mockMvc.perform(delete("/action/item/removeSubcategory").
                param("nID", "6")).
                andExpect(status().isNotModified()).
                andExpect(content().contentType(APPLICATION_JSON_CHARSET_UTF_8)).
                andReturn().getResponse().getContentAsString();
        Assert.assertTrue(jsonData.contains("error"));

        jsonData = mockMvc.perform(delete("/action/item/removeSubcategory").
                param("nID", "6").
                param("bRecursive", "true")).
                andExpect(status().isOk()).
                andExpect(content().contentType(APPLICATION_JSON_CHARSET_UTF_8)).
                andReturn().getResponse().getContentAsString();
        Assert.assertTrue(jsonData.contains("success"));
    }

    @Test
    public void deletedSubcategoryById() throws Exception {
        String jsonData = mockMvc.perform(delete("/action/item/removeSubcategory").
                param("nID", "6")).
                andExpect(status().isNotModified()).
                andExpect(content().contentType(APPLICATION_JSON_CHARSET_UTF_8)).
                andReturn().getResponse().getContentAsString();
        Assert.assertTrue(jsonData.contains("error"));

        // currently no subcategory without services

        //       jsonData = mockMvc.perform(delete("/action/item/removeSubcategory").
        //               param("nID", "6").param("bRecursive", "true")).
        //               andExpect(status().isOk()).
        //               andExpect(content().contentType(APPLICATION_JSON_CHARSET_UTF_8)).
        //               andReturn().getResponse().getContentAsString();
        //       Assert.assertTrue(jsonData.contains("success"));
    }

    @Test
    public void recursiveRemoveCategory() throws Exception {
        String jsonData = mockMvc.perform(delete("/action/item/removeCategory").
                param("nID", "2").
                param("bRecursive", "true")).
                andExpect(status().isOk()).
                andExpect(content().contentType(APPLICATION_JSON_CHARSET_UTF_8)).
                andReturn().getResponse().getContentAsString();
        Assert.assertTrue(jsonData.contains("success"));
    }

    @Test
    public void removeCategoryById() throws Exception {
        String jsonData = mockMvc.perform(delete("/action/item/removeCategory").
                param("nID", "1")).
                andExpect(status().isNotModified()).
                andExpect(content().contentType(APPLICATION_JSON_CHARSET_UTF_8)).
                andReturn().getResponse().getContentAsString();
        Assert.assertTrue(jsonData.contains("error"));
    }

    @Test
    public void removeServiceData() throws Exception {
        String jsonData = mockMvc.perform(delete("/action/item/removeServiceData").
                param("nID", "1").
                param("bRecursive", "true")).
                andExpect(status().isOk()).
                andExpect(content().contentType(APPLICATION_JSON_CHARSET_UTF_8)).
                andReturn().getResponse().getContentAsString();
        Assert.assertTrue(jsonData.contains("success"));
    }


    @Test
    public void shouldSuccessfullyGetCatalogTreeTag() throws Exception {
        dbManager.recreateDb();
        String jsonData = mockMvc.perform(get("/action/item/getCatalogTreeTag").
                param("nID_Category", "1").
                param("sFind", "реєстрація").
                param("bNew", "true")).
                andExpect(status().isOk()).
                andExpect(content().contentType(APPLICATION_JSON_CHARSET_UTF_8)).
                andExpect(jsonPath("$", not(empty()))).
                andReturn().getResponse().getContentAsString();
        ServiceTagTreeVO tree = JsonRestUtils.readObject(jsonData, ServiceTagTreeVO.class);

        Assert.assertTrue(tree.getaNode().size() > 0);
        Assert.assertTrue(tree.getaService().size() > 0);
    }

    @Test
    public void shouldSuccessfullyGetCatalogTreeTagService() throws Exception {
        dbManager.recreateDb();
        ServiceTagTreeNodeVO[] tableDataList = null;

        for (int i = 0; i < 2; ++i) {
            String jsonData = mockMvc.perform(get("/action/item/getCatalogTreeTagService").
                    param("nID_Category", "1").
                    param("nID_ServiceTag_Root", "1").
                    param("sFind", "реєстрація").
                    param("bShowEmptyFolders", "false")).
                    andExpect(status().isOk()).
                    andExpect(content().contentType(APPLICATION_JSON_CHARSET_UTF_8)).
                    andExpect(jsonPath("$", not(empty()))).
                    andReturn().getResponse().getContentAsString();
            tableDataList = JsonRestUtils.readObject(jsonData, ServiceTagTreeNodeVO[].class);
        }

        Assert.assertTrue(tableDataList.length == 1);
    }

    @Ignore
    @Test
    public void shouldGetService() throws Exception {
        String jsonData = mockMvc.perform(get("/action/item/getService").
                param("nID", "142")).
                andExpect(status().isOk()).
                andExpect(content().contentType(APPLICATION_JSON_CHARSET_UTF_8)).
                andExpect(jsonPath("$", not(empty()))).
                andReturn().getResponse().getContentAsString();
        Service service = JsonRestUtils.readObject(jsonData, Service.class);
        Assert.assertNotNull(service);
    }

    // region Helpers

    private void assertServiceFieldExpected(ResultActions ra, String jsonPath, String expected) throws Exception {
        ra.andExpect(status().isOk()).
                andExpect(content().contentType(APPLICATION_JSON_CHARSET_UTF_8)).
                andExpect(jsonPath(jsonPath, is(expected)));
    }

    private ResultActions performGetService(Long serviceId) throws Exception {
        return mockMvc.perform(get("/action/item/getService").
                param("nID", serviceId.toString()).
                contentType(APPLICATION_JSON_CHARSET_UTF_8));
    }

    private ResultActions performSetService(String service) throws Exception {
        return mockMvc.perform(post("/action/item/setService").content(service).
                contentType(APPLICATION_JSON_CHARSET_UTF_8).
                accept(MediaType.APPLICATION_JSON));
    }

    //endregion
}
