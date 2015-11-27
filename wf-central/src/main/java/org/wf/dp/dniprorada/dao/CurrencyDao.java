package org.wf.dp.dniprorada.dao;

import org.wf.dp.dniprorada.base.dao.EntityDao;
import org.wf.dp.dniprorada.model.Currency;

import java.util.List;

public interface CurrencyDao extends EntityDao<Currency> {

    List<Currency> getCurrencies(String sID_UA, String sName_UA, String sName_EN);

}
