package org.egov.tl.web.models;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.List;

@Data
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
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
