package org.igov.service.controller;

import org.activiti.rest.controller.util.ErrMessage;
import org.apache.commons.collections.Predicate;
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
import org.igov.model.document.Document;
import org.igov.model.document.DocumentOperator_SubjectOrgan;
import org.igov.model.subject.organ.SubjectOrganJoin;

import java.util.List;

import static java.util.Arrays.asList;
import static org.apache.commons.collections.CollectionUtils.find;
import static org.junit.Assert.*;
import org.springframework.http.HttpStatus;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.igov.model.subject.DocumentOperatorTest.DUMMY_OPERATOR_ID;
import org.igov.model.subject.Subject;
import org.igov.model.subject.SubjectContact;
import org.igov.model.subject.SubjectContactDao;
import org.igov.model.subject.SubjectDao;
import org.igov.model.subject.SubjectHuman;
import org.igov.model.subject.SubjectHumanDao;
import org.junit.Assert;

/**
 * @author dgroup
 * @since 28.06.15
 */
//@Ignore
@WebAppConfiguration
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = IntegrationTestsApplicationConfiguration.class)
@ActiveProfiles("default")
public class DocumentControllerTest {

    public static final String APPLICATION_JSON_CHARSET_UTF_8 = "application/json;charset=UTF-8";

    @Autowired
    private WebApplicationContext webApplicationContext;
    private MockMvc mockMvc;
    
    @Autowired
    private SubjectContactDao subjectContactDao;
    @Autowired
    private SubjectDao subjectDao;
    @Autowired
    private SubjectHumanDao subjectHumanDao;

    @Before
    public void setUp() {
        mockMvc = MockMvcBuilders
                .webAppContextSetup(webApplicationContext)
                .build();
    }
    @Ignore
    @Test
    public void getAvailableOperators() throws Exception {
        String jsonData = mockMvc
                .perform(get("/document/getDocumentOperators"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andReturn()
                .getResponse()
                .getContentAsString();

        List<DocumentOperator_SubjectOrgan> operators = asList(
                JsonRestUtils.readObject(jsonData, DocumentOperator_SubjectOrgan[].class));

        assertFalse(operators.isEmpty());

        DocumentOperator_SubjectOrgan iGov = (DocumentOperator_SubjectOrgan)
                find(operators, new Predicate() {
                    public boolean evaluate(Object object) {
                        DocumentOperator_SubjectOrgan opr = (DocumentOperator_SubjectOrgan) object;
                        return DUMMY_OPERATOR_ID.equals(opr.getnID_SubjectOrgan());
                    }
                });

        assertEquals("ID aren't match", 1L, iGov.getId().longValue()); // Long vs Object = compiler error
    }
    @Ignore
    @Test
    public void getDocumentByCodeAndOrgan() throws Exception {
        String jsonData = mockMvc
                .perform(get("/document/access/getDocumentAccessByHandler")
                        .param("sCode_DocumentAccess", "1")
                        .param("nID_DocumentOperator_SubjectOrgan", "2")
                        .param("nID_Subject", "1"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andReturn()
                .getResponse()
                .getContentAsString();

        Document doc = JsonRestUtils.readObject(jsonData, Document.class);

        assertNotNull("Document can't be a null", doc);
        assertEquals("IDs aren't match", 1, doc.getId().longValue());
        assertNotNull("Name can't be empty", doc.getName());
        assertEquals("Doc. types aren't match", 0, doc.getDocumentType().getId().longValue());
        assertEquals("Subjects aren't match, ", 1, doc.getSubject().getId().longValue());
    }
    @Ignore
    @Test
    public void getDocumentByCodeAndWrongOrgan() throws Exception {
        String organID = "100500";
        String jsonData = mockMvc
                .perform(get("/document/access/getDocumentAccessByHandler")
                        .param("sCode_DocumentAccess", "1")
                        .param("nID_DocumentOperator_SubjectOrgan", organID)
                        .param("nID_Subject", "1"))
                .andExpect(status().is5xxServerError())
                .andExpect(content().contentType(APPLICATION_JSON_CHARSET_UTF_8))
                .andReturn()
                .getResponse()
                .getContentAsString();

        ErrMessage msg = JsonRestUtils.readObject(jsonData, ErrMessage.class);
        assertNotNull("Expected error message not found", msg);
        assertEquals("Entity with nID_SubjectOrgan='" + organID + "' not found", msg.getMessage());
    }
    @Ignore
    @Test
    public void getDocumentByWrongCode() throws Exception {
        String jsonData = mockMvc
                .perform(get("/document/access/getDocumentAccessByHandler")
                        .param("sCode_DocumentAccess", "100500")
                        .param("nID_DocumentOperator_SubjectOrgan", "2")
                        .param("nID_Subject", "1"))
                .andExpect(status().is5xxServerError())
                .andExpect(content().contentType(APPLICATION_JSON_CHARSET_UTF_8))
                .andReturn()
                .getResponse()
                .getContentAsString();

        ErrMessage msg = JsonRestUtils.readObject(jsonData, ErrMessage.class);
        assertNotNull("Expected error message not found", msg);
        assertEquals("Document Access not found", msg.getMessage());
    }
    @Ignore
    @Test
    public void getDocumentByCodeAndOrganAndPassword() throws Exception {
        String jsonData = mockMvc
                .perform(get("/document/access/getDocumentAccessByHandler")
                        .param("sCode_DocumentAccess", "2")
                        .param("nID_DocumentOperator_SubjectOrgan", "2")
                        .param("sPass", "123")
                        .param("nID_Subject", "1"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andReturn()
                .getResponse()
                .getContentAsString();

        Document doc = JsonRestUtils.readObject(jsonData, Document.class);

        assertNotNull("Document can't be a null", doc);
        assertEquals("IDs aren't match", 2, doc.getId().longValue());
        assertNotNull("Name can't be empty", doc.getName());
        assertEquals("Doc. types aren't match", 1, doc.getDocumentType().getId().longValue());
        assertEquals("Subjects aren't match, ", 2, doc.getSubject().getId().longValue());
    }

    @Test
    @Ignore
    public void getDocumentByCodeAndOrganAndWrongPassword() throws Exception {
        String jsonData = mockMvc
                .perform(get("/document/access/getDocumentAccessByHandler")
                        .param("sCode_DocumentAccess", "2")
                        .param("nID_DocumentOperator_SubjectOrgan", "2")
                        .param("sPass", "100500")
                        .param("nID_Subject", "1"))
                .andExpect(status().is5xxServerError())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andReturn()
                .getResponse()
                .getContentAsString();

        ErrMessage msg = JsonRestUtils.readObject(jsonData, ErrMessage.class);
        assertNotNull("Expected error message not found", msg);
        assertEquals("Document Access wrong password", msg.getMessage());
    }
    @Ignore
    @Test
    public void getDocumentByCodeAndDocumentTypeAndOrganAndPassword() throws Exception {
        String jsonData = mockMvc
                .perform(get("/document/access/getDocumentAccessByHandler")
                        .param("sCode_DocumentAccess", "2")
                        .param("nID_DocumentOperator_SubjectOrgan", "2")
                        .param("nID_DocumentType", "1")
                        .param("sPass", "123")
                        .param("nID_Subject", "1"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andReturn()
                .getResponse()
                .getContentAsString();

        Document doc = JsonRestUtils.readObject(jsonData, Document.class);

        assertNotNull("Document can't be a null", doc);
        assertEquals("IDs aren't match", 2, doc.getId().longValue());
        assertNotNull("Name can't be empty", doc.getName());
        assertEquals("Doc. types aren't match", 1, doc.getDocumentType().getId().longValue());
        assertEquals("Subjects aren't match, ", 2, doc.getSubject().getId().longValue());
    }
    @Ignore
    @Test
    public void getDocumentByCodeAndWrongDocumentTypeAndOrganAndPassword() throws Exception {
        String jsonData = mockMvc
                .perform(get("/document/access/getDocumentAccessByHandler")
                                .param("sCode_DocumentAccess", "2")
                                .param("nID_DocumentOperator_SubjectOrgan", "2")
                                .param("nID_DocumentType", "2")
                                .param("sPass", "123")
                                .param("nID_Subject", "1")
                )
                .andExpect(status().is5xxServerError())
                .andExpect(content().contentType(APPLICATION_JSON_CHARSET_UTF_8))
                .andReturn()
                .getResponse()
                .getContentAsString();

        ErrMessage msg = JsonRestUtils.readObject(jsonData, ErrMessage.class);
        assertNotNull("Expected error message not found", msg);
        assertEquals("Document Access not found", msg.getMessage());
    }

    @Test
    @Ignore
    public void getSubjectOrganJoinByOrganID() throws Exception {
        final Long region = 11L;
        final Long city = 33L;

        String jsonData = mockMvc
                .perform(get("/subject/getSubjectOrganJoins")
                        .param("nID_SubjectOrgan", "1")
                        .param("nID_Region", "11")
                        .param("nID_City", "33"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andReturn()
                .getResponse()
                .getContentAsString();

        List<SubjectOrganJoin> operators = asList(
                JsonRestUtils.readObject(jsonData, SubjectOrganJoin[].class));

        assertFalse("Two or three test operators must be here", operators.isEmpty());

        SubjectOrganJoin mvsGovUa = (SubjectOrganJoin)
                find(operators, new Predicate() {
                    public boolean evaluate(Object object) {
                        SubjectOrganJoin mvs = (SubjectOrganJoin) object;
                        return region.equals(mvs.getRegionId()) &&
                                city.equals(mvs.getCityId());
                    }
                });

        assertEquals("ID aren't match", 356L, mvsGovUa.getId().longValue()); // compile error: Long vs Object

        jsonData = mockMvc
                .perform(get("/subject/getSubjectOrganJoins")
                        .param("nID_SubjectOrgan", "2"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andReturn()
                .getResponse()
                .getContentAsString();

        operators = asList(JsonRestUtils.readObject(jsonData, SubjectOrganJoin[].class));
        assertFalse("Only one row expected, test data error", operators.size() > 1);

        mvsGovUa = operators.iterator().next();
        assertEquals("ID[2] aren't match", 358L, mvsGovUa.getId().longValue()); // compile error: Long vs Object
    }

    @Test
    @Ignore
    public void setSubjectOrganJoins() throws Exception {
        SubjectOrganJoin soj = new SubjectOrganJoin();
        soj.setId(400L);
        soj.setSubjectOrganId(5L);
        soj.setNameUa("Українська мова");
        soj.setNameRu("Русский язык");
        soj.setPrivatId("12345");
        soj.setPublicId("55555");
        soj.setGeoLongitude("15.232312");
        soj.setGeoLatitude("23.234231");

        String jsonSoj = JsonRestUtils.toJson(soj);

        mockMvc.perform(post("/subject/setSubjectOrganJoins")
                .content(jsonSoj)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isOk());

        jsonSoj = mockMvc.
                perform(get("/subject/getSubjectOrganJoins")
                        .param("nID_SubjectOrgan", "5"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andReturn()
                .getResponse()
                .getContentAsString();

        List<SubjectOrganJoin> sojs = asList(JsonRestUtils.readObject(jsonSoj, SubjectOrganJoin[].class));
        assertTrue("Only one element should be available", sojs.size() == 1);

        SubjectOrganJoin persistedSoj = sojs.iterator().next();

        assertEquals("ID's aren't match", soj.getId(), persistedSoj.getId());
        assertEquals("Public ID's aren't match", soj.getPublicId(), persistedSoj.getPublicId());
        assertEquals("Longitude aren't match", soj.getGeoLongitude(), persistedSoj.getGeoLongitude());
        assertEquals("Latitude aren't match", soj.getGeoLatitude(), persistedSoj.getGeoLatitude());
    }
    @Ignore
    @Test
    public void testSetDocumentAccessLinkSaveSubjectContact() throws Exception
    {
       //issue-1054 если нет контактов у nID_Subject
      final String url = "/document/access/setDocumentLink";
      String phone = "33333890";
      String mail = "test25@igov.org.ua";
      
      Subject subject = subjectDao.getSubject(25L);
      List<SubjectContact> subjectContacts = subjectContactDao.findContacts(subject);
      Assert.assertTrue(subjectContacts.size() == 0);
      
      String result = mockMvc.perform(get(url)
               .param("nID_Document", "7")
               .param("sFIO", "Тест Тестовый Тестович")
               .param("sTelephone", phone )
               .param("nMS", "111")
               .param("sMail", mail)
               .param("nID_Subject", "25"))
               .andExpect(status().isOk()).andReturn().getResponse().getContentAsString();
      
      subjectContacts = subjectContactDao.findContacts(subject);
      Assert.assertTrue(subjectContacts.size() == 2);
      boolean bPhone = false;
      boolean bMail = false;
      for(SubjectContact sc : subjectContacts)
      {
         if(sc.getsValue().equals(phone))
         {
             bPhone = true;
         }
         if(sc.getsValue().equals(mail))
         {
             bMail = true;
         }
      }
      Assert.assertTrue(bPhone);
      Assert.assertTrue(bMail);
      
      SubjectHuman subjectHuman = subjectHumanDao.findByExpected("oSubject", subject);
      SubjectContact phoneDefault = subjectHuman.getDefaultPhone();
      SubjectContact mailDefault = subjectHuman.getDefaultEmail();
      Assert.assertNotNull(phoneDefault);
      Assert.assertNotNull(mailDefault);
      Assert.assertEquals(phoneDefault.getsValue(), phone);
      Assert.assertEquals(mailDefault.getsValue(), mail);
      
    //issue-1054 если нет заданного контакта
      phone = "33333891";
      mail = "test26@igov.org.ua";
      
      subject = subjectDao.getSubject(25L);
      subjectContacts = subjectContactDao.findContacts(subject);
      Assert.assertTrue(subjectContacts.size() != 0);
      
      result = mockMvc.perform(get(url)
               .param("nID_Document", "7")
               .param("sFIO", "Тест Тестовый Тестович")
               .param("sTelephone", phone )
               .param("nMS", "111")
               .param("sMail", mail)
               .param("nID_Subject", "25"))
               .andExpect(status().isOk()).andReturn().getResponse().getContentAsString();
      
      subjectContacts = subjectContactDao.findContacts(subject);
      bPhone = false;
      bMail = false;
      for(SubjectContact sc : subjectContacts)
      {
         if(sc.getsValue().equals(phone))
         {
             bPhone = true;
         }
         if(sc.getsValue().equals(mail))
         {
             bMail = true;
         }
      }
      Assert.assertTrue(bPhone);
      Assert.assertTrue(bMail);
      subjectHuman = subjectHumanDao.findByExpected("oSubject", subject);
      phoneDefault = subjectHuman.getDefaultPhone();
      mailDefault = subjectHuman.getDefaultEmail();
      Assert.assertNotNull(phoneDefault);
      Assert.assertNotNull(mailDefault);
      Assert.assertEquals(phoneDefault.getsValue(), phone);
      Assert.assertEquals(mailDefault.getsValue(), mail);
      
     //issue-1054 если контакты есть
     
      subject = subjectDao.getSubject(25L);
      subjectContacts = subjectContactDao.findContacts(subject);
      Assert.assertTrue(subjectContacts.size() != 0);
      
      SubjectContact oPhone = null;
      SubjectContact oMail = null;
      for(SubjectContact sc : subjectContacts)
      {
         if(sc.getsValue().equals(phone))
         {
             oPhone = sc;
         }
         if(sc.getsValue().equals(mail))
         {
             oMail = sc;
         }
      }
      Assert.assertNotNull(oPhone);
      Assert.assertNotNull(oMail);
      
      
      result = mockMvc.perform(get(url)
               .param("nID_Document", "7")
               .param("sFIO", "Тест Тестовый Тестович")
               .param("sTelephone", phone )
               .param("nMS", "111")
               .param("sMail", mail)
               .param("nID_Subject", "25"))
               .andExpect(status().isOk()).andReturn().getResponse().getContentAsString();
      
      subjectContacts = subjectContactDao.findContacts(subject);
      
      subjectContacts = subjectContactDao.findContacts(subject);
      Assert.assertTrue(subjectContacts.size() != 0);
      
      SubjectContact oPhone1 = null;
      SubjectContact oMail1 = null;
      for(SubjectContact sc : subjectContacts)
      {
         if(sc.getsValue().equals(phone))
         {
             oPhone1 = sc;
         }
         if(sc.getsValue().equals(mail))
         {
             oMail1 = sc;
         }
      }
      
      Assert.assertNotEquals(oMail.getsDate(), oMail1.getsDate());
      Assert.assertNotEquals(oPhone.getsDate(), oPhone1.getsDate());
      
     
    }
     
    
    
}
