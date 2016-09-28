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
     *
     * @param snID_Process
     * @return 
     * @throws Exception
     */
    public String runFeedBack(String snID_Process) throws Exception {
        String snID_Proccess_Feedback = bpHandler
                    .startFeedbackProcessNew(snID_Process);
        if (!generalConfig.isFeedbackCountExpired(BpServiceHandler.getFeedBackCount())) {
            BpServiceHandler.setFeedBackCount(BpServiceHandler.getFeedBackCount() + 1);
            
            if (snID_Proccess_Feedback == null || snID_Proccess_Feedback.isEmpty()) {
                throw new Exception("FeedBack proces not started");
            }
            return snID_Proccess_Feedback;
        } else {
            LOG.info("Skip start process feedback: " + BpServiceHandler.getFeedBackCount());
            return null;
        }
    }

}
