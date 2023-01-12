package org.egov.tl.web.models;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

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
@Table(name="eg_electric_plan")
public class ElectricPlan {

	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private Long id;
	
	private String loiNumber;
	private String selfCentredDrawing;
	private String shapFileTemplate;
	private boolean electricalInfra;
	private boolean elecricDistribution;
	private boolean electricalCapacity;
	private boolean switchingStation;
	private boolean loadSancation;
	private String environmentalClearance;
	private String autoCad;
	private String pdfFormat;
	private String verifiedPlan;
	private String tenantID;
	private String applicationNumber;
	private String businessService;
	private String action;
	private String status;
	private String comment;
	
	@Transient
	 private AuditDetails auditDetails;
	
	
	 private String createdBy = null;

	    
     private String lastModifiedBy = null;

     
     private Long createdTime = null;

     
     private Long lastModifiedTime = null;
	
	
	
}
