/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.igov.run.schedule;

import org.quartz.*;
import org.quartz.impl.StdSchedulerFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Belyavtsev Vladimir Vladimirovich (BW)
 */
public class ScheduleInitializer {

    private final static Logger LOG = LoggerFactory.getLogger(ScheduleInitializer.class);
    
    public void init() throws SchedulerException {
        // ��������� Schedule Factory
        LOG.info("Schedule Factory...");
        SchedulerFactory oSchedulerFactory = new StdSchedulerFactory();
        // ��������� ����������� �� schedule factory
        LOG.info("getScheduler...");
        Scheduler oScheduler = oSchedulerFactory.getScheduler();

        // ������� �����
        long nNowMS = System.currentTimeMillis();
        LOG.info("(nNowMS={})", nNowMS);

        // ��������� JobDetail � ������ �������,
        // ������� ������� � ������� ������������ �������
        JobDetail oJobDetail_Escalation_Standart = new JobDetail("oJobDetail_Escalation_Standart",
                "oJobDetail_Escalation_Group", JobEscalation.class);
        // ��������� CronTrigger � ��� ������ � ������ ������
        CronTrigger oCronTrigger_EveryNight_Deep = new CronTrigger("oCronTrigger_EveryNight_Deep",
                "oCronTrigger_EveryNight_Group");
        try {
            // ������������� CronExpression
            LOG.info("oCronExpression__EveryNight_Deep...");
            //CronExpression oCronExpression__EveryNight_Deep = new CronExpression("0/5 * * * * ?");
            //http://www.ibm.com/developerworks/ru/library/j-quartz/
            /*
            <p>��������� cron ������� �� ��������� ���� �����:</p>
            <ul class="ibm-bullet-list">
                <li>�������</li>
                <li>������</li>
                <li>����</li>
                <li>���� ������</li>
                <li>�����</li>
                <li>���� ������</li>
                <li>��� (�������������� ����)</li>
            </ul>
            <h3 id="N10100">����������� �������</h3>
            <p>�������� cron ���������� ����� ����������� ��������, ��������:</p>
            <ul class="ibm-bullet-list">
                <li>������ ����� ����� (/) ���������� ���������� ��������. ��������, "5/15" � ���� "�������" �������� ������ 15 ������, ������� � ����� �������.</li>
                <li>���� ������� (?) � ����� L (L) ����������� ������������ ������ � ����� "���� ������" � "���� ������". ���� ������� ��������, ��� � ���� �� ������ ���� ��������� ��������. ����� �������, ���� �� �������������� ���� ������, �� ������ �������� "?" � ���� "���� ������" ��� ����������� ����, ��� �������� "���� ������" �������������. ����� L - ��� ���������� �� <em>last (���������)</em>. ���� ��� ���������� � ���� "���� ������", ������� ����� ������������� �� ��������� ���� ������. � ���� "���� ������" "L" ����������� "7", ���� ���������� ���� �� ����, ��� �������� ��������� ��������� "��� ������" � ���� ������. ���, "0L" ����������� ���������� ������� �� ��������� ����������� ������� ������.</li>
                <li> ����� W (W) � ���� "���� ������" ��������� ���������� ������� �� ��������� � ��������� �������� ������� ����. ����� "1W" � ���� "���� ������" �� ���������� ���������� ������� �� ������� ����, ��������� � ������� ����� ������.</li>
                <li>���� ����� (#) ������������� ���������� ������� ���� ������� ������. ���� "MON#2" � ���� "���� ������" ��������� ������� �� ������ ����������� ������.</li>
                <li>���� ��������� (*) �������� �������������� ������ � ����������, ��� ����� ��������� �������� ����� ���� ������� ��� ������� ���������� ����. </li>
            </ul>
            */
            CronExpression oCronExpression__EveryNight_Deep = new CronExpression(
                    "0 0 2 1/1 * ?");//� 2 ���� ���� ������ ����
            // ����������� CronExpression CronTrigger'�
            LOG.info("oCronExpression__EveryNight_Deep.setCronExpression...");
            oCronTrigger_EveryNight_Deep.setCronExpression(oCronExpression__EveryNight_Deep);
        } catch (Exception oException) {
            LOG.error("Bad: ", oException.getMessage());
            LOG.debug("FAIL:", oException);
            //oException.printStackTrace();
        }
        // ��������� ������� � ������� JobDetail � Trigger
        LOG.info("scheduleJob...");
        oScheduler.scheduleJob(oJobDetail_Escalation_Standart, oCronTrigger_EveryNight_Deep);

        // ��������� �����������
        LOG.info("start...");
        oScheduler.start();
        LOG.info("Ok!!");

    }

}


