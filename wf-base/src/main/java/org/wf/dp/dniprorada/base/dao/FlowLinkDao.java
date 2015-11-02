package org.wf.dp.dniprorada.base.dao;


import org.wf.dp.dniprorada.base.model.FlowLink;
import org.wf.dp.dniprorada.base.model.Flow_ServiceData;

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
    FlowLink findLinkByService(Long nID_Service);

}
