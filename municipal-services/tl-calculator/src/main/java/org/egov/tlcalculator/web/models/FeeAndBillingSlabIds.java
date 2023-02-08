package org.egov.tlcalculator.web.models;

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
    private BigDecimal scrutinyFeeCharges;
    
    @JsonProperty("licenseFeeCharges")
	private BigDecimal licenseFeeCharges;
    
    @JsonProperty("conversionCharges")
	private BigDecimal conversionCharges;
    
    @JsonProperty("externalDevelopmentCharges")
	private BigDecimal externalDevelopmentCharges;
    
    @JsonProperty("stateInfrastructureDevelopmentCharges")
	private BigDecimal stateInfrastructureDevelopmentCharges;

    @JsonProperty("billingSlabIds")
    private List<String> billingSlabIds;

}
