package org.egov.tlcalculator.web.models;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
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
