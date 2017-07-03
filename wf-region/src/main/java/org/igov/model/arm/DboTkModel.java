package org.igov.model.arm;

import java.io.Serializable;
import java.lang.invoke.MethodHandles;
import java.math.BigDecimal;
import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonRootName;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

@JsonRootName(value = "dboTkModel")
public class DboTkModel implements Serializable {
	private static final transient Logger LOG = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());	
	 /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

		@JsonProperty(value = "id")
		 private Long id;
		 
		 @JsonProperty(value = "industry")
	     private String industry;
		 
		 @JsonProperty(value = "priznak")
	     private String priznak;
		 
		 @JsonProperty(value = "out_number")
	     private String out_number;
		 
		 @JsonProperty(value = "data_out")
		 private Date data_out;
		 
		 @JsonProperty(value = "dep_number")
	     private String dep_number;

		 @JsonProperty(value = "number_441")
	     private Integer number_441;

		 @JsonProperty(value = "data_in")
		 private Date data_in; 
		 
		 @JsonProperty(value = "state")
	     private String state;

		 @JsonProperty(value = "name_object")
	     private String name_object;
		 
		 @JsonProperty(value = "kod")
	     private String kod;
		 
		 @JsonProperty(value = "gruppa")
	     private String gruppa;
		 
		 @JsonProperty(value = "undergroup")
	     private String undergroup;
		 
		 @JsonProperty(value = "finans")
	     private String finans;
		 
		 @JsonProperty(value = "data_out_raz")
		 private Date data_out_raz;
		 
		 @JsonProperty(value = "number_442")
	     private Integer number_442;
		 
		 @JsonProperty(value = "winner")
	     private String winner;
		 
		 @JsonProperty(value = "kod_okpo")
	     private String kod_okpo;
		 
		 @JsonProperty(value = "phone")
	     private String phone;
		 
		 @JsonProperty(value = "srok")
	     private String srok;
		 
		 @JsonProperty(value = "expert")
	     private String expert;
		 
		 @JsonProperty(value = "summa")
	     private BigDecimal summa;
		 
		 @JsonProperty(value = "uAN")
	     private String uAN;
		 
		 @JsonProperty(value = "if_oplata")
	     private String if_oplata;
		 
		 @JsonProperty(value = "uslovie")
	     private String uslovie;
		 
		 @JsonProperty(value = "bank")
	     private String bank;
		 
		 @JsonProperty(value = "smeta")
	     private String smeta;
		 
		 @JsonProperty(value = "dataEZ")
		 private Date dataEZ;
		 
		 @JsonProperty(value = "prilog")
	     private String prilog;
		 
		 @JsonProperty(value = "updateData")
		 private Date updateData;
		 
		 @JsonProperty(value = "updOKBID")
	     private Integer updOKBID;
		 
		 @JsonProperty(value = "notes")
	     private String notes;
		 
		 @JsonProperty(value = "arhiv")
	     private String arhiv;
		 
		 @JsonProperty(value = "createDate")
		 private Date createDate;
		 	 
		 @JsonProperty(value = "zametki")
	     private String zametki;
		 
		 @JsonProperty(value = "id_corp")
	     private Integer id_corp;
		 
		 @JsonProperty(value = "dataBB")
		 private Date dataBB;
		 
		 @JsonProperty(value = "priemka")
	     private String priemka;
		 
		 @JsonProperty(value = "prockred")
	     private String prockred;
		 
		 @JsonProperty(value = "sumkred")
	     private BigDecimal sumkred;
		 
		 @JsonProperty(value = "sumzak")
	     private BigDecimal sumzak;
		 
		 @JsonProperty(value = "auctionForm")
	     private String auctionForm;
		 
		 @JsonProperty(value = "protocol_Number")
	     private String protocol_Number;
		 
		 @JsonProperty(value = "correctionDoc")
	     private String correctionDoc;
		 
		 @JsonProperty(value = "prioritet")
	     private String prioritet;
		 
		 @JsonProperty(value = "longterm")
	     private String longterm;

		public String getIndustry() {
			return industry;
		}

		public void setIndustry(String industry) {
			this.industry = industry;
		}

		public String getPriznak() {
			return priznak;
		}

		public void setPriznak(String priznak) {
			this.priznak = priznak;
		}

		public String getOut_number() {
			return out_number;
		}

		public void setOut_number(String out_number) {
			this.out_number = out_number;
		}

		public Date getData_out() {
			return data_out;
		}

		public void setData_out(Date data_out) {
			this.data_out = data_out;
		}

		public String getDep_number() {
			return dep_number;
		}

		public void setDep_number(String dep_number) {
			this.dep_number = dep_number;
		}

		public Integer getNumber_441() {
			return number_441;
		}

		public void setNumber_441(Integer number_441) {
			this.number_441 = number_441;
		}

		public Date getData_in() {
			return data_in;
		}

		public void setData_in(Date data_in) {
			this.data_in = data_in;
		}

		public String getState() {
			return state;
		}

		public void setState(String state) {
			this.state = state;
		}

		public String getName_object() {
			return name_object;
		}

		public void setName_object(String name_object) {
			this.name_object = name_object;
		}

		public String getKod() {
			return kod;
		}

		public void setKod(String kod) {
			this.kod = kod;
		}

		public String getGruppa() {
			return gruppa;
		}

		public void setGruppa(String gruppa) {
			this.gruppa = gruppa;
		}

		public String getUndergroup() {
			return undergroup;
		}

		public void setUndergroup(String undergroup) {
			this.undergroup = undergroup;
		}

		public String getFinans() {
			return finans;
		}

		public void setFinans(String finans) {
			this.finans = finans;
		}

		public Date getData_out_raz() {
			return data_out_raz;
		}

		public void setData_out_raz(Date data_out_raz) {
			this.data_out_raz = data_out_raz;
		}

		public Integer getNumber_442() {
			return number_442;
		}

		public void setNumber_442(Integer number_442) {
			this.number_442 = number_442;
		}

		public String getWinner() {
			return winner;
		}

		public void setWinner(String winner) {
			this.winner = winner;
		}

		public String getKod_okpo() {
			return kod_okpo;
		}

		public void setKod_okpo(String kod_okpo) {
			this.kod_okpo = kod_okpo;
		}

		public String getPhone() {
			return phone;
		}

		public void setPhone(String phone) {
			this.phone = phone;
		}

		public String getSrok() {
			return srok;
		}

		public void setSrok(String srok) {
			this.srok = srok;
		}

		public String getExpert() {
			return expert;
		}

		public void setExpert(String expert) {
			this.expert = expert;
		}

		public String getuAN() {
			return uAN;
		}

		public void setuAN(String uAN) {
			this.uAN = uAN;
		}

		public String getIf_oplata() {
			return if_oplata;
		}

		public void setIf_oplata(String if_oplata) {
			this.if_oplata = if_oplata;
		}

		public String getUslovie() {
			return uslovie;
		}

		public void setUslovie(String uslovie) {
			this.uslovie = uslovie;
		}

		public String getBank() {
			return bank;
		}

		public void setBank(String bank) {
			this.bank = bank;
		}

		public String getSmeta() {
			return smeta;
		}

		public void setSmeta(String smeta) {
			this.smeta = smeta;
		}

		public Date getDataEZ() {
			return dataEZ;
		}

		public void setDataEZ(Date dataEZ) {
			this.dataEZ = dataEZ;
		}

		public String getPrilog() {
			return prilog;
		}

		public void setPrilog(String prilog) {
			this.prilog = prilog;
		}

		public Date getUpdateData() {
			return updateData;
		}

		public void setUpdateData(Date updateData) {
			this.updateData = updateData;
		}

		public Integer getUpdOKBID() {
			return updOKBID;
		}

		public void setUpdOKBID(Integer updOKBID) {
			this.updOKBID = updOKBID;
		}

		public String getNotes() {
			return notes;
		}

		public void setNotes(String notes) {
			this.notes = notes;
		}

		public String getArhiv() {
			return arhiv;
		}

		public Date getCreateDate() {
			return createDate;
		}

		public void setCreateDate(Date createDate) {
			this.createDate = createDate;
		}

		public void setArhiv(String arhiv) {
			this.arhiv = arhiv;
		}

		public String getZametki() {
			return zametki;
		}

		public void setZametki(String zametki) {
			this.zametki = zametki;
		}

		public Integer getId_corp() {
			return id_corp;
		}

		public void setId_corp(Integer id_corp) {
			this.id_corp = id_corp;
		}

		public Date getDataBB() {
			return dataBB;
		}

		public void setDataBB(Date dataBB) {
			this.dataBB = dataBB;
		}

		public String getPriemka() {
			return priemka;
		}

		public void setPriemka(String priemka) {
			this.priemka = priemka;
		}

		public String getProckred() {
			return prockred;
		}

		public void setProckred(String prockred) {
			this.prockred = prockred;
		}

		public String getAuctionForm() {
			return auctionForm;
		}

		public void setAuctionForm(String auctionForm) {
			this.auctionForm = auctionForm;
		}

		public String getProtocol_Number() {
			return protocol_Number;
		}

		public void setProtocol_Number(String protocol_Number) {
			this.protocol_Number = protocol_Number;
		}

		public String getCorrectionDoc() {
			return correctionDoc;
		}

		public void setCorrectionDoc(String correctionDoc) {
			this.correctionDoc = correctionDoc;
		}

		public String getPrioritet() {
			return prioritet;
		}

		public void setPrioritet(String prioritet) {
			this.prioritet = prioritet;
		}

		public String getLongterm() {
			return longterm;
		}

		public void setLongterm(String longterm) {
			this.longterm = longterm;
		}

		public BigDecimal getSumma() {
			return summa;
		}

		public void setSumma(BigDecimal summa) {
			this.summa = summa;
		}

		public BigDecimal getSumkred() {
			return sumkred;
		}

		public void setSumkred(BigDecimal sumkred) {
			this.sumkred = sumkred;
		}

		public BigDecimal getSumzak() {
			return sumzak;
		}

		public void setSumzak(BigDecimal sumzak) {
			this.sumzak = sumzak;
		}

		public Long getId() {
			return id;
		}

		public void setId(Long id) {
			this.id = id;
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