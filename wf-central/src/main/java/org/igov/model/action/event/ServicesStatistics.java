package org.igov.model.action.event;

/**
 * Created by Dmytro Tsapko on 06.09.2016.
 */

import com.fasterxml.jackson.annotation.JsonProperty;
import org.igov.model.core.AbstractEntity;

import javax.persistence.Column;

@javax.persistence.Entity
public class ServicesStatistics extends AbstractEntity {

    @JsonProperty(value = "nID_Service")
    @Column(name = "nID_Service")
    private long nID_Service;

    @JsonProperty(value = "ServiceName")
    @Column(name = "ServiceName")
    private String ServiceName;

    @JsonProperty(value = "SID_UA")
    @Column(name = "SID_UA")
    private long SID_UA;

    @JsonProperty(value = "placeName")
    @Column(name = "placeName")
    private String placeName;

    public long getnCountTotal() {
        return nCountTotal;
    }

    @JsonProperty(value = "nCountTotal")
    @Column(name = "nCountTotal")
    private long nCountTotal;

    @JsonProperty(value = "averageRate")
    @Column(name = "averageRate")
    private float averageRate;

    @JsonProperty(value = "averageTime")
    @Column(name = "averageTime")
    private float averageTime;

    public long getnID_Service() {
        return nID_Service;
    }

    public String getServiceName() {
        return ServiceName;
    }

    public long getSID_UA() {
        return SID_UA;
    }

    public String getPlaceName() {
        return placeName;
    }

    public float getAverageRate() {
        return averageRate;
    }

    public float getAverageTime() {
        return averageTime;
    }

    @Override
    public String toString() {
        return "ServicesStatistics{" +
                "nID_Service=" + nID_Service +
                ", ServiceName='" + ServiceName + '\'' +
                ", SID_UA=" + SID_UA +
                ", placeName='" + placeName + '\'' +
                ", averageRate=" + averageRate +
                ", averageTime=" + averageTime +
                '}';
    }
}
