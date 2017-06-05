package org.igov.arm.service;

import java.util.List;

import org.igov.arm.dao.ArmDao;
import org.igov.arm.model.DboTkModel;
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
