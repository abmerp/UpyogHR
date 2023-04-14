package org.egov.tl.service;

import java.sql.Timestamp;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.egov.common.contract.request.RequestInfo;
import org.egov.tl.abm.repo.CompletionCertificateRepo;
import org.egov.tl.config.TLConfiguration;
import org.egov.tl.service.repo.LicenseServiceRepo;
import org.egov.tl.util.LandUtil;
import org.egov.tl.util.TradeUtil;
import org.egov.tl.validator.LandMDMSValidator;
import org.egov.tl.web.models.AuditDetails;
import org.egov.tl.web.models.CompletionCertificate;
import org.egov.tl.web.models.CompletionCertificateRequest;
import org.egov.tl.web.models.CompletionCertificateResponse;
import org.egov.tl.web.models.TradeLicense;
import org.egov.tl.web.models.TradeLicenseRequest;
import org.egov.tl.workflow.WorkflowIntegrator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import lombok.extern.log4j.Log4j2;

@Service
@Log4j2
public class CompletionCertificateService {
	
	private static final String COMPLETION_CERTIFICATE_WORKFLOWCODE = "COMPLETION_CERTIFICATE";
	private static final String WFTENANTID = "hr";

	
	
	@Autowired
	private LandUtil landUtil;

	@Autowired
	private RestTemplate rest;

	@Autowired
	private TLConfiguration config;

	@Autowired
	private LandMDMSValidator valid;
	
	@Autowired
	private TradeUtil tradeUtil;

	@Autowired
	private ThirPartyAPiCall thirPartyAPiCall;

	@Autowired
	LicenseServiceRepo newServiceInfoRepo;
	
	@Autowired
	private CompletionCertificateRepo completionCertificateRepo;
	
	@Autowired
	private ChangeBeneficialService changeBeneficialService;
	
	@Autowired
	private ServicePlanService servicePlanService;
	
	@Autowired
	private WorkflowIntegrator workflowIntegrator;
	

	public CompletionCertificateResponse createCompletionCertificate(CompletionCertificateRequest completionCertificateRequest){
		CompletionCertificateResponse completionCertificateResponse = null;
		String licenseNumber=completionCertificateRequest.getCompletionCertificate().get(0).getLicenseNumber();
		
		List<TradeLicense> tradeLicense = completionCertificateRepo.getLicenseByLicenseNumber(licenseNumber,completionCertificateRequest.getRequestInfo().getUserInfo().getId());
		if(tradeLicense==null||tradeLicense.isEmpty()) {
			completionCertificateResponse = CompletionCertificateResponse.builder().completionCertificate(null)
					.requestInfo(null).message("This Application Number has expaired or Application Number is not existing").status(false).build();
		}else if(tradeLicense.get(0).getTradeLicenseDetail().getLicenseFeeCharges()==null) {
			completionCertificateResponse = CompletionCertificateResponse.builder()
					.completionCertificate(null).requestInfo(null).message("licence fees is null of this Application").status(false).build();
	    }else {
	    	
	    	CompletionCertificate CompletionCertificateCheck=completionCertificateRepo.getCompletionCertificateByLicenseNumber(licenseNumber);
	    	if(CompletionCertificateCheck!=null) {
	    		if(CompletionCertificateCheck.getApplicationStatus()==1) {
	    			completionCertificateResponse=createCompletionCertificate(completionCertificateRequest,CompletionCertificateCheck,false);
	    		}else {
	    			completionCertificateResponse=createCompletionCertificate(completionCertificateRequest,null,true);
	    		}
	    	
	    	} else {
	    		completionCertificateResponse=createCompletionCertificate(completionCertificateRequest,null,true);
	    	}
	    	
        }
	   return completionCertificateResponse;

	}


	private CompletionCertificateResponse createCompletionCertificate(CompletionCertificateRequest completionCertificateRequest,CompletionCertificate completionCertificateData,boolean isCreate){
		CompletionCertificateResponse completionCertificateResponse=null;
		List<CompletionCertificate> completionCertificate = (List<CompletionCertificate>) completionCertificateRequest.getCompletionCertificate()
				.stream().map(certificate -> {
					String applicationNumberCC = servicePlanService.getIdList(completionCertificateRequest.getRequestInfo(), "hr",
							config.getCompletionCertificateName(), config.getCompletionCertificateFormat(), 1).get(0);
					Long time = System.currentTimeMillis();
					AuditDetails auditDetails = null;
					if(isCreate) {
						auditDetails=AuditDetails.builder().createdBy(completionCertificateRequest.getRequestInfo().getUserInfo().getUuid()).createdTime(time).build();
						certificate.setId(UUID.randomUUID().toString());
						certificate.setWorkFlowCode(COMPLETION_CERTIFICATE_WORKFLOWCODE);
						certificate.setApplicationStatus(1);
						certificate.setCreatedDate(new Timestamp(time));
						certificate.setFullPaymentDone(false);
						certificate.setApplicationNumber(applicationNumberCC);
						certificate.setCreatedTime(time);
					}else {
						auditDetails=completionCertificateData.getAuditDetails();
						auditDetails.setLastModifiedBy(completionCertificateRequest.getRequestInfo().getUserInfo().getUuid());
						auditDetails.setLastModifiedTime(time);
					}
					certificate.setAuditDetails(auditDetails);
					
					if(certificate.getIsDraft()==null) {
						certificate.setIsDraft("0");	
					}else {
						certificate.setIsDraft("1");
					}					
					return certificate;
				}).collect(Collectors.toList());	
		   completionCertificateRequest.setCompletionCertificate(completionCertificate);
		
		if(isCreate) {
			List<String> assignee=Arrays.asList(servicePlanService.assignee("CTP_HR", WFTENANTID, true, completionCertificateRequest.getRequestInfo()));
			TradeLicenseRequest prepareProcessInstanceRequest=changeBeneficialService.prepareProcessInstanceRequest(WFTENANTID,COMPLETION_CERTIFICATE_WORKFLOWCODE,"INITIATE",assignee,completionCertificate.get(0).getApplicationNumber(),COMPLETION_CERTIFICATE_WORKFLOWCODE,completionCertificateRequest.getRequestInfo());
			workflowIntegrator.callWorkFlow(prepareProcessInstanceRequest);	
		    completionCertificateRepo.save(completionCertificateRequest);
		    completionCertificateResponse = CompletionCertificateResponse.builder().completionCertificate(completionCertificate)
					.requestInfo(completionCertificateRequest.getRequestInfo()).message("Records has been inserted successfully.").status(true).build();
		} else {
			completionCertificateRepo.update(completionCertificateRequest);
			completionCertificateResponse = CompletionCertificateResponse.builder().completionCertificate(completionCertificate)
					.requestInfo(completionCertificateRequest.getRequestInfo()).message("Records has been updated successfully.").status(true).build();
		}
		return completionCertificateResponse;
	}
	
	
	public CompletionCertificateResponse getCompletionCertificate(RequestInfo requestInfo,String applicationNumber,String licenseNumber){
		CompletionCertificateResponse completionCertificateResponse = null;
		List<CompletionCertificate> CompletionCertificateDetails = null;
		
		if(applicationNumber==null&&licenseNumber==null) {
			completionCertificateResponse = CompletionCertificateResponse.builder().completionCertificate(null)
						.requestInfo(requestInfo).message("Application Number or License Number both can't be null.").status(false).build();
		}else {
			try {
				if(applicationNumber==null) {
					CompletionCertificateDetails=completionCertificateRepo.searcherBeneficialDetailsByLicenceNumberList(licenseNumber);
				}else {
					CompletionCertificateDetails=completionCertificateRepo.getBeneficialDetailsByApplicationNumberList(applicationNumber);
				}
				
			} catch (Exception e1) {
				e1.printStackTrace();
			}
		}
		if(CompletionCertificateDetails!=null) {
			completionCertificateResponse = CompletionCertificateResponse.builder().completionCertificate(CompletionCertificateDetails)
				.requestInfo(requestInfo).message("Fetched success").status(true).build();
		}else {
			completionCertificateResponse = CompletionCertificateResponse.builder().completionCertificate(null)
					.requestInfo(null).message("Record not found").status(false).build();
		}
		
		return completionCertificateResponse;
	}
	
	
}