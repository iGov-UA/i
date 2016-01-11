package org.igov.model.subject.organ;

import org.igov.model.core.EntityDao;

import java.util.List;

/**
 * @author NickVeremeichyk
 * @since 2015-11-24.
 */
public interface SubjectOrganJoinTaxDao extends EntityDao<SubjectOrganJoinTax> {
    List<SubjectOrganJoinTax> getAll(String sIdUA, String sNameUA);

    SubjectOrganJoinTax setSubjectOrganJoinTax(Long nId, Integer nIdSubjectOrganJoin, String sIdUA, String sNameUA);

    void removeByKey(Long nId, String sIdUA);

    SubjectOrganJoinTax getByKey(Long nId, Integer nIdSubjectOrganJoin, String sIdUA, String sNameUA);

}
