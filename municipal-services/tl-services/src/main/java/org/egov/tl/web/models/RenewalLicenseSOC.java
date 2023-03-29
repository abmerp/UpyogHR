package org.egov.tl.web.models;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class RenewalLicenseSOC {

	
	@JsonProperty("socDateOfGrantOfOC")
	private String socDateOfGrantOfOC;
	
	@JsonProperty("socTower")
	private String socTower;
	
	@JsonProperty("socTargetDateForfillingDOD")
	private String socTargetDateForfillingDOD;
	
	@JsonProperty("socDodFiledOrNOT")
	private String socDodFiledOrNOT;
	
	@JsonProperty("socDodFilledWithInTimeOrDely")
	private String socDodFilledWithInTimeOrDely;
	
	@JsonProperty("socDodFilledDelayTime")
	private String socDodFilledDelayTime;
	
	@JsonProperty("socRemark")
	private String socRemark;
	
	@JsonProperty("socUploadLetterFromDepartmentSize")
	private String socUploadLetterFromDepartmentSize;
}
