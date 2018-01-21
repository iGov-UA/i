package org.igov.model.action.item;

import org.igov.model.core.GenericEntityDao;
import org.springframework.stereotype.Repository;

/**
 *
 * @author Kovylin
 */
@Repository
public class ServiceDaoImpl extends GenericEntityDao<Long, Service> implements ServiceDao{
    public ServiceDaoImpl() {
         super(Service.class);
    }
}
