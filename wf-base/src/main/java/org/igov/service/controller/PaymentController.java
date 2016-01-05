package org.igov.service.controller;

import com.google.gson.Gson;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.activiti.engine.RuntimeService;
import org.igov.service.security.AuthenticationTokenSelector;
import org.apache.commons.codec.binary.Base64;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.igov.model.AccessDataDao;
import org.igov.io.web.AccessCover;
import org.igov.io.liqpay.LiqpayCallbackModel;
import org.igov.io.GeneralConfig;
import org.igov.io.mail.Mail;

import javax.servlet.http.HttpServletRequest;
import org.igov.service.security.AccessContract;

@Api(tags = { "ActivitiPaymentRestController" }, description = "Контроллер платежей")
@Controller
public class PaymentController {

    public static final String LIQPAY_PAYMENT_SYSTEM = "Liqpay";
    public static final String TASK_MARK = "TaskActiviti_";
    public static final String PAYMENT_SUCCESS = "success";
    public static final String PAYMENT_SUCCESS_TEST = "sandbox";

    private static final Logger oLog = Logger.getLogger(PaymentController.class);

    ///////////////////////////////////////////////////////////////////////////////////////////////////////
    // Подробные описания сервисов для документирования в Swagger
    ///////////////////////////////////////////////////////////////////////////////////////////////////////
    private static final String noteCODE= "\n```\n";    
    private static final String noteCODEJSON= "\n```json\n";    
    private static final String noteController = "#####  ActivitiPaymentRestController. ";    
    
    private static final String noteSetPaymentStatus_TaskActiviti = noteController + "Регистрация проведенного платежа - по колбэку от платежной системы #####\n\n";

    private static final String noteSetPaymentStatus_TaskActiviti_Direct = noteController + "Регистрация проведенного платежа - по прямому вызову#####\n\n";    
    ///////////////////////////////////////////////////////////////////////////////////////////////////////

    @Autowired
    GeneralConfig generalConfig;
    @Autowired
    AccessCover accessCover;
    @Autowired
    Mail oMail;
    @Autowired
    AccessDataDao accessDataDao;
    @Autowired
    private RuntimeService runtimeService;

    @ApiOperation(value = "/setPaymentStatus_TaskActiviti", notes = noteSetPaymentStatus_TaskActiviti )
    @RequestMapping(value = "/setPaymentStatus_TaskActiviti", method = RequestMethod.POST, headers = {
            "Accept=application/json"})
    public
    @ResponseBody
    String setPaymentStatus_TaskActiviti(
	    @ApiParam(value = "Строка-ИД заявки", required = true) @RequestParam String sID_Order,
	    @ApiParam(value = "Строка-ИД платежной системы", required = true) @RequestParam String sID_PaymentSystem,
	    @ApiParam(value = "Строка со вспомогательными данными", required = true) @RequestParam String sData,
	    @ApiParam(value = "Строка-префикс платежа (если их несколько в рамках заявки)", required = false) @RequestParam(value = "sPrefix", required = false) String sPrefix,
	    @ApiParam(value = "Данные от платежной системы", required = false) @RequestParam(value = "data", required = false) String data,
	    @ApiParam(value = "Подпись платежной системы", required = false) @RequestParam(value = "signature", required = false) String signature,
            HttpServletRequest request
    ) throws Exception {

        if (sPrefix == null) {
            sPrefix = "";
        }

        String URI = request.getRequestURI() + "?" + request.getQueryString();
        oLog.info("/setPaymentStatus_TaskActiviti");

        oLog.info("sID_Order=" + sID_Order);
        oLog.info("sID_PaymentSystem=" + sID_PaymentSystem);
        oLog.info("sData=" + sData);
        oLog.info("sPrefix=" + sPrefix);

        oLog.info("data=" + data);
        oLog.info("signature=" + signature);
        oLog.info("URI=" + URI);
        String sDataDecoded = null;

        try {
            if (data != null) {
                sDataDecoded = new String(Base64.decodeBase64(data.getBytes()));
                oLog.info("sDataDecoded=" + sDataDecoded);
            }
            setPaymentStatus(sID_Order, sDataDecoded, sID_PaymentSystem, sPrefix);
            //setPaymentStatus(sID_Order, null, sID_PaymentSystem);
        } catch (Exception oException) {
            oLog.error("/setPaymentStatus_TaskActiviti", oException);
            String snID_Subject = "0";
            String sAccessKey = null;
            try {
                //sAccessKey = accessDataDao.setAccessData(URI);
                sAccessKey = accessCover.getAccessKey(URI);
            } catch (Exception oException1) {
                oLog.error("/setPaymentStatus_TaskActiviti:sAccessKey=", oException1);
            }

            //generalConfig.sHost() + "/wf/service/setPaymentStatus_TaskActiviti_Direct?sID_Order="+sID_Order+"&sID_PaymentSystem="+sID_PaymentSystem+"&sData=&sID_Transaction=&sStatus_Payment="
            String sURL = new StringBuilder(generalConfig.sHost())
                    .append("/wf/service/setPaymentStatus_TaskActiviti_Direct?")
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

            String saToMail = "bvv4ik@gmail.com,dmitrij.zabrudskij@privatbank.ua";
            String sHead = (generalConfig.bTest() ? "(test)" : "(PROD)")
                    + "/setPaymentStatus_TaskActiviti:������ ��� ������� �������� ��������� ���������� � ������� � ��������-������!";
            String sBody = "oException.getMessage()=" + oException.getMessage() + "<br>" +
                    "<br>" +

                    "sID_Order=" + sID_Order + "<br>" +
                    "sID_PaymentSystem=" + sID_PaymentSystem + "<br>" +
                    "sData=" + sData + "<br>" +
                    "data=" + data + "<br>" +
                    "signature=" + signature + "<br>" +
                    "<br>" +
                    "���� ������� �������� ��� �������� �������:<br>" +
                    sFormHTML + "<br>" +
                    "<br>" +
                    "���� �� ����������, ��������� �� <a href=\"" + sURL + "&sID_Transaction=&sStatus_Payment="
                    + "\" target=\"_top\">���� ������</a>, � ���������� ������� �� ����������(sID_Transaction) � ������(sStatus_Payment). �����, ��� ������������� ��������������� ����� � ������ (����� �������� � ������)<br>"
                    +
                    "(" + sURL + "&sID_Transaction=&sStatus_Payment=" + ")<br>" +
                    "<br>";
            oMail
                    .reset();
            oMail
                    //._From(mailAddressNoreplay)			
                    ._To(saToMail)
                    ._Head(sHead)
                    ._Body(sBody)
            ;
            oMail.send();
            throw oException;
        }
        return sData;
    }

    @ApiOperation(value = "/setPaymentStatus_TaskActiviti_Direct", notes = noteSetPaymentStatus_TaskActiviti_Direct )
    @RequestMapping(value = "/setPaymentStatus_TaskActiviti_Direct", method = RequestMethod.GET, headers = {
            "Accept=application/json"})
    public
    @ResponseBody
    String setPaymentStatus_TaskActiviti_Direct(
            @ApiParam(value = "Строка-ИД заявки", required = true) @RequestParam String sID_Order,
	    @ApiParam(value = "Строка-ИД платежной системы", required = true) @RequestParam String sID_PaymentSystem,
	    @ApiParam(value = "Строка со вспомогательными данными", required = true) @RequestParam String sData,
	    @ApiParam(value = "Cтрока-префикс платежа (если их несколько в рамках заявки)", required = false) @RequestParam(value = "sPrefix", required = false) String sPrefix,

            //@RequestParam String snID_Task,
	    @ApiParam(value = "Строка-ИД транзакции", required = true) @RequestParam String sID_Transaction,
	    @ApiParam(value = "Строка-статуса платежа", required = true) @RequestParam String sStatus_Payment

    ) throws Exception {

        if (sPrefix == null) {
            sPrefix = "";
        }

        oLog.info("/setPaymentStatus_TaskActiviti_Direct");
        oLog.info("sID_Order=" + sID_Order);
        oLog.info("sID_PaymentSystem=" + sID_PaymentSystem);
        oLog.info("sData=" + sData);
        oLog.info("sPrefix=" + sPrefix);

        oLog.info("sID_Transaction=" + sID_Transaction);
        oLog.info("sStatus_Payment=" + sStatus_Payment);

        //String snID_Task=sID_Order;

        Long nID_Task = null;
        try {
            if (sID_Order.contains(TASK_MARK)) {
                nID_Task = Long.decode(sID_Order.replace(TASK_MARK, ""));
            }
        } catch (NumberFormatException e) {
            oLog.error("incorrect sID_Order! can't invoke task_id: " + sID_Order);
        }
        String snID_Task = "" + nID_Task;
        oLog.info("snID_Task=" + snID_Task);

        if ("Liqpay".equals(sID_PaymentSystem)) {
            setPaymentTransaction_ToActiviti(snID_Task, sID_Transaction, sStatus_Payment, sPrefix);
            sData = "Ok";
        } else {
            sData = "Fail";
        }
        //sID_Order=TaskActiviti_105123&sID_PaymentSystem=Liqpay&sData=&nID_Subject=25447
        return sData;
    }

    private void setPaymentStatus(String sID_Order, String sData, String sID_PaymentSystem, String sPrefix)
            throws Exception {
        if (!LIQPAY_PAYMENT_SYSTEM.equals(sID_PaymentSystem)) {
            oLog.error("not liqpay system");
            throw new Exception("not liqpay system");
            //return;			
        }

        oLog.info("sData=" + sData);

        Long nID_Task = null;
        try {
            if (sID_Order.contains(TASK_MARK)) {
                oLog.info("sID_Order(1)=" + sID_Order);
                String s = sID_Order.replace(TASK_MARK, "");
                oLog.info("sID_Order(2)=" + s);
                if (sPrefix != null && !"".equals(sPrefix.trim()) && s.endsWith(sPrefix)) {
                    s = s.substring(0, s.length() - sPrefix.length());
                }
                oLog.info("sID_Order(3)=" + s);
                nID_Task = Long.decode(s);
                oLog.info("nID_Task=" + nID_Task);
                //nID_Task = Long.decode(sID_Order.replace(TASK_MARK, ""));			
            }
        } catch (NumberFormatException e) {
            oLog.error("incorrect sID_Order! can't invoke task_id: " + sID_Order);
        }
        String snID_Task = "" + nID_Task;

        //https://test.region.igov.org.ua/wf/service/setPaymentStatus_TaskActiviti0?sID_Order=TaskActiviti_1485001&sID_PaymentSystem=Liqpay&sData=&nID_Subject=20045&sAccessKey=b32d9855-dce0-44df-bbe0-dd0e41958cde			
        //data=eyJwYXltZW50X2lkIjo2MzQ0NDcxOCwidHJhbnNhY3Rpb25faWQiOjYzNDQ0NzE4LCJzdGF0dXMiOiJzYW5kYm94IiwidmVyc2lvbiI6MywidHlwZSI6ImJ1eSIsInB1YmxpY19rZXkiOiJpMTAxNzI5NjgwNzgiLCJhY3FfaWQiOjQxNDk2Mywib3JkZXJfaWQiOiJUYXNrQWN0aXZpdGlfMTQ4NTAwMSIsImxpcXBheV9vcmRlcl9pZCI6IjQwMXUxNDM3MzI1MDIyMTgzMzAzIiwiZGVzY3JpcHRpb24iOiLQotC10YHRgtC+0LLQsNGPINGC0YDQsNC90LfQsNC60YbQuNGPIiwic2VuZGVyX3Bob25lIjoiMzgwOTc5MTM4MDA3IiwiYW1vdW50IjowLjAxLCJjdXJyZW5jeSI6IlVBSCIsInNlbmRlcl9jb21taXNzaW9uIjowLjAsInJlY2VpdmVyX2NvbW1pc3Npb24iOjAuMCwiYWdlbnRfY29tbWlzc2lvbiI6MC4wLCJhbW91bnRfZGViaXQiOjAuMDEsImFtb3VudF9jcmVkaXQiOjAuMDEsImNvbW1pc3Npb25fZGViaXQiOjAuMCwiY29tbWlzc2lvbl9jcmVkaXQiOjAuMCwiY3VycmVuY3lfZGViaXQiOiJVQUgiLCJjdXJyZW5jeV9jcmVkaXQiOiJVQUgiLCJzZW5kZXJfYm9udXMiOjAuMCwiYW1vdW50X2JvbnVzIjowLjB9			
        //signature=z77CQeBn3Z75n5UpJqXKG+KjZyI=			
        String sID_Transaction = "Pay_" + snID_Task;
        String sStatus_Payment = null;
        //parse sData			
        if (sData != null) {
            try {			

                Gson oGson = new Gson();
                LiqpayCallbackModel oLiqpayCallbackModel = oGson.fromJson(sData, LiqpayCallbackModel.class);
                //log.info("sID_PaymentSystem="+sID_PaymentSystem);			
                oLog.info("oLiqpayCallbackModel.getOrder_id()=" + oLiqpayCallbackModel.getOrder_id());
                sID_Transaction = oLiqpayCallbackModel.getTransaction_id();
                oLog.info("oLiqpayCallbackModel.getTransaction_id()=" + sID_Transaction);
                sStatus_Payment = oLiqpayCallbackModel.getStatus();
                oLog.info("oLiqpayCallbackModel.getStatus()=" + sStatus_Payment);
            } catch (Exception e) {
                oLog.error("can't parse json! reason:" + e.getMessage());
                throw e;			

            }
        } else {
            oLog.warn("incorrect input data: sData == null: " + "snID_Task=" + snID_Task
                    + ", sID_Transaction=" + sID_Transaction + ", sStatus_Payment=" + sStatus_Payment);
        }

        //check variables			
        //if (sData != null && (sID_Transaction == null || nID_Task == null || !PAYMENT_SUCCESS.equals(sStatus_Payment))) {			
        if (sData != null && (sID_Transaction == null || sStatus_Payment == null)) {
            oLog.error("incorrect secondary input data: " + "nID_Task=" + snID_Task
                    + ", sID_Transaction=" + sID_Transaction + ", sStatus_Payment=" + sStatus_Payment);
        }

        if (sData != null && !PAYMENT_SUCCESS.equals(sStatus_Payment) && !PAYMENT_SUCCESS_TEST
                .equals(sStatus_Payment)) {
            oLog.error("incorrect sStatus_Payment: " + "nID_Task=" + snID_Task
                    + ", sID_Transaction=" + sID_Transaction + ", sStatus_Payment=" + sStatus_Payment);
        }

        if (nID_Task == null) {
            oLog.error("incorrect primary input data(BREAKED): " + "snID_Task=" + snID_Task
                    + ", sID_Transaction=" + sID_Transaction + ", sStatus_Payment=" + sStatus_Payment);
            //return;			
            throw new Exception("incorrect primary input data(BREAKED): " + "snID_Task=" + snID_Task
                    + ", sID_Transaction=" + sID_Transaction + ", sStatus_Payment=" + sStatus_Payment);
        }

        setPaymentTransaction_ToActiviti(snID_Task, sID_Transaction, sStatus_Payment, sPrefix);
    }

    private void setPaymentTransaction_ToActiviti(String snID_Task, String sID_Transaction, String sStatus_Payment,
                                                  String sPrefix) throws Exception {
        //save info to process			
        try {
            oLog.info("try to get task. snID_Task=" + snID_Task);

            //TODO ����������� ������ �������� �� �������� � �� �����
            String snID_Process = snID_Task;
            String sID_Payment = sID_Transaction + "_" + sStatus_Payment;
            oLog.info("try to set: sID_Payment=" + sID_Payment);
            runtimeService.setVariable(snID_Process, "sID_Payment" + sPrefix, sID_Payment);
            oLog.info("completed set sID_Payment" + sPrefix + "=" + sID_Payment + " to: snID_Process=" + snID_Process);
        } catch (Exception e) {
            oLog.error("during changing: snID_Task=" + snID_Task
                    + ", sID_Transaction=" + sID_Transaction + ", sStatus_Payment=" + sStatus_Payment, e);
            throw e;
        }
    }

}			
