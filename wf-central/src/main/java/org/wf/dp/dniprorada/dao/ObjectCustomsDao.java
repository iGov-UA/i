package org.wf.dp.dniprorada.dao;
import org.wf.dp.dniprorada.base.dao.EntityDao;
import org.wf.dp.dniprorada.model.ObjectCustoms;

import java.util.List;
import java.util.Map;

public interface ObjectCustomsDao extends EntityDao<ObjectCustoms>
{
    public List<ObjectCustoms> getObjectCustoms(Map<String, String> args);
    public ObjectCustoms setObjectCustoms(Map<String, String> args);
    public void removeObjectCustoms(Map<String, String> args) throws Exception;
}
