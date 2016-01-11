/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.igov.model.document;

import org.igov.model.core.EntityDao;

import java.util.List;

/**
 * @author olya
 */
public interface DocumentTypeDao extends EntityDao<DocumentType> {

    public List<DocumentType> getDocumentTypes();

    public DocumentType setDocumentType(Long nID, String sName, Boolean bHidden);

    public void removeDocumentType(Long nID);

    public DocumentType getDocumentType(Long nID);
}