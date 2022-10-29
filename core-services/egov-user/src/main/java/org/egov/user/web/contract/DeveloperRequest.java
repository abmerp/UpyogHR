package org.egov.user.web.contract;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.*;

import org.egov.common.contract.request.RequestInfo;
import org.egov.user.config.*;
import org.egov.user.domain.model.Address;
import org.egov.user.domain.model.Role;
import org.egov.user.domain.model.User;
import org.egov.user.domain.model.enums.*;
import org.hibernate.validator.constraints.Email;
import org.hibernate.validator.constraints.SafeHtml;
import org.springframework.beans.factory.annotation.Value;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

@Setter
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DeveloperRequest {

	//@JsonProperty("RequestInfo")
	//private RequestInfo requestInfo;
	@JsonProperty("name")
	private String name;
}
