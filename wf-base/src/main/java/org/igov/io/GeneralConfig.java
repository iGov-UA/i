package org.igov.io;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

import static org.igov.util.ToolLuna.getProtectedNumber;

/**
 * @author bw
 */
@Component("generalConfig")
@Scope("prototype")
public class GeneralConfig {

    private final static Logger LOG = LoggerFactory.getLogger(GeneralConfig.class);

    @Value("${general.Self.bTest}")
    private String sbTest_Self;
    @Value("${general.Self.nID_Server}")
    private String snID_Server_Self;

    @Value("${general.Self.saServerReplace}")
    private String saServerReplace;
    private Map<Integer, Integer> mServerReplace = null;

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

    @Value("${general.Exchange.Corezoid.Gorsovet.sID_User}")
    private String sUser_Corezoid_Gorsovet_Exchange;
    @Value("${general.Exchange.Corezoid.Gorsovet.sSecretKey}")
    private String sSecretKey_Corezoid_Gorsovet_Exchange;

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

    @Value("${general.SMS.PB.Auth.sLogin}")
    private String sLogin_Auth_PB_SMS;
    @Value("${general.SMS.PB.Auth.sPassword}")
    private String sPassword_Auth_PB_SMS;
    @Value("${general.SMS.PB.Auth.sURL_GenerateSID}")
    private String sURL_GenerateSID_Auth_PB_SMS;

    @Value("${general.SMS.sURL_Send}")
    private String sURL_Send_SMS;
    @Value("${general.SMS.sURL_SendNew}")
    private String sURL_Send_SMSNew;
    @Value("${general.SMS.sMerchantId}")
    private String sMerchantId_SMS;
    @Value("${general.SMS.sMerchantPassword}")
    private String sMerchantPassword_SMS;
    @Value("${general.SMS.nID_Shema}")
    private String snID_Shema;
    @Value("${general.SMS.lifeURL}")
    private String lifeURL;
    @Value("${general.SMS.lifeLogin}")
    private String lifeLogin;
    @Value("${general.SMS.lifePassword}")
    private String lifePassword;

    @Value("${general.LiqPay.sURL_CheckOut}")
    private String sURL_CheckOut_LiqPay;
    @Value("${general.LiqPay.bTest}")
    private String sbTest_LiqPay;

    @Value("${general.queue.cherg.sURL}")
    private String queueManagementSystemAddress;
    @Value("${general.queue.cherg.sLogin}")
    private String queueManagementSystemLogin;
    @Value("${general.queue.cherg.sPassword}")
    private String queueManagementSystemPassword;

    @Value("${general.Pay.Yuzhny.FTP.sHost}")
    private String sHost_Pay_Yuzhny_FTP;
    @Value("${general.Pay.Yuzhny.FTP.nPort}")
    private String nPort_Pay_Yuzhny_FTP;
    @Value("${general.Pay.Yuzhny.FTP.sLogin}")
    private String sLogin_Pay_Yuzhny_FTP;
    @Value("${general.Pay.Yuzhny.FTP.sPassword}")
    private String sPassword_Pay_Yuzhny_FTP;
    //@Value("${general.Pay.Yuzhny.FTP.sPathFileName}")
    //private String sPathFileName_Pay_Yuzhny_FTP;
    @Value("${general.Pay.Yuzhny.FTP.sFileNameMask}")
    private String sFileNameMask_Pay_Yuzhny_FTP;
    @Value("${general.Pay.Yuzhny.FTP.sPath}")
    private String sPath_Pay_Yuzhny_FTP;
    @Value("${general.Pay.Yuzhny.FTP.sSuffixDateMask}")
    private String sSuffixDateMask_Pay_Yuzhny_FTP;
    @Value("${general.Pay.Yuzhny.FTP.nDaysOffset}")
    private String snDaysOffset_Pay_Yuzhny_FTP;

    @Value("${general.feedbackCountLimit}")
    private String feedbackCountLimit;

    @Value("${general.Escalation.bTest}")
    private String sbTest_Escalation;

    @Value("${general.DFS.sURL}")
    private String sURL_DFS;

    @Value("${general.Object.SubPlace.PB.Auth.sLogin}")
    private String sObjectSubPlace_Auth_sLogin;

    @Value("${general.Object.SubPlace.PB.Auth.sPassword}")
    private String sObjectSubPlace_Auth_sPassword;

    @Value("${general.Object.SubPlace.PB.Auth.sURL_GenerateSID}")
    private String sObjectSubPlace_Auth_sURL_GenerateSID;

    @Value("${general.Object.SubPlace.sURL_Send}")
    private String sObjectSubPlace_sURL_Send;

    @Value("${general.ECP.Self.sSubPathFile}")
    private String sECPKeystoreFilename;

    @Value("${general.ECP.Self.sPassword}")
    private String sECPKeystorePasswd;

    @Value("${general.ECP.SelfCert.sSubPathFile}")
    private String sECPSelfCertPathFile;

    @Value("${general.ECP.SelfCertEncrypt.sSubPathFile}")
    private String sECPSelfCertEncryptPathFile;

    @Value("${general.Export.Agroholding.sURL}")
    private String sURL_Agroholding;
    @Value("${general.Export.Agroholding.Auth.sLogin}")
    private String sLogin_Auth_Agroholding;
    @Value("${general.Export.Agroholding.Auth.sPassword}")
    private String sPassword_Auth_Agroholding;

    public String getObjectSubPlace_Auth_sLogin() {
        return sObjectSubPlace_Auth_sLogin;
    }

    public String getObjectSubPlace_Auth_sPassword() {
        return sObjectSubPlace_Auth_sPassword;
    }

    public String getObjectSubPlace_Auth_sURL_GenerateSID() {
        return sObjectSubPlace_Auth_sURL_GenerateSID;
    }

    public String getObjectSubPlace_sURL_Send() {
        return sObjectSubPlace_sURL_Send;
    }

    public String getsECPKeystoreFilename() {
        return sECPKeystoreFilename;
    }

    public String getsECPKeystorePasswd() {
        return sECPKeystorePasswd;
    }

    public String getsECPSelfCertPathFile() {
        return sECPSelfCertPathFile;
    }

    public String getsECPSelfCertEncryptPathFile() {
        return sECPSelfCertEncryptPathFile;
    }

    public boolean isSelfTest() {
        boolean b = true;
        try {
            b = (sbTest_Self == null ? b : Boolean.valueOf(sbTest_Self));
            //LOG.info("(sbTest_Self={})", sbTest_Self);
        } catch (Exception oException) {
            LOG.error("Bad: {} (sbTest={})", oException.getMessage(), sbTest_Self);
            LOG.debug("FAIL:", oException);
        }
        return b;
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

    public String getURL_Send_OTP() {
        return sURL_Send_OTP;
    }

    public String getMerchantId_OTP() {
        return sMerchantId_OTP;
    }

    public String getMerchantPassword_OTP() {
        return sMerchantPassword_OTP;
    }

    public String getLogin_Auth_PB_SMS() {
        return sLogin_Auth_PB_SMS;
    }

    public String getPassword_Auth_PB_SMS() {
        return sPassword_Auth_PB_SMS;
    }

    public String getURL_GenerateSID_Auth_PB_SMS() {
        return sURL_GenerateSID_Auth_PB_SMS;
    }

    public String getURL_Send_SMS() {
        return sURL_Send_SMS;
    }

    public String getURL_Send_SMSNew() {
        return sURL_Send_SMSNew;
    }

    public String getMerchantId_SMS() {
        return sMerchantId_SMS;
    }

    public String getMerchantPassword_SMS() {
        return sMerchantPassword_SMS;
    }

    public String getChemaId() {
        return snID_Shema;
    }

    public String getLifeURL() {
        return lifeURL;
    }

    public String getLifeLogin() {
        return lifeLogin;
    }

    public String getLifePassword() {
        return lifePassword;
    }

    public String getsURL_DFS() {
        return sURL_DFS;
    }

    public Boolean isEnable_UniSender_Mail() {
        return Boolean.valueOf(sbEnable_UniSender_Mail);
    }

    public String getURL_UniSender_Mail() {
        return sURL_UniSender_Mail;
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

    public boolean isTest_LiqPay() {
        boolean b = true;
        try {
            b = (sbTest_LiqPay == null ? b : Boolean.valueOf(sbTest_LiqPay));
            //LOG.info("(sbTest_LiqPay={})", sbTest_LiqPay);
        } catch (Exception oException) {
            LOG.error("Bad: {} (sbTest_LiqPay={})", oException.getMessage(), sbTest_LiqPay);
            LOG.debug("FAIL:", oException);
        }
        return b;
    }

    public Integer getServerId(Integer nID_Server) {
        if (mServerReplace == null) {
            mServerReplace = new HashMap();
            if (saServerReplace != null && !"".equals(saServerReplace.trim())) {
                String saServerReplace_Trimed = saServerReplace.trim();
                for (String sServerReplace : saServerReplace_Trimed.split("\\,")) {
                    //  //mServerReplace.put(nID_Server, nID_Server)
                    if (sServerReplace != null && !"".equals(sServerReplace.trim())) {
                        sServerReplace = sServerReplace.trim();

                    }
                    int n = 0;
                    Integer nAt = null;
                    Integer nTo = null;
                    for (String s : sServerReplace.split("\\>")) {
                        if (n == 0) {
                            nAt = Integer.valueOf(s);
                        }
                        if (n == 1) {
                            nTo = Integer.valueOf(s);
                        }
                        //  //mServerReplace.put(nID_Server, nID_Server)
                        n++;
                    }
                    if (nAt != null && nTo != null) {
                        mServerReplace.put(nAt, nTo);
                    }
                }
            }
        }
        //System.out.println("mServerReplace: " + mServerReplace);
        LOG.info("nID_Server={}, mServerReplace={}", nID_Server, mServerReplace);
        Integer nID_Server_Return = nID_Server;
        if (mServerReplace != null && !mServerReplace.isEmpty() && nID_Server != null) {
            nID_Server_Return = mServerReplace.get(nID_Server);
            if (nID_Server_Return == null) {
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

    public Long getOrderId_ByProcess(String snID_Process) {
        if (snID_Process == null) {
            return null;
        }
        Long nID_Process = null;
        try {
            nID_Process = Long.valueOf(snID_Process);
        } catch (Exception oException) {
            LOG.warn(oException.getMessage());
        }
        return nID_Process;
    }

    @PostConstruct
    // Вывод всех переменных с аннотацией @Value
    private void init() {

        LOG.info("-------   Установки конфигурации -------------------");
        for (Field f : this.getClass().getDeclaredFields()) {

            for (Annotation an : f.getDeclaredAnnotations()) {
                Class<? extends Annotation> ant = an.annotationType();
                if ("org.springframework.beans.factory.annotation.Value".equals(ant.getName())) {
                    try {
                        LOG.info("{} = {}", f.getName(), f.get(this));
                    } catch (IllegalArgumentException e) {
                        e.printStackTrace();
                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                    }
                    break;
                }
            }
        }
        LOG.info("----------------------------------------------------");

    }

    public String getQueueManagementSystemAddress() {
        return queueManagementSystemAddress;
    }

    public String getQueueManagementSystemLogin() {
        return queueManagementSystemLogin;
    }

    public String getQueueManagementSystemPassword() {
        return queueManagementSystemPassword;
    }

    public String getHost_FTP_Yuzhny_Pay() {
        return sHost_Pay_Yuzhny_FTP;
    }

    public String getPort_FTP_Yuzhny_Pay() {
        return nPort_Pay_Yuzhny_FTP;
    }

    public String getLogin_FTP_Yuzhny_Pay() {
        return sLogin_Pay_Yuzhny_FTP;
    }

    public String getPassword_FTP_Yuzhny_Pay() {
        return sPassword_Pay_Yuzhny_FTP;
    }
    //public String getPathFileName_FTP_Yuzhny_Pay() {
    //	return sPathFileName_Pay_Yuzhny_FTP;
    //}

    public String getFileNameMask_FTP_Yuzhny_Pay() {
        return sFileNameMask_Pay_Yuzhny_FTP;
    }

    public String getPath_FTP_Yuzhny_Pay() {
        return sPath_Pay_Yuzhny_FTP;
    }

    public String getSuffixDateMask_FTP_Yuzhny_Pay() {
        return sSuffixDateMask_Pay_Yuzhny_FTP;
    }

    public Integer getDaysOffset_FTP_Yuzhny_Pay() {
        return Integer.valueOf(snDaysOffset_Pay_Yuzhny_FTP);
    }

    public Long getFeedbackCountLimit() {
        return Long.valueOf(feedbackCountLimit);
    }

    public boolean isFeedbackCountExpired(Long feedbackCount) {
        return feedbackCount > getFeedbackCountLimit();
    }

    public String getsUser_Corezoid_Gorsovet_Exchange() {
        return sUser_Corezoid_Gorsovet_Exchange;
    }

    public void setsUser_Corezoid_Gorsovet_Exchange(String sUser_Corezoid_Gorsovet_Exchange) {
        this.sUser_Corezoid_Gorsovet_Exchange = sUser_Corezoid_Gorsovet_Exchange;
    }

    public String getsSecretKey_Corezoid_Gorsovet_Exchange() {
        return sSecretKey_Corezoid_Gorsovet_Exchange;
    }

    public void setsSecretKey_Corezoid_Gorsovet_Exchange(String sSecretKey_Corezoid_Gorsovet_Exchange) {
        this.sSecretKey_Corezoid_Gorsovet_Exchange = sSecretKey_Corezoid_Gorsovet_Exchange;
    }

    public boolean isTest_Escalation() {
        boolean b = true;
        try {
            b = (sbTest_Escalation == null ? b : Boolean.valueOf(sbTest_Escalation));
            //LOG.info("(sbTest_LiqPay={})", sbTest_LiqPay);
        } catch (Exception oException) {
            LOG.error("Bad: {} (sbTest_Escalation={})", oException.getMessage(), sbTest_Escalation);
            LOG.debug("FAIL:", oException);
        }
        return b;
    }

    public String getsURL_Agroholding() {
        return sURL_Agroholding;
    }

    public String getsLogin_Auth_Agroholding() {
        return sLogin_Auth_Agroholding;
    }

    public String getsPassword_Auth_Agroholding() {
        return sPassword_Auth_Agroholding;
    }

}
