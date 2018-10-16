package org.igov.model.object.place;

import org.igov.model.core.GenericEntityDao;
import org.springframework.stereotype.Repository;

@Repository
public class PlaceBranchDaoImpl extends GenericEntityDao<Long, PlaceBranch> implements PlaceBranchDao {
    public PlaceBranchDaoImpl() {
        super(PlaceBranch.class);
    }


    @Override
    public PlaceBranch findByKey(String sKey) {
        return null;
    }
}
