package org.egov.tl.web.models;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class GenerateLoiNumberResponse {
	
	private String dispatchId;
	private String dispatchNo;
	private String dispatchedDate;
	private String dispatchedTo;
	private String type;
	private String subject;
	private String description;
	private String relateddiaryNo;
	private String tblApplicationId;
	private String fileId;

}
