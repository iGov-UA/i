package org.igov.model.access;

import org.igov.model.core.EntityDao;

import java.util.List;

/**
 * User: goodg_000
 * Date: 28.02.2016
 * Time: 18:49
 */
public interface AccessServiceLoginRoleDao extends EntityDao<AccessServiceLoginRole> {

    List<AccessServiceLoginRole> getUserRoles(String sLogin);

    AccessServiceLoginRole findLoginRole(String sLogin, Long nID_AccessServiceRole);
}
