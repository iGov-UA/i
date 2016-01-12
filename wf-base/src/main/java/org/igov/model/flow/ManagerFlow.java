/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.igov.model.flow;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.repository.ProcessDefinition;
import org.activiti.engine.task.Task;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

/**
 *
 * @author Belyavtsev Vladimir Vladimirovich (BW)
 */
public class ManagerFlow {

    private static final Logger LOG = LoggerFactory.getLogger(ManagerFlow.class);
    
    @Autowired
    private RepositoryService repositoryService;
    
    @Autowired
    private FlowServiceDataDao flowServiceDataDao;
    
    public void addFlowSlowTicketToResult(List<Map<String, String>> res,
            SimpleDateFormat dateFormat, FlowSlotTicket currFlowSlowTicket,
            Task tasksByActivitiID) {
        Map<String, String> currRes = new HashMap<String, String>();

        StringBuilder sb = new StringBuilder();
        sb.append("Adding flow slot ticket: ");
        sb.append(currFlowSlowTicket.getId());
        sb.append(":");
        sb.append(currFlowSlowTicket.getnID_Subject());
        sb.append(":");
        sb.append(currFlowSlowTicket.getsDateStart());
        sb.append(":");
        sb.append(currFlowSlowTicket.getsDateFinish());
        LOG.info(sb.toString());

        currRes.put("nID", currFlowSlowTicket.getId().toString());
        currRes.put("nID_FlowSlot", currFlowSlowTicket.getoFlowSlot() != null ?
                currFlowSlowTicket.getoFlowSlot().getId().toString() : "");
        currRes.put("nID_Subject", currFlowSlowTicket.getnID_Subject().toString());
        Date startDate = new Date(currFlowSlowTicket.getsDateStart().getMillis());
        currRes.put("sDateStart", dateFormat.format(startDate));
        Date finishDate = new Date(currFlowSlowTicket.getsDateFinish().getMillis());
        currRes.put("sDateFinish", dateFormat.format(finishDate));
        Date editDate = new Date(currFlowSlowTicket.getsDateEdit().getMillis());
        currRes.put("sDateEdit", dateFormat.format(editDate));

        currRes.put("nID_Task_Activiti", tasksByActivitiID.getId());

        currRes.put("name", tasksByActivitiID.getName());
        currRes.put("id", tasksByActivitiID.getId());
        currRes.put("assignee", tasksByActivitiID.getAssignee());
        currRes.put("nID_Instance", tasksByActivitiID.getProcessInstanceId());

        currRes.put("sUserTaskName", tasksByActivitiID.getName());
        ProcessDefinition processDefinition = repositoryService.createProcessDefinitionQuery()
                .processDefinitionId(tasksByActivitiID.getProcessDefinitionId()).singleResult();
        currRes.put("sNameBP", processDefinition != null ? processDefinition.getName() : "");
        currRes.put("sTaskDate", dateFormat.format(tasksByActivitiID.getCreateTime()));
        res.add(currRes);
    }

    public List<FlowProperty> getFilteredFlowPropertiesForFlowServiceData(Long nID_Flow_ServiceData,
            String sID_BP,
            Long nID_SubjectOrganDepartment,
            Boolean bExclude) throws Exception {

        if (nID_Flow_ServiceData == null) {
            if (sID_BP != null) {
                nID_Flow_ServiceData = flowServiceDataDao.findFlowId(sID_BP, nID_SubjectOrganDepartment);
                LOG.info("(sID_BP={},nID_Flow_ServiceData={})",sID_BP,nID_Flow_ServiceData);
            } else {
                String sError = "nID_Flow_ServiceData==null and sID_BP==null";
                LOG.error(sError);
                throw new Exception(sError);
            }
        }
        if (nID_Flow_ServiceData == null) {
            String sError = "nID_Flow_ServiceData==null";
            LOG.error(sError);
            throw new Exception(sError);
        }

        LOG.info("(nID_Flow_ServiceData={})", nID_Flow_ServiceData);
        Flow_ServiceData flowServiceData = flowServiceDataDao.findByIdExpected(nID_Flow_ServiceData);
        List<FlowProperty> res = new LinkedList<FlowProperty>();
        if (flowServiceData != null) {
            if (flowServiceData.getFlowProperties() != null && !flowServiceData.getFlowProperties().isEmpty()) {
                LOG.info("(nID_Flow_ServiceData contains {} elements. Getting only with bExclude ={}) ",
                        flowServiceData.getFlowProperties().size(), bExclude.toString());
                for (FlowProperty flowProperty : flowServiceData.getFlowProperties()) {
                    LOG.info("flowProperty (id={} : name={} : bExclude={})",
                            flowProperty.getId(), flowProperty.getsName(), flowProperty.getbExclude());
                    if (flowProperty.getbExclude() != null && flowProperty.getbExclude().equals(bExclude)) {
                        res.add(flowProperty);
                    }
                }
                LOG.info("Found {} flow properties with bExclude={}. Results:{}",res.size(), bExclude, res.toString());
            } else {
                LOG.info("Flow service data contains empty list of FlowProperty");
            }
        } else {
            LOG.info("Have not found nID_Flow_ServiceData object with ID: {}", nID_Flow_ServiceData);
        }
        return res;
    }

    public FlowProperty fillFlowProperty(String sName, String sRegionTime,
            String saRegionWeekDay, String sDateTimeAt, String sDateTimeTo,
            Integer nLen,
            String sLenType,
            String sData,
            FlowProperty flowProperty) {
        flowProperty.setbExclude(false);
        flowProperty.setsName(sName);
        flowProperty.setsRegionTime(sRegionTime);
        flowProperty.setSaRegionWeekDay(saRegionWeekDay);
        if (nLen != null) {
            flowProperty.setLen(nLen);
        }
        if (sLenType != null) {
            flowProperty.setLenType(sLenType);
        }
        if (sData != null) {
            flowProperty.setsData(sData);
        }
        flowProperty.setsDateTimeAt(sDateTimeAt);
        flowProperty.setsDateTimeTo(sDateTimeTo);
        return flowProperty;
    }    
}
