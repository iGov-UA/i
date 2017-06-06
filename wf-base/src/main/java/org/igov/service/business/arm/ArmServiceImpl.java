package org.igov.service.business.arm;

import java.util.List;

import org.igov.model.arm.ArmDao;
import org.igov.model.arm.DboTkModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ArmServiceImpl implements ArmService {
	
	@Autowired
    private ArmDao armDao;

	@Override
	public List<DboTkModel> getDboTkByOutNumber(String outNumber) {
		return armDao.getDboTkByOutNumber(outNumber);
	}

	@Override
	public void createDboTk(DboTkModel dboTkModel) {
		armDao.createDboTk(dboTkModel);
		
	}

	@Override
	public void updateDboTk(DboTkModel dboTkModel) {
		armDao.updateDboTk(dboTkModel);
		
	}
	
	

}
