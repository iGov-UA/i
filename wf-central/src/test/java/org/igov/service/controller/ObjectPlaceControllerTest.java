package org.igov.service.controller;

import org.apache.commons.lang3.exception.ExceptionUtils;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.igov.util.JSON.JsonRestUtils;
import org.igov.model.object.place.PlaceHierarchyTree;
import org.igov.model.object.place.Region;
import static org.igov.service.controller.ActionItemControllerScenario.APPLICATION_JSON_CHARSET_UTF_8;
import org.junit.Assert;

import static org.junit.Assert.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * @author dgroup
 * @since 09.08.2015
 */
@WebAppConfiguration
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = IntegrationTestsApplicationConfiguration.class)
@ActiveProfiles("default")
public class ObjectPlaceControllerTest {

    @Autowired
    private WebApplicationContext webApplicationContext;
    private MockMvc mockMvc;

    @Before
    public void setUp() {
        mockMvc = MockMvcBuilders
                .webAppContextSetup(webApplicationContext)
                .build();
    }

    

    @Test
    public void shouldSuccessfullyGetAndSetPlaces() throws Exception {
        String jsonData = mockMvc.perform(get("/object/place/getPlaces").
                contentType(APPLICATION_JSON_CHARSET_UTF_8)).
                andExpect(status().isOk()).
                andExpect(content().contentType(APPLICATION_JSON_CHARSET_UTF_8)).
                andExpect(jsonPath("$", not(empty()))).
                andReturn().getResponse().getContentAsString();
        Region[] regionsBeforeChange = JsonRestUtils.readObject(jsonData, Region[].class);

        String testName = "Place4378";
        String cityName = "City438";
        regionsBeforeChange[0].setName(testName);
        regionsBeforeChange[0].getCities().get(0).setName(cityName);

        mockMvc.perform(post("/object/place/setPlaces").content(JsonRestUtils.toJson(regionsBeforeChange)).
                contentType(APPLICATION_JSON_CHARSET_UTF_8).
                accept(MediaType.APPLICATION_JSON)).
                andExpect(status().isOk()).
                andExpect(content().contentType(APPLICATION_JSON_CHARSET_UTF_8)).
                andExpect(jsonPath("$[0].sName", is(testName)));

        jsonData = mockMvc.perform(get("/object/place/getPlaces")).
                andExpect(status().isOk()).
                andExpect(content().contentType(APPLICATION_JSON_CHARSET_UTF_8)).
                andReturn().getResponse().getContentAsString();
        Region[] placesAfterChange = JsonRestUtils.readObject(jsonData, Region[].class);
        Assert.assertEquals(testName, placesAfterChange[0].getName());
        Assert.assertEquals(cityName, placesAfterChange[0].getCities().get(0).getName());
    }
    
    
    @Test
    @Ignore(value = "Should be run only on test evn, but 'Test' profile is working on local env.")
    public void getPlacesTreeById() {
        try {

            String jsonData = mockMvc
                    .perform(get("/object/place/getPlacesTree").param("nID", "459"))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andReturn()
                    .getResponse()
                    .getContentAsString();
            PlaceHierarchyTree tree = JsonRestUtils.readObject(jsonData, PlaceHierarchyTree.class);
            assertNotNull(tree);

        } catch (Exception e) {
            fail(ExceptionUtils.getStackTrace(e));
        }
    }

    @Test
    @Ignore(value = "Should be run only on test evn, but 'Test' profile is working on local env.")
    public void getPlace() {
        try {

            String jsonData = mockMvc
                    .perform(get("/object/place/getPlace").param("nID", "459"))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andReturn()
                    .getResponse()
                    .getContentAsString();
            PlaceHierarchyTree tree = JsonRestUtils.readObject(jsonData, PlaceHierarchyTree.class);
            assertNotNull(tree);
            assertEquals("IDs aren't match", 459L, tree.getPlace().getId().longValue());

        } catch (Exception e) {
            fail(ExceptionUtils.getStackTrace(e));
        }
    }
    
    
    
}