package org.egov.tl.service.dao;

import java.util.Date;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import org.egov.tl.web.models.LicenseDetails;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;

import com.vladmihalcea.hibernate.type.json.JsonBinaryType;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@TypeDef(name = "jsonb", typeClass = JsonBinaryType.class)
@Table(name = "eg_license")
public class LicenseServiceDao {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(name = "current_version")
	private float currentVersion;

	@Column(name = "created_by", length = 100)
	private String createdBy;

	@Column(name = "created_date")
	private Date createdDate;

	@Column(name = "updated_by", length = 100)
	private String updateddBy;

	@Column(name = "updated_date")
	private Date updatedDate;

	@Column(name = "application_Status")
	private String applicationStatus;

	@Column(name = "applicationNumber")
	private String applicationNumber;

	@Column(name = "dairyNumber")
	private String dairyNumber;

	@Column(name = "caseNumber")
	private String caseNumber;

	@Column(name = "loiNumber")
	private String loiNumber;
	@Column(name = "tenantId")
	private String tenantId;
	@Type(type = "jsonb")
	@Column(columnDefinition = "jsonb")
	private List<LicenseDetails> newServiceInfoData;

}
