package org.egov.land.abm.newservices.entity;

import java.sql.Time;
import java.util.Date;

import javax.persistence.Column;

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
	private String role;
	private String employeeName;
	private String remarks;
	private String isApproved;
	private Time createdOn;
	private Date ts;
}
