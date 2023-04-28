package org.egov.tl.abm.newservices.contract;

import java.util.List;

import org.egov.common.contract.request.RequestInfo;
import org.egov.tl.web.models.ExtensionOfCLUPermission;

import com.fasterxml.jackson.annotation.JsonProperty;

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
public class ExtensionOfCLUPermissionContract {
	
	@JsonProperty("RequestInfo")
	private RequestInfo requestInfo;
	
	
    @JsonProperty("ExtensionOfCLUPermission")
    private List<ExtensionOfCLUPermission> extensionOfCLUPermission;

}
