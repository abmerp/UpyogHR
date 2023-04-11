package org.egov.tl.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import org.apache.commons.collections.map.HashedMap;
import org.egov.common.contract.request.RequestInfo;
import org.egov.common.contract.request.Role;
import org.egov.tl.abm.newservices.contract.ApprovalStandardContract;
import org.egov.tl.abm.newservices.entity.ApprovalStandardEntity;
import org.egov.tl.config.TLConfiguration;
import org.egov.tl.producer.Producer;
import org.egov.tl.repository.IdGenRepository;
import org.egov.tl.repository.ServiceRequestRepository;
import org.egov.tl.repository.rowmapper.ApprovalStandardRowMapper;
import org.egov.tl.repository.rowmapper.TransferRowMapper;
import org.egov.tl.util.TradeUtil;
import org.egov.tl.web.models.AuditDetails;
import org.egov.tl.web.models.TradeLicense;
import org.egov.tl.web.models.TradeLicenseDetail;
import org.egov.tl.web.models.TradeLicenseRequest;
import org.egov.tl.web.models.Transfer;
import org.egov.tl.web.models.TransferOfLicence;
import org.egov.tl.web.models.TransferOfLicenseRequest;
import org.egov.tl.web.models.workflow.Action;
import org.egov.tl.web.models.workflow.BusinessService;
import org.egov.tl.web.models.workflow.State;
import org.egov.tl.workflow.WorkflowIntegrator;
import org.egov.tl.workflow.WorkflowService;
import org.egov.tracer.model.CustomException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class TransferOfLicenseServices {

	@Autowired
	ObjectMapper mapper;
	@Value("${persister.create.transfer.licence.topic}")
	private String transferTopic;
	
	@Value("${persister.create.transfer.licence.topic}")
	private String tranferUpdateTopic;
	private static final String businessService_TRANSFER = "TRANSFER_OF_LICIENCE";

	private static final String SENDBACK_STATUS = "SEND_BACK_TO_APPLICANT";

	private static final String CITIZEN_UPDATE_ACTION = "FORWARD";
	@Autowired
	private Producer producer;
	@Autowired
	private ApprovalStandardRowMapper approvalStandardRowMapper;
	@Autowired
	private NamedParameterJdbcTemplate namedParameterJdbcTemplate;

	@Autowired
	private JdbcTemplate jdbcTemplate;
	@Autowired
	private TradeUtil tradeUtil;

	@Autowired
	private WorkflowIntegrator wfIntegrator;

	@Autowired
	private TLConfiguration config;

	@Autowired
	private IdGenRepository idGenRepository;
	@Autowired
	private TransferRowMapper transferRowMapper;

	@Autowired
	private WorkflowService workflowService;

	@Autowired
	private ServiceRequestRepository serviceRequestRepository;

	@Autowired
	ServicePlanService servicePlanService;

	public List<Transfer> create(TransferOfLicenseRequest transferOfLicenseRequest) throws JsonProcessingException {

		String licenseNumber = transferOfLicenseRequest.getTransfer().get(0).getLicenseNo();

		String uuid = transferOfLicenseRequest.getRequestInfo().getUserInfo().getUuid();

		AuditDetails auditDetails = tradeUtil.getAuditDetails(uuid, true);

		RequestInfo requestInfo = transferOfLicenseRequest.getRequestInfo();
		List<Transfer> transferList = new ArrayList<>();
		TransferOfLicence transferOfLicence = transferOfLicenseRequest.getTransfer().get(0).getTransferOfLicence();
		Transfer transfers = new Transfer();
		String data = mapper.writeValueAsString(transferOfLicence);
		JsonNode jsonNode = mapper.readTree(data);
		//transfers.setTransferOfLicence(transferOfLicence);
		transfers.setAdditionalDetails(jsonNode);
		transfers.setAction(transferOfLicenseRequest.getTransfer().get(0).getAction());
		transfers.setTenantId(transferOfLicenseRequest.getTransfer().get(0).getTenantId());
		transfers.setAraeInAcres(transferOfLicenseRequest.getTransfer().get(0).getAraeInAcres());
		transfers.setLicenseNo(transferOfLicenseRequest.getTransfer().get(0).getLicenseNo());
		transfers.setSelectType(transferOfLicenseRequest.getTransfer().get(0).getSelectType());
		transferList.add(transfers);
		for (Transfer transferRequest : transferList) {

			List<String> applicationNumbers = null;
			int count = 1;
			List<Transfer> transferSearch = search(requestInfo, transferRequest.getLicenseNo(),
					transferRequest.getApplicationNumber());
			if (!CollectionUtils.isEmpty(transferSearch) || transferSearch.size() > 1) {
				throw new CustomException("Already Found  or multiple transfer of licence applications with LoiNumber.",
						"Already Found or multiple transfer of licence applications with LoiNumber.");
			}

			transferRequest.setId(UUID.randomUUID().toString());
			transferRequest.setAssignee(Arrays
					.asList(servicePlanService.assignee("CTP_HR", transferRequest.getTenantId(), true, requestInfo)));
//			approvalStandardRequest.setAssignee(Arrays.asList("f9b7acaf-c1fb-4df2-ac10-83b55238a724"));
			applicationNumbers = servicePlanService.getIdList(transferOfLicenseRequest.getRequestInfo(),
					transferRequest.getTenantId(), config.getTransferName(), config.getTransferFormat(), count);

			transferRequest.setBusinessService(businessService_TRANSFER);
			transferRequest.setWorkflowCode(businessService_TRANSFER);

			transferRequest.setAuditDetails(auditDetails);
			transferRequest.setApplicationNumber(applicationNumbers.get(0));

			TradeLicenseRequest prepareProcessInstanceRequest = prepareProcessInstanceRequest(transferRequest,
					requestInfo, businessService_TRANSFER);

			wfIntegrator.callWorkFlow(prepareProcessInstanceRequest);

			transferRequest.setStatus(prepareProcessInstanceRequest.getLicenses().get(0).getStatus());

		}
		transferOfLicenseRequest.setTransfer(transferList);

		producer.push(transferTopic, transferOfLicenseRequest);
		return transferList;

	}

	public List<Transfer> search(RequestInfo requestInfo, String licenseNo, String applicationNumber) {
		List<Object> preparedStatement = new ArrayList<>();

		Map<String, String> paramMap = new HashedMap();
		Map<String, List<String>> paramMapList = new HashedMap();
		StringBuilder builder;

		String query = "SELECT id, area_in_acres, license_no, application_number, tenant_id, action, businessservice, additiona_details, created_by, last_modify_by, created_time, last_modified_time, status, transfer_of_licence\r\n"
				+ "	FROM public.eg_transfer_of_licence_service" + " Where ";
		builder = new StringBuilder(query);

		List<Transfer> Result = null;
		if (licenseNo != null) {
			builder.append(" license_no= :LN");
			paramMap.put("LN", licenseNo);
			preparedStatement.add(licenseNo);
			Result = namedParameterJdbcTemplate.query(builder.toString(), paramMap, transferRowMapper);
		} else if (applicationNumber != null) {
			List<String> applicationNumberList = Arrays.asList(applicationNumber.split(","));
			log.info("applicationNumberList" + applicationNumberList);
			if (applicationNumberList != null) {
				builder.append(" application_number in ( :AN )");
				paramMapList.put("AN", applicationNumberList);
				preparedStatement.add(applicationNumberList);
				Result = namedParameterJdbcTemplate.query(builder.toString(), paramMapList, transferRowMapper);
			}

		} else if ((requestInfo.getUserInfo().getUuid() != null)) {
			builder.append(" created_by= :CB");
			paramMap.put("CB", requestInfo.getUserInfo().getUuid());
			preparedStatement.add(requestInfo.getUserInfo().getUuid());
			Result = namedParameterJdbcTemplate.query(builder.toString(), paramMap, transferRowMapper);

		}
		return Result;

	}
	public List<Transfer> Update(TransferOfLicenseRequest transferOfLicenseRequest) {

		String uuid = transferOfLicenseRequest.getRequestInfo().getUserInfo().getUuid();

		AuditDetails auditDetails = tradeUtil.getAuditDetails(uuid, false);

		RequestInfo requestInfo = transferOfLicenseRequest.getRequestInfo();

		List<Transfer> transferList = transferOfLicenseRequest.getTransfer();

		for (Transfer transfer : transferList) {

			if (Objects.isNull(transferOfLicenseRequest)
					|| Objects.isNull(transferOfLicenseRequest.getTransfer())) {
				throw new CustomException("transfer of licence must not be null",
						"transfer of licence must not be null");
			}

			if (StringUtils.isEmpty(transfer.getApplicationNumber())) {
				throw new CustomException("ApplicationNumber must not be null", "ApplicationNumber must not be null");
			}

			List<Transfer> transferSearch = search(requestInfo,
					transfer.getLicenseNo(), transfer.getApplicationNumber());
			if (CollectionUtils.isEmpty(transferSearch) || transferSearch.size() > 1) {
				throw new CustomException("Found none or multiple transfer of licence applications with applicationNumber.",
						"Found none or multiple transfer of licence applications with applicationNumber.");
			}

			transfer.setBusinessService(transfer.getBusinessService());
			transfer.setWorkflowCode(transfer.getBusinessService());

			// EMPLOYEE RUN THE APPLICATION NORMALLY
			if (!transfer.getStatus().equalsIgnoreCase(SENDBACK_STATUS) && !usercheck(requestInfo)) {

				String currentStatus = transferSearch.get(0).getStatus();

				BusinessService workflow = workflowService.getBusinessService(transfer.getTenantId(),
						transferOfLicenseRequest.getRequestInfo(), transfer.getBusinessService());

				validateUpdateRoleAndActionFromWorkflow(workflow, currentStatus, transferOfLicenseRequest,
						transfer);

				transfer.setAuditDetails(auditDetails);

				TradeLicenseRequest prepareProcessInstanceRequest = prepareProcessInstanceRequest(
						transfer, requestInfo, transfer.getBusinessService());

				wfIntegrator.callWorkFlow(prepareProcessInstanceRequest);

				transfer.setStatus(prepareProcessInstanceRequest.getLicenses().get(0).getStatus());

			}

			// CITIZEN MODIFY THE APPLICATION WHEN EMPLOYEE SENDBACK TO CITIZEN
			else if ((transfer.getStatus().equalsIgnoreCase(SENDBACK_STATUS)) && usercheck(requestInfo)) {

				String currentStatus = transferSearch.get(0).getStatus();

				transfer.setAssignee(Arrays.asList(servicePlanService.assignee("CAO",
						transfer.getTenantId(), true, requestInfo)));

				transfer.setAction(CITIZEN_UPDATE_ACTION);

				BusinessService workflow = workflowService.getBusinessService(transfer.getTenantId(),
						transferOfLicenseRequest.getRequestInfo(), transfer.getBusinessService());

				validateUpdateRoleAndActionFromWorkflow(workflow, currentStatus, transferOfLicenseRequest,
						transfer);

				transfer.setAuditDetails(auditDetails);

				TradeLicenseRequest prepareProcessInstanceRequest = prepareProcessInstanceRequest(
						transfer, requestInfo, transfer.getBusinessService());

				wfIntegrator.callWorkFlow(prepareProcessInstanceRequest);

				transfer.setStatus(prepareProcessInstanceRequest.getLicenses().get(0).getStatus());

			}
		}

		transferOfLicenseRequest.setTransfer(transferList);

		producer.push(tranferUpdateTopic, transferOfLicenseRequest);

		return transferList;

	}

	private boolean usercheck(RequestInfo requestInfo) {
		List<Role> roles = requestInfo.getUserInfo().getRoles();
		for (Role role : roles) {
			if (role.getCode().equalsIgnoreCase("BPA_BUILDER") || role.getCode().equalsIgnoreCase("BPA_DEVELOPER")) {
				return true;
			}
		}
		return false;
	}

	private void validateUpdateRoleAndActionFromWorkflow(BusinessService workflow, String currentStatus,
			TransferOfLicenseRequest transferOfLicenseRequest, Transfer transfer) {
		// validate Action-
		Optional<State> currentWorkflowStateOptional = workflow.getStates().stream()
				.filter(state -> state.getState().equals(currentStatus)).findFirst();
		if (!currentWorkflowStateOptional.isPresent()) {
			throw new CustomException("workflow State not found:" + currentStatus,
					"workflow State not found:" + currentStatus);
		}
		State currentWorkflowState = currentWorkflowStateOptional.get();
		List<Action> permissibleActions = currentWorkflowState.getActions();
		String currentActionFromRequest = transfer.getAction();
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
		List<Role> rolesFromUserInfo = transferOfLicenseRequest.getRequestInfo().getUserInfo().getRoles();
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
		transfer.setStatus(nextStateName);
	}

	private TradeLicenseRequest prepareProcessInstanceRequest(Transfer transferRequest, RequestInfo requestInfo,
			String bussinessServicename) {

		TradeLicenseRequest tradeLicenseASRequest = new TradeLicenseRequest();
		TradeLicense tradeLicenseAS = new TradeLicense();
		List<TradeLicense> tradeLicenseASlist = new ArrayList<>();
		tradeLicenseAS.setBusinessService(transferRequest.getBusinessService());
		tradeLicenseAS.setAction(transferRequest.getAction());
		tradeLicenseAS.setAssignee(transferRequest.getAssignee());
		tradeLicenseAS.setApplicationNumber(transferRequest.getApplicationNumber());
		tradeLicenseAS.setWorkflowCode(transferRequest.getWorkflowCode());
		TradeLicenseDetail tradeLicenseDetail = new TradeLicenseDetail();
		tradeLicenseDetail.setTradeType(bussinessServicename);
		tradeLicenseAS.setTradeLicenseDetail(tradeLicenseDetail);
		tradeLicenseAS.setComment(transferRequest.getComment());
		tradeLicenseAS.setWfDocuments(transferRequest.getWfDocuments());
		tradeLicenseAS.setTenantId(transferRequest.getTenantId());
		tradeLicenseAS.setBusinessService(bussinessServicename);

		tradeLicenseASRequest.setRequestInfo(requestInfo);
		tradeLicenseASlist.add(tradeLicenseAS);
		tradeLicenseASRequest.setLicenses(tradeLicenseASlist);

		return tradeLicenseASRequest;
	}
}
