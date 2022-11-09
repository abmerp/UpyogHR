package org.egov.lndcalculator.web.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
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

    @JsonProperty("billingSlabIds")
    private List<String> billingSlabIds;

}
