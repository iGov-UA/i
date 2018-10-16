package org.igov.model.subject;

import org.igov.model.core.EntityDao;

public interface SubjectContactPlacePointTypeDao extends EntityDao<Long, SubjectContactPlacePointType> {
    public SubjectContactPlacePointType findByNameEN(String sName_EN);
}
