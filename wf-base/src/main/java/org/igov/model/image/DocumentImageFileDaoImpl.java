package org.igov.model.image;

import org.igov.model.core.GenericEntityDao;
import org.springframework.stereotype.Repository;

/**
 *
 * @author Kovylin
 */
@Repository
public class DocumentImageFileDaoImpl extends GenericEntityDao<Long, DocumentImageFile> implements DocumentImageFileDao {
    
    public DocumentImageFileDaoImpl() {
        super(DocumentImageFile.class);
    }
}
