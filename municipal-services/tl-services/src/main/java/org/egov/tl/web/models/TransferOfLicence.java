package org.egov.tl.web.models;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class TransferOfLicence {
	
	private String selectType;
	private String areaInAcres;
	private String licenceTransferredFromLandOwn;
	private String transferredTitleOfLand;
	private String changeOfDeveloper;
	private String amount;
	private String affidavitForLicencedArea;
	private String colonizerSeekingTransferLicence;
	private String consentLetterDoc;
	private String boardResolutionDoc;
	private String noObjectionCertificate;
	private String technicalAndFinancialCapacityDoc;
	private String affidavitOfAdmCharges;
	private String justificationForRequest;
	private String affidavitFixedChargesForAdm;
	private String thirdPartyCreationStatus;
	private String registrationProjectRera;
	private String anyOtherDoc;
}
