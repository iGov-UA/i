package org.igov.model.object.place;

import org.igov.model.core.GenericEntityDao;
import org.springframework.stereotype.Repository;

@Repository
public class PlaceBuildPartDaoImpl extends GenericEntityDao<Long, PlaceBuildPart> implements PlaceBuildPartDao {
    public PlaceBuildPartDaoImpl() {
        super(PlaceBuildPart.class);
    }
}
