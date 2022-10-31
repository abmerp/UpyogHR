package org.egov.user.abm.developer.contract;

import java.util.List;

import org.egov.user.web.contract.UserRequest;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DevDetail {
	

	private LicenceDetails licenceDetails;
	
	  private AddInfo addInfo;
	 // private UploadDocument uploadDocument;
	  private List<AddRemoveAuthoizedUsers> aurthorizedUserInfoArray; 
	  private CapcityDevelopAColony capacityDevelopAColony;
	 

}
