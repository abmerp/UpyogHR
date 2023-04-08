package org.egov.tl.web.models;

import java.math.BigDecimal;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonNode;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ReviseLayoutPlan {
	 
    private String areaProposedRevision;
    private String areaCommercial;
    private String areaResidential;
    
    private String anyOtherRemarks;
    private String reasonRevisionLayoutPlanDoc;
    
    private String statusCreationAffidavitDoc;
    private String boardResolutionAuthSignatoryDoc;
    private String anyOther;
    private List<ExixtingAreaDetails> existingAreaDetails;
    private String existingArea;

	private String areaPlanning;

	private String anyOtherFeature;

	private BigDecimal amount;

	private String reasonRevision;

	private String earlierApprovedlayoutPlan;
}
