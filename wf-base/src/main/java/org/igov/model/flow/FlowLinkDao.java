package org.igov.model.flow;

import org.igov.model.core.EntityDao;


/**
 * Created by Богдан on 25.10.2015.
 */
public interface FlowLinkDao extends EntityDao<FlowLink> {

    /**
     * Gets flow link by service ID
     *
     * @param nID_Service            ID service of Service
     * @return flow link
     */
    FlowLink findLinkByService(Long nID_Service, Long nID_SubjectOrganDepartment);

}
