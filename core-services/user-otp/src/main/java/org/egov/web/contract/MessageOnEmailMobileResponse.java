package org.egov.web.contract;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Setter
@Builder
public class MessageOnEmailMobileResponse {
     private String message;
	 private Object body;
     private boolean status;
}



