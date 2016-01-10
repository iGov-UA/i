package org.igov.model.object.place;

import org.igov.model.core.EntityDao;
import org.igov.model.object.place.PlaceHibernateHierarchyRecord;
import org.igov.model.object.place.PlaceHierarchy;

/**
 * @author dgroup
 * @since 20.07.2015
 */
public interface PlaceDao extends EntityDao<Place> {
    PlaceHierarchy getTreeDown(PlaceHibernateHierarchyRecord rootRecord);

    PlaceHierarchy getTreeUp(Long placeId, String uaId, Boolean tree);

    Place getRoot(Place place);
}