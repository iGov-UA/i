package org.wf.dp.dniprorada.dao;

import org.wf.dp.dniprorada.base.dao.EntityDao;
import org.wf.dp.dniprorada.model.SubjectHuman;
import org.wf.dp.dniprorada.model.SubjectHumanIdType;

public interface SubjectHumanDao extends EntityDao<SubjectHuman> {

    SubjectHuman getSubjectHuman(String sINN);

    SubjectHuman saveSubjectHuman(String sINN);

    SubjectHuman getSubjectHuman(SubjectHumanIdType subjectHumanIdType, String sCode_Subject);

    SubjectHuman saveSubjectHuman(SubjectHumanIdType subjectHumanIdType, String sCode_Subject);

    SubjectHuman saveOrUpdateHuman(SubjectHuman subject);

}
