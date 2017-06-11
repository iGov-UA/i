package org.igov.service.business.finance;

import java.io.BufferedReader;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import org.apache.commons.codec.binary.Base64;
import org.igov.io.GeneralConfig;
import org.igov.io.mail.Mail;
import org.igov.service.business.access.AccessKeyService;
import org.igov.service.business.object.Language;
import org.igov.service.controller.security.AccessContract;
import org.igov.service.controller.security.AuthenticationTokenSelector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.context.ApplicationContext;
import static org.igov.service.business.finance.LiqpayService.TASK_MARK;

/**
 * @author kyriss-ig
 */
@Service
public class FinanceService {

    private static final Logger LOG = LoggerFactory.getLogger(FinanceService.class);
    private static final String ESCALATION_MAILBOXES = "bvv4ik@gmail.com,dmitrij.zabrudskij@privatbank.ua";

    @Autowired
    AccessKeyService accessCover;
    @Autowired
    GeneralConfig generalConfig;
    @Autowired
    Liqpay oLiqPuy;

    @Autowired
    private ApplicationContext context;
    @Autowired
    private LiqpayService oLiqpayService;

    public String setPaymentStatus_TaskActiviti( 
            String sID_Order,
            String sID_PaymentSystem,
            String sData,
            String sPrefix,
            String sPaySystemData,
            String sPaySystemSignature,
            HttpServletRequest request)
            throws Exception {

        //формируем и выполняем запрос к платежной системе
        //читаем ответ
        //если не получилось, формируем письмо
        //отправляем его на ESCALATION_MAILBOXES
        
        if (sPrefix == null) {
            sPrefix = "";
        }

        String URI = request.getRequestURI() + "?" + request.getQueryString();
        //LOG.info("/setPaymentStatus_TaskActiviti");

        LOG.info("(sID_Order=>>>>>>>>>>>>{})", sID_Order);
        LOG.info("(sID_PaymentSystem=>>>>>>>>>>>>{})", sID_PaymentSystem);
        LOG.info("(sData=>>>>>>>>>>>>>)", sData);
        LOG.info("(sPrefix={}>>>>>>>>>>>>)", sPrefix);

        LOG.info("(data={}>>>>>>>>>>>>>>>)", sPaySystemData);
        LOG.info("(signature={}>>>>>>>>>>>)", sPaySystemSignature);
        LOG.info("(URI={}>>>>>>>>>>.)", URI);
        String sDataDecoded = null;
        try {
            StringBuilder osRequestBody = new StringBuilder("");
            BufferedReader oReader = request.getReader();
            String line;
            if (oReader != null) {
                while ((line = oReader.readLine()) != null) {
                    osRequestBody.append(line);
                }
            }
            request.getReader();
            sPaySystemData = parseData(osRequestBody.toString());
            if (sPaySystemData != null) {
                sDataDecoded = new String(Base64.decodeBase64(sPaySystemData.getBytes()));
                int index = sDataDecoded.indexOf("}");
                if (index >= 0) {
                    sDataDecoded = sDataDecoded.substring(0, index + 1);
                }
                LOG.info("(sDataDecoded={})", sDataDecoded);
            }
            oLiqpayService.setPaymentStatus(sID_Order, sDataDecoded, sID_PaymentSystem, sPrefix);
        } catch (Exception oException) {
            LOG.error("FAIL:", oException);
            String snID_Subject = "0";
            String sAccessKey = null;
            try {
                //sAccessKey = accessDataDao.setAccessData(URI);
                sAccessKey = accessCover.getAccessKey(URI);
            } catch (Exception oException1) {
                LOG.error("FAIL:sAccessKey=", oException1);
            }

            //generalConfig.getSelfHost() + "/wf/service/finance/setPaymentStatus_TaskActiviti_Direct?sID_Order="+sID_Order+"&sID_PaymentSystem="+sID_PaymentSystem+"&sData=&sID_Transaction=&sStatus_Payment="
            String sURL = new StringBuilder(generalConfig.getSelfHost())
                    .append("/wf/service/finance/setPaymentStatus_TaskActiviti_Direct?")
                    .append("sID_Order=").append(sID_Order)
                    .append("&sID_PaymentSystem=").append(sID_PaymentSystem)
                    .append("&sData=").append("")
                    .append("&nID_Subject=").append(snID_Subject)
                    //.append("sAccessContract=").append("Request")
                    //.append("&sAccessKey=").append(sAccessKey)
                    .append("&").append(AuthenticationTokenSelector.ACCESS_CONTRACT).append("=")
                    .append(AccessContract.Request.name())
                    .append("&").append(AuthenticationTokenSelector.ACCESS_KEY).append("=").append(sAccessKey)
                    .toString();

            String sFormHTML = new StringBuilder()
                    .append("<form method=\"GET\" action=\"")//POST
                    .append(sURL)
                    .append("\" ")
                    .append("accept-charset=\"utf-8\">")
                    .append("<input type=\"text\" name=\"sID_Transaction\" value=\"\"/>")
                    .append("<input type=\"text\" name=\"sStatus_Payment\" value=\"\"/>")
                    .append("<input type=\"submit\" value=\"��������� ������ ����������!\"/>")
                    .append("</form>").toString();

            String sHead = (generalConfig.isSelfTest() ? "(test)" : "(PROD)")
                    + "/finance/setPaymentStatus_TaskActiviti:������ ��� ������� �������� ��������� ���������� � ������� � ��������-������!";
            String sBody = "oException.getMessage()=" + oException.getMessage() + "<br>"
                    + "<br>"
                    + "sID_Order=" + sID_Order + "<br>"
                    + "sID_PaymentSystem=" + sID_PaymentSystem + "<br>"
                    + "sData=" + sData + "<br>"
                    + "data=" + sPaySystemData + "<br>"
                    + "signature=" + sPaySystemSignature + "<br>"
                    + "<br>"
                    + "���� ������� �������� ��� �������� �������:<br>"
                    + sFormHTML + "<br>"
                    + "<br>"
                    + "���� �� ����������, ��������� �� <a href=\"" + sURL + "&sID_Transaction=&sStatus_Payment="
                    + "\" target=\"_top\">���� ������</a>, � ���������� ������� �� ����������(sID_Transaction) � ������(sStatus_Payment). �����, ��� ������������� ��������������� ����� � ������ (����� �������� � ������)<br>"
                    + "(" + sURL + "&sID_Transaction=&sStatus_Payment=" + ")<br>"
                    + "<br>";
            Mail oMail = context.getBean(Mail.class);
            oMail
                    //._From(mailAddressNoreplay)			
                    ._To(ESCALATION_MAILBOXES)
                    ._Head(sHead)
                    ._Body(sBody);
            oMail.send();
            throw oException;
        }
        return sData;
    }

    public String setPaymentStatus_TaskActiviti_Direct( 
            String sID_Order,
            String sID_PaymentSystem,
            String sAdditionalData,
            String sPrefix,
            String sID_Transaction,
            String sStatus_Payment
    ) throws Exception {

        if (sPrefix == null) {
            sPrefix = "";
        }

        //LOG.info("/setPaymentStatus_TaskActiviti_Direct");
        LOG.info("(sID_Order={})", sID_Order);
        LOG.info("(sID_PaymentSystem={})", sID_PaymentSystem);
        LOG.info("(sData={})", sAdditionalData);                   //раньше имя переменной было sData
        LOG.info("(sPrefix={})", sPrefix);

        LOG.info("(sID_Transaction={})", sID_Transaction);
        LOG.info("(sStatus_Payment={})", sStatus_Payment);

        //String snID_Task=sID_Order;                                
        Long nID_Task = null;
        try {
            if (sID_Order.contains(TASK_MARK)) {
                nID_Task = Long.decode(sID_Order.replace(TASK_MARK, ""));
            }
        } catch (NumberFormatException e) {
            LOG.error(" Error: {}, incorrect sID_Order! can't invoke task_id: ", e.getMessage(), sID_Order);
        }
        String snID_Task = "" + nID_Task;
        LOG.info("(snID_Task={})", snID_Task);

        if ("Liqpay".equals(sID_PaymentSystem)) {
            //ManagerLiqpay oLiqpayService = new LiqpayService();
            oLiqpayService.setPaymentTransaction_ToActiviti(snID_Task, sID_Transaction, sStatus_Payment, sPrefix);
            sAdditionalData = "Ok";
        } else {
            sAdditionalData = "Fail";
        }
        //sID_Order=TaskActiviti_105123&sID_PaymentSystem=Liqpay&sData=&nID_Subject=25447
        return sAdditionalData;
    }


    public Map<String, String> getRedirectPaymentLiqpay(
            String sID_Merchant,
            String sSum,
            String sID_Order,
            String sDescription,
            String sID_Currency,
            String sURL_CallbackStatusNew,
            String sURL_CallbackPaySuccess,
            Long nID_Subject,
            Integer sExpired_Period_Hour
    ) throws Exception {

        LOG.info("sID_Merchant in fcc = >>>>>", sID_Merchant);
        LOG.info("sID_Order in fcc = >>>>>", sID_Order);
        LOG.info("sSum in fcc = >>>>>", sSum);
        LOG.info("sDescription in fcc = >>>>>", sDescription);
        LOG.info("sID_Currency in fcc = >>>>>", sID_Currency);
        LOG.info("nID_Subject in fcc = >>>>>", nID_Subject);
        LOG.info("sExpired_Period_Hour in fcc = >>>>>", sExpired_Period_Hour);
        if (sSum != null) {
            sSum = sSum.replaceAll(",", ".");
        } else {
            sSum = "0";
        }
        if (sDescription == null) {
            sDescription = "";
        }
        boolean bTest = generalConfig.isTest_LiqPay();
        sID_Order = (sID_Order == null ? "" : sID_Order);
        Language oLanguage = Liqpay.DEFAULT_LANG;
        Currency oID_Currency = Currency.valueOf(sID_Currency == null ? "UAH" : sID_Currency);
        nID_Subject = (nID_Subject == null ? 0 : nID_Subject);

        Map<String, String> mReturn = oLiqPuy.getPayDataRequest(sID_Merchant, sSum,
                oID_Currency, oLanguage, sDescription,
                sID_Order, sURL_CallbackStatusNew,
                sURL_CallbackPaySuccess, nID_Subject, bTest, sExpired_Period_Hour);

        return mReturn;
    }

    private static String parseData(String data) {
        if (data != null && data.length() > 0) {
            data = data.contains("data") ? data.substring(data.indexOf("data")) : null;
            if (data != null) {
                data = data.replaceFirst("data=", "");
                int indexAmpersant = data.indexOf("&");
                if (indexAmpersant >= 0) {
                    data = data.substring(0, indexAmpersant);
                }
            }
        }
        LOG.info("data after: " + data);
        return data;
    }

}
