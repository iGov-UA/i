package org.igov.service.business.action.task.listener.doc;

import java.io.IOException;
import java.io.InputStream;
//import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.TaskService;
import org.activiti.engine.delegate.DelegateTask;
import org.activiti.engine.delegate.Expression;
import org.activiti.engine.delegate.TaskListener;
import org.apache.commons.io.IOUtils;

import static org.igov.service.business.action.task.core.AbstractModelTask.getStringFromFieldExpression;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.JSONArray;
import org.json.simple.parser.ParseException;

import org.igov.model.process.ProcessSubjectDao;
import org.igov.model.process.ProcessSubject;
import org.igov.model.process.ProcessSubjectStatus;
import org.igov.model.process.ProcessSubjectTree;
import org.igov.model.process.ProcessSubjectTreeDao;

import org.apache.commons.io.IOUtils;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormatter;

/**
 *
 * @author Kovilin
 */
@Component("SetTasks")
public class SetTasks_Listener implements TaskListener {
    
    private final static org.slf4j.Logger LOG = LoggerFactory.getLogger(SetTasks_Listener.class);
     
    private Expression sTaskProcessDefinition;
            
    private Expression sID_Attachment;
            
    private Expression sContent;
            
    private Expression sAutorResolution;
    
    private Expression sTextResolution;
    
    private Expression sDateExecution;
     
    @Autowired
    private TaskService taskService;
    
    @Autowired
    private ProcessSubjectDao processSubject;

    @Autowired
    private ProcessSubjectTreeDao processSubjectTree;
    
    @Autowired
    protected RuntimeService runtimeService;
    
    //@Autowired
    private ProcessSubjectStatus processSubjectStatus;
    
    @Override
    public void notify(DelegateTask delegateTask) {

        Map<String, Object> resultJsonMap = new HashMap<>();

        try{
            String sTaskProcessDefinition_Value = 
                getStringFromFieldExpression(this.sTaskProcessDefinition, delegateTask.getExecution());
            String sID_Attachment_Value = 
                getStringFromFieldExpression(this.sID_Attachment, delegateTask.getExecution());
            String sContent_Value = 
                getStringFromFieldExpression(this.sContent, delegateTask.getExecution());
            String sAutorResolution_Value = 
                getStringFromFieldExpression(this.sAutorResolution, delegateTask.getExecution());
            String sTextResolution_Value = 
                getStringFromFieldExpression(this.sTextResolution, delegateTask.getExecution());
            String sDateExecution_Value = 
                getStringFromFieldExpression(this.sDateExecution, delegateTask.getExecution());
 
            LOG.info("SetTasks listener data: sTaskProcessDefinition_Value: " 
                + sTaskProcessDefinition_Value + " sID_Attachment_Value: " + sID_Attachment_Value + " sContent: " +
                sContent_Value + " sAutorResolution: " + sAutorResolution_Value + " sTextResolution: " 
                + sTextResolution_Value + " sDateExecution: " + sDateExecution_Value ); 
                
            InputStream attachmentContent = taskService.getAttachmentContent(sID_Attachment_Value);
            
            
            JSONParser parser = new JSONParser();
            JSONObject oJSONObject = (JSONObject) parser.parse(IOUtils.toString(attachmentContent, "UTF-8"));   // (JSONObject) new JSONParser().parse(IOUtils.toString(attachmentContent));
            LOG.info("JSON String: " + oJSONObject.toJSONString());
                
            LOG.info("JSON objectType is: " +  oJSONObject.get("aRow").getClass());
            
            JSONArray aJsonRow = (JSONArray) oJSONObject.get("aRow");
            
            ProcessSubject oProcessSubject = new ProcessSubject();
            //processSubjectStatus.setId(1L);
            //oProcessSubject.setProcessSubjectStatus(processSubjectStatus);
            //oProcessSubject.setSnID_Process_Activiti(delegateTask.getExecution().getId());
            //oProcessSubject.setnOrder(0L);
            //oProcessSubject.setsDateEdit(new DateTime());
            //oProcessSubject.setsDatePlan(DateTime.parse(sDateExecution_Value));
            oProcessSubject.setsLogin(sAutorResolution_Value);
            
            //LOG.info("processSubjectStatus: " + oProcessSubject.getProcessSubjectStatus().getName());
            //LOG.info("ID_Process_Activiti: " + oProcessSubject.getSnID_Process_Activiti());
            //LOG.info("Order: " + oProcessSubject);
            //LOG.info("DateEdit: " + oProcessSubject.getsDateEdit().toString());
            //LOG.info("DatePlan: " + oProcessSubject.getsDatePlan().toString());
            LOG.info("Login: " + oProcessSubject.getsLogin());
            
            
            if (aJsonRow != null){
                for (int i = 0; i < aJsonRow.size(); i++){
                    LOG.info("json array element" + i + " is " + aJsonRow.get(i).toString());
                            
                    JSONObject sJsonField =  (JSONObject) aJsonRow.get(i);
                    JSONArray aJsonField = (JSONArray) sJsonField.get("aField");
                            
                    for (int j = 0; j < aJsonField.size(); j++){
                        JSONObject sJsonElem =  (JSONObject) aJsonField.get(j);
                        
                        String id =  sJsonElem.get("id").toString();
                        String value =  sJsonElem.get("value").toString();
                        resultJsonMap.put(id, value);
                        LOG.info("json array id " + id + " and value " + value);
                    }
                }
                        
                resultJsonMap.put("sTaskProcessDefinition", sTaskProcessDefinition_Value);
                resultJsonMap.put("sID_Attachment", sID_Attachment_Value);
                resultJsonMap.put("sContent", sContent_Value);
                resultJsonMap.put("sAutorResolution", sAutorResolution_Value);
                resultJsonMap.put("sDateExecution", sDateExecution_Value);
                resultJsonMap.put("sTextResolution", sTextResolution_Value);
                        
                for (String key : resultJsonMap.keySet())
                {
                    LOG.info("resultJsonMap: " + key + " : " + resultJsonMap.get(key));
                }
            }
            else{
                LOG.info("JSONArray is null");
            }
        }
        catch (Exception e){
             LOG.error("SetTasks listener throws an error: " + e.toString());
        }
        
        
        //ProcessSubjectTree oProcessSubjectTree = new ProcessSubjectTree();
        //oProcessSubject.
        //processSubject.saveOrUpdate(oProcessSubject);
        
        //processSubjectTree.saveOrUpdate(oProcessSubjectTree);
        //runtimeService.startProcessInstanceByKey("system_task", resultJsonMap);
    }
}
