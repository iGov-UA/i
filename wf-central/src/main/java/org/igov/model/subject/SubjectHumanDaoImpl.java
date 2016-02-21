package org.igov.model.subject;

import org.hibernate.Criteria;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.igov.model.core.GenericEntityDao;

import java.util.Arrays;

@Repository
public class SubjectHumanDaoImpl extends GenericEntityDao<SubjectHuman> implements SubjectHumanDao {

    @Autowired
    private SubjectDao subjectDao;

    @Autowired
    private SubjectContactTypeDao contactTypeDao;

    public SubjectHumanDaoImpl() {
        super(SubjectHuman.class);
    }

    @Override
    public SubjectHuman getSubjectHuman(String sINN) {
        return findBy("sINN", sINN).orNull();
    }

    public SubjectHuman getSubjectHuman(SubjectHumanIdType subjectHumanIdType, String sCode_Subject) {
        String subjectId = SubjectHuman.getSubjectId(subjectHumanIdType, sCode_Subject);
        Criteria criteria = createCriteria();
        criteria.createCriteria("oSubject").add(Restrictions.eq("sID", subjectId));
        return (SubjectHuman) criteria.uniqueResult();
    }

    @Override
    public SubjectHuman saveSubjectHuman(SubjectHumanIdType subjectHumanIdType, String sCode_Subject) {
        SubjectHuman oSubjectHuman = new SubjectHuman();
        oSubjectHuman.setSubjectHumanIdType(subjectHumanIdType);

        Subject subject = new Subject();
        String subjectId = SubjectHuman.getSubjectId(subjectHumanIdType, sCode_Subject);
        subject.setsID(subjectId);

        if (SubjectHumanIdType.INN == subjectHumanIdType) {
            oSubjectHuman.setsINN(sCode_Subject);
        }
        else if (Arrays.asList(SubjectHumanIdType.Phone, SubjectHumanIdType.Email).contains(subjectHumanIdType)) {
            SubjectContact subjectContact = new SubjectContact();
            subjectContact.setSubject(subject);
            subjectContact.setsValue(sCode_Subject);

            final boolean isPhone = SubjectHumanIdType.Phone == subjectHumanIdType;
            if (isPhone) {
                subjectContact.setSubjectContactType(contactTypeDao.getPhoneType());
                oSubjectHuman.setDefaultPhone(subjectContact);
            }
            else {
                subjectContact.setSubjectContactType(contactTypeDao.getEmailType());
                oSubjectHuman.setDefaultEmail(subjectContact);
            }

            subjectDao.saveOrUpdate(subject);
        }
        //else if (subjectHumanIdType.equals(SubjectHumanIdType.Passport)) {
        // TODO logic of setting fields  sPassportSeria, sPassportNumber
        //}

        oSubjectHuman.setoSubject(subject);

        saveOrUpdate(oSubjectHuman);
        return oSubjectHuman;
    }

    @Override
    public SubjectHuman saveSubjectHuman(String sINN) {
        return saveSubjectHuman(SubjectHumanIdType.INN, sINN);
    }

    @Override
    public SubjectHuman saveOrUpdateHuman(SubjectHuman oSubjectHuman) {
        String subjectId = SubjectHuman.getSubjectId(oSubjectHuman.getSubjectHumanIdType(),
                oSubjectHuman.getsINN());
        oSubjectHuman.getoSubject().setsID(subjectId);
        saveOrUpdate(oSubjectHuman);
        return oSubjectHuman;
    }

}
