package org.egov.tl.service;

import java.sql.Timestamp;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
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
import org.egov.tl.web.models.TradeLicenseRequest;
import org.egov.tl.web.models.TradeLicenseSearchCriteria;
import org.egov.tl.workflow.WorkflowIntegrator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

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
	private ConstructionOfCommunityRepo constructionOfCommunityRepo;
	
	
	@Autowired
	private ChangeBeneficialService changeBeneficialService;
	
	@Autowired
	private ServicePlanService servicePlanService;
	
	@Autowired
	private WorkflowIntegrator workflowIntegrator;
	
	@Autowired
	ObjectMapper mapper;
	
	@Autowired
	GenerateTcpNumbers generateTcpNumbers;

	

	public ConstructionOfCommunityResponse saveConstructionOfCommunity(ConstructionOfCommunityRequest constructionOfCommunityRequest,boolean isScunitny){
		ConstructionOfCommunityResponse constructionOfCommunityResponse = null;
		String licenseNumber=constructionOfCommunityRequest.getConstructionOfCommunity().get(0).getLicenseNumber();
		
		List<TradeLicense> tradeLicense = constructionOfCommunityRepo.getLicenseByLicenseNumber(licenseNumber,constructionOfCommunityRequest.getRequestInfo().getUserInfo().getId());
		if(tradeLicense==null||tradeLicense.isEmpty()) {
			constructionOfCommunityResponse = ConstructionOfCommunityResponse.builder().constructionOfCommunity(null)
					.requestInfo(null).message("This License Number is not existing").status(false).build();
		}else if(tradeLicense.get(0).getTradeLicenseDetail().getLicenseFeeCharges()==null) {
			constructionOfCommunityResponse = ConstructionOfCommunityResponse.builder()
					.constructionOfCommunity(null).requestInfo(null).message("licence fees is null of this License").status(false).build();
	    }else {
	    	ConstructionOfCommunity constructionOfCommunity=constructionOfCommunityRepo.getConstructionOfCommunityByLicenseNumber(licenseNumber);
	    	if(constructionOfCommunity!=null) {
	    		if(isScunitny) {
	    			constructionOfCommunity.setApplicationStatus(1);
				}
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
						construction.setId(UUID.randomUUID().toString());
						construction.setWorkFlowCode(CONSTRUCTION_OF_COMMUNITY_WORKFLOWCODE);
						
						construction.setBusinessService(CONSTRUCTION_OF_COMMUNITY_WORKFLOWCODE);
						construction.setTenantId("hr");
						construction.setAction("INITIATE");
						construction.setStatus("INITIATE");
						
						try {
							TradeLicenseSearchCriteria criteria=new TradeLicenseSearchCriteria();
							criteria.setLicenseNumbers(Arrays.asList(construction.getLicenseNumber()));
							Map<String,Object> tcpNumber= generateTcpNumbers.tcpNumbers(criteria, constructionOfCommunityRequest.getRequestInfo());
							String tcpApplicationNumber=tcpNumber.get("TCPApplicationNumber").toString();
							String tcpCaseNumber=tcpNumber.get("TCPCaseNumber").toString();
							String tcpDairyNumber=tcpNumber.get("TCPDairyNumber").toString();
							construction.setTcpApplicationNumber(tcpApplicationNumber);
							construction.setTcpDairyNumber(tcpDairyNumber);
							construction.setTcpCaseNumber(tcpCaseNumber);
						}catch (Exception e) {
							e.printStackTrace();
							// TODO: handle exception
						}

					
						construction.setApplicationStatus(1);
						construction.setCreatedDate(new Timestamp(time));
						construction.setFullPaymentDone(false);
						construction.setApplicationNumber(applicationNumberCC);
						construction.setCreatedTime(time);
					}else {
						construction.setId(constructionOfCommunity.getId());
						auditDetails=constructionOfCommunity.getAuditDetails();
						auditDetails.setLastModifiedBy(constructionOfCommunityRequest.getRequestInfo().getUserInfo().getUuid());
						auditDetails.setLastModifiedTime(time);
					
						construction.setApplicationNumber(constructionOfCommunity.getApplicationNumber());
						String action=construction.getAction();
						String status=construction.getStatus();
						construction.setAction(action!=null?action:"INITIATE");
						construction.setStatus(status!=null?status:"INITIATE");
						
					}
					construction.setAuditDetails(auditDetails);
					
					if(construction.getIsDraft()==null) {
						construction.setIsDraft("0");	
					}else {
						construction.setIsDraft("1");
					}		
					try {
						String data = mapper.writeValueAsString(construction.getNewAdditionalDetails());
						JsonNode jsonNode = mapper.readTree(data);
						construction.setNewAdditionalDetails(jsonNode);
					} catch (JsonProcessingException e) {
						e.printStackTrace();
					}catch(Exception e) {
						e.printStackTrace();
					}
					return construction;

				}).collect(Collectors.toList());	
		        constructionOfCommunityRequest.setConstructionOfCommunity(constructionOfCommunityList);
		
		if(isCreate) {
			List<String> assignee=Arrays.asList(servicePlanService.assignee("CTP_HR", WFTENANTID, true, constructionOfCommunityRequest.getRequestInfo()));
			TradeLicenseRequest prepareProcessInstanceRequest=changeBeneficialService.prepareProcessInstanceRequest(WFTENANTID,CONSTRUCTION_OF_COMMUNITY_WORKFLOWCODE,"INITIATE",assignee,constructionOfCommunityList.get(0).getApplicationNumber(),CONSTRUCTION_OF_COMMUNITY_WORKFLOWCODE,constructionOfCommunityRequest.getRequestInfo());
			workflowIntegrator.callWorkFlow(prepareProcessInstanceRequest);	
			
			constructionOfCommunityRepo.save(constructionOfCommunityRequest);
		    constructionOfCommunityResponse = ConstructionOfCommunityResponse.builder().constructionOfCommunity(constructionOfCommunityList)
					.requestInfo(constructionOfCommunityRequest.getRequestInfo()).message("Records has been inserted successfully.").status(true).build();
		} else {
			
			if(constructionOfCommunityList.get(0).getApplicationNumber()!=null&&constructionOfCommunityList.get(0).getAction()==null&&constructionOfCommunityList.get(0).getStatus()==null) {
				List<String> assignee=Arrays.asList(servicePlanService.assignee("CTP_HR", WFTENANTID, true, constructionOfCommunityRequest.getRequestInfo()));
				TradeLicenseRequest prepareProcessInstanceRequest=changeBeneficialService.prepareProcessInstanceRequest(WFTENANTID,CONSTRUCTION_OF_COMMUNITY_WORKFLOWCODE,"INITIATE",assignee,constructionOfCommunityList.get(0).getApplicationNumber(),CONSTRUCTION_OF_COMMUNITY_WORKFLOWCODE,constructionOfCommunityRequest.getRequestInfo());
				workflowIntegrator.callWorkFlow(prepareProcessInstanceRequest);	
			}
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
			   ConstructionOfCommunityDetails=constructionOfCommunityRepo.getAllRecords();
//			constructionOfCommunityResponse = ConstructionOfCommunityResponse.builder().constructionOfCommunity(null)
//						.requestInfo(requestInfo).message("Application Number or License Number both can't be null.").status(false).build();
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