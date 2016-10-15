package org.igov.service.business.document.access.handler;

import org.igov.model.document.Document;
import org.igov.model.document.access.DocumentAccess;
import org.springframework.stereotype.Component;

/**
 * @author dgroup
 * @since 28.06.15
 */
@Component
public interface DocumentAccessHandler {

    DocumentAccessHandler setDocumentType(Long docTypeID);

    DocumentAccessHandler setAccessCode(String sCode_DocumentAccess);

    DocumentAccessHandler setPassword(String sPass);

    DocumentAccessHandler setWithContent(Boolean bWithContent);

    DocumentAccessHandler setIdSubject(Long nID_Subject);

    DocumentAccess getAccess();

    Document getDocument();
}