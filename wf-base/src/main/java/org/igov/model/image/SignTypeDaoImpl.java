package org.igov.model.image;

import org.igov.model.core.GenericEntityDao;
import org.springframework.stereotype.Repository;

/**
 *
 * @author Kovylin
 */
@Repository
public class SignTypeDaoImpl extends GenericEntityDao<Long, SignType> implements SignTypeDao{
    
    public SignTypeDaoImpl() {
        super(SignType.class);
    }
    
}
