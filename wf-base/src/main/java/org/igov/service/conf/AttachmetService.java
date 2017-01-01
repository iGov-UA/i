package org.igov.service.conf;

import java.util.List;
import java.util.Map;
import java.util.Date;
import java.text.SimpleDateFormat;
import java.util.logging.Level;
import org.activiti.engine.RuntimeService;

import org.json.simple.parser.JSONParser;
import org.activiti.engine.TaskService;
import org.activiti.engine.task.Attachment;
import org.igov.io.db.kv.statical.IBytesDataStorage;
import org.igov.io.db.kv.temp.IBytesDataInmemoryStorage;
import org.igov.io.db.kv.temp.exception.RecordInmemoryException;
import org.igov.model.action.vo.TaskAttachVO;
import static org.igov.util.Tool.sTextTranslit;
import org.json.simple.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AttachmetService {
	
        @Autowired
        protected TaskService taskService;
        
        @Autowired
        private RuntimeService oRuntimeService;
        
        @Autowired
        private IBytesDataInmemoryStorage oBytesDataInmemoryStorage;
        
        @Autowired
        private IBytesDataStorage oBytesDataStaticStorage;
        
	private final Logger LOG = LoggerFactory.getLogger(AttachmetService.class);
	
	
	public String createAttachment (String nID_Process, String sFileNameAndExt,
			boolean bSigned, String sID_StorageType, List<Map<String, Object>> saAttribute_JSON,
			byte[] aContent){
            
            LOG.info("createAttachment nID_Process: " + nID_Process);
            LOG.info("createAttachment sFileNameAndExt: " + sFileNameAndExt);
            LOG.info("createAttachment bSigned: " + bSigned);
            LOG.info("createAttachment sID_StorageType: " + sID_StorageType);
            LOG.info("createAttachment saAttribute_JSON size: " + saAttribute_JSON.size());
            LOG.info("createAttachment aContent: " + new String(aContent));
             
            
            TaskAttachVO oTaskAttachVO = new TaskAttachVO();
            
            String sKey = null;
            sFileNameAndExt = sTextTranslit(sFileNameAndExt);
            
            if (aContent != null) {
                if(sID_StorageType.equals("Mongo")){
                    sKey = oBytesDataStaticStorage.saveData(aContent);
                }
                if (sID_StorageType.equals("Redis")){
                    try {
                        sKey = oBytesDataInmemoryStorage.putBytes(aContent);
                    } catch (RecordInmemoryException ex) {
                        throw new RuntimeException(ex);
                    }
               }
            }else{
                throw new RuntimeException("Content is null");
            }
            
            LOG.info("database sKey: " + sKey);
            
            SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
            SimpleDateFormat dtf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
           
            oTaskAttachVO.setsID_StorageType(sID_StorageType);
            oTaskAttachVO.setsKey(sKey);
            oTaskAttachVO.setsVersion(df.format(new Date()));
            oTaskAttachVO.setsDateTime(dtf.format(new Date()));
            oTaskAttachVO.setsFileNameAndExt(sFileNameAndExt);
            oTaskAttachVO.setsContentType("html/text");
            oTaskAttachVO.setnBytes(Integer.toString(aContent.length));
            oTaskAttachVO.setbSigned(bSigned);
            oTaskAttachVO.setaAttribute(saAttribute_JSON);
            
            String sID_Field = ((JSONObject)((Object)oTaskAttachVO)).toJSONString();
            oRuntimeService.setVariable(nID_Process, sID_Field, sKey);
            
	    return sID_Field;
	  }
}
