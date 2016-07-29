package org.igov.model.object.place;

import java.util.List;
import org.igov.model.core.EntityDao;

public interface ObjectPlace_UADao extends EntityDao<Long, ObjectPlace_UA>{

    List<ObjectPlace_UA> getObjectPlace_UA(String sID, String sName_UA);
    List<ObjectPlace_UA> getObjectPlace_UA(String sFind);

}
