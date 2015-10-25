package org.wf.dp.dniprorada.base.dao;


import org.wf.dp.dniprorada.base.model.FlowLink;

/**
 * Created by Богдан on 25.10.2015.
 */
public interface FlowLinkDao extends EntityDao<FlowLink> {

    /**
     * Gets flow slots by service data ID ordered by date in given interval
     *
     * @param nID_Service            ID service of Service Data.
     * @return service data slot
     */
    long findServiceDataByService(Long nID_Service) throws Exception;

}
