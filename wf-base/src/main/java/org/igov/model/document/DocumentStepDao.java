package org.igov.model.document;

import java.util.List;


import org.igov.model.core.EntityDao;

public interface DocumentStepDao extends EntityDao<Long, DocumentStep>  {

    /**
     * finds all steps for Activiti process
     * @return list of DocumentStep
     */
    List<DocumentStep> getStepForProcess(String snID_Process_Activiti);

    List <DocumentStep> getRightsByStep(String snID_Process_Activiti,String sKey_Step);

    }

