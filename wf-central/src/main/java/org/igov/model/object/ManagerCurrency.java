/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.igov.model.object;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

/**
 *
 * @author Belyavtsev Vladimir Vladimirovich (BW)
 */
public class ManagerCurrency {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(ManagerCurrency.class);
    
    @Autowired
    private CurrencyDao currencyDao;

    public void deleteByKeys(Long nID, String sID_UA){
        if (nID != null) {
            currencyDao.delete(nID);
        } else {
            currencyDao.deleteBy("sID_UA", sID_UA);
        }
    }
    
    
    public Currency findByKeys(Long nID, String sID_UA) {
        if (nID != null) {
            return currencyDao.findByIdExpected(nID);
        }
        if (sID_UA != null) {
            return currencyDao.findBy("sID_UA", sID_UA).orNull();
        }
        return null;
    }

    public Currency updateCurrencyParams(Currency currency,
            String sID_UA, String sName_UA, String sName_EN) {
        if (sID_UA != null) {
            currency.setsID_UA(sID_UA);
        }
        if (sName_UA != null) {
            currency.setsName_UA(sName_UA);
        }
        if (sName_EN != null) {
            currency.setsName_EN(sName_EN);
        }
        return currencyDao.saveOrUpdate(currency);
        
    }    
}
