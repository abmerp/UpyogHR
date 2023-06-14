package org.egov.land.abm.newservices.entity;

import java.io.Serializable;
import java.sql.Time;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * The persistent class for the eg_scrutiny database table.
 * 
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "eg_scrutiny")
//@NamedQuery(name="EgScrutiny.findAll", query="SELECT e FROM EgScrutiny e")
public class EgScrutiny implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@SequenceGenerator(name = "EG_SCRUTINY_ID_GENERATOR", sequenceName = "EG_SCRUTINY_SEQ")
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "EG_SCRUTINY_ID_GENERATOR")
	private Integer id;

	@Column(name = "application_id")
	private String applicationId;

	@Column(name = "comment")
	private String comment;

	@Column(name = "created_on")
	private Time createdOn;

	@Column(name = "field_value", length = 1000)
	private String fieldValue;

	@Column(name = "name")
	private String name;

	@Column(name = "\"field-d\"", length = 250)
	private String fieldIdL;

	@Column(name = "is_approved")
	private String isApproved;
	@Column(name = "userid")
	private Integer userid;
	@Column(name = "role")
	private String role;

	private Integer serviceId;
	@Column(name = "IsLOIPart")
	private Boolean isLOIPart;

	@Column(name = "document_id")
	private String documentId;

	@Column(name = "ts")
	private Time ts;

	@Column(name = "is_final_approval")
	private boolean isFinalApproval;

	@Column(name = "bussiness_service_name")
	private String bussinessServiceName;
	@Column(name = "designation")
	private String designation;
	@Column(name = "employee_name")
	private String employeeName;
	@Column(name = "application_status")
	private String applicationStatus;
}