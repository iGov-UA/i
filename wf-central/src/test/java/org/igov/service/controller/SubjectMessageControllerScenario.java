package org.igov.service.controller;

import java.util.ArrayList;
import java.util.List;

import org.activiti.engine.impl.util.json.JSONObject;
import org.apache.commons.lang.RandomStringUtils;
import org.igov.io.GeneralConfig;
import org.igov.model.action.event.HistoryEvent_Service;
import org.igov.model.action.event.HistoryEvent_ServiceDao;
import org.igov.model.subject.Subject;
import org.igov.model.subject.SubjectContact;
import org.igov.model.subject.SubjectContactDao;
import org.igov.model.subject.SubjectDao;
import org.igov.model.subject.SubjectHuman;
import org.igov.model.subject.SubjectHumanDao;
import org.igov.model.subject.message.SubjectMessageFeedback;
import org.igov.model.subject.message.SubjectMessageType;
import org.igov.service.business.subject.SubjectMessageService;
import org.junit.Before;
import org.junit.FixMethodOrder;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
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

import static org.igov.service.business.subject.SubjectMessageService.sMessageHead;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebAppConfiguration
@RunWith(SpringJUnit4ClassRunner.class)
@ActiveProfiles("default")
@ContextConfiguration(classes = IntegrationTestsApplicationConfiguration.class)
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class SubjectMessageControllerScenario {

	public static final String GET_MESSAGE = "/subject/message/getMessage";
	public static final String SET_MESSAGE = "/subject/message/setMessage";
	public static final String SET_FEEDBACK_MESSAGE = "/subject/message/setMessageFeedbackExtended";
	public static final String GET_FEEDBACK_EXTERNAL = "/subject/message/getFeedbackExternal";

	@Autowired
	private WebApplicationContext webApplicationContext;
	private MockMvc mockMvc;
	@Autowired
	SubjectContactDao subjectContactDao;
	@Autowired
	SubjectHumanDao subjectHumanDao;
	@Autowired
	SubjectDao subjectDao;
	@Autowired
	GeneralConfig generalConfig;

	@InjectMocks
	@Autowired
	private SubjectMessageController subjectMessageController;
	@Mock
	private HistoryEvent_ServiceDao historyEventServiceDao;
	@Mock
	private HistoryEvent_Service oHistoryEvent_Service;
	@Mock
	private SubjectMessageService oSubjectMessageService;
	private SubjectMessage subjectMessage;

	private String sHead = "expect";
	private String sBody = "XXX";
	private Long nID_Subject = 1L;
	private String sMail = "ukr.net";
	private String sContacts = "093";
	private String sData = "some data";
	private Long nID_SubjectMessageType = SubjectMessageType.DEFAULT.getId();

	@Before
	public void setUp() {
		mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
		MockitoAnnotations.initMocks(this);
		subjectMessage = new SubjectMessage();
	}

	@Test
	public void firstShouldSuccessfullySetAndGetMassage() throws Exception {
		subjectMessage.setHead(sHead);
		subjectMessage.setBody(sBody);
		subjectMessage.setId_subject(nID_Subject);
		subjectMessage.setMail(sMail);
		subjectMessage.setContacts(sContacts);
		subjectMessage.setData(sData);
		subjectMessage.setDate(new DateTime());
		subjectMessage.setSubjectMessageType(SubjectMessageType.DEFAULT);
		when(oSubjectMessageService.createSubjectMessage(sHead, sBody, nID_Subject, sMail, sContacts, sData,
				nID_SubjectMessageType)).thenReturn(subjectMessage);

		String jsonAfterSave = mockMvc
				.perform(post(SET_MESSAGE).contentType(MediaType.APPLICATION_JSON).param("sHead", sHead)
						.param("sBody", sBody).param("nID_Subject", String.valueOf(nID_Subject)).param("sMail", sMail)
						.param("sContacts", sContacts).param("sData", sData)
						.param("nID_SubjectMessageType", String.valueOf(nID_SubjectMessageType)))
				.andExpect(status().isOk()).andReturn().getResponse().getContentAsString();

		SubjectMessage savedMessage = JsonRestUtils.readObject(jsonAfterSave, SubjectMessage.class);
		assertNotNull(savedMessage.getId());
		assertNotNull(savedMessage.getSubjectMessageType());
		assertEquals(subjectMessage.getSubjectMessageType().getId().longValue(),
				savedMessage.getSubjectMessageType().getId().longValue());
		assertEquals(subjectMessage.getBody(), savedMessage.getBody());
		assertEquals(subjectMessage.getId_subject().longValue(), savedMessage.getId_subject().longValue());

		String jsonAfterGet = mockMvc.perform(get(GET_MESSAGE).param("nID", "" + savedMessage.getId()))
				.andExpect(status().isOk()).andReturn().getResponse().getContentAsString();
		assertEquals(jsonAfterSave, jsonAfterGet);
	}

	@Ignore
	@Test
	// null not allowed for column nid_subject
	public void nextShouldSuccessfullySetMassageWithDefaultSubjectID() throws Exception {
		subjectMessage.setHead(sHead);
		subjectMessage.setBody(sBody);
		subjectMessage.setMail(sMail);
		subjectMessage.setDate(new DateTime());
		when(oSubjectMessageService.createSubjectMessage(sHead, sBody, null, sMail, null, null, null))
				.thenReturn(subjectMessage);

		mockMvc.perform(post(SET_MESSAGE).contentType(MediaType.APPLICATION_JSON).param("sHead", sHead)
				.param("sBody", sBody).param("sMail", sMail)).andExpect(status().isOk());
	}

	@Test
	public void shouldFailedNoObligatedParam() throws Exception {
		subjectMessage.setBody(sBody);
		subjectMessage.setId_subject(nID_Subject);
		subjectMessage.setMail(sMail);
		subjectMessage.setContacts(sContacts);
		subjectMessage.setData(sData);
		subjectMessage.setDate(new DateTime());
		subjectMessage.setSubjectMessageType(SubjectMessageType.DEFAULT);
		when(oSubjectMessageService.createSubjectMessage(null, sBody, nID_Subject, sMail, sContacts, sData,
				nID_SubjectMessageType)).thenReturn(subjectMessage);

		mockMvc.perform(
				post(SET_MESSAGE).contentType(MediaType.APPLICATION_JSON).param("sBody", sBody).param("sMail", sMail))
				.andExpect(status().isBadRequest());
	}

	@Test
	public void testSetMessage_nIDSubject_sMailNull() throws Exception {
		subjectMessage.setHead(sHead);
		subjectMessage.setBody(sBody);
		subjectMessage.setId_subject(nID_Subject);
		subjectMessage.setDate(new DateTime());
		when(oSubjectMessageService.createSubjectMessage(sHead, sBody, nID_Subject, null, null, null, null))
				.thenReturn(subjectMessage);

		String jsonAfterSave = mockMvc
				.perform(post(SET_MESSAGE).contentType(MediaType.APPLICATION_JSON).param("sHead", sHead)
						.param("sBody", sBody).param("nID_Subject", String.valueOf(nID_Subject)))
				.andExpect(status().isOk()).andReturn().getResponse().getContentAsString();
	}

	@Test
	public void testSetMessage_nIDSubject_sMailEmpty() throws Exception {
		subjectMessage.setHead(sHead);
		subjectMessage.setBody(sBody);
		subjectMessage.setId_subject(nID_Subject);
		subjectMessage.setDate(new DateTime());
		String sEmptyMail = "";
		subjectMessage.setMail(sEmptyMail);
		when(oSubjectMessageService.createSubjectMessage(sHead, sBody, nID_Subject, sEmptyMail, null, null, null))
				.thenReturn(subjectMessage);

		String jsonAfterSave = mockMvc.perform(
				post(SET_MESSAGE).contentType(MediaType.APPLICATION_JSON).param("sHead", sHead).param("sBody", sBody)
						.param("nID_Subject", String.valueOf(nID_Subject)).param("sMail", sEmptyMail))
				.andExpect(status().isOk()).andReturn().getResponse().getContentAsString();
	}

	@Ignore
	@Test
	// null not allowed for column nid_subject
	public void testSetMessage_nIDSubject() throws Exception {
		String messageBody = "XXX";
		String messageHead = "expect";
		String jsonAfterSave = mockMvc
				.perform(post(SET_MESSAGE).contentType(MediaType.APPLICATION_JSON).param("sHead", messageHead)
						.param("sBody", messageBody).param("nID_Subject", "22").param("sMail", "test@igov.org.ua"))
				.andExpect(status().isOk()).andReturn().getResponse().getContentAsString();
	}

	@Ignore
	@Test
	// null not allowed for column nid_subject
	public void testSetMessageMailEmpty_nIDSubjectNull() throws Exception {
		subjectMessage.setHead(sHead);
		subjectMessage.setBody(sBody);
		String sEmptyMail = "";
		subjectMessage.setMail(sEmptyMail);
		subjectMessage.setDate(new DateTime());
		when(oSubjectMessageService.createSubjectMessage(sHead, sBody, null, sEmptyMail, null, null, null))
				.thenReturn(subjectMessage);

		String jsonAfterSave = mockMvc
				.perform(post(SET_MESSAGE).contentType(MediaType.APPLICATION_JSON).param("sHead", sHead)
						.param("sBody", sBody).param("sMail", sEmptyMail))
				.andExpect(status().isOk()).andReturn().getResponse().getContentAsString();

	}

	@Ignore
	@Test
	public void testSetMessageWithSubject() throws Exception {
		// issue-1053
		// пункт 3.2 (при наличии nID_Subject: в случае отсутствия контактов
		// подвязываем SubjectContact и
		// делаем его дефолтным в структуре SubjectHuman)
		Subject subject = subjectDao.getSubject(25L);
		List<SubjectContact> subjectContacts = subjectContactDao.findContacts(subject);
		Assert.assertTrue(subjectContacts.size() == 0);
		String messageBody = "XXX";
		String messageHead = "expect";
		String jsonAfterSave = mockMvc
				.perform(post(SET_MESSAGE).contentType(MediaType.APPLICATION_JSON).param("sHead", messageHead)
						.param("sBody", messageBody).param("nID_Subject", "25").param("sMail", "test24@igov.org.ua"))
				.andExpect(status().isOk()).andReturn().getResponse().getContentAsString();

		SubjectMessage savedMessage = JsonRestUtils.readObject(jsonAfterSave, SubjectMessage.class);
		SubjectContact subjectContact = savedMessage.getoMail();
		SubjectHuman subjectHuman = subjectHumanDao.findByExpected("oSubject", subject);
		SubjectContact subjectContact_Default = subjectHuman.getDefaultEmail();
		Assert.assertNotNull(subjectContact);
		Assert.assertNotNull(subjectContact_Default);
		Assert.assertEquals(subjectContact.getsValue(), subjectContact_Default.getsValue());

		// пункт 3.2 при наличии nID_Subject и контактов у субъекта, но
		// отсутствии заданного контакта
		// добавляем контакт в SubjectContact и делаем его дефолтным в
		// SubjectHuman

		subject = subjectDao.getSubject(24L);
		subjectContacts = subjectContactDao.findContacts(subject);

		Assert.assertTrue(subjectContacts.size() != 0);
		boolean no_contact = true;
		for (SubjectContact sc : subjectContacts) {
			if (sc.getsValue().equals("test25@igov.org.ua"))
				no_contact = false;
		}
		Assert.assertTrue(no_contact);

		jsonAfterSave = mockMvc
				.perform(post(SET_MESSAGE).contentType(MediaType.APPLICATION_JSON).param("sHead", messageHead)
						.param("sBody", messageBody).param("nID_Subject", "24").param("sMail", "test25@igov.org.ua"))
				.andExpect(status().isOk()).andReturn().getResponse().getContentAsString();

		savedMessage = JsonRestUtils.readObject(jsonAfterSave, SubjectMessage.class);
		subjectContact = savedMessage.getoMail();
		subjectHuman = subjectHumanDao.findByExpected("oSubject", subject);
		subjectContact_Default = subjectHuman.getDefaultEmail();
		subjectContacts = subjectContactDao.findContacts(subject);
		for (SubjectContact sc : subjectContacts) {
			if (sc.getsValue().equals("test25@igov.org.ua"))
				no_contact = false;

		}
		Assert.assertNotNull(subjectContact);
		Assert.assertNotNull(subjectContact_Default);
		Assert.assertEquals(subjectContact.getsValue(), subjectContact_Default.getsValue());
		Assert.assertFalse(no_contact);

		// все контакты есть — обновляем sDate SubjectContact на текущее
		// значение

		subject = subjectDao.getSubject(24L);
		subjectContacts = subjectContactDao.findContacts(subject);
		Assert.assertTrue(subjectContacts.size() != 0);
		no_contact = true;
		DateTime date = null;
		for (SubjectContact sc : subjectContacts) {
			if (sc.getsValue().equals("test25@igov.org.ua")) {
				no_contact = false;
				date = sc.getsDate();
			}
		}
		Assert.assertFalse(no_contact);

		jsonAfterSave = mockMvc
				.perform(post(SET_MESSAGE).contentType(MediaType.APPLICATION_JSON).param("sHead", messageHead)
						.param("sBody", messageBody).param("nID_Subject", "24").param("sMail", "test25@igov.org.ua"))
				.andExpect(status().isOk()).andReturn().getResponse().getContentAsString();

		savedMessage = JsonRestUtils.readObject(jsonAfterSave, SubjectMessage.class);
		subjectContact = savedMessage.getoMail();
		DateTime date1 = null;
		subjectContacts = subjectContactDao.findContacts(subject);
		Assert.assertTrue(subjectContacts.size() != 0);
		no_contact = true;
		for (SubjectContact sc : subjectContacts) {
			if (sc.getsValue().equals("test25@igov.org.ua")) {
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
	public void testSetMessageWithout_nIDSubject() throws Exception {
		// issue-1053
		// если есть такой дефолтный контакт в SubjectHuman обновляем
		// nID_Subject в SubjectMessage и в SubjectContact
		// к пункту 3.1

		String messageBody = "XXX";
		String messageHead = "expect";
		String mail = "test@igov.org.ua";
		String jsonAfterSave = mockMvc
				.perform(post(SET_MESSAGE).contentType(MediaType.APPLICATION_JSON).param("sHead", messageHead)
						.param("sBody", messageBody).param("sMail", mail))
				.andExpect(status().isOk()).andReturn().getResponse().getContentAsString();

		SubjectMessage savedMessage = JsonRestUtils.readObject(jsonAfterSave, SubjectMessage.class);
		SubjectContact subjectContact = savedMessage.getoMail();

		Assert.assertNotNull(savedMessage.getId_subject());
		Assert.assertNotNull(subjectContact.getSubject());
		Assert.assertEquals(savedMessage.getId_subject(), subjectContact.getSubject().getId());

	}

	@Test
	public void testTransferDataFromMail() throws Exception {
		// issue-1053
		// тест переноса данных с поля sMail в поле типа SubjectContact
		String jsonAfterExecute = mockMvc
				.perform(get("/subject/message/transferDataFromMail").contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk()).andReturn().getResponse().getContentAsString();

	}

	@Test
	public void shouldSetMessageFeedback() throws Exception {
		String sValidID_Order = "sValidID_Order";
		String sValidToken = "sValidToken";
		Long nDefaultID_SubjectMessageType = 2l;
		String sValidBody = "sValidBody";

//		String sHead = "expect";
//		String sBody = "XXX";
//		Long nID_Subject = 1L;
//		String sMail = "ukr.net";
//		String sContacts = "093";
//		String sData = "some data";
		SubjectMessage oSubjectMessage_Feedback = subjectMessage;
		oSubjectMessage_Feedback.setHead(sHead);
		oSubjectMessage_Feedback.setBody(sBody);
		oSubjectMessage_Feedback.setId_subject(nID_Subject);
		oSubjectMessage_Feedback.setMail(sMail);
		oSubjectMessage_Feedback.setContacts(sContacts);
		oSubjectMessage_Feedback.setData(sData);
		oSubjectMessage_Feedback.setDate(new DateTime());
		oSubjectMessage_Feedback.setSubjectMessageType(SubjectMessageType.DEFAULT);

		HistoryEvent_Service oHistoryEvent_Service = mock(HistoryEvent_Service.class);

		when(oHistoryEvent_Service.getsToken()).thenReturn(sValidToken);
		when(historyEventServiceDao.getOrgerByID(sValidID_Order)).thenReturn(oHistoryEvent_Service);
		when(oSubjectMessageService.createSubjectMessage(sMessageHead(nDefaultID_SubjectMessageType, sValidID_Order),
				"", oHistoryEvent_Service.getnID_Subject(), "", "", "", nDefaultID_SubjectMessageType))
						.thenReturn(oSubjectMessage_Feedback);

		mockMvc.perform(post(SET_FEEDBACK_MESSAGE).param("sID_Order", sValidID_Order).param("sToken", sValidToken)
				.param("nID_SubjectMessageType", nDefaultID_SubjectMessageType.toString()).param("sBody", sValidBody))
				.andExpect(status().isOk()).andExpect(content().string("Ok"));
	}

	@Test
	public void shouldReturn403IfTokenIsNotCorrect() throws Exception {
		String sValidID_Order = "sValidID_Order";
		String sWrongToken = "sWrongToken";
		String sValidToken = "sValidToken";
		String sEmptyToken = "";
		Long nDefaultID_SubjectMessageType = 2l;
		String sValidBody = "sValidBody";

		JSONObject expectedResponseObject = new JSONObject();
		expectedResponseObject.put("code", ExceptionCommonController.BUSINESS_ERROR_CODE);
		expectedResponseObject.put("message", "Security Error");

		when(oHistoryEvent_Service.getsToken()).thenReturn(sWrongToken);
		when(historyEventServiceDao.getOrgerByID(sValidID_Order)).thenReturn(oHistoryEvent_Service);

		mockMvc.perform(post(SET_FEEDBACK_MESSAGE).param("sID_Order", sValidID_Order).param("sToken", sValidToken)
				.param("nID_SubjectMessageType", nDefaultID_SubjectMessageType.toString()).param("sBody", sValidBody))
				.andExpect(status().isForbidden()).andExpect(content().json(expectedResponseObject.toString()));

		mockMvc.perform(post(SET_FEEDBACK_MESSAGE).param("sID_Order", sValidID_Order).param("sToken", sEmptyToken)
				.param("nID_SubjectMessageType", nDefaultID_SubjectMessageType.toString()).param("sBody", sValidBody))
				.andExpect(status().isForbidden()).andExpect(content().json(expectedResponseObject.toString()));
	}

	// TODO
	// @Test
	// public void shouldReturn403IfRecordIsFoundAndFeedbackMessageIsNotEmpty()
	// {
	// }
	//
	// @Test
	// public void
	// shouldReturn404IfRecordIsNotFoundAndFeedbackMessageIsNotEmpty() {
	// }

	@Test
	public void shouldAddFeedbackToServiceAndReturnLink() throws Exception {
		SubjectMessageFeedback feedback = new SubjectMessageFeedback();
		feedback.setId(1L);
		feedback.setsID_Source("-1");
		feedback.setsAuthorFIO("FIO");
		feedback.setsMail("sMail");
		feedback.setsHead("sHead");
		feedback.setsBody("sBody");
		feedback.setsPlace("sPlace");
		feedback.setnID_Rate(-1L);
		feedback.setnID_Service(-1L);
		feedback.setsID_Token(RandomStringUtils.randomAlphanumeric(20));

		String responseMessage = String.format("%s/service/%d/feedback?nID=%d&sID_Token=%s",
				generalConfig.getSelfHost(), feedback.getnID_Service(), feedback.getId(), feedback.getsID_Token());

		JSONObject expectedResponseObject = new JSONObject();
		expectedResponseObject.put("sURL", responseMessage);

		when(oSubjectMessageService.setSubjectMessageFeedback(feedback.getsID_Source(), feedback.getsAuthorFIO(),
				feedback.getsMail(), feedback.getsHead(), feedback.getsBody(), feedback.getsPlace(),
				feedback.getsEmployeeFIO(), feedback.getnID_Rate(), feedback.getnID_Service(), null, // sAnswer
				null, // nId
				null, null))// nID_Subject
						.thenReturn(feedback);

		mockMvc.perform(post("/subject/message/setFeedbackExternal").contentType(MediaType.APPLICATION_JSON)
				.param("sID_Source", feedback.getsID_Source()).param("sAuthorFIO", feedback.getsAuthorFIO())
				.param("sMail", feedback.getsMail()).param("sHead", feedback.getsHead())
				.param("sBody", feedback.getsBody()).param("sPlace", feedback.getsPlace())
				.param("nID_Rate", feedback.getnID_Rate().toString())
				.param("nID_Service", feedback.getnID_Service().toString())).andExpect(status().isCreated())
				.andExpect(content().json(expectedResponseObject.toString()));
	}

	@Test
	public void shouldReturnSubjectMessageFeedbackByIdAndToken() throws Exception {
		SubjectMessageFeedback feedback = new SubjectMessageFeedback();
		feedback.setId(1L);
		feedback.setsID_Source("-1");
		feedback.setsAuthorFIO("FIO");
		feedback.setsMail("sMail");
		feedback.setsBody("sBody");
		feedback.setnID_Rate(-1L);
		feedback.setnID_Service(-1L);
		feedback.setsID_Token(RandomStringUtils.randomAlphanumeric(20));

		SubjectMessageFeedback feedbackWithNullToken = new SubjectMessageFeedback();
		feedbackWithNullToken.setId(1L);
		feedbackWithNullToken.setsID_Source("-1");
		feedbackWithNullToken.setsAuthorFIO("FIO");
		feedbackWithNullToken.setsMail("sMail");
		feedbackWithNullToken.setsBody("sBody");
		feedbackWithNullToken.setnID_Rate(-1L);
		feedbackWithNullToken.setnID_Service(-1L);

		String expectedResponse = JsonRestUtils.toJson(feedbackWithNullToken);

		when(oSubjectMessageService.getSubjectMessageFeedbackById(feedback.getId())).thenReturn(feedback);

		mockMvc.perform(get(GET_FEEDBACK_EXTERNAL).contentType(MediaType.APPLICATION_JSON)
				.param("nID", feedback.getId().toString()).param("sID_Token", feedback.getsID_Token()))
				.andExpect(status().isOk()).andExpect(content().json(expectedResponse));
	}

	@Test
	public void shouldReturnFilteredListOfSubjectMessageFeedbackBynID_Service() throws Exception {
		SubjectMessageFeedback feedback = new SubjectMessageFeedback();
		feedback.setId(1L);
		feedback.setsID_Source("-1");
		feedback.setsAuthorFIO("FIO");
		feedback.setsMail("sMail");
		feedback.setsBody("sBody");
		feedback.setnID_Rate(-1L);
		feedback.setnID_Service(-1L);
		feedback.setsID_Token(RandomStringUtils.randomAlphanumeric(20));

		SubjectMessageFeedback feedbackWithNullToken = new SubjectMessageFeedback();
		feedbackWithNullToken.setId(1L);
		feedbackWithNullToken.setsID_Source("-1");
		feedbackWithNullToken.setsAuthorFIO("FIO");
		feedbackWithNullToken.setsMail("sMail");
		feedbackWithNullToken.setsBody("sBody");
		feedbackWithNullToken.setnID_Rate(-1L);
		feedbackWithNullToken.setnID_Service(-1L);

		List<SubjectMessageFeedback> expectedFeedbackList = new ArrayList<>();
		expectedFeedbackList.add(feedbackWithNullToken);
		expectedFeedbackList.add(feedbackWithNullToken);

		String response = JsonRestUtils.toJson(expectedFeedbackList);

		Long nID__LessThen_Filter = -1L;
		Integer nRowsMax = -1;

		when(oSubjectMessageService.getAllSubjectMessageFeedback_Filtered(feedback.getnID_Service(),
				nID__LessThen_Filter, nRowsMax)).thenReturn(expectedFeedbackList);

		mockMvc.perform(get("/subject/message/getFeedbackExternal").contentType(MediaType.APPLICATION_JSON)
				/* .param("nID", feedback.getId().toString()) */.param("sID_Token", feedback.getsID_Token())
				.param("nID__LessThen_Filter", String.valueOf(nID__LessThen_Filter))
				.param("nRowsMax", String.valueOf(nRowsMax)).param("nID_Service", feedback.getnID_Service().toString()))
				.andExpect(status().isOk()).andExpect(content().json(response));

		verify(oSubjectMessageService).getAllSubjectMessageFeedback_Filtered(feedback.getnID_Service(),
				nID__LessThen_Filter, nRowsMax);
	}

	// TODO: THIS TEST SHOULD BE INTEGRATIONAL OR REFACTORED
	@Test
	@Ignore
	public void shouldAddAnswerToFeedback() throws Exception {
		SubjectMessageFeedback expectedFeedback = new SubjectMessageFeedback();
		ArrayList<String> expectedFeedbackList = new ArrayList<>();
		expectedFeedbackList.add("feedbackAfterInit");
		String expectedComments = JsonRestUtils.toJson(expectedFeedbackList);

		expectedFeedback.setId(1L);
		expectedFeedback.setsID_Source("-1");
		expectedFeedback.setsAuthorFIO("FIO");
		expectedFeedback.setsMail("sMail");
		expectedFeedback.setsHead("sHead");
		expectedFeedback.setsBody("sBody");
		expectedFeedback.setsPlace("sPlace");
		expectedFeedback.setnID_Rate(-1L);
		expectedFeedback.setnID_Service(-1L);
		expectedFeedback.setsAnswer(expectedComments);

		when(oSubjectMessageService.setSubjectMessageFeedback(expectedFeedback.getsID_Source(),
				expectedFeedback.getsAuthorFIO(), expectedFeedback.getsMail(), expectedFeedback.getsHead(),
				expectedFeedback.getsBody(), expectedFeedback.getsPlace(), expectedFeedback.getsEmployeeFIO(),
				expectedFeedback.getnID_Rate(), expectedFeedback.getnID_Service(), "feedbackAfterInit", null, null,
				null)).thenCallRealMethod();

		mockMvc.perform(post("/subject/message/setFeedbackExternal").contentType(MediaType.APPLICATION_JSON)
				.param("sID_Source", expectedFeedback.getsID_Source())
				.param("sAuthorFIO", expectedFeedback.getsAuthorFIO()).param("sMail", expectedFeedback.getsMail())
				.param("sHead", expectedFeedback.getsHead()).param("sBody", expectedFeedback.getsBody())
				.param("sPlace", expectedFeedback.getsPlace())
				.param("nID_Rate", expectedFeedback.getnID_Rate().toString())
				.param("nID_Service", expectedFeedback.getnID_Service().toString())
				.param("sAnswer", "feedbackAfterInit")).andExpect(status().isCreated());
	}

	// TODO: THIS TEST SHOULD BE INTEGRATIONAL OR REFACTORED
	// this test need prepared DB with SubjectMessageFeedback or DAO mock
	@Ignore
	@Test
	public void shouldUpdateFeedbackIfnIdPresent() throws Exception {
		SubjectMessageFeedback expectedFeedback = new SubjectMessageFeedback();
		List<String> expectedFeedbackList = new ArrayList<>();
		expectedFeedbackList.add("feedbackAfterInit");
		String expectedComments = JsonRestUtils.toJson(expectedFeedbackList);

		expectedFeedback.setId(1L);
		expectedFeedback.setsID_Source("-1");
		expectedFeedback.setsAuthorFIO("FIO");
		expectedFeedback.setsMail("sMail");
		expectedFeedback.setsHead("sHead");
		expectedFeedback.setsBody("sBody");
		expectedFeedback.setsPlace("sPlace");
		expectedFeedback.setnID_Rate(-1L);
		expectedFeedback.setnID_Service(-1L);
		expectedFeedback.setsAnswer(expectedComments);

		when(oSubjectMessageService.setSubjectMessageFeedback(expectedFeedback.getsID_Source(),
				expectedFeedback.getsAuthorFIO(), expectedFeedback.getsMail(), expectedFeedback.getsHead(),
				expectedFeedback.getsBody(), expectedFeedback.getsPlace(), expectedFeedback.getsEmployeeFIO(),
				expectedFeedback.getnID_Rate(), expectedFeedback.getnID_Service(), "feedbackAfterInit",
				expectedFeedback.getId(), null, null)).thenCallRealMethod();

		mockMvc.perform(post("/subject/message/setFeedbackExternal").contentType(MediaType.APPLICATION_JSON)
				.param("sID_Source", expectedFeedback.getsID_Source())
				.param("sAuthorFIO", expectedFeedback.getsAuthorFIO()).param("sMail", expectedFeedback.getsMail())
				.param("sHead", expectedFeedback.getsHead()).param("sBody", expectedFeedback.getsBody())
				.param("sPlace", expectedFeedback.getsPlace())
				.param("nID_Rate", expectedFeedback.getnID_Rate().toString())
				.param("nID_Service", expectedFeedback.getnID_Service().toString())
				.param("sAnswer", "feedbackAfterInit").param("nID", expectedFeedback.getId().toString()))
				.andExpect(status().isCreated());
	}
}
