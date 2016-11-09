package org.igov.model.document;

import org.igov.model.core.GenericEntityDao;
import org.springframework.stereotype.Repository;

@Repository
public class DocumentStepDaoImpl extends GenericEntityDao<Long, DocumentStep> implements DocumentStepDao {

    public DocumentStepDaoImpl() {
        super(DocumentStep.class);
    }

}
