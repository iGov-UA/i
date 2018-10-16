package org.igov.model.object.place;

import org.igov.model.core.EntityDao;

public interface PlaceBranchDao extends EntityDao<Long, PlaceBranch> {
    PlaceBranch findByKey(String sKey);
}
