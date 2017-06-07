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
	    dataForTransferToArm.setNumber_442(data.get("Number_442")==null? 0: Integer.parseInt( (String) data.get("Number_442")));
	    dataForTransferToArm.setWinner(data.get("Winner")==null? "": (String) data.get("Winner"));
	    dataForTransferToArm.setKod_okpo(data.get("Kod_okpo")==null? "":(String)data.get("Kod_okpo"));
	    dataForTransferToArm.setPhone(data.get("Phone")==null? "": (String) data.get("Phone"));
	    dataForTransferToArm.setSrok(data.get("Srok")==null? "":(String) data.get("Srok"));
	    dataForTransferToArm.setExpert(data.get("Expert")==null? "":(String) data.get("Expert"));
	    dataForTransferToArm.setSumma(new BigDecimal((String) data.get("Summa")));
	    dataForTransferToArm.setuAN(data.get("UAN")==null? "":(String) data.get("UAN"));
	    dataForTransferToArm.setIf_oplata(data.get("If_oplata")==null? "":(String) data.get("If_oplata"));
	    dataForTransferToArm.setUslovie(data.get("Uslovie")==null? "":(String) data.get("Uslovie"));
	    dataForTransferToArm.setBank(data.get("Bank")==null? "":(String) data.get("Bank"));
	    dataForTransferToArm.setSmeta(data.get("Smeta")==null? "":(String) data.get("Smeta"));
	    dataForTransferToArm.setDataEZ(data.get("DataEZ")==null ? (new java.util.Date()) :df.parse((String) data.get("DataEZ")));
	    dataForTransferToArm.setPrilog(data.get("Prilog")==null? "":(String) data.get("Prilog"));
	    dataForTransferToArm.setUpdateData(data.get("UpdateData")==null ? (new java.util.Date()) :df.parse((String) data.get("UpdateData")));
	    dataForTransferToArm.setUpdOKBID(Integer.parseInt( (String)data.get("UpdOKBID")));
	    dataForTransferToArm.setNotes(data.get("Notes")==null? "":(String) data.get("Notes"));
	    dataForTransferToArm.setArhiv(data.get("Arhiv")==null? "":(String) data.get("Arhiv"));
        dataForTransferToArm.setCreateDate(data.get("CreateDate")==null ? (new java.util.Date()) :df.parse((String) data.get("CreateDate")));
	    dataForTransferToArm.setZametki(data.get("Zametki")==null? "":(String) data.get("Zametki"));
	    dataForTransferToArm.setDataBB(data.get("DataBB")==null ? (new java.util.Date()) :df.parse((String) data.get("DataBB")));
	    dataForTransferToArm.setPriemka(data.get("Priemka")==null? "":(String) data.get("Priemka"));
	    dataForTransferToArm.setProckred(data.get("Prockred")==null? "":(String) data.get("Prockred"));
	    dataForTransferToArm.setSumkred(new BigDecimal((String) data.get("Sumkred")));
	    dataForTransferToArm.setSumzak(new BigDecimal((String) data.get("Sumzak")));
	    dataForTransferToArm.setAuctionForm(data.get("AuctionForm")==null? "":(String) data.get("AuctionForm"));
	    dataForTransferToArm.setProtocol_Number(data.get("Protocol_Number")==null? "":(String) data.get("Protocol_Number"));
	    dataForTransferToArm.setCorrectionDoc(data.get("CorrectionDoc")==null? "":(String) data.get("CorrectionDoc"));
	    dataForTransferToArm.setPrioritet(data.get("Prioritet")==null? "":(String) data.get("Prioritet"));
	    dataForTransferToArm.setLongterm(data.get("Longterm")==null? "":(String) data.get("Longterm"));

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
