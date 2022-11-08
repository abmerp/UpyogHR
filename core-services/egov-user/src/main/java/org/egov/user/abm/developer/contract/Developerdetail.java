package org.egov.user.abm.developer.contract;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class Developerdetail {

	private float version;

	@JsonProperty
	private LicenceDetails licenceDetails;
	@JsonProperty
	private AddInfo addInfo;
	// private UploadDocument uploadDocument;
	@JsonProperty
	private List<AddRemoveAuthoizedUsers> aurthorizedUserInfoArray;
	@JsonProperty
	private CapcityDevelopAColony capacityDevelopAColony;

	/*
	 * public void setJsonData(String jsonData) { // Method parameter jsonData is
	 * simply ignored try { this.jsonData = new
	 * ObjectMapper().writeValueAsString(this); } catch (JsonProcessingException e)
	 * { log.error(e.getMessage()); } }
	 */

}
