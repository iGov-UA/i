/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.igov.activiti.common;

/**
 *
 * @author Belyavtsev Vladimir Vladimirovich (BW)
 */
public class ProcessDTO {
    
        private String sName;
        private String sBP;
        private Long nID;
        private String sDateCreate;

        public ProcessDTO(String sName, String sBP, Long nID, String sDateCreate) {
            this.sName = sName;
            this.sBP = sBP;
            this.nID = nID;
            this.sDateCreate = sDateCreate;
        }

        public String getName() {
            return sName;
        }

        public String getBP() {
            return sBP;
        }

        public Long getID() {
            return nID;
        }

        public String getDateCreate() {
            return sDateCreate;
        }
}
