package org.egov.tl.web.models;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;

import lombok.Setter;

@Getter
@Setter

public class Compliance {
	@JsonProperty("compliance")
	private String compliance;
	@JsonProperty("isPartOfLoi")
	private boolean isPartOfLoi;
	@JsonProperty("userName")
	private String userName;
	@JsonProperty("userId")
	private String userId;
	@JsonProperty("designation")
	private String designation;
	@JsonProperty("created_On")
	private String created_On;

}
