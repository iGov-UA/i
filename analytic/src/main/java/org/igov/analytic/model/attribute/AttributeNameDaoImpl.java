package org.igov.analytic.model.attribute;

import org.apache.log4j.Logger;
import org.igov.analytic.model.core.GenericEntityDaoAnalytic;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

/**
 * Created by dpekach on 18.05.17.
 */
@Transactional("transactionManagerAnalytic")
@Repository
public class AttributeNameDaoImpl extends GenericEntityDaoAnalytic<Long, AttributeName> implements AttributeNameDao {
    private static final Logger log = Logger.getLogger(AttributeNameDaoImpl.class);

    protected AttributeNameDaoImpl() {
        super(AttributeName.class);
    }

    @Override
    public AttributeName getAttributeNameByStringId(String id) {
        return (AttributeName) getSession().createSQLQuery("select * from \"AttributeName\" where \"sID\" = \'" + id +
        "\'").addEntity(AttributeName.class).uniqueResult();
    }
}
