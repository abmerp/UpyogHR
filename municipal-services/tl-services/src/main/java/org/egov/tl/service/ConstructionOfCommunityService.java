package org.egov.tl.service;

import java.sql.Timestamp;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.egov.common.contract.request.RequestInfo;
import org.egov.tl.abm.repo.CompletionCertificateRepo;
import org.egov.tl.abm.repo.ConstructionOfCommunityRepo;
import org.egov.tl.config.TLConfiguration;
import org.egov.tl.service.repo.LicenseServiceRepo;
import org.egov.tl.util.LandUtil;
import org.egov.tl.util.TradeUtil;
import org.egov.tl.validator.LandMDMSValidator;
import org.egov.tl.web.models.AuditDetails;
import org.egov.tl.web.models.CompletionCertificate;
import org.egov.tl.web.models.CompletionCertificateResponse;
import org.egov.tl.web.models.ConstructionOfCommunity;
import org.egov.tl.web.models.ConstructionOfCommunityRequest;
import org.egov.tl.web.models.ConstructionOfCommunityResponse;
import org.egov.tl.web.models.TradeLicense;
import org.egov.tl.workflow.WorkflowIntegrator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import lombok.extern.log4j.Log4j2;

@Service
@Log4j2
public class ConstructionOfCommunityService {
	
	private static final String CONSTRUCTION_OF_COMMUNITY_WORKFLOWCODE = "CONSTRUCTION_OF_COMMUNITY";
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
	private ConstructionOfCommunityRepo constructionOfCommunityRepo;
	
	
	@Autowired
	private ChangeBeneficialService changeBeneficialService;
	
	@Autowired
	private ServicePlanService servicePlanService;
	
	@Autowired
	private WorkflowIntegrator workflowIntegrator;
	

	public ConstructionOfCommunityResponse saveConstructionOfCommunity(ConstructionOfCommunityRequest constructionOfCommunityRequest){
		ConstructionOfCommunityResponse constructionOfCommunityResponse = null;
		String licenseNumber=constructionOfCommunityRequest.getConstructionOfCommunity().get(0).getLicenseNumber();
		
		List<TradeLicense> tradeLicense = completionCertificateRepo.getLicenseByLicenseNumber(licenseNumber,constructionOfCommunityRequest.getRequestInfo().getUserInfo().getId());
		if(tradeLicense==null||tradeLicense.isEmpty()) {
			constructionOfCommunityResponse = ConstructionOfCommunityResponse.builder().constructionOfCommunity(null)
					.requestInfo(null).message("This Application Number has expaired or Application Number is not existing").status(false).build();
		}else if(tradeLicense.get(0).getTradeLicenseDetail().getLicenseFeeCharges()==null) {
			constructionOfCommunityResponse = ConstructionOfCommunityResponse.builder()
					.constructionOfCommunity(null).requestInfo(null).message("licence fees is null of this Application").status(false).build();
	    }else {
	    	ConstructionOfCommunity constructionOfCommunity=constructionOfCommunityRepo.getConstructionOfCommunityByLicenseNumber(licenseNumber);
	    	if(constructionOfCommunity!=null) {
	    		if(constructionOfCommunity.getApplicationStatus()==1) {
	    			constructionOfCommunityResponse=createConstructionOfCommunity(constructionOfCommunityRequest,constructionOfCommunity,false);
	    		}else {
	    			constructionOfCommunityResponse=createConstructionOfCommunity(constructionOfCommunityRequest,null,true);
	    		}
	    	
	    	} else {
	    		constructionOfCommunityResponse=createConstructionOfCommunity(constructionOfCommunityRequest,null,true);
	    	} 
	    	
        }
	   return constructionOfCommunityResponse;

	}


	private ConstructionOfCommunityResponse createConstructionOfCommunity(ConstructionOfCommunityRequest constructionOfCommunityRequest,ConstructionOfCommunity constructionOfCommunity,boolean isCreate){
		ConstructionOfCommunityResponse constructionOfCommunityResponse=null;
		List<ConstructionOfCommunity> constructionOfCommunityList = (List<ConstructionOfCommunity>) constructionOfCommunityRequest.getConstructionOfCommunity()
				.stream().map(construction -> {
					String applicationNumberCC = servicePlanService.getIdList(constructionOfCommunityRequest.getRequestInfo(), "hr",
							config.getConstructionOfCommunityName(), config.getConstructionOfCommunityFormat(), 1).get(0);
					Long time = System.currentTimeMillis();
					AuditDetails auditDetails = null;
					if(isCreate) {
						auditDetails=AuditDetails.builder().createdBy(constructionOfCommunityRequest.getRequestInfo().getUserInfo().getUuid()).createdTime(time).build();
//						construction.setId(UUID.randomUUID().toString());
						construction.setWorkFlowCode(CONSTRUCTION_OF_COMMUNITY_WORKFLOWCODE);
						construction.setApplicationStatus(1);
						construction.setCreatedDate(new Timestamp(time));
						construction.setFullPaymentDone(false);
						construction.setApplicationNumber(applicationNumberCC);
						construction.setCreatedTime(time);
					}else {
						auditDetails=constructionOfCommunity.getAuditDetails();
						auditDetails.setLastModifiedBy(constructionOfCommunityRequest.getRequestInfo().getUserInfo().getUuid());
						auditDetails.setLastModifiedTime(time);
					}
					construction.setAuditDetails(auditDetails);
					
					if(construction.getIsDraft()==null) {
						construction.setIsDraft("0");	
					}else {
						construction.setIsDraft("1");
					}					
					return construction;
				}).collect(Collectors.toList());	
		constructionOfCommunityRequest.setConstructionOfCommunity(constructionOfCommunityList);
		
		if(isCreate) {
//			List<String> assignee=Arrays.asList(servicePlanService.assignee("CTP_HR", WFTENANTID, true, constructionOfCommunityRequest.getRequestInfo()));
//			TradeLicenseRequest prepareProcessInstanceRequest=changeBeneficialService.prepareProcessInstanceRequest(WFTENANTID,COMPLETION_CERTIFICATE_WORKFLOWCODE,"INITIATE",assignee,completionCertificate.get(0).getApplicationNumber(),COMPLETION_CERTIFICATE_WORKFLOWCODE,completionCertificateRequest.getRequestInfo());
//			workflowIntegrator.callWorkFlow(prepareProcessInstanceRequest);	
			constructionOfCommunityRepo.save(constructionOfCommunityRequest);
		    constructionOfCommunityResponse = ConstructionOfCommunityResponse.builder().constructionOfCommunity(constructionOfCommunityList)
					.requestInfo(constructionOfCommunityRequest.getRequestInfo()).message("Records has been inserted successfully.").status(true).build();
		} else {
			constructionOfCommunityRepo.update(constructionOfCommunityRequest);
			constructionOfCommunityResponse = ConstructionOfCommunityResponse.builder().constructionOfCommunity(constructionOfCommunityList)
					.requestInfo(constructionOfCommunityRequest.getRequestInfo()).message("Records has been updated successfully.").status(true).build();
		}
		return constructionOfCommunityResponse;
	}
	
	
	public ConstructionOfCommunityResponse getConstructionOfCommunity(RequestInfo requestInfo,String applicationNumber,String licenseNumber){
		ConstructionOfCommunityResponse constructionOfCommunityResponse = null;
		List<ConstructionOfCommunity> ConstructionOfCommunityDetails = null;
		
		if(applicationNumber==null&&licenseNumber==null) {
			constructionOfCommunityResponse = ConstructionOfCommunityResponse.builder().constructionOfCommunity(null)
						.requestInfo(requestInfo).message("Application Number or License Number both can't be null.").status(false).build();
		}else {
			try {
				if(applicationNumber==null) {
					ConstructionOfCommunityDetails=constructionOfCommunityRepo.searcherConstructionOfCommunityDetailsByLicenceNumberList(licenseNumber);
				}else {
					ConstructionOfCommunityDetails=constructionOfCommunityRepo.getConstructionOfCommunityDetailsByApplicationNumberList(applicationNumber);
				}
				
			} catch (Exception e1) {
				e1.printStackTrace();
			}
		}
		if(ConstructionOfCommunityDetails!=null) {
			constructionOfCommunityResponse = ConstructionOfCommunityResponse.builder().constructionOfCommunity(ConstructionOfCommunityDetails)
				.requestInfo(requestInfo).message("Fetched success").status(true).build();
		}else {
			constructionOfCommunityResponse = ConstructionOfCommunityResponse.builder().constructionOfCommunity(null)
					.requestInfo(null).message("Record not found").status(false).build();
		}
		
		return constructionOfCommunityResponse;
	}
	
	
}