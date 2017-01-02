package org.igov.service.conf;

import com.fasterxml.jackson.core.JsonProcessingException;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.util.List;
import java.util.Map;
import java.util.Date;
import java.text.SimpleDateFormat;
import org.activiti.engine.ActivitiObjectNotFoundException;
import org.activiti.engine.RuntimeService;


import org.activiti.engine.TaskService;
import org.activiti.engine.history.HistoricTaskInstance;
import org.activiti.engine.task.Attachment;
import org.activiti.engine.task.Task;
import org.igov.io.db.kv.statical.IBytesDataStorage;
import org.igov.io.db.kv.temp.IBytesDataInmemoryStorage;
import org.igov.io.db.kv.temp.exception.RecordInmemoryException;
import org.igov.io.db.kv.temp.model.ByteArrayMultipartFile;
import org.igov.model.action.vo.TaskAttachVO;
import static org.igov.util.Tool.sTextTranslit;
import org.json.simple.JSONObject;
import org.igov.util.JSON.JsonRestUtils;
import org.igov.util.VariableMultipartFile;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AttachmetService {
	
    @Autowired
    protected TaskService oTaskService;
        
    @Autowired
    private RuntimeService oRuntimeService;
        
    @Autowired
    private IBytesDataInmemoryStorage oBytesDataInmemoryStorage;
        
    @Autowired
    private IBytesDataStorage oBytesDataStaticStorage;
        
    private final Logger LOG = LoggerFactory.getLogger(AttachmetService.class);
	
	
    public String createAttachment (String nID_Process, String sFileNameAndExt,
        	boolean bSigned, String sID_StorageType, String sContentType, List<Map<String, Object>> saAttribute_JSON,
		byte[] aContent) throws JsonProcessingException{
            
        LOG.info("createAttachment nID_Process: " + nID_Process);
        LOG.info("createAttachment sFileNameAndExt: " + sFileNameAndExt);
        LOG.info("createAttachment bSigned: " + bSigned);
        LOG.info("createAttachment sID_StorageType: " + sID_StorageType);
        LOG.info("createAttachment sContentType: " + sContentType);
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
        
        
        String sID_Field = JsonRestUtils.toJson((Object)oTaskAttachVO);
        oRuntimeService.setVariable(nID_Process, sKey, sID_Field);
            
	return sID_Field;
    }
    
    public VariableMultipartFile getAttachment(String nID_Process, String sID_Field) throws ParseException, RecordInmemoryException, IOException, ClassNotFoundException {
       
        Map<String, Object> variables = oRuntimeService.getVariables(nID_Process);
        byte [] aResultArray = null;
        String sFileName = null;
        String sVersion = null;
        String sContentType = null;
        LOG.info("VariableMap: " + variables);
                   
        if (variables != null) {
            
            if (variables.containsKey(sID_Field)){
                LOG.info("VariableMap contains found key");
                
                JSONParser parser = new JSONParser();
                JSONObject result = (JSONObject) parser.parse(String.valueOf(variables.get(sID_Field)));
                    
                String sID_StorageType = (String)result.get("sID_StorageType");
                String sKey = (String)result.get("sKey");
                sFileName = (String)result.get("sFileNameExt");
                sVersion = (String)result.get("sVersion");
                sContentType = (String)result.get("sContentType");      
                    
                if(sID_StorageType.equals("Mongo")){
                    aResultArray = oBytesDataStaticStorage.getData(sKey);
                    if(aResultArray != null){
                        LOG.info("Mongo byte array isn't null");
                    }
                }
                if (sID_StorageType.equals("Redis")){
                    aResultArray = oBytesDataInmemoryStorage.getBytes(sKey);
                     if(aResultArray != null){
                        LOG.info("Redis byte array isn't null");
                    }
                }
            }
        }
        
        VariableMultipartFile resultFile = null;
        
        if(aResultArray != null){
            ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(aResultArray);
            //ObjectInputStream ois = new ObjectInputStream(byteArrayInputStream);
            resultFile = new VariableMultipartFile(byteArrayInputStream, sContentType, sFileName, sVersion);
            //ois.close();
        }
        
        if(resultFile != null){
            LOG.info("result file isn't null");
        }
        
        return resultFile;
    }    
}
