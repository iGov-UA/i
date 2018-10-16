package org.igov.service.business.subject;

import org.igov.model.object.place.*;
import org.igov.model.subject.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
public class SubjectContactPlacePointService {

    @Autowired
    private SubjectContactPlacePointDao subjectContactPlacePointDao;

    @Autowired
    private SubjectDao subjectDao;

    @Autowired
    private PlaceDao placeDao;

    @Autowired
    private PlaceBranchDao placeBranchDao;

    @Autowired
    private PlaceBuildDao placeBuildDao;

    @Autowired
    private PlaceBuildPartDao placeBuildPartDao;

    @Autowired
    private PlaceBuildPartCellDao placeBuildPartCellDao;

    @Autowired
    private SubjectContactPlacePointTypeDao subjectContactPlacePointTypeDao;

    @Autowired
    private PlaceTypeDao oPlaceTypeDao;

    public SubjectContactPlacePoint getSubjectContactPlacePoint(Long nID) {
        SubjectContactPlacePoint subjectContactPlacePoint = subjectContactPlacePointDao.findById(nID).orNull();

        if (subjectContactPlacePoint == null) {
            throw new RuntimeException("SubjectContactPlacePoint with nID " + nID + " not found");
        }
        String sResult = subjectContactPlacePoint.getSubjectContactPlacePointType().getSName_UA() + ": " +
                subjectContactPlacePoint.getoPlaceBuild().getSKey_MailIndex() + " " +
                subjectContactPlacePoint.getoPlace_Region().getOriginalName() + " " +
                getsPlaceTypeName(subjectContactPlacePoint.getoPlace_Region()) + ", " +
                getsPlaceTypeName(subjectContactPlacePoint.getoPlace()) + " " +
                subjectContactPlacePoint.getoPlace().getOriginalName() + ", " +
                subjectContactPlacePoint.getoPlaceBranch().getOPlaceBranchType().getSName() + " " +
                subjectContactPlacePoint.getoPlaceBranch().getSName() + ", " +
                subjectContactPlacePoint.getoPlaceBuildPartCell().getOPlaceBuildPartCellType().getSName() + " " +
                subjectContactPlacePoint.getoPlaceBuildPartCell().getSKey();
        subjectContactPlacePoint.setsValue(sResult);
        return subjectContactPlacePoint;
    }

    private String getsPlaceTypeName(Place oPlace) {
        PlaceType placeType = oPlaceTypeDao.findByIdExpected(oPlace.getPlaceTypeId());
        return placeType.getName();
    }

    public void removeSubjectContactPlacePoint(Long nID) {
        SubjectContactPlacePoint subjectContactPlacePoint = subjectContactPlacePointDao.findById(nID).orNull();
        if (subjectContactPlacePoint != null) {
            subjectContactPlacePointDao.delete(subjectContactPlacePoint);
        } else {
            throw new RuntimeException("SubjectContactPlacePoint with nID " + nID + " not found");
        }
    }

    public SubjectContactPlacePoint setSubjectContactPlacePoint(Long nID_Subject, Long nID_SubjectContactPlacePointType,
                                                                Long nID_Place, Long nID_Place_Region, Long nID_PlaceBranch, Long nID_PlaceBuild,
                                                                Long nID_PlaceBuildPart, Long nID_PlaceBuildPartCell) {

        Subject oSubject = subjectDao.getSubject(nID_Subject);
        SubjectContactPlacePointType oSubjectContactPlacePointType = subjectContactPlacePointTypeDao.findByIdExpected(nID_SubjectContactPlacePointType);
        Place oPlace = placeDao.findByIdExpected(nID_Place);
        Place oPlace_Region = placeDao.findByIdExpected(nID_Place_Region);
        PlaceBranch oPlaceBranch = placeBranchDao.findByIdExpected(nID_PlaceBranch);
        PlaceBuild oPlaceBuild = placeBuildDao.findByIdExpected(nID_PlaceBuild);
        PlaceBuildPart oPlaceBuildPart = placeBuildPartDao.findByIdExpected(nID_PlaceBuildPart);
        PlaceBuildPartCell oPlaceBuildPartCell = placeBuildPartCellDao.findByIdExpected(nID_PlaceBuildPartCell);

        SubjectContactPlacePoint oSubjectContactPlacePoint = new SubjectContactPlacePoint();

        oSubjectContactPlacePoint.setoPlace(oPlace);
        oSubjectContactPlacePoint.setoPlace_Region(oPlace_Region);
        oSubjectContactPlacePoint.setoPlaceBranch(oPlaceBranch);
        oSubjectContactPlacePoint.setoPlaceBuild(oPlaceBuild);
        oSubjectContactPlacePoint.setoPlaceBuildPart(oPlaceBuildPart);
        oSubjectContactPlacePoint.setoPlaceBuildPartCell(oPlaceBuildPartCell);

        oSubjectContactPlacePoint.setoSubject(oSubject);
        oSubjectContactPlacePoint.setSubjectContactPlacePointType(oSubjectContactPlacePointType);
        return subjectContactPlacePointDao.saveOrUpdate(oSubjectContactPlacePoint);
    }

    public List<SubjectContactPlacePoint> findSubjectContactPlacePoint(Long nID_Subject) {
        return subjectContactPlacePointDao.findBySubject(nID_Subject);
    }
}

