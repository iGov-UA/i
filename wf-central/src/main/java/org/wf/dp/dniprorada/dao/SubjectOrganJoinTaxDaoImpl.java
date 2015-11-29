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
    public List<SubjectOrganJoinTax> getAll(String sIdUA, String sNameUA) {
        return null;
    }

    @Override
    public SubjectOrganJoinTax setSubjectOrganJoinTax(Long nId, String sIdUA, String sNameUA) {
        SubjectOrganJoinTax subjectOrganJoinTax = getByKey(nId, sIdUA, sNameUA);
        if (subjectOrganJoinTax == null) {
            if (nId == null) {
                subjectOrganJoinTax = new SubjectOrganJoinTax();
            } else {
                throw new EntityNotFoundException("Record not found!");
            }
        }
        if (sIdUA != null && subjectOrganJoinTax.getsIdUA() != sIdUA)
            subjectOrganJoinTax.setsIdUA(sIdUA);
        if (sNameUA != null && !sNameUA.equals(subjectOrganJoinTax.getsNameUA()))
            subjectOrganJoinTax.setsNameUA(sNameUA);

        subjectOrganJoinTax = saveOrUpdate(subjectOrganJoinTax);
        LOG.info("country " + subjectOrganJoinTax + "is updated");
        return subjectOrganJoinTax;
    }

    @Override
    public void removeByKey(Long nId, String sIdUa) {
        SubjectOrganJoinTax subjectOrganJoinTax = getByKey(nId, sIdUa, null);
        if (subjectOrganJoinTax == null) {
            throw new EntityNotFoundException("Record not found!");
        } else {
            delete(subjectOrganJoinTax);
            LOG.info("subjectOrganJoinTax " + subjectOrganJoinTax + "is deleted");
        }
    }

    @Override
    public SubjectOrganJoinTax getByKey(Long nID, String sIdUA, String sNameUA) {
        if (nID != null) {
            return findById(nID).orNull();
        } else if (sIdUA != null) {
            return findBy("sIdUA", sIdUA).orNull();
        } else if (sNameUA != null) {
            return findBy("sNameUA", sNameUA).orNull();
        } else
            throw new IllegalArgumentException("All args are null!");
    }
}
