package org.igov.service.business.action.task.systemtask;

import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.delegate.Expression;
import org.activiti.engine.delegate.JavaDelegate;
import org.igov.io.web.HttpRequester;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.DecimalFormat;
import org.activiti.engine.impl.util.json.JSONObject;
import org.igov.io.GeneralConfig;
import org.igov.model.action.event.HistoryEvent_Service_StatusType;
import org.igov.service.business.action.event.ActionEventHistoryService;

/**
 * @author Kovylin
 * If bPay - true - then we conduct materPass payment else - cancel it  
 */
@Component("PayConfirm")
public class ProcessIpayPayment implements JavaDelegate{
    
    private final String sPay_URL = "https://walletmc.ipay.ua/";
    private final static String CONTENT_TYPE = "text/json; charset=utf-8";
    
    private Expression bPay;
    
    private final static Logger LOG = LoggerFactory.getLogger(ProcessCountTaskCustom.class);
    
    @Autowired
    private HttpRequester oHttpRequester;
    
    @Autowired
    private GeneralConfig oGeneralConfig;
    
    @Autowired
    private ActionEventHistoryService oActionEventHistoryService;
    
    @Override
    public void execute(DelegateExecution execution) throws Exception{
        
        try{
            //do not do it static and final!
            String sBody ="{" +
                "\"request\": {" +
                "\"auth\": {" +
                " \"login\": \"sPayLogin\"," +
                " \"time\": \"sPayDate\"," +
                " \"sign\": \"sPaySign\"" +
                "}," +
                " \"action\": \"PaymentAction\"," +
                " \"body\": sPaymentBody" +
                "}" +
                "}";


            sBody = sBody.replaceAll("sPayLogin", oGeneralConfig.getsMasterPass_sPassword());

            String sPaymentBody = (String)execution.getVariable("sID_Pay_MasterPass");
            LOG.info("sPaymentBody {}", sPaymentBody);
            LOG.info("bPay is {}", this.bPay.getExpressionText());
            Boolean isPay = Boolean.valueOf(this.bPay.getExpressionText());
            LOG.info("isPay is {}", isPay);

            if(sPaymentBody == null){
                throw new RuntimeException("Variable sPaymentBody not found!");
            }

            sBody = sBody.replaceAll("sPaymentBody", sPaymentBody);

            if(isPay){
                sBody = sBody.replaceAll("PaymentAction", "PaymentSale");
            }else{
                sBody = sBody.replaceAll("PaymentAction", "PaymentCancel");
            }

            sBody = buildSignAndDate(sBody);

            LOG.info("sBody ipay {}",sBody);
            String response = oHttpRequester.postInside(sPay_URL, null, sBody, CONTENT_TYPE);
            LOG.info("response {}", response);

            JSONObject oResponseParams = new JSONObject(response);
            String pmt_status = (String)((JSONObject)oResponseParams.get("response")).get("pmt_status");

            Double amount_total = (double)Integer.parseInt((String)((JSONObject)oResponseParams.get("response")).get("amount"))/100;
            Double invoice = (double)Integer.parseInt((String)((JSONObject)oResponseParams.get("response")).get("invoice"))/100;
            Double amount = amount_total - invoice;

            DecimalFormat decim = new DecimalFormat("0.00");

            if(pmt_status!= null && (pmt_status.equals("5")||pmt_status.equals("4")||pmt_status.equals("9"))){
                execution.setVariable("bPayResult", "success");
            }else{
                execution.setVariable("bPayResult", "unsuccess");
            }

            LOG.info("pmt_status is {}", pmt_status);
            
            execution.setVariable("asPayResult", response);
            if(pmt_status.equals("9")){
                execution.setVariable("sPaymetStatus", "операцію скасовано");
            }else if(pmt_status.equals("4")){
                execution.setVariable("sPaymetStatus", "відмова");
            }else if(pmt_status.equals("5")){
                execution.setVariable("sPaymetStatus", "платіж проведено");
            }
            
            execution.setVariable("sAmountTotal", decim.format(amount_total));
            execution.setVariable("sAmount", decim.format(amount));
            execution.setVariable("sAmountWithoutComission", decim.format(amount_total - amount));
        }
        catch (Exception ex){
            execution.setVariable("bPayResult", "error");
            LOG.info("error in ProcessIpayPayment {}", ex);
        }
    }
    
    public static String md5Custom(String st) {
        MessageDigest messageDigest = null;
        byte[] digest = new byte[0];

        try {
            messageDigest = MessageDigest.getInstance("MD5");
            messageDigest.reset();
            messageDigest.update(st.getBytes());
            digest = messageDigest.digest();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }

        BigInteger bigInt = new BigInteger(1, digest);
        String md5Hex = bigInt.toString(16);

        while( md5Hex.length() < 32 ){
            md5Hex = "0" + md5Hex;
        }

        return md5Hex;
    }
    
    private String buildSignAndDate(String sBody) throws NoSuchAlgorithmException, UnsupportedEncodingException
    {
        Date oPaymentDate = new Date();
        SimpleDateFormat oSimpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String sPaymentDate = oSimpleDateFormat.format(oPaymentDate);
        sBody = sBody.replaceAll("sPayDate", sPaymentDate);
        sBody = sBody.replaceAll("sPaySign", md5Custom(sPaymentDate + oGeneralConfig.getsMasterPass_sSecretToken()));
        
        return sBody;
    }
}