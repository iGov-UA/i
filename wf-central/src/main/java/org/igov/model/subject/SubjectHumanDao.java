package org.igov.model.subject;

import org.igov.model.core.EntityDao;

public interface SubjectHumanDao extends EntityDao<SubjectHuman> {

    SubjectHuman getSubjectHuman(String sINN);

    SubjectHuman saveSubjectHuman(String sINN);

    SubjectHuman getSubjectHuman(SubjectHumanIdType subjectHumanIdType, String sCode_Subject);

    SubjectHuman saveSubjectHuman(SubjectHumanIdType subjectHumanIdType, String sCode_Subject);

    SubjectHuman saveOrUpdateHuman(SubjectHuman subject);

}
