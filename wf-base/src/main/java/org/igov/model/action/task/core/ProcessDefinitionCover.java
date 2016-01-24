package org.igov.model.action.task.core;

import org.igov.model.action.task.core.entity.CoverInterface;
import org.activiti.engine.repository.ProcessDefinition;
import org.igov.model.action.task.core.entity.ProcDefinition;
import org.igov.model.action.task.core.entity.ProcDefinitionI;

/**
 * Created by diver on 4/12/15.
 */
public class ProcessDefinitionCover implements CoverInterface<ProcessDefinition, ProcDefinitionI> {

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
