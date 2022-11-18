package org.egov.user.abm.developer.contract;

import java.util.List;

import org.egov.user.domain.model.Document;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DirectorsInformation {

//	private int serialNumber;
//	private String DIN_Number;
//	private String name;
//	private String PAN_Number;
//	private String uploadPdf;
	private List<Document> document;
	private String contactNumber;
	private String din;
	private String name;

}
