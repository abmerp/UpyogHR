package org.egov.tl.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import org.apache.commons.collections.map.HashedMap;
import org.egov.common.contract.request.RequestInfo;
import org.egov.common.contract.request.Role;
import org.egov.tl.config.TLConfiguration;
import org.egov.tl.producer.Producer;
import org.egov.tl.repository.IdGenRepository;
import org.egov.tl.repository.ServiceRequestRepository;
import org.egov.tl.repository.rowmapper.EPRowMapper;
import org.egov.tl.repository.rowmapper.SPRowMapper;
import org.egov.tl.service.repo.ElectricPlanRepo;
import org.egov.tl.util.TradeUtil;
import org.egov.tl.web.models.AuditDetails;
import org.egov.tl.web.models.ElectricPlan;
import org.egov.tl.web.models.ElectricPlanContract;
import org.egov.tl.web.models.ElectricPlanRequest;
import org.egov.tl.web.models.EmployeeResponse;
import org.egov.tl.web.models.GuranteeCalculatorResponse;
import org.egov.tl.web.models.RequestInfoWrapper;
import org.egov.tl.web.models.ServicePlanRequest;
import org.egov.tl.web.models.TradeLicense;
import org.egov.tl.web.models.TradeLicenseDetail;
import org.egov.tl.web.models.TradeLicenseRequest;
import org.egov.tl.web.models.TradeLicenseSearchCriteria;
import org.egov.tl.web.models.Idgen.IdResponse;
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
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;

import lombok.extern.slf4j.Slf4j;
import net.minidev.json.JSONObject;

@Slf4j
@Service
public class ElectricPlanService {
	@Value("${tcp.employee.ctp}")
	private String ctpUser;
	// private static final String businessService_TL = "ELECTRICAL_PLAN";

	private static final String SENDBACK_STATUS = "EP_SENDBACK_TO_APPLICANT";

	private static final String CITIZEN_UPDATE_ACTION = "FORWARD";

	@Autowired
	ElectricPlanRepo electricPlanRepo;
	@Autowired
	GenerateTcpNumbers generateTcpNumbers;
	@Autowired
	private TradeUtil tradeUtil;

	@Autowired
	private IdGenRepository idGenRepository;

	@Autowired
	private TLConfiguration config;

	@Autowired
	private WorkflowIntegrator wfIntegrator;

	@Autowired
	private Producer producer;

	@Autowired
	private NamedParameterJdbcTemplate namedParameterJdbcTemplate;

	@Autowired
	private EPRowMapper epRowMapper;

	@Autowired
	private ServiceRequestRepository serviceRequestRepository;

	@Autowired
	ObjectMapper mapper;

	@Autowired
	private WorkflowService workflowService;

	public List<ElectricPlanRequest> create(ElectricPlanContract electricPlanContract) throws JsonProcessingException {

		String uuid = electricPlanContract.getRequestInfo().getUserInfo().getUuid();

		AuditDetails auditDetails = tradeUtil.getAuditDetails(uuid, true);

		RequestInfo requestInfo = electricPlanContract.getRequestInfo();

		List<ElectricPlanRequest> electricPlanRequestlist = electricPlanContract.getElectricPlanRequest();

		for (ElectricPlanRequest electricPlanRequest : electricPlanRequestlist) {
			List<String> applicationNumbers = null;
			int count = 1;
			List<ElectricPlanRequest> searchElectricPlan = searchElectricPlan(electricPlanRequest.getLoiNumber(),
					electricPlanRequest.getApplicationNumber(), requestInfo);
			if (!CollectionUtils.isEmpty(searchElectricPlan) || searchElectricPlan.size() > 1) {
				throw new CustomException("Already Found  or multiple Electric plan applications with LoiNumber.",
						"Already Found or multiple Electric plan applications with LoiNumber.");
			}

			electricPlanRequest.setId(UUID.randomUUID().toString());

//			electricPlanRequest.setAssignee(
//					Arrays.asList(assignee("CTP_HR", electricPlanRequest.getTenantID(), true, requestInfo)));
			electricPlanRequest.setAssignee(Arrays
					.asList(tradeUtil.getFirstAssigneeByRole(ctpUser, electricPlanRequest.getTenantID(), true, requestInfo)));
			applicationNumbers = getIdList(electricPlanContract.getRequestInfo(), electricPlanRequest.getTenantID(),
					config.getEPapplicationNumberIdgenNameTL(), config.getEPapplicationNumberIdgenFormatTL(), count);

			electricPlanRequest.setAuditDetails(auditDetails);
			electricPlanRequest.setApplicationNumber(applicationNumbers.get(0));

			TradeLicenseRequest prepareProcessInstanceRequest = prepareProcessInstanceRequest(electricPlanRequest,
					requestInfo);

			wfIntegrator.callWorkFlow(prepareProcessInstanceRequest);

			electricPlanRequest.setStatus(prepareProcessInstanceRequest.getLicenses().get(0).getStatus());
			ElectricPlanRequest electricPlanRequests = makePayment(electricPlanRequest.getLoiNumber(), requestInfo);
			electricPlanRequest.setTcpApplicationNumber(electricPlanRequests.getTcpApplicationNumber());
			electricPlanRequest.setTcpCaseNumber(electricPlanRequests.getTcpCaseNumber());
			electricPlanRequest.setTcpDairyNumber(electricPlanRequests.getTcpDairyNumber());

		}

		electricPlanContract.setElectricPlanRequest(electricPlanRequestlist);

		producer.push(config.getEPsaveTopic(), electricPlanContract);
		List<ElectricPlanRequest> update = Update(electricPlanContract);

		return update;

	}

	private String assignee(String role, String tenantID, boolean b, RequestInfo requestInfo) {

		StringBuilder uri = new StringBuilder();
		uri.append(config.getHrmsHost());
		uri.append(config.getHrmsContextPath());
		uri.append("?tenantId=" + tenantID);
		uri.append("&roles=" + role);
		uri.append("&isActive=" + b);

		RequestInfoWrapper requestInfoWrapper = RequestInfoWrapper.builder().requestInfo(requestInfo).build();

		EmployeeResponse employeeResponse = null;

		String data = null;

		Object fetchResult = serviceRequestRepository.fetchResult(uri, requestInfoWrapper);

		try {
			data = mapper.writeValueAsString(fetchResult);
		} catch (JsonProcessingException e) {

			e.printStackTrace();
		}
		ObjectReader reader = mapper.readerFor(new TypeReference<EmployeeResponse>() {
		});

		try {
			employeeResponse = reader.readValue(data);
		} catch (JsonMappingException e) {

			e.printStackTrace();
		} catch (JsonProcessingException e) {

			e.printStackTrace();
		}
		String uuid = employeeResponse.getEmployees().get(0).getUuid();

		return uuid;
	}

	public List<ElectricPlanRequest> searchElectricPlan(String loiNumber, String applicationNumber,
			RequestInfo requestInfo) {

		List<Object> preparedStatement = new ArrayList<>();

		Map<String, String> paramMap = new HashedMap();
		Map<String, List<String>> paramMapList = new HashedMap();
		StringBuilder builder;

		String query = "SELECT id, auto_cad, environmental_clearance, verified_plan, loi_number, self_centred_drawing, shap_file_template, tenantid, created_by, created_time, last_modified_by, last_modified_time, application_number, business_service, action, status, comment, pdf_format, elecric_distribution, electrical_capacity, electrical_infra, load_sancation, switching_station, additionaldetails, devname, developmentplan, purpose, totalarea, tcpapplicationnumber, tcpcasenumber, tcpdairynumber\r\n"
				+ "	FROM public.eg_electric_plan" + " WHERE business_service = 'ELECTRICAL_PLAN' ";

		builder = new StringBuilder(query);

		List<ElectricPlanRequest> Result = null;
		if (loiNumber != null) {
			builder.append("and loi_number= :LN");
			paramMap.put("LN", loiNumber);
			preparedStatement.add(loiNumber);
			Result = namedParameterJdbcTemplate.query(builder.toString(), paramMap, epRowMapper);

		} else if (applicationNumber != null) {
			List<String> applicationNumberList = Arrays.asList(applicationNumber.split(","));
			if (applicationNumberList != null) {
				builder.append("and application_number in ( :AN )");
				paramMapList.put("AN", applicationNumberList);
				preparedStatement.add(applicationNumberList);
				Result = namedParameterJdbcTemplate.query(builder.toString(), paramMapList, epRowMapper);
			}
		} else if ((requestInfo.getUserInfo().getUuid() != null)) {
			builder.append("and created_by= :CB");
			paramMap.put("CB", requestInfo.getUserInfo().getUuid());
			preparedStatement.add(requestInfo.getUserInfo().getUuid());
			Result = namedParameterJdbcTemplate.query(builder.toString(), paramMap, epRowMapper);

		}

		return Result;
	}

	private List<String> getIdList(RequestInfo requestInfo, String tenantId, String idKey, String idformat, int count) {
		List<IdResponse> idResponses = idGenRepository.getId(requestInfo, tenantId, idKey, idformat, count)
				.getIdResponses();

		if (CollectionUtils.isEmpty(idResponses))
			throw new CustomException("IDGEN ERROR", "No ids returned from idgen Service");

		return idResponses.stream().map(IdResponse::getId).collect(Collectors.toList());
	}

	private TradeLicenseRequest prepareProcessInstanceRequest(ElectricPlanRequest electricPlanRequest,
			RequestInfo requestInfo) {

//		ElectricPlanRequest electricPlanRequest = electricPlanContract.getElectricPlanRequest();

		TradeLicenseRequest tradeLicenseRequest = new TradeLicenseRequest();
		TradeLicense tradeLicenseEP = new TradeLicense();
		List<TradeLicense> tradeLicenseEPlist = new ArrayList<>();
		tradeLicenseEP.setBusinessService(electricPlanRequest.getBusinessService());
		tradeLicenseEP.setAction(electricPlanRequest.getAction());
		tradeLicenseEP.setAssignee(electricPlanRequest.getAssignee());
		tradeLicenseEP.setApplicationNumber(electricPlanRequest.getApplicationNumber());
		tradeLicenseEP.setWorkflowCode(electricPlanRequest.getWorkflowCode());
		TradeLicenseDetail tradeLicenseDetail = new TradeLicenseDetail();
		tradeLicenseDetail.setTradeType(config.getElectricPlanBusinessService());
		tradeLicenseEP.setTradeLicenseDetail(tradeLicenseDetail);
		tradeLicenseEP.setComment(electricPlanRequest.getComment());
		tradeLicenseEP.setWfDocuments(electricPlanRequest.getWfDocuments());
		tradeLicenseEP.setTenantId(electricPlanRequest.getTenantID());
		tradeLicenseEP.setBusinessService(config.getElectricPlanBusinessService());
		tradeLicenseRequest.setRequestInfo(requestInfo);
		tradeLicenseEPlist.add(tradeLicenseEP);
		tradeLicenseRequest.setLicenses(tradeLicenseEPlist);

		return tradeLicenseRequest;
	}

	public List<ElectricPlanRequest> Update(ElectricPlanContract electricPlanContract) {

		String uuid = electricPlanContract.getRequestInfo().getUserInfo().getUuid();

		AuditDetails auditDetails = tradeUtil.getAuditDetails(uuid, false);

		RequestInfo requestInfo = electricPlanContract.getRequestInfo();

		List<ElectricPlanRequest> electricPlanRequestlist = electricPlanContract.getElectricPlanRequest();

		for (ElectricPlanRequest electricPlanRequest : electricPlanRequestlist) {

			if (Objects.isNull(electricPlanContract) || Objects.isNull(electricPlanContract.getElectricPlanRequest())) {
				throw new CustomException("ElectricalPlanContract must not be null",
						"ServicePlanContract must not be null");
			}

			if (StringUtils.isEmpty(electricPlanRequest.getApplicationNumber())) {
				throw new CustomException("ApplicationNumber must not be null", "ApplicationNumber must not be null");
			}

			String loiNumber = null;
			String applicationNumber = electricPlanRequest.getApplicationNumber();
			List<ElectricPlanRequest> searchServicePlan = searchElectricPlan(loiNumber, applicationNumber, requestInfo);
			if (CollectionUtils.isEmpty(searchServicePlan) || searchServicePlan.size() > 1) {
				throw new CustomException("Found none or multiple Electric plan applications with applicationNumber.",
						"Found none or multiple Electric plan applications with applicationNumber.");
			}

			// EMPLOYEE RUN THE APPLICATION NORMALLY
			if (!electricPlanRequest.getStatus().equalsIgnoreCase(SENDBACK_STATUS) && !usercheck(requestInfo)) {
				String currentStatus = searchServicePlan.get(0).getStatus();
				BusinessService workflow = workflowService.getBusinessService(electricPlanRequest.getTenantID(),
						electricPlanContract.getRequestInfo(), config.getElectricPlanBusinessService());
				validateUpdateRoleAndActionFromWorkflow(workflow, currentStatus, electricPlanContract,
						electricPlanRequest);
				electricPlanRequest.setAuditDetails(auditDetails);
				TradeLicenseRequest prepareProcessInstanceRequest = prepareProcessInstanceRequest(electricPlanRequest,
						requestInfo);
				wfIntegrator.callWorkFlow(prepareProcessInstanceRequest);
				electricPlanRequest.setStatus(prepareProcessInstanceRequest.getLicenses().get(0).getStatus());
			}

			// CITIZEN MODIFY THE APPLICATION WHEN EMPLOYEE SENDBACK TO CITIZEN
			else if ((electricPlanRequest.getStatus().equalsIgnoreCase(SENDBACK_STATUS)) && usercheck(requestInfo)) {
				String currentStatus = searchServicePlan.get(0).getStatus();
				electricPlanRequest.setAssignee(
						Arrays.asList(assignee("STP_HQ", electricPlanRequest.getTenantID(), true, requestInfo)));
				electricPlanRequest.setAction(CITIZEN_UPDATE_ACTION);
				BusinessService workflow = workflowService.getBusinessService(electricPlanRequest.getTenantID(),
						electricPlanContract.getRequestInfo(), config.getElectricPlanBusinessService());
				validateUpdateRoleAndActionFromWorkflow(workflow, currentStatus, electricPlanContract,
						electricPlanRequest);
				electricPlanRequest.setAuditDetails(auditDetails);
				TradeLicenseRequest prepareProcessInstanceRequest = prepareProcessInstanceRequest(electricPlanRequest,
						requestInfo);
				wfIntegrator.callWorkFlow(prepareProcessInstanceRequest);
				electricPlanRequest.setStatus(prepareProcessInstanceRequest.getLicenses().get(0).getStatus());
			}

		}

		electricPlanContract.setElectricPlanRequest(electricPlanRequestlist);

		producer.push(config.getEPupdateTopic(), electricPlanContract);

		return electricPlanRequestlist;

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
			ElectricPlanContract electricPlanContract, ElectricPlanRequest electricPlanRequest) {
		// validate Action-
		Optional<State> currentWorkflowStateOptional = workflow.getStates().stream()
				.filter(state -> state.getState().equals(currentStatus)).findFirst();
		if (!currentWorkflowStateOptional.isPresent()) {
			throw new CustomException("workflow State not found:" + currentStatus,
					"workflow State not found:" + currentStatus);
		}
		State currentWorkflowState = currentWorkflowStateOptional.get();
		List<Action> permissibleActions = currentWorkflowState.getActions();
		String currentActionFromRequest = electricPlanRequest.getAction();
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
		List<Role> rolesFromUserInfo = electricPlanContract.getRequestInfo().getUserInfo().getRoles();
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
		electricPlanRequest.setStatus(nextStateName);
	}

	public ElectricPlanRequest makePayment(String loiNumber, RequestInfo requestInfo) throws JsonProcessingException {
		TradeLicenseSearchCriteria tradeLicenseSearchCriteria = new TradeLicenseSearchCriteria();
//		List<String> licenseNumberList = new ArrayList<>();
//		licenseNumberList.add(licenseNumber);

//		tradeLicenseSearchCriteria.setLicenseNumbers(licenseNumberList);
		tradeLicenseSearchCriteria.setLoiNumber(loiNumber);
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

		ElectricPlanRequest electricPlanRequest = new ElectricPlanRequest();

		electricPlanRequest.setTcpApplicationNumber(application);
		electricPlanRequest.setTcpCaseNumber(caseNumber);
		electricPlanRequest.setTcpDairyNumber(dairyNumber);

		return electricPlanRequest;

	}
}