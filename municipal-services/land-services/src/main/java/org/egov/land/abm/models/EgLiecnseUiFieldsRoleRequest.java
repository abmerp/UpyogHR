package org.egov.land.abm.models;

import org.egov.common.contract.request.RequestInfo;
import org.egov.land.abm.newservices.entity.EgLiecnseUiField;
import org.egov.land.abm.newservices.entity.EgLiecnseUiFieldsRole;

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
public class EgLiecnseUiFieldsRoleRequest {

	private RequestInfo requestInfo = null;
	private EgLiecnseUiFieldsRole egLiecnseUiFieldsRole;
	
}
