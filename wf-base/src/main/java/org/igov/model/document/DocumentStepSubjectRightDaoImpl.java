package org.igov.model.document;

import org.igov.model.core.GenericEntityDao;
import org.springframework.stereotype.Repository;

@Repository
public class DocumentStepSubjectRightDaoImpl extends GenericEntityDao<Long, DocumentStepSubjectRight> implements DocumentStepSubjectRightDao {

    public DocumentStepSubjectRightDaoImpl() {
        super(DocumentStepSubjectRight.class);
    }

}
