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

import com.google.common.base.Predicate;
import com.google.common.collect.Collections2;
import com.google.common.collect.Lists;

/**
 *
 * @author Elena Предназначен для работы с исполнителями и апдейта существующих
 *         заявок
 *
 */
@Component("Update_ARM")
public class Update_ARM extends Abstract_MailTaskCustom implements JavaDelegate {

	private final static Logger LOG = LoggerFactory.getLogger(Update_ARM.class);

	private Expression soData;

	@Autowired
	private ArmService armService;

	// имя исполнителя , который выполняет заявку
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

		LOG.info("expert>>>>>>>>>>>> = {}", expert);

		DboTkModel dataWithExecutorForTransferToArm = ValidationARM.fillModel(soData_Value_Result);

		String prilog = ValidationARM.getPrilog(dataWithExecutorForTransferToArm.getPrilog(), oAttachmetService);

		dataWithExecutorForTransferToArm.setPrilog(ValidationARM.isValidSizePrilog(prilog));

		List<DboTkModel> listOfModels = armService
				.getDboTkByOutNumber(dataWithExecutorForTransferToArm.getOut_number());

		// ветка - когда назначаются исполнители
		if (expert == null) {
			if (dataWithExecutorForTransferToArm.getExpert() != null) {
				List<String> asExecutorsFromsoData = ValidationARM.getAsExecutors(
						dataWithExecutorForTransferToArm.getExpert(), oAttachmetService, "sName_isExecute");// json c ключом из монги
				LOG.info("asExecutorsFromsoData = {}", asExecutorsFromsoData);

				if (listOfModels != null && !listOfModels.isEmpty()) {
					// если заявка есть в базе, получаем из листа моделей из
					// базы - номера 441 и 442 и сетим их в свою модель
					int _441fromModelFromBase = listOfModels.get(0).getNumber_441();
					int _442fromModelFromBase = listOfModels.get(0).getNumber_442();
					// и сетим их в свою модель
					dataWithExecutorForTransferToArm.setNumber_441(_441fromModelFromBase);
					dataWithExecutorForTransferToArm.setNumber_442(_442fromModelFromBase);
					if (asExecutorsFromsoData != null && !asExecutorsFromsoData.isEmpty()) {
						dataWithExecutorForTransferToArm.setExpert(asExecutorsFromsoData.get(0));
						// dataWithExecutorForTransferToArm.setNumber_442(dataWithExecutorForTransferToArm.getNumber_442());
						LOG.info("dataBEFOREgetEXEC первый исполнитель = {}", dataWithExecutorForTransferToArm);
						armService.updateDboTk(dataWithExecutorForTransferToArm);
						// если в листе не одно значение - для каждого
						// исполнителя сетим
						if (asExecutorsFromsoData.size() > 1) {
							for (int i = 1; i < asExecutorsFromsoData.size(); i++) {
								dataWithExecutorForTransferToArm.setExpert(asExecutorsFromsoData.get(i));
								dataWithExecutorForTransferToArm
										.setNumber_442(dataWithExecutorForTransferToArm.getNumber_442() + 1);
								armService.createDboTk(dataWithExecutorForTransferToArm);
							}
						}
					} else {
						LOG.info("Executors are abcent ");
					}

				} else {
					LOG.info("Model include sID_order " + dataWithExecutorForTransferToArm.getOut_number()
							+ "not found in ARM");
				}
			}
		} else {
			if (listOfModels != null && !listOfModels.isEmpty()) {
				// получить только нужных експертов
				final List<DboTkModel> dboTkModels = Lists
						.newArrayList(Collections2.filter(listOfModels, new Predicate<DboTkModel>() {
							@Override
							public boolean apply(DboTkModel dboTkModel) {
								return dboTkModel.equals(expert);
							}
						}));

				if (dboTkModels != null && !dboTkModels.isEmpty()) {
					for (DboTkModel dboTkModel : dboTkModels) {
						dataWithExecutorForTransferToArm.setExpert(expert);
						LOG.info("dataWithExecutorForTransferToArm what will be upload to ARM >>>",
								dataWithExecutorForTransferToArm);
						dataWithExecutorForTransferToArm.setNumber_442(dboTkModel.getNumber_442());
						dataWithExecutorForTransferToArm.setNumber_441(dboTkModel.getNumber_441());
						armService.updateDboTkByExpert(dataWithExecutorForTransferToArm);
					}
				} else {
					updateExpert(expert, dataWithExecutorForTransferToArm);
				}
			}else{
				updateExpert(expert, dataWithExecutorForTransferToArm);
			}
		}
	}

	private void updateExpert(String expert, DboTkModel dataWithExecutorForTransferToArm) {
		// ветка, когда исполнители уже есть и они отрабатывают свое
		// задание
		dataWithExecutorForTransferToArm.setExpert(expert);
		LOG.info("dataWithExecutorForTransferToArm what will be upload to ARM >>>",
				dataWithExecutorForTransferToArm);
		// dataWithExecutorForTransferToArm.setNumber_442(dataWithExecutorForTransferToArm.getNumber_442());
		armService.updateDboTkByExpert(dataWithExecutorForTransferToArm);
	}

}
