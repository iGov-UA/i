package org.wf.dp.dniprorada.dao;

import org.apache.log4j.Logger;
import org.hibernate.Criteria;
import org.hibernate.NullPrecedence;
import org.hibernate.Session;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.joda.time.DateTime;
import org.springframework.stereotype.Repository;
import org.wf.dp.dniprorada.base.dao.EntityNotFoundException;
import org.wf.dp.dniprorada.base.dao.GenericEntityDao;
import org.wf.dp.dniprorada.model.HistoryEvent_Service;
import org.wf.dp.dniprorada.util.luna.AlgorithmLuna;
import org.wf.dp.dniprorada.util.luna.CRCInvalidException;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

@Repository
public class HistoryEvent_ServiceDaoImpl extends GenericEntityDao<HistoryEvent_Service>
        implements HistoryEvent_ServiceDao {

    private static final Logger LOG = Logger.getLogger(HistoryEvent_ServiceDaoImpl.class);
    private static final String DASH = "-";
    private static final String RATE_FIELD = "nRate";
    private static final String TIME_MINUTES_FIELD = "nTimeMinutes";
    private static final String NAME_FIELD = "sName";
    private static final String COUNT_FIELD = "nCount";
    private static final int RATE_CORRELATION_NUMBER = 20; // for converting rate to percents in range 0..100
    
    protected HistoryEvent_ServiceDaoImpl() {
        super(HistoryEvent_Service.class);
    }

    public static double round(double value, int scale) {
        return Math.round(value * Math.pow(10, scale)) / Math.pow(10, scale);
    }

    @Override
    public HistoryEvent_Service addHistoryEvent_Service(HistoryEvent_Service historyEventService) {

        try {//check on duplicates
            HistoryEvent_Service duplicateEvent = getHistoryEvent_service(historyEventService.getnID_Task(),
                    historyEventService.getnID_Server());
            if (duplicateEvent != null) {
                throw new IllegalArgumentException(
                        "Cannot create historyEventService with the same nID_Process and nID_Server!");
            }
        } catch (EntityNotFoundException ex) {
            LOG.info("create new historyEventService", ex);/*NOP*/
        }
        historyEventService.setsDate(new DateTime());
        Long nID_Protected = AlgorithmLuna.getProtectedNumber(historyEventService.getnID_Task());
        historyEventService.setnID_Protected(nID_Protected);
        historyEventService.setsID_Order(historyEventService.getnID_Server() + DASH + nID_Protected);
        Session session = getSession();
        session.saveOrUpdate(historyEventService);
        return historyEventService;
    }

    @Override
    public HistoryEvent_Service updateHistoryEvent_Service(HistoryEvent_Service event_service) {
        event_service.setsDate(new DateTime());
        return saveOrUpdate(event_service);
    }

    @Override
    public List<Map<String, Long>> getHistoryEvent_ServiceBynID_Service(Long nID_Service) {

        List<Map<String, Long>> resHistoryEventService = new LinkedList<>();
        if (nID_Service == 159) {
            Map<String, Long> currRes = new HashMap<>();
            currRes.put(NAME_FIELD, 5L);
            currRes.put(COUNT_FIELD, 1L);
            currRes.put(RATE_FIELD, 0L);
            currRes.put(TIME_MINUTES_FIELD, 0L);
            resHistoryEventService.add(currRes);
        }
        Criteria criteria = getSession().createCriteria(HistoryEvent_Service.class);
        criteria.add(Restrictions.eq("nID_Service", nID_Service));
        criteria.setProjection(Projections.projectionList()
                .add(Projections.groupProperty("nID_Region"))
                .add(Projections.count("nID_Service"))
                        .add(Projections.avg(RATE_FIELD)) //for issue 777
                        .add(Projections.avg(TIME_MINUTES_FIELD))
        );
        Object res = criteria.list();
        LOG.info("Received result in getHistoryEvent_ServiceBynID_Service:" + res);
        if (res == null) {
            LOG.warn("List of records based on nID_Service not found" + nID_Service);
            throw new EntityNotFoundException("Record not found");
        }
        int i = 0;
        for (Object item : criteria.list()) {
            Object[] currValue = (Object[]) item;
            Long nID_Region = (long) 0x0;
            if(currValue[0] != null){
                nID_Region = (Long) currValue[0];
            }
            
            LOG.info(String.format("Line %s: %s, %s, %s, %s", i, nID_Region, currValue[1],
                    currValue[2] != null ? currValue[2] : "",
                    currValue[3] != null ? currValue[3] : ""));
            i++;
            Long rate = 0L;
            try {
                Double nRate = (Double) currValue[2];
                LOG.info("nRate=" + nRate);
                if (nRate != null) {
                    String snRate = "" + nRate * RATE_CORRELATION_NUMBER;
                    //String snRate = "" + round(nRate, 1);
                    LOG.info("snRate=" + snRate);
                    if (snRate.contains(".")) {
                        rate = Long.valueOf(snRate.substring(0, snRate.indexOf(".")));
                        LOG.info("total rate = " + rate);
                    }
                }
            } catch (Exception oException) {
                LOG.error("cannot get nRate! " + currValue[2] + " caused: " + oException.getMessage(), oException);
            }
            BigDecimal timeMinutes = null;
            try {
                Double nTimeMinutes = (Double) currValue[3];
                LOG.info("nTimeMinutes=" + nTimeMinutes);
                if (nTimeMinutes != null) {
                    timeMinutes = BigDecimal.valueOf(nTimeMinutes);
                    timeMinutes = timeMinutes.abs();
                }
            } catch (Exception oException) {
                LOG.error("cannot get nTimeMinutes! " + currValue[3] + " caused: " + oException.getMessage(),
                        oException);
            }
            Map<String, Long> currRes = new HashMap<>();
            currRes.put(NAME_FIELD, nID_Region); //currValue[0]);
            currRes.put(COUNT_FIELD, (Long) currValue[1]);
            currRes.put(RATE_FIELD, rate);
            currRes.put(TIME_MINUTES_FIELD, timeMinutes != null ? timeMinutes.longValue() : 0L);
            resHistoryEventService.add(currRes);
        }
        LOG.info("Found " + resHistoryEventService.size() + " records based on nID_Service " + nID_Service);

        return resHistoryEventService;
    }
    
    @Override
    public HistoryEvent_Service getOrgerByID(String sID_Order) throws CRCInvalidException {
        Integer serverId;
        Long protectedId;
        try {
            int dashPosition = sID_Order.indexOf(DASH);
            serverId = Integer.parseInt(sID_Order.substring(0, dashPosition));
            protectedId = Long.valueOf(sID_Order.substring(dashPosition + 1));
        } catch (Exception e) {
            throw new IllegalArgumentException(
                    String.format("sID_Order has incorrect format! expected format:[XXX%sXXXXXX], actual value: %s",
                            DASH, sID_Order), e);
        }
        return getOrgerByProtectedID(protectedId, serverId);
    }

    @Override
    public HistoryEvent_Service getOrgerByProcessID(Long nID_Process, Integer nID_Server) {
        HistoryEvent_Service historyEventService = getHistoryEvent_service(nID_Process, nID_Server);
        historyEventService.setnID_Protected(AlgorithmLuna.getProtectedNumber(nID_Process));
        return historyEventService;
    }

    @Override
    public HistoryEvent_Service getOrgerByProtectedID(Long nID_Protected, Integer nID_Server)
            throws CRCInvalidException {
        AlgorithmLuna.validateProtectedNumber(nID_Protected);
        Long nID_Process = AlgorithmLuna.getOriginalNumber(nID_Protected);
        HistoryEvent_Service historyEventService = getHistoryEvent_service(nID_Process, nID_Server);
        historyEventService.setnID_Protected(nID_Protected);
        return historyEventService;
    }

    @SuppressWarnings("unchecked")
    private HistoryEvent_Service getHistoryEvent_service(Long nID_Process, Integer nID_Server) {
        Criteria criteria = getSession().createCriteria(HistoryEvent_Service.class);
        criteria.addOrder(
                Order.desc("sDate").nulls(NullPrecedence.LAST));//todo remove after fix dublicates. todo uniqueResult
        criteria.add(Restrictions.eq("nID_Task", nID_Process));
        Integer serverId = nID_Server != null ? nID_Server : 0;
        criteria.add(Restrictions.eq("nID_Server", serverId));
        List<HistoryEvent_Service> list = (List<HistoryEvent_Service>) criteria.list();
        HistoryEvent_Service historyEventService = !list.isEmpty() ? list.get(0) : null;
        if (historyEventService == null) {
            throw new EntityNotFoundException(
                    String.format("Record with nID_Server=%s and nID_Process=%s not found!", serverId, nID_Process));
        }
        return historyEventService;
    }

    @Override
    public HistoryEvent_Service getLastTaskHistory(Long nID_Subject, Long nID_Service, String sID_UA) {
        Criteria criteria = getSession().createCriteria(HistoryEvent_Service.class);
        criteria.add(Restrictions.eq("nID_Subject", nID_Subject));
        criteria.add(Restrictions.eq("nID_Service", nID_Service));
        criteria.add(Restrictions.eq("sID_UA", sID_UA));
        criteria.addOrder(Order.desc("id"));
        criteria.setMaxResults(1);
        HistoryEvent_Service event_service = (HistoryEvent_Service) criteria.uniqueResult();
        if (event_service != null) {
            event_service.setnID_Protected(AlgorithmLuna.getProtectedNumber(event_service.getnID_Task()));
        }
        return event_service;
    }

}
