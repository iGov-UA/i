/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.igov.analytic.model.process;

import java.util.ArrayList;
import java.util.List;
import org.apache.log4j.Logger;
import org.igov.analytic.model.attribute.Attribute;
import org.igov.analytic.model.attribute.AttributeType;
import org.igov.analytic.model.attribute.AttributeTypeDao;
import org.igov.analytic.model.attribute.Attribute_File;
import org.igov.analytic.model.core.GenericEntityDaoAnalytic;
import org.igov.analytic.model.source.SourceDB;
import org.igov.analytic.model.source.SourceDBDao;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

/**
 *
 * @author olga
 */
//@Transactional("transactionManagerAnalytic")
@Repository()
public class ProcessDaoImpl extends GenericEntityDaoAnalytic<Long, Process> implements ProcessDao {

    private static final Logger log = Logger.getLogger(ProcessDaoImpl.class);

    /*@Autowired
    private SourceDBDao sourceDBDao;

    @Autowired
    private AttributeTypeDao attributeTypeDao;*/

    protected ProcessDaoImpl() {
        super(Process.class);
    }

    /*@Transactional("transactionManagerAnalytic")
    @SuppressWarnings("unchecked")
    @Override 
    public Process save(Process process, Long nID_SourceDB, Long nID_AttributeType) {

        Attribute attribute = new Attribute();
        Attribute_File attribute_File = new Attribute_File();

        SourceDB sourceDB = sourceDBDao.findByIdExpected(new Long(1));
        AttributeType attributeType = attributeTypeDao.findByIdExpected(new Long(7));
        process.setoDateStart(new DateTime());
        process.setoDateFinish(new DateTime());
        process.setoSourceDB(sourceDB);
        process.setsID_("test!!");
        process.setsID_Data("test!!");

        List<Attribute> attributes = new ArrayList();
        process.setaAttribute(attributes);
        attribute.setoAttributeType(attributeType);
        attribute_File.setsID_Data("test");
        attribute_File.setsFileName("test");
        attribute_File.setsContentType("pdf");
        attribute_File.setsExtName("txt");

        try {
            attribute = attributeDao.saveOrUpdate(attribute);
            attributes.add(attribute);
            attribute_File.setoAttribute(attribute);
            attribute_File = attribute_FileDao.saveOrUpdate(attribute_File);
            attribute.setoAttribute_File(attribute_File);
            process = processDao.saveOrUpdate(process);
        } catch (Exception ex) {
            LOG.error("!!!!Eror: ", ex);
            process.setsID_(ex.getMessage());
        }
        return null;
    }*/

}
