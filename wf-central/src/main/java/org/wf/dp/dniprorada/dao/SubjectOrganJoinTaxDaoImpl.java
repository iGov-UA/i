package org.wf.dp.dniprorada.dao;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Repository;
import org.wf.dp.dniprorada.base.dao.EntityNotFoundException;
import org.wf.dp.dniprorada.base.dao.GenericEntityDao;
import org.wf.dp.dniprorada.model.SubjectOrganJoinTax;

import java.util.List;

/**
 * @author NickVeremeichyk
 * @since 2015-11-24.
 */
@Repository
public class SubjectOrganJoinTaxDaoImpl extends GenericEntityDao<SubjectOrganJoinTax> implements SubjectOrganJoinTaxDao {
    private static final Logger LOG = Logger.getLogger(SubjectOrganJoinTaxDaoImpl.class);

    protected SubjectOrganJoinTaxDaoImpl() {
        super(SubjectOrganJoinTax.class);
    }

    @Override
    public List<SubjectOrganJoinTax> getAll(String sID_UA, String sName_UA) {
        return null;
    }

//    @Override
//    public SubjectOrganJoinTax getSubjectOrganJoinTax(Long nID, String sID_UA, String sName_UA) {
//        SubjectOrganJoinTax subjectOrganJoinTax = getByKey(nID, sID_UA, sName_UA);
//        if (subjectOrganJoinTax == null) {
//            throw new EntityNotFoundException("Record not found!");
//        } else {
//            return subjectOrganJoinTax;
//        }
//    }

    @Override
    public SubjectOrganJoinTax setSubjectOrganJoinTax(Long nID, String sID_UA, String sName_UA) {
        SubjectOrganJoinTax subjectOrganJoinTax = getByKey(nID, sID_UA, sName_UA);
        if (subjectOrganJoinTax == null) {
            if (nID == null) {
                subjectOrganJoinTax = new SubjectOrganJoinTax();
            } else {
                throw new EntityNotFoundException("Record not found!");
            }
        }
        if (sID_UA != null && subjectOrganJoinTax.getsID_UA() != sID_UA)
            subjectOrganJoinTax.setsID_UA(sID_UA);
        if (sName_UA != null && !sName_UA.equals(subjectOrganJoinTax.getsName_UA()))
            subjectOrganJoinTax.setsName_UA(sName_UA);

        subjectOrganJoinTax = saveOrUpdate(subjectOrganJoinTax);
        LOG.info("country " + subjectOrganJoinTax + "is updated");
        return subjectOrganJoinTax;
    }

    @Override
    public void removeByKey(Long nID, String sID_UA) {
        SubjectOrganJoinTax subjectOrganJoinTax = getByKey(nID, sID_UA, null);
        if (subjectOrganJoinTax == null) {
            throw new EntityNotFoundException("Record not found!");
        } else {
            delete(subjectOrganJoinTax);
            LOG.info("subjectOrganJoinTax " + subjectOrganJoinTax + "is deleted");
        }
    }

    @Override
    public SubjectOrganJoinTax getByKey(Long nID, String sID_UA, String sName_UA) {
        if (nID != null) {
            return findById(nID).orNull();
        } else if (sID_UA != null) {
            return findBy("nID_UA", sID_UA).orNull();
        } else if (sName_UA != null) {
            return findBy("sName_UA", sName_UA).orNull();
        } else
            throw new IllegalArgumentException("All args are null!");
    }
}
