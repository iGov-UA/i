/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.igov.analytic.model.source;

import org.apache.log4j.Logger;
import org.igov.analytic.model.core.GenericEntityDaoAnalytic;
import org.springframework.stereotype.Repository;

/**
 *
 * @author olga
 */
//@Transactional("transactionManagerAnalytic")
@Repository()
public class SourceDBDaoImpl extends GenericEntityDaoAnalytic<Long, SourceDB> implements SourceDBDao {

    private static final Logger log = Logger.getLogger(SourceDBDaoImpl.class);

    protected SourceDBDaoImpl() {
        super(SourceDB.class);
    }

}
