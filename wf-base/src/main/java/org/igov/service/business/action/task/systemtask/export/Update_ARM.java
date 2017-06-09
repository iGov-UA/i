package org.igov.service.business.action.task.systemtask.export;

import java.io.InputStream;
import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

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

import static org.igov.util.Tool.parseData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 *
 * @author Elena
 *
 */
@Component("Update_ARM")
public class Update_ARM extends Abstract_MailTaskCustom implements JavaDelegate {

	private final static Logger LOG = LoggerFactory.getLogger(Update_ARM.class);

	private final static String[] formats = { "yyyy-MM-dd", "dd-MM-yyyy", "yyyy/MM/dd", "dd/MM/yyyy", "yyyy.MM.dd",
			"dd.MM.yyyy", "yyyyy-MM-dd HH:mm:ss", "yyyyy/MM/dd HH:mm:ss", "yyyyy.MM.dd HH:mm:ss" };

	private Expression soData;

	@Autowired
	private ArmService armService;

	@Autowired
	GeneralConfig generalConfig;

	@Override
	public void execute(DelegateExecution execution) throws Exception {
		// получаю из екзекьюшена sID_order
		String sID_order = generalConfig.getOrderId_ByProcess(Long.valueOf(execution.getProcessInstanceId()));
		LOG.info("sID_order in Update_ARM>>>>>>>>>>>" + sID_order);

		

		// получаю из екзекьюшена soData
		String soData_Value = this.soData.getExpressionText();
		LOG.info("soData_Value before: " + soData_Value);
		String soData_Value_Result = replaceTags(soData_Value, execution);
		LOG.info("soData_Value after: " + soData_Value_Result);

		// из мапы получаем по ключу значения и укладываем все это в
		// модель и туда же укладываем по ключу Out_number значение sID_order
		DboTkModel dataWithExecutorForTransferToArm = ValidationARM.fillModel(soData_Value_Result);
		
		dataWithExecutorForTransferToArm.setOut_number(sID_order);
		List<String> asExecutorsFromsoData = getAsExecutors(dataWithExecutorForTransferToArm.getExpert(), oAttachmetService);
		LOG.info("asExecutorsFromsoData = {}", asExecutorsFromsoData);

		List<DboTkModel> listOfModels = armService.getDboTkByOutNumber(sID_order);

		if (listOfModels != null && !listOfModels.isEmpty()) {

			if (!asExecutorsFromsoData.isEmpty() && asExecutorsFromsoData != null) {
				dataWithExecutorForTransferToArm.setExpert(asExecutorsFromsoData.get(0));
				armService.updateDboTk(dataWithExecutorForTransferToArm);
				// если в листе не одно значение - для каждого исполнителя сетим
				if (asExecutorsFromsoData.get(1) != null) {
					for (int i = 1; i < asExecutorsFromsoData.size(); i++) {
						
						dataWithExecutorForTransferToArm.setExpert(asExecutorsFromsoData.get(0));
						armService.createDboTk(dataWithExecutorForTransferToArm);
					}
				}
			}else LOG.info("Executors are abcent ");

		}else LOG.info("Model include sID_order "+ sID_order + "not found in ARM");
	}

	/**
	 * Получение значения поля Expert - которое содержит имена исполнителей
	 * 
	 * @param data
	 * @param oAttachmetService
	 * @return
	 */
	public List<String> getAsExecutors(String data, AttachmetService oAttachmetService) {
		List<String> listPrilogName = new ArrayList<String>(); // array of
																// executors
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
				LOG.info("oTableJSONObject in listener Update_ARM: " + oJSONObject.toJSONString());
				org.json.simple.JSONArray aJsonRow = (org.json.simple.JSONArray) oJSONObject.get("aRow");

				if (aJsonRow != null) {

					for (int i = 0; i < aJsonRow.size(); i++) {
						org.json.simple.JSONObject oJsonField = (org.json.simple.JSONObject) aJsonRow.get(i);
						LOG.info("oJsonField in  Update_ARM: {}", oJsonField);
						if (oJsonField != null) {
							org.json.simple.JSONArray aJsonField = (org.json.simple.JSONArray) oJsonField.get("aField");
							LOG.info("aJsonField in getExpert is {}", aJsonField);
							if (aJsonField != null) {
								for (int j = 0; j < aJsonField.size(); j++) {
									org.json.simple.JSONObject oJsonMap = (org.json.simple.JSONObject) aJsonField
											.get(j);
									LOG.info("oJsonMap in getExpert is {}", oJsonMap);
									if (oJsonMap != null) {
										Object value = oJsonMap.get("value");
										if (value != null) {
											LOG.info("oValue in getExpert is {}", value);
											listPrilogName.add((String) value);
										} else {
											LOG.info("oValue in getExpert is null");
										}
									}
								}
							}
						}
					}

				} else {
					LOG.info("JSON array is null in getExpert is null");
				}
			} catch (Exception e) {
				LOG.error("oTableJSONObject in listener Update_ARM: " + oJSONObject.toJSONString());
			}
		}
		return listPrilogName;
	}
}
