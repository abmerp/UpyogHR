package org.egov.user.abm.developer.contract;

import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CapcityDevelopAColony {	
	
	//private String individualCertificateCA;
	private String companyBalanceSheet;
	private String paidUpCapital;
	private String networthPartners;
	private String networthFirm;
    private String permissionGrantedHRDU;
	//private String permissionGrantedLawAct;
	private String technicalExpert;
	private String designatedDirectors;
	private String alreadtObtainedLic;
	private String technicalCapacityOutsideHaryana;

	private List<CapacityDevelopAColonyHdruAct> capacityDevelopColonyHdruAct;
	private List<TechnicalExpertEngaged> technicalExpertEngaged;
	private List<DesignationDirector> designationDirector;
	private List<ObtainedLicense> obtainedLicense;	
	private TechnicalCapacityOutsideHaryanaDetails technicalCapacityOutsideHaryanaDetails;
	private Documents documents;
	private Boolean isUndertakenDR;
	
	//extra
//	private List<CapacityDevelopColonyLawAct> capacityDevelopColonyLawAct;
	
	

}
