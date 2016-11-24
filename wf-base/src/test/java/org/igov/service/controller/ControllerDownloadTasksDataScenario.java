package org.igov.service.controller;

import org.activiti.engine.FormService;
import org.activiti.engine.HistoryService;
import org.activiti.engine.TaskService;
import org.activiti.engine.form.FormProperty;
import org.activiti.engine.form.TaskFormData;
import org.activiti.engine.impl.form.StringFormType;
import org.activiti.engine.task.DelegationState;
import org.activiti.engine.task.Task;
import org.activiti.engine.task.TaskQuery;
import org.activiti.engine.history.HistoricTaskInstanceQuery;
import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.util.Arrays;
import java.util.Date;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.util.AssertionErrors.assertEquals;

/**
 * Created by Taky on 7/14/15.
 */
@WebAppConfiguration
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = IntegrationTestsApplicationConfiguration.class)
public class ControllerDownloadTasksDataScenario {

    @Autowired
    private WebApplicationContext webApplicationContext;

    private MockMvc mockMvc;

    @Autowired
    private TaskService taskService;
    @Autowired
    private FormService formService;

    @Autowired
    private HistoryService historyService;

        @Before
        public void setUp() {
            mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
        }

        @Before
        public void initTaskServiceMock() {
            Task mockedTask = mock(Task.class);
            when(mockedTask.getId()).thenReturn("42");
            when(mockedTask.getCreateTime()).thenReturn(DateTime.now().withYear(2015).withMonthOfYear(1).withDayOfMonth(21)
                    .withHourOfDay(9).withMinuteOfHour(45).withSecondOfMinute(33).withMillisOfSecond(0).toDate());

            TaskQuery taskQuery = mock(TaskQuery.class);
            HistoricTaskInstanceQuery instanceQuery = mock(HistoricTaskInstanceQuery.class);
        	when(taskQuery.taskCreatedAfter((Date) Mockito.anyObject())).thenReturn(taskQuery);
        	when(taskQuery.taskCreatedBefore((Date) Mockito.anyObject())).thenReturn(taskQuery);
            when(taskQuery.processDefinitionKey(Mockito.anyString())).thenReturn(taskQuery);
            when(taskQuery.taskDelegationState(DelegationState.PENDING)).thenReturn(taskQuery);
        	when(taskQuery.listPage(Mockito.anyInt(), Mockito.anyInt())).thenReturn(Arrays.asList(mockedTask));
            when(instanceQuery.processDefinitionKey(Mockito.anyString())).thenReturn(instanceQuery);
            when(instanceQuery.taskCreatedBefore((Date) Mockito.anyObject())).thenReturn(instanceQuery);
            when(instanceQuery.taskCreatedAfter((Date) Mockito.anyObject())).thenReturn(instanceQuery);

        	when(taskService.createTaskQuery()).thenReturn(taskQuery);
            when(historyService.createHistoricTaskInstanceQuery()).thenReturn(instanceQuery);


            StringFormType stringFormType = mock(StringFormType.class);
            when(stringFormType.getName()).thenReturn("string");

            FormProperty formProperty = mock(FormProperty.class);
            when(formProperty.getId()).thenReturn("bankIdlastName");
            when(formProperty.getType()).thenReturn(stringFormType);
            when(formProperty.getValue()).thenReturn("LastName");

            TaskFormData data = mock(TaskFormData.class);
            when(data.getFormProperties()).thenReturn(Arrays.asList(formProperty));

            when(formService.getTaskFormData("42")).thenReturn(data);
        }

    @Test
    public void shouldSuccessfullyReturnFileResponse() throws Exception {
            	MvcResult result = mockMvc.perform(MockMvcRequestBuilders.get("/action/task/downloadTasksData").
                        accept(MediaType.APPLICATION_JSON).
                        param("sID_BP", "dnepr_cnap_225").
                        param("sDateAt", "2016-03-01").
                        param("sDateTo", "2016-04-01").
                        param("saFields", "${nID_Task}").
                        param("nASCI_Spliter", "18").
                        param("sID_Codepage", "win1251").
                        param("sDateCreateFormat", "dd.mm.yyyy%20hh:MM:ss").
                        param("sFileName", "dohody.dat").
                        param("sMailTo", "pekach.d@gmail.com").
                        header("Authorization", "Basic YWN0aXZpdGktbWFzdGVyOlVqaHRKbkV2ZiE=")).andReturn();

            	assertEquals("expected no error", 200, result.getResponse().getStatus());
            	assertEquals("expected cp1251 encoding", "text/csv;charset=windows-1251", result.getResponse().getContentType());
    }
    //
    //    @Test
    //    public void shouldReturnError() throws Exception {
    //    	mockMvc.perform(get("/action/task/downloadTasksData").
    //                accept(MediaType.APPLICATION_JSON).
    //                param("sID_BP", "").
    //                param("saFields", "nID_Task;sDateCreate;1").
    //                param("nASCI_Spliter", "18").
    //                param("sID_Codepage", "win1251").
    //                header("Authorization", "Basic YWN0aXZpdGktbWFzdGVyOlVqaHRKbkV2ZiE=")).
    //                andExpect(status().isInternalServerError());
    //
    //    }
    //
    //    @Test
    //    public void testParseEnum(){
    //        assertEquals("expect", ActivitiRestApiController.parseEnumValue("expect"));
    //        assertEquals("expect", ActivitiRestApiController.parseEnumValue("test;expect"));
    //        assertEquals("expect", ActivitiRestApiController.parseEnumValue("expect;"));
    //        assertEquals("expect", ActivitiRestApiController.parseEnumValue("another;expect;"));
    //        assertEquals("", ActivitiRestApiController.parseEnumValue(""));
    //        assertEquals("", ActivitiRestApiController.parseEnumValue(null));
    //    }

}