/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.igov.service;

import com.google.common.base.Optional;
import java.io.PrintStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import org.igov.analytic.model.attribute.Attribute;
import org.igov.analytic.model.attribute.AttributeDao;
import org.igov.analytic.model.attribute.AttributeType;
import org.igov.analytic.model.attribute.AttributeTypeDao;
import org.igov.analytic.model.attribute.Attribute_Date;
import org.igov.analytic.model.attribute.Attribute_DateDao;
import org.igov.analytic.model.attribute.Attribute_Integer;
import org.igov.analytic.model.attribute.Attribute_IntegerDao;
import org.igov.analytic.model.attribute.Attribute_StringLong;
import org.igov.analytic.model.attribute.Attribute_StringShort;
import org.igov.analytic.model.attribute.Attribute_StringShortDao;
import org.igov.analytic.model.config.Config;
import org.igov.analytic.model.config.ConfigDao;
import org.igov.analytic.model.process.ProcessDao;
import org.igov.analytic.model.source.SourceDB;
import org.igov.analytic.model.source.SourceDBDao;
import org.igov.model.core.AbstractEntity;
import org.igov.model.core.EntityDao;
import org.igov.service.controller.ProcessController;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 *
 * @author olga
 */
@Service
public class ArchiveServiceImpl implements ArchiveService {

    private static Connection conn;
    private static Statement stat, statComplain, statUpdateComplain;
    private static final PrintStream o = System.out;
    private static final Logger LOG = LoggerFactory.getLogger(ProcessController.class);

    @Autowired
    private ProcessDao processDao;

    @Autowired
    private SourceDBDao sourceDBDao;

    @Autowired
    private AttributeTypeDao attributeTypeDao;

    @Autowired
    private AttributeDao attributeDao;

    @Autowired
    private Attribute_IntegerDao attribute_IntegerDao;

    @Autowired
    private Attribute_StringShortDao attribute_StringShortDao;
    
    @Autowired
    private Attribute_StringShortDao attribute_StringLongDao;

    @Autowired
    private Attribute_DateDao attribute_DateDao;

    /*@Autowired
     private IFileStorage durableFileStorage;*/
    @Autowired
    private ConfigDao configDao;

    //public static void main(String[] args) throws Exception {
    public void archiveData() throws SQLException, ParseException, Exception {

        try {
            //SimpleDateFormat dateFormatFull = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.S");
            //SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
            DriverManager.registerDriver(new com.sybase.jdbc3.jdbc.SybDriver());
            conn = DriverManager.getConnection(DB_PATH, DB_USR, DB_PSWD);
            stat = conn.createStatement();
            statComplain = conn.createStatement();
            statUpdateComplain = conn.createStatement();
            ResultSet rsComplain;
            boolean hasNextDate = true;
            int index = 0;
            Optional<Config> configOptional;
            String dateLastBackup;
            Config config;
            while (hasNextDate) { // while (hasNextDate && index < 3)
                configOptional = configDao.findBy("name", "dateLastBackup");
                if (configOptional.isPresent()) {
                    config = configOptional.get();
                } else {
                    config = new Config();
                    config.setName("dateLastBackup");
                    config.setsValue("1999-01-01");
                    configDao.saveOrUpdate(config);
                }
                dateLastBackup = config.getsValue();
                LOG.info("dateLastBackup:" + dateLastBackup);
                ResultSet rs = stat.executeQuery(String.format(queryMinDate, dateLastBackup));

                if (rs.next()) {
                    String date = rs.getString("minREGDATE");
                    LOG.info("date:" + date);
                    for (rs = stat.executeQuery(String.format(queryListComplain, date)); rs.next();) {
                        index++;
                        String sID_Complain = rs.getString("IDENTITY");

                        for (rsComplain = statComplain.executeQuery(String.format(queryComplaim, sID_Complain)); rsComplain.next();) {
                            LOG.info("index = " + index + " sID_Complain:" + sID_Complain + " rsComplain = " + rsComplain.getString("REGNUMBER"));
                            Optional<org.igov.analytic.model.process.Process> process = processDao.findBy("sID_Data", sID_Complain);
                            if (!process.isPresent()) {
                                setProcess(rsComplain);
                            } else {
                                LOG.info("Already presented sID_Complain: " + sID_Complain);
                            }

                            //setProcess(rsComplain);
                        }
                    }
                    config.setsValue(date.trim());
                    configDao.saveOrUpdate(config);
                } else {
                    hasNextDate = false;
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            LOG.error("!!! archive error: ", ex);
        } finally {
            try {
                if (null != stat) {
                    stat.close();
                    stat = null;
                }
            } catch (Exception _) {
            }
            try {
                if (null != statComplain) {
                    statComplain.close();
                    statComplain = null;
                }
            } catch (Exception _) {
            }
            try {
                if (null != statUpdateComplain) {
                    statUpdateComplain.close();
                    statUpdateComplain = null;
                }
            } catch (Exception _) {
            }
            try {
                if (null != conn) {
                    conn.close();
                    conn = null;
                }
            } catch (Exception _) {
            }
        }
    }

    private void setProcess(ResultSet rs) throws SQLException, ParseException, Exception {
        //SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.S");
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        EntityDao attributeValueDao;
        AbstractEntity attributeValueEntity;

        ResultSetMetaData metaData = rs.getMetaData();
        int columnCount = metaData.getColumnCount();

        org.igov.analytic.model.process.Process process = new org.igov.analytic.model.process.Process();
        SourceDB sourceDB = sourceDBDao.findByIdExpected(new Long(1));
        //LOG.info("rs.getString(\"REGDATE\"): " + rs.getString("REGDATE"));
        DateTime dateStart, dateFinish;
        if (rs.getString("REGDATE") != null) {
            dateStart = new DateTime(dateFormat.parse(rs.getString("REGDATE")));
        } else {
            dateStart = new DateTime();
        }
        process.setoDateStart(dateStart);
        if (rs.getString("EXECCOMPLDATE") != null) {
            dateFinish = new DateTime(dateFormat.parse(rs.getString("EXECCOMPLDATE")));
        } else {
            dateFinish = new DateTime();
        }
        process.setoDateFinish(dateFinish);
        process.setoSourceDB(sourceDB);
        process.setsID_(rs.getString("REGNUMBER"));
        process.setsID_Data(rs.getString("IDENTITY"));
        process.setsID_Data(rs.getString("IDENTITY"));
        process = processDao.saveOrUpdate(process);
        for (int i = 1; i <= columnCount; i++) {
            AttributeType attributeType;
            Attribute attribute = new Attribute();

            if ("java.lang.Integer".equalsIgnoreCase(metaData.getColumnClassName(i).trim())) {
                attributeType = attributeTypeDao.findByIdExpected(new Long(1));
                Attribute_Integer attributeValue = new Attribute_Integer();
                attributeValue.setnValue(rs.getInt(i));
                //attribute.setoAttribute_Integer(attributeValue);
                attributeValue.setoAttribute(attribute);
                attributeValueDao = attribute_IntegerDao;
                attributeValueEntity = attributeValue;
            } else if ("java.lang.String".equalsIgnoreCase(metaData.getColumnClassName(i).trim())) {
                if (rs.getString(i) != null && rs.getString(i).length() < 255) {
                    attributeType = attributeTypeDao.findByIdExpected(new Long(3));
                    Attribute_StringShort attributeValue = new Attribute_StringShort();
                    attributeValue.setsValue(rs.getString(i));
                    //attribute.setoAttribute_StringShort(attributeValue);
                    attributeValue.setoAttribute(attribute);
                    attributeValueDao = attribute_StringShortDao;
                    attributeValueEntity = attributeValue;
                } else {
                    attributeType = attributeTypeDao.findByIdExpected(new Long(4));
                    Attribute_StringLong attributeValue = new Attribute_StringLong();
                    attributeValue.setsValue(rs.getString(i));
                    //attribute.setoAttribute_StringLong(attributeValue);
                    attributeValue.setoAttribute(attribute);
                    attributeValueDao = attribute_StringLongDao;
                    attributeValueEntity = attributeValue;
                }

            } else if ("java.sql.Timestamp".equalsIgnoreCase(metaData.getColumnClassName(i).trim())
                    || "java.sql.Date".equalsIgnoreCase(metaData.getColumnClassName(i).trim())) {
                attributeType = attributeTypeDao.findByIdExpected(new Long(6));
                Attribute_Date attributeValue = new Attribute_Date();
                if (rs.getString(i) != null) {
                    attributeValue.setoValue(new DateTime(dateFormat.parse(rs.getString(i))));
                }
                //attribute.setoAttribute_Date(attributeValue);
                attributeValue.setoAttribute(attribute);
                attributeValueDao = attribute_DateDao;
                attributeValueEntity = attributeValue;
            } else {
                throw new Exception("Not Foud type of attribute " + metaData.getColumnClassName(i).trim() + " !!!!!!!!!!!!!!!1");
            }
            attribute.setoProcess(process);
            attribute.setoAttributeType(attributeType);
            attribute.setsID_(metaData.getTableName(i) + ":" + metaData.getColumnLabel(i));
            attribute.setName(metaData.getColumnLabel(i));
            attribute = attributeDao.saveOrUpdate(attribute);
            LOG.info("attribute:" + attribute.getId());
            attributeValueDao.saveOrUpdate(attributeValueEntity);
            LOG.info("attributeValueEntity:" + attributeValueEntity.getId());
        }
    }
}
