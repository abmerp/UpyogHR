package org.egov.tl.web.models;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LicensesDoc {
	
	private String comment;
    private String id;
    private String tenantId;
    private String businessService;
    private String licenseType;
    private String applicationType;
    private String workflowCode;
    private String licenseNumber;
    private String applicationNumber;
    private String tradeName;
    private String applicationDate;
    private String issuedDate;
    private String financialYear;
    private String validFrom;
    private String validTo;
    private String action;
    private String assignee;
    private String wfDocuments;
    private String status;
	private ApplicationDocuments applicationDocuments;

	private String articlesOfAssociation;
	private String memorandumOfArticles;
	private String registeredIrrevocablePaternshipDeed;
	private String affidavitAndPancard;
	private String anyOtherDoc;
}
