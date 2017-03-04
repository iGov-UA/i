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

    public Expression sDateVacationBegin;

    public Expression sDateVacationEnd;

    public Expression sTypeVacation;

    public Expression sNote;

    public Expression sKeyResponsible;

    public Expression sKeyOrgan;

    public Expression sKeySubjectType;

    @Override
    public void execute(DelegateExecution execution) throws Exception {
        try {
            //по ид вытащить того, кто отрабатывает и достать кеш человека ..
            
            String filePath = FileSystemData.SUB_PATH_XML + "agroholding/";
            File oFile = FileSystemData.getFile(filePath, "documentVacation.xml");
            String documentVacation = Files.toString(oFile, Charset.defaultCharset());
            //подмена данных
            agroholdingService.transferDocumentVacation(documentVacation);
        } catch (Exception ex) {
            LOG.error("transferDocumentVacation: ", ex);
        }
    }
}
