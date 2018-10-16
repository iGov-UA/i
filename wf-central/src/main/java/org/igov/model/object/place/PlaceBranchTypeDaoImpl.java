package org.igov.model.object.place;

import org.igov.model.core.GenericEntityDao;
import org.springframework.stereotype.Repository;

@Repository
public class PlaceBranchTypeDaoImpl extends GenericEntityDao<Long, PlaceBranchType> implements PlaceBranchTypeDao {
    public PlaceBranchTypeDaoImpl() {
        super(PlaceBranchType.class);
    }
}
