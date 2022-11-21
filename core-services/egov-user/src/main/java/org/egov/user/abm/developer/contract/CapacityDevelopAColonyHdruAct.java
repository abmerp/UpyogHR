package org.egov.user.abm.developer.contract;

import java.util.List;

import org.egov.user.domain.model.Document;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CapacityDevelopAColonyHdruAct {
	private List<Document> document;
	private String licenceNumber;
	private String nameOfDeveloper;
	private String purposeOfColony;
	private String sectorAndDevelopmentPlan;
	private String validatingLicence;

}
