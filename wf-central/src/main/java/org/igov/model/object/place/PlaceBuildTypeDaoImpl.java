package org.igov.model.object.place;

import org.igov.model.core.GenericEntityDao;
import org.springframework.stereotype.Repository;

@Repository
public class PlaceBuildTypeDaoImpl extends GenericEntityDao<Long, PlaceBuildType> implements PlaceBuildTypeDao {
    public PlaceBuildTypeDaoImpl() {
        super(PlaceBuildType.class);
    }
}
