package org.igov.model;

import org.igov.model.core.EntityDao;

public interface MerchantDao extends EntityDao<Merchant> {

    Merchant getMerchant(String sID);

    boolean deleteMerchant(String sID);

}