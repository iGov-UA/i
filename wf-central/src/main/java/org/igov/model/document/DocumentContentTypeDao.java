package org.igov.model.document;

import org.igov.model.core.EntityDao;

import java.util.List;

public interface DocumentContentTypeDao extends EntityDao<DocumentContentType> {

    public DocumentContentType getDocumentContentType(String documentContentTypeName);

    public Long setDocumentContent(DocumentContentType documentContentType);

    public List<DocumentContentType> getDocumentContentTypes();

    public DocumentContentType setDocumentContentType(Long nID, String sName);

    public DocumentContentType getDocumentContentType(Long nID);

    public void removeDocumentContentType(Long nID);
}
