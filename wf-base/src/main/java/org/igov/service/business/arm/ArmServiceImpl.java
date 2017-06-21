package org.igov.service.business.arm;

import java.util.Comparator;
import java.util.List;

import org.igov.model.arm.ArmDao;
import org.igov.model.arm.DboTkModel;
import org.igov.model.arm.DboTkModelMaxNum;
import org.igov.model.arm.DboTkResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.google.common.base.Function;
import com.google.common.collect.Collections2;
import com.google.common.collect.Lists;

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
		
		List<DboTkModelMaxNum> dboTkModelMaxNumList = armDao.getMaxValue();
		
		final List<Integer> number441 = Lists.newArrayList(Collections2.transform(
				dboTkModelMaxNumList, new Function<DboTkModelMaxNum, Integer>() {
					@Override
					public Integer apply(DboTkModelMaxNum dboTkModelMaxNum) {
						return dboTkModelMaxNum.getNumber_441();
					}
				}));
		
		Integer maxNumber = number441.stream()
		        .max(Comparator.comparing(i -> i)).get();
		
		
		return maxNumber;
	}

	
	

}
