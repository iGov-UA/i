package org.igov.service.business.arm;

import java.util.List;

import org.igov.model.arm.DboTkModel;
import org.igov.model.arm.DboTkResult;

public interface ArmService {

	public List<DboTkModel> getDboTkByOutNumber(String outNumber);

	public DboTkResult createDboTk(DboTkModel dboTkModel);

	public DboTkResult updateDboTk(DboTkModel dboTkModel);
	
	public DboTkResult updateDboTkByExpert(DboTkModel dboTkModel);
	
	public Integer getMaxValue();
	
	
	

}
