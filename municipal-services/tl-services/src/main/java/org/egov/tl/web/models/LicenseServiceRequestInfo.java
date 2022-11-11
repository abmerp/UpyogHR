package org.egov.tl.web.models;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter

public class LicenseServiceRequestInfo {

	private Long id;

	private float currentVersion;

	private String createdBy;

	private Date createdDate;

	private String updateddBy;

	private Date updatedDate;		

	private String pageName;
	private String applicationStatus;
	@JsonProperty("LicenseDetails")
	private LicenseDetails licenseDetails;
}
