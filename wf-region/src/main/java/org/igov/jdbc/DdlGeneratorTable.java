package org.igov.jdbc;

import java.io.*;
import java.sql.*;

public class DdlGeneratorTable {

    /*private static final String DB_USR = "dba";
    private static final String DB_PSWD = "D@taba$e1029";
    private static final String EX_PATH = "D:/";
    private static final String DB_PATH = "jdbc:sybase:Tds:89.28.203.14:5000/gComplain";*/
    
    
    private static final String DB_USR = "dba";
    private static final String DB_PSWD = "Databa$e1029";
    private static final String EX_PATH = "D:/";
    private static final String DB_PATH = "jdbc:sybase:Tds:52.209.80.165:5000/gComplain";

    private static Connection conn;
    private static Statement stat;
    //private static BufferedWriter bw;
    private static final PrintStream o = System.out;

    public static void main(String[] args) throws Exception {

        try {
            DriverManager.registerDriver(new com.sybase.jdbc2.jdbc.SybDriver());
            conn = DriverManager.getConnection(DB_PATH, DB_USR, DB_PSWD);
            stat = conn.createStatement();

            String query = new StringBuilder()
                    .append("select ")
                    .append("COMPLAIN.FIO, ")
                    .append("COMPLAIN.SEX, ")
                    .append("COMPLAIN.CITYCODE, ")
                    .append("CITY.CITYNAME, ")
                    .append("CITY.CITYTYPE, ")
                    .append("COMPLAIN.DISTRICTCODE, ")
                    .append("DISTRICT.DISTRICTNAME, ")
                    .append("COMPLAIN.STREETCODE, ")
                    .append("STREET.STREETNAME, ")
                    .append("STREET.STREETTYPE, ")
                    .append("COMPLAIN.HOME, ")
                    .append("COMPLAIN.CORPS, ")
                    .append("COMPLAIN.APARTMENT, ")
                    .append("COMPLAIN.TELEPHONE, ")
                    .append("COMPLAIN.EMAIL, ")
                    .append("COMPLAIN.REGDATE, ")
                    .append("COMPLAIN.REGNUMBER, ")
                    .append("COMPLAIN.CATEGORYCODE, ")
                    .append("CATEGORIES.CATEGORYNUMBER, ")
                    .append("CATEGORIES.CATEGORYNAME, ")
                    .append("COMPLAIN.SOCIALSTATUSCODE, ")
                    .append("SOCIALSTATUS.SOCIALSTATUSNUMBER, ")
                    .append("SOCIALSTATUS.SOCIALSTATUSNAME, ")
                    .append("COMPLAIN.INVOCSUBJECT, ")
                    .append("COMPLAIN.COLLECTIVESIGNCOUNT, ")
                    .append("COMPLAIN.INVOCFORM, ")
                    .append("COMPLAIN.INVOCCOUNT, ")
                    .append("COMPLAIN.INVOCTYPE, ")
                    .append("COMPLAIN.ISMAYOR, ")
                    .append("COMPLAIN.EXECCOMPLDATE, ")
                    .append("COMPLAIN.EXECCOMPLRESULT, ")
                    .append("COMPLAIN.PARENTORGANIZCODE, ")
                    .append("PARENTORGANIZATIONS.ORGANIZNAME, ")
                    .append("COMPLAIN.PARENTDOCNUMBER, ")
                    .append("COMPLAIN.PARENTDOCDATE, ")
                    .append("COMPLAIN.PARENTCONTROLDATE, ")
                    .append("COMPLAIN.PARENTEXECDATE, ")
                    .append("COMPLAIN.PARENTISPACKAGE, ")
                    .append("COMPLAIN.NOTE, ")
                    .append("WEBCOMPLAIN.IDENTITY as WEBCOMPLAINCODE ")
                    .append("from dba.COMPLAIN ")
                    .append("left outer join dba.CITY on CITY.IDENTITY = COMPLAIN.CITYCODE ")
                    .append("left outer join dba.DISTRICT on DISTRICT.IDENTITY = COMPLAIN.DISTRICTCODE ")
                    .append(",dba.STREET ")
                    .append("left outer join dba.CATEGORIES on CATEGORIES.IDENTITY = COMPLAIN.CATEGORYCODE ")
                    .append("left outer join dba.PARENTORGANIZATIONS on PARENTORGANIZATIONS.IDENTITY = COMPLAIN.PARENTORGANIZCODE ")
                    .append("left outer join dba.SOCIALSTATUS on SOCIALSTATUS.IDENTITY = COMPLAIN.SOCIALSTATUSCODE ")
                    .append("left outer join dba.WEBCOMPLAIN on WEBCOMPLAIN.COMPLAINCODE = 263857 and WEBCOMPLAIN.COMPLAINCODE = COMPLAIN.IDENTITY where STREET.IDENTITY = COMPLAIN.STREETCODE and COMPLAIN.IDENTITY ")
                    .append("= 263857 ").toString();
            for (ResultSet rs = stat.executeQuery(query.toString()); rs.next();) {
                //bw = new BufferedWriter(new FileWriter(EX_PATH + rs.getString(1) + ".sql"));
                System.out.println("!!!!!!!!!!!!!!!!!!!!!!" + rs.getString(1));
                //bw.flush();
                //bw.close();
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
                if (null != conn) {
                    conn.close();
                    conn = null;
                }
            } catch (Exception _) {
            }
        }
    }
}
