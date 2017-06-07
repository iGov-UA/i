package org.igov.model.arm;

import java.util.List;

public interface ArmDao {
	
	public List<DboTkModel> getDboTkByOutNumber(String outNumber);
	
	public DboTkResult createDboTk(DboTkModel dboTkModel);
	
	public DboTkResult updateDboTk(DboTkModel dboTkModel);

}
