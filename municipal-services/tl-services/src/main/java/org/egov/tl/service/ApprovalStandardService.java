package org.egov.tl.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import org.apache.commons.collections.map.HashedMap;
import org.egov.common.contract.request.RequestInfo;
import org.egov.common.contract.request.Role;
import org.egov.tl.abm.newservices.contract.ApprovalStandardContract;
import org.egov.tl.abm.newservices.contract.ApprovalStandardRequest;
import org.egov.tl.abm.newservices.contract.TransferOfLicenseContract;
import org.egov.tl.abm.newservices.entity.ApprovalStandardEntity;
import org.egov.tl.config.TLConfiguration;
import org.egov.tl.producer.Producer;
import org.egov.tl.repository.IdGenRepository;
import org.egov.tl.repository.ServiceRequestRepository;
import org.egov.tl.repository.rowmapper.ApprovalStandardRowMapper;
import org.egov.tl.util.TradeUtil;
import org.egov.tl.web.models.AuditDetails;
import org.egov.tl.web.models.ServicePlanContract;
import org.egov.tl.web.models.ServicePlanRequest;
import org.egov.tl.web.models.TradeLicense;
import org.egov.tl.web.models.TradeLicenseDetail;
import org.egov.tl.web.models.TradeLicenseRequest;
import org.egov.tl.web.models.TradeLicenseSearchCriteria;
import org.egov.tl.web.models.Transfer;
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
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.val;
import lombok.extern.slf4j.Slf4j;
import net.minidev.json.JSONObject;

@Slf4j
@Service
public class ApprovalStandardService {
	@Value("${persister.create.approval.standard.topic}")
	private String approvaltopic;
	@Value("${tcp.employee.ctp}")
	private String ctpUser;
	@Value("${persister.update.approval.standard.topic}")
	private String approvalUpdateTopic;

	// private static final String businessService_AS = "APPROVAL_OF_STANDARD";

	private static final String SENDBACK_STATUS = "SENDBACK_TO_APPLICANT";

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
	private WorkflowService workflowService;

	@Autowired
	private ServiceRequestRepository serviceRequestRepository;

	@Autowired
	ObjectMapper mapper;
	@Autowired
	ServicePlanService servicePlanService;
	@Autowired
	GenerateTcpNumbers generateTcpNumbers;

	public List<ApprovalStandardEntity> createNewServic(ApprovalStandardContract approvalStandardContract)
			throws JsonProcessingException {

		String licenseNumber = approvalStandardContract.getApprovalStandardRequest().getLicenseNo();

		String uuid = approvalStandardContract.getRequestInfo().getUserInfo().getUuid();

		AuditDetails auditDetails = tradeUtil.getAuditDetails(uuid, true);

		RequestInfo requestInfo = approvalStandardContract.getRequestInfo();
		// List<ApprovalStandardEntity> approvalList =
		// approvalStandardContract.getApprovalStandardRequest();
		// for (ApprovalStandardEntity approvalStandardRequest : approvalList) {
		ApprovalStandardEntity approvalStandardRequest = approvalStandardContract.getApprovalStandardRequest();
		List<String> applicationNumbers = null;
		int count = 1;
		List<ApprovalStandardEntity> searchApprovalPlan = searchApprovalStandard(requestInfo,
				approvalStandardRequest.getLicenseNo(), approvalStandardRequest.getApplicationNumber());
		if (!CollectionUtils.isEmpty(searchApprovalPlan) || searchApprovalPlan.size() > 1) {

			throw new CustomException(
					"Already Found  or multiple approval of standard design applications with LoiNumber.",
					"Already Found or multiple approval of standard design applications with LoiNumber.");
		}

		applicationNumbers = servicePlanService.getIdList(approvalStandardContract.getRequestInfo(),
				approvalStandardRequest.getTenantId(), config.getApprovalStandardApplicationName(),
				config.getApprovalStandardformat(), count);

		approvalStandardRequest.setAuditDetails(auditDetails);
		approvalStandardRequest.setApplicationNumber(applicationNumbers.get(0));

		// }
//		ApprovalStandardEntity approvalStandardEntity = makePayment(licenseNumber, requestInfo);
//		approvalStandardRequest.setTcpApplicationNumber(approvalStandardEntity.getTcpApplicationNumber());
//		approvalStandardRequest.setTcpCaseNumber(approvalStandardEntity.getTcpCaseNumber());
//		approvalStandardRequest.setTcpDairyNumber(approvalStandardEntity.getTcpDairyNumber());
//		approvalStandardContract.setApprovalStandardRequest(approvalStandardRequest);

		log.info(approvaltopic);

		producer.push(approvaltopic, approvalStandardContract);
		List<ApprovalStandardEntity> approvalStandardEntity = makePayment(approvalStandardContract);
		return approvalStandardEntity;

	}

	public List<ApprovalStandardEntity> searchApprovalStandard(RequestInfo requestInfo, String licenseNumbers,
			String applicationNumber) {
		List<Object> preparedStatement = new ArrayList<>();

		Map<String, String> paramMap = new HashedMap();
		Map<String, List<String>> paramMapList = new HashedMap();
		StringBuilder builder;

		String query = "SELECT license_no, standarddrawingdesigns, anyotherdoc, created_by, created_time, last_modified_by, last_modified_time, application_number, tenantid, id, action, status, business_service, comment, workflow_code, tcpapplicationnumber, tcpcasenumber, tcpdairynumber, additionaldetails,newadditionaldetails\r\n"
				+ "	FROM public.eg_approval_standard " + " Where ";
		builder = new StringBuilder(query);

		List<ApprovalStandardEntity> Result = null;
		if (licenseNumbers != null) {

			builder.append(" license_no= :LN");
			paramMap.put("LN", licenseNumbers);
			preparedStatement.add(licenseNumbers);
			Result = namedParameterJdbcTemplate.query(builder.toString(), paramMap, approvalStandardRowMapper);
		} else if (applicationNumber != null) {
			List<String> applicationNumberList = Arrays.asList(applicationNumber.split(","));
			log.info("applicationNumberList" + applicationNumberList);
			if (applicationNumberList != null) {
				builder.append(" application_number in ( :AN )");
				paramMapList.put("AN", applicationNumberList);
				preparedStatement.add(applicationNumberList);
				Result = namedParameterJdbcTemplate.query(builder.toString(), paramMapList, approvalStandardRowMapper);
			}

		} else if ((requestInfo.getUserInfo().getUuid() != null)) {
			builder.append(" created_by= :CB");
			paramMap.put("CB", requestInfo.getUserInfo().getUuid());
			preparedStatement.add(requestInfo.getUserInfo().getUuid());
			Result = namedParameterJdbcTemplate.query(builder.toString(), paramMap, approvalStandardRowMapper);

		}

		return Result;

	}

	public List<ApprovalStandardEntity> Update(ApprovalStandardRequest approvalStandardContract) {

		String uuid = approvalStandardContract.getRequestInfo().getUserInfo().getUuid();

		AuditDetails auditDetails = tradeUtil.getAuditDetails(uuid, false);

		RequestInfo requestInfo = approvalStandardContract.getRequestInfo();

		List<ApprovalStandardEntity> approvalStandardEntityList = approvalStandardContract.getApprovalStandardRequest();

		for (ApprovalStandardEntity approvalStandardEntity : approvalStandardEntityList) {
			// ApprovalStandardEntity approvalStandardEntity =
			// approvalStandardContract.getApprovalStandardRequest();
			if (Objects.isNull(approvalStandardContract)
					|| Objects.isNull(approvalStandardContract.getApprovalStandardRequest())) {
				throw new CustomException("approval of standard design must not be null",
						"approval of standard design must not be null");
			}

			if (StringUtils.isEmpty(approvalStandardEntity.getApplicationNumber())) {
				throw new CustomException("ApplicationNumber must not be null", "ApplicationNumber must not be null");
			}

			List<ApprovalStandardEntity> approvalStandardEntitySearch = searchApprovalStandard(requestInfo,
					approvalStandardEntity.getLicenseNo(), approvalStandardEntity.getApplicationNumber());

			if (CollectionUtils.isEmpty(approvalStandardEntitySearch) || approvalStandardEntitySearch.size() > 1) {
				throw new CustomException(
						"Found none or multiple approval of standard applications with applicationNumber.",
						"Found none or multiple approval of standard applications with applicationNumber.");
			}

			approvalStandardEntity.setBusinessService(approvalStandardEntity.getBusinessService());
			approvalStandardEntity.setWorkflowCode(approvalStandardEntity.getBusinessService());

			// EMPLOYEE RUN THE APPLICATION NORMALLY
			if (!approvalStandardEntity.getStatus().equalsIgnoreCase(SENDBACK_STATUS) && !usercheck(requestInfo)) {

				String currentStatus = approvalStandardEntitySearch.get(0).getStatus();

				BusinessService workflow = workflowService.getBusinessService(approvalStandardEntity.getTenantId(),
						approvalStandardContract.getRequestInfo(), approvalStandardEntity.getBusinessService());

				validateUpdateRoleAndActionFromWorkflow(workflow, currentStatus, approvalStandardContract,
						approvalStandardEntity);

				approvalStandardEntity.setAuditDetails(auditDetails);

				TradeLicenseRequest prepareProcessInstanceRequest = prepareProcessInstanceRequest(
						approvalStandardEntity, requestInfo, approvalStandardEntity.getBusinessService());

				wfIntegrator.callWorkFlow(prepareProcessInstanceRequest);

				approvalStandardEntity.setStatus(prepareProcessInstanceRequest.getLicenses().get(0).getStatus());

			}

			// CITIZEN MODIFY THE APPLICATION WHEN EMPLOYEE SENDBACK TO CITIZEN
			else if ((approvalStandardEntity.getStatus().equalsIgnoreCase(SENDBACK_STATUS)) && usercheck(requestInfo)) {

				String currentStatus = approvalStandardEntitySearch.get(0).getStatus();

				approvalStandardEntity.setAssignee(Arrays.asList(
						servicePlanService.assignee("CAO", approvalStandardEntity.getTenantId(), true, requestInfo)));

				approvalStandardEntity.setAction(CITIZEN_UPDATE_ACTION);

				BusinessService workflow = workflowService.getBusinessService(approvalStandardEntity.getTenantId(),
						approvalStandardContract.getRequestInfo(), approvalStandardEntity.getBusinessService());

				validateUpdateRoleAndActionFromWorkflow(workflow, currentStatus, approvalStandardContract,
						approvalStandardEntity);

				approvalStandardEntity.setAuditDetails(auditDetails);

				TradeLicenseRequest prepareProcessInstanceRequest = prepareProcessInstanceRequest(
						approvalStandardEntity, requestInfo, approvalStandardEntity.getBusinessService());

				wfIntegrator.callWorkFlow(prepareProcessInstanceRequest);

				approvalStandardEntity.setStatus(prepareProcessInstanceRequest.getLicenses().get(0).getStatus());

			}
		}

		approvalStandardContract.setApprovalStandardRequest(approvalStandardEntityList);

		producer.push(approvalUpdateTopic, approvalStandardContract);

		return approvalStandardEntityList;

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
			ApprovalStandardRequest approvalStandardContract, ApprovalStandardEntity approvalStandardEntity) {
		// validate Action-
		Optional<State> currentWorkflowStateOptional = workflow.getStates().stream()
				.filter(state -> state.getState().equals(currentStatus)).findFirst();
		if (!currentWorkflowStateOptional.isPresent()) {
			throw new CustomException("workflow State not found:" + currentStatus,
					"workflow State not found:" + currentStatus);
		}
		State currentWorkflowState = currentWorkflowStateOptional.get();
		List<Action> permissibleActions = currentWorkflowState.getActions();
		String currentActionFromRequest = approvalStandardEntity.getAction();
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
		List<Role> rolesFromUserInfo = approvalStandardContract.getRequestInfo().getUserInfo().getRoles();
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
		approvalStandardEntity.setStatus(nextStateName);
	}

	private TradeLicenseRequest prepareProcessInstanceRequest(ApprovalStandardEntity approvalStandardEntity,
			RequestInfo requestInfo, String bussinessServicename) {

		TradeLicenseRequest tradeLicenseASRequest = new TradeLicenseRequest();
		TradeLicense tradeLicenseAS = new TradeLicense();
		List<TradeLicense> tradeLicenseASlist = new ArrayList<>();
		tradeLicenseAS.setBusinessService(approvalStandardEntity.getBusinessService());
		tradeLicenseAS.setAction(approvalStandardEntity.getAction());
		tradeLicenseAS.setAssignee(approvalStandardEntity.getAssignee());
		tradeLicenseAS.setApplicationNumber(approvalStandardEntity.getApplicationNumber());
		tradeLicenseAS.setWorkflowCode(approvalStandardEntity.getWorkflowCode());
		TradeLicenseDetail tradeLicenseDetail = new TradeLicenseDetail();
		tradeLicenseDetail.setTradeType(bussinessServicename);
		tradeLicenseAS.setTradeLicenseDetail(tradeLicenseDetail);
		tradeLicenseAS.setComment(approvalStandardEntity.getComment());
		tradeLicenseAS.setWfDocuments(approvalStandardEntity.getWfDocuments());
		tradeLicenseAS.setTenantId(approvalStandardEntity.getTenantId());
		tradeLicenseAS.setBusinessService(bussinessServicename);

		tradeLicenseASRequest.setRequestInfo(requestInfo);
		tradeLicenseASlist.add(tradeLicenseAS);
		tradeLicenseASRequest.setLicenses(tradeLicenseASlist);

		return tradeLicenseASRequest;
	}

	public List<ApprovalStandardEntity> makePayment(ApprovalStandardContract approvalStandardContract)
			throws JsonProcessingException {

		RequestInfo requestInfo = approvalStandardContract.getRequestInfo();
		ApprovalStandardEntity approvalStandardRequest = approvalStandardContract.getApprovalStandardRequest();

		approvalStandardRequest.setId(UUID.randomUUID().toString());
//		approvalStandardRequest.setAssignee(Arrays.asList(
//				servicePlanService.assignee("CTP_HR", approvalStandardRequest.getTenantId(), true, requestInfo)));
////			approvalStandardRequest.setAssignee(Arrays.asList("f9b7acaf-c1fb-4df2-ac10-83b55238a724"));
		approvalStandardRequest.setAssignee(Arrays
				.asList(tradeUtil.getFirstAssigneeByRole(ctpUser, approvalStandardRequest.getTenantId(), true, requestInfo)));
		approvalStandardRequest.setBusinessService(config.getApprovalOfStandardBusinessService());
		approvalStandardRequest.setWorkflowCode(config.getApprovalOfStandardBusinessService());
		TradeLicenseRequest prepareProcessInstanceRequest = prepareProcessInstanceRequest(approvalStandardRequest,
				requestInfo, config.getApprovalOfStandardBusinessService());

		wfIntegrator.callWorkFlow(prepareProcessInstanceRequest);

		approvalStandardRequest.setStatus(prepareProcessInstanceRequest.getLicenses().get(0).getStatus());
		TradeLicenseSearchCriteria tradeLicenseSearchCriteria = new TradeLicenseSearchCriteria();
		List<String> licenseNumberList = new ArrayList<>();
		licenseNumberList.add(approvalStandardRequest.getLicenseNo());
		tradeLicenseSearchCriteria.setLicenseNumbers(licenseNumberList);

		Map<String, Object> tcpNumbers = generateTcpNumbers.tcpNumbers(tradeLicenseSearchCriteria, requestInfo);
		log.info("tcpnumbers:\t" + tcpNumbers);
		String data = null;

		data = mapper.writeValueAsString(tcpNumbers);
//			
		JSONObject json = new JSONObject(tcpNumbers);

		json.toString();
		String application = json.getAsString("TCPApplicationNumber");
		String caseNumber = json.getAsString("TCPCaseNumber");
		String dairyNumber = json.getAsString("TCPDairyNumber");

		List<ApprovalStandardEntity> approvalStandardEntityList = new ArrayList<>();

		approvalStandardRequest.setTcpApplicationNumber(application);
		approvalStandardRequest.setTcpCaseNumber(caseNumber);
		approvalStandardRequest.setTcpDairyNumber(dairyNumber);
		approvalStandardEntityList.add(approvalStandardRequest);
		ApprovalStandardRequest approvalStandardRequestList = new ApprovalStandardRequest();
		approvalStandardRequestList.setApprovalStandardRequest(approvalStandardEntityList);
		approvalStandardRequestList.setRequestInfo(requestInfo);
		List<ApprovalStandardEntity> Update = Update(approvalStandardRequestList);

		return Update;

	}

}