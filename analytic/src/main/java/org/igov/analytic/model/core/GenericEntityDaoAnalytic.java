package org.igov.analytic.model.core;

import static org.hibernate.criterion.Restrictions.eq;

import java.io.Serializable;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Criterion;
import org.igov.service.exception.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.Assert;

import java.util.Collection;

import com.google.common.base.Optional;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import org.igov.model.core.Entity;
import org.igov.model.core.EntityDao;
import static org.hibernate.criterion.Restrictions.in;

/**
 * Base implementation of CRUD operations.
 *
 * @param <P> type of entity primary key
 * @param <T> entities type
 * @see EntityDao
 */
public class GenericEntityDaoAnalytic<P extends Serializable, T extends Entity<P>> extends org.igov.model.core.GenericEntityDao{
    private static final Log LOG = LogFactory.getLog(GenericEntityDaoAnalytic.class);

    private final static int DEFAULT_DELETE_BATCH_SIZE = 1000;

    private Class<T> entityClass;
    private SessionFactory sessionFactory;

    
    public GenericEntityDaoAnalytic(Class<T> entityClass){
        super(entityClass);
        this.entityClass = entityClass;
    }

    @Autowired
    public void setSessionFactory(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    protected Session getSession() {
        return sessionFactory.getCurrentSession();
    }
}
