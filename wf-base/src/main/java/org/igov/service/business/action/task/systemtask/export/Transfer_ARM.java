package org.igov.service.business.action.task.systemtask.export;


import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.delegate.Expression;
import org.activiti.engine.delegate.JavaDelegate;
import org.igov.io.GeneralConfig;
import org.igov.model.arm.DboTkModel;
import org.igov.service.business.action.task.systemtask.mail.Abstract_MailTaskCustom;
import org.igov.service.business.arm.ArmService;
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
@Component("Transfer_ARM")
public class Transfer_ARM extends Abstract_MailTaskCustom implements JavaDelegate {

	private final static Logger LOG = LoggerFactory.getLogger(Transfer_ARM.class);

	private Expression soData;

	@Autowired
	private ArmService armService;

	@Autowired
	GeneralConfig generalConfig;

	
	@Override
	public void execute(DelegateExecution execution) throws Exception {
		// получаю из екзекьюшена sID_order
		String sID_order = generalConfig.getOrderId_ByProcess(Long.valueOf(execution.getProcessInstanceId()));
		LOG.info("sID_order", sID_order);

		// получаю из екзекьюшена soData
		String soData_Value = this.soData.getExpressionText();
		LOG.info("soData_Value before: " + soData_Value);
		String soData_Value_Result = replaceTags(soData_Value, execution);
		LOG.info("soData_Value after: " + soData_Value_Result);
		Map<String, Object> data = parseData(soData_Value_Result);
		LOG.info("data: " + data);

		// из мапы получаем по ключу значения и укладываем все это в
		// модель и туда же укладываем по ключу Out_number значение sID_order
		DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
		
		DboTkModel dataForTransferToArm = new DboTkModel();
		dataForTransferToArm.setIndustry((String) data.get("Industry"));
		dataForTransferToArm.setPriznak((String) data.get("Priznak"));
		dataForTransferToArm.setOut_number(sID_order);
		dataForTransferToArm.setData_out(data.get("Data_out")==null ? (new java.util.Date()) : (df.parse((String)data.get("Data_out"))));
	    dataForTransferToArm.setDep_number((String) data.get("Dep_number"));
	    dataForTransferToArm.setData_in(data.get("Data_in") == null ? (new java.util.Date()): (df.parse((String) data.get("Data_in"))));
	    dataForTransferToArm.setState((String) data.get("State"));
	    dataForTransferToArm.setName_object((String) data.get("Name_object"));
	    dataForTransferToArm.setKod((String) data.get("Kod"));
	    dataForTransferToArm.setGruppa((String) data.get("Gruppa"));
	    dataForTransferToArm.setUndergroup((String) data.get("Undergroup"));
	    dataForTransferToArm.setFinans((String) data.get("Finans"));
	    dataForTransferToArm.setData_out_raz(data.get("Data_out_raz")==null ? (new java.util.Date()) : df.parse((String)data.get("Data_out_raz")));
	    dataForTransferToArm.setNumber_442(Integer.parseInt( (String) data.get("Number_442")));
	    dataForTransferToArm.setWinner((String) data.get("Winner"));
	    dataForTransferToArm.setKod_okpo((String) data.get("Kod_okpo"));
	    dataForTransferToArm.setPhone((String) data.get("Phone"));
	    dataForTransferToArm.setSrok((String) data.get("Srok"));
	    dataForTransferToArm.setExpert((String) data.get("Expert"));
	    dataForTransferToArm.setSumma(new BigDecimal((String) data.get("Summa")));
	    dataForTransferToArm.setuAN((String) data.get("UAN"));
	    dataForTransferToArm.setIf_oplata((String) data.get("If_oplata"));
	    dataForTransferToArm.setUslovie((String) data.get("Uslovie"));
	    dataForTransferToArm.setBank((String) data.get("Bank"));
	    dataForTransferToArm.setSmeta((String) data.get("Smeta"));
	    dataForTransferToArm.setDataEZ(data.get("DataEZ")==null ? (new java.util.Date()) :df.parse((String) data.get("DataEZ")));
	    dataForTransferToArm.setPrilog((String) data.get("Prilog"));
	    dataForTransferToArm.setUpdateData(data.get("UpdateData")==null ? (new java.util.Date()) :df.parse((String) data.get("UpdateData")));
	    dataForTransferToArm.setUpdOKBID(Integer.parseInt( (String)data.get("UpdOKBID")));
	    dataForTransferToArm.setNotes((String) data.get("Notes"));
	    dataForTransferToArm.setArhiv((String) data.get("Arhiv"));
        dataForTransferToArm.setCreateDate(data.get("CreateDate")==null ? (new java.util.Date()) :df.parse((String) data.get("CreateDate")));
	    dataForTransferToArm.setZametki((String) data.get("Zametki"));
	    dataForTransferToArm.setDataBB(data.get("DataBB")==null ? (new java.util.Date()) :df.parse((String) data.get("DataBB")));
	    dataForTransferToArm.setPriemka((String) data.get("Priemka"));
	    dataForTransferToArm.setProckred((String) data.get("Prockred"));
	    dataForTransferToArm.setSumkred(new BigDecimal((String) data.get("Sumkred")));
	    dataForTransferToArm.setSumzak(new BigDecimal((String) data.get("Sumzak")));
	    dataForTransferToArm.setAuctionForm((String) data.get("AuctionForm"));
	    dataForTransferToArm.setProtocol_Number((String) data.get("Protocol_Number"));
	    dataForTransferToArm.setCorrectionDoc((String) data.get("CorrectionDoc"));
	    dataForTransferToArm.setPrioritet((String) data.get("Prioritet"));
	    dataForTransferToArm.setLongterm((String) data.get("Longterm"));

	    LOG.info("dataForTransferToArm = {}",dataForTransferToArm);
		
		// вызываю селект - получаю лист моделей
		List<DboTkModel> listOfModels = armService.getDboTkByOutNumber(sID_order);

		if (listOfModels !=null && !listOfModels.isEmpty()) {
			armService.updateDboTk(dataForTransferToArm);
		}else{
			armService.createDboTk(dataForTransferToArm);
		}
		
	}
}
