package org.wf.dp.dniprorada.dao;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Repository;
import org.wf.dp.dniprorada.base.dao.EntityNotFoundException;
import org.wf.dp.dniprorada.base.dao.GenericEntityDao;
import org.wf.dp.dniprorada.model.TaxOrgan;

import java.util.List;

/**
 * @author NickVeremeichyk
 * @since 2015-11-24.
 */
@Repository
public class TaxOrganDaoImpl extends GenericEntityDao<TaxOrgan> implements TaxOrganDao {
    private static final Logger LOG = Logger.getLogger(TaxOrganDaoImpl.class);

    protected TaxOrganDaoImpl() {
        super(TaxOrgan.class);
    }

    @Override
    public List<TaxOrgan> getTaxOrgans(String sID_UA, String sName_UA) {
        return null;
    }

    @Override
    public TaxOrgan getTaxOrgan(Long nID, String sID_UA, String sName_UA) {
        TaxOrgan taxOrgan = getByKey(nID, sID_UA, sName_UA);
        if (taxOrgan == null) {
            throw new EntityNotFoundException("Record not found!");
        } else {
            return taxOrgan;
        }
    }

    @Override
    public TaxOrgan setTaxOrgan(Long nID, String sID_UA, String sName_UA) {
        TaxOrgan taxOrgan = getByKey(nID, sID_UA, sName_UA);
        if (taxOrgan == null) {
            if (nID == null) {
                taxOrgan = new TaxOrgan();
            } else {
                throw new EntityNotFoundException("Record not found!");
            }
        }
        if (sID_UA != null && taxOrgan.getsID_UA() != sID_UA)
            taxOrgan.setsID_UA(sID_UA);
        if (sName_UA != null && !sName_UA.equals(taxOrgan.getsName_UA()))
            taxOrgan.setsName_UA(sName_UA);

        taxOrgan = saveOrUpdate(taxOrgan);
        LOG.info("country " + taxOrgan + "is updated");
        return taxOrgan;
    }

    @Override
    public void removeTaxOrgan(Long nID, String sID_UA) {
        TaxOrgan taxOrgan = getByKey(nID, sID_UA, null);
        if (taxOrgan == null) {
            throw new EntityNotFoundException("Record not found!");
        } else {
            delete(taxOrgan);
            LOG.info("taxOrgan " + taxOrgan + "is deleted");
        }
    }

    @Override
    public TaxOrgan getByKey(Long nID, String sID_UA, String sName_UA) {
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
