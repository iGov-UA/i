package org.igov.model.document;

import java.lang.invoke.MethodHandles;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;

import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;
import org.hibernate.annotations.Type;
import org.igov.model.core.AbstractEntity;
import org.igov.util.JSON.JsonDateDeserializer;
import org.igov.util.JSON.JsonDateSerializer;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

@Entity
public class DocumentStepSubjectRight extends AbstractEntity {

	private static final transient Logger LOG = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

	@JsonIgnore
	@ManyToOne(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
	@JoinColumn(name = "nID_DocumentStep")
	private DocumentStep documentStep;

	@JsonProperty(value = "sKey_GroupPostfix")
	private String sKey_GroupPostfix;

	@JsonProperty(value = "sName")
	private String sName;

	@JsonProperty(value = "bWrite")
	private Boolean bWrite;

	@JsonProperty(value = "bNeedECP")
	private Boolean bNeedECP;

	@JsonProperty(value = "sLogin")
	private String sLogin;
        
	@JsonProperty(value = "sID_Field")
	private String sID_Field;

	@JsonProperty(value = "sID_File_ForSign")
	private String sID_File_ForSign;

	@JsonProperty(value = "sDate")
	@JsonSerialize(using = JsonDateSerializer.class)
    @JsonDeserialize(using = JsonDateDeserializer.class)
	@Type(type = DATETIME_TYPE)
	private DateTime sDate;

	@JsonProperty(value = "sDateECP")
	@JsonSerialize(using = JsonDateSerializer.class)
    @JsonDeserialize(using = JsonDateDeserializer.class)
	@Type(type = DATETIME_TYPE)
	private DateTime sDateECP;

	@OneToMany(mappedBy = "documentStepSubjectRight", cascade = CascadeType.ALL)
	@LazyCollection(LazyCollectionOption.FALSE)
	private List<DocumentStepSubjectRightField> documentStepSubjectRightFields;

	public List<DocumentStepSubjectRightField> getDocumentStepSubjectRightFields() {
		return documentStepSubjectRightFields;
	}

	public void setDocumentStepSubjectRightFields(List<DocumentStepSubjectRightField> documentStepSubjectRightFields) {
		this.documentStepSubjectRightFields = documentStepSubjectRightFields;
	}

	public DocumentStep getDocumentStep() {
		return documentStep;
	}

	public void setDocumentStep(DocumentStep documentStep) {
		this.documentStep = documentStep;
	}

	public String getsKey_GroupPostfix() {
		return sKey_GroupPostfix;
	}

	public void setsKey_GroupPostfix(String sKey_GroupPostfix) {
		this.sKey_GroupPostfix = sKey_GroupPostfix;
	}

	public String getsName() {
		return sName;
	}

	public void setsName(String sName) {
		this.sName = sName;
	}

	public Boolean getbWrite() {
		return bWrite;
	}

	public void setbWrite(Boolean bWrite) {
		this.bWrite = bWrite;
	}

	public Boolean getbNeedECP() {
		return bNeedECP;
	}

	public void setbNeedECP(Boolean bNeedECP) {
		this.bNeedECP = bNeedECP;
	}

	public String getsLogin() {
		return sLogin;
	}

	public void setsLogin(String sLogin) {
		this.sLogin = sLogin;
	}
        
	public String getsID_Field() {
		return sID_Field;
	}

	public void setsID_Field(String sID_Field) {
		this.sID_Field = sID_Field;
	}

	public String getsID_File_ForSign() {
		return sID_File_ForSign;
	}

	public void setsID_File_ForSign(String sID_File_ForSign) {
		this.sID_File_ForSign = sID_File_ForSign;
	}

	public DateTime getsDate() {
		return sDate;
	}

	public void setsDate(DateTime sDate) {
		this.sDate = sDate;
	}

	public DateTime getsDateECP() {
		return sDateECP;
	}

	public void setsDateECP(DateTime sDateECP) {
		this.sDateECP = sDateECP;
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
