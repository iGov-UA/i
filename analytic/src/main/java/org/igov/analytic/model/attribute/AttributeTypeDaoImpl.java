/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.igov.analytic.model.attribute;

import org.igov.model.core.GenericEntityDao;

/**
 *
 * @author olga
 */
public class AttributeTypeDaoImpl extends GenericEntityDao<Long, AttributeType> implements AttributeTypeDao {
    
    public AttributeTypeDaoImpl() {
        super(AttributeType.class);
    }
    
}
