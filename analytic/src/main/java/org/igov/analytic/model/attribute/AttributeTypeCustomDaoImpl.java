package org.igov.analytic.model.attribute;

import org.apache.log4j.Logger;
import org.igov.analytic.model.core.GenericEntityDaoAnalytic;
import org.springframework.stereotype.Repository;

/**
 * Created by dpekach on 19.05.17.
 */
@Repository
public class AttributeTypeCustomDaoImpl extends GenericEntityDaoAnalytic<Long, AttributeTypeCustom> implements AttributeTypeCustomDao {

    private static final Logger log = Logger.getLogger(AttributeTypeCustomDaoImpl.class);

    protected AttributeTypeCustomDaoImpl() {
        super(AttributeTypeCustom.class);
    }
}
