package org.igov.model.subject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;
import org.igov.model.core.GenericEntityDao;

@Repository
public class SubjectRightBPDaoImpl extends GenericEntityDao<Long, SubjectRightBP> implements SubjectRightBPDao  {

	private static final Logger LOG = LoggerFactory.getLogger(SubjectRightBPDaoImpl.class);

	protected SubjectRightBPDaoImpl() {
        super(SubjectRightBP.class);
    }
	
}
