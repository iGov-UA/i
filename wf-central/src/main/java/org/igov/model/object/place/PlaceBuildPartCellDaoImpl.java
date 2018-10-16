package org.igov.model.object.place;

import org.igov.model.core.GenericEntityDao;
import org.springframework.stereotype.Repository;

@Repository
public class PlaceBuildPartCellDaoImpl extends GenericEntityDao<Long, PlaceBuildPartCell> implements PlaceBuildPartCellDao {
    public PlaceBuildPartCellDaoImpl() {
        super(PlaceBuildPartCell.class);
    }
}
