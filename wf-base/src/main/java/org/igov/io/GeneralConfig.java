package org.igov.io;

import static org.igov.util.ToolLuna.getProtectedNumber;
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
    //public static Boolean bTest = null;
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
    
    @Value("${general.PB.sURL_AuthSID}")
    private String sURL_AuthSID_PB;
    
    @Value("${general.SID_login}")
    private String SID_login;
    @Value("${general.SID_password}")
    private String SID_password;
    @Value("${general.nID_Server}")
    private String snID_Server;

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
    
    @Value("${general.ukrdoc.sServerAddress}")
    private String sUkrDocServerAddress;

    public String sLogin_BankID() {
        return sLogin_BankID;
    }

    public String sPassword_BankID() {
        return sPassword_BankID;
    }
    
    public String sHost() {
        //general.sHost=https://test-version.region.igov.org.ua    
        //return sHost != null ? sHost : "https://test.region.igov.org.ua";
        return sHost;
    }

    public String sHostCentral() {
        //general.sHost=https://test-version.region.igov.org.ua    
        //return sHostCentral != null ? sHostCentral : "https://test.igov.org.ua";
        return sHostCentral;
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
        return general_sURL_GenerationSID != null ? general_sURL_GenerationSID : "https://auth-id.igov.org.ua/ChameleonServer/sessions/open";
    }

    public String sURL_AuthSID_PB() {
        return sURL_AuthSID_PB != null ? sURL_AuthSID_PB : "https://auth-id.igov.org.ua/Stage/ChameleonServer/sessions/open";
    }
    
    public String sURL_DocumentKvitanciiCallback() {
        return general_sURL_DocumentKvitanciiCallback;
    }

    public String getSID_login() {
        return SID_login != null ? SID_login : "igov";
    }

    public String getSID_password() {
        return SID_password != null ? SID_password : "igov-sess";
    }

    public boolean bTest() {
        /*if (bTest != null) {
            return bTest;
        }*/
        boolean b = true;
        try {
            b = (sbTest == null ? b : sbTest.trim().length() > 0 ? !"false".equalsIgnoreCase(sbTest.trim()) : true);
            LOG.info("(sbTest={})", sbTest);
        } catch (Exception oException) {
        	LOG.error("Bad: {} (sbTest={})", oException.getMessage(), sbTest);
        	LOG.debug("FAIL:", oException);
        }
        //bTest = b;
        return b;
    }
    public String sID_Order_ByOrder(Long nID_Order) {
        return sID_Order_ByOrder(nID_Server(), nID_Order);
    }
    public String sID_Order_ByOrder(Integer nID_Server, Long nID_Order) {
        return new StringBuilder(nID_Server+"").append("-").append(nID_Order).toString();
    }

    public String sID_Order_ByProcess(Long nID_Process) {
        return sID_Order_ByOrder(getProtectedNumber(nID_Process));
    }
    public String sID_Order_ByProcess(Integer nID_Server, Long nID_Process) {
        return GeneralConfig.this.sID_Order_ByOrder(nID_Server(), getProtectedNumber(nID_Process));
    }

    public int nID_Server() {
        Integer nID_Server=null;
        try {
            if(snID_Server==null){
                nID_Server = 0;
                throw new NumberFormatException("snID_Server="+snID_Server);
            }
            nID_Server=Integer.parseInt(snID_Server);
            if(nID_Server==null || nID_Server<0){
                nID_Server = 0;
                throw new NumberFormatException("nID_Server="+nID_Server);
            }
        } catch (NumberFormatException oNumberFormatException) {
            nID_Server = 0;
            LOG.warn("can't parse nID_Server: {} (nID_Server={})", oNumberFormatException.getMessage(), snID_Server);
        }
        return nID_Server;
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
        } catch (NumberFormatException oException) {
            LOG.warn("can't parse nID_SendList_Unisender!: {} (nID_SendList_Unisender={})", oException.getMessage(), nID_SendList_Unisender);
        }
        return 5998742; //default list_id
    }

	public String getsUkrDocServerAddress() {
		return sUkrDocServerAddress != null ? sUkrDocServerAddress : "https://doc.stage.it.loc/docs";
	}
    
}
