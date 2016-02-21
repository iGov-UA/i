package org.igov.model.finance;

import org.hibernate.Criteria;
import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Repository;
import org.igov.model.core.GenericEntityDao;

import java.util.List;

/**
 * @author grigoriy-romanenko
 */
@Repository
public class CurrencyDaoImpl extends GenericEntityDao<Currency>
        implements CurrencyDao {

    protected CurrencyDaoImpl() {
        super(Currency.class);
    }

    @SuppressWarnings("unchecked")
    public List<Currency> getCurrencies(String sID_UA,
            String sName_UA, String sName_EN, String sID_Currency) {
        Criteria criteria = getSession().createCriteria(Currency.class);
        if (sID_UA != null) {
            criteria.add(Restrictions.eq("sID_UA", sID_UA));
        }
        if (sName_UA != null) {
            if(!"".equals(sName_UA.trim())){
                criteria.add(Restrictions.eq("sName_UA", sName_UA));
                //criteria.add(Restrictions.ilike("sName_UA", sName_UA));
            }
        }
        if (sName_EN != null) {
            criteria.add(Restrictions.eq("sName_EN", sName_EN));
        }
        if (sID_Currency != null) {
            criteria.add(Restrictions.eq("sID_Currency", sID_Currency));
        }

        return (List<Currency>) criteria.list();
    }
}
