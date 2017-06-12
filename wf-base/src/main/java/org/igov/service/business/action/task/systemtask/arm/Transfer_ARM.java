package org.igov.service.business.action.task.systemtask.arm;


import java.util.ArrayList;
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
@Component("Transfer_ARM")
public class Transfer_ARM extends Abstract_MailTaskCustom implements JavaDelegate {

	private final static Logger LOG = LoggerFactory.getLogger(Transfer_ARM.class);
	
	private Expression soData;

	@Autowired
	private ArmService armService;
	
	//имя исполнителя , который выполняет заявку
	private Expression name_isExecute;

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
		
		String prilog = ValidationARM.getPrilog(dataForTransferToArm.getPrilog(),oAttachmetService);
		LOG.info("prilog>>>>>>>>>>>> = {}",prilog);
		dataForTransferToArm.setPrilog(ValidationARM.isValidSizePrilog(prilog));
	    LOG.info("dataForTransferToArm = {}",dataForTransferToArm);
		List<DboTkModel> listOfModels = new ArrayList<>();
		if(ValidationARM.isValid(dataForTransferToArm.getOut_number())){
			listOfModels = armService.getDboTkByOutNumber(dataForTransferToArm.getOut_number());
			transferDateArm(dataForTransferToArm.getOut_number(), dataForTransferToArm, listOfModels,execution);
			
		}else{
			listOfModels = armService.getDboTkByOutNumber(sID_order);
			transferDateArm(sID_order, dataForTransferToArm, listOfModels,execution);
		}

	}

	private void transferDateArm(String sID_order, DboTkModel dataForTransferToArm, List<DboTkModel> listOfModels,DelegateExecution execution) {
		if (listOfModels !=null && !listOfModels.isEmpty()) {
			if (ValidationARM.isValid(dataForTransferToArm.getExpert())) {
				String expert = getStringFromFieldExpression(this.name_isExecute, execution);
					LOG.info("expert = {}",expert);
						dataForTransferToArm.setExpert(expert);
						armService.updateDboTkByExpert(dataForTransferToArm);
			} else {
				armService.updateDboTk(dataForTransferToArm);
			}
		}else{
			dataForTransferToArm.setOut_number(sID_order);
			armService.createDboTk(dataForTransferToArm);
		}
	}
	
	

}
