/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.igov.service;

/**
 *
 * @author olga
 */
public interface ArchiveService {
    
    static final String DB_USR = "dba";
    static final String DB_PSWD = "Databa$e1029";
    static final String EX_PATH = "D:/";
    static final String DB_PATH = "jdbc:sybase:Tds:52.17.200.205:5000/gComplain";

    
    static final String queryMinDate = new StringBuilder()
            .append("select min(co.REGDATE) as minREGDATE ")
            .append("from \"gComplain\".\"DBA\".\"COMPLAIN\" as co ")
            .append("where co.\"REGDATE\" > '%s'").toString();
    static final String queryListComplain = new StringBuilder()
            .append("select co.IDENTITY ")
            .append("from \"gComplain\".\"DBA\".\"COMPLAIN\" as co ")
            .append("where co.\"REGDATE\" = '%s' ")
            .append("order by co.\"REGDATE\" asc").toString();
    static final String queryUpdateComplain = new StringBuilder()
            .append("update \"gComplain\".\"DBA\".\"COMPLAIN\" co ")
            .append("set co.\"UPDATEDATE\" = '2017-01-01' ")
            .append("where co.\"IDENTITY\" = %s ").toString();
    static final String queryComplaim = new StringBuilder()
            .append("select co.IDENTITY, ")
            .append("co.FIO, ")
            .append("co.SEX, ")
            .append("co.CITYCODE, ")
            .append("ci.CITYNAME, ")
            .append("ci.CITYTYPE, ")
            .append("co.DISTRICTCODE, ")
            .append("di.DISTRICTNAME, ")
            .append("co.STREETCODE, ")
            .append("st.STREETNAME, ")
            .append("st.STREETTYPE, ")
            .append("co.HOME, ")
            .append("co.CORPS, ")
            .append("co.APARTMENT, ")
            .append("co.TELEPHONE, ")
            .append("co.EMAIL, ")
            .append("co.REGDATE, ")
            .append("co.REGNUMBER, ")
            .append("co.CATEGORYCODE, ")
            .append("ca.CATEGORYNUMBER, ")
            .append("ca.CATEGORYNAME, ")
            .append("co.SOCIALSTATUSCODE, ")
            .append("so.SOCIALSTATUSNUMBER, ")
            .append("so.SOCIALSTATUSNAME, ")
            .append("co.INVOCSUBJECT, ")
            .append("co.COLLECTIVESIGNCOUNT, ")
            .append("co.INVOCFORM, ")
            .append("co.INVOCCOUNT, ")
            .append("co.INVOCTYPE, ")
            .append("co.ISMAYOR, ")
            .append("co.EXECCOMPLDATE, ")
            .append("co.EXECCOMPLRESULT, ")
            .append("co.PARENTORGANIZCODE, ")
            .append("pa.ORGANIZNAME, ")
            .append("co.PARENTDOCNUMBER, ")
            .append("co.PARENTDOCDATE, ")
            .append("co.PARENTCONTROLDATE, ")
            .append("co.PARENTEXECDATE, ")
            .append("co.PARENTISPACKAGE, ")
            .append("co.NOTE, ")
            .append("we.IDENTITY ")
            .append("from \"gComplain\".\"DBA\".\"COMPLAIN\" as co ")
            .append("left outer join \"gComplain\".\"DBA\".\"CITY\" ci  on co.\"CITYCODE\" = ci.\"IDENTITY\" ")
            .append("left outer join \"gComplain\".\"DBA\".\"DISTRICT\" di on co.\"DISTRICTCODE\" = di.\"IDENTITY\" ")
            .append("left outer join \"gComplain\".\"DBA\".\"STREET\" st on co.\"STREETCODE\" = st.\"IDENTITY\" ")
            .append("left outer join \"gComplain\".\"DBA\".\"CATEGORIES\" ca on co.\"CATEGORYCODE\" = ca.\"IDENTITY\" ")
            .append("left outer join\"gComplain\".\"DBA\".\"PARENTORGANIZATIONS\" pa on co.\"PARENTORGANIZCODE\" = pa.\"IDENTITY\" ")
            .append("left outer join \"gComplain\".\"DBA\".\"SOCIALSTATUS\" so on co.\"SOCIALSTATUSCODE\" = so.\"IDENTITY\" ")
            .append("left outer join \"gComplain\".\"DBA\".\"WEBCOMPLAIN\" we on co.\"IDENTITY\" = we.\"COMPLAINCODE\" ")
            .append("where co.\"IDENTITY\" = %s").toString();
}
