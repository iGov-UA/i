package org.igov.model.arm;

import java.util.List;

public interface ArmDao {
	
	public List<DboTkModel> getDboTkByOutNumber(String outNumber);
	
	public void createDboTk(DboTkModel dboTkModel);
	
	public void updateDboTk(DboTkModel dboTkModel);

}
