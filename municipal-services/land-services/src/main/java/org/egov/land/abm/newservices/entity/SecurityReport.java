package org.egov.land.abm.newservices.entity;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class SecurityReport {

	private String name;
	private String value;
	private List<UserComments> employees;
	
}
