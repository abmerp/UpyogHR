package org.egov.land.abm.models;

import org.egov.common.contract.request.RequestInfo;
import org.egov.land.abm.newservices.entity.EgLiecnseUiField;
import org.egov.land.abm.newservices.entity.EgScrutiny;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class EgLiecnseUiFieldInfoRequest {

	private RequestInfo requestInfo = null;
	private EgLiecnseUiField egLiecnseUiField;
	
}
