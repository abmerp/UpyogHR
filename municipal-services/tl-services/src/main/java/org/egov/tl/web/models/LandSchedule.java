package org.egov.tl.web.models;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LandSchedule {

	private String licenseApplied;
	private String licenseNumber;
	private String potential;
	private String siteLoc;
	private String approachType;
	private String approachRoadWidth;
	private String specify;
	private String typeLand;
	private String thirdParty;
	private String thirdPartyRemark;
	private String thirdPartyDoc;

	private String migrationLic;
	private String areaUnderMigration;
	private String purposeParentLic;
	private String licNo;
	private String areaofParentLic;
	private String validityOfParentLic;
	private String renewalFee;
	private String freshlyApplied;
	private String approvedLayoutPlan;
	private String uploadPreviouslyLayoutPlan;

	private String encumburance;
	private String encumburanceOther;
	private String encumburanceDoc;
	private String litigation;
	private String litigationRemark;;
	private String litigationDoc;

	private String court;
	private String courtyCaseNo;
	private String courtDoc;

	private String insolvency;
	private String insolvencyRemark;
	private String insolvencyDoc;

	private String appliedLand;
	private String appliedLandDoc;

	private String revenueRasta;
	private String revenueRastaWidth;

	private String waterCourse;
	private String waterCourseRemark;

	private String compactBlock;
	private String compactBlockRemark;

	private String landSandwiched;
	private String landSandwichedRemark;
	private String acquistion;

	private String acquistionRemark;

	private String sectionFour;
	private String sectionSix;
	private String orderUpload;
	private String landCompensation;
	private String releaseStatus;
	private String awardDate;
	private String releaseDate;
	private String siteDetail;
	private String siteApproachable;

	// private String approachable;
	private String vacant;
	private String vacantRemark;

	private String construction;
	private String typeOfConstruction;
	private String constructionRemark;

	private String ht;
	private String htRemark;
	private String gasRemark;

	private String gas;
	private String nallah;
	private String nallahRemark;

	private String road;
	private String roadWidth;
	private String roadRemark;
	private String marginalLand;
	private String marginalLandRemark;
	private String utilityLine;

	// private String land;
	private String utilityWidth;
	private String utilityRemark;

	@JsonProperty("landSchedule")

	private String landSchedule;
	@JsonProperty("mutation")
	private String mutation;
	@JsonProperty("jambandhi")
	private String jambandhi;
	@JsonProperty("detailsOfLease")
	private String detailsOfLease;
	@JsonProperty("addSalesDeed")
	private String addSalesDeed;
	@JsonProperty("revisedLandSchedule")
	private String revisedLandSchedule;
	@JsonProperty("copyofSpaBoard")
	private String copyofSpaBoard;
	@JsonProperty("copyOfShajraPlan")
	private String copyOfShajraPlan;
//	private String LayoutPlan;
	@JsonProperty("proposedLayoutPlan")
	private String proposedLayoutPlan;
	@JsonProperty("revisedLansSchedule")
	private String revisedLansSchedule;
	@JsonProperty("areaOfParentLicence")
	private String areaOfParentLicence;
	private String minimumApproachFour;
	private String minimumApproachEleven;
	private String alreadyConstructedSectorad;
	private String joiningOwnLand;
	private String applicantHasDonated;
	private String giftDeedHibbanama;
	private String adjoiningOthersLand;
	private String landOwnerDonated;
	private String constructedRowWidth;
	private String irrevocableConsent;
	private String uploadRrrevocableConsent;
	private String approachFromProposedSector;
	private String sectorAndDevelopmentWidth;
	private String whetherAcquired;
	private String whetherConstructed;
	private String serviceSectorRoadAcquired;
	private String serviceSectorRoadConstructed;
	private String approachFromInternalCirculation;
	private String internalAndSectoralWidth;
	private String parentLicenceApproach;
	private String availableExistingApproach;
	private String availableExistingApproachDoc;
	private String whetherAcquiredForInternalCirculation;
	private String whetherConstructedForInternalCirculation;

	// -----addon---------
	@JsonProperty("reraRegistered")
	private String reraRegistered;
	@JsonProperty("reraDocUpload")
	private String reraDocUpload;
	@JsonProperty("reraNonRegistrationDoc")
	private String reraNonRegistrationDoc;
//	@JsonProperty("LandScheduleDetails")
//	private List<LandScheduleDetails> landScheduleDetails;
	@JsonProperty("anyOther")
	private String anyOther;
	@JsonProperty("anyOtherRemark")
	private String anyOtherRemark;
	@JsonProperty("none")
	private String none;
	@JsonProperty("noneRemark")
	private String noneRemark;
	@JsonProperty("adjoiningOwnLand")
	private String adjoiningOwnLand;

	@JsonProperty("renewalLicenceFee")
	private String renewalLicenceFee;

	private String releaseOrderCopyDoc;
	private String litigationRegardingLandRelease;
	private String CWPSLPNumber;
	private String accessPermissionAuthority;
	private String NHSRAccess;
	private String othersLandFall;
	private String othersLandFallRemark;
//	private String northSurroundings;
//	private String southSurroundings;
//	private String eastSurroundings;
//	private String westSurroundings;
	@JsonProperty("surroundingsObj")
	private List<SurroundingsObj> surroundingsObj;
	private String passingOtherFeature;
	private String detailsThereof;

	private String separatedBy;
	private String whetherCompactBlock;

	private String shajraPlanOuterBoundary;
	private String patwariOriginalShajraPlan;
	private String anyOtherDoc;
}
