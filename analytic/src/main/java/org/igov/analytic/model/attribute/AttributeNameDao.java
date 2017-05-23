package org.igov.analytic.model.attribute;

import org.igov.model.core.EntityDao;

/**
 * Created by dpekach on 18.05.17.
 */
public interface AttributeNameDao extends EntityDao<Long, AttributeName> {
    AttributeName getAttributeNameByStringId(String id);
}
