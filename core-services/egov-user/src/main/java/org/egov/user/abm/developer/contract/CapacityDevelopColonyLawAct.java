package org.egov.user.abm.developer.contract;

import java.util.List;

import org.egov.user.domain.model.Document;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CapacityDevelopColonyLawAct {
	private List<Document> document;
	private int serialNumber;
	private String coloniesDeveloped;
	private String area;
	private String purpose;
	private String statusOfDevelopment;
	private String outstandingDues;

}
