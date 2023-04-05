package org.egov.tl.web.models;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class RenewalLicenseBG {
	
    @JsonProperty("bgAmount")
	private boolean bgamount;

    @JsonProperty("bgValidity")
	private boolean bgValidity;

    @JsonProperty("bgBank")
	private boolean bgBank;

    @JsonProperty("bgComponent")
	private boolean bgComponent;

}
