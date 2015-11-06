package org.wf.dp.dniprorada.dao;

import org.springframework.stereotype.Repository;
import org.wf.dp.dniprorada.base.dao.GenericEntityDao;
import org.wf.dp.dniprorada.model.Server;

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
