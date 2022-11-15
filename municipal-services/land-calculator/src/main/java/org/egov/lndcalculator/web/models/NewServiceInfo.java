package org.egov.lndcalculator.web.models;

import java.util.Date;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class NewServiceInfo {

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

	private NewServiceInfoData	 newServiceInfoData;

}
