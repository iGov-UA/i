package org.igov.service.business.action.task.listener.doc;

import org.activiti.engine.FormService;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.TaskService;
import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.delegate.DelegateTask;
import org.activiti.engine.delegate.Expression;
import org.activiti.engine.delegate.TaskListener;
import org.igov.io.GeneralConfig;
import org.igov.io.db.kv.statical.IBytesDataStorage;
import org.igov.service.business.action.task.core.AbstractModelTask;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component("SendDocument_SWinEd")
public class SendDocument_SWinEd extends AbstractModelTask implements TaskListener {

    private static final long serialVersionUID = 1L;
    private final static String SWIN_ED_ANSWER_STATUS_VARIABLE = "sAnswer_SWinEd_Doc";
    private final static String SWIN_ED_ERROR_VARIABLE = "nAnswerError_SWinEd_Doc";

    private final static Logger LOG = LoggerFactory.getLogger(SendDocument_SWinEd.class);

    @Autowired
    RuntimeService runtimeService;

    @Autowired
    TaskService taskService;

    @Autowired
    GeneralConfig generalConfig;

    @Autowired
    FormService formService;
    
    //@Autowired
    private IBytesDataStorage oBytesDataStorage;

    private Expression sID_File_XML_SWinEd;

    @Override
    public void notify(DelegateTask delegateTask) {

        //достать по ид атача ид в монге и достать контент из монги.
        DelegateExecution execution = delegateTask.getExecution();
        String sID_File_XML_SWinEdValue = getStringFromFieldExpression(this.sID_File_XML_SWinEd, execution);
        try{
            byte[] oFile_XML_SWinEd = oBytesDataStorage.getData(sID_File_XML_SWinEdValue);
            
            //поместить тело в хмл и отправить рест запрос
            //сохранение результата в поле процесса
        }catch(Exception ex){
            LOG.error("!!! Error/ Cfn't get attach from DataStorage with sID_File_XML_SWinEdValue=" + sID_File_XML_SWinEdValue, ex);
        }
        //отправить рест с контентом файла, вычитать ответ и поместить результат в поле таски
        /*LOG.info("Setting SwinEd status response variable to {} for the process {}", handler.value.getValue(), delegateTask.getProcessInstanceId());
        runtimeService.setVariable(delegateTask.getProcessInstanceId(), SWIN_ED_ANSWER_STATUS_VARIABLE, handler.value.getValue());
        LOG.info("Setting SwinEd error code response variable to {} for the process {}", errorDocIdx.value, delegateTask.getProcessInstanceId());
        runtimeService.setVariable(delegateTask.getProcessInstanceId(), SWIN_ED_ERROR_VARIABLE, errorDocIdx.value);

        LOG.info("Looking for a new task to set form properties");
        List<Task> tasks = taskService.createTaskQuery().processInstanceId(execution.getId()).active().list();
        LOG.info("Get {} active tasks for the process", tasks);
        for (Task task : tasks) {
            TaskFormData formData = formService.getTaskFormData(task.getId());
            for (FormProperty formProperty : formData.getFormProperties()) {
                if (formProperty.getId().equals(SWIN_ED_ANSWER_STATUS_VARIABLE)) {
                    LOG.info("Found form property with the id " + SWIN_ED_ANSWER_STATUS_VARIABLE + ". Setting value {}", handler.value.getValue());
                    if (formProperty instanceof FormPropertyImpl) {
                        ((FormPropertyImpl) formProperty).setValue(handler.value.getValue());
                    }
                }
                if (formProperty.getId().equals(SWIN_ED_ERROR_VARIABLE)) {
                    LOG.info("Found form property with the id " + SWIN_ED_ERROR_VARIABLE + ". Setting value {}", errorDocIdx.value);
                    if (formProperty instanceof FormPropertyImpl) {
                        ((FormPropertyImpl) formProperty).setValue(String.valueOf(errorDocIdx.value));
                    }
                }
            }
        }*/
    }

}
