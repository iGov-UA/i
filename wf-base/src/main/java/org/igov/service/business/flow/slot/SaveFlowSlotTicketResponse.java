package org.igov.service.business.flow.slot;

/**
 * User: goodg_000
 * Date: 21.06.2015
 * Time: 21:29
 */
public class SaveFlowSlotTicketResponse {

    private Long nID_Ticket;
    private Integer nSlots;

    public SaveFlowSlotTicketResponse() {
    }

    public SaveFlowSlotTicketResponse(Long nID_Ticket, Integer nSlots) {
        this.nID_Ticket = nID_Ticket;
    }

    public Long getnID_Ticket() {
        return nID_Ticket;
    }

    public void setnID_Ticket(Long nID_Ticket) {
        this.nID_Ticket = nID_Ticket;
    }

    public Integer getnSlots() {
        return nSlots;
    }

    public void setnSlots(Integer nSlots) {
        this.nSlots = nSlots;
    }
}
