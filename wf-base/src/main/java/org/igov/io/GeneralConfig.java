package org.igov.io;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import static org.igov.util.ToolLuna.getProtectedNumber;

/**
 * @author bw
 */
@Component("generalConfig")
public class GeneralConfig {

    private final static Logger LOG = LoggerFactory.getLogger(GeneralConfig.class);
    //public static Boolean bTest = null;
    
    @Value("${general.Self.bTest}")
    private String sbTest;
    @Value("${general.Self.nID_Server}")
    private String snID_Server;
    @Value("${general.Self.sHost}")
    private String sHost; //general.sHost=https://test.region.igov.org.ua
    @Value("${general.sHostCentral}")
    private String sHostCentral; //general.sHost=https://test.igov.org.ua/index#
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
    private String nID_SendList_UniSender_Mail;
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
    private String sURL_OTP_Send;
    @Value("${general.OTP.sID_Merchant}")
    private String sMerchantId_OTP_Send;
    @Value("${general.OTP.sPasswordMerchant}")
    private String sMerchantPassword_OTP_Send;
   
    
    @Value("${general.LiqPay.sURL_CheckOut}")
    private String sURL_CheckOut_LiqPay;


    public String sMsgURL() {
        return sURL_MSG_Monitor;
    }

    public String sMsgBusId() {
        return sBusinessId_MSG_Monitor;
    }

    public String sMsgTemplateMsgId() {
        return sTemplateId_MSG_Monitor;
    }

    public String sMsgLogin() {
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
        return sLogin_Auth;
    }

    public String sAuthPassword() {
        return sPassword_Auth;
    }

    
    public String getLogin_Auth_Receipt_PB_Bank() {
        return sLogin_Auth_Receipt_PB_Bank != null ? sLogin_Auth_Receipt_PB_Bank : "igov";
    }

    public String getPassword_Auth_Receipt_PB_Bank() {
        return sPassword_Auth_Receipt_PB_Bank != null ? sPassword_Auth_Receipt_PB_Bank : "igov-sess";
    }

    public String getURL_GenerateSID_Auth_Receipt_PB_Bank() {
        return sURL_GenerateSID_Auth_Receipt_PB_Bank != null ? sURL_GenerateSID_Auth_Receipt_PB_Bank : "https://auth-id.igov.org.ua/ChameleonServer/sessions/open";
    }

    public String sURL_DocumentKvitanciiForIgov() {
        return sURL_DocumentSimple_Receipt_PB_Bank;
    }

    public String sURL_DocumentKvitanciiForAccounts() {
        return sURL_DocumentByAccounts_Receipt_PB_Bank;
    }

    public String sURL_DocumentKvitanciiCallback() {
        return sURL_DocumentCallback_Receipt_PB_Bank;
    }
    
    
    public String getLogin_Auth_UkrDoc_SED() {
        return sLogin_Auth_UkrDoc_SED != null ? sLogin_Auth_UkrDoc_SED : "igov";
    }

    public String getPassword_Auth_UkrDoc_SED() {
        return sPassword_Auth_UkrDoc_SED != null ? sPassword_Auth_UkrDoc_SED : "igov-sess";
    }
    
    public String getURL_GenerateSID_Auth_UkrDoc_SED() {
        return sURL_GenerateSID_Auth_UkrDoc_SED != null ? sURL_GenerateSID_Auth_UkrDoc_SED : "https://auth-id.igov.org.ua/Stage/ChameleonServer/sessions/open";
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
        return sKey_UniSender_Mail != null ? sKey_UniSender_Mail : "591335ic471gpqoc43dbtg6n7s1e8bchpbp4wdxa";
    }

    public String getUseUniSender() {
        return sbEnable_UniSender_Mail;
    }

    public long getUniSenderListId() {

        try {
            return Integer.parseInt(nID_SendList_UniSender_Mail);
        } catch (NumberFormatException oException) {
            LOG.warn("can't parse nID_SendList_Unisender!: {} (nID_SendList_Unisender={})", oException.getMessage(), nID_SendList_UniSender_Mail);
        }
        return 5998742; //default list_id
    }

    public String getsUkrDocServerAddress() {
        return sURL_UkrDoc_SED != null ? sURL_UkrDoc_SED : "https://doc.stage.it.loc/docs";
    }

    public String getSnID_Server() {
        return snID_Server;
    }

    public String getsCorezoidUser() {
        return sUser_Corezoid_Exchange;
    }

    public String getsCorezoidSecretKey() {
        return sSecretKey_Corezoid_Exchange;
    }

    public String getURL_OTP_Send()  {
        return sURL_OTP_Send != null ? sURL_OTP_Send : "https://sms-inner.siteheart.com/api/otp_create_api.cgi";
    }
    public String getMerchantId_OTP_Send()  {
        return sMerchantId_OTP_Send;
    }
    public String getMerchantPassword_OTP_Send()  {
        return sMerchantPassword_OTP_Send;
    }
    
    public String getURL_UniSender_Mail() {
        return sURL_UniSender_Mail != null ? sURL_UniSender_Mail : "http://178.33.176.144/";
    }
    public String getContext_Subscribe_UniSender_Mail() {
        return sContext_Subscribe_UniSender_Mail != null ? sContext_Subscribe_UniSender_Mail : "/api/subscribe";
    }
    public String getContext_CreateMail_UniSender_Mail() {
        return sContext_CreateMail_UniSender_Mail != null ? sContext_CreateMail_UniSender_Mail : "/api/createEmailMessage";
    }
    public String getContext_CreateCompain_UniSender_Mail() {
        return sContext_CreateCompain_UniSender_Mail != null ? sContext_CreateCompain_UniSender_Mail : "/api/createCampaign";
    }

    public String getsURL_CheckOut() {
        return sURL_CheckOut_LiqPay != null ? sURL_CheckOut_LiqPay : "${general.LiqPay.sURL_CheckOut}";
    }

}
