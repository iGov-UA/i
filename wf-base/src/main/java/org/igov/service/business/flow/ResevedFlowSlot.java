package org.igov.service.business.flow;

import java.util.HashMap;
import java.util.Map;
import org.igov.io.web.integration.queue.cherg.Cherg;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

/**
 * @author olha
 */
@Component("resevedFlowSlot")
@Scope("prototype")
public class ResevedFlowSlot {

    private final static Logger LOG = LoggerFactory.getLogger(ResevedFlowSlot.class);

    private final Map<String, String> mReservedSlot = new HashMap<>();

    @Autowired
    Cherg cherg;

    public void setReservedSlot(String nReserve_id, String phone) {
        mReservedSlot.put(phone, nReserve_id);
    }

    public Map<String, String> canselReservedSlot(String phone) throws Exception {
        try {
            LOG.info("sPhoneNumber = {} reserve_id = {}", phone, mReservedSlot.get(phone));
            if (mReservedSlot.get(phone) != null) {
                cherg.canselReserve(mReservedSlot.get(phone));
                mReservedSlot.remove(phone);
            }
        } catch (Exception ex) {
            LOG.error("canselReservedSlot: ", ex);
        }
        return mReservedSlot;
    }
}
