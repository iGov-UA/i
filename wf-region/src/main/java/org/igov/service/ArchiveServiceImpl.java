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
import org.igov.analytic.model.attribute.Attribute_FileDao;
import org.igov.analytic.model.attribute.Attribute_Integer;
import org.igov.analytic.model.attribute.Attribute_StingShort;
import org.igov.analytic.model.config.Config;
import org.igov.analytic.model.config.ConfigDao;
import org.igov.analytic.model.process.ProcessDao;
import org.igov.analytic.model.source.SourceDB;
import org.igov.analytic.model.source.SourceDBDao;
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
    private Attribute_FileDao attribute_FileDao;

    /*@Autowired
     private IFileStorage durableFileStorage;*/
    @Autowired
    private ConfigDao configDao;

    //public static void main(String[] args) throws Exception {
    public void archiveData() throws SQLException, ParseException, Exception {

        try {
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.S");
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
            while (hasNextDate) {
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
                ResultSet rs = stat.executeQuery(String.format(queryMinDate, dateLastBackup));

                if (index < 1 && rs.next()) {
                    index++;
                    String date = rs.getString("minREGDATE");
                    LOG.info("date:" + date);
                    for (rs = stat.executeQuery(String.format(queryListComplain, date)); rs.next();) {
                        String sID_Complain = rs.getString("IDENTITY");
                        LOG.info("sID_Complain:" + sID_Complain);
                        for (rsComplain = statComplain.executeQuery(String.format(queryComplaim, sID_Complain)); rsComplain.next();) {
                            Optional<org.igov.analytic.model.process.Process> process = processDao.findBy("sID_Data", rs.getString("IDENTITY"));
                            if (!process.isPresent()) {
                                setProcess(rsComplain);
                            }
                        }
                    }
                    config.setsValue(dateFormat.format(date));
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

        ResultSetMetaData metaData = rs.getMetaData();
        int columnCount = metaData.getColumnCount();

        org.igov.analytic.model.process.Process process = new org.igov.analytic.model.process.Process();
        SourceDB sourceDB = sourceDBDao.findByIdExpected(new Long(1));
        LOG.info("rs.getString(\"REGDATE\"): " + rs.getString("REGDATE"));
        DateTime dateStart, dateFinish;
        if (rs.getString("REGDATE") != null) {
            dateStart = new DateTime(dateFormat.parse(rs.getString("REGDATE")));
        } else {
            dateStart = new DateTime();
        }
        process.setoDateStart(dateStart);
        System.out.println(process.getoDateStart());
        if (rs.getString("EXECCOMPLDATE") != null) {
            dateFinish = new DateTime(dateFormat.parse(rs.getString("EXECCOMPLDATE")));
        } else {
            dateFinish = new DateTime();
        }
        process.setoDateFinish(dateFinish); //EXECCOMPLDATE
        process.setoSourceDB(sourceDB);
        process.setsID_(rs.getString("REGNUMBER"));
        process.setsID_Data(rs.getString("IDENTITY"));
        process = processDao.saveOrUpdate(process);
        for (int i = 1; i <= columnCount; i++) {
            System.out.println(metaData.getColumnClassName(i) + " " + metaData.getColumnLabel(i));
            AttributeType attributeType;
            Attribute attribute = new Attribute();
            if ("java.lang.Integer".equalsIgnoreCase(metaData.getColumnClassName(i).trim())) {
                attributeType = attributeTypeDao.findByIdExpected(new Long(1));
                Attribute_Integer attributeValue = new Attribute_Integer();
                attributeValue.setnValue(rs.getInt(i));
                attribute.setoAttribute_Integer(attributeValue);
            } else if ("java.lang.String".equalsIgnoreCase(metaData.getColumnClassName(i).trim())) {
                attributeType = attributeTypeDao.findByIdExpected(new Long(3));
                Attribute_StingShort attributeValue = new Attribute_StingShort();
                attributeValue.setsValue(rs.getString(i));
                attribute.setoAttribute_StingShort(attributeValue);
            } else if ("java.sql.Timestamp".equalsIgnoreCase(metaData.getColumnClassName(i).trim())) {
                attributeType = attributeTypeDao.findByIdExpected(new Long(6));
                Attribute_Date attributeValue = new Attribute_Date();
                if (rs.getString(i) != null) {
                    attributeValue.setoValue(new DateTime(dateFormat.parse(rs.getString(i))));
                }
                attribute.setoAttribute_Date(attributeValue);
            } else {
                throw new Exception("Not Foud type of attribute!!!!!!!!!!!!!");
            }
            attribute.setoProcess(process);
            attribute.setoAttributeType(attributeType);
            attribute.setsID_(metaData.getTableName(i) + ":" + metaData.getColumnLabel(i) + ":");
            attribute.setName(metaData.getColumnLabel(i));
            attribute = attributeDao.saveOrUpdate(attribute);
        }
    }
}
