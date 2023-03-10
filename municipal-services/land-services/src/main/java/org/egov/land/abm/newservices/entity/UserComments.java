package org.egov.land.abm.newservices.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UserComments {
	private String designation;
	private String employeeName;
	private String remarks;
	private String isApproved;
}
