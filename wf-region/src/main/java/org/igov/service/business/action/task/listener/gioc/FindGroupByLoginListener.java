package org.igov.service.business.action.task.listener.gioc;

/**
 *
 * @author skuhtin
 */
import java.util.HashMap;
import java.util.Map;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.delegate.DelegateTask;
import org.activiti.engine.delegate.Expression;
import org.activiti.engine.delegate.TaskListener;
import org.igov.io.GeneralConfig;
import org.igov.io.web.HttpRequester;
import org.slf4j.LoggerFactory;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component("selectGroupByLogin")
public class FindGroupByLoginListener implements TaskListener {

    @Autowired
    private GeneralConfig generalConfig;

    @Autowired
    private HttpRequester httpRequester;

    @Autowired
    private RuntimeService runtimeService;

    Expression sLoginTask2Group;

    private static final Logger LOG = LoggerFactory.getLogger(FindGroupByLoginListener.class);

    @Override
    public void notify(DelegateTask delegateTask) {

        DelegateExecution oExecution = delegateTask.getExecution();

        String sLoginTask2Group = getStringFromFieldExpression(this.sLoginTask2Group, oExecution);

        if (sLoginTask2Group != null) {
            String URI = "/wf/service/action/identity/getGroups";
            Map<String, String> part = new HashMap<>();

            part.put("sLoginTask2Group", sLoginTask2Group);

            String sListOfGroupsTask2 = null;

            try {
                sListOfGroupsTask2 = httpRequester.getInside(generalConfig.getSelfHostCentral() + URI, part);
            } catch (Exception oException) {
                LOG.error("Error: {}, Exception occured while calling SendToGIOC method", oException.getMessage());
                LOG.trace("FAIL:", oException);
            }

            runtimeService.setVariable(oExecution.getProcessInstanceId(), "sListOfGroupsTask2", sListOfGroupsTask2);
            LOG.info("Set variable to runtime process:{}", sListOfGroupsTask2);
        }

    }

    protected String getStringFromFieldExpression(Expression expression,
            DelegateExecution execution) {
        if (expression != null) {
            Object value = expression.getValue(execution);
            if (value != null) {
                return value.toString();
            }
        }
        return null;
    }
}
