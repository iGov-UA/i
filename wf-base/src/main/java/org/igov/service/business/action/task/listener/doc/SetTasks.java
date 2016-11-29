package org.igov.service.business.action.task.listener.doc;

import java.io.InputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
//import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.TaskService;
import org.activiti.engine.delegate.DelegateTask;
import org.activiti.engine.delegate.Expression;
import org.activiti.engine.delegate.TaskListener;
import org.activiti.engine.runtime.ProcessInstance;

import static org.igov.service.business.action.task.core.AbstractModelTask.getStringFromFieldExpression;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.JSONArray;

import org.igov.model.process.ProcessSubjectDao;
import org.igov.model.process.ProcessSubject;
import org.igov.model.process.ProcessSubjectStatus;
import org.igov.model.process.ProcessSubjectTree;
import org.igov.model.process.ProcessSubjectTreeDao;

import org.apache.commons.io.IOUtils;
import org.igov.model.process.ProcessSubjectStatusDao;
import org.joda.time.DateTime;

/**
 *
 * @author Kovilin
 */
@Component("SetTasks")
public class SetTasks implements TaskListener {

    private final static org.slf4j.Logger LOG = LoggerFactory.getLogger(SetTasks.class);

    private Expression sTaskProcessDefinition;

    private Expression sID_Attachment;

    private Expression sContent;

    private Expression sAutorResolution;

    private Expression sTextResolution;

    private Expression sDateExecution;

    @Autowired
    private TaskService taskService;

    @Autowired
    protected RuntimeService runtimeService;
    
    @Autowired
    private ProcessSubjectDao processSubjectDao;

    @Autowired
    private ProcessSubjectTreeDao processSubjectTreeDao;

    @Autowired
    private ProcessSubjectStatusDao processSubjectStatusDao;

    @Override
    public void notify(DelegateTask delegateTask) {

        try {
            String sTaskProcessDefinition_Value
                    = getStringFromFieldExpression(this.sTaskProcessDefinition, delegateTask.getExecution());
            String sID_Attachment_Value
                    = getStringFromFieldExpression(this.sID_Attachment, delegateTask.getExecution());
            String sContent_Value
                    = getStringFromFieldExpression(this.sContent, delegateTask.getExecution());
            String sAutorResolution_Value
                    = getStringFromFieldExpression(this.sAutorResolution, delegateTask.getExecution());
            String sTextResolution_Value
                    = getStringFromFieldExpression(this.sTextResolution, delegateTask.getExecution());
            String sDateExecution_Value
                    = getStringFromFieldExpression(this.sDateExecution, delegateTask.getExecution());

            ProcessSubjectStatus processSubjectStatus = processSubjectStatusDao.findByIdExpected(1L);

            DateFormat df = new SimpleDateFormat("EEE MMM dd HH:mm:ss zzz yyyy");
            ProcessSubject oProcessSubjectParent = processSubjectDao
                    .setProcessSubject(delegateTask.getExecution().getId(), sAutorResolution_Value,
                            new DateTime(df.parse(sDateExecution_Value)), 0L, processSubjectStatus);

            LOG.info("SetTasks listener data: sTaskProcessDefinition_Value: "
                    + sTaskProcessDefinition_Value + " sID_Attachment_Value: " + sID_Attachment_Value + " sContent: "
                    + sContent_Value + " sAutorResolution: " + sAutorResolution_Value + " sTextResolution: "
                    + sTextResolution_Value + " sDateExecution: " + sDateExecution_Value);

            InputStream attachmentContent = taskService.getAttachmentContent(sID_Attachment_Value);

            JSONParser parser = new JSONParser();
            JSONObject oJSONObject = (JSONObject) parser.parse(IOUtils.toString(attachmentContent, "UTF-8"));   // (JSONObject) new JSONParser().parse(IOUtils.toString(attachmentContent));
            LOG.info("JSON String: " + oJSONObject.toJSONString());

            LOG.info("JSON aRow is: " + oJSONObject.get("aRow").getClass());

            JSONArray aJsonRow = (JSONArray) oJSONObject.get("aRow");

            //ProcessSubject oProcessSubject = new ProcessSubject();
            //oProcessSubjectParent.setProcessSubjectStatus(processSubjectStatus);
            //oProcessSubject.setSnID_Process_Activiti(delegateTask.getExecution().getId());
            //oProcessSubject.setnOrder(0L);
            //oProcessSubject.setsDateEdit(new DateTime());
            //DateFormat df = new SimpleDateFormat("EEE MMM dd HH:mm:ss zzz yyyy");
            //oProcessSubject.setsDatePlan(new DateTime(df.parse(sDateExecution_Value)));
            //oProcessSubject.setsLogin(sAutorResolution_Value);
            /*LOG.info("processSubjectStatus: " + ((oProcessSubject.getProcessSubjectStatus().getId().toString() != null)
                    ? oProcessSubject.getProcessSubjectStatus().getId().toString():"processSubjectStatus is null"));*/
            //LOG.info("ID_Process_Activiti: " + oProcessSubjectParent.getSnID_Process_Activiti());
            //LOG.info("Order: " + oProcessSubjectParent.getnOrder().toString());
            //LOG.info("DateEdit: " + oProcessSubjectParent.getsDateEdit().toString());
            //LOG.info("DatePlan: " + oProcessSubjectParent.getsDatePlan().toString());
            //LOG.info("Login: " + oProcessSubjectParent.getsLogin());
            //processSubjectDao.saveOrUpdate(oProcessSubjectParent);
            Map<String, Object> mParamDocument = new HashMap<>();
            mParamDocument.put("sTaskProcessDefinition", sTaskProcessDefinition_Value);
            mParamDocument.put("sID_Attachment", sID_Attachment_Value);
            mParamDocument.put("sContent", sContent_Value);
            mParamDocument.put("sAutorResolution", sAutorResolution_Value);
            mParamDocument.put("sDateExecution", sDateExecution_Value);
            mParamDocument.put("sTextResolution", sTextResolution_Value);

            if (aJsonRow != null) {
                for (int i = 0; i < aJsonRow.size(); i++) {

                    Map<String, Object> mParamTask = new HashMap<>();

                    LOG.info("json array element" + i + " is " + aJsonRow.get(i).toString());

                    JSONObject sJsonField = (JSONObject) aJsonRow.get(i);
                    JSONArray aJsonField = (JSONArray) sJsonField.get("aField");
                    mParamTask.putAll(mParamDocument);
                    for (int j = 0; j < aJsonField.size(); j++) {
                        JSONObject sJsonElem = (JSONObject) aJsonField.get(j);
                        String id = sJsonElem.get("id").toString();
                        String value = sJsonElem.get("value").toString();
                        mParamTask.put(id, value);
                    }
                    LOG.info("mParamTask: " + mParamTask); //логируем всю мапу
                    //for (String key : resultJsonMap.keySet()){
                    //LOG.info("resultJsonMap: " + key + " : " + resultJsonMap.get(key));
                    //}

                    ProcessInstance oProcessInstanceChild = runtimeService.startProcessInstanceByKey("system_task", mParamTask);
                    LOG.info("oProcessInstanceChild id: " + (oProcessInstanceChild != null ? oProcessInstanceChild.getId() : " oInstanse is null"));
                    if (oProcessInstanceChild != null) {
                        ProcessSubject oProcessSubjectChild = processSubjectDao
                                .setProcessSubject(oProcessInstanceChild.getId(), (String) mParamTask.get("sLogin_isExecute"),
                                        new DateTime(df.parse(sDateExecution_Value)), new Long(i + 1), processSubjectStatus);
                        ProcessSubjectTree oProcessSubjectTreeParent = new ProcessSubjectTree();
                        oProcessSubjectTreeParent.setProcessSubjectParent(oProcessSubjectParent);
                        oProcessSubjectTreeParent.setProcessSubjectChild(oProcessSubjectChild);
                        processSubjectTreeDao.saveOrUpdate(oProcessSubjectTreeParent);
                    }
                }
            } else {
                LOG.info("JSONArray is null");
            }
        } catch (Exception e) {
            LOG.error("SetTasks listener throws an error: " + e.toString());
        }

        //
        //oProcessSubject.
        //processSubject.saveOrUpdate(oProcessSubject);
        //processSubjectTree.saveOrUpdate(oProcessSubjectTree);
        //
    }
}
