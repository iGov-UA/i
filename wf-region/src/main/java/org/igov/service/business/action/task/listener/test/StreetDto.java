package org.igov.service.listener.test;

import com.fasterxml.jackson.annotation.JsonProperty;

public class StreetDto {

    @JsonProperty("id")
    private Long id;

    @JsonProperty("name_en")
    private String nameEn;

    @JsonProperty("addrNumber")
    private String addressNumber;

    @Override
    public String toString() {
        return "{Id=" + this.id + ",Ok},";
    }

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNameEn() {
        return this.nameEn;
    }

    public void setNameEn(String nameEn) {
        this.nameEn = nameEn;
    }

    public String getAddressNumber() {
        return this.addressNumber;
    }

    public void setAddressNumber(String addressNumber) {
        this.addressNumber = addressNumber;
    }

}
