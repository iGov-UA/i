package org.igov.model.finance;

import org.igov.model.core.EntityDao;

public interface MerchantDao extends EntityDao<Long, Merchant> {

    Merchant getMerchant(String sID);

    boolean deleteMerchant(String sID);

}