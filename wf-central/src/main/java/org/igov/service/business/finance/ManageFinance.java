/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.igov.service.business.finance;

import java.util.ArrayList;
import java.util.List;
import org.igov.model.finance.Merchant;
import org.igov.model.finance.MerchantVO;

/**
 *
 * @author Belyavtsev Vladimir Vladimirovich (BW)
 */
public class ManageFinance {

    public static List<MerchantVO> toVO(List<Merchant> merchants) {
        List<MerchantVO> res = new ArrayList<>();
        for (Merchant merchant : merchants) {
            res.add(new MerchantVO(merchant));
        }

        return res;
    }    
    
}
