package org.wf.dp.dniprorada.dao;

import org.wf.dp.dniprorada.base.dao.EntityDao;
import org.wf.dp.dniprorada.model.SubjectOrganJoinTax;

import java.util.List;

/**
 * @author NickVeremeichyk
 * @since 2015-11-24.
 */
public interface SubjectOrganJoinTaxDao extends EntityDao<SubjectOrganJoinTax> {
    List<SubjectOrganJoinTax> getAll(String sIdUA, String sNameUA);

    SubjectOrganJoinTax setSubjectOrganJoinTax(Long nId, String sIdUA, String sNameUA);

    void removeByKey(Long nId, String sIdUA);

    SubjectOrganJoinTax getByKey(Long nId, String sIdUA, String sNameUA);

}
