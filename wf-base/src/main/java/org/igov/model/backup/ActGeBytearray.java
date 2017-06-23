package org.igov.model.backup;

import java.io.Serializable;
import java.lang.invoke.MethodHandles;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonRootName;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

@JsonRootName(value = "actGeBytearray")
public class ActGeBytearray implements Serializable {
	private static final transient Logger LOG = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());	
	 /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	

		@JsonProperty(value = "id")
		 private String id_;
		 
		 @JsonProperty(value = "rev")
	     private Integer rev_;
		 
		 @JsonProperty(value = "name")
	     private String name_;
		 
		 @JsonProperty(value = "deployment_id")
	     private String deployment_id_;
		 
		 @JsonProperty(value = "bytes")
		 private String bytes_;
		 
		 @JsonProperty(value = "generated")
	     private String generated_;
		 
		 
		public String getId_() {
			return id_;
		}

		public void setId_(String id_) {
			this.id_ = id_;
		}

		public Integer getRev_() {
			return rev_;
		}

		public void setRev_(Integer rev_) {
			this.rev_ = rev_;
		}

		public String getName_() {
			return name_;
		}

		public void setName_(String name_) {
			this.name_ = name_;
		}

		public String getDeployment_id_() {
			return deployment_id_;
		}

		public void setDeployment_id_(String deployment_id_) {
			this.deployment_id_ = deployment_id_;
		}


		public String getBytes_() {
			return bytes_;
		}

		public void setBytes_(String bytes_) {
			this.bytes_ = bytes_;
		}

		public String getGenerated_() {
			return generated_;
		}

		public void setGenerated_(String generated_) {
			this.generated_ = generated_;
		}

		@Override
		public String toString() {
			try {
				return new ObjectMapper().configure(SerializationFeature.WRAP_ROOT_VALUE, true)
						.writerWithDefaultPrettyPrinter().writeValueAsString(this);
			} catch (JsonProcessingException e) {
				LOG.info(String.format("error [%s]", e.getMessage()));
			}
			return null;
		}
}