package org.igov.model.document;

import org.igov.model.core.GenericEntityDao;
import org.springframework.stereotype.Repository;

@Repository
public class DocumentStepSubjectRightFieldDaoImpl extends GenericEntityDao<Long, DocumentStepSubjectRightField> implements DocumentStepSubjectRightFieldDao {

    public DocumentStepSubjectRightFieldDaoImpl() {
        super(DocumentStepSubjectRightField.class);
    }

}
