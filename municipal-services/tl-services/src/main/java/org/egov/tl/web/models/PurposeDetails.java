package org.egov.tl.web.models;

import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PurposeDetails {

	private String id;
	private String name;
	private String code;
	private String area;
	private List<String> fars;
	private String far;
	private String maxPercentage;
	private String minPercentage;
	private List<PurposeDetails> purposeDetail;

}
