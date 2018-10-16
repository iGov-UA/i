package org.igov.model.object.place;

import org.igov.model.core.GenericEntityDao;
import org.springframework.stereotype.Repository;

@Repository
public class PlaceBuildPartTypeDaoImpl extends GenericEntityDao<Long, PlaceBuildPartType> implements PlaceBuildPartTypeDao {
    public PlaceBuildPartTypeDaoImpl() {
        super(PlaceBuildPartType.class);
    }
}
