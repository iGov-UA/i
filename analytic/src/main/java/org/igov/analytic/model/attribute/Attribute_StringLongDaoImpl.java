/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.igov.analytic.model.attribute;

import org.apache.log4j.Logger;
import org.igov.analytic.model.core.GenericEntityDaoAnalytic;
import org.springframework.stereotype.Repository;

/**
 *
 * @author olga
 */
//@Transactional("transactionManagerAnalytic")
@Repository()
public class Attribute_StringLongDaoImpl extends GenericEntityDaoAnalytic<Long, Attribute_StringLong> implements Attribute_StringLongDao {

    private static final Logger log = Logger.getLogger(Attribute_StringLongDaoImpl.class);

    protected Attribute_StringLongDaoImpl() {
        super(Attribute_StringLong.class);
    }

}
