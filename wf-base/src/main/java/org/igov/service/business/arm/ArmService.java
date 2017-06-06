package org.igov.service.business.arm;

import java.util.List;

import org.igov.model.arm.DboTkModel;

public interface ArmService {

	public List<DboTkModel> getDboTkByOutNumber(String outNumber);

	public void createDboTk(DboTkModel dboTkModel);

	public void updateDboTk(DboTkModel dboTkModel);

}
