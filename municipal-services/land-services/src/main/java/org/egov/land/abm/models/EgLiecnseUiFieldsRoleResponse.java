package org.egov.land.abm.models;

import java.util.List;

import org.egov.common.contract.request.RequestInfo;
import org.egov.common.contract.response.ResponseInfo;
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
public class EgLiecnseUiFieldsRoleResponse {

	private ResponseInfo responseInfo = null;
	private List<EgLiecnseUiFieldsRole> egLiecnseUiFieldsRole;
	
}
