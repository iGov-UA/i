package org.igov.analytic.model.core;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.io.Serializable;
import org.springframework.beans.factory.annotation.Qualifier;


@Repository
public class BaseEntityDao<P extends Serializable> 
extends org.igov.model.core.BaseEntityDao{

    private SessionFactory sessionFactory;

    private Session getSession() {
        return sessionFactory.getCurrentSession();
    }

    @Autowired
    @Qualifier("sessionFactoryAnalytic")
    public void setSessionFactory(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }
}
