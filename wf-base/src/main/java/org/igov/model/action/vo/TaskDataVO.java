package org.igov.model.action.vo;

import java.util.List;
import java.util.Map;

import org.activiti.engine.task.DelegationState;

/**
 *
 * @author idenysenko
 */
public class TaskDataVO {
    
    private String sId;
    private String sUrl;
    private String sOwner;
    private String sAssignee;
    private DelegationState oDelegationState;
    private String sName;
    private String sDescription;
    private String sCreateTime;
    private String sDueDate;
    private Integer nPriority;
    private Boolean bSuspended;
    private String sTaskDefinitionKey;
    private String sTenantId;
    private String sCategory;
    private String sFormKey;
    private String sParentTaskId;
    private String sParentTaskUrl;
    private String sExecutionId;
    private String sExecutionUrl;
    private String sProcessInstanceId;
    private String sProcessInstanceUrl;
    private String sProcessDefinitionId;
    private String sProcessDefinitionUrl;
    private List aVariables;
    private Map<String, Object> mFlowSlotTicketData;
    private Map<String, Object> mGlobalVariables;
       
    public TaskDataVO() {
    }

    public String getsId() {
        return sId;
    }

    public void setsId(String sId) {
        this.sId = sId;
    }

    public String getsUrl() {
        return sUrl;
    }

    public void setsUrl(String sUrl) {
        this.sUrl = sUrl;
    }

    public String getsOwner() {
        return sOwner;
    }

    public void setsOwner(String sOwner) {
        this.sOwner = sOwner;
    }

    public String getsAssignee() {
        return sAssignee;
    }

    public void setsAssignee(String sAssignee) {
        this.sAssignee = sAssignee;
    }

    public DelegationState getoDelegationState() {
        return oDelegationState;
    }

    public void setoDelegationState(DelegationState oDelegationState) {
        this.oDelegationState = oDelegationState;
    }

    public String getsName() {
        return sName;
    }

    public void setsName(String sName) {
        this.sName = sName;
    }

    public String getsDescription() {
        return sDescription;
    }

    public void setsDescription(String sDescription) {
        this.sDescription = sDescription;
    }

    public String getsCreateTime() {
        return sCreateTime;
    }

    public void setsCreateTime(String sCreateTime) {
        this.sCreateTime = sCreateTime;
    }

    public String getsDueDate() {
        return sDueDate;
    }

    public void setsDueDate(String sDueDate) {
        this.sDueDate = sDueDate;
    }

    public Integer getnPriority() {
        return nPriority;
    }

    public void setnPriority(Integer nPriority) {
        this.nPriority = nPriority;
    }

    public Boolean getbSuspended() {
        return bSuspended;
    }

    public void setbSuspended(Boolean bSuspended) {
        this.bSuspended = bSuspended;
    }

    public String getsTaskDefinitionKey() {
        return sTaskDefinitionKey;
    }

    public void setsTaskDefinitionKey(String sTaskDefinitionKey) {
        this.sTaskDefinitionKey = sTaskDefinitionKey;
    }

    public String getsTenantId() {
        return sTenantId;
    }

    public void setsTenantId(String sTenantId) {
        this.sTenantId = sTenantId;
    }

    public String getsCategory() {
        return sCategory;
    }

    public void setsCategory(String sCategory) {
        this.sCategory = sCategory;
    }

    public String getsFormKey() {
        return sFormKey;
    }

    public void setsFormKey(String sFormKey) {
        this.sFormKey = sFormKey;
    }

    public String getsParentTaskId() {
        return sParentTaskId;
    }

    public void setsParentTaskId(String sParentTaskId) {
        this.sParentTaskId = sParentTaskId;
    }

    public String getsParentTaskUrl() {
        return sParentTaskUrl;
    }

    public void setsParentTaskUrl(String sParentTaskUrl) {
        this.sParentTaskUrl = sParentTaskUrl;
    }

    public String getsExecutionId() {
        return sExecutionId;
    }

    public void setsExecutionId(String sExecutionId) {
        this.sExecutionId = sExecutionId;
    }

    public String getsExecutionUrl() {
        return sExecutionUrl;
    }

    public void setsExecutionUrl(String sExecutionUrl) {
        this.sExecutionUrl = sExecutionUrl;
    }

    public String getsProcessInstanceId() {
        return sProcessInstanceId;
    }

    public void setsProcessInstanceId(String sProcessInstanceId) {
        this.sProcessInstanceId = sProcessInstanceId;
    }

    public String getsProcessInstanceUrl() {
        return sProcessInstanceUrl;
    }

    public void setsProcessInstanceUrl(String sProcessInstanceUrl) {
        this.sProcessInstanceUrl = sProcessInstanceUrl;
    }

    public String getsProcessDefinitionId() {
        return sProcessDefinitionId;
    }

    public void setsProcessDefinitionId(String sProcessDefinitionId) {
        this.sProcessDefinitionId = sProcessDefinitionId;
    }

    public String getsProcessDefinitionUrl() {
        return sProcessDefinitionUrl;
    }

    public void setsProcessDefinitionUrl(String sProcessDefinitionUrl) {
        this.sProcessDefinitionUrl = sProcessDefinitionUrl;
    }

    public List getaVariables() {
        return aVariables;
    }

    public void setaVariables(List aVariables) {
        this.aVariables = aVariables;
    }

    public Map<String, Object> getmFlowSlotTicketData() {
        return mFlowSlotTicketData;
    }

    public void setmFlowSlotTicketData(Map<String, Object> mFlowSlotTicketData) {
        this.mFlowSlotTicketData = mFlowSlotTicketData;
    }

    public Map<String, Object> getmGlobalVariables() {
        return mGlobalVariables;
    }

    public void setmGlobalVariables(Map<String, Object> mGlobalVariables) {
        this.mGlobalVariables = mGlobalVariables;
    }
       
}
