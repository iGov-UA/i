package org.igov.service.business.action.task.systemtask.arm;

import java.util.List;

import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.delegate.Expression;
import org.activiti.engine.delegate.JavaDelegate;
import org.igov.model.arm.DboTkModel;
import org.igov.model.arm.ValidationARM;
import org.igov.service.business.action.task.systemtask.mail.Abstract_MailTaskCustom;
import org.igov.service.business.arm.ArmService;
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

	private Expression soData;

	@Autowired
	private ArmService armService;

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
		LOG.info("dataBEFOREgetEXEC = {}",dataWithExecutorForTransferToArm);
		String prilog = ValidationARM.getPrilog(dataWithExecutorForTransferToArm.getPrilog(),oAttachmetService);
		LOG.info("prilog>>>>>>>>>>>> = {}",prilog);
		dataWithExecutorForTransferToArm.setPrilog(ValidationARM.isValidSizePrilog(prilog));
		List <String> asExecutorsFromsoData = ValidationARM.getAsExecutors(dataWithExecutorForTransferToArm.getExpert(), oAttachmetService, "sName_isExecute");//json с ключом из монги
		LOG.info("asExecutorsFromsoData = {}", asExecutorsFromsoData);

		List<DboTkModel> listOfModels = armService.getDboTkByOutNumber(sID_order);
		
		if (listOfModels != null && !listOfModels.isEmpty()) {

			if (!asExecutorsFromsoData.isEmpty() && asExecutorsFromsoData != null) {
				dataWithExecutorForTransferToArm.setExpert(asExecutorsFromsoData.get(0));
				LOG.info("dataBEFOREgetEXEC первый исполнитель = {}",dataWithExecutorForTransferToArm);
				armService.updateDboTk(dataWithExecutorForTransferToArm);
				// если в листе не одно значение - для каждого исполнителя сетим
				if (asExecutorsFromsoData.get(1) != null) {
					for (int i = 1; i < asExecutorsFromsoData.size(); i++) {
						dataWithExecutorForTransferToArm.setExpert(asExecutorsFromsoData.get(i));
						armService.createDboTk(dataWithExecutorForTransferToArm);
					}
				}
			}else{
				LOG.info("Executors are abcent ");
			}

		}else {
			LOG.info("Model include sID_order "+ sID_order + "not found in ARM");
		}
	}

}
