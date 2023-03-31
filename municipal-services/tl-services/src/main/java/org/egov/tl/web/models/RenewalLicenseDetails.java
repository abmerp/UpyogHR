package org.egov.tl.web.models;

import java.sql.Date;
import java.util.List;

import javax.persistence.Column;

import org.egov.tl.web.models.TradeLicenseDetail.ChannelEnum;

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
public class RenewalLicenseDetails {
	
	@JsonProperty("ver")
	private float ver;
	
	@JsonProperty("renewalLicense")
	private List<RenewalLicense> renewalLicense;
	
	@JsonProperty("renewalLicenseEDC")
	private List<RenewalLicenseEDC> renewalLicenseEDC;
	
	@JsonProperty("renewalLicenseBG")
	private List<RenewalLicenseBG> renewalLicenseBG;
	
	@JsonProperty("renewalLicenseSIDC")
	private List<RenewalLicenseSIDC> renewalLicenseSIDC;
		
	@JsonProperty("renewalLicenseSOC")
	private List<RenewalLicenseSOC>renewalLicenseSOC;
		
	@JsonProperty("renewalLicenseSPC")
	private List<RenewalLicenseSPC> renewalLicenseSPC;
	
	@JsonProperty("previopusCondition")
	private List<RenewalLicensePreviopusCondition> previopusCondition;
	
}
