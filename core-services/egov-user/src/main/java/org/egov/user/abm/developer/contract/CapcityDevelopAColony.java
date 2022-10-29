package org.egov.user.abm.developer.contract;

import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CapcityDevelopAColony {

	private List<CapacityDevelopAColonyHdruAct> capacityDevelopColonyHdruAct;	
	private List<CapacityDevelopColonyLawAct> capacityDevelopColonyLawAct;	
	private TechnicalExpertEngaged technicalExpertEngaged;
	private DesignationDirector designationDirector;
	private ObtainedLicense obtainedLicense;
	
}
