/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.igov.model.action.task.core.entity;

import org.igov.model.core.Entity;
import org.igov.model.core.EntityDao;

/**
 *
 * @author olga
 */
public interface ProcessHistoryDao extends EntityDao<Long, ProcessHistory>{
    
    public void removeOldProcess(String sID_Process_Def, String sDateFinishAt, String sDateFinishTo);
    
}
