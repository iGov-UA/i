package org.igov.service.adapter;

import org.activiti.engine.repository.ProcessDefinition;
import org.igov.service.entity.ProcDefinition;
import org.igov.service.entity.ProcDefinitionI;

/**
 * Created by diver on 4/12/15.
 */
public class ProcDefinitionAdapter implements AdapterI<ProcessDefinition, ProcDefinitionI> {

    @Override
    public ProcDefinitionI apply(ProcessDefinition processDefinition) {
        ProcDefinition procDefinition = new ProcDefinition();
        procDefinition.setCategory(processDefinition.getCategory());
        procDefinition.setDeploymentId(processDefinition.getDeploymentId());
        procDefinition.setDescription(processDefinition.getDescription());
        procDefinition.setDiagramResourceName(processDefinition.getDiagramResourceName());
        procDefinition.setId(processDefinition.getId());
        procDefinition.setKey(processDefinition.getKey());
        procDefinition.setName(processDefinition.getName());
        procDefinition.setResourceName(processDefinition.getResourceName());
        procDefinition.setSuspended(processDefinition.isSuspended());
        procDefinition.setTenantId(processDefinition.getTenantId());
        procDefinition.setVersion(processDefinition.getVersion());
        return procDefinition;
    }
}
