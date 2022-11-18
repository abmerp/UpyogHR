package org.egov.user.abm.developer.contract;

import java.util.List;

import org.egov.user.domain.model.Document;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ShareholdingPattens {
	
//	private int serialNumber;
//	private String name;
//	private String designition;
//	private String percentage;
//	private String uploadPdf;
	 private String name;
     private String designition;
     private String percentage;
     private String uploadPdf;
     private String serialNumber;
     private List<Document> document;

}
