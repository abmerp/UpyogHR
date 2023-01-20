package org.egov.user.web.contract;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TokenResponseInfo {
	private String api_id;
	private String ver;
	private String ts;
	private String res_msg_id;
	private String msg_id;
	private String status;
}
