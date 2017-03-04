package org.igov.service.business.action.task.systemtask.export;

import com.google.common.io.Files;
import java.io.File;
import java.nio.charset.Charset;
import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.delegate.DelegateTask;
import org.activiti.engine.delegate.Expression;
import org.activiti.engine.delegate.JavaDelegate;
import org.activiti.engine.delegate.TaskListener;
import org.igov.io.fs.FileSystemData;
import org.igov.service.business.action.task.listener.doc.CreateDocument_UkrDoc;
import org.igov.service.business.export.AgroholdingService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 *
 * @author askosyr
 *
 */
@Component("Transfer_DocumentVacation")
public class Transfer_DocumentVacation implements JavaDelegate {

    private final static Logger LOG = LoggerFactory.getLogger(CreateDocument_UkrDoc.class);

    @Autowired
    AgroholdingService agroholdingService;

    private Expression sDateVacationBegin;

    private Expression sDateVacationEnd;

    private Expression sTypeVacation;

    private Expression sNote;

    private Expression sKeyResponsible;

    private Expression sKeyOrgan;

    private Expression sKeySubjectType;
    
    private String symbol = "%";

    @Override
    public void execute(DelegateExecution execution) throws Exception {
        try {
            //по ид вытащить того, кто отрабатывает и достать кеш человека ..

            String filePath = FileSystemData.SUB_PATH_XML + "agroholding/";
            File oFile = FileSystemData.getFile(filePath, "documentVacation.xml");
            String documentVacation = Files.toString(oFile, Charset.defaultCharset());
            //подмена данных
            String sDate = "";
            String sCountDay = "";
            String sKeyVacationer = "a807e909-abfb-11dc-aa58-00112f3000a2";
            agroholdingService.transferDocumentVacation(documentVacation);
        } catch (Exception ex) {
            LOG.error("transferDocumentVacation: ", ex);
        }
    }
}
