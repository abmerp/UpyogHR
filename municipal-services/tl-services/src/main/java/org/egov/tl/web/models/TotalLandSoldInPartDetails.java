package org.egov.tl.web.models;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
public class TotalLandSoldInPartDetails {
	
	@JsonProperty("totalLandSoldInPart")
	private TotalLandSoldInPart totalLandSoldInPart;

}
