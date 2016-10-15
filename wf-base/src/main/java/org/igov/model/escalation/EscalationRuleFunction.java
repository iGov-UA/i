package org.igov.model.escalation;

import com.fasterxml.jackson.annotation.JsonProperty;

import javax.persistence.Column;
import org.igov.model.core.NamedEntity;

@javax.persistence.Entity
public class EscalationRuleFunction extends NamedEntity {

    /**
     * строка бина-обработчика
     */
    @JsonProperty(value = "sBeanHandler")
    @Column
    private String sBeanHandler;

    //    @OneToMany(mappedBy = "nID_EscalationRuleFunction")
    //    private List<EscalationRule> aEscalationRule = new ArrayList<>();

    public String getsBeanHandler() {
        return sBeanHandler;
    }

    public void setsBeanHandler(String sBeanHandler) {
        this.sBeanHandler = sBeanHandler;
    }
}
