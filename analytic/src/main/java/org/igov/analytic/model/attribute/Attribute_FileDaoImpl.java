/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.igov.analytic.model.attribute;

import org.igov.analytic.model.process.*;
import org.apache.log4j.Logger;
import org.igov.analytic.model.core.GenericEntityDaoAnalytic;
import org.springframework.stereotype.Repository;

/**
 *
 * @author olga
 */
//@Transactional("transactionManagerAnalytic")
@Repository()
public class Attribute_FileDaoImpl extends GenericEntityDaoAnalytic<Long, Attribute_File> implements Attribute_FileDao {

    private static final Logger log = Logger.getLogger(Attribute_FileDaoImpl.class);

    protected Attribute_FileDaoImpl() {
        super(Attribute_File.class);
    }

}
