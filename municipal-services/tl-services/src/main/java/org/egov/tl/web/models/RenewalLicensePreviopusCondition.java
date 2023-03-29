package org.egov.tl.web.models;

import java.math.BigInteger;
import java.sql.Date;

import javax.persistence.Column;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonNode;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RenewalLicensePreviopusCondition {
		
	@JsonProperty("id")
	private int id;
	
	@JsonProperty("condition")
	private String condition;
	 
	@JsonProperty("complianceDone")
	private boolean complianceDone;
}
