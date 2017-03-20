package org.igov.service.business.flow.handler;

import org.joda.time.DateTime;

/**
 *
 * @author Kovilin
 */
public class ExcludeDateRange {
    
    private DateTime sDateTimeAt;
    private DateTime sDateTimeTo;

    public DateTime getsDateTimeAt() {
        return sDateTimeAt;
    }

    public DateTime getsDateTimeTo() {
        return sDateTimeTo;
    }

    public void setsDateTimeAt(DateTime sDateTimeAt) {
        this.sDateTimeAt = sDateTimeAt;
    }

    public void setsDateTimeTo(DateTime sDateTimeTo) {
        this.sDateTimeTo = sDateTimeTo;
    }
    
}
