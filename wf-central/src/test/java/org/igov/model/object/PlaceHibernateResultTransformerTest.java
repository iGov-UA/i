package org.igov.model.object;

import org.junit.Test;
import org.igov.model.object.place.PlaceHibernateHierarchyRecord;
import org.igov.model.object.place.PlaceHibernateResultTransformer;

import static org.junit.Assert.assertEquals;

/**
 * @author dgroup
 * @since 13.08.2015
 */
public class PlaceHibernateResultTransformerTest {

    @Test
    public void transformTuple() {
        String labels[] = {
                "id", "type_id", "ua_id",
                "name", "original_name", "parent_id",
                "area_id", "root_id", "level" };

        Object data[] = {
                459, 2, "5923500000",
                "Недригайлівський район/смт Недригайлів", "", 50023,
                50023, 50023, 0 };

        PlaceHibernateHierarchyRecord phr = new PlaceHibernateResultTransformer()
                .transformTuple(data, labels);

        assertEquals("ID aren't match", 459L, phr.getPlaceId().longValue());
    }
}
