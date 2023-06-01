package org.egov.tl.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import org.egov.common.contract.request.RequestInfo;
import org.egov.tl.abm.newservices.contract.NewBankGuaranteeContract;
import org.egov.tl.abm.repo.NewBankGuaranteeRepo;
import org.egov.tl.config.TLConfiguration;
import org.egov.tl.util.TradeUtil;
import org.egov.tl.web.models.TradeLicense;
import org.egov.tl.web.models.TradeLicenseDetail;
import org.egov.tl.web.models.TradeLicenseRequest;
import org.egov.tl.web.models.bankguarantee.NewBankGuaranteeRequest;
import org.egov.tl.workflow.WorkflowIntegrator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class MortgageBGService {

	@Autowired
	private NewBankGuaranteeRepo newBankGuaranteeRepo;	
	@Autowired
	private TLConfiguration tlConfiguration;
	@Autowired
	private EnrichmentService enrichmentService;
	@Autowired
	private WorkflowIntegrator workflowIntegrator;
	@Autowired
	private ObjectMapper objectMapper;
	@Autowired
	private TradeUtil tradeUtil;
	
	public static final String BG_MORTGAGE_LANDING_EMPLOYEE_ROLE = "DTP_HQ";

	public NewBankGuaranteeRequest createBankGuarantee(NewBankGuaranteeRequest newBankGuaranteeRequest,
			RequestInfo requestInfo) {
		log.info("inside method createBankGuarantee");
		List<String> idGenIds = enrichmentService.getIdList(requestInfo, newBankGuaranteeRequest.getTenantId(),
				tlConfiguration.getNewBankGuaranteeApplNoIdGenName(),
				tlConfiguration.getNewBankGuaranteeApplNoIdGenFormat(), 1);
		newBankGuaranteeRequest.setBusinessService(BankGuaranteeService.BUSINESSSERVICE_BG_MORTGAGE);
		newBankGuaranteeRequest
				.setAssignee(Arrays.asList(tradeUtil.getFirstAssigneeByRole(BG_MORTGAGE_LANDING_EMPLOYEE_ROLE,
						newBankGuaranteeRequest.getTenantId(), true, requestInfo)));
		String applicationNo = idGenIds.get(0);
		newBankGuaranteeRequest.setApplicationNumber(applicationNo);
		newBankGuaranteeRequest.setId(UUID.randomUUID().toString());
		newBankGuaranteeRequest.setStatus(BankGuaranteeService.BG_STATUS_INITIATED);
		newBankGuaranteeRequest.setAction(BankGuaranteeService.BG_ACTION_INITIATE);
		TradeLicenseRequest processInstanceRequest = prepareProcessInstanceRequestForNewBG(newBankGuaranteeRequest,
				requestInfo);
		workflowIntegrator.callWorkFlow(processInstanceRequest);
		List<NewBankGuaranteeRequest> newBankGuaranteeRequests = new ArrayList<>();
		newBankGuaranteeRequests.add(newBankGuaranteeRequest);
		NewBankGuaranteeContract newBankGuaranteeContract = NewBankGuaranteeContract.builder()
				.newBankGuaranteeRequest(newBankGuaranteeRequests).requestInfo(requestInfo).build();
		newBankGuaranteeRepo.save(newBankGuaranteeContract);
		return newBankGuaranteeRequest;
	}
	
	public TradeLicenseRequest prepareProcessInstanceRequestForNewBG(NewBankGuaranteeRequest newBankGuaranteeRequest, RequestInfo requestInfo) {
		TradeLicenseRequest tradeLicenseRequest = new TradeLicenseRequest();
		List<TradeLicense> licenses = new ArrayList<>();
		TradeLicense tradeLicense = new TradeLicense();

		tradeLicense.setBusinessService(BankGuaranteeService.BUSINESSSERVICE_BG_MORTGAGE);
		tradeLicense.setAction(newBankGuaranteeRequest.getAction());
		tradeLicense.setAssignee(newBankGuaranteeRequest.getAssignee());
		tradeLicense.setApplicationNumber(newBankGuaranteeRequest.getApplicationNumber());
		tradeLicense.setWorkflowCode(BankGuaranteeService.BUSINESSSERVICE_BG_MORTGAGE);// workflowname
		TradeLicenseDetail tradeLicenseDetail = new TradeLicenseDetail();
		tradeLicenseDetail.setTradeType(BankGuaranteeService.BUSINESSSERVICE_BG_MORTGAGE);
		tradeLicense.setTradeLicenseDetail(tradeLicenseDetail);
		tradeLicense.setComment(newBankGuaranteeRequest.getComment());
		tradeLicense.setWfDocuments(newBankGuaranteeRequest.getWfDocuments());
		tradeLicense.setTenantId("hr");
		tradeLicense.setBusinessService(BankGuaranteeService.BUSINESSSERVICE_BG_MORTGAGE);

		licenses.add(tradeLicense);
		tradeLicenseRequest.setLicenses(licenses);
		tradeLicenseRequest.setRequestInfo(requestInfo);
		return tradeLicenseRequest;
	}
	
	public void updateBankGuarantee(NewBankGuaranteeRequest newBankGuaranteeRequest) {
		
	}
}
