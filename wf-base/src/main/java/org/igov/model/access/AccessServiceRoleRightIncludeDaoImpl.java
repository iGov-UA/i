package org.igov.model.access;

import org.igov.model.core.GenericEntityDao;
import org.springframework.stereotype.Repository;

/**
 * User: goodg_000
 * Date: 08.03.2016
 * Time: 15:42
 */
@Repository
public class AccessServiceRoleRightIncludeDaoImpl extends GenericEntityDao<AccessServiceRoleRightInclude>
        implements AccessServiceRoleRightIncludeDao {

    protected AccessServiceRoleRightIncludeDaoImpl() {
        super(AccessServiceRoleRightInclude.class);
    }
}
