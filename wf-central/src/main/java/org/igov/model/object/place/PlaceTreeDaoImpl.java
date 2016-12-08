package org.igov.model.object.place;

import org.igov.model.core.GenericEntityDao;

/**
 *
 * @author Kovilin
 */
public class PlaceTreeDaoImpl extends GenericEntityDao<Long, PlaceTree> implements PlaceTreeDao{

    public PlaceTreeDaoImpl() {
        super(PlaceTree.class);
    }

    
}
