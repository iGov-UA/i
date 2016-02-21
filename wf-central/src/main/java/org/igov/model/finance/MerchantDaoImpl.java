package org.igov.model.finance;

import org.springframework.stereotype.Repository;
import org.igov.model.core.GenericEntityDao;

@Repository
public class MerchantDaoImpl extends GenericEntityDao<Merchant> implements MerchantDao {

    public static final String S_ID = "sID";

    protected MerchantDaoImpl() {
        super(Merchant.class);
    }

    @Override
    public Merchant getMerchant(String sID) {
        return findBy(S_ID, sID).orNull();
    }

    public boolean deleteMerchant(String sID) {
        return deleteBy(S_ID, sID) > 0;
    }
}