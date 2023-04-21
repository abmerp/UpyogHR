package org.egov.tl.web.models;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
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
	@JsonProperty("comment")
	private String comment;

}
