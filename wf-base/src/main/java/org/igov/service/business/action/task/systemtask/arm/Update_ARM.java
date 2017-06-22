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
 * Предназначен для работы с исполнителями и апдейта существующих заявок
 *
 */
@Component("Update_ARM")
public class Update_ARM extends Abstract_MailTaskCustom implements JavaDelegate {

	private final static Logger LOG = LoggerFactory.getLogger(Update_ARM.class);

	private Expression soData;

	@Autowired
	private ArmService armService;
	
	//имя исполнителя , который выполняет заявку
		private Expression name_isExecute;

	@Override
	public void execute(DelegateExecution execution) throws Exception {
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

		DboTkModel dataWithExecutorForTransferToArm = ValidationARM.fillModel(soData_Value_Result);
		
		String prilog = ValidationARM.getPrilog(dataWithExecutorForTransferToArm.getPrilog(),oAttachmetService);

		dataWithExecutorForTransferToArm.setPrilog(ValidationARM.isValidSizePrilog(prilog));
		/**
		 * Достаем макс значение из базы - будет общее для всех исполнителей в рамках одного докумаента
		 */
		Integer maxNum = armService.getMaxValue();
		LOG.info("int max.... " + maxNum);
	    int number441toModel = maxNum+1;
	    LOG.info("int number441toModel.... " + number441toModel);
	    dataWithExecutorForTransferToArm.setNumber_441(number441toModel);
	    LOG.info("dataWithExecutorForTransferToArm with number441>>>>>>>>>>= {}",dataWithExecutorForTransferToArm);
	    
	    Integer maxNum442 = armService.getMaxValue442();
	    LOG.info("int maxNum442.... " + maxNum442);
	    
	//ветка - когда назначаются исполнители	
			if(expert==null){
				if(dataWithExecutorForTransferToArm.getExpert()!=null){
					List <String> asExecutorsFromsoData = ValidationARM.getAsExecutors(dataWithExecutorForTransferToArm.getExpert(), oAttachmetService, "sName_isExecute");//json с ключом из монги
					LOG.info("asExecutorsFromsoData = {}", asExecutorsFromsoData);
			
					List<DboTkModel> listOfModels = armService.getDboTkByOutNumber(dataWithExecutorForTransferToArm.getOut_number());
					
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
						LOG.info("Model include sID_order "+ dataWithExecutorForTransferToArm.getOut_number() + "not found in ARM");
					}
				}
			}else{
					//ветка, когда исполнители уже есть и они отрабатывают свое задание
				dataWithExecutorForTransferToArm.setExpert(expert);
				armService.updateDboTkByExpert(dataWithExecutorForTransferToArm);
			}
		
	}
	
	
	

}
