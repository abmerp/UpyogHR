package org.egov.tlcalculator.web.models;

import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DetailsAppliedLandPlot {

	
	private String totalAreaScheme;
	private String areaUnderSectorRoad;
	private String balanceAreaAfterDeduction;
	private String areaUnderUndetermined;
	private String areaUnderGH;
	private String balanceArea;
	private String areaUnderSectorAndGreenBelt;
	private String netPlannedArea;
	private String totalNumberOfPlots;
	private String generalPlots;
	private String requiredNPNLPlots;
	private String requiredEWSPlots;
	private String permissibleDensity;
	private String permissibleCommercialArea;
	private String underPlot;
	private String commercial;
	private String permissibleSaleableArea;
	private String requiredGreenArea;
	private List<DetailOfCommunitySite> detailOfCommunitySites;
	private String provided;
	private String layoutPlanPdf;
	private String layoutPlanDxf;
	private String layoutPlanZip;
	private String undertaking;
	// ----add new fields-----//
	private String totalSiteArea;
	private String groundCoverage;
	private String FAR;
	private String parkingSpace;
	private String providedArea;
	private String permissableGroundCoverage;
	private String permissableFAR;
	private String permissableCommercial;

}
