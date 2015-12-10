package org.wf.dp.dniprorada.dao;

import org.wf.dp.dniprorada.base.dao.EntityDao;
import org.wf.dp.dniprorada.model.ObjectEarthTarget;

import java.util.List;

/**
 * @author grigoriy-romanenko
 */
public interface ObjectEarthTargetDao extends EntityDao<ObjectEarthTarget> {

    List<ObjectEarthTarget> getObjectEarthTargets(String sID_UA, String sName_UA);

}
