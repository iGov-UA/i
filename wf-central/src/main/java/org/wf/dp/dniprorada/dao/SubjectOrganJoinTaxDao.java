package org.wf.dp.dniprorada.dao;

import org.wf.dp.dniprorada.base.dao.EntityDao;
import org.wf.dp.dniprorada.model.SubjectOrganJoinTax;

import java.util.List;

/**
 * @author NickVeremeichyk
 * @since 2015-11-24.
 */
public interface SubjectOrganJoinTaxDao extends EntityDao<SubjectOrganJoinTax> {
    List<SubjectOrganJoinTax> getAll(String sID_UA, String sName_UA);

    SubjectOrganJoinTax setSubjectOrganJoinTax(Long nID, String sID_UA, String sName_UA);

    void removeByKey(Long nID, String sID_UA);

    SubjectOrganJoinTax getByKey(Long nID, String sID_UA, String sName_UA);

}
