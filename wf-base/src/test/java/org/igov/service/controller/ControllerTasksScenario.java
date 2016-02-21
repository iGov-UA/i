package org.igov.service.controller;

import org.igov.service.exception.RecordNotFoundException;
import com.google.common.collect.ImmutableMap;
import org.activiti.engine.TaskService;
import org.activiti.engine.history.HistoricTaskInstance;
import org.activiti.engine.impl.persistence.entity.TaskEntity;
import org.activiti.engine.task.Task;
import org.activiti.engine.task.TaskQuery;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.igov.util.JSON.JsonRestUtils;
import org.igov.util.ToolLuna;
import org.igov.service.exception.CRCInvalidException;

import java.util.*;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.core.Is.is;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Created by diver on 4/12/15.
 */
public class ControllerTasksScenario extends ActivitiScenarioBase{

    @Autowired
    private TaskService taskService;

    @Before
    public void initTaskServiceMock() {
        TaskQuery taskQueue = mock(TaskQuery.class);
        List<Task> tasks = new ArrayList<>();
        tasks.add(IntegrationTestsFixtures.getTask());
        when(taskQueue.list()).thenReturn(tasks);
        when(taskQueue.taskAssignee(eq("kermit"))).thenReturn(taskQueue);
        when(taskService.createTaskQuery()).thenReturn(taskQueue);
    }

    @Ignore
    @Test
    public void shouldSuccessfullyReturnTasksJsonResponse() throws Exception {
        mockMvc.perform(get("/action/task/login/kermit").
                accept(MediaType.APPLICATION_JSON).
                header("Authorization", "Basic YWN0aXZpdGktbWFzdGVyOlVqaHRKbkV2ZiE=")).
                andExpect(status().isOk()).
                andExpect(content().contentType("application/json;charset=UTF-8")).
                andExpect(jsonPath("$", hasSize(1))).
                andExpect(jsonPath("$[0].*", hasSize(18))).
                andExpect(jsonPath("$[0].id", is("38"))).
                andExpect(jsonPath("$[0].name", is("Первый процесс пользователя kermit"))).
                andExpect(jsonPath("$[0].description", is("Описание процесса"))).
                andExpect(jsonPath("$[0].priority", is(51))).
                andExpect(jsonPath("$[0].owner", is("kermit-owner"))).
                andExpect(jsonPath("$[0].assignee", is("kermit-assignee"))).
                andExpect(jsonPath("$[0].processInstanceId", is("12"))).
                andExpect(jsonPath("$[0].executionId", is("1"))).
                andExpect(jsonPath("$[0].createTime", is("2015-04-13 00:51:34.527"))).
                andExpect(jsonPath("$[0].taskDefinitionKey", is("task-definition"))).
                andExpect(jsonPath("$[0].delegationState", is("RESOLVED"))).
                andExpect(jsonPath("$[0].dueDate", is("2015-04-13 00:51:36.527"))).
                andExpect(jsonPath("$[0].category", is("my-category"))).
                andExpect(jsonPath("$[0].parentTaskId", is("2"))).
                andExpect(jsonPath("$[0].tenantId", is("diver"))).
                andExpect(jsonPath("$[0].formKey", is("form-key-12"))).
                andExpect(jsonPath("$[0].processDefinitionId", is("21"))).
                andExpect(jsonPath("$[0].suspended", is(true)));
    }

    @Test
    public void shouldReturnJsonErrorMessageOnAnyRuntimeException() throws Exception {
        when(taskService.createTaskQuery()).
                thenThrow(new NullPointerException("Parameter not specified"));
        mockMvc.perform(get("/action/task/login/kermit").
                accept(MediaType.APPLICATION_JSON).
                header("Authorization", "Basic YWN0aXZpdGktbWFzdGVyOlVqaHRKbkV2ZiE=")).
                andExpect(status().isInternalServerError()).
                andExpect(content().contentType("application/json;charset=UTF-8")).
                andExpect(jsonPath("$.*", hasSize(2))).
                andExpect(jsonPath("$.code", is("SYSTEM_ERR"))).
                andExpect(jsonPath("$.message", is("Parameter not specified")));
        reset(taskService);
    }

    @Ignore
    @Test
    public void testGetTaskByOrder() throws Exception {

        final String getTasksByOrderUrl = "/action/task/getTasksByOrder";

        String jsonData = mockMvc.perform(get(getTasksByOrderUrl).
                param("nID_Order", "123123")).
                andExpect(status().isForbidden()).
                andExpect(content().contentType(APPLICATION_JSON_CHARSET_UTF_8)).
                andReturn().getResponse().getContentAsString();
        Map<String, String> res = JsonRestUtils.readObject(jsonData, Map.class);
        Assert.assertEquals(new CRCInvalidException().getMessage(), res.get("message"));

        TaskQuery taskQuery = mock(TaskQuery.class);
        when(taskService.createTaskQuery()).thenReturn(taskQuery);
        when(taskQuery.processInstanceId("12345")).thenReturn(taskQuery);
        when(taskQuery.singleResult()).thenReturn(null);

        jsonData = mockMvc.perform(get(getTasksByOrderUrl).
                param("nID_Order", "123451")).
                andExpect(status().isForbidden()).
                andExpect(content().contentType(APPLICATION_JSON_CHARSET_UTF_8)).
                andReturn().getResponse().getContentAsString();
        res = JsonRestUtils.readObject(jsonData, Map.class);
        Assert.assertEquals(new RecordNotFoundException().getMessage(), res.get("message"));

        HistoricTaskInstance historicTaskInstance = mock(HistoricTaskInstance.class);
        String processTaskId = "777";
        when(historicTaskInstance.getProcessInstanceId()).thenReturn(processTaskId);

        List<String> taskIds = Arrays.asList("1", "2");
        when(taskService.createTaskQuery()).thenReturn(taskQuery);
        when(taskQuery.processInstanceId(processTaskId)).thenReturn(taskQuery);
        List<Task> tasks = new ArrayList<>();
        for (String taskId : taskIds) {
            tasks.add(new TaskEntity(taskId));
        }
        when(taskQuery.list()).thenReturn(tasks);

        jsonData = mockMvc.perform(get(getTasksByOrderUrl).
                param("nID_Order", "123451")).
                andExpect(status().isOk()).
                andExpect(content().contentType(APPLICATION_JSON_CHARSET_UTF_8)).
                andReturn().getResponse().getContentAsString();
        String[] taskIdsResult = JsonRestUtils.readObject(jsonData, String[].class);
        Assert.assertEquals(Arrays.asList(taskIdsResult), taskIds);
    }

}
