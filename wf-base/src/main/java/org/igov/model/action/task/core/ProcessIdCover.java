package org.igov.model.action.task.core;

import org.igov.service.controller.ExceptionCommonController;
import org.igov.service.exception.CommonServiceException;
import org.igov.util.ToolLuna;
import org.springframework.http.HttpStatus;

/**
 *
 * @author Belyavtsev Vladimir Vladimirovich (BW)
 */
public class ProcessIdCover {

    private String sID_Order;
    private Long nID_Protected;
    private Long nID_Process;
    private Integer nID_Server;

    public ProcessIdCover(String sID_Order, Long nID_Protected, Long nID_Process, Integer nID_Server)
            throws CommonServiceException {
        if (sID_Order != null) {
            this.sID_Order = sID_Order;
            int dash_position = sID_Order.indexOf("-");
            this.nID_Server = dash_position != -1 ? Integer.parseInt(sID_Order.substring(0, dash_position)) : 0;
            this.nID_Protected = Long.valueOf(sID_Order.substring(dash_position + 1));
            this.nID_Process = ToolLuna.getOriginalNumber(this.nID_Protected);
        } else if (nID_Process != null) {
            this.nID_Process = nID_Process;
            this.nID_Protected = ToolLuna.getProtectedNumber(nID_Process);
            this.nID_Server = nID_Server != null ? nID_Server : 0;
            this.sID_Order = "" + this.nID_Server + "-" + this.nID_Protected;

        } else if (nID_Protected != null) {
            this.nID_Protected = nID_Protected;
            this.nID_Process = ToolLuna.getOriginalNumber(this.nID_Protected);
            this.nID_Server = nID_Server != null ? nID_Server : 0;
            this.sID_Order = "" + this.nID_Server + "-" + this.nID_Protected;
        } else {
            throw new CommonServiceException(
                    ExceptionCommonController.BUSINESS_ERROR_CODE,
                    "incorrect input data!! must be: [sID_Order] OR [nID_Protected + nID_Server (optional)] OR [nID_Process + nID_Server(optional)]",
                    HttpStatus.FORBIDDEN);
        }
    }

    public String sID_Order() {
        return sID_Order;
    }

    public Long nID_Protected() {
        return nID_Protected;
    }

    public Long nID_Process() {
        return nID_Process;
    }

    public Integer nID_Server() {
        return nID_Server;
    }
}
