package org.igov.service.business.action.task.systemtask.misc;

import org.igov.service.controller.security.AuthenticationTokenSelector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.igov.service.business.access.AccessDataService;
import org.igov.service.business.access.AccessKeyService;
import org.igov.io.GeneralConfig;
import org.igov.service.controller.security.AccessContract;

@Component
public class CancelTaskUtil {

    private static final Logger LOG = LoggerFactory.getLogger(CancelTaskUtil.class);
    private static final String sURL_CancelTask = "/wf/service/action/task/cancelTask";
    private static final String TAG_action = "[sURL_CancelTask]";
    private static final String cancelButtonHTML = new StringBuilder()
            .append("<form method=\"POST\" action=\"")
            .append(TAG_action)
            .append("\" ")
            .append("accept-charset=\"utf-8\">")
            .append("Ви можете скасувати свою заявку, вказавши причину в цьому полі: <br/>\n")
            .append("<input type=\"text\" name=\"sInfo\"/><br/>\n")
                    //.append("<input type=\"hidden\" name=\"nID_Protected\" value=\"")
                    //.append(TAG_nID_Protected + "\"/><br/>\n")
            .append("<input type=\"submit\" name=\"submit\" ")
            .append("value=\"Скасувати заявку!\"/>")
            .append("</form>")
            .toString();
    //private static String TAG_nID_Protected = "[nID_Protected]";
    @Autowired
    AccessKeyService accessCover;
    @Autowired
    private AccessDataService accessDataDao;
    @Autowired
    private GeneralConfig generalConfig;

    public String getCancelFormHTML(Long nID_Order) throws Exception {

        String sURL_ForAccessKey = new StringBuilder(sURL_CancelTask)
                .append("?nID_Order=").append(nID_Order)
                //TODO: Need remove in future!!!
                .append("&").append(AuthenticationTokenSelector.ACCESS_CONTRACT).append("=")
                .append(AccessContract.RequestAndLoginUnlimited.name())//.append("&sAccessContract=Request")
                .toString();
        //String sAccessKey = accessDataDao.setAccessData(sURL_ForAccessKey);
        String sAccessKey = accessCover.getAccessKey(sURL_ForAccessKey);
        String sURL_CancelTaskAction = new StringBuilder(generalConfig.sHost())
                .append(sURL_ForAccessKey)
                .append("&").append(AuthenticationTokenSelector.ACCESS_KEY).append("=").append(sAccessKey)//.append("&sAccessKey=").append(sAccessKey)
                .toString();
        LOG.info("total URL for action ={}", sURL_CancelTaskAction);

        String cancelBtn = cancelButtonHTML
                .replace(TAG_action, sURL_CancelTaskAction)
                //.replace(TAG_nID_Protected, "" + nID_Order)
                ;
        return cancelBtn;
    }
}
