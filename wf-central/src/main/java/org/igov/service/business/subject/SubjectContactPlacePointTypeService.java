package org.igov.service.business.subject;


import org.igov.model.subject.SubjectContactPlacePointType;
import org.igov.model.subject.SubjectContactPlacePointTypeDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class SubjectContactPlacePointTypeService {

    @Autowired
    private SubjectContactPlacePointTypeDao oSubjectContactPlacePointTypeDao;

    public SubjectContactPlacePointType getSubjectContactPlacePointType(Long nID) {
        SubjectContactPlacePointType oSubjectContactPlacePointType = oSubjectContactPlacePointTypeDao.findById(nID).orNull();
        if (oSubjectContactPlacePointType == null) {
            throw new RuntimeException("SubjectContactPlacePointType with nID " + nID + " not found");
        }
        return oSubjectContactPlacePointType;
    }

    public void removeSubjectContactPlacePointType(Long nID) {
        SubjectContactPlacePointType oSubjectContactPlacePointType = oSubjectContactPlacePointTypeDao.findByIdExpected(nID);
        if (oSubjectContactPlacePointType != null) {
            oSubjectContactPlacePointTypeDao.delete(oSubjectContactPlacePointType);
        } else {
            throw new RuntimeException("SubjectContactPlacePointType with nID " + nID + " not found");
        }
    }

    public SubjectContactPlacePointType setSubjectContactPlacePointType(String sName_UA, String sName_EN, String sName_RU) {

        boolean bExists = oSubjectContactPlacePointTypeDao.findBy("sName_EN", sName_EN).isPresent();
        if (bExists) {
            throw new RuntimeException("SubjectContactPlacePointType with sName_EN = '" + sName_EN + "' already exists");
        }
        SubjectContactPlacePointType subjectContactPlacePointType = new SubjectContactPlacePointType();

        subjectContactPlacePointType.setSName_EN(sName_EN);
        subjectContactPlacePointType.setSName_RU(sName_RU);
        subjectContactPlacePointType.setSName_UA(sName_UA);
        return oSubjectContactPlacePointTypeDao.saveOrUpdate(subjectContactPlacePointType);
    }
}
