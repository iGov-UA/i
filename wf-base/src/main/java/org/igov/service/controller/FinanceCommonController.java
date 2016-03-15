package org.igov.service.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.apache.commons.codec.binary.Base64;
import org.igov.io.GeneralConfig;
import org.igov.io.mail.Mail;
import org.igov.service.business.access.AccessDataService;
import org.igov.service.business.access.AccessKeyService;
import org.igov.service.business.finance.LiqpayService;
import org.igov.service.controller.security.AccessContract;
import org.igov.service.controller.security.AuthenticationTokenSelector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;

import static org.igov.service.business.finance.LiqpayService.TASK_MARK;

@Api(tags = { "FinanceCommonController -- Финансы общие (в т.ч. платежи)" })
@Controller
public class FinanceCommonController {

    private static final Logger LOG = LoggerFactory.getLogger(FinanceCommonController.class);

    @Autowired
    GeneralConfig generalConfig;
    @Autowired
    AccessKeyService accessCover;
    @Autowired
    Mail oMail;
    @Autowired
    AccessDataService accessDataDao;

    @Autowired
    private LiqpayService oLiqpayService;

    @ApiOperation(value = "/finance/setPaymentStatus_TaskActiviti", notes = "##### Контроллер платежей. Регистрация проведенного платежа - по колбэку от платежной системы\n")
    @RequestMapping(value = { "/finance/setPaymentStatus_TaskActiviti",
            "/setPaymentStatus_TaskActiviti" }, method = RequestMethod.POST,
            headers = { "Accept=application/json" })
    public
    @ResponseBody
    String setPaymentStatus_TaskActiviti(
	    @ApiParam(value = "Строка-ИД заявки", required = true) @RequestParam String sID_Order,
	    @ApiParam(value = "Строка-ИД платежной системы", required = true) @RequestParam String sID_PaymentSystem,
	    @ApiParam(value = "Строка со вспомогательными данными", required = true) @RequestParam String sData,
	    @ApiParam(value = "Строка-префикс платежа (если их несколько в рамках заявки)", required = false) @RequestParam(value = "sPrefix", required = false) String sPrefix,
            @ApiParam(value = "Строка-Данные от платежной системы", required = false) @RequestParam(value = "data", required = false) String data,
            @ApiParam(value = "Строка-Подпись платежной системы", required = false) @RequestParam(value = "signature", required = false) String signature,
            HttpServletRequest request
    ) throws Exception {

        //ManagerLiqpay oLiqpayService = new LiqpayService();
        
        if (sPrefix == null) {
            sPrefix = "";
        }

        String URI = request.getRequestURI() + "?" + request.getQueryString();
        //LOG.info("/setPaymentStatus_TaskActiviti");

        LOG.info("(sID_Order={})", sID_Order);
        LOG.info("(sID_PaymentSystem={})", sID_PaymentSystem);
        LOG.info("(sData={})", sData);
        LOG.info("(sPrefix={})", sPrefix);

        LOG.info("(data={})", data);
        LOG.info("(signature={})", signature);
        LOG.info("(URI={})", URI);
        String sDataDecoded = null;

        try {
            if (data != null) {
                sDataDecoded = new String(Base64.decodeBase64(data.getBytes()));
                LOG.info("(sDataDecoded={})", sDataDecoded);
            }
            oLiqpayService.setPaymentStatus(sID_Order, sDataDecoded, sID_PaymentSystem, sPrefix);
            //setPaymentStatus(sID_Order, null, sID_PaymentSystem);
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

            //generalConfig.sHost() + "/wf/service/finance/setPaymentStatus_TaskActiviti_Direct?sID_Order="+sID_Order+"&sID_PaymentSystem="+sID_PaymentSystem+"&sData=&sID_Transaction=&sStatus_Payment="
            String sURL = new StringBuilder(generalConfig.sHost())
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

            String saToMail = "bvv4ik@gmail.com,dmitrij.zabrudskij@privatbank.ua";
            String sHead = (generalConfig.bTest() ? "(test)" : "(PROD)")
                    + "/finance/setPaymentStatus_TaskActiviti:������ ��� ������� �������� ��������� ���������� � ������� � ��������-������!";
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

    @ApiOperation(value = "/finance/setPaymentStatus_TaskActiviti_Direct", notes = "##### Контроллер платежей. Регистрация проведенного платежа - по прямому вызову\n")
    @RequestMapping(value = "/finance/setPaymentStatus_TaskActiviti_Direct", method = RequestMethod.GET, headers = {
            "Accept=application/json" })
    public
    @ResponseBody
    String setPaymentStatus_TaskActiviti_Direct(
            @ApiParam(value = "Строка-ИД заявки", required = true) @RequestParam String sID_Order,
	    @ApiParam(value = "Строка-ИД платежной системы", required = true) @RequestParam String sID_PaymentSystem,
	    @ApiParam(value = "Строка со вспомогательными данными", required = true) @RequestParam String sData,
	    @ApiParam(value = "Cтрока-префикс платежа (если их несколько в рамках заявки)", required = false) @RequestParam(value = "sPrefix", required = false) String sPrefix,
	    @ApiParam(value = "Строка-ИД транзакции", required = true) @RequestParam String sID_Transaction,
	    @ApiParam(value = "Строка-статуса платежа", required = true) @RequestParam String sStatus_Payment

    ) throws Exception {

        if (sPrefix == null) {
            sPrefix = "";
        }

        //LOG.info("/setPaymentStatus_TaskActiviti_Direct");
        LOG.info("(sID_Order={})", sID_Order);
        LOG.info("(sID_PaymentSystem={})", sID_PaymentSystem);
        LOG.info("(sData={})", sData);
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
            sData = "Ok";
        } else {
            sData = "Fail";
        }
        //sID_Order=TaskActiviti_105123&sID_PaymentSystem=Liqpay&sData=&nID_Subject=25447
        return sData;
    }


}			
