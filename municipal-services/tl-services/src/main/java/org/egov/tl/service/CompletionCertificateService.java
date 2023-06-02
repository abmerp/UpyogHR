package org.egov.tl.service;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
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
import org.egov.tl.web.models.ChangeBeneficial;
import org.egov.tl.web.models.ChangeBeneficialRequest;
import org.egov.tl.web.models.ChangeBeneficialResponse;
import org.egov.tl.web.models.CompletionCertificate;
import org.egov.tl.web.models.CompletionCertificateRequest;
import org.egov.tl.web.models.CompletionCertificateResponse;
import org.egov.tl.web.models.TradeLicense;
import org.egov.tl.web.models.TradeLicenseRequest;
import org.egov.tl.web.models.TradeLicenseSearchCriteria;
import org.egov.tl.web.models.Transfer;
import org.egov.tl.workflow.WorkflowIntegrator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.extern.log4j.Log4j2;
import net.minidev.json.JSONObject;

@Service
@Log4j2
public class CompletionCertificateService {

	private static final String COMPLETION_CERTIFICATE_WORKFLOWCODE = "COMPLETION_CERTIFICATE";
	private static final String WFTENANTID = "hr";
	@Value("${tcp.employee.ctp}")
	private String ctpUser;
	@Autowired
	ObjectMapper mapper;
	@Autowired
	GenerateTcpNumbers generateTcpNumbers;

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
	private ServicePlanService servicePlanService;
	
	@Autowired
	ChangeBeneficialService changeBeneficialService;

	@Autowired
	private WorkflowIntegrator workflowIntegrator;

	public CompletionCertificateResponse createCompletionCertificate(CompletionCertificateRequest completionCertificateRequest) {
		boolean isScunitny=false;
		try {
			CompletionCertificate completionCertificates=completionCertificateRequest.getCompletionCertificate().get(0);
			if(completionCertificates.getApplicationNumber()!=null&&completionCertificates.getAction()!=null&&completionCertificates.getStatus()!=null){
				isScunitny=true;
			}
		}catch (Exception e) {
              e.printStackTrace();	
		}
		CompletionCertificateResponse completionCertificateResponse = null;
		String licenseNumber = completionCertificateRequest.getCompletionCertificate().get(0).getLicenseNumber();

		List<TradeLicense> tradeLicense = completionCertificateRepo.getLicenseByLicenseNumber(licenseNumber,
				completionCertificateRequest.getRequestInfo().getUserInfo().getId());
		if (tradeLicense == null || tradeLicense.isEmpty()) {
			completionCertificateResponse = CompletionCertificateResponse.builder().completionCertificate(null)
					.requestInfo(null).message("This License Number has expaired or License Number is not existing")
					.status(false).build();
		} else if (tradeLicense.get(0).getTradeLicenseDetail().getLicenseFeeCharges() == null) {
			completionCertificateResponse = CompletionCertificateResponse.builder().completionCertificate(null)
					.requestInfo(null).message("licence fees is null of this Application").status(false).build();
		} else {

			CompletionCertificate CompletionCertificateCheck = completionCertificateRepo
					.getCompletionCertificateByLicenseNumber(licenseNumber);
			if (CompletionCertificateCheck != null) {
				if(isScunitny) {
					CompletionCertificateCheck.setApplicationStatus(1);
				}
				
				if (CompletionCertificateCheck.getApplicationStatus() == 1) {
					completionCertificateResponse = createCompletionCertificate(completionCertificateRequest,
							CompletionCertificateCheck, false);
				} else {
					completionCertificateResponse = createCompletionCertificate(completionCertificateRequest, null,
							true);
				}

			} else {
				completionCertificateResponse = createCompletionCertificate(completionCertificateRequest, null, true);
			}

		}
		return completionCertificateResponse;

	}

	private CompletionCertificateResponse createCompletionCertificate(
			CompletionCertificateRequest completionCertificateRequest, CompletionCertificate completionCertificateData,
			boolean isCreate) {
		CompletionCertificateResponse completionCertificateResponse = null;
		List<CompletionCertificate> completionCertificate = (List<CompletionCertificate>) completionCertificateRequest
				.getCompletionCertificate().stream().map(certificate -> {
					String applicationNumberCC = servicePlanService
							.getIdList(completionCertificateRequest.getRequestInfo(), "hr",
									config.getCompletionCertificateName(), config.getCompletionCertificateFormat(), 1)
							.get(0);
					Long time = System.currentTimeMillis();
					AuditDetails auditDetails = null;
					if (isCreate) {

//						try {
//							CompletionCertificate completionCertificates = makePayment(certificate.getLicenseNumber(),
//									completionCertificateRequest.getRequestInfo());
//							certificate.setTcpApplicationNumber(completionCertificates.getTcpApplicationNumber());
//							certificate.setTcpCaseNumber(completionCertificates.getTcpCaseNumber());
//							certificate.setTcpDairyNumber(completionCertificates.getTcpDairyNumber());
//						} catch (JsonProcessingException e) {
//							// TODO Auto-generated catch block
//							e.printStackTrace();
//						}
						auditDetails = AuditDetails.builder()
								.createdBy(completionCertificateRequest.getRequestInfo().getUserInfo().getUuid())
								.createdTime(time).build();
						certificate.setId(UUID.randomUUID().toString());
						certificate.setWorkFlowCode(COMPLETION_CERTIFICATE_WORKFLOWCODE);
						
						certificate.setBusinessService(COMPLETION_CERTIFICATE_WORKFLOWCODE);
						certificate.setTenantId("hr");
						certificate.setAction("INITIATE");
						certificate.setStatus("INITIATE");
					
						try {
							TradeLicenseSearchCriteria criteria=new TradeLicenseSearchCriteria();
							criteria.setLicenseNumbers(Arrays.asList(certificate.getLicenseNumber()));
							Map<String,Object> tcpNumber= generateTcpNumbers.tcpNumbers(criteria, completionCertificateRequest.getRequestInfo());
							String tcpApplicationNumber=tcpNumber.get("TCPApplicationNumber").toString();
							String tcpCaseNumber=tcpNumber.get("TCPCaseNumber").toString();
							String tcpDairyNumber=tcpNumber.get("TCPDairyNumber").toString();
							certificate.setTcpApplicationNumber(tcpApplicationNumber);
							certificate.setTcpDairyNumber(tcpDairyNumber);
							certificate.setTcpCaseNumber(tcpCaseNumber);
						}catch (Exception e) {
							// TODO: handle exception
						}
						
						certificate.setApplicationStatus(1);
						certificate.setCreatedDate(new Timestamp(time));
						certificate.setFullPaymentDone(false);
						certificate.setApplicationNumber(applicationNumberCC);
						certificate.setCreatedTime(time);
					} else {
						certificate.setId(completionCertificateData.getId());
						auditDetails = completionCertificateData.getAuditDetails();
						auditDetails.setLastModifiedBy(
								completionCertificateRequest.getRequestInfo().getUserInfo().getUuid());
						auditDetails.setLastModifiedTime(time);
						
						certificate.setApplicationNumber(completionCertificateData.getApplicationNumber());
						String action=certificate.getAction();
						String status=certificate.getStatus();
						certificate.setAction(action!=null?action:"INITIATE");
						certificate.setStatus(status!=null?status:"INITIATE");
						
						certificate.setTcpApplicationNumber(completionCertificateData.getTcpApplicationNumber());
						certificate.setTcpCaseNumber(completionCertificateData.getTcpCaseNumber());
						certificate.setTcpDairyNumber(completionCertificateData.getTcpDairyNumber());
					
					}
					certificate.setAuditDetails(auditDetails);

					if (certificate.getIsDraft() == null) {
						certificate.setIsDraft("0");
					} else {
						certificate.setIsDraft("1");
					}
					return certificate;
				}).collect(Collectors.toList());
		completionCertificateRequest.setCompletionCertificate(completionCertificate);

		if (isCreate) {
			List<String> assignee=Arrays.asList(servicePlanService.assignee(ctpUser, WFTENANTID, true, completionCertificateRequest.getRequestInfo()));
			TradeLicenseRequest prepareProcessInstanceRequest=changeBeneficialService.prepareProcessInstanceRequest(WFTENANTID,COMPLETION_CERTIFICATE_WORKFLOWCODE,"INITIATE",assignee,completionCertificate.get(0).getApplicationNumber(),COMPLETION_CERTIFICATE_WORKFLOWCODE,completionCertificateRequest.getRequestInfo());
			workflowIntegrator.callWorkFlow(prepareProcessInstanceRequest);	
		    completionCertificateRepo.save(completionCertificateRequest);

			completionCertificateResponse = CompletionCertificateResponse.builder()
					.completionCertificate(completionCertificate)
					.requestInfo(completionCertificateRequest.getRequestInfo())
					.message("Records has been inserted successfully.").status(true).build();
		} else {
			if(completionCertificate.get(0).getApplicationNumber()!=null&&completionCertificate.get(0).getAction()==null&&completionCertificate.get(0).getStatus()==null) {
				List<String> assignee=Arrays.asList(servicePlanService.assignee(ctpUser, WFTENANTID, true, completionCertificateRequest.getRequestInfo()));
				TradeLicenseRequest prepareProcessInstanceRequest=changeBeneficialService.prepareProcessInstanceRequest(WFTENANTID,COMPLETION_CERTIFICATE_WORKFLOWCODE,"INITIATE",assignee,completionCertificate.get(0).getApplicationNumber(),COMPLETION_CERTIFICATE_WORKFLOWCODE,completionCertificateRequest.getRequestInfo());
				workflowIntegrator.callWorkFlow(prepareProcessInstanceRequest);	
			}
			completionCertificateRepo.update(completionCertificateRequest);
			completionCertificateResponse = CompletionCertificateResponse.builder()
					.completionCertificate(completionCertificate)
					.requestInfo(completionCertificateRequest.getRequestInfo())
					.message("Records has been updated successfully.").status(true).build();
		}
		return completionCertificateResponse;
	}

	public CompletionCertificateResponse getCompletionCertificate(RequestInfo requestInfo, String applicationNumber,
			String licenseNumber) {
		CompletionCertificateResponse completionCertificateResponse = null;
		List<CompletionCertificate> CompletionCertificateDetails = null;

		if (applicationNumber == null && licenseNumber == null) {
			CompletionCertificateDetails=completionCertificateRepo.getAllRecords();
//			completionCertificateResponse = CompletionCertificateResponse.builder().completionCertificate(null)
//					.requestInfo(requestInfo).message("Application Number or License Number both can't be null.")
//					.status(false).build();
		} else {
			try {
				if (applicationNumber == null) {
					CompletionCertificateDetails = completionCertificateRepo
							.searcherBeneficialDetailsByLicenceNumberList(licenseNumber);
				} else {
					CompletionCertificateDetails = completionCertificateRepo
							.getBeneficialDetailsByApplicationNumberList(applicationNumber);
				}

			} catch (Exception e1) {
				e1.printStackTrace();
			}
		}
		if (CompletionCertificateDetails != null) {
			completionCertificateResponse = CompletionCertificateResponse.builder()
					.completionCertificate(CompletionCertificateDetails).requestInfo(requestInfo)
					.message("Fetched success").status(true).build();
		} else {
			completionCertificateResponse = CompletionCertificateResponse.builder().completionCertificate(null)
					.requestInfo(null).message("Record not found").status(false).build();
		}

		return completionCertificateResponse;
	}

}
