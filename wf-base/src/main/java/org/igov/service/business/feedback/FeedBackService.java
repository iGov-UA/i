package org.igov.service.business.feedback;

import org.igov.io.GeneralConfig;
import org.igov.service.business.action.task.bp.handler.BpServiceHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class FeedBackService {

    @SuppressWarnings("unused")
    private static final Logger LOG = LoggerFactory.getLogger(FeedBackService.class);

    @Autowired
    private BpServiceHandler bpHandler;

    @Autowired
    private GeneralConfig generalConfig;

    /**
     * Запуск процесса фидбэка
     * Сброс счетчика в JobFeedBack
     *
     * @param snID_Process
     * @return 
     * @throws Exception
     */
    public String runFeedBack(String snID_Process) throws Exception {
        String snID_Proccess_Feedback = null;
       
        if (!generalConfig.isFeedbackCountExpired(BpServiceHandler.getFeedBackCount())) {
            snID_Proccess_Feedback = bpHandler
                    .startFeedbackProcessNew(snID_Process);
            if (snID_Proccess_Feedback == null || snID_Proccess_Feedback.isEmpty()) {
                throw new Exception("FeedBack proces not started for snID_Process: "+snID_Process);
            }
            BpServiceHandler.setFeedBackCount(BpServiceHandler.getFeedBackCount() + 1);
            return snID_Proccess_Feedback;
        } else {
            LOG.info("Skip start process feedback: " + BpServiceHandler.getFeedBackCount());
            return snID_Proccess_Feedback;
        }
    }

}
