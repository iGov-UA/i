/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.igov.service.controller.security;

/**
 *
 * @author bw
 */
public enum AccessContract {
    Request("Request")
    , RequestAndLogin("RequestAndLogin")
    , RequestAndLoginUnlimited("RequestAndLoginUnlimited")
    ;
    public String sID = null;
    AccessContract(String sID){
        this.sID = sID;
    }
    
}
