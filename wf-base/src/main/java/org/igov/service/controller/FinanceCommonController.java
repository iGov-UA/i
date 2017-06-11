package org.igov.service.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import javax.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.RequestBody;
import org.igov.service.business.finance.FinanceService;

@Api(tags = { "FinanceCommonController -- Финансы общие (в т.ч. платежи)" })
@Controller
public class FinanceCommonController {
 
    
    @Autowired
    private FinanceService oFinanceService;
    

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
            @ApiParam(value = "Строка-Данные от платежной системы", required = false) @RequestBody(required = false) String data,
            @ApiParam(value = "Строка-Подпись платежной системы", required = false) @RequestParam(value = "signature", required = false) String signature,
            HttpServletRequest request
    ) throws Exception {
    
        String resultString = oFinanceService.setPaymentStatus_TaskActiviti(sID_Order, sID_PaymentSystem, sData, sPrefix, sData, signature, request);
        return resultString;     
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
        
        String resultString = oFinanceService.setPaymentStatus_TaskActiviti_Direct(sID_Order, sID_PaymentSystem, sData, sPrefix, sID_Transaction, sStatus_Payment);
        return resultString; 
    }


    @ApiOperation(value = "/finance/redirectPaymentLiqpay", notes = "##### Получение URL-а и параметров для отправки платежа через POST-запрос\n")
    @RequestMapping(value = "/finance/redirectPaymentLiqpay", method = RequestMethod.GET, headers = {
            "Accept=application/json" })
    public
    @ResponseBody
    Map<String, String> getRedirectPaymentLiqpay(
        @ApiParam(value = "Строка-ИД Мерчанта", required = true) @RequestParam(value = "sID_Merchant", required = true) String sID_Merchant,
        @ApiParam(value = "Сумма (разделитель копеек - точка)", required = false) @RequestParam(value = "sSum", required = false) String sSum,
        @ApiParam(value = "Строка-ИД заявки-платежа", required = false) @RequestParam(value = "sID_Order", required = false) String sID_Order,
        @ApiParam(value = "Описание платежа", required = false) @RequestParam(value = "sDescription", required = false) String sDescription,
        @ApiParam(value = "ИД валюты (3 символа)", required = false) @RequestParam(value = "sID_Currency", required = false) String sID_Currency,
        @ApiParam(value = "", required = false) @RequestParam(value = "sURL_CallbackStatusNew", required = false) String sURL_CallbackStatusNew,
        @ApiParam(value = "", required = false) @RequestParam(value = "sURL_CallbackPaySuccess", required = false) String sURL_CallbackPaySuccess,
        @ApiParam(value = "номер-ИД субьекта", required = false) @RequestParam Long nID_Subject,
        @ApiParam(value = "количество часов актуальности платежа", required = false) @RequestParam Integer sExpired_Period_Hour
    ) throws Exception {

    	
        
        Map<String, String> mReturn = oFinanceService.getRedirectPaymentLiqpay(sID_Merchant, 
                sSum, sID_Order, sDescription, sID_Currency, sURL_CallbackStatusNew, 
                sURL_CallbackPaySuccess, nID_Subject, sExpired_Period_Hour);
        
        return mReturn;
    }
    

    

}			
