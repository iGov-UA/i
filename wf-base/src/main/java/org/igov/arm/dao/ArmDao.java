package org.igov.arm.dao;

import java.util.List;

import javax.sql.DataSource;

import org.igov.arm.model.DboTkModel;

public interface ArmDao {
	
	public List<DboTkModel> getDboTkByOutNumber(String outNumber);
	
	public void createDboTk(DboTkModel dboTkModel);
	
	public void updateDboTk(DboTkModel dboTkModel);

}
