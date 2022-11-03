package org.egov.land.abm.models;

import java.util.Date;

import org.egov.land.abm.newservices.pojo.NewServiceInfoData;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter

public class NewServiceInfoModel {

	private Long id;

	private float currentVersion;

	private String createdBy;

	private Date createdDate;

	private String updateddBy;

	private Date updatedDate;		

	private String pageName;
	private String applicationStatus;

	private NewServiceInfoData newServiceInfoData;
}
