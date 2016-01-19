package org.igov.model.flow;

import org.igov.model.core.GenericEntityDao;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

/**
 * User: goodg_000
 * Date: 22.06.2015
 * Time: 0:03
 */
@Repository
public class FlowSlotTicketDaoImpl extends GenericEntityDao<FlowSlotTicket> implements FlowSlotTicketDao {

    private static final Logger LOG = LoggerFactory.getLogger(FlowSlotTicketDaoImpl.class);

    public FlowSlotTicketDaoImpl() {
        super(FlowSlotTicket.class);
    }

    @Override
    public FlowSlotTicket findFlowSlotTicket(Long nID_FlowSlot) {
        return findBy("oFlowSlot.id", nID_FlowSlot).orNull();
    }

    @Override
    public boolean unbindFromTask(Long nID_FlowSlotTicket) {
        LOG.info("(nID_FlowSlotTicket={})", nID_FlowSlotTicket);
        FlowSlotTicket flowSlotTicket = findByIdExpected(nID_FlowSlotTicket);

        if (flowSlotTicket.getnID_Task_Activiti() == null) {
            LOG.info("Ticket is not bound to any task. Skip unbind operation. (id={})", flowSlotTicket.getId());

            return false;
        }

        LOG.info("Ticket is unbound from Task. (id={})", flowSlotTicket.getId(),
                flowSlotTicket.getnID_Task_Activiti());

        flowSlotTicket.setnID_Task_Activiti(null);
        // flowSlotTicket.setsDateEdit(DateTime.now());  do we need this?
        saveOrUpdate(flowSlotTicket);

        return true;
    }
}
