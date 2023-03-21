package org.egov.tlcalculator.web.models.demand;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import javax.validation.Valid;

import org.egov.common.contract.request.User;
import org.egov.tlcalculator.web.models.AuditDetails;
import org.egov.tlcalculator.web.models.demand.Demand.StatusEnum;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@Valid
public class DemandRequiredParamater {
	
	private long taxPeriodFrom;
	private long taxPeriodTo;
	private String tenantId;
	private String consumerCode;
	private String consumerType;
	private String businessService;
	private BigDecimal taxAmount;
	

}
