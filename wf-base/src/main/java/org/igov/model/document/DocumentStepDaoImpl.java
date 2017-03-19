package org.igov.model.document;

import org.hibernate.Criteria;
import org.hibernate.criterion.Restrictions;
import org.igov.model.core.GenericEntityDao;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class DocumentStepDaoImpl extends GenericEntityDao<Long, DocumentStep> implements DocumentStepDao {

	public DocumentStepDaoImpl() {
		super(DocumentStep.class);
	}

	@Override
	public List<DocumentStep> getStepForProcess(String snID_Process_Activiti) {
		return super.findAllBy("snID_Process_Activiti", snID_Process_Activiti);
	}

	@Override
	public List <DocumentStep> getRightsByStep(String snID_Process_Activiti, String sKey_Step) {
		Criteria criteria = createCriteria();
		criteria.add(Restrictions.eq("sKey_Step", sKey_Step));
		criteria.add(Restrictions.eq("snID_Process_Activiti", snID_Process_Activiti));

		return criteria.list();
	}

}
