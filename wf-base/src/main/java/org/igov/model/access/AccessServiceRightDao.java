package org.igov.model.access;

import org.igov.model.access.vo.AccessRightVO;
import org.igov.model.core.EntityDao;

import java.util.List;

/**
 * User: goodg_000
 * Date: 28.02.2016
 * Time: 18:49
 */
public interface AccessServiceRightDao extends EntityDao<AccessServiceRight> {

    List<AccessServiceRight> getAccessServiceRights(Long nID, String sService, String saMethod, String sHandlerBean);
}
