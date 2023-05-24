package org.egov.tl.service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
import org.egov.tl.web.models.bankguarantee.BankGuaranteeSearchCriteria;
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

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

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
	@Autowired
	private ObjectMapper objectMapper;
	@Autowired
	private MortgageBGService mortgageBGService;
	
	public static final String BUSINESSSERVICE_BG_NEW = "BG_NEW";
	public static final String BUSINESSSERVICE_BG_MORTGAGE = "BG_MORTGAGE";
	public static final String BUSINESSSERVICE_TENANTID = "hr";
	public static final String BUSINESSSERVICE_BG_RENEW = "BG_RENEW";
	public static final String BG_NEW_ACTION_EXTEND = "EXTEND";
	public static final String BG_NEW_ACTION_RELEASE = "RELEASE";
	public static final String BG_NEW_STATUS_APPROVED = "APPROVED";
	public static final String BG_STATUS_VALID = "VALID";
	public static final String BG_NEW_ACTION_APPROVE = "APPROVE";
	public static final String BG_STATUS_RELEASED = "RELEASED";
	public static final String BG_STATUS_PRE_SUBMIT = "PRE_SUBMIT";
	public static final String BG_STATUS_INITIATED = "APPLIED";
	public static final String BG_ACTION_INITIATE = "APPLY";
	public static final String BG_STATUS_PENDING_AT_CAO = "PENDING_AT_CAO";
	public static final String BG_NEW_LANDING_EMPLOYEE_ROLE = "SO_HQ";
	
	//@Autowired RenewBankGuaranteeRepo renewBankGuaranteeRepo;	
	//@Autowired ReleaseBankGuaranteeRepo releaseBankGuaranteeRepo;
	//@Autowired ReplaceBankGuaranteeRepo replaceBankGuaranteeRepo;

	public List<NewBankGuarantee> createNewBankGuarantee(NewBankGuaranteeContract newBankGuaranteeContract) {
		setDefaultBusinessServiceIfNull(newBankGuaranteeContract);
		List<NewBankGuarantee> insertedData = new ArrayList<>();
		
		for (NewBankGuaranteeRequest newBankGuaranteeRequest : newBankGuaranteeContract.getNewBankGuaranteeRequest()) {
			// populate audit details-
			AuditDetails auditDetails = tradeUtil
					.getAuditDetails(newBankGuaranteeContract.getRequestInfo().getUserInfo().getUuid(), true);
			newBankGuaranteeRequest.setAuditDetails(auditDetails);

			if (!StringUtils.isEmpty(newBankGuaranteeRequest.getBusinessService())
					&& newBankGuaranteeRequest.getBusinessService().equalsIgnoreCase(BUSINESSSERVICE_BG_MORTGAGE)) {
				insertedData.add(mortgageBGService
						.createBankGuarantee(newBankGuaranteeRequest, newBankGuaranteeContract.getRequestInfo())
						.toBuilder());
				continue;
			} else {
				// default set businessservice as BG_NEW as of now-
				newBankGuaranteeRequest.setBusinessService(BUSINESSSERVICE_BG_NEW);
				newBankGuaranteeRequest
						.setAssignee(tradeUtil.getFirstAssigneeByRoleBG(BG_NEW_LANDING_EMPLOYEE_ROLE,
								BUSINESSSERVICE_TENANTID, true,
								newBankGuaranteeContract.getRequestInfo()));
			}
			if (StringUtils.isEmpty(newBankGuaranteeRequest.getAction())
					&& StringUtils.isEmpty(newBankGuaranteeRequest.getId())) {
				// basic create with processinstance-
				List<String> idGenIds = enrichmentService.getIdList(newBankGuaranteeContract.getRequestInfo(),
						BUSINESSSERVICE_TENANTID, tlConfiguration.getNewBankGuaranteeApplNoIdGenName(),
						tlConfiguration.getNewBankGuaranteeApplNoIdGenFormat(), 1);
				String applicationNo = idGenIds.get(0);
				newBankGuaranteeRequest.setApplicationNumber(applicationNo);
				newBankGuaranteeRequest.setStatus(null);
				newBankGuaranteeRequest.setId(UUID.randomUUID().toString());
				newBankGuaranteeRequest.setStatus(BG_STATUS_INITIATED);
				newBankGuaranteeRequest.setAction(BG_ACTION_INITIATE);
				validateValidityFormat(newBankGuaranteeRequest.getValidity());
				NewBankGuarantee newBankGuaranteeEntity = newBankGuaranteeRequest.toBuilder();
				TradeLicenseRequest processInstanceRequest = prepareProcessInstanceRequestForNewBG(
						newBankGuaranteeRequest, newBankGuaranteeContract.getRequestInfo());
				workflowIntegrator.callWorkFlow(processInstanceRequest);
				newBankGuaranteeRepo.save(newBankGuaranteeContract);
				insertedData.add(newBankGuaranteeEntity);
			}
			else if (!StringUtils.isEmpty(newBankGuaranteeRequest.getAction())
					&& newBankGuaranteeRequest.getAction()
							.equalsIgnoreCase(BG_STATUS_PRE_SUBMIT)) {
				// if this is for creation of entries in table without workflow involvement-
				// populate applicationNumber from idgen-
				List<String> idGenIds = enrichmentService.getIdList(newBankGuaranteeContract.getRequestInfo(),
						BUSINESSSERVICE_TENANTID,
						tlConfiguration.getNewBankGuaranteeApplNoIdGenName(),
						tlConfiguration.getNewBankGuaranteeApplNoIdGenFormat(), 1);
				String applicationNo = idGenIds.get(0);
				newBankGuaranteeRequest.setApplicationNumber(applicationNo);
				newBankGuaranteeRequest.setStatus(null);
				newBankGuaranteeRequest.setId(UUID.randomUUID().toString());
				NewBankGuarantee newBankGuaranteeEntity = newBankGuaranteeRequest.toBuilder();
				newBankGuaranteeRepo.save(newBankGuaranteeContract);
				insertedData.add(newBankGuaranteeEntity);
			} else {
				// normal update to INITIATED with workflow involvement-
				List<NewBankGuarantee> newBankGuaranteeSearchResult = validateAndFetchFromDbForUpdate(
						newBankGuaranteeRequest, newBankGuaranteeContract.getRequestInfo());
				// set INITIATED status as not expected from UI-
				newBankGuaranteeRequest.setStatus(BG_STATUS_INITIATED);
				newBankGuaranteeRequest.setAction(BG_ACTION_INITIATE);
				validateValidityFormat(newBankGuaranteeRequest.getValidity());
				NewBankGuarantee newBankGuaranteeEntity = newBankGuaranteeRequest.toBuilder();
				enrichAuditDetailsOnUpdate(newBankGuaranteeRequest,newBankGuaranteeContract.getRequestInfo());
				// call workflow to insert processinstance-
				TradeLicenseRequest processInstanceRequest = prepareProcessInstanceRequestForNewBG(
						newBankGuaranteeRequest, newBankGuaranteeContract.getRequestInfo());
				workflowIntegrator.callWorkFlow(processInstanceRequest);
				newBankGuaranteeRepo.update(newBankGuaranteeContract);
				insertedData.add(newBankGuaranteeEntity);
			}
		}
		return insertedData;

	}
	
	private void setDefaultBusinessServiceIfNull(NewBankGuaranteeContract newBankGuaranteeContract) {
		// set businessservice and workflowCode as BG_NEW by default to support existing
		// flow without sending BG_NEW from UI.TODO:ask UI to send BG_NEW for usual and
		// BG_MORTGAGE for specific
		newBankGuaranteeContract.getNewBankGuaranteeRequest().stream().forEach(bankGuarantee -> {
			if (StringUtils.isEmpty(bankGuarantee.getBusinessService())) {
				bankGuarantee.setBusinessService(BUSINESSSERVICE_BG_NEW);
			}
		});
	}
	
	private TradeLicenseRequest prepareProcessInstanceRequestForUpdate(NewBankGuaranteeRequest newBankGuaranteeRequest,
			RequestInfo requestInfo) {
		if (!StringUtils.isEmpty(newBankGuaranteeRequest.getBusinessService())
				&& newBankGuaranteeRequest.getBusinessService().equalsIgnoreCase(BUSINESSSERVICE_BG_MORTGAGE)) {
			return mortgageBGService.prepareProcessInstanceRequestForNewBG(newBankGuaranteeRequest, requestInfo);
		} else
			return prepareProcessInstanceRequestForNewBG(newBankGuaranteeRequest, requestInfo);
	}
	
	private TradeLicenseRequest prepareProcessInstanceRequestForNewBG(NewBankGuaranteeRequest newBankGuaranteeRequest, RequestInfo requestInfo) {
		TradeLicenseRequest tradeLicenseRequest = new TradeLicenseRequest();
		List<TradeLicense> licenses = new ArrayList<>();
		TradeLicense tradeLicense = new TradeLicense();

		tradeLicense.setBusinessService(BUSINESSSERVICE_BG_NEW);
		tradeLicense.setAction(newBankGuaranteeRequest.getAction());
		tradeLicense.setAssignee(newBankGuaranteeRequest.getAssignee());
		tradeLicense.setApplicationNumber(newBankGuaranteeRequest.getApplicationNumber());
		tradeLicense.setWorkflowCode(BUSINESSSERVICE_BG_NEW);// workflowname
		TradeLicenseDetail tradeLicenseDetail = new TradeLicenseDetail();
		tradeLicenseDetail.setTradeType(BUSINESSSERVICE_BG_NEW);
		tradeLicense.setTradeLicenseDetail(tradeLicenseDetail);
		tradeLicense.setComment(newBankGuaranteeRequest.getComment());
		tradeLicense.setWfDocuments(newBankGuaranteeRequest.getWfDocuments());
		tradeLicense.setTenantId("hr");
		tradeLicense.setBusinessService(BUSINESSSERVICE_BG_NEW);

		licenses.add(tradeLicense);
		tradeLicenseRequest.setLicenses(licenses);
		tradeLicenseRequest.setRequestInfo(requestInfo);
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
	
	public List<NewBankGuarantee> searchNewBankGuarantee(RequestInfo requestInfo, List<String> applicationNumber,
			String loiNumber, String typeOfBg, String bgNumber, String existingBgNumber, String bankName,String licenceNumber) {
		BankGuaranteeSearchCriteria bankGuaranteeSearchCriteria = BankGuaranteeSearchCriteria.builder()
				.applicationNumber(applicationNumber).loiNumber(loiNumber).typeOfBg(typeOfBg).bgNumber(bgNumber)
				.bankName(bankName).existingBgNumber(existingBgNumber).licenceNumber(licenceNumber).build();
		List<NewBankGuaranteeRequest> newBankGuaranteeRequestData = newBankGuaranteeRepo
				.getNewBankGuaranteeData(bankGuaranteeSearchCriteria);
		//populate audit entries-
		// populate new license detail-
		//populateNewLicenseDetails(newBankGuaranteeRequestData);
		List<NewBankGuarantee> newBankGuaranteeData = newBankGuaranteeRequestData.stream()
				.map(newBankGuaranteeRequest -> newBankGuaranteeRequest.toBuilder()).collect(Collectors.toList());
		populateAuditEntries(newBankGuaranteeData);
		return newBankGuaranteeData;
	}
	
	private void populateAuditEntries(List<NewBankGuarantee> newBankGuaranteeData) {
		for (NewBankGuarantee newBankGuarantee : newBankGuaranteeData) {
			List<NewBankGuaranteeRequest> auditData = newBankGuaranteeRepo
					.getBankGuaranteeAuditEntries(newBankGuarantee.getApplicationNumber());
			newBankGuarantee.setAuditEntries(auditData);
		}
	}
	
	private void populateNewLicenseDetails(List<NewBankGuaranteeRequest> newBankGuaranteeRequestData) {
		for (NewBankGuaranteeRequest newBankGuaranteeRequest : newBankGuaranteeRequestData) {
			LicenseServiceDao license = licenseService.findByLoiNumber(newBankGuaranteeRequest.getLoiNumber());
			newBankGuaranteeRequest.setLicense(license);
		}
	}
	
	public List<NewBankGuarantee> updateNewBankGuarantee(NewBankGuaranteeContract newBankGuaranteeContract) {
		List<NewBankGuarantee> updatedData = new ArrayList<>();
		
			
		for(NewBankGuaranteeRequest newBankGuaranteeRequest:newBankGuaranteeContract.getNewBankGuaranteeRequest()) {
			List<NewBankGuarantee> newBankGuaranteeSearchResult = validateAndFetchFromDbForUpdate(
					newBankGuaranteeRequest, newBankGuaranteeContract.getRequestInfo());
			
			Long time = System.currentTimeMillis();
			AuditDetails auditDetails = newBankGuaranteeSearchResult.get(0).getAuditDetails();
			auditDetails.setLastModifiedBy(newBankGuaranteeContract.getRequestInfo().getUserInfo().getUuid());
			auditDetails.setLastModifiedTime(time);
			newBankGuaranteeRequest.setAuditDetails(auditDetails);
		
			
			String businessService = getBusinessServiceName(newBankGuaranteeRequest);
			String currentStatus = newBankGuaranteeSearchResult.get(0).getStatus();
			BusinessService workflow = workflowService.getBusinessService(BUSINESSSERVICE_TENANTID,
					newBankGuaranteeContract.getRequestInfo(), businessService);
			validateUpdateRoleAndActionFromWorkflow(workflow, currentStatus, newBankGuaranteeRequest,
					newBankGuaranteeContract.getRequestInfo());
			setValidBgStatusOnApproval(newBankGuaranteeRequest);
			setBgStatusOnRelease(newBankGuaranteeRequest);
			enrichAuditDetailsOnUpdate(newBankGuaranteeRequest, newBankGuaranteeContract.getRequestInfo());
			enrichAssigneeOnApproval(newBankGuaranteeRequest, newBankGuaranteeSearchResult.get(0));

			// call workflow to insert processinstance-
			boolean isWorkflowBasedUpdate = StringUtils.isEmpty(newBankGuaranteeRequest.getAction()) ? false : true;
			if (isWorkflowBasedUpdate) {
				TradeLicenseRequest processInstanceRequest = prepareProcessInstanceRequestForUpdate(
						newBankGuaranteeRequest, newBankGuaranteeContract.getRequestInfo());
				workflowIntegrator.callWorkFlow(processInstanceRequest);
			}
			if(BG_NEW_ACTION_RELEASE.equals(newBankGuaranteeRequest.getUpdateType())) {
				NewBankGuaranteeRequest newBankGuaranteeRespondData=new NewBankGuaranteeRequest(newBankGuaranteeSearchResult.get(0));
				setReleaseRequestData(newBankGuaranteeRespondData,newBankGuaranteeRequest,newBankGuaranteeContract.getRequestInfo());
				newBankGuaranteeContract.setNewBankGuaranteeRequest(Arrays.asList(newBankGuaranteeRespondData)); 
				newBankGuaranteeRepo.updateRelease(newBankGuaranteeContract);
				NewBankGuarantee newBankGuarantee = newBankGuaranteeContract.getNewBankGuaranteeRequest().get(0).toBuilder();
				updatedData.add(newBankGuarantee);
			}else if(BG_NEW_ACTION_EXTEND.equals(newBankGuaranteeRequest.getUpdateType())) {
				NewBankGuaranteeRequest newBankGuaranteeRespondData=new NewBankGuaranteeRequest(newBankGuaranteeSearchResult.get(0));
				setExtendRequestData(newBankGuaranteeRespondData,newBankGuaranteeRequest,newBankGuaranteeContract.getRequestInfo());
				newBankGuaranteeContract.setNewBankGuaranteeRequest(Arrays.asList(newBankGuaranteeRespondData)); 
				newBankGuaranteeRepo.updateExtend(newBankGuaranteeContract);
				NewBankGuarantee newBankGuarantee = newBankGuaranteeContract.getNewBankGuaranteeRequest().get(0).toBuilder();
				updatedData.add(newBankGuarantee);
			}else{
			// push to update-
				newBankGuaranteeRepo.update(newBankGuaranteeContract);
				NewBankGuarantee newBankGuarantee = newBankGuaranteeRequest.toBuilder();
				updatedData.add(newBankGuarantee);
			}
		}
		return updatedData;
		
	}
	
	private void setReleaseRequestData(NewBankGuaranteeRequest newBankGuaranteeRespondData,NewBankGuaranteeRequest newBankGuaranteeRequest,RequestInfo requestInfo) {
		newBankGuaranteeRespondData.setRelease(newBankGuaranteeRequest.getRelease());
		newBankGuaranteeRespondData.setBankGuaranteeReplacedWith(newBankGuaranteeRequest.getBankGuaranteeReplacedWith());
		newBankGuaranteeRespondData.setReasonForReplacement(newBankGuaranteeRequest.getReasonForReplacement());
		newBankGuaranteeRespondData.setApplicationCerficifate(newBankGuaranteeRequest.getApplicationCerficifate());
		newBankGuaranteeRespondData.setApplicationCerficifateDescription(newBankGuaranteeRequest.getApplicationCerficifateDescription());
		newBankGuaranteeRespondData.setCompletionCertificate(newBankGuaranteeRequest.getCompletionCertificate());
		newBankGuaranteeRespondData.setCompletionCertificateDescription(newBankGuaranteeRequest.getCompletionCertificateDescription());
		newBankGuaranteeRespondData.setAnyOtherDocument(newBankGuaranteeRequest.getAnyOtherDocument());
		newBankGuaranteeRespondData.setAnyOtherDocumentDescription(newBankGuaranteeRequest.getAnyOtherDocumentDescription());
		
		newBankGuaranteeRespondData.setWfDocuments(newBankGuaranteeRequest.getWfDocuments());
		newBankGuaranteeRespondData.setComment(newBankGuaranteeRequest.getComment());
		newBankGuaranteeRespondData.setApplicationNumber(newBankGuaranteeRequest.getApplicationNumber());
		newBankGuaranteeRespondData.setBusinessService(BUSINESSSERVICE_BG_NEW);
		newBankGuaranteeRespondData.setAssignee(tradeUtil.getFirstAssigneeByRoleBG(BG_NEW_LANDING_EMPLOYEE_ROLE,BUSINESSSERVICE_TENANTID, true,requestInfo));
		newBankGuaranteeRespondData.setStatus(BG_STATUS_INITIATED);
		newBankGuaranteeRespondData.setAction(BG_ACTION_INITIATE);
		TradeLicenseRequest processInstanceRequest = prepareProcessInstanceRequestForNewBG(newBankGuaranteeRequest, requestInfo);
		workflowIntegrator.callWorkFlow(processInstanceRequest);


	}
	
	private void setExtendRequestData(NewBankGuaranteeRequest newBankGuaranteeRespondData,NewBankGuaranteeRequest newBankGuaranteeRequest,RequestInfo requestInfo) {
			
		newBankGuaranteeRespondData.setDateOfAmendment(newBankGuaranteeRequest.getDateOfAmendment());
		newBankGuaranteeRespondData.setAmendmentExpiryDate(newBankGuaranteeRequest.getAmendmentExpiryDate());
		newBankGuaranteeRespondData.setAmendmentClaimExpiryDate(newBankGuaranteeRequest.getAmendmentClaimExpiryDate());
		newBankGuaranteeRespondData.setIssuingBank(newBankGuaranteeRequest.getIssuingBank());
		newBankGuaranteeRespondData.setBankGurenteeCertificate(newBankGuaranteeRequest.getBankGurenteeCertificate());
		newBankGuaranteeRespondData.setBankGurenteeCertificateDescription(newBankGuaranteeRequest.getBankGurenteeCertificateDescription());
		newBankGuaranteeRespondData.setAnyOtherDocument(newBankGuaranteeRequest.getAnyOtherDocument());
		newBankGuaranteeRespondData.setAnyOtherDocumentDescription(newBankGuaranteeRequest.getAnyOtherDocumentDescription());
		
		newBankGuaranteeRespondData.setWfDocuments(newBankGuaranteeRequest.getWfDocuments());
		newBankGuaranteeRespondData.setComment(newBankGuaranteeRequest.getComment());
		newBankGuaranteeRespondData.setApplicationNumber(newBankGuaranteeRequest.getApplicationNumber());
		newBankGuaranteeRespondData.setBusinessService(BUSINESSSERVICE_BG_NEW);
		newBankGuaranteeRespondData.setAssignee(tradeUtil.getFirstAssigneeByRoleBG(BG_NEW_LANDING_EMPLOYEE_ROLE,BUSINESSSERVICE_TENANTID, true,requestInfo));
		newBankGuaranteeRespondData.setStatus(BG_STATUS_INITIATED);
		newBankGuaranteeRespondData.setAction(BG_ACTION_INITIATE);
		TradeLicenseRequest processInstanceRequest = prepareProcessInstanceRequestForNewBG(newBankGuaranteeRequest, requestInfo);
		workflowIntegrator.callWorkFlow(processInstanceRequest);
	}
	
	public void getKhasraDetails(String loiNumber) {
		LicenseServiceDao license = licenseService.findByLoiNumber(loiNumber);
	}
	
	private String getBusinessServiceName(NewBankGuaranteeRequest newBankGuaranteeRequest) {
		if (!StringUtils.isEmpty(newBankGuaranteeRequest.getBusinessService())
				&& newBankGuaranteeRequest.getBusinessService().equalsIgnoreCase(BUSINESSSERVICE_BG_MORTGAGE)) {
			return BUSINESSSERVICE_BG_MORTGAGE;
		} else {
			return BUSINESSSERVICE_BG_NEW;
		}
	}
	
	private void enrichAssigneeOnApproval(NewBankGuaranteeRequest bankGuaranteeRequest,
			NewBankGuarantee bankGuaranteeInDB) {
		if (!StringUtils.isEmpty(bankGuaranteeInDB.getStatus())
				&& bankGuaranteeInDB.getStatus().equalsIgnoreCase(BG_STATUS_PENDING_AT_CAO)
				&& !StringUtils.isEmpty(bankGuaranteeRequest.getAction())
				&& bankGuaranteeRequest.getAction().equalsIgnoreCase(BG_NEW_ACTION_APPROVE)) {
			List<String> assignee = new ArrayList<>();
			assignee.add(bankGuaranteeInDB.getAuditDetails().getCreatedBy());
			bankGuaranteeRequest.setAssignee(assignee);
		}
	}
	
	private List<NewBankGuarantee> validateAndFetchFromDbForUpdate(NewBankGuaranteeRequest newBankGuaranteeRequest, RequestInfo requestInfo) {
		if (Objects.isNull(newBankGuaranteeRequest)) {
			throw new CustomException("NewBankGuaranteeRequest must not be null",
					"NewBankGuaranteeRequest must not be null");
		}
		if (StringUtils.isEmpty(newBankGuaranteeRequest.getApplicationNumber())) {
			throw new CustomException("ApplicationNumber must not be null", "ApplicationNumber must not be null");
		}
		List<String> applicationNos = new ArrayList<>();
		applicationNos.add(newBankGuaranteeRequest.getApplicationNumber());
		List<NewBankGuarantee> newBankGuaranteeSearchResult = searchNewBankGuarantee(
				requestInfo, applicationNos, null, null, null, null, null, null);
		if (CollectionUtils.isEmpty(newBankGuaranteeSearchResult) || newBankGuaranteeSearchResult.size() > 1) {
			throw new CustomException(
					"Found none or multiple new bank guarantee applications with applicationNumber:"
							+ newBankGuaranteeRequest.getApplicationNumber(),
					"Found none or multiple new bank guarantee applications with applicationNumber:"
							+ newBankGuaranteeRequest.getApplicationNumber());
		}
		return newBankGuaranteeSearchResult;
	}
	
	private void validateExtendOrRelease(NewBankGuaranteeRequest newBankGuaranteeRequest, String currentStatus,
			String currentBgStatus) {
		if (Objects.nonNull(newBankGuaranteeRequest.getAction())
				&& BG_NEW_ACTION_EXTEND
						.equals(newBankGuaranteeRequest.getAction())
				&& !(currentStatus.equals(BG_NEW_STATUS_APPROVED) && currentBgStatus.equals(BG_STATUS_VALID))) {
			throw new CustomException("Cannot extend the Bank guarantee as it is not valid/approved",
					"Cannot extend the Bank guarantee as it is not valid/approved");
		} else if (Objects.nonNull(newBankGuaranteeRequest.getAction())
				&& BG_NEW_ACTION_RELEASE
						.equals(newBankGuaranteeRequest.getAction())
				&& !(currentStatus.equals(BG_NEW_STATUS_APPROVED) && currentBgStatus.equals(BG_STATUS_VALID))) {
			throw new CustomException("Cannot release the Bank guarantee as it is not valid/approved",
					"Cannot release the Bank guarantee as it is not valid/approved");
		}
	}
	
	private void setValidBgStatusOnApproval(NewBankGuaranteeRequest newBankGuaranteeRequest) {
		if (Objects.nonNull(newBankGuaranteeRequest.getAction())
				&& BG_NEW_ACTION_APPROVE
						.equals(newBankGuaranteeRequest.getAction())) {
			newBankGuaranteeRequest.setBankGuaranteeStatus(BG_STATUS_VALID);
		}
	}
	
	private void setBgStatusOnRelease(NewBankGuaranteeRequest newBankGuaranteeRequest) {
		if (Objects.nonNull(newBankGuaranteeRequest.getAction())
				&& BG_NEW_ACTION_RELEASE
						.equals(newBankGuaranteeRequest.getAction())) {
			newBankGuaranteeRequest.setBankGuaranteeStatus(BG_STATUS_RELEASED);
		}
	}

	private void validateUpdateRoleAndActionFromWorkflow(BusinessService workflow, String currentStatus,
			NewBankGuaranteeRequest newBankGuaranteeRequest, RequestInfo requestInfo) {
		if (StringUtils.isEmpty(newBankGuaranteeRequest.getAction())) {
			// support action less update without workflow involvement
			log.info("action is null in update call. Allowing update without workflow involvement");
			return;
		}
		// validate Action-
		String currentActionFromRequest = newBankGuaranteeRequest.getAction();
		Optional<State> currentWorkflowStateOptional = workflow.getStates().stream()
				.filter(state -> state.getState().equals(currentStatus)).findFirst();
		if (!currentWorkflowStateOptional.isPresent()) {
			throw new CustomException("workflow State not found:" + currentStatus,
					"workflow State not found:" + currentStatus);
		}
		State currentWorkflowState = currentWorkflowStateOptional.get();
		List<Action> permissibleActions = currentWorkflowState.getActions();
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
		List<Role> rolesFromUserInfo = requestInfo.getUserInfo().getRoles();
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
		newBankGuaranteeRequest.setStatus(nextStateName);
	}

	private void enrichAuditDetailsOnUpdate(NewBankGuaranteeRequest newBankGuaranteeRequest, RequestInfo requestInfo) {
		AuditDetails auditDetails = tradeUtil
				.getAuditDetails(requestInfo.getUserInfo().getUuid(), false);
		auditDetails
				.setCreatedBy(newBankGuaranteeRequest.getAuditDetails().getCreatedBy());
		auditDetails.setCreatedTime(
				newBankGuaranteeRequest.getAuditDetails().getCreatedTime());
		newBankGuaranteeRequest.setAuditDetails(auditDetails);
	}
	
	public List<Map<String,Object>> getDropDownList(int type, RequestInfo requestInfo) {
		List<Map<String,Object>> dropList=newBankGuaranteeRepo.getDropDownList();
		List<Map<String,Object>> dropDoneList=new ArrayList<>();
		if(type==1) {
			dropDoneList=dropList.stream().filter(bg->bg.get("application_number")!=null&&!bg.get("application_number").equals("")).map(bg->{
				Map<String,Object> val=new HashMap();
				val.put("label", bg.get("application_number"));
				val.put("id",bg.get("id"));
				return val;	
			}).collect(Collectors.toList());
		}else if(type==2){
			dropDoneList=dropList.stream().filter(bg->bg.get("loi_number")!=null&&!bg.get("loi_number").equals("")).map(bg->{
				Map<String,Object> val=new HashMap();
				val.put("label", bg.get("loi_number"));
				val.put("id",bg.get("id"));
				return val;	
		    }).collect(Collectors.toList());
		}else {
			dropDoneList=dropList.stream().filter(bg->bg.get("licence_number")!=null&&!bg.get("licence_number").equals("")).map(bg->{
				Map<String,Object> val=new HashMap();
				val.put("label", bg.get("licence_number"));
				val.put("id",bg.get("id"));
				return val;	
			}).collect(Collectors.toList());
		}
		
		return dropDoneList;
		
	}
	
	/*
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
