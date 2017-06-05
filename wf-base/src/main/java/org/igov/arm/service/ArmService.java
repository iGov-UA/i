package org.igov.arm.service;

import java.util.List;

import org.igov.arm.model.DboTkModel;

public interface ArmService {

	public List<DboTkModel> getDboTkByOutNumber(String outNumber);

	public void createDboTk(DboTkModel dboTkModel);

	public void updateDboTk(DboTkModel dboTkModel);

}
