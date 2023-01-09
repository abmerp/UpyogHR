package org.egov.tl.service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import org.egov.common.contract.request.RequestInfo;
import org.egov.common.contract.request.Role;
import org.egov.tl.abm.newservices.contract.BankGuaranteeSearchContract;
import org.egov.tl.abm.newservices.contract.NewBankGuaranteeContract;
import org.egov.tl.abm.newservices.contract.RenewBankGuaranteeContract;
import org.egov.tl.abm.newservices.entity.NewBankGuarantee;
import org.egov.tl.abm.newservices.entity.RenewBankGuarantee;
import org.egov.tl.abm.repo.NewBankGuaranteeJpaRepo;
import org.egov.tl.abm.repo.NewBankGuaranteeRepo;
import org.egov.tl.abm.repo.RenewBankGuaranteeRepo;
import org.egov.tl.config.TLConfiguration;
import org.egov.tl.producer.Producer;
import org.egov.tl.service.dao.LicenseServiceDao;
import org.egov.tl.util.TradeUtil;
import org.egov.tl.web.models.AuditDetails;
import org.egov.tl.web.models.TradeLicense;
import org.egov.tl.web.models.TradeLicenseDetail;
import org.egov.tl.web.models.TradeLicenseRequest;
import org.egov.tl.web.models.bankguarantee.NewBankGuaranteeRequest;
import org.egov.tl.web.models.workflow.Action;
import org.egov.tl.web.models.workflow.BusinessService;
import org.egov.tl.web.models.workflow.State;
import org.egov.tl.workflow.WorkflowIntegrator;
import org.egov.tl.workflow.WorkflowService;
import org.egov.tracer.model.CustomException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class BankGuaranteeService {

	@Autowired
	private NewBankGuaranteeRepo newBankGuaranteeRepo;
	@Autowired
	private TradeUtil tradeUtil;
	@Autowired
	private TLConfiguration tlConfiguration;
	@Autowired
	private EnrichmentService enrichmentService;
	@Autowired
	private WorkflowIntegrator workflowIntegrator;
	@Autowired
	private WorkflowService workflowService;
	@Autowired
	private LicenseService licenseService;
	@Autowired
	private RenewBankGuaranteeRepo renewBankGuaranteeRepo;
	
	public static final String BUSINESSSERVICE_BG_NEW = "BG_NEW";
	public static final String BUSINESSSERVICE_TENANTID = "hr";
	public static final String BUSINESSSERVICE_BG_RENEW = "BG_RENEW";
	
	//@Autowired RenewBankGuaranteeRepo renewBankGuaranteeRepo;	
	//@Autowired ReleaseBankGuaranteeRepo releaseBankGuaranteeRepo;
	//@Autowired ReplaceBankGuaranteeRepo replaceBankGuaranteeRepo;

	public NewBankGuarantee createNewBankGuarantee(NewBankGuaranteeContract newBankGuaranteeContract) {

		// populate audit details-
		AuditDetails auditDetails = tradeUtil
				.getAuditDetails(newBankGuaranteeContract.getRequestInfo().getUserInfo().getUuid(), true);
		newBankGuaranteeContract.getNewBankGuaranteeRequest().setAuditDetails(auditDetails);

		//populate applicationNumber from idgen-
		List<String> idGenIds = enrichmentService.getIdList(newBankGuaranteeContract.getRequestInfo(),
				newBankGuaranteeContract.getNewBankGuaranteeRequest().getTenantId(),
				tlConfiguration.getNewBankGuaranteeApplNoIdGenName(),
				tlConfiguration.getNewBankGuaranteeApplNoIdGenFormat(), 1);
		String applicationNo = idGenIds.get(0);
		newBankGuaranteeContract.getNewBankGuaranteeRequest().setApplicationNumber(applicationNo);
		//set INITIATED status as not expected from UI-
		newBankGuaranteeContract.getNewBankGuaranteeRequest().setStatus("INITIATED");
		newBankGuaranteeContract.getNewBankGuaranteeRequest().setId(UUID.randomUUID().toString());
		validateValidityFormat(newBankGuaranteeContract.getNewBankGuaranteeRequest().getValidity());
		NewBankGuarantee newBankGuaranteeEntity = newBankGuaranteeContract.getNewBankGuaranteeRequest().toBuilder();
		
		//call workflow to insert processinstance-
		TradeLicenseRequest processInstanceRequest = prepareProcessInstanceRequestForNewBG(newBankGuaranteeContract);
		workflowIntegrator.callWorkFlow(processInstanceRequest);
		
		newBankGuaranteeRepo.save(newBankGuaranteeContract);
		
		return newBankGuaranteeEntity;

	}
	
	private TradeLicenseRequest prepareProcessInstanceRequestForNewBG(NewBankGuaranteeContract newBankGuaranteeContract) {
		TradeLicenseRequest tradeLicenseRequest = new TradeLicenseRequest();
		List<TradeLicense> licenses = new ArrayList<>();
		TradeLicense tradeLicense = new TradeLicense();

		tradeLicense.setBusinessService(BUSINESSSERVICE_BG_NEW);
		tradeLicense.setAction(newBankGuaranteeContract.getNewBankGuaranteeRequest().getWorkflowAction());
		tradeLicense.setAssignee(newBankGuaranteeContract.getNewBankGuaranteeRequest().getWorkflowAssignee());
		tradeLicense.setApplicationNumber(newBankGuaranteeContract.getNewBankGuaranteeRequest().getApplicationNumber());
		tradeLicense.setWorkflowCode(BUSINESSSERVICE_BG_NEW);// workflowname
		TradeLicenseDetail tradeLicenseDetail = new TradeLicenseDetail();
		tradeLicenseDetail.setTradeType(BUSINESSSERVICE_BG_NEW);
		tradeLicense.setTradeLicenseDetail(tradeLicenseDetail);
		tradeLicense.setComment(newBankGuaranteeContract.getNewBankGuaranteeRequest().getWorkflowComment());
		tradeLicense.setWfDocuments(null);
		tradeLicense.setTenantId("hr");
		tradeLicense.setBusinessService(BUSINESSSERVICE_BG_NEW);

		licenses.add(tradeLicense);
		tradeLicenseRequest.setLicenses(licenses);
		tradeLicenseRequest.setRequestInfo(newBankGuaranteeContract.getRequestInfo());
		return tradeLicenseRequest;
	}
	
	private void validateValidityFormat(String validity) {
		try {
			if (Objects.nonNull(validity)) {
				log.debug("validity in payload:" + validity);
				LocalDate localDate = LocalDate.parse(validity);
			}
		} catch (Exception ex) {
			log.error("Exception while parsing validity into java.time.LocalDate", ex);
			throw new CustomException("Please send the validity in proper yyyy-MM-dd format",
					"Please send the validity in proper yyyy-MM-dd format");
		}
	}
	
	public List<NewBankGuarantee> searchNewBankGuarantee(RequestInfo requestInfo, List<String> applicationNumber) {
		List<NewBankGuaranteeRequest> newBankGuaranteeRequestData = newBankGuaranteeRepo
				.getNewBankGuaranteeData(applicationNumber);
		// populate new license detail-
		//populateNewLicenseDetails(newBankGuaranteeRequestData);
		List<NewBankGuarantee> newBankGuaranteeData = newBankGuaranteeRequestData.stream()
				.map(newBankGuaranteeRequest -> newBankGuaranteeRequest.toBuilder()).collect(Collectors.toList());
		return newBankGuaranteeData;
	}
	
	private void populateNewLicenseDetails(List<NewBankGuaranteeRequest> newBankGuaranteeRequestData) {
		for (NewBankGuaranteeRequest newBankGuaranteeRequest : newBankGuaranteeRequestData) {
			LicenseServiceDao license = licenseService.findByLoiNumber(newBankGuaranteeRequest.getLoiNumber());
			newBankGuaranteeRequest.setLicense(license);
		}
	}
	
	public NewBankGuarantee updateNewBankGuarantee(NewBankGuaranteeContract newBankGuaranteeContract) {
		if (Objects.isNull(newBankGuaranteeContract)
				|| Objects.isNull(newBankGuaranteeContract.getNewBankGuaranteeRequest())) {
			throw new CustomException("NewBankGuaranteeRequest must not be null",
					"NewBankGuaranteeRequest must not be null");
		}
		if (StringUtils.isEmpty(newBankGuaranteeContract.getNewBankGuaranteeRequest().getApplicationNumber())) {
			throw new CustomException("ApplicationNumber must not be null", "ApplicationNumber must not be null");
		}
		List<String> applicationNos = new ArrayList<>();
		applicationNos.add(newBankGuaranteeContract.getNewBankGuaranteeRequest().getApplicationNumber());
		List<NewBankGuarantee> newBankGuaranteeSearchResult = searchNewBankGuarantee(
				newBankGuaranteeContract.getRequestInfo(), applicationNos);
		if (CollectionUtils.isEmpty(newBankGuaranteeSearchResult) || newBankGuaranteeSearchResult.size() > 1) {
			throw new CustomException(
					"Found none or multiple new bank guarantee applications with applicationNumber:"
							+ newBankGuaranteeContract.getNewBankGuaranteeRequest().getApplicationNumber(),
					"Found none or multiple new bank guarantee applications with applicationNumber:"
							+ newBankGuaranteeContract.getNewBankGuaranteeRequest().getApplicationNumber());
		}
		String currentStatus = newBankGuaranteeSearchResult.get(0).getStatus();
		BusinessService workflow = workflowService.getBusinessService(BUSINESSSERVICE_TENANTID,
				newBankGuaranteeContract.getRequestInfo(), BUSINESSSERVICE_BG_NEW);
		validateUpdateRoleAndActionFromWorkflow(workflow, currentStatus, newBankGuaranteeContract);
		enrichAuditDetailsOnUpdate(newBankGuaranteeContract);

		// call workflow to insert processinstance-
		TradeLicenseRequest processInstanceRequest = prepareProcessInstanceRequestForNewBG(newBankGuaranteeContract);
		workflowIntegrator.callWorkFlow(processInstanceRequest);

		// push to update-
		newBankGuaranteeRepo.update(newBankGuaranteeContract);
		NewBankGuarantee newBankGuarantee = newBankGuaranteeContract.getNewBankGuaranteeRequest().toBuilder();
		return newBankGuarantee;
	}

	private void validateUpdateRoleAndActionFromWorkflow(BusinessService workflow, String currentStatus,
			NewBankGuaranteeContract newBankGuaranteeContract) {
		// validate Action-
		Optional<State> currentWorkflowStateOptional = workflow.getStates().stream()
				.filter(state -> state.getState().equals(currentStatus)).findFirst();
		if (!currentWorkflowStateOptional.isPresent()) {
			throw new CustomException("workflow State not found:" + currentStatus,
					"workflow State not found:" + currentStatus);
		}
		State currentWorkflowState = currentWorkflowStateOptional.get();
		List<Action> permissibleActions = currentWorkflowState.getActions();
		String currentActionFromRequest = newBankGuaranteeContract.getNewBankGuaranteeRequest().getWorkflowAction();
		Optional<Action> currentWorkflowActionOptional = permissibleActions.stream()
				.filter(action -> action.getAction().equals(currentActionFromRequest)).findFirst();
		if (!currentWorkflowActionOptional.isPresent()) {
			throw new CustomException(
					"Action " + currentActionFromRequest + " not found in workflow for current status " + currentStatus,
					"Action " + currentActionFromRequest + " not found in workflow for current status "
							+ currentStatus);
		}
		Action currentWorkflowAction = currentWorkflowActionOptional.get();
		// validate roles:
		List<String> workflowPermissibleRoles = currentWorkflowAction.getRoles();
		List<Role> rolesFromUserInfo = newBankGuaranteeContract.getRequestInfo().getUserInfo().getRoles();
		List<String> currentUserRoles = rolesFromUserInfo.stream().map(role -> role.getCode())
				.collect(Collectors.toList());
		boolean isAuthorizedActionByRole = org.apache.commons.collections.CollectionUtils.containsAny(currentUserRoles,
				workflowPermissibleRoles);
		if (!isAuthorizedActionByRole) {
			throw new CustomException("User role not authorized to perform this action",
					"User role not authorized to perform this action");
		}
		String nextStateUUID = currentWorkflowAction.getNextState();
		Optional<State> nextStateOptional = workflow.getStates().stream()
				.filter(state -> state.getUuid().equals(nextStateUUID)).findFirst();
		State nextState = nextStateOptional.get();
		String nextStateName = nextState.getState();
		// set next state as status-
		newBankGuaranteeContract.getNewBankGuaranteeRequest().setStatus(nextStateName);
	}

	private void enrichAuditDetailsOnUpdate(NewBankGuaranteeContract newBankGuaranteeContract) {
		AuditDetails auditDetails = tradeUtil
				.getAuditDetails(newBankGuaranteeContract.getRequestInfo().getUserInfo().getUuid(), false);
		auditDetails
				.setCreatedBy(newBankGuaranteeContract.getNewBankGuaranteeRequest().getAuditDetails().getCreatedBy());
		auditDetails.setCreatedTime(
				newBankGuaranteeContract.getNewBankGuaranteeRequest().getAuditDetails().getCreatedTime());
		newBankGuaranteeContract.getNewBankGuaranteeRequest().setAuditDetails(auditDetails);
	}
	
	
	public RenewBankGuarantee createRenewBankGuarantee(RenewBankGuaranteeContract renewBankGuaranteeContract) {
		// populate audit details-
		AuditDetails auditDetails = tradeUtil
				.getAuditDetails(renewBankGuaranteeContract.getRequestInfo().getUserInfo().getUuid(), true);
		renewBankGuaranteeContract.getRenewBankGuaranteeRequest().setAuditDetails(auditDetails);

		//populate applicationNumber from idgen-
		List<String> idGenIds = enrichmentService.getIdList(renewBankGuaranteeContract.getRequestInfo(),
				renewBankGuaranteeContract.getRenewBankGuaranteeRequest().getTenantId(),
				tlConfiguration.getRenewBankGuaranteeApplNoIdGenName(),
				tlConfiguration.getRenewBankGuaranteeApplNoIdGenFormat(), 1);
		String applicationNo = idGenIds.get(0);
		renewBankGuaranteeContract.getRenewBankGuaranteeRequest().setApplicationNumber(applicationNo);
		//set INITIATED status as not expected from UI-
		renewBankGuaranteeContract.getRenewBankGuaranteeRequest().setStatus("INITIATED");
		renewBankGuaranteeContract.getRenewBankGuaranteeRequest().setId(UUID.randomUUID().toString());
		validateValidityFormat(renewBankGuaranteeContract.getRenewBankGuaranteeRequest().getValidity());
		RenewBankGuarantee renewBankGuaranteeEntity = renewBankGuaranteeContract.getRenewBankGuaranteeRequest().toBuilder();
		
		//call workflow to insert processinstance-
		TradeLicenseRequest processInstanceRequest = prepareProcessInstanceRequestForRenewBG(renewBankGuaranteeContract);
		workflowIntegrator.callWorkFlow(processInstanceRequest);
		
		renewBankGuaranteeRepo.save(renewBankGuaranteeContract);
		
		return renewBankGuaranteeEntity;
	}
	
	private TradeLicenseRequest prepareProcessInstanceRequestForRenewBG(RenewBankGuaranteeContract renewBankGuaranteeContract) {
		TradeLicenseRequest tradeLicenseRequest = new TradeLicenseRequest();
		List<TradeLicense> licenses = new ArrayList<>();
		TradeLicense tradeLicense = new TradeLicense();

		tradeLicense.setBusinessService(BUSINESSSERVICE_BG_RENEW);
		tradeLicense.setAction(renewBankGuaranteeContract.getRenewBankGuaranteeRequest().getWorkflowAction());
		tradeLicense.setAssignee(renewBankGuaranteeContract.getRenewBankGuaranteeRequest().getWorkflowAssignee());
		tradeLicense.setApplicationNumber(renewBankGuaranteeContract.getRenewBankGuaranteeRequest().getApplicationNumber());
		tradeLicense.setWorkflowCode(BUSINESSSERVICE_BG_RENEW);// workflowname
		TradeLicenseDetail tradeLicenseDetail = new TradeLicenseDetail();
		tradeLicenseDetail.setTradeType(BUSINESSSERVICE_BG_RENEW);
		tradeLicense.setTradeLicenseDetail(tradeLicenseDetail);
		tradeLicense.setComment(renewBankGuaranteeContract.getRenewBankGuaranteeRequest().getWorkflowComment());
		tradeLicense.setWfDocuments(null);
		tradeLicense.setTenantId("hr");
		tradeLicense.setBusinessService(BUSINESSSERVICE_BG_RENEW);

		licenses.add(tradeLicense);
		tradeLicenseRequest.setLicenses(licenses);
		tradeLicenseRequest.setRequestInfo(renewBankGuaranteeContract.getRequestInfo());
		return tradeLicenseRequest;
	}
	
	/*
	public RenewBankGuarantee searchRenewBankGuarantee(Long renewBankGuaranteeId) {
		return renewBankGuaranteeRepo.getOne(renewBankGuaranteeId);
	}
	
	public ReleaseBankGuarantee createReleaseBankGuarantee(ReleaseBankGuaranteeContract releaseBankGuaranteeContract) {
		return releaseBankGuaranteeRepo.save(releaseBankGuaranteeContract.getReleaseBankGuaranteeRequest().toBuilder());
	}
	
	public ReleaseBankGuarantee searchReleaseBankGuarantee(Long releaseBankGuaranteeId) {
		return releaseBankGuaranteeRepo.getOne(releaseBankGuaranteeId);
	}
	
	public ReplaceBankGuarantee createReplaceBankGuarantee(ReplaceBankGuaranteeContract replaceBankGuaranteeContract) {
		return replaceBankGuaranteeRepo.save(replaceBankGuaranteeContract.getReplaceBankGuarantee().toBuilder());
	}
	
	public ReplaceBankGuarantee searchReplaceBankGuarantee(Long replaceBankGuaranteeId) {
		return replaceBankGuaranteeRepo.getOne(replaceBankGuaranteeId);
	}
	*/
}
