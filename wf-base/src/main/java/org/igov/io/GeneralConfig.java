package org.igov.io;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * @author bw
 */
@Component("generalConfig")
public class GeneralConfig {

    private final static Logger LOG = LoggerFactory.getLogger(GeneralConfig.class);
    public static Boolean bTest = null;
    @Value("${general.sHost}")
    private String sHost; //general.sHost=https://test.region.igov.org.ua
    @Value("${general.sHostCentral}")
    private String sHostCentral; //general.sHost=https://test.igov.org.ua
    @Value("${general.bTest}")
    private String sbTest;
    @Value("${general.auth.login}")
    private String generalUsername;
    @Value("${general.auth.password}")
    private String generalPassword;

    @Value("${general.sURL_DocumentKvitanciiForIgov}")
    private String general_sURL_DocumentKvitanciiForIgov;
    @Value("${general.sURL_DocumentKvitanciiForAccounts}")
    private String general_sURL_DocumentKvitanciiForAccounts;
    @Value("${general.sURL_GenerationSID}")
    private String general_sURL_GenerationSID;
    @Value("${general.sURL_DocumentKvitanciiCallback}")
    private String general_sURL_DocumentKvitanciiCallback;
    @Value("${general.SID_login}")
    private String SID_login;
    @Value("${general.SID_password}")
    private String SID_password;
    @Value("${general.nID_Server}")
    private String nID_Server;

    @Value("${general.mail.sKey_Sender}")
    private String sKey_Sender;
    @Value("${general.mail.useUniSender}")
    private String useUniSender;
    @Value("${general.mail.nID_SendList_Unisender}")
    private String nID_SendList_Unisender;

    @Value("${BankID_sLogin}")
    private String sLogin_BankID;
    @Value("${BankID_sPassword}")
    private String sPassword_BankID;
    

    public String sLogin_BankID() {
        return sLogin_BankID;
    }

    public String sPassword_BankID() {
        return sPassword_BankID;
    }
    
    public String sHost() {
        //general.sHost=https://test-version.region.igov.org.ua    
        return sHost != null ? sHost : "https://test.region.igov.org.ua";
    }

    public String sHostCentral() {
        //general.sHost=https://test-version.region.igov.org.ua    
        return sHostCentral != null ? sHostCentral : "https://test.igov.org.ua";
    }

    public String sAuthLogin() {
        return generalUsername;
    }

    public String sAuthPassword() {
        return generalPassword;
    }

    public String sURL_DocumentKvitanciiForIgov() {
        return general_sURL_DocumentKvitanciiForIgov;
    }

    public String sURL_DocumentKvitanciiForAccounts() {
        return general_sURL_DocumentKvitanciiForAccounts;
    }

    public String sURL_GenerationSID() {
        return general_sURL_GenerationSID;
    }

    public String sURL_DocumentKvitanciiCallback() {
        return general_sURL_DocumentKvitanciiCallback;
    }

    public String getSID_login() {
        return SID_login;
    }

    public String getSID_password() {
        return SID_password;
    }

    public boolean bTest() {
        if (bTest != null) {
            return bTest;
        }
        boolean b = true;
        try {
            b = (sbTest == null ? b : sbTest.trim().length() > 0 ? !"false".equalsIgnoreCase(sbTest.trim()) : true);
            LOG.info("sbTest={}", sbTest);
        } catch (Exception oException) {
            LOG.error("Error: {}, sbTest={}", oException.getMessage(), sbTest);
        }
        bTest = b;
        return b;
    }
    public String sID_Order(Long nID_Protected) {
        return nID_Server()+"-"+nID_Protected;
    }
    public int nID_Server() {
        try {
            return Integer.parseInt(nID_Server);
        } catch (NumberFormatException ignored) {
            LOG.warn("Error: {}, can't parse nID_Server! nID_Server={}", ignored.getMessage(), nID_Server);
        }
        return 0;
    }


    public String getsKey_Sender() {
        return sKey_Sender != null ? sKey_Sender : "591335ic471gpqoc43dbtg6n7s1e8bchpbp4wdxa";
    }

    public String getUseUniSender() {
        return useUniSender;
    }

    public long getUniSenderListId() {

        try {
            return Integer.parseInt(nID_SendList_Unisender);
        } catch (NumberFormatException ignored) {
            LOG.warn("Error: {}, can't parse nID_SendList_Unisender! nID_SendList_Unisender={}", ignored.getMessage(), nID_SendList_Unisender);
        }
        return 5998742; //default list_id
    }
}
