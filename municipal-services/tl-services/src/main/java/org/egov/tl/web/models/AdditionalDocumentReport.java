package org.egov.tl.web.models;

import java.sql.Time;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class AdditionalDocumentReport {
	@JsonProperty("id")
	private String id;

	@JsonProperty("licenceNumber")
	private String licenceNumber;
	@JsonProperty("userName")
	private String userName;
	@JsonProperty("developerName")
	private String developerName;
	@JsonProperty("businessService")
	private String businessService;

	List<Fileddetail> applicantInfo = null;
	List<Fileddetail> applicantPurpose = null;
	List<Fileddetail> landSchedule = null;
	List<Fileddetail> detailsOfAppliedland = null;
	List<Fileddetail> feesANdCharges = null;
}
