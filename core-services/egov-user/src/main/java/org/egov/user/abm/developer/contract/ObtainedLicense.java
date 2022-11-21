package org.egov.user.abm.developer.contract;

import java.util.List;

import org.egov.user.domain.model.Document;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ObtainedLicense {
	
	private String obtainedLiceneseYN;
    private String boardDocY;
    private String earlierDocY;
    private String boardDocN;
    private String earlierDocN;
    private List<Document> document;      

	
	
}
