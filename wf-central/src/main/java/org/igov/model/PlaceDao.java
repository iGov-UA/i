package org.igov.model;

import org.igov.model.core.EntityDao;
import org.igov.model.PlaceHibernateHierarchyRecord;
import org.igov.model.PlaceHierarchy;

/**
 * @author dgroup
 * @since 20.07.2015
 */
public interface PlaceDao extends EntityDao<Place> {
    PlaceHierarchy getTreeDown(PlaceHibernateHierarchyRecord rootRecord);

    PlaceHierarchy getTreeUp(Long placeId, String uaId, Boolean tree);

    Place getRoot(Place place);
}