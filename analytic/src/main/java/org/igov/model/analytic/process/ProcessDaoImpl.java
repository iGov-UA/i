/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.igov.model.analytic.process;

import org.igov.model.core.GenericEntityDao;
import org.springframework.stereotype.Repository;

/**
 *
 * @author olga
 */
@Repository
public class ProcessDaoImpl extends GenericEntityDao<Long, Process> implements ProcessDao {
    
    public ProcessDaoImpl() {
        super(Process.class);
    }
    
}
