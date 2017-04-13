package org.igov.model.object;

import org.igov.model.core.EntityDao;

import java.util.List;
import java.util.Map;

public interface ObjectCustomsDao extends EntityDao<Long, ObjectCustoms> {

    public List<ObjectCustoms> getObjectCustoms(Map<String, String> args);

    public ObjectCustoms setObjectCustoms(Map<String, String> args);

    public void removeObjectCustoms(Map<String, String> args) throws Exception;
}
