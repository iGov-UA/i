package org.igov.service.business.action.task.systemtask.export;


import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.delegate.Expression;
import org.activiti.engine.delegate.JavaDelegate;
import org.apache.commons.io.IOUtils;
import org.igov.io.GeneralConfig;
import org.igov.model.arm.DboTkModel;
import org.igov.model.arm.ValidationARM;
import org.igov.service.business.action.task.systemtask.mail.Abstract_MailTaskCustom;
import org.igov.service.business.arm.ArmService;
import org.igov.service.conf.AttachmetService;
import org.json.simple.parser.JSONParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component; 

/**
 *
 * @author Elena
 *
 */
@Component("Transfer_ARM")
public class Transfer_ARM extends Abstract_MailTaskCustom implements JavaDelegate {

	private final static Logger LOG = LoggerFactory.getLogger(Transfer_ARM.class);
	
	private Expression soData;

	@Autowired
	private ArmService armService;

	@Autowired
	GeneralConfig generalConfig;
	
	@Autowired
	private AttachmetService oAttachmetService;

	
	@Override
	public void execute(DelegateExecution execution) throws Exception {
		// получаю из екзекьюшена sID_order
		String sID_order = generalConfig.getOrderId_ByProcess(Long.valueOf(execution.getProcessInstanceId()));
		LOG.info("sID_order in Transfer_ARM>>>>>>>>>>>"+ sID_order);

		// получаю из екзекьюшена soData
		String soData_Value = this.soData.getExpressionText();
		LOG.info("soData_Value before: " + soData_Value);
		String soData_Value_Result = replaceTags(soData_Value, execution);
		LOG.info("soData_Value after: " + soData_Value_Result);


		// из мапы получаем по ключу значения и укладываем все это в
		// модель и туда же укладываем по ключу Out_number значение sID_order
		DboTkModel dataForTransferToArm = ValidationARM.fillModel(soData_Value_Result);
		dataForTransferToArm.setOut_number(sID_order);
		String prilog = getPrilog(dataForTransferToArm.getPrilog(),oAttachmetService);
		LOG.info("prilog>>>>>>>>>>>> = {}",prilog);
		dataForTransferToArm.setPrilog(ValidationARM.isValidSizePrilog(prilog));
	    LOG.info("dataForTransferToArm = {}",dataForTransferToArm);
		
		// вызываю селект - получаю лист моделей
		List<DboTkModel> listOfModels = armService.getDboTkByOutNumber(sID_order);

		if (listOfModels !=null && !listOfModels.isEmpty()) {
			armService.updateDboTk(dataForTransferToArm);
		}else{
			armService.createDboTk(dataForTransferToArm);
		}
		
	}
	
	

	/**
	 * Получение значения поля Prilog - должно одержать имена атачей прикрепленных
	 * @param data
	 * @param oAttachmetService
	 * @return
	 */
	public String getPrilog(String data, AttachmetService oAttachmetService) {
		String listStringPrilog = ", ";
		if (ValidationARM.isValid(data)) {
			org.json.simple.JSONObject oJSONObject = null;
			try {
				JSONParser parser = new JSONParser();

				org.json.simple.JSONObject oTableJSONObject = (org.json.simple.JSONObject) parser.parse(data);

				InputStream oAttachmet_InputStream = oAttachmetService.getAttachment(null, null,
						(String) oTableJSONObject.get("sKey"), (String) oTableJSONObject.get("sID_StorageType"))
						.getInputStream();

				oJSONObject = (org.json.simple.JSONObject) parser
						.parse(IOUtils.toString(oAttachmet_InputStream, "UTF-8"));
				LOG.info("oTableJSONObject in listener Transfer_ARM: " + oJSONObject.toJSONString());
				 org.json.simple.JSONArray aJsonRow = (org.json.simple.JSONArray) oJSONObject.get("aRow");

	                if (aJsonRow != null) {
	                	List<String> listPrilogName = new ArrayList<String>();
	                    for (int i = 0; i < aJsonRow.size(); i++) {
	                        org.json.simple.JSONObject oJsonField = (org.json.simple.JSONObject) aJsonRow.get(i);
	                        LOG.info("oJsonField in {}", oJsonField);
	                        if (oJsonField != null) {
	                            org.json.simple.JSONArray aJsonField = (org.json.simple.JSONArray) oJsonField.get("aField");
	                            LOG.info("aJsonField in getPrilog is {}", aJsonField);
	                            if (aJsonField != null) {
	                                for (int j = 0; j < aJsonField.size(); j++) {
	                                    org.json.simple.JSONObject oJsonMap = (org.json.simple.JSONObject) aJsonField
	                                            .get(j);
	                                    LOG.info("oJsonMap in getPrilog is {}", oJsonMap);
	                                    if (oJsonMap != null) {
	                                        Object fileName = oJsonMap.get("fileName");
	                                            if (fileName != null) {
	                                                LOG.info("oValue in getPrilog is {}", fileName);
	                                                listPrilogName.add((String) fileName);
	                                            } else {
	                                                LOG.info("oValue in getPrilog is null");
	                                            }
	                                    }
	                                }
	                            }
	                        }
	                    }
	                    listStringPrilog = String.join(", ", listPrilogName);
	                } else {
	                    LOG.info("JSON array is null in getPrilog is null");
	                }
			} catch (Exception e) {
				LOG.error("oTableJSONObject in listener Transfer_ARM: " + oJSONObject.toJSONString());
			}
		}
		return listStringPrilog;
	}
	
	
}
