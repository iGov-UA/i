package org.igov.model.object;

import org.igov.model.core.EntityDao;

import java.util.List;

/**
 * @author grigoriy-romanenko
 */
public interface ObjectEarthTargetDao extends EntityDao<Long, ObjectEarthTarget> {

    List<ObjectEarthTarget> getObjectEarthTargets(String sID_UA, String sName_UA);

}
