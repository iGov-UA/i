/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.igov.jdbc;

import java.io.PrintStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import org.igov.analytic.model.access.AccessGroup;
import org.igov.analytic.model.access.AccessUser;
import org.igov.analytic.model.attribute.Attribute;
import org.igov.analytic.model.attribute.AttributeDao;
import org.igov.analytic.model.attribute.AttributeType;
import org.igov.analytic.model.attribute.AttributeTypeDao;
import org.igov.analytic.model.attribute.Attribute_Date;
import org.igov.analytic.model.attribute.Attribute_File;
import org.igov.analytic.model.attribute.Attribute_FileDao;
import org.igov.analytic.model.attribute.Attribute_Integer;
import org.igov.analytic.model.attribute.Attribute_StingShort;
import org.igov.analytic.model.process.ProcessDao;
import org.igov.analytic.model.process.ProcessTask;
import org.igov.analytic.model.source.SourceDB;
import org.igov.analytic.model.source.SourceDBDao;
import org.igov.io.db.kv.analytic.IFileStorage;
import org.igov.service.ArchiveService;
import static org.igov.service.ArchiveService.DB_PATH;
import static org.igov.service.ArchiveService.DB_PSWD;
import static org.igov.service.ArchiveService.DB_USR;
import static org.igov.service.ArchiveService.queryComplaim;
import static org.igov.service.ArchiveService.queryListComplain;
import static org.igov.service.ArchiveService.queryMinDate;
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

public class BackupData implements ArchiveService {

    private static Connection conn;
    private static Statement stat, statComplain, statUpdateComplain;
    private static final PrintStream o = System.out;
    private static final Logger LOG = LoggerFactory.getLogger(ProcessController.class);

    public static void main(String[] args) throws Exception {

        try {
            DriverManager.registerDriver(new com.sybase.jdbc3.jdbc.SybDriver());
            conn = DriverManager.getConnection(DB_PATH, DB_USR, DB_PSWD);
            stat = conn.createStatement();
            statComplain = conn.createStatement();
            statUpdateComplain = conn.createStatement();
            boolean hasNextDate = true;
            int index = 0;
            while (hasNextDate) {
                ResultSet rs = stat.executeQuery(queryMinDate.toString());
                ResultSet rsComplain;
                if (index < 1 && rs.next()) {
                    index++;
                    String date = rs.getString("minREGDATE");
                    System.out.println("date:" + date);
                    for (rs = stat.executeQuery(String.format(queryListComplain, date)); rs.next();) {
                        String sID_Complain = rs.getString("IDENTITY");
                        System.out.println("sID_Complain:" + sID_Complain);
                        for (rsComplain = statComplain.executeQuery(String.format(queryComplaim, sID_Complain)); rsComplain.next();) {
                            getProcess(rsComplain);
                        }
                    }
                } else {
                    hasNextDate = false;
                }
            }
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

    private static void getProcess(ResultSet rs) throws SQLException, ParseException, Exception {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.S");
        ResultSetMetaData metaData = rs.getMetaData();
        int columnCount = metaData.getColumnCount();
        for (int i = 1; i <= columnCount; i++) {
            System.out.println(metaData.getColumnClassName(i) + " " + metaData.getColumnLabel(i));
            org.igov.analytic.model.process.Process process = new org.igov.analytic.model.process.Process();
            //SourceDB sourceDB = sourceDBDao.findByIdExpected(new Long(1));
            System.out.println("rs.getString(\"REGDATE\"): " + rs.getString("REGDATE"));
            process.setoDateStart(new DateTime(dateFormat.parse(rs.getString("REGDATE"))));
            System.out.println(process.getoDateStart());
            process.setoDateFinish(new DateTime(dateFormat.parse(rs.getString("EXECCOMPLDATE")))); //EXECCOMPLDATE
            //System.out.println(process.getoDateFinish());
            //process.setoSourceDB(sourceDB);
            process.setsID_(rs.getString("REGNUMBER"));
            process.setData(rs.getString("IDENTITY"));
            //process = processDao.saveOrUpdate(process);
            AttributeType attributeType = null;
            Attribute attribute = new Attribute();
            if ("java.lang.Integer".equalsIgnoreCase(metaData.getColumnClassName(i).trim())) {
                //attributeType = attributeTypeDao.findByIdExpected(new Long(1));
                Attribute_Integer attributeValue = new Attribute_Integer();
                attributeValue.setnValue(rs.getInt(i));
                attribute.setoAttribute_Integer(attributeValue);
            } else if ("java.lang.String".equalsIgnoreCase(metaData.getColumnClassName(i).trim())) {
                //attributeType = attributeTypeDao.findByIdExpected(new Long(3));
                Attribute_StingShort attributeValue = new Attribute_StingShort();
                attributeValue.setsValue(rs.getString(i));
                attribute.setoAttribute_StingShort(attributeValue);
            } else if ("java.sql.Timestamp".equalsIgnoreCase(metaData.getColumnClassName(i).trim())) {
                //attributeType = attributeTypeDao.findByIdExpected(new Long(6));
                Attribute_Date attributeValue = new Attribute_Date();
                System.out.println(metaData.getColumnLabel(i) + ": " + rs.getString(i));
                if(rs.getString(i) != null){
                    attributeValue.setoValue(new DateTime(dateFormat.parse(rs.getString(i))));
                }
                attribute.setoAttribute_Date(attributeValue);
            } else {
                throw new Exception("Not Foud type of attribute!!!!!!!!!!!!!");
            }
            attribute.setoProcess(process);
            attribute.setoAttributeType(attributeType);
            attribute.setsID_(metaData.getColumnLabel(i));
            attribute.setName(metaData.getColumnLabel(i));
            //attribute = attributeDao.saveOrUpdate(attribute);
        }

        //statUpdateComplain.executeUpdate(String.format(queryUpdateComplain, sID_Complain));
    }
}
