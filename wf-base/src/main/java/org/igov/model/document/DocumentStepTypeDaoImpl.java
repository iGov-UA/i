/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.igov.model.document;

import org.igov.model.core.GenericEntityDao;
import org.springframework.stereotype.Repository;

/**
 *
 * @author olga
 */
@Repository
public class DocumentStepTypeDaoImpl extends GenericEntityDao<Long, DocumentStepType> implements DocumentStepTypeDao  {
    
    public DocumentStepTypeDaoImpl() {
        super(DocumentStepType.class);
    }
}
