package org.igov.model;

import org.igov.model.core.EntityDao;

import java.util.List;

public interface CurrencyDao extends EntityDao<Currency> {

    List<Currency> getCurrencies(String sID_UA, String sName_UA, String sName_EN);

}
