package org.igov.model.document.access;

import org.igov.model.core.EntityDao;

public interface DocumentAccessDao extends EntityDao<DocumentAccess> {
    Long getIdAccess() throws Exception;

    String setDocumentLink(Long nID_Document, String sFIO, String sTarget, String sTelephone, Long nMS,
            String sMail) throws Exception;

    DocumentAccess getDocumentLink(Long nID_Access, String sSecret) throws Exception;

    //public boolean bSentDocumentAccessOTP(String sCode) throws Exception;
    String sSentDocumentAccessOTP_Phone(String sCode) throws Exception;

    String setDocumentAccess(Long nID_Access, String sSecret, String sAnswer) throws Exception;

    String getDocumentAccess(Long nID_Access, String sSecret) throws Exception;

    DocumentAccess getDocumentAccess(String sCode_DocumentAccess);
}
