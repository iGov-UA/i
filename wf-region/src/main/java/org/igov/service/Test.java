/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.igov.service;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author olga
 */
public class Test {

    public static void main(String[] arg) {
        String DB_USR = "activiti";
        String DB_PSWD = "2xm53DhqB8VD";
        String DB_PATH = "jdbc:postgresql://test.db-all.igov.org.ua:5432/test-analytic";
        Logger LOG = LoggerFactory.getLogger(Test.class);
        Connection conn = null;
        Statement stat = null;
        try {
            System.out.println("Start!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
            DriverManager.registerDriver(new com.sybase.jdbc3.jdbc.SybDriver());
            conn = DriverManager.getConnection(DB_PATH, DB_USR, DB_PSWD);
            stat = conn.createStatement();
            String st = "delete FROM \"public\".\"Process\"";
            System.out.println(st);
            stat.executeUpdate(st);
            System.out.println(st + " ok!");
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
                if (null != conn) {
                    conn.close();
                    conn = null;
                }
            } catch (Exception _) {
            }
        }

    }

}
