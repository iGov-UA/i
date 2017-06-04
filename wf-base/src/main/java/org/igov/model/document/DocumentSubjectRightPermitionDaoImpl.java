package org.igov.model.document;

import org.igov.model.core.GenericEntityDao;
import org.springframework.stereotype.Repository;

/**
 *
 * @author Kovilin
 */
@Repository
public class DocumentSubjectRightPermitionDaoImpl extends GenericEntityDao<Long, DocumentSubjectRightPermition> implements DocumentSubjectRightPermitionDao{
    
    public DocumentSubjectRightPermitionDaoImpl(){
        super(DocumentSubjectRightPermition.class); 
    }
    
}
