package org.igov.model.document;

import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import org.hibernate.Query;
import org.igov.io.db.kv.statical.IBytesDataStorage;
import org.igov.model.document.DocumentOperator_SubjectOrgan;
import org.igov.model.subject.Subject;
import org.igov.util.db.QueryBuilder;
import org.igov.util.db.queryloader.QueryLoader;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;
import org.igov.model.core.EntityDao;
import org.igov.model.core.GenericEntityDao;
import org.igov.io.db.kv.statical.impl.BytesDataStorage;

import java.io.IOException;
import java.util.List;
import org.igov.model.subject.Subject;

@Repository
public class DocumentDaoImpl extends GenericEntityDao<Document> implements DocumentDao {

    @Autowired
    QueryLoader queryLoader;

    private static final String contentMock = "No content!!!";

    @Autowired
    @Qualifier("documentOperatorDao")
    private EntityDao<DocumentOperator_SubjectOrgan> documentOperatorDao;

    @Autowired
    private IBytesDataStorage durableBytesDataStorage;

    protected DocumentDaoImpl() {
        super(Document.class);
    }

    @Override
    public List<Document> getDocuments(Long nID_Subject) {
        return findAllBy("subject.id", nID_Subject);
    }

    @Override
    public List<String> getDocumentContentKeys(int offset) {
        String selectQuery = queryLoader.get("select_document_content_key.sql");
        Query query = new QueryBuilder(getSession())
                .append(selectQuery)
                .setParam("OFFSET", offset)
                .toSQLQuery();
        return Lists.newArrayList(Iterables.filter(query.list(), String.class));
    }

    @Override
    public Document getDocument(Long id) {
        return findById(id).orNull();
    }

    @Override
    public byte[] getDocumentContent(Long id) {
        Document document = findByIdExpected(id);
        byte[] contentByte = durableBytesDataStorage.getData(document.getContentKey());
        return contentByte != null ? contentByte : contentMock.getBytes();
    }

    @Override
    public byte[] getDocumentContent(String contentKey) {
        byte[] contentByte = durableBytesDataStorage.getData(contentKey);
        return contentByte != null ? contentByte : contentMock.getBytes();
    }

    //TODO: it's not cool
    public Long setDocument(Long nID_Subject, Long nID_Subject_Upload, String sID_Subject_Upload,
            String sSubjectName_Upload, String sName, Long nID_DocumentType,
            Long nID_DocumentContentType, String sFileName,
            String sFileContentType, byte[] aoContent, String oSignData) throws IOException {

        Document document = new Document();
        document.setsID_subject_Upload(sID_Subject_Upload);
        document.setSubjectName_Upload(sSubjectName_Upload);
        document.setName(sName);

        Subject oSubject_Upload = new Subject();
        oSubject_Upload.setId(nID_Subject_Upload);
        document.setSubject_Upload(oSubject_Upload);

        DocumentType oDocumentType = new DocumentType();
        oDocumentType.setId(nID_DocumentType);
        document.setDocumentType(oDocumentType);

        DocumentContentType documentContentType = new DocumentContentType();
        documentContentType.setId(nID_DocumentContentType == null ? 2
                : nID_DocumentContentType);// TODO определять/генерить реальный
        // ИД, по Контенттайп с oFile
        document.setDocumentContentType(documentContentType);

        if (nID_Subject != null) {
            Subject oSubject = new Subject();
            oSubject.setId(nID_Subject);
            document.setSubject(oSubject);
        }

        document.setContentKey(durableBytesDataStorage.saveData(aoContent));
        document.setContentType(sFileContentType);
        document.setFile(sFileName);
        document.setDate_Upload(new DateTime());

        document.setoSignData(oSignData);
        getSession().saveOrUpdate(document);
        return document.getId();

    }

    @Override
    public DocumentOperator_SubjectOrgan getOperator(Long operatorId) {
        return documentOperatorDao.findByExpected("nID_SubjectOrgan", operatorId);
    }

    //TODO: why this method is here?
    @Override
    public List<DocumentOperator_SubjectOrgan> getAllOperators() {
        return documentOperatorDao.findAll();
    }
}