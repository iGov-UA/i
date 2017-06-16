package org.igov.model.action.event;

import org.hibernate.Criteria;
import org.hibernate.NullPrecedence;
import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.igov.model.core.GenericEntityDao;
import org.igov.service.exception.CRCInvalidException;
import org.igov.service.exception.EntityNotFoundException;
import org.igov.util.ToolLuna;
import org.igov.util.db.queryloader.QueryLoader;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

@Repository
public class HistoryEvent_ServiceDaoImpl extends GenericEntityDao<Long, HistoryEvent_Service>
        implements HistoryEvent_ServiceDao {

    public static final String DASH = "-";
    private static final Logger LOG = LoggerFactory.getLogger(HistoryEvent_ServiceDaoImpl.class);
    private static final String RATE_FIELD = "nRate";
    private static final String TIME_MINUTES_FIELD = "nTimeMinutes";
    private static final String NAME_FIELD = "sName";
    private static final String COUNT_FIELD = "nCount";
    private static final int RATE_CORRELATION_NUMBER = 20; // for converting rate to percents in range 0..100
    private static final String GET_SERVICES_STATISTICS_QUERY = "get_ServicesStatistics.sql";
    private static final String GET_SERVICES_STATISTICS_QUERY_DNEPR = "get_ServicesStatisticsOfDnepr.sql";

    @Autowired
    private QueryLoader sqlStorage;

    protected HistoryEvent_ServiceDaoImpl() {
        super(HistoryEvent_Service.class);
    }

    public static double round(double value, int scale) {
        return Math.round(value * Math.pow(10, scale)) / Math.pow(10, scale);
    }

    @Override
    public HistoryEvent_Service addHistoryEvent_Service(HistoryEvent_Service historyEventService) {

        try {//check on duplicates
            HistoryEvent_Service duplicateEvent = getHistoryEvent_service(historyEventService.getnID_Process(),
                    historyEventService.getnID_Server());
            if (duplicateEvent != null) {
                throw new IllegalArgumentException(
                            "Cannot create historyEventService with the same nID_Process and nID_Server!");
            }
        } catch (EntityNotFoundException ex) {
            LOG.info("create new historyEventService");/*NOP*/
        }
        historyEventService.setsDate(new DateTime());
        historyEventService.setsDateCreate(new DateTime());
        Long nID_Protected = ToolLuna.getProtectedNumber(historyEventService.getnID_Process());
        historyEventService.setnID_Protected(nID_Protected);
        historyEventService.setsID_Order(historyEventService.getnID_Server() + DASH + nID_Protected);
        Session session = getSession();
        session.saveOrUpdate(historyEventService);
        return historyEventService;
    }

    @Override
    public HistoryEvent_Service updateHistoryEvent_Service(HistoryEvent_Service event_service) {
        event_service.setsDate(new DateTime());
        if (event_service.getnID_StatusType() != null && event_service.getnID_StatusType() == 8) {
            event_service.setsDateClose(new DateTime());
        }
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
        LOG.info("Received result in getHistoryEvent_ServiceBynID_Service:{}", res);
        if (res == null) {
            LOG.warn("List of records based on nID_Service not found {}", nID_Service);
            throw new EntityNotFoundException("Record not found");
        }
        int i = 0;
        for (Object item : criteria.list()) {
            Object[] currValue = (Object[]) item;
            Long nID_Region = (long) 0x0;
            if (currValue[0] != null) {
                nID_Region = (Long) currValue[0];
            }

            LOG.info(String.format("Line %s: %s, %s, %s, %s", i, nID_Region, currValue[1],
                    currValue[2] != null ? currValue[2] : "",
                    currValue[3] != null ? currValue[3] : ""));
            i++;
            Long rate = 0L;
            try {
                Double nRate = (Double) currValue[2];
                LOG.info("nRate={}", nRate);
                if (nRate != null) {
                    String snRate = "" + nRate * RATE_CORRELATION_NUMBER;
                    //String snRate = "" + round(nRate, 1);
                    LOG.info("snRate={}", snRate);
                    if (snRate.contains(".")) {
                        rate = Long.valueOf(snRate.substring(0, snRate.indexOf(".")));
                        LOG.info("total rate = {}", rate);
                    }
                }
            } catch (Exception oException) {
                LOG.error("Error:{}, cannot get nRate! {}", oException.getMessage(), currValue[2]);
                LOG.trace("FAIL:", oException);
            }
            BigDecimal timeMinutes = null;
            try {
                Double nTimeMinutes = (Double) currValue[3];
                LOG.info("nTimeMinutes={}", nTimeMinutes);
                if (nTimeMinutes != null) {
                    timeMinutes = BigDecimal.valueOf(nTimeMinutes);
                    timeMinutes = timeMinutes.abs();
                }
            } catch (Exception oException) {
                LOG.error("Error: {}, cannot get nTimeMinutes!{}", oException.getMessage(), currValue[3]);
                LOG.trace("FAIL:", oException);
            }
            Map<String, Long> currRes = new HashMap<>();
            currRes.put(NAME_FIELD, nID_Region); //currValue[0]);
            currRes.put(COUNT_FIELD, (Long) currValue[1]);
            currRes.put(RATE_FIELD, rate);
            currRes.put(TIME_MINUTES_FIELD, timeMinutes != null ? timeMinutes.longValue() : 0L);
            resHistoryEventService.add(currRes);
        }
        LOG.info("Found {} records based on nID_Service={}", resHistoryEventService.size(), nID_Service);

        return resHistoryEventService;
    }

    @Override
    public List<ServicesStatistics> getServicesStatistics(DateTime from, DateTime to) {
        String queryString = sqlStorage.get(GET_SERVICES_STATISTICS_QUERY);

        List<ServicesStatistics> servicesStatistics = null;
        SQLQuery query = getSession().createSQLQuery(queryString);
        query.setParameter("dateFrom", from.toString("y-MM-d HH:mm:ss"));
        query.setParameter("dateTo", to.toString("y-MM-d HH:mm:ss"));
        query.addEntity(ServicesStatistics.class);

        servicesStatistics = query.list();

        return servicesStatistics;
    }

    @Override
    public List<ServicesStatistics> getServicesStatisticsOfDnepr(DateTime from, DateTime to) {
        String queryString = sqlStorage.get(GET_SERVICES_STATISTICS_QUERY_DNEPR);

        List<ServicesStatistics> servicesStatistics = null;
        SQLQuery query = getSession().createSQLQuery(queryString);
        query.setParameter("dateFrom", from.toString("y-MM-d HH:mm:ss"));
        query.setParameter("dateTo", to.toString("y-MM-d HH:mm:ss"));
        query.addEntity(ServicesStatistics.class);

        servicesStatistics = query.list();
        
        LOG.info("List servicesStatistics after get sql = {} ", servicesStatistics );

        return servicesStatistics;
    }
    
    @Override
    public HistoryEvent_Service getOrgerByID(String sID_Order) throws CRCInvalidException, EntityNotFoundException, IllegalArgumentException {
        Integer nID_Server;
        Long nID_Order;
        try {
            String[] as = sID_Order.split("\\-");
            nID_Server = Integer.parseInt(as[0]);
            nID_Order = Long.valueOf(as[1]);
            /*int dashPosition = sID_Order.indexOf(DASH);
            nID_Server = Integer.parseInt(sID_Order.substring(0, dashPosition));
            nID_Order = Long.valueOf(sID_Order.substring(dashPosition + 1));*/
        } catch (Exception e) {
            LOG.error("getOrgerByID: ", e);
            throw new IllegalArgumentException(
                    String.format("sID_Order has incorrect format! expected format:[XXX%sXXXXXX], actual value: %s",
                            DASH, sID_Order), e);
        }
        return getOrgerByProtectedID(nID_Order, nID_Server);
    }

    @Override
    public HistoryEvent_Service getOrgerByProcessID(Long nID_Process, Integer nID_Server) {
        return getHistoryEvent_service(nID_Process, nID_Server);
    }

    @Override
    public HistoryEvent_Service getOrgerByProtectedID(Long nID_Protected, Integer nID_Server)
            throws CRCInvalidException, EntityNotFoundException {
        ToolLuna.validateProtectedNumber(nID_Protected);
        Long nID_Process = ToolLuna.getOriginalNumber(nID_Protected);
        return getHistoryEvent_service(nID_Process, nID_Server);
    }

    //@SuppressWarnings("unchecked")
    private HistoryEvent_Service getHistoryEvent_service(Long nID_Process, Integer nID_Server) throws EntityNotFoundException {
        Criteria criteria = getSession().createCriteria(HistoryEvent_Service.class);
        criteria.addOrder(
                Order.desc("sDate").nulls(NullPrecedence.LAST));//todo remove after fix dublicates. todo uniqueResult
        criteria.add(Restrictions.eq("nID_Process", nID_Process));
        Integer serverId = nID_Server != null ? nID_Server : 0;
        criteria.add(Restrictions.eq("nID_Server", serverId));
        List<HistoryEvent_Service> list = (List<HistoryEvent_Service>) criteria.list();
        HistoryEvent_Service historyEventService = !list.isEmpty() ? list.get(0) : null;
        if (historyEventService == null) {
            throw new EntityNotFoundException(
                    String.format("Record with nID_Server=%s and nID_Process=%s not found!", serverId, nID_Process));
        }
        Long nID_Protected = ToolLuna.getProtectedNumber(nID_Process);
        historyEventService.setsID_Order(serverId + "-" + nID_Protected);
        historyEventService.setnID_Protected(nID_Protected);
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
            event_service.setnID_Protected(ToolLuna.getProtectedNumber(event_service.getnID_Process()));
        }
        return event_service;
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<HistoryEvent_Service> getOrdersHistory(Long nID_Subject, Long nID_Service, String sID_UA) {
        LOG.info(String.format("Start get orders history with parameters nID_Subject = %s, nID_Service = %s, sID_UA = %s", nID_Subject, nID_Service, sID_UA));
        Criteria oCriteria = getSession().createCriteria(HistoryEvent_Service.class);

        if (nID_Subject == null && nID_Service == null && sID_UA == null) {
            return new LinkedList<>();
        }

        if (nID_Subject != null) {
            oCriteria.add(Restrictions.eq("nID_Subject", nID_Subject));
        }
        if (nID_Service != null) {
            oCriteria.add(Restrictions.eq("nID_Service", nID_Service));
        }
        if (sID_UA != null && !"".equals(sID_UA)) {
            oCriteria.add(Restrictions.eq("sID_UA", sID_UA));
        }

        List<HistoryEvent_Service> aHistoryEvent_Service = (List<HistoryEvent_Service>) oCriteria.list();
        if (aHistoryEvent_Service == null) {
            LOG.warn("Result List<HistoryEvent_Service> is NULL");
            aHistoryEvent_Service = new LinkedList<>();
        }

        LOG.info("Result List<HistoryEvent_Service> size = " + aHistoryEvent_Service.size());
        return aHistoryEvent_Service;
    }

    @Override
    public List<HistoryEvent_Service> getHistoryEventPeriod(DateTime dateAt,
            DateTime dateTo, List<Long> anID_Service_Exclude) {
        Criteria oCriteria = getSession().createCriteria(HistoryEvent_Service.class);
        oCriteria.add(Restrictions.gt("sDate", dateAt));
        oCriteria.add(Restrictions.lt("sDate", dateTo));
        if (anID_Service_Exclude != null && !anID_Service_Exclude.isEmpty()) {
            oCriteria.add(Restrictions.not(Restrictions.in("nID_Service", anID_Service_Exclude)));
        }

        List<HistoryEvent_Service> aHistoryEvent_Service = (List<HistoryEvent_Service>) oCriteria.list();
        if (aHistoryEvent_Service == null) {
            aHistoryEvent_Service = new LinkedList<>();
        }

        return aHistoryEvent_Service;
    }

    @Override
    public List<HistoryEvent_Service> getHistoryEventPeriodByCreate(DateTime dateAt,
            DateTime dateTo, List<Long> anID_Service_Exclude) {
        Criteria oCriteria = getSession().createCriteria(HistoryEvent_Service.class);
        oCriteria.add(Restrictions.gt("sDateCreate", dateAt));
        oCriteria.add(Restrictions.lt("sDateCreate", dateTo));
        if (anID_Service_Exclude != null && !anID_Service_Exclude.isEmpty()) {
            oCriteria.add(Restrictions.not(Restrictions.in("nID_Service", anID_Service_Exclude)));
        }

        List<HistoryEvent_Service> aHistoryEvent_Service = (List<HistoryEvent_Service>) oCriteria.list();
        if (aHistoryEvent_Service == null) {
            aHistoryEvent_Service = new LinkedList<>();
        }

        return aHistoryEvent_Service;
    }
    
    @Override
    public List<HistoryEvent_Service> getHistoryEventPeriodByClose(DateTime dateAt,
            DateTime dateTo, List<Long> anID_Service_Exclude) {
        Criteria oCriteria = getSession().createCriteria(HistoryEvent_Service.class);
        oCriteria.add(Restrictions.gt("sDateClose", dateAt));
        oCriteria.add(Restrictions.lt("sDateClose", dateTo));
        if (anID_Service_Exclude != null && !anID_Service_Exclude.isEmpty()) {
            oCriteria.add(Restrictions.not(Restrictions.in("nID_Service", anID_Service_Exclude)));
        }

        List<HistoryEvent_Service> aHistoryEvent_Service = (List<HistoryEvent_Service>) oCriteria.list();
        if (aHistoryEvent_Service == null) {
            aHistoryEvent_Service = new LinkedList<>();
        }

        return aHistoryEvent_Service;
    }

    @Override
    public Long getClaimCountHistory(String sID_UA, Long nID_Service, Long nID_StatusType) {
        LOG.info(String.format("Start get getClaimCountHistory with parameters nID_StatusType = %s, nID_Service = %s, sID_UA = %s", nID_StatusType, nID_Service, sID_UA));

        Long countClaim = 0L;
        Criteria oCriteria = getSession().createCriteria(HistoryEvent_Service.class);
        oCriteria.setProjection(Projections.rowCount());

        if (nID_StatusType == null && nID_Service == null && sID_UA == null) {
            return countClaim;
        }

        if (sID_UA != null && !"".equals(sID_UA)) {
            oCriteria.add(Restrictions.eq("sID_UA", sID_UA));
        }

        if (nID_Service != null) {
            oCriteria.add(Restrictions.eq("nID_Service", nID_Service));
        }

        if (nID_StatusType != null) {
            oCriteria.add(Restrictions.eq("nID_StatusType", nID_StatusType));
        }

        countClaim = (Long) oCriteria.uniqueResult();
        LOG.info("countClaim size = " + countClaim);
        return countClaim;
    }
    
    @Override
    public List<HistoryEvent_Service> getHistoryEvent_Service(String sID_UA, Long nID_Service, Long nID_StatusType) {
        LOG.info(String.format("Start get getHistoryEvent_Service with parameters nID_StatusType = %s, nID_Service = %s, sID_UA = %s", nID_StatusType, nID_Service, sID_UA));

        Criteria oCriteria = getSession().createCriteria(HistoryEvent_Service.class);
        
        if (nID_StatusType == null && nID_Service == null && sID_UA == null) {
            return null;
        }

        if (sID_UA != null && !"".equals(sID_UA)) {
            oCriteria.add(Restrictions.eq("sID_UA", sID_UA));
        }

        if (nID_Service != null) {
            oCriteria.add(Restrictions.eq("nID_Service", nID_Service));
        }

        if (nID_StatusType != null) {
            oCriteria.add(Restrictions.eq("nID_StatusType", nID_StatusType));
        }

        return oCriteria.list();
    }
}
