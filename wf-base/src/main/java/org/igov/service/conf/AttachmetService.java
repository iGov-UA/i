package org.igov.service.conf;

import static org.igov.service.business.action.task.core.AbstractModelTask.getByteArrayMultipartFileFromStorageInmemory;
import static org.igov.util.Tool.sTextTranslit;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.activiti.engine.IdentityService;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.TaskService;
import org.activiti.engine.identity.Group;
import org.igov.io.db.kv.statical.IBytesDataStorage;
import org.igov.io.db.kv.temp.IBytesDataInmemoryStorage;
import org.igov.io.db.kv.temp.exception.RecordInmemoryException;
import org.igov.model.action.vo.TaskAttachVO;
import org.igov.model.document.DocumentStep;
import org.igov.model.document.DocumentStepDao;
import org.igov.model.document.DocumentStepSubjectRight;
import org.igov.model.document.DocumentStepSubjectRightDao;
import org.igov.service.business.action.task.core.AbstractModelTask;
import static org.igov.service.business.action.task.core.AbstractModelTask.getByteArrayMultipartFileFromStorageInmemory;
import org.igov.service.business.action.task.core.ActionTaskService;
import org.igov.service.business.util.CustomRegexPattern;
import org.igov.service.exception.CRCInvalidException;
import org.igov.service.exception.RecordNotFoundException;
import org.igov.util.VariableMultipartFile;
import org.igov.util.JSON.JsonRestUtils;
import org.joda.time.DateTime;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.core.JsonProcessingException;

@Service
public class AttachmetService implements CustomRegexPattern{

    @Autowired
    protected TaskService oTaskService;

    @Autowired
    private RuntimeService oRuntimeService;

    @Autowired
    private IBytesDataInmemoryStorage oBytesDataInmemoryStorage;

    @Autowired
    @Qualifier("durableBytesDataStorage")
    private IBytesDataStorage oBytesDataStaticStorage;

    @Autowired
    private DocumentStepDao documentStepDao;

    @Autowired
    private DocumentStepSubjectRightDao documentStepSubjectRightDao;

    @Autowired
    private IdentityService identityService;

    private final Logger LOG = LoggerFactory.getLogger(AttachmetService.class);

    public String createAttachment(String nID_Process, String sID_Field, String sFileNameAndExt,
            boolean bSigned, String sID_StorageType, String sContentType, List<Map<String, Object>> saAttribute_JSON,
            byte[] aContent, boolean bSetVariable) throws JsonProcessingException, CRCInvalidException, RecordNotFoundException {

        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        SimpleDateFormat dtf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");

        sFileNameAndExt = (sFileNameAndExt != null) ? transliterateAll(getFileName(sFileNameAndExt.replace(" ", "_")))
                + "." + getFileExtention(sTextTranslit(sFileNameAndExt)) : sFileNameAndExt;

        TaskAttachVO oTaskAttachVO = new TaskAttachVO();

        String sKey = null;

        if (aContent != null) {
            if (sID_StorageType.equals("Mongo")) {
                sKey = oBytesDataStaticStorage.saveData(aContent);
            }
            if (sID_StorageType.equals("Redis")) {
                try {
                    sKey = oBytesDataInmemoryStorage.putBytes(aContent);
                } catch (RecordInmemoryException ex) {
                    throw new RuntimeException(ex);
                }
            }
        } else {
            throw new RuntimeException("Content is null");
        }

        LOG.info("database sKey: " + sKey);

        oTaskAttachVO.setsID_StorageType(sID_StorageType);
        oTaskAttachVO.setsKey(sKey);
        oTaskAttachVO.setsVersion(df.format(new Date()));
        oTaskAttachVO.setsDateTime(dtf.format(new Date()));
        oTaskAttachVO.setsFileNameAndExt(sFileNameAndExt);
        oTaskAttachVO.setsContentType(sContentType);
        oTaskAttachVO.setnBytes(Integer.toString(aContent.length));
        oTaskAttachVO.setbSigned(bSigned);
        oTaskAttachVO.setaAttribute(saAttribute_JSON);

        String sID_Field_Value = JsonRestUtils.toJson((Object) oTaskAttachVO);

        if (nID_Process != null && sID_Field != null && bSetVariable == true) {

            oRuntimeService.setVariable(nID_Process, sID_Field, sID_Field_Value);
        }

        return sID_Field_Value;
    }

    public String setDocumentImage(String nID_Process, String sID_Field, String sFileNameAndExt,
            boolean bSigned, String sID_StorageType, String sContentType, List<Map<String, Object>> saAttribute_JSON,
            byte[] aContent, boolean bSetVariable, String sKey_Step, String sLogin) throws JsonProcessingException, CRCInvalidException, RecordNotFoundException, ParseException {

        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        //получаю по апи активити список груп	
        List<Group> aGroup = identityService.createGroupQuery().groupMember(sLogin).list();
        // получаю все группы
        Set<String> asID_Group = new HashSet<>();
        
        if (aGroup != null) {
            aGroup.stream().forEach(group -> asID_Group.add(group.getId()));
        }

        LOG.info("sLogin={}, asID_Group={}", sLogin, asID_Group);
        LOG.info("aGroup={}", aGroup);

        List<DocumentStep> aDocumentStep = documentStepDao.getStepForProcess(nID_Process);
        LOG.info("The size of list" + aDocumentStep.size());
        LOG.info("Result list of steps: {}", aDocumentStep);

        DocumentStep oFindedDocumentStep = null;

        for (DocumentStep oDocumentStep : aDocumentStep) {
            if (oDocumentStep.getsKey_Step().equals(sKey_Step)) {
                oFindedDocumentStep = oDocumentStep;
                break;
            }
        }
        //получаю список логинов        
        
        List<DocumentStepSubjectRight> aDocumentStepSubjectRight = new ArrayList<>();
        
        if(oFindedDocumentStep != null){
            aDocumentStepSubjectRight.addAll(oFindedDocumentStep.aDocumentStepSubjectRight());
            LOG.info("oFindedDocumentStep ={}", oFindedDocumentStep.aDocumentStepSubjectRight());
        }
        
        // пробегаюсь по листу логинов, ищу нужный
        
        DocumentStepSubjectRight oTargetDocumentStepSubjectRight = null;
        
        for (DocumentStepSubjectRight oDocumentStepSubjectRight : aDocumentStepSubjectRight) {
            
            for(String sID_Group : asID_Group)
            {
                if (oDocumentStepSubjectRight.getsKey_GroupPostfix().equals(sID_Group)) {
                    if(oTargetDocumentStepSubjectRight == null){
                        oTargetDocumentStepSubjectRight = oDocumentStepSubjectRight;
                    }
                    else{
                        break;
                    }
                }
                
            }
        }
        
        if (oTargetDocumentStepSubjectRight != null) {

            LOG.info("oDocumentStepSubjectRight's Key_GroupPostfix() is " + oTargetDocumentStepSubjectRight.getsKey_GroupPostfix());

            String sJsonValue = createAttachment(nID_Process, sID_Field, sFileNameAndExt,
                    bSigned, sID_StorageType, sContentType, saAttribute_JSON,
                    aContent, bSetVariable);
            JSONParser parser = new JSONParser();
            JSONObject result = (JSONObject) parser.parse(sJsonValue);
            LOG.info("Result parse json={}", result);

            if (bSigned == true) {
                oTargetDocumentStepSubjectRight.setsDateECP(new DateTime(df.format(new Date())));
            }
            
            String sKey = (String) result.get("sKey");
            LOG.info("SKEY is =", sKey);

            oTargetDocumentStepSubjectRight.setsID_File_ForSign(sKey);
            documentStepSubjectRightDao.saveOrUpdate(oTargetDocumentStepSubjectRight);
            return sJsonValue;

        }
    
        return "There is no document step right for current subject";
    }

    public MultipartFile getAttachment(String nID_Process, String sID_Field, String sKey, String sID_StorageType)
            throws ParseException, RecordInmemoryException, IOException, ClassNotFoundException, CRCInvalidException, RecordNotFoundException {
        MultipartFile oMultipartFile = null;

        byte[] aResultArray = null;
        String sFileName = null;
        String sVersion = "";
        String sContentType = "";
        ByteArrayInputStream byteArrayInputStream = null;

        if (nID_Process != null && sID_Field != null) {

            Map<String, Object> variables = oRuntimeService.getVariables(nID_Process);
            SimpleDateFormat dtf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
            LOG.info("getAttachment started in " + dtf.format(new Date()));
            LOG.info("VariableMap: " + variables);

            if (variables != null) {

                if (variables.containsKey(sID_Field)) {
                    LOG.info("VariableMap contains found key");

                    JSONParser parser = new JSONParser();
                    JSONObject result = (JSONObject) parser.parse(String.valueOf(variables.get(sID_Field)));

                    sID_StorageType = (String) result.get("sID_StorageType");
                    sKey = (String) result.get("sKey");
                    sFileName = (String) result.get("sFileNameAndExt");
                    sVersion = (String) result.get("sVersion");
                    sContentType = (String) result.get("sContentType");

                    LOG.info("getProcessAttach sID_StorageType: " + sID_StorageType);
                    LOG.info("getProcessAttach sKey: " + sKey);
                    LOG.info("getProcessAttach sFileName: " + sFileName);
                    LOG.info("getProcessAttach sVersion: " + sVersion);
                    LOG.info("getProcessAttach sContentType: " + sContentType);
                }
            }

            if (oMultipartFile == null) {
                LOG.info("result file is null");
            }
        }
        if (sID_StorageType.equals("Mongo")) {
            aResultArray = oBytesDataStaticStorage.getData(sKey);
            byteArrayInputStream = new ByteArrayInputStream(aResultArray);
            oMultipartFile = new VariableMultipartFile(byteArrayInputStream, sVersion, sFileName, sContentType);
            if (aResultArray != null) {
                LOG.info("Mongo byte array isn't null");
            }
        }
        if (sID_StorageType.equals("Redis")) {
            aResultArray = oBytesDataInmemoryStorage.getBytes(sKey);
            oMultipartFile = getByteArrayMultipartFileFromStorageInmemory(aResultArray); //приводим

            if (aResultArray != null) {
                LOG.info("Redis byte array isn't null");
            }
        }

        return oMultipartFile;
        //return aResultArray;
    }

    public MultipartFile getDocumentImage(String nID_Process, String sLogin, String sKey_Step)
            throws ParseException, RecordInmemoryException, IOException, ClassNotFoundException, CRCInvalidException, RecordNotFoundException {

        List<Group> aGroup = identityService.createGroupQuery().groupMember(sLogin).list();
        // получаю все группы
        Set<String> asID_Group = new HashSet<>();
        
        if (aGroup != null) {
            aGroup.stream().forEach(group -> asID_Group.add(group.getId()));
        }

        LOG.info("sLogin={}, asID_Group={}", sLogin, asID_Group);
        LOG.info("aGroup={}", aGroup);
        
        List<DocumentStep> aDocumentStep = documentStepDao.getStepForProcess(nID_Process);
        LOG.info("The size of list" + aDocumentStep.size());
        LOG.info("Result list of steps: {}", aDocumentStep);

        DocumentStep oFindedDocumentStep = null;

        for (DocumentStep oDocumentStep : aDocumentStep) {
            if (oDocumentStep.getsKey_Step().equals(sKey_Step)) {
                oFindedDocumentStep = oDocumentStep;
                break;
            }
        }
        
        List<DocumentStepSubjectRight> aDocumentStepSubjectRight = new ArrayList<>();
        
        if(oFindedDocumentStep != null){
            aDocumentStepSubjectRight.addAll(oFindedDocumentStep.aDocumentStepSubjectRight());
            LOG.info("oFindedDocumentStep ={}", oFindedDocumentStep.aDocumentStepSubjectRight());
        }
        
        DocumentStepSubjectRight oTargetDocumentStepSubjectRight = null;
        
        for (DocumentStepSubjectRight oDocumentStepSubjectRight : aDocumentStepSubjectRight) {
            
            for(String sID_Group : asID_Group)
            {
                if (oDocumentStepSubjectRight.getsKey_GroupPostfix().equals(sID_Group)) {
                    if(oTargetDocumentStepSubjectRight == null){
                        oTargetDocumentStepSubjectRight = oDocumentStepSubjectRight;
                    }
                    else{
                        break;
                        //throw new RuntimeException("There are few target groups in the DocumentStep set");
                    }
                }
            }
        }
        
        if(oTargetDocumentStepSubjectRight != null)
        {
            String sKey = oTargetDocumentStepSubjectRight.getsID_File_ForSign();
            return getAttachment(null, null, sKey, "Mongo");
        }
        
        return null;
    }

    public String getFileExtention(String fileName) {
        String[] parts = fileName.split("\\.");
        if (parts.length != 0) {
            return parts[parts.length - 1];
        }
        return "";
    }

    public String getFileName(String fileName) {
        String[] parts = fileName.split("\\.");

        if (parts.length != 0) {
            return parts[0];
        }
        return "";
    }

    public static String transliterateAll(String message) {
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < message.length(); i++) {
            boolean isAppended = false;
            for (int x = 0; x < CYR_ABC.length; x++) {
                if (message.charAt(i) == CYR_ABC[x]) {
                    builder.append(LAT_ABC[x]);
                    isAppended = true;
                    break;
                }
            }
            if (!isAppended) {
                builder.append(message.charAt(i));
            }
        }
        return builder.toString();
    }

	
}
