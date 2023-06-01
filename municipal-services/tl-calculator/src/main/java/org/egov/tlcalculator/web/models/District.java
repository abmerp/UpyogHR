package org.egov.tlcalculator.web.models;

import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class District {

	private String value;
	private String label;
	private String distCodeTCP;
	private List<Tenant> applicationTenantId;

}
