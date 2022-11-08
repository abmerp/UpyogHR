package org.egov.land.abm.contract;

import org.egov.common.contract.request.RequestInfo;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ElectricPlanContract {

	private RequestInfo requestInfo = null;
	private ElectricPlanRequest electricPlanRequest;
}
