package org.egov.tl.web.models.bankguarantee;

import java.math.BigDecimal;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class MortgagePlotDetails {

	@JsonProperty("plotNumber")
	private String plotNumber;

	@JsonProperty("areaInSqMtrs")
	private BigDecimal areaInSqMtrs;
}
