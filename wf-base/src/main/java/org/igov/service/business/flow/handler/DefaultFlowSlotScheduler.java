package org.igov.service.business.flow.handler;

import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.igov.model.flow.FlowSlotDao;
import org.igov.model.flow.FlowSlot;
import org.igov.util.JSON.JsonRestUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * User: goodg_000
 * Date: 29.06.2015
 * Time: 18:57
 */
public class DefaultFlowSlotScheduler extends BaseFlowSlotScheduler {
    
    protected static final Logger LOG = LoggerFactory.getLogger(DefaultFlowSlotGenerator.class);

    protected int maxGeneratedSlotsCount;

    protected String defaultFlowSlotName;

    protected FlowSlotDao flowSlotDao;

    @Required
    public void setMaxGeneratedSlotsCount(int maxGeneratedSlotsCount) {
        this.maxGeneratedSlotsCount = maxGeneratedSlotsCount;
    }

    @Required
    public void setDefaultFlowSlotName(String defaultFlowSlotName) {
        this.defaultFlowSlotName = defaultFlowSlotName;
    }

    @Autowired
    public void setFlowSlotDao(FlowSlotDao flowSlotDao) {
        this.flowSlotDao = flowSlotDao;
    }

    @Override
    public List<FlowSlot> generateObjects(String sData) {
        Map<String, String> configuration = JsonRestUtils.readObject(sData, Map.class);

        prepareInterval();

        DefaultFlowSlotGenerator generator = new DefaultFlowSlotGenerator();
        List<FlowSlot> generatedSlots = generator.generateObjects(configuration, startDate, endDate,
                maxGeneratedSlotsCount, defaultFlowSlotName);

        MultiValueMap<String, FlowSlot> flowSlotsGeneratedByDuration = new LinkedMultiValueMap<>();
        for (FlowSlot slot : generatedSlots) {
            flowSlotsGeneratedByDuration.put(slot.getsDuration(), generatedSlots);
        }

        List<FlowSlot> res = new ArrayList<>();

        for (Map.Entry<String, List<FlowSlot>> entry : flowSlotsGeneratedByDuration.entrySet()) {
            String duration = entry.getKey();
            List<FlowSlot> slots = entry.getValue();

            List<DateTime> datesToUpdateSlotsIn = new ArrayList<>();

            DateTime minDateTime = null;
            DateTime maxDateTime = null;
            for (FlowSlot slot : slots) {
                DateTime dateTime = slot.getsDate();
                if (minDateTime == null) {
                    minDateTime = slot.getsDate();
                    maxDateTime = minDateTime;
                } else {
                    if (minDateTime.isAfter(dateTime)) {
                        minDateTime = dateTime;
                    }
                    if (maxDateTime.isBefore(dateTime)) {
                        maxDateTime = dateTime;
                    }
                }
            }

            if (!slots.isEmpty()) {
                Set<DateTime> existingDates = flowSlotDao.findFlowSlotsDates(flow.getId(), minDateTime, maxDateTime);
                for(DateTime startDateTime : existingDates){
                    LOG.info("Date already exist startDateTime: " + startDateTime);
                }
                for (FlowSlot slot : slots) {
                    if (!existingDates.contains(slot.getsDate())) {
                        slot.setFlow(flow);
                        slot = flowSlotDao.saveOrUpdate(slot);
                        res.add(slot);
                        LOG.info("createNewSlot: slot.getId={} flow.getId={} getsDate={} getsDuration={}", slot.getId(), flow.getId(), slot.getsDate(), slot.getsDuration());
                    } else {
                        datesToUpdateSlotsIn.add(slot.getsDate());
                        LOG.info("datesToUpdateSlotsIn: slot.getId={} flow.getId={} getsDate={} getsDuration={}", slot.getId(), flow.getId(), slot.getsDate(), slot.getsDuration());
                    }
                }

                if (!datesToUpdateSlotsIn.isEmpty()) {
                    flowSlotDao.updateSlots(flow.getId(), datesToUpdateSlotsIn, duration);
                }
            }

        }

        return res;
    }

}
