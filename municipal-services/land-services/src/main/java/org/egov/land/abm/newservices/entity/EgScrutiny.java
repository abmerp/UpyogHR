package org.egov.land.abm.newservices.entity;

import java.io.Serializable;
import javax.persistence.*;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.sql.Time;


/**
 * The persistent class for the eg_scrutiny database table.
 * 
 */
@Getter
@Setter
@AllArgsConstructor
@Entity
@Table(name="eg_scrutiny")
@NamedQuery(name="EgScrutiny.findAll", query="SELECT e FROM EgScrutiny e")
public class EgScrutiny implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@SequenceGenerator(name="EG_SCRUTINY_ID_GENERATOR", sequenceName="EG_SCRUTINY_SEQ")
	@GeneratedValue(strategy=GenerationType.SEQUENCE, generator="EG_SCRUTINY_ID_GENERATOR")
	private Integer id;

	@Column(name="application_id")
	private Integer applicationId;

	private String comment;

	@Column(name="created_on")
	private Time createdOn;

	@Column(name="field_value")
	private String fieldValue;

	@Column(name="\"field-d\"")
	private Integer field_d;

	@Column(name="is_approved")
	private Boolean isApproved;

	private Integer userid;

	public EgScrutiny() {
	}

	public Integer getId() {
		return this.id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Integer getApplicationId() {
		return this.applicationId;
	}

	public void setApplicationId(Integer applicationId) {
		this.applicationId = applicationId;
	}

	public String getComment() {
		return this.comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}

	public Time getCreatedOn() {
		return this.createdOn;
	}

	public void setCreatedOn(Time createdOn) {
		this.createdOn = createdOn;
	}

	public String getFieldValue() {
		return this.fieldValue;
	}

	public void setFieldValue(String fieldValue) {
		this.fieldValue = fieldValue;
	}

	public Integer getField_d() {
		return this.field_d;
	}

	public void setField_d(Integer field_d) {
		this.field_d = field_d;
	}

	public Boolean getIsApproved() {
		return this.isApproved;
	}

	public void setIsApproved(Boolean isApproved) {
		this.isApproved = isApproved;
	}

	public Integer getUserid() {
		return this.userid;
	}

	public void setUserid(Integer userid) {
		this.userid = userid;
	}

}