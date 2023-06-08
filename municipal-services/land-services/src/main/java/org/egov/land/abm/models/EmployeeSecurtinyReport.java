package org.egov.land.abm.models;

import java.sql.Time;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class EmployeeSecurtinyReport  {
	private Integer id;
	private String designation;
	private String role;
	private String employeeName;
	private Time createdOn;
	private String applicationStatus;
	private String userID;
	private Date ts;
	private List<FiledDetails> approvedfiledDetails;
	private List<FiledDetails> disApprovedfiledDetails;
	private List<FiledDetails> condApprovedfiledDetails;
	private List<FiledDetails> performaFieldDetail;
	private List<FiledDetails> notingDetail;

	

}
