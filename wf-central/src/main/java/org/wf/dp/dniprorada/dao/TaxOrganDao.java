package org.wf.dp.dniprorada.dao;

import org.wf.dp.dniprorada.base.dao.EntityDao;
import org.wf.dp.dniprorada.model.TaxOrgan;

import java.util.List;

/**
 * @author NickVeremeichyk
 * @since 2015-11-24.
 */
public interface TaxOrganDao extends EntityDao<TaxOrgan> {
    List<TaxOrgan> getAll(String sID_UA, String sName_UA);

    TaxOrgan setTaxOrgan(Long nID, String sID_UA, String sName_UA);

    void removeByKey(Long nID, String sID_UA);

    TaxOrgan getByKey(Long nID, String sID_UA, String sName_UA);

//    TaxOrgan getTaxOrgan(Long nID, String sID_UA, String sName_UA);


}
