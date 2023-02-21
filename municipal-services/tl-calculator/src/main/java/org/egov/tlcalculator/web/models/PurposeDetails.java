package org.egov.tlcalculator.web.models;

import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PurposeDetails {

	private String name;
	private String code;
	private String area;
	private String far;
	private List<PurposeDetails> purposeDetail;

}
