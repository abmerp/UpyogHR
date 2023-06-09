package org.egov.tl.service;

import java.sql.Timestamp;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

import org.egov.common.contract.request.RequestInfo;
import org.egov.tl.abm.repo.CompletionCertificateRepo;
import org.egov.tl.abm.repo.CompositionOfUrbanRepo;
import org.egov.tl.abm.repo.ConstructionOfCommunityRepo;
import org.egov.tl.config.TLConfiguration;
import org.egov.tl.service.repo.LicenseServiceRepo;
import org.egov.tl.util.LandUtil;
import org.egov.tl.util.TradeUtil;
import org.egov.tl.validator.LandMDMSValidator;
import org.egov.tl.web.models.AuditDetails;
import org.egov.tl.web.models.ChangeBeneficial;
import org.egov.tl.web.models.CompletionCertificate;
import org.egov.tl.web.models.CompletionCertificateResponse;
import org.egov.tl.web.models.CompositionOfUrban;
import org.egov.tl.web.models.CompositionOfUrbanRequest;
import org.egov.tl.web.models.CompositionOfUrbanResponse;
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

import lombok.extern.log4j.Log4j2;

@Service
@Log4j2
public class CompositionOfUrbanService {
	
	private static final String COMPOSITION_OF_URBAN_WORKFLOWCODE = "COMPOSITION_OF_URBAN";
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
	private CompositionOfUrbanRepo compositionOfUrbanRepo;
	
	
	@Autowired
	private ChangeBeneficialService changeBeneficialService;
	
	@Autowired
	private ServicePlanService servicePlanService;
	
	@Autowired
	private WorkflowIntegrator workflowIntegrator;
	
	@Autowired
	private GenerateTcpNumbers generateTcpNumbers;

	public CompositionOfUrbanResponse saveCompositionOfUrban(CompositionOfUrbanRequest compositionOfUrbanRequest){
		boolean isScunitny=false;
		CompositionOfUrbanResponse compositionOfUrbanResponse = null;
		
		try {
			CompositionOfUrban compositionOfUrbans=compositionOfUrbanRequest.getCompositionOfUrban().get(0);
			if(compositionOfUrbans.getApplicationNumber()!=null&&compositionOfUrbans.getAction()!=null&&compositionOfUrbans.getStatus()!=null){
				isScunitny=true;
			}
		}catch (Exception e) {
              e.printStackTrace();	
		}
		
		String applicationNumber=compositionOfUrbanRequest.getCompositionOfUrban().get(0).getApplicationNumber();
		
		CompositionOfUrban compositionOfUrban=compositionOfUrbanRepo.getCompositionOfUrbanByApplicationNumber(applicationNumber!=null?applicationNumber:"0");
	    	if(compositionOfUrban!=null) {
	    		if(isScunitny) {
	    			compositionOfUrban.setApplicationStatus(1);
	    		}
	    		if(compositionOfUrban.getApplicationStatus()==1) {
	    			compositionOfUrbanResponse=createCompositionOfUrban(compositionOfUrbanRequest,compositionOfUrban,false);
	    		}else {
	    			compositionOfUrbanResponse=createCompositionOfUrban(compositionOfUrbanRequest,null,true);
	    		}
	    	
	    	} else {
	    		compositionOfUrbanResponse=createCompositionOfUrban(compositionOfUrbanRequest,null,true);
	    	} 
	    	
       
	   return compositionOfUrbanResponse;

	}


	private CompositionOfUrbanResponse createCompositionOfUrban(CompositionOfUrbanRequest compositionOfUrbanRequest,CompositionOfUrban compositionOfUrban,boolean isCreate){
		CompositionOfUrbanResponse compositionOfUrbanResponse=null;
		List<CompositionOfUrban> compositionOfUrbanList = (List<CompositionOfUrban>) compositionOfUrbanRequest.getCompositionOfUrban()
				.stream().map(composition -> {
					String applicationNumberCC = servicePlanService.getIdList(compositionOfUrbanRequest.getRequestInfo(), "hr",
							config.getCompositionOfUrbanName(), config.getCompositionOfUrbanFormat(), 1).get(0);
					Long time = System.currentTimeMillis();
					AuditDetails auditDetails = null;
					if(isCreate) {
						String userUuid=compositionOfUrbanRequest.getRequestInfo().getUserInfo().getUuid();
						auditDetails=AuditDetails.builder().createdBy(userUuid).createdTime(time).build();
						composition.setId(UUID.randomUUID().toString());
						composition.setWorkFlowCode(COMPOSITION_OF_URBAN_WORKFLOWCODE);
						
						composition.setBusinessService(COMPOSITION_OF_URBAN_WORKFLOWCODE);
						composition.setTenantId("hr");
						composition.setAction("INITIATE");
						composition.setStatus("INITIATE");
					
						composition.setUserUUID(userUuid);
						composition.setApplicationStatus(1);
						composition.setCreatedDate(new Timestamp(time));
						composition.setFullPaymentDone(false);
						composition.setApplicationNumber(applicationNumberCC);
						composition.setCreatedTime(time);
						
						try {
							TradeLicenseSearchCriteria criteria=new TradeLicenseSearchCriteria();
							criteria.setApplicationNumber(composition.getApplicationNumber());
							Map<String,Object> tcpNumber= generateTcpNumbers.tcpNumbers(criteria, compositionOfUrbanRequest.getRequestInfo());
							String tcpApplicationNumber=tcpNumber.get("TCPApplicationNumber").toString();
							String tcpCaseNumber=tcpNumber.get("TCPCaseNumber").toString();
							String tcpDairyNumber=tcpNumber.get("TCPDairyNumber").toString();
							composition.setTcpApplicationNumber(tcpApplicationNumber);
							composition.setTcpDairyNumber(tcpDairyNumber);
							composition.setTcpCaseNumber(tcpCaseNumber);
						}catch (Exception e) {
							e.printStackTrace();
							// TODO: handle exception
						}
						
						
					}else {
						composition.setId(compositionOfUrban.getId());
						auditDetails=compositionOfUrban.getAuditDetails();
						auditDetails.setLastModifiedBy(compositionOfUrbanRequest.getRequestInfo().getUserInfo().getUuid());
						auditDetails.setLastModifiedTime(time);
						composition.setApplicationNumber(compositionOfUrban.getApplicationNumber());
						String action=composition.getAction();
						String status=composition.getStatus();
						composition.setAction(action!=null?action:"INITIATE");
						composition.setStatus(status!=null?status:"INITIATE");
						
						composition.setTcpApplicationNumber(compositionOfUrban.getTcpApplicationNumber());
						composition.setTcpCaseNumber(compositionOfUrban.getTcpCaseNumber());
						composition.setTcpDairyNumber(compositionOfUrban.getTcpDairyNumber());
					
					}
					composition.setAuditDetails(auditDetails);
					
					if(composition.getIsDraft()==null) {
						composition.setIsDraft("0");	
					}else {
						composition.setIsDraft("1");
					}					
					return composition;
				}).collect(Collectors.toList());	
		compositionOfUrbanRequest.setCompositionOfUrban(compositionOfUrbanList);
		
		if(isCreate) {
//			List<String> assignee=Arrays.asList(servicePlanService.assignee("CTP_HR", WFTENANTID, true, compositionOfUrbanRequest.getRequestInfo()));
//			TradeLicenseRequest prepareProcessInstanceRequest=changeBeneficialService.prepareProcessInstanceRequest(WFTENANTID,COMPOSITION_OF_URBAN_WORKFLOWCODE,"INITIATE",assignee,compositionOfUrbanList.get(0).getApplicationNumber(),COMPOSITION_OF_URBAN_WORKFLOWCODE,compositionOfUrbanRequest.getRequestInfo());
//			workflowIntegrator.callWorkFlow(prepareProcessInstanceRequest);	
			
			compositionOfUrbanRepo.save(compositionOfUrbanRequest);
			compositionOfUrbanResponse = CompositionOfUrbanResponse.builder().compositionOfUrban(compositionOfUrbanList)
					.requestInfo(compositionOfUrbanRequest.getRequestInfo()).message("Records has been inserted successfully.").status(true).build();
		} else {
			compositionOfUrbanRepo.update(compositionOfUrbanRequest);
			compositionOfUrbanResponse = CompositionOfUrbanResponse.builder().compositionOfUrban(compositionOfUrbanList)
					.requestInfo(compositionOfUrbanRequest.getRequestInfo()).message("Records has been updated successfully.").status(true).build();
		}
		return compositionOfUrbanResponse;
	}
	
	
	public CompositionOfUrbanResponse getCompositionOfUrban(RequestInfo requestInfo,String applicationNumber){
		CompositionOfUrbanResponse compositionOfUrbanResponse = null;
		List<CompositionOfUrban> compositionOfUrbanDetails = null;
		
		if(applicationNumber==null) {
			compositionOfUrbanDetails=compositionOfUrbanRepo.getAllRecords();
//			compositionOfUrbanResponse = CompositionOfUrbanResponse.builder().compositionOfUrban(null)
//						.requestInfo(requestInfo).message("Application Number can't be null.").status(false).build();
		}else {
			try {
				compositionOfUrbanDetails=compositionOfUrbanRepo.getCompositionOfUrbanByApplicationNumberList(applicationNumber);
			} catch (Exception e1) {
				e1.printStackTrace();
			}
		}
		if(compositionOfUrbanDetails!=null) {
			compositionOfUrbanResponse = CompositionOfUrbanResponse.builder().compositionOfUrban(compositionOfUrbanDetails)
				.requestInfo(requestInfo).message("Fetched success").status(true).build();
		}else {
			compositionOfUrbanResponse = CompositionOfUrbanResponse.builder().compositionOfUrban(null)
					.requestInfo(null).message("Record not found").status(false).build();
		}
		
		return compositionOfUrbanResponse;
	}
	
	
}