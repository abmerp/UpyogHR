package org.egov.tl.web.models;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class RenewalLicenseBG {
	
    @JsonProperty("BG_amount")
	private boolean BG_amount;

    @JsonProperty("BG_validity")
	private boolean BG_validity;

    @JsonProperty("BG_bank")
	private boolean BG_bank;

    @JsonProperty("BG_component")
	private boolean BG_component;

}
