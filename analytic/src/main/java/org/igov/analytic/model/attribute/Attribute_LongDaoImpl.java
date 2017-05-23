package org.igov.analytic.model.attribute;

import org.apache.log4j.Logger;
import org.igov.model.core.GenericEntityDao;

/**
 * Created by dpekach on 11.05.17.
 */

public class Attribute_LongDaoImpl extends GenericEntityDao<Long, Attribute_Long> implements Attribute_LongDao {

    private static final Logger log = Logger.getLogger(Attribute_LongDaoImpl.class);

    protected Attribute_LongDaoImpl() {
        super(Attribute_Long.class);
    }
}
