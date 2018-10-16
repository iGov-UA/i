package org.igov.model.subject;

import org.igov.model.core.EntityDao;

import java.util.List;

public interface SubjectContactPlacePointDao extends EntityDao<Long, SubjectContactPlacePoint> {

    List<SubjectContactPlacePoint> findBySubject(Long nID_Subject);
}
