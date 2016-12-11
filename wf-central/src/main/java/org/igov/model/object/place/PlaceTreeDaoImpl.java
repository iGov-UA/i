package org.igov.model.object.place;

import org.igov.model.core.GenericEntityDao;
import org.springframework.stereotype.Repository;

/**
 *
 * @author Kovilin
 */
@Repository
public class PlaceTreeDaoImpl extends GenericEntityDao<Long, PlaceTree> implements PlaceTreeDao{

    public PlaceTreeDaoImpl() {
        super(PlaceTree.class);
    }

    
}
