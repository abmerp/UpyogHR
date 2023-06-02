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
import org.egov.tl.abm.newservices.contract.ZonePlanContract;
import org.egov.tl.config.TLConfiguration;
import org.egov.tl.producer.Producer;
import org.egov.tl.repository.rowmapper.ZonePlanRowMapper;
import org.egov.tl.util.TradeUtil;
import org.egov.tl.web.models.AuditDetails;
import org.egov.tl.web.models.TradeLicense;
import org.egov.tl.web.models.TradeLicenseDetail;
import org.egov.tl.web.models.TradeLicenseRequest;
import org.egov.tl.web.models.TradeLicenseSearchCriteria;
import org.egov.tl.web.models.Transfer;
import org.egov.tl.web.models.ZonePlan;
import org.egov.tl.web.models.ZonePlanRequest;
import org.egov.tl.web.models.workflow.Action;
import org.egov.tl.web.models.workflow.BusinessService;
import org.egov.tl.web.models.workflow.State;
import org.egov.tl.workflow.WorkflowIntegrator;
import org.egov.tl.workflow.WorkflowService;
import org.egov.tracer.model.CustomException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.extern.slf4j.Slf4j;
import net.minidev.json.JSONObject;

@Slf4j
@Service
public class ZonePlanServices {

	private static final String BUSINESS_ZONEPLAN = "ZONE_PLAN";
	@Value("${tcp.employee.ctp}")
	private String ctpUser;
	@Value("${persister.create.zone.plan.topic}")
	private String zoneplanTopic;

	@Value("${persister.update.zone.plan.topic}")
	private String zoneplanUpdateTopic;

	@Autowired
	private ZonePlanRowMapper zonePlanRowMapper;
	@Autowired
	private NamedParameterJdbcTemplate namedParameterJdbcTemplate;

	@Autowired
	private Producer producer;

	@Autowired
	private TradeUtil tradeUtil;

	@Autowired
	private TLConfiguration config;

	@Autowired
	ServicePlanService servicePlanService;

	@Autowired
	private WorkflowIntegrator wfIntegrator;

	@Autowired
	private WorkflowService workflowService;
	@Autowired
	GenerateTcpNumbers generateTcpNumbers;
	private static final String SENDBACK_STATUS = "SENDBACK_TO_APPLICANT";

	private static final String CITIZEN_UPDATE_ACTION = "FORWARD";
	@Autowired
	ObjectMapper mapper;

	@SuppressWarnings("null")
	public ZonePlan create(ZonePlanRequest zonePlanRequest) throws JsonProcessingException {

		String uuid = zonePlanRequest.getRequestInfo().getUserInfo().getUuid();

		AuditDetails auditDetails = tradeUtil.getAuditDetails(uuid, true);

		RequestInfo requestInfo = zonePlanRequest.getRequestInfo();
		ZonePlan zonePlan = zonePlanRequest.getZonePlan();

//		for (ZonePlan zonePlan : renewalList) {

		List<String> applicationNumbers = null;
		int count = 1;
		List<ZonePlan> searchZonePlan = search(requestInfo, zonePlan.getLicenseNo(), zonePlan.getApplicationNumber());
		if (!CollectionUtils.isEmpty(searchZonePlan) || searchZonePlan.size() > 1) {
			throw new CustomException("Already Found  or multiple surender of licence applications with LoiNumber.",
					"Already Found or multiple Service plan applications with LoiNumber.");
		}
		zonePlan.setTenantId("hr");
		zonePlan.setId(UUID.randomUUID().toString());
//		zonePlan.setAssignee(
//				Arrays.asList(servicePlanService.assignee("CTP_HR", zonePlan.getTenantId(), true, requestInfo)));
////		approvalStandardRequest.setAssignee(Arrays.asList("f9b7acaf-c1fb-4df2-ac10-83b55238a724"));
		zonePlan.setAssignee(Arrays
				.asList(tradeUtil.getFirstAssigneeByRole(ctpUser, zonePlan.getTenantId(), true, requestInfo)));
		applicationNumbers = servicePlanService.getIdList(requestInfo, zonePlan.getTenantId(), config.getZonePlanName(),
				config.getZonePlanFormat(), count);
		zonePlan.setAction("INITIATE");
		zonePlan.setAuditDetails(auditDetails);
		zonePlan.setBusinessService(BUSINESS_ZONEPLAN);
		zonePlan.setWorkflowCode(BUSINESS_ZONEPLAN);

		zonePlan.setApplicationNumber(applicationNumbers.get(0));
		TradeLicenseRequest prepareProcessInstanceRequest = prepareProcessInstanceRequest(zonePlanRequest.getZonePlan(),
				requestInfo, zonePlan.getBusinessService());

		wfIntegrator.callWorkFlow(prepareProcessInstanceRequest);
		zonePlan.setStatus(prepareProcessInstanceRequest.getLicenses().get(0).getStatus());
//		}
		ZonePlan zonePlanData = makePayment(zonePlan.getLicenseNo(), requestInfo);
		zonePlan.setTcpApplicationNumber(zonePlanData.getTcpApplicationNumber());
		zonePlan.setTcpCaseNumber(zonePlanData.getTcpCaseNumber());
		zonePlan.setTcpDairyNumber(zonePlanData.getTcpDairyNumber());
		zonePlanRequest.setZonePlan(zonePlan);

		log.info(zoneplanTopic);

		producer.push(zoneplanTopic, zonePlanRequest);

		return zonePlan;
	}

	public List<ZonePlan> update(ZonePlanContract zonePlanRequest) {

		String uuid = zonePlanRequest.getRequestInfo().getUserInfo().getUuid();

		AuditDetails auditDetails = tradeUtil.getAuditDetails(uuid, false);

		RequestInfo requestInfo = zonePlanRequest.getRequestInfo();

		List<ZonePlan> zonePlanList = zonePlanRequest.getZonePlan();

		 for (ZonePlan zonePlan : zonePlanList) {

		if (Objects.isNull(zonePlanRequest) || Objects.isNull(zonePlanRequest.getZonePlan())) {
			throw new CustomException("ZonePlan must not be null", "ZonePlan must not be null");
		}

		if (StringUtils.isEmpty(zonePlan.getApplicationNumber())) {
			throw new CustomException("ApplicationNumber must not be null", "ApplicationNumber must not be null");
		}

		List<ZonePlan> searchZonePlan = search(requestInfo, zonePlan.getLicenseNo(), zonePlan.getApplicationNumber());
		if (CollectionUtils.isEmpty(searchZonePlan) || searchZonePlan.size() > 1) {
			throw new CustomException("Found none or multiple ZonePlan applications with applicationNumber.",
					"Found none or multiple ZonePlan applications with applicationNumber.");
		}

		zonePlan.setBusinessService(zonePlan.getBusinessService());
		zonePlan.setWorkflowCode(zonePlan.getBusinessService());

		// EMPLOYEE RUN THE APPLICATION NORMALLY
		if (!zonePlan.getStatus().equalsIgnoreCase(SENDBACK_STATUS) && !usercheck(requestInfo)) {

			String currentStatus = searchZonePlan.get(0).getStatus();

			BusinessService workflow = workflowService.getBusinessService(zonePlan.getTenantId(),
					zonePlanRequest.getRequestInfo(), zonePlan.getBusinessService());

			validateUpdateRoleAndActionFromWorkflow(workflow, currentStatus, zonePlanRequest, zonePlan);

			zonePlan.setAuditDetails(auditDetails);

			TradeLicenseRequest prepareProcessInstanceRequest = prepareProcessInstanceRequest(zonePlan, requestInfo,
					zonePlan.getBusinessService());

			wfIntegrator.callWorkFlow(prepareProcessInstanceRequest);

			zonePlan.setStatus(prepareProcessInstanceRequest.getLicenses().get(0).getStatus());

		}

		// CITIZEN MODIFY THE APPLICATION WHEN EMPLOYEE SENDBACK TO CITIZEN
		else if ((zonePlan.getStatus().equalsIgnoreCase(SENDBACK_STATUS)) && usercheck(requestInfo)) {

			String currentStatus = searchZonePlan.get(0).getStatus();

			zonePlan.setAssignee(
					Arrays.asList(servicePlanService.assignee("CAO", zonePlan.getTenantId(), true, requestInfo)));

			zonePlan.setAction(CITIZEN_UPDATE_ACTION);

			BusinessService workflow = workflowService.getBusinessService(zonePlan.getTenantId(),
					zonePlanRequest.getRequestInfo(), zonePlan.getBusinessService());

			validateUpdateRoleAndActionFromWorkflow(workflow, currentStatus, zonePlanRequest, zonePlan);

			zonePlan.setAuditDetails(auditDetails);

			TradeLicenseRequest prepareProcessInstanceRequest = prepareProcessInstanceRequest(zonePlan, requestInfo,
					zonePlan.getBusinessService());

			wfIntegrator.callWorkFlow(prepareProcessInstanceRequest);

			zonePlan.setStatus(prepareProcessInstanceRequest.getLicenses().get(0).getStatus());

		}
		 }

		zonePlanRequest.setZonePlan(zonePlanList);

		producer.push(zoneplanUpdateTopic, zonePlanRequest);

		return zonePlanList;

	}

	private TradeLicenseRequest prepareProcessInstanceRequest(ZonePlan zonePlan, RequestInfo requestInfo,
			String bussinessServicename) {

		TradeLicenseRequest tradeLicenseASRequest = new TradeLicenseRequest();
		TradeLicense tradeLicenseAS = new TradeLicense();
		List<TradeLicense> tradeLicenseASlist = new ArrayList<>();
		tradeLicenseAS.setBusinessService(bussinessServicename);
		tradeLicenseAS.setAction(zonePlan.getAction());
		tradeLicenseAS.setAssignee(zonePlan.getAssignee());
		tradeLicenseAS.setApplicationNumber(zonePlan.getApplicationNumber());
		tradeLicenseAS.setWorkflowCode(bussinessServicename);
		TradeLicenseDetail tradeLicenseDetail = new TradeLicenseDetail();
		tradeLicenseDetail.setTradeType(bussinessServicename);
		tradeLicenseAS.setTradeLicenseDetail(tradeLicenseDetail);
		tradeLicenseAS.setComment("Workflow for ZonePlan");
		tradeLicenseAS.setWfDocuments(null);
		tradeLicenseAS.setTenantId(zonePlan.getTenantId());
		// tradeLicenseAS.setBusinessService("SURRENDER_OF_LICENSE");

		tradeLicenseASRequest.setRequestInfo(requestInfo);
		tradeLicenseASlist.add(tradeLicenseAS);
		tradeLicenseASRequest.setLicenses(tradeLicenseASlist);

		return tradeLicenseASRequest;
	}

	public List<ZonePlan> search(RequestInfo info, String licenseNo, String applicationNumber) {
		List<Object> preparedStatement = new ArrayList<>();

		Map<String, String> paramMap = new HashedMap();
		Map<String, List<String>> paramMapList = new HashedMap();
		StringBuilder builder;

		String query = "SELECT id, license_no, case_number, layout_plan, anyother_document, amount, application_number, additionaldetails, created_by, \"lastModified_by\", created_time, \"lastModified_time\", tenant_id, businessservice, comment, workflowcode, status, tcpapplicationnumber, tcpcasenumber, tcpdairynumber, newadditionaldetails\r\n"
				+ "	FROM public.eg_zone_plan " + " Where ";
		builder = new StringBuilder(query);

		List<ZonePlan> Result = null;

		if (licenseNo != null) {
			builder.append(" license_no= :LN");
			paramMap.put("LN", licenseNo);
			preparedStatement.add(licenseNo);
			Result = namedParameterJdbcTemplate.query(builder.toString(), paramMap, zonePlanRowMapper);
		} else if (applicationNumber != null) {
			List<String> applicationNumberList = Arrays.asList(applicationNumber.split(","));
			log.info("applicationNumberList" + applicationNumberList);
			if (applicationNumberList != null) {
				builder.append(" application_number in ( :AN )");
				paramMapList.put("AN", applicationNumberList);
				preparedStatement.add(applicationNumberList);
				Result = namedParameterJdbcTemplate.query(builder.toString(), paramMapList, zonePlanRowMapper);
			}

		} else if ((info.getUserInfo().getUuid() != null)) {
			builder.append(" createdBy= :CB");
			paramMap.put("CB", info.getUserInfo().getUuid());
			preparedStatement.add(info.getUserInfo().getUuid());
			Result = namedParameterJdbcTemplate.query(builder.toString(), paramMap, zonePlanRowMapper);

		}

		return Result;

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
			ZonePlanContract zonePlanRequest, ZonePlan zonePlan) {
		// validate Action-
		Optional<State> currentWorkflowStateOptional = workflow.getStates().stream()
				.filter(state -> state.getState().equals(currentStatus)).findFirst();
		if (!currentWorkflowStateOptional.isPresent()) {
			throw new CustomException("workflow State not found:" + currentStatus,
					"workflow State not found:" + currentStatus);
		}
		State currentWorkflowState = currentWorkflowStateOptional.get();
		List<Action> permissibleActions = currentWorkflowState.getActions();
		String currentActionFromRequest = zonePlan.getAction();
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
		List<Role> rolesFromUserInfo = zonePlanRequest.getRequestInfo().getUserInfo().getRoles();
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
		zonePlan.setStatus(nextStateName);
	}

	public ZonePlan makePayment(String licenseNumber, RequestInfo requestInfo) throws JsonProcessingException {
		TradeLicenseSearchCriteria tradeLicenseSearchCriteria = new TradeLicenseSearchCriteria();
		List<String> licenseNumberList = new ArrayList<>();
		licenseNumberList.add(licenseNumber);
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

		ZonePlan transfer = new ZonePlan();

		transfer.setTcpApplicationNumber(application);
		transfer.setTcpCaseNumber(caseNumber);
		transfer.setTcpDairyNumber(dairyNumber);

		return transfer;

	}

}
