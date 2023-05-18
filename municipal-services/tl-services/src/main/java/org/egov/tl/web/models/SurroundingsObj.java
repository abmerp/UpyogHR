package org.egov.tl.web.models;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SurroundingsObj {
	
	@JsonProperty("east")
	private String east;
	@JsonProperty("north")
	private String north;
	@JsonProperty("pocketName")
	private String pocketName;
	@JsonProperty("south")
	private String south;
	@JsonProperty("west")
	private String west;

}
