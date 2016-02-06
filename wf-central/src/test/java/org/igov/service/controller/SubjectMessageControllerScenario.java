package org.igov.service.controller;

import java.util.List;
import org.igov.model.subject.Subject;
import org.igov.model.subject.SubjectContact;
import org.igov.model.subject.SubjectContactDao;
import org.igov.model.subject.SubjectDao;
import org.igov.model.subject.SubjectHuman;
import org.igov.model.subject.SubjectHumanDao;
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
import org.igov.util.JSON.JsonRestUtils;
import org.igov.model.subject.message.SubjectMessage;
import org.joda.time.DateTime;
import org.junit.Assert;
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
    @Autowired
    SubjectContactDao subjectContactDao;
    @Autowired
    SubjectHumanDao subjectHumanDao;
    @Autowired
    SubjectDao subjectDao;

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
    
    
    @Ignore
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
   
    @Ignore
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
    @Ignore
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
    @Ignore
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
    @Ignore
    @Test
    public void testSetMessageWithSubject() throws Exception
    {
        //issue-1053
        //пункт 3.2 (при наличии nID_Subject: в случае отсутствия контактов подвязываем SubjectContact и 
        //делаем его дефолтным в структуре SubjectHuman)
       Subject subject = subjectDao.getSubject(25L);
       List<SubjectContact> subjectContacts = subjectContactDao.findContacts(subject);
       Assert.assertTrue(subjectContacts.size() == 0);
       String messageBody = "XXX";
       String messageHead = "expect";
       String jsonAfterSave = mockMvc.perform(post(SET_MESSAGE).
              contentType(MediaType.APPLICATION_JSON).
              param("sHead", messageHead).
              param("sBody", messageBody).
              param("nID_Subject", "25").
              param("sMail", "test24@igov.org.ua")).
              andExpect(status().isOk()).andReturn().getResponse().getContentAsString();
       
       SubjectMessage savedMessage = JsonRestUtils.readObject(jsonAfterSave, SubjectMessage.class);
       SubjectContact subjectContact = savedMessage.getoMail();
       SubjectHuman subjectHuman = subjectHumanDao.findByExpected("oSubject", subject);
       SubjectContact subjectContact_Default = subjectHuman.getDefaultEmail();
       Assert.assertNotNull(subjectContact);
       Assert.assertNotNull(subjectContact_Default);
       Assert.assertEquals(subjectContact.getsValue(), subjectContact_Default.getsValue());
       
       //пункт 3.2 при наличии nID_Subject и контактов у субъекта, но отсутствии заданного контакта
       //добавляем контакт в SubjectContact и делаем его дефолтным в SubjectHuman
       
       subject = subjectDao.getSubject(24L);
       subjectContacts = subjectContactDao.findContacts(subject);
       
       Assert.assertTrue(subjectContacts.size() != 0);
       boolean no_contact = true;
       for(SubjectContact sc : subjectContacts)
       {
          if(sc.getsValue().equals("test25@igov.org.ua"))
              no_contact = false;
       }
       Assert.assertTrue(no_contact);
       
       jsonAfterSave = mockMvc.perform(post(SET_MESSAGE).
              contentType(MediaType.APPLICATION_JSON).
              param("sHead", messageHead).
              param("sBody", messageBody).
              param("nID_Subject", "24").
              param("sMail", "test25@igov.org.ua")).
              andExpect(status().isOk()).andReturn().getResponse().getContentAsString();
       
       savedMessage = JsonRestUtils.readObject(jsonAfterSave, SubjectMessage.class);
       subjectContact = savedMessage.getoMail();
       subjectHuman = subjectHumanDao.findByExpected("oSubject", subject);
       subjectContact_Default = subjectHuman.getDefaultEmail();
       subjectContacts = subjectContactDao.findContacts(subject);
       for(SubjectContact sc : subjectContacts)
       {
          if(sc.getsValue().equals("test25@igov.org.ua"))
              no_contact = false;
  
       }
       Assert.assertNotNull(subjectContact);
       Assert.assertNotNull(subjectContact_Default);
       Assert.assertEquals(subjectContact.getsValue(), subjectContact_Default.getsValue());
       Assert.assertFalse(no_contact);
       
       //все контакты есть — обновляем sDate SubjectContact на текущее значение
       
       subject = subjectDao.getSubject(24L);
       subjectContacts = subjectContactDao.findContacts(subject);
       Assert.assertTrue(subjectContacts.size() != 0);
       no_contact = true;
       DateTime date = null;
       for(SubjectContact sc : subjectContacts)
       {
          if(sc.getsValue().equals("test25@igov.org.ua"))
          {
              no_contact = false;
              date = sc.getsDate();
          }
       }
       Assert.assertFalse(no_contact);
       
       
       jsonAfterSave = mockMvc.perform(post(SET_MESSAGE).
              contentType(MediaType.APPLICATION_JSON).
              param("sHead", messageHead).
              param("sBody", messageBody).
              param("nID_Subject", "24").
              param("sMail", "test25@igov.org.ua")).
              andExpect(status().isOk()).andReturn().getResponse().getContentAsString();
       
       savedMessage = JsonRestUtils.readObject(jsonAfterSave, SubjectMessage.class);
       subjectContact = savedMessage.getoMail();
       DateTime date1 = null;
       subjectContacts = subjectContactDao.findContacts(subject);
       Assert.assertTrue(subjectContacts.size() != 0);
       no_contact = true;
       for(SubjectContact sc : subjectContacts)
       {
          if(sc.getsValue().equals("test25@igov.org.ua"))
          {
              no_contact = false;
              date1 = sc.getsDate();
          }
       }
       Assert.assertFalse(no_contact);
      
       Assert.assertNotNull(subjectContact);
       Assert.assertNotEquals(date, date1);
       
    }
    @Ignore
    @Test
    public void testSetMessageWithout_nIDSubject() throws Exception
    {
       //issue-1053
      //если есть такой дефолтный контакт в SubjectHuman обновляем nID_Subject в SubjectMessage и в SubjectContact
      //к пункту 3.1
        
       String messageBody = "XXX";
       String messageHead = "expect";
       String mail = "test@igov.org.ua";
       String jsonAfterSave = mockMvc.perform(post(SET_MESSAGE).
              contentType(MediaType.APPLICATION_JSON).
              param("sHead", messageHead).
              param("sBody", messageBody).
              param("sMail", mail)).
              andExpect(status().isOk()).andReturn().getResponse().getContentAsString();
       
        SubjectMessage savedMessage = JsonRestUtils.readObject(jsonAfterSave, SubjectMessage.class);
        SubjectContact subjectContact = savedMessage.getoMail();
        
        Assert.assertNotNull(savedMessage.getId_subject());
        Assert.assertNotNull(subjectContact.getSubject());
        Assert.assertEquals(savedMessage.getId_subject(), subjectContact.getSubject().getId());
      
        
        
    }
  
    @Test
    public void testTransferDataFromMail() throws Exception
    {
        //issue-1053
        //тест переноса данных с поля sMail в поле типа SubjectContact 
        String jsonAfterExecute = mockMvc.perform(get("/subject/message/transferDataFromMail").
              contentType(MediaType.APPLICATION_JSON)).
              andExpect(status().isOk()).andReturn().getResponse().getContentAsString();
       
    }
   
}
