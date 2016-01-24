/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.igov.service.business.finance;

import com.google.gson.Gson;
import org.activiti.engine.RuntimeService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 *
 * @author Belyavtsev Vladimir Vladimirovich (BW)
 */
@Service
public class LiqpayService {

    private static final Logger LOG = LoggerFactory.getLogger(LiqpayService.class);
    
    public static final String LIQPAY_PAYMENT_SYSTEM = "Liqpay";
    public static final String TASK_MARK = "TaskActiviti_";
    public static final String PAYMENT_SUCCESS = "success";
    public static final String PAYMENT_SUCCESS_TEST = "sandbox";
    
    @Autowired
    private RuntimeService runtimeService;
    
    
    public void setPaymentStatus(String sID_Order, String sData, String sID_PaymentSystem, String sPrefix)
            throws Exception {
        if (!LIQPAY_PAYMENT_SYSTEM.equals(sID_PaymentSystem)) {
            LOG.error("not liqpay system");
            throw new Exception("not liqpay system");
            //return;			
        }

        LOG.info("(sData={})", sData);

        Long nID_Task = null;
        try {
            if (sID_Order.contains(TASK_MARK)) {
                LOG.info("(sID_Order(1)={})", sID_Order);
                String s = sID_Order.replace(TASK_MARK, "");
                LOG.info("(sID_Order(2)={})", s);
                if (sPrefix != null && !"".equals(sPrefix.trim()) && s.endsWith(sPrefix)) {
                    s = s.substring(0, s.length() - sPrefix.length());
                }
                LOG.info("(sID_Order(3)={}", s);
                nID_Task = Long.decode(s);
                LOG.info("(nID_Task={})", nID_Task);
                //nID_Task = Long.decode(sID_Order.replace(TASK_MARK, ""));			
            }
        } catch (NumberFormatException e) {
            LOG.error("incorrect sID_Order! can't invoke task_id: {}", sID_Order);
        }
        String snID_Task = "" + nID_Task;

        //https://test.region.igov.org.ua/wf/service/finance/setPaymentStatus_TaskActiviti0?sID_Order=TaskActiviti_1485001&sID_PaymentSystem=Liqpay&sData=&nID_Subject=20045&sAccessKey=b32d9855-dce0-44df-bbe0-dd0e41958cde			
        //data=eyJwYXltZW50X2lkIjo2MzQ0NDcxOCwidHJhbnNhY3Rpb25faWQiOjYzNDQ0NzE4LCJzdGF0dXMiOiJzYW5kYm94IiwidmVyc2lvbiI6MywidHlwZSI6ImJ1eSIsInB1YmxpY19rZXkiOiJpMTAxNzI5NjgwNzgiLCJhY3FfaWQiOjQxNDk2Mywib3JkZXJfaWQiOiJUYXNrQWN0aXZpdGlfMTQ4NTAwMSIsImxpcXBheV9vcmRlcl9pZCI6IjQwMXUxNDM3MzI1MDIyMTgzMzAzIiwiZGVzY3JpcHRpb24iOiLQotC10YHRgtC+0LLQsNGPINGC0YDQsNC90LfQsNC60YbQuNGPIiwic2VuZGVyX3Bob25lIjoiMzgwOTc5MTM4MDA3IiwiYW1vdW50IjowLjAxLCJjdXJyZW5jeSI6IlVBSCIsInNlbmRlcl9jb21taXNzaW9uIjowLjAsInJlY2VpdmVyX2NvbW1pc3Npb24iOjAuMCwiYWdlbnRfY29tbWlzc2lvbiI6MC4wLCJhbW91bnRfZGViaXQiOjAuMDEsImFtb3VudF9jcmVkaXQiOjAuMDEsImNvbW1pc3Npb25fZGViaXQiOjAuMCwiY29tbWlzc2lvbl9jcmVkaXQiOjAuMCwiY3VycmVuY3lfZGViaXQiOiJVQUgiLCJjdXJyZW5jeV9jcmVkaXQiOiJVQUgiLCJzZW5kZXJfYm9udXMiOjAuMCwiYW1vdW50X2JvbnVzIjowLjB9			
        //signature=z77CQeBn3Z75n5UpJqXKG+KjZyI=			
        String sID_Transaction = "Pay_" + snID_Task;
        String sStatus_Payment = null;
        //parse sData			
        if (sData != null) {
            try {			

                Gson oGson = new Gson();
                LiqpayCallbackEntity oLiqpayCallbackModel = oGson.fromJson(sData, LiqpayCallbackEntity.class);
                //log.info("sID_PaymentSystem="+sID_PaymentSystem);			
                LOG.info("(oLiqpayCallbackModel.getOrder_id()={})", oLiqpayCallbackModel.getOrder_id());
                sID_Transaction = oLiqpayCallbackModel.getTransaction_id();
                LOG.info("(oLiqpayCallbackModel.getTransaction_id()={})", sID_Transaction);
                sStatus_Payment = oLiqpayCallbackModel.getStatus();
                LOG.info("(oLiqpayCallbackModel.getStatus()={})", sStatus_Payment);
            } catch (Exception e) {
                LOG.error("Error: {}, can't parse json! ", e.getMessage());
                throw e;			

            }
        } else {
            LOG.warn("incorrect input data: sData == null: (snID_Task={}, sID_Transaction={}, sStatus_Payment={})"
                    , snID_Task, sID_Transaction, sStatus_Payment);
        }

        //check variables			
        //if (sData != null && (sID_Transaction == null || nID_Task == null || !PAYMENT_SUCCESS.equals(sStatus_Payment))) {			
        if (sData != null && (sID_Transaction == null || sStatus_Payment == null)) {
            LOG.error("incorrect secondary input data: (nID_Task={}, sID_Transaction={}, sStatus_Payment={})",
                    snID_Task, sID_Transaction, sStatus_Payment);
        }

        if (sData != null && !PAYMENT_SUCCESS.equals(sStatus_Payment) && !PAYMENT_SUCCESS_TEST
                .equals(sStatus_Payment)) {
            LOG.error("incorrect sStatus_Payment: (nID_Task={}, sID_Transaction={}, sStatus_Payment={})",
                    snID_Task, sID_Transaction, sStatus_Payment);
        }

        if (nID_Task == null) {
            LOG.error("incorrect primary input data(BREAKED): (nID_Task={}, sID_Transaction={}, sStatus_Payment={})",
                    snID_Task, sID_Transaction, sStatus_Payment);
            //return;			
            throw new Exception("incorrect primary input data(BREAKED): " + "snID_Task=" + snID_Task
                    + ", sID_Transaction=" + sID_Transaction + ", sStatus_Payment=" + sStatus_Payment);
        }

        setPaymentTransaction_ToActiviti(snID_Task, sID_Transaction, sStatus_Payment, sPrefix);
    }

    public void setPaymentTransaction_ToActiviti(String snID_Task, String sID_Transaction, String sStatus_Payment,
                                                  String sPrefix) throws Exception {
        //save info to process			
        try {
            LOG.info("try to get task. (snID_Task={}", snID_Task);

            //TODO ����������� ������ �������� �� �������� � �� �����
            String snID_Process = snID_Task;
            String sID_Payment = sID_Transaction + "_" + sStatus_Payment;
            LOG.info("try to set: (sID_Payment={})", sID_Payment);
            runtimeService.setVariable(snID_Process, "sID_Payment" + sPrefix, sID_Payment);
            LOG.info("completed set sID_Payment{}+{} to: snID_Process={}", sPrefix, sID_Payment, snID_Process);
        } catch (Exception e) {
            LOG.error("Error: {}, during changing: (snID_Task={}, sID_Transaction={}, sStatus_Payment={})",
                    e.getMessage(), snID_Task,  sID_Transaction, sStatus_Payment);
            throw e;
        }
    }    
}
