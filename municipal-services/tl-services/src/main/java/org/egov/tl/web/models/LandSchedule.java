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
	private Document thirdPartyDoc;

	private String migrationLic;
	private String areaUnderMigration;
	private String purposeParentLic;
	private String licNo;
	private String areaofParentLic;
	private String validityOfParentLic;
	private String renewalFee;
	private String freshlyApplied;
	private Document approvedLayoutPlan;
	private Document uploadPreviouslyLayoutPlan;

	private String encumburance;
	private String encumburanceOther;
	private String litigation;
	private String litigationRemark;;
	private Document litigationDoc;

	private String court;
	private String courtyCaseNo;
	private Document courtDoc;

	private String insolvency;
	private String insolvencyRemark;
	private Document insolvencyDoc;

	private String appliedLand;
	private Document appliedLandDoc;

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
	private Document orderUpload;
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

	private Document landSchedule;
	@JsonProperty("mutation")
	private Document mutation;
	@JsonProperty("jambandhi")
	private Document jambandhi;
	@JsonProperty("detailsOfLease")
	private Document detailsOfLease;
	@JsonProperty("addSalesDeed")
	private Document addSalesDeed;
	@JsonProperty("revisedLandSchedule")
	private Document revisedLandSchedule;
	@JsonProperty("copyofSpaBoard")
	private Document copyofSpaBoard;
	@JsonProperty("copyOfShajraPlan")
	private Document copyOfShajraPlan;
//	private String LayoutPlan;
	@JsonProperty("proposedLayoutPlan")
	private Document proposedLayoutPlan;
	@JsonProperty("revisedLansSchedule")
	private Document revisedLansSchedule;

}
