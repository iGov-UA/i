package org.igov.model.object.place;

import org.igov.model.core.GenericEntityDao;
import org.springframework.stereotype.Repository;

@Repository
public class PlaceBuildDaoImpl extends GenericEntityDao<Long, PlaceBuild> implements PlaceBuildDao {
    public PlaceBuildDaoImpl() {
        super(PlaceBuild.class);
    }
}
