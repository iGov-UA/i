package org.igov.service.business.arm;

import java.util.List;

import org.igov.model.arm.ArmDao;
import org.igov.model.arm.DboTkModel;
import org.igov.model.arm.DboTkResult;
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
	public DboTkResult createDboTk(DboTkModel dboTkModel) {
		return armDao.createDboTk(dboTkModel);
		
	}

	@Override
	public DboTkResult updateDboTk(DboTkModel dboTkModel) {
		return armDao.updateDboTk(dboTkModel);
		
	}
	
	@Override
	public DboTkResult updateDboTkByExpert(DboTkModel dboTkModel) {
		return armDao.updateDboTkByExpert(dboTkModel);
	}

	@Override
	public Integer getMaxValue() {
		return armDao.getMaxValue();
	}

	
	

}
