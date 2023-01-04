package org.egov.tl.web.models;

import java.io.Serializable;
import java.sql.Time;
import java.util.Date;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.validation.constraints.Size;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Builder
@Table(name = "eg_service_plan")
public class ServicePlan implements Serializable {
	private static final long serialVersionUID = 1L;
	@Id
	private String loiNumber;
	private boolean undertaking;
	private boolean selfCertifiedDrawingsFromCharetedEng;
	private String selfCertifiedDrawingFromEmpaneledDoc;
	private String environmentalClearance;
	private String shapeFileAsPerTemplate;
	private String autoCadFile;
	private String certifieadCopyOfThePlan;
	private String assignee;
	private String action;
	private String status;
	private String businessService;
	private String comment;
	private String tenantID;
	private String applicationNumber;
	@Transient
	 private AuditDetails auditDetails;
	 
     private String createdBy = null;

    
     private String lastModifiedBy = null;

     
     private Long createdTime = null;

     
     private Long lastModifiedTime = null;

}
