package org.igov.service.controller;

import org.junit.Before;
import org.junit.FixMethodOrder;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.igov.util.convert.JsonRestUtils;
import org.igov.model.subject.message.SubjectMessage;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebAppConfiguration
@RunWith(SpringJUnit4ClassRunner.class)
@ActiveProfiles("default")
@ContextConfiguration(classes = IntegrationTestsApplicationConfiguration.class)
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class SubjectMessageControllerScenario {

    public static final String SET_MESSAGE = "/subject/message/setMessage";
    
    @Autowired
    private WebApplicationContext webApplicationContext;

    private MockMvc mockMvc;

    @Before
    public void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
    }

    @Ignore
    @Test
    public void firstShouldSuccessfullySetAndGetMassage() throws Exception {
        String messageBody = "XXX";
        String jsonAfterSave = mockMvc.perform(post("/subject/message/setMessage").
                contentType(MediaType.APPLICATION_JSON).
                param("sHead", "expect").
                param("sBody", messageBody).
                param("sContacts", "093").
                param("sData", "some data").
                param("sMail", "ukr.net").
                param("nID_SubjectMessageType", "1")).
                andExpect(status().isOk()).andReturn().getResponse().getContentAsString();
        SubjectMessage savedMessage = JsonRestUtils.readObject(jsonAfterSave, SubjectMessage.class);
        assertNotNull(savedMessage.getId());
        assertNotNull(savedMessage.getSubjectMessageType());
        assertEquals(1L, savedMessage.getSubjectMessageType().getId().longValue());
        assertEquals(messageBody, savedMessage.getBody());
        assertEquals(0L, savedMessage.getId_subject().longValue());

        String jsonAfterGet = mockMvc.perform(get("/subject/message/getMessage").param("nID", "" + savedMessage.getId())).
                andExpect(status().isOk()).andReturn().getResponse().getContentAsString();
        assertEquals(jsonAfterSave, jsonAfterGet);
    }

    @Ignore
    @Test
    public void nextShouldSuccessfullySetMassageWithDefaultSubjectID() throws Exception {
        mockMvc.perform(post("/subject/message/setMessage").
                contentType(MediaType.APPLICATION_JSON).
                param("sHead", "expect").
                param("sBody", "XXX").
                param("sMail", "ukr.net")).
                andExpect(status().isOk());
    }
    @Ignore
    @Test
    public void shouldFailedNoObligatedParam() throws Exception {
        mockMvc.perform(post("/subject/message/setMessage").
                contentType(MediaType.APPLICATION_JSON).
                param("sBody", "XXXXXxxx").
                param("sMail", "ukr.ed")).
                andExpect(status().isBadRequest());
    }
    
    @Test
    public void testTransferDataFromMail() throws Exception
    {
        String jsonAfterExecute = mockMvc.perform(get("/subject/message/transferDataFromMail").
              contentType(MediaType.APPLICATION_JSON)).
              andExpect(status().isOk()).andReturn().getResponse().getContentAsString();
       
    }
    //@Ignore
    @Test
    public void testSetMessage_nIDSubject_sMailNull() throws Exception
    {
       String messageBody = "XXX";
       String messageHead = "expect";
       String jsonAfterSave = mockMvc.perform(post(SET_MESSAGE).
              contentType(MediaType.APPLICATION_JSON).
              param("sHead", messageHead).
              param("sBody", messageBody).
              param("nID_Subject", "22")).
              andExpect(status().isOk()).andReturn().getResponse().getContentAsString();

    }
   
    //@Ignore
    @Test
    public void testSetMessage_nIDSubject_sMailEmpty() throws Exception
    {
       String messageBody = "XXX";
       String messageHead = "expect";
       String jsonAfterSave = mockMvc.perform(post(SET_MESSAGE).
              contentType(MediaType.APPLICATION_JSON).
              param("sHead", messageHead).
              param("sBody", messageBody).
              param("nID_Subject", "22").
              param("sMail", "")).
              andExpect(status().isOk()).andReturn().getResponse().getContentAsString();

    }
    //@Ignore
    @Test
    public void testSetMessage_nIDSubject() throws Exception
    {
       String messageBody = "XXX";
       String messageHead = "expect";
       String jsonAfterSave = mockMvc.perform(post(SET_MESSAGE).
              contentType(MediaType.APPLICATION_JSON).
              param("sHead", messageHead).
              param("sBody", messageBody).
              param("nID_Subject", "22").
              param("sMail", "test@igov.org.ua")).
              andExpect(status().isOk()).andReturn().getResponse().getContentAsString();
          
    }
   // @Ignore
    @Test
    public void testSetMessageMailEmpty_nIDSubjectNull() throws Exception
    {
       String messageBody = "XXX";
       String messageHead = "expect";
       String jsonAfterSave = mockMvc.perform(post(SET_MESSAGE).
              contentType(MediaType.APPLICATION_JSON).
              param("sHead", messageHead).
              param("sBody", messageBody).
              param("sMail", "")).
              andExpect(status().isOk()).andReturn().getResponse().getContentAsString();

    }
    
    //@Ignore
    @Test
    public void testSetMessageWithout_nIDSubject() throws Exception
    {
       String messageBody = "XXX";
       String messageHead = "expect";
       String jsonAfterSave = mockMvc.perform(post(SET_MESSAGE).
              contentType(MediaType.APPLICATION_JSON).
              param("sHead", messageHead).
              param("sBody", messageBody).
              param("sMail", "test@igov.org.ua")).
              andExpect(status().isOk()).andReturn().getResponse().getContentAsString();
    }
}
