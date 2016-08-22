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
public class Attribute_DateDaoImp extends GenericEntityDaoAnalytic<Long, Attribute_Date> implements Attribute_DateDao {

    private static final Logger log = Logger.getLogger(Attribute_DateDaoImp.class);

    protected Attribute_DateDaoImp() {
        super(Attribute_Date.class);
    }

}
