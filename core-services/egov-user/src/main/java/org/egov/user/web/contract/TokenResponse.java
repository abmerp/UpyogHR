package org.egov.user.web.contract;

import org.egov.common.contract.response.ResponseInfo;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TokenResponse {
	 private String access_token;
	 private String token_type;
	 private String refresh_token;
	 private String expires_in;
	 private String scope;
	 @JsonProperty("ResponseInfo")
	 private TokenResponseInfo responseInfo;
	 @JsonProperty("UserRequest")
	 private UserRequest userRequest;
	 
}
