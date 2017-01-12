package org.igov.model.subject;

import org.igov.model.core.EntityDao;

public interface SubjectHumanDao extends EntityDao<Long, SubjectHuman> {

    SubjectHuman getSubjectHuman(String sINN);

    SubjectHuman getSubjectHuman(Subject subject);

    SubjectHuman saveSubjectHuman(String sINN);

    SubjectHuman getSubjectHuman(SubjectHumanIdType subjectHumanIdType, String sCode_Subject);

    SubjectHuman saveSubjectHuman(SubjectHumanIdType subjectHumanIdType, String sCode_Subject);

    SubjectHuman saveOrUpdateHuman(SubjectHuman subject);

}
