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
import org.egov.tl.repository.rowmapper.RevisedLayoutPlanRowMapper;
import org.egov.tl.util.TradeUtil;
import org.egov.tl.web.models.AuditDetails;
import org.egov.tl.web.models.RevisedPlan;
import org.egov.tl.web.models.RevisedPlanRequest;
import org.egov.tl.web.models.ServicePlanContract;
import org.egov.tl.web.models.ServicePlanRequest;
import org.egov.tl.web.models.TradeLicense;
import org.egov.tl.web.models.TradeLicenseDetail;
import org.egov.tl.web.models.TradeLicenseRequest;
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

import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class RevisedPlanServices {
	@Value("${persister.create.revised.layout.plan.topic}")
	private String revisedTopic;
	
	@Value("${persister.update.revised.layout.plan.topic}")
	private String revisdUpdateTopic;

	private static final String businessService_RLP = "REVISED_LAYOUT_PLAN";

	private static final String SENDBACK_STATUS = "FORWARD_TO_APPLICANT";

	private static final String CITIZEN_UPDATE_ACTION = "FORWARD";
	@Autowired
	private Producer producer;
	@Autowired
	private RevisedLayoutPlanRowMapper revisedLayoutPlanRowMapper;
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

	public List<RevisedPlan> create(RevisedPlanRequest revisedPlanRequest) {

		String uuid = revisedPlanRequest.getRequestInfo().getUserInfo().getUuid();

		AuditDetails auditDetails = tradeUtil.getAuditDetails(uuid, true);

		RequestInfo requestInfo = revisedPlanRequest.getRequestInfo();
		List<RevisedPlan> renewalList = revisedPlanRequest.getRevisedPlan();
		for (RevisedPlan revisedPlan : renewalList) {

			List<String> applicationNumbers = null;
			int count = 1;
			List<RevisedPlan> searchApprovalPlan = search(requestInfo, revisedPlan.getLicenseNo(),
					revisedPlan.getApplicationNumber());
			if (!CollectionUtils.isEmpty(searchApprovalPlan) || searchApprovalPlan.size() > 1) {
				throw new CustomException("Already Found  or multiple Service plan applications with LoiNumber.",
						"Already Found or multiple Service plan applications with LoiNumber.");
			}

			revisedPlan.setId(UUID.randomUUID().toString());
			revisedPlan.setAssignee(
					Arrays.asList(servicePlanService.assignee("CTP_HR", revisedPlan.getTenantId(), true, requestInfo)));
////		approvalStandardRequest.setAssignee(Arrays.asList("f9b7acaf-c1fb-4df2-ac10-83b55238a724"));
			applicationNumbers = servicePlanService.getIdList(requestInfo, revisedPlan.getTenantId(),
					config.getRevisedLayoutPlanName(), config.getRevisedLayoutPlanFormat(), count);

			revisedPlan.setBusinessService(businessService_RLP);
			revisedPlan.setWorkflowCode(businessService_RLP);

			revisedPlan.setAuditDetails(auditDetails);
			revisedPlan.setApplicationNumber(applicationNumbers.get(0));

			TradeLicenseRequest prepareProcessInstanceRequest = prepareProcessInstanceRequest(revisedPlan, requestInfo,
					businessService_RLP);

			wfIntegrator.callWorkFlow(prepareProcessInstanceRequest);

			revisedPlan.setStatus(prepareProcessInstanceRequest.getLicenses().get(0).getStatus());

		}

		revisedPlanRequest.setRevisedPlan(renewalList);

		log.info(revisedTopic);

		producer.push(revisedTopic, revisedPlanRequest);

		return renewalList;

	}

	public List<RevisedPlan> search(RequestInfo info, String applicattionNumber, String licenceNumber) {
		List<Object> preparedStatement = new ArrayList<>();

		Map<String, String> paramMap = new HashedMap();
		Map<String, List<String>> paramMapList = new HashedMap();
		StringBuilder builder;

		String query = "SELECT id, licence_number, application_number, tenantid, action, status, workflowcode, businessservice, additionaldetails, createdby, lastmodifyby, created_time, lastmodifiedtime, existingarea, areaplanning, anyotherfeature, amount, reasonrevision, earlyapprovedlayoutplan\r\n"
				+ "	FROM public.eg_revised_layout_plan " + " Where ";
		builder = new StringBuilder(query);

		List<RevisedPlan> Result = null;
		if (licenceNumber != null) {
			builder.append(" licence_number= :LN");
			paramMap.put("LN", licenceNumber);
			preparedStatement.add(licenceNumber);
			Result = namedParameterJdbcTemplate.query(builder.toString(), paramMap, revisedLayoutPlanRowMapper);
		} else if (applicattionNumber != null) {
			List<String> applicationNumberList = Arrays.asList(applicattionNumber.split(","));
			log.info("applicationNumberList" + applicationNumberList);
			if (applicationNumberList != null) {
				builder.append(" application_number in ( :AN )");
				paramMapList.put("AN", applicationNumberList);
				preparedStatement.add(applicationNumberList);
				Result = namedParameterJdbcTemplate.query(builder.toString(), paramMapList, revisedLayoutPlanRowMapper);
			}

		}
		return Result;

	}

	public List<RevisedPlan> update(RevisedPlanRequest revisedPlanRequest) {

		String uuid = revisedPlanRequest.getRequestInfo().getUserInfo().getUuid();

		AuditDetails auditDetails = tradeUtil.getAuditDetails(uuid, false);

		RequestInfo requestInfo = revisedPlanRequest.getRequestInfo();

		List<RevisedPlan> revisedPlanList = revisedPlanRequest.getRevisedPlan();

		for (RevisedPlan revisedPlan : revisedPlanList) {

			if (Objects.isNull(revisedPlanRequest) || Objects.isNull(revisedPlanRequest.getRevisedPlan())) {
				throw new CustomException("ServicePlanContract must not be null",
						"ServicePlanContract must not be null");
			}

			if (StringUtils.isEmpty(revisedPlan.getApplicationNumber())) {
				throw new CustomException("ApplicationNumber must not be null", "ApplicationNumber must not be null");
			}

			List<RevisedPlan> revisedLayoutPlanSearch = search(requestInfo, revisedPlan.getApplicationNumber(),
					revisedPlan.getLicenseNo());
			if (CollectionUtils.isEmpty(revisedLayoutPlanSearch) || revisedLayoutPlanSearch.size() > 1) {
				throw new CustomException("Found none or multiple revised plan applications with applicationNumber.",
						"Found none or multiple revised plan applications with applicationNumber.");
			}

			revisedPlan.setBusinessService(revisedPlan.getBusinessService());
			revisedPlan.setWorkflowCode(revisedPlan.getBusinessService());

			// EMPLOYEE RUN THE APPLICATION NORMALLY
			if (!revisedPlan.getStatus().equalsIgnoreCase(SENDBACK_STATUS) && !usercheck(requestInfo)) {

				String currentStatus = revisedLayoutPlanSearch.get(0).getStatus();

				BusinessService workflow = workflowService.getBusinessService(revisedPlan.getTenantId(),
						revisedPlanRequest.getRequestInfo(), revisedPlan.getBusinessService());

				validateUpdateRoleAndActionFromWorkflow(workflow, currentStatus, revisedPlanRequest, revisedPlan);

				revisedPlan.setAuditDetails(auditDetails);

				TradeLicenseRequest prepareProcessInstanceRequest = prepareProcessInstanceRequest(revisedPlan,
						requestInfo, revisedPlan.getBusinessService());

				wfIntegrator.callWorkFlow(prepareProcessInstanceRequest);

				revisedPlan.setStatus(prepareProcessInstanceRequest.getLicenses().get(0).getStatus());

			}

			// CITIZEN MODIFY THE APPLICATION WHEN EMPLOYEE SENDBACK TO CITIZEN
			else if ((revisedPlan.getStatus().equalsIgnoreCase(SENDBACK_STATUS)) && usercheck(requestInfo)) {

				String currentStatus = revisedLayoutPlanSearch.get(0).getStatus();

				revisedPlan.setAssignee(Arrays
						.asList(servicePlanService.assignee("DTP_HQ", revisedPlan.getTenantId(), true, requestInfo)));

				revisedPlan.setAction(CITIZEN_UPDATE_ACTION);

				BusinessService workflow = workflowService.getBusinessService(revisedPlan.getTenantId(),
						revisedPlanRequest.getRequestInfo(), revisedPlan.getBusinessService());

				validateUpdateRoleAndActionFromWorkflow(workflow, currentStatus, revisedPlanRequest, revisedPlan);

				revisedPlan.setAuditDetails(auditDetails);

				TradeLicenseRequest prepareProcessInstanceRequest = prepareProcessInstanceRequest(revisedPlan,
						requestInfo, revisedPlan.getBusinessService());

				wfIntegrator.callWorkFlow(prepareProcessInstanceRequest);

				revisedPlan.setStatus(prepareProcessInstanceRequest.getLicenses().get(0).getStatus());
				revisedPlan.setLicenseNo(revisedPlan.getLicenseNo());

			}

		}
		

		revisedPlanRequest.setRevisedPlan(revisedPlanList);

		producer.push(revisdUpdateTopic, revisedPlanRequest);

		return revisedPlanList;

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

	private TradeLicenseRequest prepareProcessInstanceRequest(RevisedPlan revisedPlan, RequestInfo requestInfo,
			String bussinessServicename) {

		TradeLicenseRequest tradeLicenseASRequest = new TradeLicenseRequest();
		TradeLicense tradeLicenseAS = new TradeLicense();
		List<TradeLicense> tradeLicenseASlist = new ArrayList<>();
		tradeLicenseAS.setBusinessService(revisedPlan.getBusinessService());
		tradeLicenseAS.setAction(revisedPlan.getAction());
		tradeLicenseAS.setAssignee(revisedPlan.getAssignee());
		tradeLicenseAS.setApplicationNumber(revisedPlan.getApplicationNumber());
		tradeLicenseAS.setWorkflowCode(revisedPlan.getWorkflowCode());
		TradeLicenseDetail tradeLicenseDetail = new TradeLicenseDetail();
		tradeLicenseDetail.setTradeType(bussinessServicename);
		tradeLicenseAS.setTradeLicenseDetail(tradeLicenseDetail);
		tradeLicenseAS.setComment(revisedPlan.getComment());
		tradeLicenseAS.setWfDocuments(revisedPlan.getWfDocuments());
		tradeLicenseAS.setTenantId(revisedPlan.getTenantId());
		tradeLicenseAS.setBusinessService(bussinessServicename);

		tradeLicenseASRequest.setRequestInfo(requestInfo);
		tradeLicenseASlist.add(tradeLicenseAS);
		tradeLicenseASRequest.setLicenses(tradeLicenseASlist);

		return tradeLicenseASRequest;
	}

	private void validateUpdateRoleAndActionFromWorkflow(BusinessService workflow, String currentStatus,
			RevisedPlanRequest revisedPlanRequest, RevisedPlan revisedPlan) {
		// validate Action-
		Optional<State> currentWorkflowStateOptional = workflow.getStates().stream()
				.filter(state -> state.getState().equals(currentStatus)).findFirst();
		if (!currentWorkflowStateOptional.isPresent()) {
			throw new CustomException("workflow State not found:" + currentStatus,
					"workflow State not found:" + currentStatus);
		}
		State currentWorkflowState = currentWorkflowStateOptional.get();
		List<Action> permissibleActions = currentWorkflowState.getActions();
		String currentActionFromRequest = revisedPlan.getAction();
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
		List<Role> rolesFromUserInfo = revisedPlanRequest.getRequestInfo().getUserInfo().getRoles();
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
		revisedPlan.setStatus(nextStateName);
	}
}
