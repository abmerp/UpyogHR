package org.egov.lndcalculator.web.models.tradelicense;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

import org.egov.lndcalculator.web.models.FeeAndBillingSlabIds;
import org.egov.lndcalculator.web.models.demand.TaxHeadEstimate;

@Data
public class EstimatesAndSlabs {

    @JsonProperty("estimates")
    private List<TaxHeadEstimate> estimates;

    @JsonProperty("tradeTypeFeeAndBillingSlabIds")
    private FeeAndBillingSlabIds tradeTypeFeeAndBillingSlabIds;

    @JsonProperty("accessoryFeeAndBillingSlabIds")
    private FeeAndBillingSlabIds accessoryFeeAndBillingSlabIds;



}
