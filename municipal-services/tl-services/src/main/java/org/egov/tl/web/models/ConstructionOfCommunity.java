package org.egov.tl.web.models;

import java.io.Serializable;
import java.sql.Timestamp;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import org.egov.tl.web.models.TradeLicenseDetail.ChannelEnum;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonNode;
import com.vladmihalcea.hibernate.type.json.JsonBinaryType;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@AllArgsConstructor
@Getter
@Setter
@NoArgsConstructor
@Builder
//@Entity
//@Table(name="eg_tl_construction_Of_community")
//@TypeDef(name = "jsonb", typeClass = JsonBinaryType.class)
public class ConstructionOfCommunity {

	@JsonProperty("id")
	private String id;
	
	@JsonProperty("licenseNumber")
	private String licenseNumber;
	
	@JsonProperty("applicationNumber")
	private String applicationNumber;
	
	@JsonProperty("workFlowCode")
	private String workFlowCode; 
	
	@JsonProperty("isDraft")
	private String isDraft;  // 0-draft,1 -not draft
	
	@JsonProperty("paymentType")
	private int paymentType;  // 1-partial payment,2- full payment   //new 
	
	@JsonProperty("applicationStatus")
	private int applicationStatus; //  1- pending,2-partial paied,3-full paied    // new
	
	@JsonProperty("paidAmount")
	private String paidAmount;	

	@JsonProperty("isFullPaymentDone")
	private boolean isFullPaymentDone;
	
	@JsonProperty("totalConstructionOfCommunityCharge")
	private String totalConstructionOfCommunityCharge;
	
	@JsonProperty("createdDate")
	private Timestamp createdDate;
	
	@JsonProperty("auditDetails")
	private AuditDetails auditDetails; 
	
	@JsonProperty("createdTime")
	private long createdTime;
	
	
	@JsonProperty("appliedBy")
	private String appliedBy;
	
	@JsonProperty("areaInAcers")
	private String areaInAcers; 
	
	@JsonProperty("validUpTo")
	private String validUpTo; 
	
	@JsonProperty("applyedForExtentionPerioud")
	private String applyedForExtentionPerioud; 
	
	@JsonProperty("typeOfCommunitySite")
	private String typeOfCommunitySite; 
	
	//file below
	
	@JsonProperty("copyOfBoardResolution")
	private String copyOfBoardResolution; 
	
	@JsonProperty("justificationForExtention")
	private String justificationForExtention; 
	
	@JsonProperty("proofOfOwnershipOfCommunity")
	private String proofOfOwnershipOfCommunity; 
	
	@JsonProperty("proofOfOnlinePaymentOfExtention")
	private String proofOfOnlinePaymentOfExtention; 
	
	@JsonProperty("uploadRenewalLicenseCopy")
	private String uploadRenewalLicenseCopy; 
	
	@JsonProperty("explonatoryNotForExtention")
	private String explonatoryNotForExtention; 
	
	@JsonProperty("locationOfApplied")
	private String locationOfApplied; 
	
	@JsonProperty("anyOtherDocumentByDirector")
	private String anyOtherDocumentByDirector; 
	
	
}
