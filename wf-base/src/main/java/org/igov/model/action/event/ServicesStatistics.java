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
    private Long nID_Service;

    @JsonProperty(value = "ServiceName")
    @Column(name = "ServiceName")
    private String ServiceName;

    @JsonProperty(value = "SID_UA")
    @Column(name = "SID_UA")
    private Long SID_UA;

    @JsonProperty(value = "placeName")
    @Column(name = "placeName")
    private String placeName;

    public Long getnCountTotal() {
        return nCountTotal;
    }

    @JsonProperty(value = "nCountTotal")
    @Column(name = "nCountTotal")
    private Long nCountTotal;

    @JsonProperty(value = "nCountFeedback")
    @Column(name = "nCountFeedback")
    private Long nCountFeedback;

    @JsonProperty(value = "nCountEscalation")
    @Column(name = "nCountEscalation")
    private Long nCountEscalation;

    @JsonProperty(value = "averageRate")
    @Column(name = "averageRate")
    private Float averageRate;

    @JsonProperty(value = "averageTime")
    @Column(name = "averageTime")
    private Float averageTime;

    public Long getnCountFeedback() {
        return nCountFeedback;
    }

    public Long getnCountEscalation() {
        return nCountEscalation;
    }

    public Long getnID_Service() {
        return nID_Service;
    }

    public String getServiceName() {
        return ServiceName;
    }

    public Long getSID_UA() {
        return SID_UA;
    }

    public String getPlaceName() {
        return placeName;
    }

    public Float getAverageRate() {
        return averageRate;
    }

    public Float getAverageTime() {
        return averageTime;
    }

	@Override
	public String toString() {
		return "ServicesStatistics [nID_Service=" + nID_Service + ", ServiceName=" + ServiceName + ", SID_UA=" + SID_UA
				+ ", placeName=" + placeName + ", nCountTotal=" + nCountTotal + ", nCountFeedback=" + nCountFeedback
				+ ", nCountEscalation=" + nCountEscalation + ", averageRate=" + averageRate + ", averageTime="
				+ averageTime + "]";
	}

    
    
    
}
