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
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.transaction.annotation.Transactional;

/**
 * Base implementation of CRUD operations.
 *
 * @param <P> type of entity primary key
 * @param <T> entities type
 * @see EntityDao
 */
public class GenericEntityDaoAnalytic<P extends Serializable, T extends Entity<P>> implements EntityDao<P, T>{
    private static final Log LOG = LogFactory.getLog(GenericEntityDaoAnalytic.class);

    private final static int DEFAULT_DELETE_BATCH_SIZE = 1000;

    private Class<T> entityClass;
    
    private SessionFactory sessionFactory;

    
    public GenericEntityDaoAnalytic(Class<T> entityClass){
       // super(entityClass);
        this.entityClass = entityClass;
    }

    @Autowired
    @Qualifier("sessionFactoryAnalytic")
    public void setSessionFactory(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    protected Session getSession() {
        return sessionFactory.getCurrentSession();
    }

    @Override
    public Class<T> getEntityClass() {
        return entityClass;
    }

    protected Criteria createCriteria() {
        return getSession().createCriteria(entityClass);
    }

    protected String getEntityName() {
        return entityClass.getSimpleName();
    }

    @Transactional("transactionManagerAnalytic")
    @SuppressWarnings("unchecked")
    @Override
    public Optional<T> findById(P id) {
        Assert.notNull(id);

        T found = (T) getSession().get(entityClass, id);
        return found == null ? Optional.<T>absent() : Optional.of(found);
    }

    @Transactional("transactionManagerAnalytic")
    @Override
    public T findByIdExpected(P id) {
        Optional<T> entity = findById(id);

        if (!entity.isPresent()) {
            throw new EntityNotFoundException(id);
        }

        return entity.get();
    }

    @Transactional("transactionManagerAnalytic")
    @SuppressWarnings("unchecked")
    @Override
    public Optional<T> findBy(String field, Object value) {
        List<T> allBy = findAllByAttributeCriteria(field, value)
                .setFirstResult(0)
                .list();
        T foundEntity = Iterables.getFirst(allBy, null);
        return foundEntity == null ? Optional.<T>absent() : Optional.of(foundEntity);
    }

    @Transactional("transactionManagerAnalytic")
    @Override
    public T findByExpected(String attribute, Object value) {
        Optional<T> foundEntity = findBy(attribute, value);
        if (!foundEntity.isPresent()) {
            throw new EntityNotFoundException(String.format("Entity with %s='%s' not found", attribute, value));
        }
        return foundEntity.get();
    }

    @Transactional("transactionManagerAnalytic")
    @SuppressWarnings("unchecked")
    @Override
    public List<T> findAllBy(String field, Object value) {
        return findAllByAttributeCriteria(field, value).list();
    }

    @Transactional("transactionManagerAnalytic")
    @SuppressWarnings("unchecked")
    @Override
    public List<T> findAllByInValues(String field, List<?> value) {
        return findAllByAttributeCriteria(field, value).list();
    }

    protected Criteria findAllByAttributeCriteria(String field, Object value) {
        Assert.hasText(field, "Specify field name");
        Assert.notNull(value, "Specify value");

        Criteria criteria = createCriteria();
        String fieldName = field;

        if (StringUtils.contains(field, ".")) {
            String propertyPath = StringUtils.substringBeforeLast(field, ".");
            fieldName = StringUtils.substringAfterLast(field, ".");

            criteria = criteria.createCriteria(propertyPath);
        }

        Criterion criterion = value instanceof Collection ? in(fieldName, (Collection) value) : eq(fieldName, value);

        return criteria.add(criterion);
    }

    @Transactional("transactionManagerAnalytic")
    @SuppressWarnings("unchecked")
    @Override
    public List<T> findAll() {
        return createCriteria().list();
    }

    @Transactional("transactionManagerAnalytic")
    @SuppressWarnings("unchecked")
    @Override
    public List<T> findAll(List<P> ids) {
        Assert.notEmpty(ids);

        return createCriteria()
                .add(in("id", ids))
                .list();
    }

    @Transactional("transactionManagerAnalytic")
    @SuppressWarnings("unchecked")
    @Override
    public T saveOrUpdate(T entity) {
        Assert.notNull(entity);

        getSession().saveOrUpdate(entity);
        return entity;
    }

    @Transactional("transactionManagerAnalytic")
    @Override
    public List<T> saveOrUpdate(List<T> entities) {
        for (T entity : entities) {
            saveOrUpdate(entity);
        }
        return entities;
    }

    @Transactional("transactionManagerAnalytic")
    @Override
    public void delete(P id) {
        T entity = findByIdExpected(id);
        delete(entity);
    }

    /**
     * Note: this method actually executes 2 sql requests: select and delete.
     *
     * @param field
     * @param value
     * @return
     */
    @Transactional("transactionManagerAnalytic")
    @Override
    public int deleteBy(String field, Object value) {
        Assert.hasText(field);
        Assert.notNull(value);

        List<T> toDelete = findAllBy(field, value);

        List<T> notDeleted = delete(toDelete);
        return toDelete.size() - notDeleted.size();
    }

    @Transactional("transactionManagerAnalytic")
    @SuppressWarnings("unchecked")
    @Override
    public void delete(T entity) {
        Assert.notNull(entity);

        if (!exists(entity.getId())) {
            throw new EntityNotFoundException(entity.getId());
        }
        T merged = (T) getSession().merge(entity);
        getSession().delete(merged);
    }

    @Transactional("transactionManagerAnalytic")
    @Override
    public List<T> delete(List<T> entities) {
        List<T> notDeletedEntities = Lists.newArrayList();

        int i = 0;
        for (T entity : entities) {
            if (!exists(entity.getId())) {
                LOG.debug(String.format("Entity %s with id=%s does not exist.", entityClass.getName(), entity.getId()));
                LOG.debug(String.format("Add entity %s with id=%s to not deleted.", entityClass.getName(),
                        entity.getId()));
                notDeletedEntities.add(entity);
            } else {
                LOG.debug(String.format("Delete entity %s with id=%s", entityClass.getName(), entity.getId()));
                delete(entity);
            }
            i++;

            if (i >= DEFAULT_DELETE_BATCH_SIZE) {
                getSession().flush();
                i = 0;
            }
        }
        return notDeletedEntities;
    }

    @Transactional("transactionManagerAnalytic")
    @Override
    public void deleteAll() {
        getSession().createQuery(String.format("delete from %s", getEntityName()))
                .executeUpdate();
    }

    @Transactional("transactionManagerAnalytic")
    @Override
    public boolean exists(P id) {
        return findById(id).isPresent();
    }
}
