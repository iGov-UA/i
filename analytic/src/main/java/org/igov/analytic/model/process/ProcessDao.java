/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.igov.analytic.model.process;

import org.igov.model.core.EntityDao;

/**
 * @author olga
 */
public interface ProcessDao extends EntityDao<Long, Process> {
    void saveWithConfigBackup(Process process);
}
