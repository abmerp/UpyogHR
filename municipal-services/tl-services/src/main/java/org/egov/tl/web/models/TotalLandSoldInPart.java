package org.egov.tl.web.models;

import java.sql.Timestamp;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@Getter
@Setter
@NoArgsConstructor
@Builder
public class TotalLandSoldInPart {
	
	@JsonProperty("kashraNo")
	private String kashraNo;
	
	@JsonProperty("area")
	private String area;


}
