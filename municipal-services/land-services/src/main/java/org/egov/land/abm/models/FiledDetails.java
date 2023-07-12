package org.egov.land.abm.models;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor	
public class FiledDetails {
	private String name;
	private String value;
	private String remarks;
	private String isApproved;
	private String docId;
}
