package org.igov.service.business.action.task.systemtask.export;

import com.google.common.io.Files;
import java.io.File;
import java.nio.charset.Charset;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;
import java.util.Map.Entry;
import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.delegate.Expression;
import org.activiti.engine.delegate.JavaDelegate;
import org.igov.io.fs.FileSystemData;
import org.igov.service.business.action.task.listener.doc.CreateDocument_UkrDoc;
import org.igov.service.business.export.AgroholdingService;
import static org.igov.util.Tool.parseData;
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

    private Expression sID_Pattern;

    private Expression soData;

    private final String SYMBOL = "%";

    @Override
    public void execute(DelegateExecution execution) throws Exception {
        //по ид вытащить того, кто отрабатывает и достать кеш человека .
        String soData_Value = this.soData.getExpressionText();
        Map<String, Object> data = parseData(soData_Value);
        LOG.info("data: " + data);

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-ddTHH-mm-ss");
        String sDate = sdf.format(new Date());
        Date oDateVacationBegin = sdf.parse((String) data.get("sDateVacationBegin"));
        Date oDateVacationEnd = sdf.parse((String) data.get("sDateVacationEnd"));
        String sCountDay = String.valueOf(getDateDiff(oDateVacationEnd, oDateVacationBegin));
        String sKeyVacationer = getKeyUser(execution.getProcessInstanceId());
        data.put("sDate", sDate);
        data.put("sCountDay", sCountDay);
        data.put("sKeyVacationer", sKeyVacationer);
        LOG.info("Transfer_DocumentVacation data: " + data);

        String filePath = FileSystemData.SUB_PATH_XML + "agroholding/";
        File oFile = FileSystemData.getFile(filePath, sID_Pattern + ".xml");
        String documentVacation = Files.toString(oFile, Charset.defaultCharset());
        LOG.info("Transfer_DocumentVacation documentVacation before: " + documentVacation);
        
        for (Entry<String, Object> entry : data.entrySet()) {
            documentVacation = documentVacation.replaceAll(SYMBOL + entry.getKey(), String.valueOf(entry.getValue()));
        }
        LOG.info("Transfer_DocumentVacation documentVacation after: " + documentVacation);
        
        String result = agroholdingService.transferDocumentVacation(documentVacation);
        LOG.info("Transfer_DocumentVacation result: " + result);
    }

    private Integer getDateDiff(java.util.Date date1, java.util.Date date2) {
        return Math.round((date2.getTime() - date1.getTime()) / (1000 * 60 * 60 * 24));
    }

    private String getKeyUser(String sID_Process) {
        return "a807e909-abfb-11dc-aa58-00112f3000a2";
    }
}
