package org.igov.model.image;

import org.igov.model.core.GenericEntityDao;
import org.springframework.stereotype.Repository;

/**
 *
 * @author Kovylin
 */
@Repository
public class DocumentImageFileSignDaoImpl extends GenericEntityDao<Long, DocumentImageFileSign> implements DocumentImageFileSignDao {
    
    public DocumentImageFileSignDaoImpl() {
        super(DocumentImageFileSign.class);
    }
    
}
