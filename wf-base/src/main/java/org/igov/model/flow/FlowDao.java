package org.igov.model.flow;

import org.igov.model.core.EntityDao;

/**
 * User: goodg_000
 * Date: 21.06.2015
 * Time: 15:44
 */
public interface FlowDao extends EntityDao<Long, Flow> {

    /**
     * Gets id of entity Flow_ServiceData with given sID_BP and nID_SubjectOrganDepartment
     *
     * @param sID_BP                     id of business process
     * @param nID_SubjectOrganDepartment id department
     */
    Long findFlowId(String sID_BP, Long nID_SubjectOrganDepartment);

}
