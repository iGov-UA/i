/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.igov.model.object;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import org.igov.model.Document;
import org.igov.model.DocumentDao;
import org.igov.model.HistoryEventDao;
import org.igov.model.Subject;
import org.igov.model.SubjectDao;
import org.igov.model.SubjectOrganDao;
import org.igov.model.enums.HistoryEventMessage;
import org.igov.model.enums.HistoryEventType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

/**
 *
 * @author Belyavtsev Vladimir Vladimirovich (BW)
 */
public class ModelManager_Central {
    
    private static final Logger LOG = LoggerFactory.getLogger(ModelManager_Central.class);
    
    @Autowired
    private HistoryEventDao historyEventDao;
    
    @Autowired
    private DocumentDao documentDao;
    
    @Autowired
    private SubjectDao subjectDao;
    @Autowired
    private SubjectOrganDao subjectOrganDao;
    
    
    public void createHistoryEvent(HistoryEventType eventType, Long documentId,
            String sFIO, String sPhone, Long nMs, String sEmail) {
        Map<String, String> values = new HashMap<>();
        try {
            values.put(HistoryEventMessage.FIO, sFIO);
            values.put(HistoryEventMessage.TELEPHONE, sPhone);
            values.put(HistoryEventMessage.EMAIL, sEmail);
            values.put(HistoryEventMessage.DAYS, "" + TimeUnit.MILLISECONDS.toDays(nMs));

            Document oDocument = documentDao.getDocument(documentId);
            values.put(HistoryEventMessage.DOCUMENT_NAME, oDocument.getName());
            values.put(HistoryEventMessage.DOCUMENT_TYPE, oDocument.getDocumentType().getName());
            documentId = oDocument.getSubject().getId();
        } catch (Exception e) {
            LOG.warn("can't get document info!", e);
        }
        try {
            String eventMessage = HistoryEventMessage.createJournalMessage(eventType, values);
            historyEventDao.setHistoryEvent(documentId, eventType.getnID(),
                    eventMessage, eventMessage);
        } catch (IOException e) {
            LOG.error("error during creating HistoryEvent", e);
        }
    }    
    
    public void createHistoryEvent(HistoryEventType eventType,
            Long nID_Subject, String sSubjectName_Upload, Long nID_Document,
            Document document) {
        Map<String, String> values = new HashMap<>();
        try {
            Document oDocument = document == null ? documentDao
                    .getDocument(nID_Document) : document;
            values.put(HistoryEventMessage.DOCUMENT_TYPE, oDocument
                    .getDocumentType().getName());
            values.put(HistoryEventMessage.DOCUMENT_NAME, oDocument.getName());
            values.put(HistoryEventMessage.ORGANIZATION_NAME,
                    sSubjectName_Upload);
        } catch (RuntimeException e) {
            LOG.warn("can't get document info!", e);
        }
        try {
            String eventMessage = HistoryEventMessage.createJournalMessage(
                    eventType, values);
            historyEventDao.setHistoryEvent(nID_Subject, eventType.getnID(),
                    eventMessage, eventMessage);
        } catch (IOException e) {
            LOG.error("error during creating HistoryEvent", e);
        }
    }    
    
    public Subject syncSubject_Upload(String sID_Subject_Upload) {
        Subject subject_Upload = subjectDao.getSubject(sID_Subject_Upload);
        if (subject_Upload == null) {
            subject_Upload = subjectOrganDao.setSubjectOrgan(sID_Subject_Upload).getoSubject();
        }
        return subject_Upload;
    }
    
}
