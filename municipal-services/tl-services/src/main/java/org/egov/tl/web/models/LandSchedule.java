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

	private String approachable;
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

	private String land;
	private String utilityWidth;
	private String utilityRemark;

	private String landSchedule;
	private String mutation;
	private String jambandhi;
	private String detailsOfLease;
	private String addSalesDeed;
	private String revisedLandSchedule;
	private String copyofSpaBoard;
	private String copyOfShajraPlan;
	private String LayoutPlan;
	private String proposedLayoutPlan;
	private String revisedLansSchedule;
	@JsonProperty("document")
	private List<Document> document;

}
