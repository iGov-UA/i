package org.igov.model.object.place;

import org.igov.model.core.GenericEntityDao;
import org.springframework.stereotype.Repository;

@Repository
public class PlaceBuildPartCellTypeDaoImpl extends GenericEntityDao<Long, PlaceBuildPartCellType> implements PlaceBuildPartCellTypeDao {
    public PlaceBuildPartCellTypeDaoImpl() {
        super(PlaceBuildPartCellType.class);
    }
}
