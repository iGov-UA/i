package org.igov.model.document;

import java.util.List;
import org.igov.model.core.EntityDao;

public interface DocumentStepSubjectRightDao extends EntityDao<Long, DocumentStepSubjectRight> {

    public List<DocumentStepSubjectRight> findUnassignedUnprocessedDocument(String sLogin);

    public List<DocumentStepSubjectRight> findOpenedUnassignedWithoutECPDocument(String sLogin);

}
