package org.igov.service.business.action.task.core;

import org.igov.model.action.task.core.entity.ListKeyable;
import org.igov.model.flow.FlowSlotTicket;
import org.activiti.engine.ActivitiException;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.delegate.DelegateTask;
import org.activiti.engine.delegate.Expression;
import org.activiti.engine.form.FormData;
import org.activiti.engine.form.FormProperty;
import org.activiti.engine.task.Attachment;
import org.igov.io.db.kv.temp.exception.RecordInmemoryException;
import org.igov.io.db.kv.temp.model.ByteArrayMultipartFile;
import org.apache.commons.codec.binary.Base64;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.multipart.MultipartFile;
import org.igov.model.flow.FlowSlotDao;
import org.igov.model.flow.FlowSlotTicketDao;
import org.igov.util.JSON.JsonRestUtils;
import org.igov.service.business.flow.slot.SaveFlowSlotTicketResponse;
import org.igov.service.business.action.task.form.FormFileType;
import org.igov.service.business.action.task.form.QueueDataFormType;

import java.io.*;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import org.igov.io.db.kv.temp.IBytesDataInmemoryStorage;

public abstract class AbstractModelTask {

    public static final String LIST_KEY_PREFIX = "lst";
    public static final String LIST_KEY_DELIM = ":";
    private static Logger LOG = LoggerFactory
            .getLogger(AbstractModelTask.class);
    
    @Autowired
    protected FlowSlotDao flowSlotDao;
    @Autowired
    protected FlowSlotTicketDao oFlowSlotTicketDao;
    @Autowired
    private IBytesDataInmemoryStorage oBytesDataInmemoryStorage;

    /**
     * Возвращает сложгый ключ переменной
     * бизнес-процесса
     *
     * @param listKey
     * @param elementKey
     * @return
     */
    private static String getExecutionVarKey(String listKey, String elementKey) {
        return new StringBuilder().append(LIST_KEY_PREFIX)
                .append(LIST_KEY_DELIM)
                .append(listKey)
                .append(LIST_KEY_DELIM)
                .append(elementKey).toString();
    }

    /**
     * Конверт Byte To String
     *
     * @param contentBytes
     * @return
     */
    public static String contentByteToString(byte[] contentBytes) {
        return Base64.encodeBase64String(contentBytes);
    }

    /**
     * Конверт String to Byte
     *
     * @param contentString
     * @return
     * @throws java.io.IOException
     */
    public static byte[] contentStringToByte(String contentString) throws IOException {
        return Base64.decodeBase64(contentString);
    }

    public static String getStringFromFieldExpression(Expression expression,
            DelegateExecution execution) {
        if (expression != null) {
            Object value = expression.getValue(execution);
            if (value != null) {
                return value.toString();
            }
        }
        return null;
    }

    /**
     * Получить
     *
     * @param keyRedis
     * @return
     */
    public static List<String> getListKeysRedis(String keyRedis) {
        List<String> listKeys = new ArrayList<String>();
        if (keyRedis != null && !keyRedis.isEmpty()) {
            String[] keys = keyRedis.split(";");
            listKeys = Arrays.asList(keys);
            return listKeys;
        }
        return listKeys;
    }

    public static List<String> getVariableValues(DelegateExecution execution, List<String> formFieldIds) {
        return getVariableValues(execution.getEngineServices().getRuntimeService(), execution.getProcessInstanceId(),
                formFieldIds);
    }

    public static List<String> getVariableValues(RuntimeService runtimeService, String processInstanceId,
            List<String> formFieldIds) {
        List<String> listValueKeys = new ArrayList<String>();
        if (!formFieldIds.isEmpty()) {
            Map<String, Object> variables = runtimeService.getVariables(
                    processInstanceId);
            for (String fieldId : formFieldIds) {
                if (variables.containsKey(fieldId)) {
                    listValueKeys.add(String.valueOf(variables.get(fieldId)));
                }
            }
        }
        return listValueKeys;
    }

    /**
     * Получить ид поля с кастомным типом file
     *
     * @param oFormData
     * @return
     */
    public static List<String> getListFieldCastomTypeFile(FormData oFormData) {
        List<String> asFieldID = new ArrayList<String>();
        List<FormProperty> aFormProperty = oFormData.getFormProperties();
        if (!aFormProperty.isEmpty()) {
            for (FormProperty oFormProperty : aFormProperty) {
                if (oFormProperty.getType() instanceof FormFileType) {
                    asFieldID.add(oFormProperty.getId());
                }
            }
        }
        return asFieldID;
    }

    /**
     * @param oFormData form data of process
     * @return variable ids with custom property type QueueDataFormType
     */
    public static List<String> getListField_QueueDataFormType(FormData oFormData) {
        List<String> asFieldID = new ArrayList<String>();
        List<FormProperty> aFormProperty = oFormData.getFormProperties();
        if (!aFormProperty.isEmpty()) {
            for (FormProperty oFormProperty : aFormProperty) {
                if (oFormProperty.getType() instanceof QueueDataFormType) {
                    asFieldID.add(oFormProperty.getId());
                }
            }
        }
        return asFieldID;
    }

    /**
     * Получить имя поля
     *
     * @param formData
     * @return
     */
    public static List<String> getListCastomFieldName(FormData formData) {
        List<String> filedName = new ArrayList<String>();
        List<FormProperty> formDataList = formData.getFormProperties();
        if (!formDataList.isEmpty()) {
            for (FormProperty prop : formDataList) {
                if (prop.getType() instanceof FormFileType) {
                    filedName.add(prop.getName());
                }
            }
        }
        return filedName;
    }

    public static String getCastomFieldValue(FormData formData, String fieldName) {
        List<FormProperty> formDataList = formData.getFormProperties();
        if (!formDataList.isEmpty()) {
            for (FormProperty prop : formDataList) {
                if (prop.getType() instanceof FormFileType && prop.getName().equalsIgnoreCase(fieldName)) {
                    return prop.getValue() != null ? prop.getValue() : "";
                }
            }
        }
        return "";
    }

    public static ByteArrayOutputStream multipartFileToByteArray(MultipartFile file) throws IOException {
        return multipartFileToByteArray(file, null);
    }

    /**
     * multipartFile To ByteArray
     *
     * @param oMultipartFile
     * @return
     * @throws java.io.IOException
     */
    public static ByteArrayOutputStream multipartFileToByteArray(MultipartFile oMultipartFile, String sFileNameReal)
            throws IOException {

        LOG.debug("(sFileNameReal={})", sFileNameReal);

        //String sFilename = new String(file.getOriginalFilename().getBytes(), "Cp1251");//UTF-8
        //LOG.debug("(sFilename={})", sFilename);

        String sFilenameEncoded = new String(oMultipartFile.getOriginalFilename().getBytes(Charset.forName("UTF-8")));//UTF-8
        LOG.debug("(sFilenameEncoded={})", sFilenameEncoded);
        
        /*
        String sFilename2 = new String(file.getOriginalFilename().getBytes(), "UTF-8");//UTF-8
        LOG.debug("(sFilename2={})", sFilename2);
        String sFilename3 = new String(file.getOriginalFilename().getBytes(Charset.forName("Cp1251")));//UTF-8
        LOG.debug("(sFilename3={})", sFilename3);
        String sFilename4 = new String(file.getOriginalFilename().getBytes());//UTF-8
        LOG.debug("(sFilename4={})", sFilename4);

        String sFilename0 = file.getOriginalFilename();//UTF-8
        LOG.debug("(sFilename0={})", sFilename0);

        //sFilename = Renamer.sRenamed(sFilename0);
        String sFilenameNew0 = Renamer.sRenamed(sFilename0);
        LOG.debug("(sFilenameNew0={})", sFilenameNew0);
        String sFilenameNew1 = Renamer.sRenamed(sFilename1);
        LOG.debug("(sFilenameNew1={})", sFilenameNew1);
        String sFilenameNew2 = Renamer.sRenamed(sFilename2);
        LOG.debug("(sFilenameNew2={})", sFilenameNew2);
        String sFilenameNew3 = Renamer.sRenamed(sFilename3);
        LOG.debug("(sFilenameNew3={})", sFilenameNew3);
        String sFilenameNew4 = Renamer.sRenamed(sFilename4);
        LOG.debug("(sFilenameNew4={})", sFilenameNew4);

        //sFilename=sFilenameNew;
        LOG.debug("(sFilename(new)={})", sFilename);*/

        ByteArrayMultipartFile oByteArrayMultipartFile = new ByteArrayMultipartFile(
                oMultipartFile.getBytes(), oMultipartFile.getName(), sFileNameReal == null ? sFilenameEncoded : sFileNameReal,
                oMultipartFile.getContentType());
        ByteArrayOutputStream oByteArrayOutputStream = new ByteArrayOutputStream();
        ObjectOutputStream oObjectOutputStream = new ObjectOutputStream(oByteArrayOutputStream);
        oObjectOutputStream.writeObject(oByteArrayMultipartFile);
        oObjectOutputStream.flush();
        oObjectOutputStream.close();
        return oByteArrayOutputStream;
    }

    /**
     * ByteArray To multipartFile
     *
     * @param byteFile
     * @return
     * @throws java.io.IOException
     * @throws ClassNotFoundException
     */
    public static ByteArrayMultipartFile getByteArrayMultipartFileFromStorageInmemory(
            byte[] byteFile) throws IOException, ClassNotFoundException {
        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(byteFile);
        ObjectInputStream ois = new ObjectInputStream(byteArrayInputStream);
        ByteArrayMultipartFile contentMultipartFile = (ByteArrayMultipartFile) ois.readObject();
        ois.close();
        return contentMultipartFile;
    }

    /**
     * Получить список по ключу списка из execution
     *
     * @param listKey
     * @param execution
     * @return
     */
    @SuppressWarnings("unchecked")
    public <T> List<T> getListVariable(String listKey, DelegateExecution execution) {
        List<T> result = new ArrayList<T>();

        String keyPrefix = LIST_KEY_PREFIX + LIST_KEY_DELIM + listKey;

        for (String execVarKey : execution.getVariableNames()) {
            if (execVarKey.startsWith(keyPrefix)) {
                result.add((T) execution.getVariable(execVarKey));
            }
        }
        return result;
    }

    /**
     * Сохранить список обьектов в execution
     *
     * @param listKey
     * @param list
     * @param execution
     */
    public <T extends ListKeyable> void setListVariable(String listKey, List<T> list, DelegateExecution execution) {
        for (ListKeyable listKeyable : list) {
            execution.setVariable(
                    getExecutionVarKey(listKey, listKeyable.getKey()),
                    listKeyable);
        }
    }

    /**
     * Adds Attachemnts based on formData to task.
     *
     * @param oFormData FormData from task where we search file fields.
     * @param oTask     where we add Attachments.
     */
    public void addAttachmentsToTask(FormData oFormData, DelegateTask oTask) {
        DelegateExecution oExecution = oTask.getExecution();

        LOG.info("SCAN:file");
        List<String> asFieldID = getListFieldCastomTypeFile(oFormData);
        LOG.info("[addAttachmentsToTask]");
        LOG.info("(asFieldID={})", asFieldID.toString());
        List<String> asFieldValue = getVariableValues(oExecution, asFieldID);
        LOG.info("(asFieldValue={})", asFieldValue.toString());
        List<String> asFieldName = getListCastomFieldName(oFormData);
        LOG.info("(asFieldName={})", asFieldName.toString());
        if (!asFieldValue.isEmpty()) {
            int n = 0;
            for (String sKeyRedis : asFieldValue) {
                LOG.info("(sKeyRedis={})", sKeyRedis);
                if (sKeyRedis != null && !sKeyRedis.isEmpty() && !"".equals(sKeyRedis.trim()) && !"null"
                        .equals(sKeyRedis.trim()) && sKeyRedis.length() > 15) {
                    if (!asFieldName.isEmpty() && n < asFieldName.size()) {
                        //String sDescription = asFieldName.get((asFieldName.size() - 1) - n);
                        String sDescription = asFieldName.get(n);
                        LOG.info("(sDescription={})", sDescription);
                        String sID_Field = asFieldID.get(n);
                        LOG.info("(sID_Field={})", sID_Field);

                        byte[] aByteFile;
                        ByteArrayMultipartFile oByteArrayMultipartFile = null;
                        try {
                            aByteFile = oBytesDataInmemoryStorage.getBytes(sKeyRedis);
                            oByteArrayMultipartFile = getByteArrayMultipartFileFromStorageInmemory(aByteFile);
                        } catch (ClassNotFoundException | IOException | RecordInmemoryException e1) {
                            throw new ActivitiException(e1.getMessage(), e1);
                        }
                        if (oByteArrayMultipartFile != null) {
                            String sFileName = null;
                            try {
                                sFileName = new String(oByteArrayMultipartFile.getOriginalFilename().getBytes(),
                                        "UTF-8");
                            } catch (java.io.UnsupportedEncodingException oException) {
                                LOG.error("error on getting sFileName: {}", oException.getMessage());
                                LOG.debug("FAIL:", oException);
                                throw new ActivitiException(oException.getMessage(), oException);
                            }
                            LOG.info("(sFileName={})", sFileName);

                            //===
                            InputStream oInputStream = null;
                            try {
                                oInputStream = oByteArrayMultipartFile.getInputStream();
                            } catch (Exception e) {
                                throw new ActivitiException(e.getMessage(), e);
                            }
                            Attachment oAttachment = oExecution.getEngineServices().getTaskService().createAttachment(
                                    oByteArrayMultipartFile.getContentType() + ";" + oByteArrayMultipartFile.getExp(),
                                    oTask.getId(), oExecution.getProcessInstanceId(), sFileName, sDescription,
                                    oInputStream);

                            if (oAttachment != null) {
                                String nID_Attachment = oAttachment.getId();
                                //LOG.info("(nID_Attachment={})", nID_Attachment);
                                LOG.info("Try set variable(sID_Field={}) with the value(nID_Attachment={}), for new attachment...",
                                        sID_Field, nID_Attachment);
                                oExecution.getEngineServices().getRuntimeService()
                                        .setVariable(oExecution.getProcessInstanceId(), sID_Field, nID_Attachment);
                                LOG.info("Finished setting new value for variable with attachment (sID_Field={})",
                                        sID_Field);
                            } else {
                                LOG.error("Can't add attachment to (oTask.getId()={})", oTask.getId());
                            }
                            //===

                        } else {
                            LOG.error("oByteArrayMultipartFile==null!  (sKeyRedis={})", sKeyRedis);
                        }
                    } else {
                        LOG.error("asFieldName has nothing! (asFieldName={})", asFieldName);
                    }
                } else {
                    LOG.error("Invalid Redis Key!!! (sKeyRedis={})", sKeyRedis);
                }
                n++;
            }
        }

        scanExecutionOnQueueTickets(oExecution, oFormData); //, oTask);//startformData

        /*LOG.info("SCAN:queueData");
         asFieldID = getListField_QueueDataFormType(formData);
         LOG.info("asFieldID="+asFieldID.toString());
         asFieldValue = getVariableValues(execution, asFieldID);
         LOG.info("asFieldValue="+asFieldValue.toString());
         //asFieldName = getListCastomFieldName(formData);
         //LOG.info("asFieldName="+asFieldName.toString());
         if (!asFieldValue.isEmpty()) {
         String sValue = asFieldValue.get(0);
         LOG.info("sValue=" + sValue);
         long nID_FlowSlotTicket=0;
         int nAt=sValue.indexOf(":");
         int nTo=sValue.indexOf(",");
         String s=sValue.substring(nAt+1,nTo);
         LOG.info("s=" + s);
         try{
         nID_FlowSlotTicket = Long.valueOf(s);
         LOG.info("Ok!");
         }catch(Exception oException){
         LOG.error(oException.getMessage());
         nID_FlowSlotTicket=1;
         }
         LOG.info("nID_FlowSlotTicket=" + nID_FlowSlotTicket);
            
         try{
                

         //                FlowSlotTicket oFlowSlotTicket = baseEntityDao.getById(FlowSlotTicket.class, nID_FlowSlotTicket);
         //                if (oFlowSlotTicket == null) {
         //                    LOG.error("FlowSlotTicket with id=" + nID_FlowSlotTicket + " is not found!");
         //                }else{
         //                    long nID_FlowSlot=oFlowSlotTicket.getoFlowSlot().getId();
         //                    LOG.error("nID_FlowSlot="+nID_FlowSlot);
         //                    long nID_Subject = oFlowSlotTicket.getnID_Subject();
         //                    LOG.error("nID_Subject="+nID_Subject);
         //                    long nID_Task_Activiti = 1; //TODO set real ID!!!
         //                    oFlowSlotTicket.setnID_Task_Activiti(nID_Task_Activiti);
         //                    baseEntityDao.saveOrUpdate(oFlowSlotTicket);
         //                    LOG.info("JSON:" + JsonRestUtils.toJsonResponse(new SaveFlowSlotTicketResponse(oFlowSlotTicket.getId())));
         //                }
                
         }catch(Exception oException){
         LOG.error(oException.getMessage());
         }
            
         }*/

    }

    public void scanExecutionOnQueueTickets(DelegateExecution oExecution,
            FormData oFormData) { //DelegateTask oTask) {//StartFormData startformData
        LOG.info("SCAN:queueData");
        List<String> asFieldID = getListField_QueueDataFormType(oFormData);//startformData
        LOG.info("(asFieldID={})", asFieldID.toString());
        List<String> asFieldValue = getVariableValues(oExecution, asFieldID);
        LOG.info("(asFieldValue={})", asFieldValue.toString());
        //asFieldName = getListCastomFieldName(startformData);
        //LOG.info("(asFieldName={})", asFieldName.toString());
        if (!asFieldValue.isEmpty()) {
            String sValue = asFieldValue.get(0);
            LOG.info("(sValue={})", sValue);
            long nID_FlowSlotTicket = 0;

            //sValue={"nID_FlowSlotTicket":20756,"sDate":"2015-08-22 12:00:00.00"}
            Map<String, Object> m = QueueDataFormType.parseQueueData(sValue);
            //String snID_FlowSlotTicket = (String) m.get("nID_FlowSlotTicket");
            //String snID_FlowSlotTicket = m.get("nID_FlowSlotTicket")+"";
            //Double
            nID_FlowSlotTicket = QueueDataFormType.get_nID_FlowSlotTicket(m);
            //LOG.info("(snID_FlowSlotTicket={})", snID_FlowSlotTicket);
            //nID_FlowSlotTicket = Long.valueOf(snID_FlowSlotTicket);
            LOG.info("(nID_FlowSlotTicket={})", nID_FlowSlotTicket);
            String sDate = (String) m.get(QueueDataFormType.sDate);
            LOG.info("(sDate={})", sDate);

            /*int nAt=sValue.indexOf(":");
             int nTo=sValue.indexOf(",");
             String s=sValue.substring(nAt+1,nTo);
             LOG.info("(s={})", s);
             try{
             nID_FlowSlotTicket = Long.valueOf(s);
             Long.valueOf(s)
             LOG.info("nID_FlowSlotTicket:Ok!");
             }catch(Exception oException){
             LOG.error(oException.getMessage());
             nID_FlowSlotTicket=1;
             }*/

            try {

                long nID_Task_Activiti = 1; //TODO set real ID!!!
                /*
                try {
                
                     LOG.info("(oExecution.getBusinessKey()={})", oExecution.getBusinessKey());
                     LOG.info("(oExecution.getCurrentActivityId()={})", oExecution.getCurrentActivityId());
                     LOG.info("(oExecution.getCurrentActivityName()={})", oExecution.getCurrentActivityName());
                     LOG.info("(oExecution.getEventName()={})", oExecution.getEventName());
                     LOG.info("(oExecution.getId()={})", oExecution.getId());
                     LOG.info("(oExecution.getParentId()={})", oExecution.getParentId());
                     LOG.info("(oExecution.getProcessBusinessKey()={})", oExecution.getProcessBusinessKey());
                     LOG.info("(oExecution.getProcessDefinitionId()={})", oExecution.getProcessDefinitionId());
                     LOG.info("(oExecution.getProcessInstanceId()={})", oExecution.getProcessInstanceId());//THIS!!!
                     LOG.info("(oExecution.getTenantId()={})", oExecution.getTenantId());
                     */
                    /*                                
                     2015-07-05_15:33:11.144 | INFO | org.igov.activiti.systemtask.FileTaskUpload- oExecution.getBusinessKey()=null
                     2015-07-05_15:33:11.144 | INFO | org.igov.activiti.systemtask.FileTaskUpload- oExecution.getCurrentActivityId()=servicetask1
                     2015-07-05_15:33:11.144 | INFO | org.igov.activiti.systemtask.FileTaskUpload- oExecution.getCurrentActivityName()=ϳ���������� ���������
                     2015-07-05_15:33:11.144 | INFO | org.igov.activiti.systemtask.FileTaskUpload- oExecution.getEventName()=null
                     2015-07-05_15:33:11.144 | INFO | org.igov.activiti.systemtask.FileTaskUpload- oExecution.getId()=955057
                     2015-07-05_15:33:11.144 | INFO | org.igov.activiti.systemtask.FileTaskUpload- oExecution.getParentId()=955001
                     2015-07-05_15:33:11.144 | INFO | org.igov.activiti.systemtask.FileTaskUpload- oExecution.getProcessBusinessKey()=key
                     2015-07-05_15:33:11.144 | INFO | org.igov.activiti.systemtask.FileTaskUpload- oExecution.getProcessDefinitionId()=kiev_mreo_1:126:942617
                     2015-07-05_15:33:11.144 | INFO | org.igov.activiti.systemtask.FileTaskUpload- oExecution.getProcessInstanceId()=955001
                     2015-07-05_15:33:11.144 | INFO | org.igov.activiti.systemtask.FileTaskUpload- oExecution.getTenantId()=
                     */
                    try {
                        nID_Task_Activiti = Long.valueOf(oExecution.getProcessInstanceId());
                        LOG.info("nID_Task_Activiti:Ok!");
                    } catch (Exception oException) {
                        LOG.error("nID_Task_Activiti:Fail! :{}",oException.getMessage());
                        LOG.debug("FAIL:", oException);
                    }
                    //oExecution.getCurrentActivityId()
                    //nID_Task_Activiti
                /*    
                } catch (Exception oException) {
                    LOG.error("fail!"oException);
                }
                */
                LOG.info("nID_Task_Activiti=" + nID_Task_Activiti);

                FlowSlotTicket oFlowSlotTicket = oFlowSlotTicketDao.findById(nID_FlowSlotTicket).orNull();
                if (oFlowSlotTicket == null) {
                    String sError = "FlowSlotTicket with id=" + nID_FlowSlotTicket + " is not found!";
                    LOG.error(sError);
                    throw new Exception(sError);
                } else if (oFlowSlotTicket.getnID_Task_Activiti() != null) {
                    if (nID_Task_Activiti == oFlowSlotTicket.getnID_Task_Activiti()) {
                        String sWarn = "FlowSlotTicket with id=" + nID_FlowSlotTicket
                                + " has assigned same getnID_Task_Activiti()=" + oFlowSlotTicket.getnID_Task_Activiti();
                        LOG.warn(sWarn);
                    } else {
                        String sError =
                                "FlowSlotTicket with id=" + nID_FlowSlotTicket + " has assigned getnID_Task_Activiti()="
                                        + oFlowSlotTicket.getnID_Task_Activiti();
                        LOG.error(sError);
                        throw new Exception(sError);
                    }
                } else {
                    long nID_FlowSlot = oFlowSlotTicket.getoFlowSlot().getId();
                    LOG.info("(nID_FlowSlot={})", nID_FlowSlot);
                    long nID_Subject = oFlowSlotTicket.getnID_Subject();
                    LOG.info("(nID_Subject={})", nID_Subject);

                    oFlowSlotTicket.setnID_Task_Activiti(nID_Task_Activiti);
                    oFlowSlotTicketDao.saveOrUpdate(oFlowSlotTicket);
                    LOG.info("(JSON={})", JsonRestUtils
                            .toJsonResponse(new SaveFlowSlotTicketResponse(oFlowSlotTicket.getId())));
                    oExecution.setVariable("date_of_visit", sDate);
                    LOG.info("(date_of_visit={})", sDate);
                }
            } catch (Exception oException) {
                LOG.error("Error scanExecutionOnQueueTickets: {}", oException.getMessage());
                LOG.debug("FAIL:", oException);
            }

        }

    }

}
