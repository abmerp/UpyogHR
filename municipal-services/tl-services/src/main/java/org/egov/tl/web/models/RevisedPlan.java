package org.egov.tl.web.models;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonNode;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RevisedPlan implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private String id;

	private String licenseNo;

	private String existingArea;

	private String areaPlanning;

	private String anyOtherFeature;

	private BigDecimal amount;

	private String reasonRevision;

	private String earlierApprovedlayoutPlan;
	@JsonProperty("auditDetails")
	private AuditDetails auditDetails;
	private String applicationNumber;
    
    private String tenantId;
    
    private String action;
    
    private String status;
    
    private String businessService;
    
    private String comment;
     
    private String workflowCode;
    
    private JsonNode additionalDetails;
    
    private List<String> assignee;
    
    @JsonProperty("wfDocuments")
	private List<Document> wfDocuments;
    
    private JsonNode feesCharges;
    
    private JsonNode feesResult;
}
