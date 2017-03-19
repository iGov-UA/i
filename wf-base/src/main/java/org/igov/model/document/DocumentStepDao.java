package org.igov.model.document;

import org.igov.model.core.EntityDao;

import java.util.List;

public interface DocumentStepDao extends EntityDao<Long, DocumentStep> {

    /**
     * finds all steps for Activiti process
     * @return list of DocumentStep
     */
    List<DocumentStep> getStepForProcess(String snID_Process_Activiti);



}
