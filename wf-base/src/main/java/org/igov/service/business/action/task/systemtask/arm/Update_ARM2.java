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
@Component("Update_ARM2")
public class Update_ARM2 extends Abstract_MailTaskCustom implements JavaDelegate {

	private final static Logger LOG = LoggerFactory.getLogger(Update_ARM2.class);

	private Expression soData;

	@Autowired
	private ArmService armService;
	
	//имя исполнителя , который выполняет заявку
		private Expression name_isExecute;

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
		
		/**
		 * Достаем имя исполнителя
		 */
		String expert = getStringFromFieldExpression(this.name_isExecute, execution);
		
		LOG.info("expert>>>>>>>>>>>> = {}",expert);

		// из мапы получаем по ключу значения и укладываем все это в
		// модель и туда же укладываем по ключу Out_number значение sID_order
		DboTkModel dataWithExecutorForTransferToArm = ValidationARM.fillModel(soData_Value_Result);
		dataWithExecutorForTransferToArm.setOut_number(sID_order);
		LOG.info("dataBEFOREgetEXEC = {}",dataWithExecutorForTransferToArm);
		String prilog = ValidationARM.getPrilog(dataWithExecutorForTransferToArm.getPrilog(),oAttachmetService);
		LOG.info("prilog>>>>>>>>>>>> = {}",prilog);
		dataWithExecutorForTransferToArm.setPrilog(ValidationARM.isValidSizePrilog(prilog));
		
	//ветка - когда назначаются исполнители	
			if(expert==null){
				if(dataWithExecutorForTransferToArm.getExpert()!=null){
					List <String> asExecutorsFromsoData = ValidationARM.getAsExecutors(dataWithExecutorForTransferToArm.getExpert(), oAttachmetService, "sName_isExecute");//json с ключом из монги
					LOG.info("asExecutorsFromsoData = {}", asExecutorsFromsoData);
			
					List<DboTkModel> listOfModels = armService.getDboTkByOutNumber(sID_order);
					
					if (listOfModels != null && !listOfModels.isEmpty()) {
			
						if (asExecutorsFromsoData != null && !asExecutorsFromsoData.isEmpty()) {
							dataWithExecutorForTransferToArm.setExpert(asExecutorsFromsoData.get(0));
							LOG.info("dataBEFOREgetEXEC первый исполнитель = {}",dataWithExecutorForTransferToArm);
							armService.updateDboTk(dataWithExecutorForTransferToArm);
							// если в листе не одно значение - для каждого исполнителя сетим
							if (asExecutorsFromsoData.size()>1) {
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
			}else{
				LOG.info("expert1>>>>>>>>>>>> = {}",expert);
					//ветка, когда исполнители уже есть и они отрабатывают свое задание
					List<DboTkModel> listOfModels = new ArrayList<>();
					if(ValidationARM.isValid(dataWithExecutorForTransferToArm.getOut_number())){
						listOfModels = armService.getDboTkByOutNumber(dataWithExecutorForTransferToArm.getOut_number());
					}else{
						listOfModels = armService.getDboTkByOutNumber(sID_order);
					}
					transferDateArm(dataWithExecutorForTransferToArm, listOfModels,expert);
			}
		
	}
	
	
	
	private void transferDateArm(DboTkModel dataForTransferToArm, List<DboTkModel> listOfModels,String expert) {
		if (listOfModels !=null && !listOfModels.isEmpty()) {
					LOG.info("expert = {}",expert);
						dataForTransferToArm.setExpert(expert);
						armService.updateDboTkByExpert(dataForTransferToArm);
		}
	}

}
