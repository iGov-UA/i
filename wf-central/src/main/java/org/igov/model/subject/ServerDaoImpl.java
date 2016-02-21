package org.igov.model.subject;

import org.springframework.stereotype.Repository;
import org.igov.model.core.GenericEntityDao;

/**
 * User: goodg_000
 * Date: 29.10.2015
 * Time: 21:33
 */
@Repository
public class ServerDaoImpl extends GenericEntityDao<Server> implements ServerDao {

    protected ServerDaoImpl() {
        super(Server.class);
    }
}
