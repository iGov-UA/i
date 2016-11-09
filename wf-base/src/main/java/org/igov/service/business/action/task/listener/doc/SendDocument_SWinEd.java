package org.igov.service.business.action.task.listener.doc;

import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.delegate.DelegateTask;
import org.activiti.engine.delegate.Expression;
import org.activiti.engine.delegate.TaskListener;
import org.igov.io.db.kv.temp.IBytesDataInmemoryStorage;
import org.igov.io.db.kv.temp.model.ByteArrayMultipartFile;
import org.igov.service.business.action.task.core.AbstractModelTask;
import org.igov.service.business.dfs.DfsService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component("SendDocument_SWinEd")
public class SendDocument_SWinEd extends AbstractModelTask implements TaskListener {

    private static final long serialVersionUID = 1L;

    private final static Logger LOG = LoggerFactory.getLogger(SendDocument_SWinEd.class);
    
    private Expression sEmail;
    
    private Expression sID_File_XML_SWinEd;
    
    @Autowired
    private DfsService dfsService;

    @Autowired
    private IBytesDataInmemoryStorage oBytesDataInmemoryStorage;

    @Override
    public void notify(DelegateTask delegateTask) {

        DelegateExecution execution = delegateTask.getExecution();
        String sID_File_XML_SWinEdValue = getStringFromFieldExpression(this.sID_File_XML_SWinEd, execution);
        String sEmailValue = getStringFromFieldExpression(this.sEmail, execution);
        String resp = "[none]";
        try {
            LOG.info("sID_File_XML_SWinEdValue: " + sID_File_XML_SWinEdValue);
            byte[] oFile_XML_SWinEd = oBytesDataInmemoryStorage.getBytes(sID_File_XML_SWinEdValue);
            ByteArrayMultipartFile oByteArrayMultipartFile = getByteArrayMultipartFileFromStorageInmemory(oFile_XML_SWinEd);
            LOG.info("sEmailValue : " + sEmailValue 
                    + " oByteArrayMultipartFile.getOriginalFilename(): " + oByteArrayMultipartFile.getOriginalFilename());
            if (oFile_XML_SWinEd != null) {
                String content = new String(oByteArrayMultipartFile.getBytes());
                resp += " content: " + content;
                LOG.info("content: " + content);
                resp = dfsService.send(content, oByteArrayMultipartFile.getOriginalFilename(), sEmailValue);
                LOG.info("!!!response:" + resp);
            } else {
                LOG.info("sID_File_XML_SWinEdValue: " + sID_File_XML_SWinEdValue + " oFile_XML_SWinEd is null!!!");
            }
            execution.setVariable("result", resp);
        } catch (Exception ex) {
            LOG.error("!!! Error in SendDocument_SWinEd sID_File_XML_SWinEdValue=" + sID_File_XML_SWinEdValue, ex);
            execution.setVariable("result", resp);
        }
    }
    
}
