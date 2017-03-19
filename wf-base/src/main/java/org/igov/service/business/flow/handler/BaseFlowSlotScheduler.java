package org.igov.service.business.flow.handler;

import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.util.Assert;
import org.igov.model.flow.FlowSlot;
import org.igov.model.flow.Flow;

import java.util.List;

/**
 * User: goodg_000
 * Date: 29.06.2015
 * Time: 20:03
 */
public abstract class BaseFlowSlotScheduler implements FlowPropertyHandler<FlowSlot> {

    protected DateTime startDate;
    protected DateTime endDate;
    protected int defaultIntervalDaysLength;
    protected Flow flow;
    protected List<ExcludeDateRange> aDateRange_Exclude;
    
    public DateTime getStartDate() {
        return startDate;
    }

    public void setStartDate(DateTime startDate) {
        this.startDate = startDate;
    }

    public DateTime getEndDate() {
        return endDate;
    }

    public void setEndDate(DateTime endDate) {
        this.endDate = endDate;
    }

    public int getDefaultIntervalDaysLength() {
        return defaultIntervalDaysLength;
    }

    @Required
    public void setDefaultIntervalDaysLength(int defaultIntervalDaysLength) {
        this.defaultIntervalDaysLength = defaultIntervalDaysLength;
    }

    public Flow getFlow() {
        return flow;
    }

    public void setFlow(Flow flow) {
        this.flow = flow;
    }

    @Override
    public Class<FlowSlot> getTargetObjectClass() {
        return FlowSlot.class;
    }

    public List<ExcludeDateRange> getaDateRange_Exclude() {
        return aDateRange_Exclude;
    }

    public void setaDateRange_Exclude(List<ExcludeDateRange> aDateRange_Exclude) {
        this.aDateRange_Exclude = aDateRange_Exclude;
    }
    
    protected void prepareInterval() {
        if (startDate == null) {
            startDate = DateTime.now();
        }
        if (endDate == null) {
            endDate = startDate.plusDays(defaultIntervalDaysLength);
        }
        Assert.isTrue(startDate.isBefore(endDate));
    }
}
