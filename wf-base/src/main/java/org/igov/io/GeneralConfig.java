package org.igov.io;

import java.util.HashMap;
import java.util.Map;
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

    @Value("${general.Self.bTest}")
    private String sbTest_Self;
    @Value("${general.Self.nID_Server}")
    private String snID_Server_Self;
    
    @Value("${general.Self.saServerReplace}")
    private String saServerReplace;
    private Map<Integer,Integer> mServerReplace = null;

    @Value("${general.Self.sHost}")
    private String sHost_Self;
    @Value("${general.sHostCentral}")
    private String sHostCentral_Self;
    @Value("${general.Auth.sLogin}")
    private String sLogin_Auth;
    @Value("${general.Auth.sPassword}")
    private String sPassword_Auth;

    @Value("${general.Bank.PB.Receipt.Auth.sLogin}")
    private String sLogin_Auth_Receipt_PB_Bank;
    @Value("${general.Bank.PB.Receipt.Auth.sPassword}")
    private String sPassword_Auth_Receipt_PB_Bank;
    @Value("${general.Bank.PB.Receipt.Auth.sURL_GenerateSID}")
    private String sURL_GenerateSID_Auth_Receipt_PB_Bank;
    @Value("${general.Bank.PB.Receipt.sURL_DocumentSimple}")
    private String sURL_DocumentSimple_Receipt_PB_Bank;
    @Value("${general.Bank.PB.Receipt.sURL_DocumentByAccounts}")
    private String sURL_DocumentByAccounts_Receipt_PB_Bank;
    @Value("${general.Bank.PB.Receipt.sURL_DocumentCallback}")
    private String sURL_DocumentCallback_Receipt_PB_Bank;
    
    @Value("${general.SED.UkrDoc.Auth.sLogin}")
    private String sLogin_Auth_UkrDoc_SED;
    @Value("${general.SED.UkrDoc.Auth.sPassword}")
    private String sPassword_Auth_UkrDoc_SED;
    @Value("${general.SED.UkrDoc.Auth.sURL_GenerateSID}")
    private String sURL_GenerateSID_Auth_UkrDoc_SED;
    @Value("${general.SED.UkrDoc.sURL}")
    private String sURL_UkrDoc_SED;
    
    @Value("${general.Mail.UniSender.bEnable}")
    private String sbEnable_UniSender_Mail;
    @Value("${general.Mail.UniSender.sKeyAPI}")
    private String sKey_UniSender_Mail;
    @Value("${general.Mail.UniSender.nID_SendList}")
    private String snID_SendList_UniSender_Mail;
    @Value("${general.Mail.UniSender.sURL}")
    private String sURL_UniSender_Mail;
    @Value("${general.Mail.UniSender.sContext_Subscribe}")
    private String sContext_Subscribe_UniSender_Mail;
    @Value("${general.Mail.UniSender.sContext_CreateMail}")
    private String sContext_CreateMail_UniSender_Mail;
    @Value("${general.Mail.UniSender.sContext_CreateCompain}")
    private String sContext_CreateCompain_UniSender_Mail;
    
    @Value("${general.Auth.BankID.PB.sLogin}")
    private String sLogin_BankID_PB_Auth;
    @Value("${general.Auth.BankID.PB.sPassword}")
    private String sPassword_BankID_PB_Auth;
    
    @Value("${general.Auth.BankID.PB.AccessToken.sHost}")
    private String sHost_AccessToken_BankID_PB_Auth;
    @Value("${general.Auth.BankID.PB.AccessToken.sPath}")
    private String sPath_AccessToken_BankID_PB_Auth;
    @Value("${general.Auth.BankID.PB.Authorize.sHost}")
    private String sHost_Authorize_BankID_PB_Auth;
    @Value("${general.Auth.BankID.PB.Authorize.sPath}")
    private String sPath_Authorize_BankID_PB_Auth;
    @Value("${general.Auth.BankID.PB.sURL_ResourceSignature}")
    private String sURL_ResourceSignature_BankID_PB_Auth;
    
    
    @Value("${general.Exchange.Corezoid.sID_User}")
    private String sUser_Corezoid_Exchange;
    @Value("${general.Exchange.Corezoid.sSecretKey}")
    private String sSecretKey_Corezoid_Exchange;

    // MSG Properties
    @Value("${general.Monitor.MSG.sURL}")
    private String sURL_MSG_Monitor;
    @Value("${general.Monitor.MSG.sBusinessId}")
    private String sBusinessId_MSG_Monitor;
    @Value("${general.Monitor.MSG.sTemplateId}")
    private String sTemplateId_MSG_Monitor;
    @Value("${general.Monitor.MSG.sLogin}")
    private String sLogin_MSG_Monitor;
    
    @Value("${general.OTP.sURL_Send}")
    private String sURL_Send_OTP;
    @Value("${general.OTP.sMerchantId}")
    private String sMerchantId_OTP;
    @Value("${general.OTP.sMerchantPassword}")
    private String sMerchantPassword_OTP;
    
    @Value("${general.LiqPay.sURL_CheckOut}")
    private String sURL_CheckOut_LiqPay;
    
    @Value("${general.SMS.sURL_Send}")
    private String sURL_Send_SMS;
    @Value("${general.SMS.sMerchantId}")
    private String sMerchantId_SMS;
    @Value("${general.SMS.sMerchantPassword}")
    private String sMerchantPassword_SMS;
    
    
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
    
    @Value("${general.SID_login2}")
    private String SID_login2;
    @Value("${general.SID_password2}")
    private String SID_password2;
    
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

    @Value("${general.corezoid.sID_User}")
    private String sCorezoidUser;
    @Value("${general.corezoid.sSecretKey}")
    private String sCorezoidSecretKey;

    
    
    public boolean isSelfTest() {
        boolean b = true;
        try {
            b = (sbTest_Self == null ? b : Boolean.valueOf(sbTest_Self));
            //LOG.info("(sbTest={})", sbTest_Self);
        } catch (Exception oException) {
            LOG.error("Bad: {} (sbTest={})", oException.getMessage(), sbTest_Self);
            LOG.debug("FAIL:", oException);
        }
        return b;
    }
    
    public String getURL_MSG_Monitor() {
        return sURL_MSG_Monitor;
    }
    public String getBusinessId_MSG_Monitor() {
        return sBusinessId_MSG_Monitor;
    }
    public String getTemplateId_MSG_Monitor() {
        return sTemplateId_MSG_Monitor;
    }
    public String getLogin_MSG_Monitor() {
        return sLogin_MSG_Monitor;
    }
        
    public String getLogin_BankID_PB_Auth() {
        return sLogin_BankID_PB_Auth;
    }
    public String getPassword_BankID_PB_Auth() {
        return sPassword_BankID_PB_Auth;
    }
    public String getHost_AccessToken_BankID_PB_Auth() {
        return sHost_AccessToken_BankID_PB_Auth != null ? sHost_AccessToken_BankID_PB_Auth : "bankid.privatbank.ua";
    }
    public String getPath_AccessToken_BankID_PB_Auth() {
        return sPath_AccessToken_BankID_PB_Auth != null ? sPath_AccessToken_BankID_PB_Auth : "/DataAccessService/oauth/token";
    }
    public String getHost_Authorize_BankID_PB_Auth() {
        return sHost_Authorize_BankID_PB_Auth != null ? sHost_Authorize_BankID_PB_Auth : "bankid.privatbank.ua";
    }
    public String getPath_Authorize_BankID_PB_Auth() {
        return sPath_Authorize_BankID_PB_Auth != null ? sPath_Authorize_BankID_PB_Auth : "/DataAccessService/das/authorize";
    }
    public String getURL_ResourceSignature_BankID_PB_Auth() {
        return sURL_ResourceSignature_BankID_PB_Auth != null ? sURL_ResourceSignature_BankID_PB_Auth : "https://bankid.privatbank.ua/ResourceService/checked/signatureData";
    }
    
    public String getSelfHost() {
        return sHost_Self;
    }
    public String getSelfHostCentral() {
        return sHostCentral_Self;
    }
    public String getAuthLogin() {
        return sLogin_Auth;
    }
    public String getAuthPassword() {
        return sPassword_Auth;
    }
    
    public String getLogin_Auth_Receipt_PB_Bank() {
        return sLogin_Auth_Receipt_PB_Bank;
    }
    public String getPassword_Auth_Receipt_PB_Bank() {
        return sPassword_Auth_Receipt_PB_Bank;
    }
    public String getURL_GenerateSID_Auth_Receipt_PB_Bank() {
        return sURL_GenerateSID_Auth_Receipt_PB_Bank;
    }
    public String getURL_DocumentSimple_Receipt_PB_Bank() {
        return sURL_DocumentSimple_Receipt_PB_Bank;
    }
    public String getURL_DocumentByAccounts_Receipt_PB_Bank() {
        return sURL_DocumentByAccounts_Receipt_PB_Bank;
    }
    public String getURL_DocumentCallback_Receipt_PB_Bank() {
        return sURL_DocumentCallback_Receipt_PB_Bank;
    }
    
    public String getLogin_Auth_UkrDoc_SED() {
        return sLogin_Auth_UkrDoc_SED;
    }
    public String getPassword_Auth_UkrDoc_SED() {
        return sPassword_Auth_UkrDoc_SED;
    }
    public String getURL_GenerateSID_Auth_UkrDoc_SED() {
        return sURL_GenerateSID_Auth_UkrDoc_SED;
    }
    public String getURL_UkrDoc_SED() {
        return sURL_UkrDoc_SED;
    }
    
    public String getUser_Coreziod_Exchange() {
        return sUser_Corezoid_Exchange;
    }
    public String getSecretKey_Coreziod_Exchange() {
        return sSecretKey_Corezoid_Exchange;
    }

    public String getURL_Send_OTP()  {
        return sURL_Send_OTP;
    }
    public String getMerchantId_OTP()  {
        return sMerchantId_OTP;
    }
    public String getMerchantPassword_OTP()  {
        return sMerchantPassword_OTP;
    }

    public String getURL_Send_SMS()  {
        return sURL_Send_SMS;
    }
    public String getMerchantId_SMS()  {
        return sMerchantId_SMS;
    }
    public String getMerchantPassword_SMS()  {
        return sMerchantPassword_SMS;
    }
    
    public Boolean isEnable_UniSender_Mail() {
        return Boolean.valueOf(sbEnable_UniSender_Mail);
    }
    public String getURL_UniSender_Mail() {
        return sURL_UniSender_Mail;
    }
    public String getContext_Subscribe_UniSender_Mail() {
        return sContext_Subscribe_UniSender_Mail;
    }
    public String getContext_CreateMail_UniSender_Mail() {
        return sContext_CreateMail_UniSender_Mail;
    }
    public String getContext_CreateCompain_UniSender_Mail() {
        return sContext_CreateCompain_UniSender_Mail;
    }
    public String getKey_UniSender_Mail() {
        return sKey_UniSender_Mail;
    }
    public long getSendListId_UniSender_Mail() {
        try {
            return Integer.valueOf(snID_SendList_UniSender_Mail);
        } catch (NumberFormatException oException) {
            LOG.warn("can't parse nID_SendList_Unisender!: {} (nID_SendList_Unisender={})", oException.getMessage(), snID_SendList_UniSender_Mail);
            return 5998742; //default list_id
        }
    }
    
    public String getURL_CheckOut_LiqPay() {
        return sURL_CheckOut_LiqPay;
    }

    
    public Integer getServerId(Integer nID_Server) {
        if(mServerReplace==null){
            mServerReplace=new HashMap();
            if(saServerReplace!=null && !"".equals(saServerReplace.trim())){
                String saServerReplace_Trimed =  saServerReplace.trim();
                for(String sServerReplace : saServerReplace_Trimed.split("\\,")){
                //  //mServerReplace.put(nID_Server, nID_Server)
                    if(sServerReplace!=null && !"".equals(sServerReplace.trim())){
                        sServerReplace =  sServerReplace.trim();

                    }
                    int n=0;
                    Integer nAt=null;
                    Integer nTo=null;
                    for(String s : sServerReplace.split("\\>")){
                        if(n==0){
                            nAt = Integer.valueOf(s);
                        }
                        if(n==1){
                            nTo = Integer.valueOf(s);
                        }
                    //  //mServerReplace.put(nID_Server, nID_Server)
                        n++;
                    }
                    if(nAt!=null&&nTo!=null){
                        mServerReplace.put(nAt, nTo);
                    }
                }
            }
        }
        LOG.info("nID_Server={}, mServerReplace={}", nID_Server, mServerReplace);
        Integer nID_Server_Return = nID_Server;
        if(mServerReplace!=null&&!mServerReplace.isEmpty()&&nID_Server!=null){
            nID_Server_Return = mServerReplace.get(nID_Server);
            if(nID_Server_Return==null){
                nID_Server_Return = nID_Server;
            }
        }
        return nID_Server_Return;
        //saServerReplace
        //saServerReplace=0>5,1>4    
    }
    //private String saServerReplace;
    
    
    public Integer getSelfServerId() {
        Integer nID_Server = null;
        try {
            if (snID_Server_Self == null) {
                nID_Server = 0;
                throw new NumberFormatException("snID_Server=" + snID_Server_Self);
            }
            nID_Server = Integer.valueOf(snID_Server_Self);
            if (nID_Server == null || nID_Server < 0) {
                nID_Server = 0;
                throw new NumberFormatException("nID_Server=" + nID_Server);
            }
        } catch (NumberFormatException oNumberFormatException) {
            nID_Server = 0;
            LOG.warn("can't parse nID_Server: {} (nID_Server={})", oNumberFormatException.getMessage(), snID_Server_Self);
        }
        return nID_Server;
    }
    public String getOrderId_ByOrder(Long nID_Order) {
        return getOrderId_ByOrder(getSelfServerId(), nID_Order);
    }
    public String getOrderId_ByOrder(Integer nID_Server, Long nID_Order) {
        return new StringBuilder(nID_Server + "").append("-").append(nID_Order).toString();
    }
    public String getOrderId_ByProcess(Long nID_Process) {
        return GeneralConfig.this.getOrderId_ByOrder(getProtectedNumber(nID_Process));
    }
    public String getOrderId_ByProcess(Integer nID_Server, Long nID_Process) {
        return getOrderId_ByOrder(getSelfServerId(), getProtectedNumber(nID_Process));
    }    
    
    
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

    public String getSID_login2() {
        return SID_login2 != null ? SID_login2 : "igov";
    }

    public String getSID_password2() {
        return SID_password2 != null ? SID_password2 : "igov-sess";
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
        return new StringBuilder(nID_Server + "").append("-").append(nID_Order).toString();
    }

    public String sID_Order_ByProcess(Long nID_Process) {
        return sID_Order_ByOrder(getProtectedNumber(nID_Process));
    }

    public String sID_Order_ByProcess(Integer nID_Server, Long nID_Process) {
        return GeneralConfig.this.sID_Order_ByOrder(nID_Server(), getProtectedNumber(nID_Process));
    }

    public int nID_Server() {
        Integer nID_Server = null;
        try {
            if (snID_Server == null) {
                nID_Server = 0;
                throw new NumberFormatException("snID_Server=" + snID_Server);
            }
            nID_Server = Integer.parseInt(snID_Server);
            if (nID_Server == null || nID_Server < 0) {
                nID_Server = 0;
                throw new NumberFormatException("nID_Server=" + nID_Server);
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

    public String getSnID_Server() {
        return snID_Server;
    }

    public String getsCorezoidUser() {
        return sCorezoidUser;
    }

    public String getsCorezoidSecretKey() {
        return sCorezoidSecretKey;
    }

}
