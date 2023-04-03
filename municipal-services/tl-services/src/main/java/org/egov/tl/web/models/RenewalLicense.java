package org.egov.tl.web.models;

import java.io.Serializable;
import java.sql.Date;
import java.sql.Timestamp;
import java.util.List;

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


@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name="eg_tl_renewal_license")
@TypeDef(name = "jsonb", typeClass = JsonBinaryType.class)
public class RenewalLicense {
	
	@JsonProperty("id")
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int id;
	
	/********************************** Step:-1 Application Information start **************************************/
	@JsonProperty("applicationNumber")
	private String applicationNumber;
	
	@JsonProperty("createdAt")
	private Timestamp createdAt;

    @JsonProperty("validUpTo")
    private String validUpTo;
	
	@JsonProperty("renewalForDuration")
	private String renewalForDuration;// Month/Year
	
	@JsonProperty("clonizerName")
    private String clonizerName;
	
	@JsonProperty("clonizerType")
    private String clonizerType;
	
	@JsonProperty("areaInAcre")
    private String areaInAcre;
	
	@JsonProperty("sectorNo")
    private String sectorNo;
	
	@JsonProperty("village")
    private String village;
	
	@JsonProperty("tehsil")
    private String tehsil;
	
	@JsonProperty("district")
    private String district;
	
	@JsonProperty("isRenewalAppliedWithIn")
	private boolean isRenewalAppliedWithIn; // if no
	
	@JsonProperty("renewalDelayInDays")
	private String renewalDelayInDays;
		
	@JsonProperty("penaltyPayable")
	private String penaltyPayable;
	
	@JsonProperty("renewalAmount")
	private String renewalAmount;
	
	@JsonProperty("nonCompletionProjectReasons")
	@Column(columnDefinition="TEXT")
	private String nonCompletionProjectReasons;
	 
	@JsonProperty("isRenewalAppliedFirstTime")
	private boolean isRenewalAppliedFirstTime;
	
	@Type(type = "jsonb")
	@Column(columnDefinition = "jsonb")
	private List<RenewalLicensePreviopusCondition> previouslyCondition_RL;
	
	@JsonProperty("colonizerObtainApproveNoc")
	private boolean colonizerObtainApproveNoc;
	
	@JsonProperty("colonizerTransferred")
	private boolean colonizerTransferred;
	
//	@JsonProperty("colonizerTransferredDetails")
//	private JsonNode colonizerTransferredDetails=null;///if  colonizerTransferred yes	

	@JsonProperty("colonizerConveyed")
	private String colonizerConveyed;
	
	@JsonProperty("complianceWithCondition")
	private String complianceWithCondition;
	
	
	@JsonProperty("isAnyLegalActivity")
	private boolean isAnyLegalActivity;
	
	@JsonProperty("isAnyLegalActivityDetails")
	private String isAnyLegalActivityDetails;///if  isAnyLegalActivity yes
	
	/********************************** Application Information end **************************************/
	
	/********************************** step:-2 Development Data start ***********************************/
		
	@JsonProperty("edc_status")
	private boolean edc_status;
	
//	@JsonProperty("edc_details")
//	private JsonNode edc_details=null;
	
	@Type(type = "jsonb")
	@Column(columnDefinition = "jsonb")
	private List<RenewalLicenseEDC> renewalLicenseEDC;
	
	@JsonProperty("sidc_status")
	private boolean sidc_status;
	
	@Type(type = "jsonb")
	@Column(columnDefinition = "jsonb")
	private List<RenewalLicenseSIDC> renewalLicenseSIDC;
	
//	@JsonProperty("sidc_details")
//	private JsonNode sidc_details=null;
	
	@JsonProperty("enhanceEDCStatus")
	private String enhanceEDCStatus; 
	
	@JsonProperty("enhanceEDCAmount")
	private String enhanceEDCAmount; 
	
	@JsonProperty("enhanceEDCRemark")
	private String enhanceEDCRemark; 
	
	
	@JsonProperty("caCertificateDocument")
	private String caCertificateDocument ; 
	
	
	@JsonProperty("rule24FinancialYearData")
	private boolean rule24FinancialYearData; 
	
	@JsonProperty("rule24Remark")
	private String rule24Remark; 
	
	@JsonProperty("rule24DocumentUpload")
	private String rule24DocumentUpload; 
	
	
	@JsonProperty("rule26FinancialYearData")
	private boolean rule26FinancialYearData; 
	
	@JsonProperty("rule26Remark")
	private String rule26Remark; 
	
	@JsonProperty("rule26DocumentUpload")
	private String rule26DocumentUpload; 
	
	@JsonProperty("rule27FinancialYearData")
	private boolean rule27FinancialYearData; 
	
	@JsonProperty("rule27Remark")
	private String rule27Remark; 
	
	@JsonProperty("rule27DocumentUpload")
	private String rule27DocumentUpload; 
	
	@JsonProperty("rule28FinancialYearData")
	private boolean rule28FinancialYearData; 
	
	@JsonProperty("rule28Remark")
	private String rule28Remark; 
	
	@JsonProperty("rule28DocumentUpload")
	private String rule28DocumentUpload; 
	
	
	@JsonProperty("statusOfCommunitySites")
	private boolean statusOfCommunitySites;
	
	
	@JsonProperty("communitySiteSoldToThirdParty")
	private boolean communitySiteSoldToThirdParty; //if statusOfCommunitySites no then here data will come y/n
	
//	@JsonProperty("soc_details")
//	private JsonNode soc_details=null;
	
	@Type(type = "jsonb")
	@Column(columnDefinition = "jsonb")
	private List<RenewalLicenseSOC> renewalLicenseSOC;
	
//	@JsonProperty("spc_details")
//	private JsonNode spc_details=null;
	
	@Type(type = "jsonb")
	@Column(columnDefinition = "jsonb")
	private List<RenewalLicenseSPC> renewalLicenseSPC;
		
	@JsonProperty("totalNoOfEWSFlatsOrPlotsApprovedInLayoutPlan")
	private String totalNoOfEWSFlatsOrPlotsApprovedInLayoutPlan;
	
	/********************************** step:-2 Development Data start ***********************************/

	
	
	
	/********************************** SOA--Status Of the Allotment start ***********************************/

//	@JsonProperty("SOA_allotmentStatus")
//	private String SOA_allotmentStatus;
//	
//	@JsonProperty("SOA_allotmentInTimeOrDelay")
//	private String SOA_allotmentInTimeOrDelay;
//	
//	@JsonProperty("SOA_allotmentNoOfPlot")
//	private String SOA_allotmentNoOfPlot;
//	
//	@JsonProperty("SOA_compositionFeePaid")
//	private String SOA_compositionFeePaid;
//	
//	@JsonProperty("SOA_compositionAmount")
//	private String SOA_compositionAmount;
//	
//	@JsonProperty("SOA_compositionAmountRemark")
//	private String SOA_compositionAmountRemark;
//	
//	@JsonProperty("SOA_allotmentRemark")
//	private String SOA_allotmentRemark;
	
	
	/********************************** SOA--Status Of the Allotment end ***********************************/
	
	/********************************** Document upload start ***********************************/
	@JsonProperty("incomTaxClearnenceCertificate")
	private String incomTaxClearnenceCertificate;
	
	@JsonProperty("explanationDevelopmentWorkDoc")
	private String explanationDevelopmentWorkDoc;
	
	@JsonProperty("statusDevelopmentWorkDoc")
	private String statusDevelopmentWorkDoc;
	
	@JsonProperty("oldLicenseDoc")
	private String oldLicenseDoc;
	
	/********************************** Document upload end ***********************************/
	
	
}
