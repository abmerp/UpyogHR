package org.egov.lndcalculator.web.models;

import java.math.BigDecimal;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
	
@Data
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class FeeAndBillingSlabIds {

    @JsonProperty("id")
    private String id;

    @JsonProperty("fee")
    private BigDecimal fee;
    
    
    @JsonProperty("scrutinyFeeCharges")
    private double scrutinyFeeCharges;
    
    @JsonProperty("licenseFeeCharges")
	private double licenseFeeCharges;
    
    @JsonProperty("conversionCharges")
	private double conversionCharges;
    
    @JsonProperty("externalDevelopmentCharges")
	private double externalDevelopmentCharges;
    
    @JsonProperty("stateInfrastructureDevelopmentCharges")
	private double stateInfrastructureDevelopmentCharges;


}
