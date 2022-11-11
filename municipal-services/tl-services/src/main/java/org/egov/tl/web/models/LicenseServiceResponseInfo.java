package org.egov.tl.web.models;

import java.util.Date;
import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter

public class LicenseServiceResponseInfo {

	private Long id;

	private float currentVersion;

	private String createdBy;

	private Date createdDate;

	private String updateddBy;

	private Date updatedDate;

	private String application_Status;

	private String applicationNumber;

	private String dairyNumber;

	private String caseNumber;

	private List<LicenseDetails> newServiceInfoData;

}
