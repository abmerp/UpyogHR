package org.egov.bpa.web.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OwnerDocs {

	private String formBRSI;
	private String formBRSII;
	private String formBRSV;
	private String anAfidavitFromOwner;
	private String certificateRegardingTheFunctionality;
	private String copyOfZoningPlan;
	private String ownershipDocuments;
	private String siteReport;
	private String structuralStabilityCertificate;
	private String copyOfSaleDeed;
	private String copyOfApprovedZoning;
	private String copyOfAffidavitClarifying;

}
