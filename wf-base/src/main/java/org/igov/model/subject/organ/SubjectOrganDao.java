package org.igov.model.subject.organ;

import org.igov.model.core.EntityDao;
import org.igov.model.subject.Subject;

import java.util.List;

public interface SubjectOrganDao extends EntityDao<Long, SubjectOrgan> {

    public SubjectOrgan getSubjectOrgan(String sOKPO);

    public SubjectOrgan setSubjectOrgan(String sOKPO);

    public SubjectOrgan saveOrUpdateSubjectOrgan(SubjectOrgan subjectOrgan);

    public SubjectOrgan getSubjectOrgan(Long nID);

    public SubjectOrgan getSubjectOrgan(Subject subject);

    List<SubjectOrganJoin> findSubjectOrganJoinsBy(Long organID, Long regionID, Long cityID, String uaID);

    //SubjectOrganJoin findSubjectOrganJoin(Long nID);

    void add(SubjectOrganJoin subjectOrganJoin);

    void removeSubjectOrganJoin(Long organID, String[] publicIDs);
}
