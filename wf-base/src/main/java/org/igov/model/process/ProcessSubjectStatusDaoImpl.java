package org.igov.model.process;

import org.apache.log4j.Logger;
import org.igov.model.core.GenericEntityDao;
import org.springframework.stereotype.Repository;

@Repository()
public class ProcessSubjectStatusDaoImpl extends GenericEntityDao<Long, ProcessSubjectStatus> implements ProcessSubjectStatusDao {

    private static final Logger LOG = Logger.getLogger(ProcessSubjectStatusDaoImpl.class);

    public ProcessSubjectStatusDaoImpl() {
        super(ProcessSubjectStatus.class);
    }

}
