package org.egov.user.abm.developer.contract;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class SsoCitizen {

	private String userId;
	private String emailId;	
	private String mobileNumber;
	private String returnUrl;
	private String redirectUrl;
	private String tenantId;
	private String tokenId;

}
