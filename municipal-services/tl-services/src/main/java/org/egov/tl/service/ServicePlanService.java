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
import org.egov.tl.config.TLConfiguration;
import org.egov.tl.producer.Producer;
import org.egov.tl.repository.IdGenRepository;
import org.egov.tl.repository.ServiceRequestRepository;
import org.egov.tl.repository.rowmapper.SPRowMapper;
import org.egov.tl.service.repo.ServicePlanRepo;
import org.egov.tl.util.TradeUtil;
import org.egov.tl.web.models.AuditDetails;
import org.egov.tl.web.models.ElectricPlanRequest;
import org.egov.tl.web.models.EmployeeResponse;
import org.egov.tl.web.models.RequestInfoWrapper;
import org.egov.tl.web.models.ServicePlan;
import org.egov.tl.web.models.ServicePlanContract;
import org.egov.tl.web.models.ServicePlanRequest;
import org.egov.tl.web.models.TradeLicense;
import org.egov.tl.web.models.TradeLicenseDetail;
import org.egov.tl.web.models.TradeLicenseRequest;
import org.egov.tl.web.models.Idgen.IdResponse;
import org.egov.tl.web.models.workflow.Action;
import org.egov.tl.web.models.workflow.BusinessService;
import org.egov.tl.web.models.workflow.State;
import org.egov.tl.workflow.WorkflowIntegrator;
import org.egov.tl.workflow.WorkflowService;
import org.egov.tracer.model.CustomException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.flywaydb.core.internal.jdbc.RowMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;
import com.itextpdf.text.pdf.PdfStructTreeController.returnType;

@Service
public class ServicePlanService {

	private static final String businessService_TL = "SERVICE_PLAN";

	private static final String businessService_TL_DEMARCATION = "SERVICE_PLAN_DEMARCATION";

	private static final String SENDBACK_STATUS = "SP_SENDBACK_TO_APPLICANT";

	private static final String SENDBACK_STATUS_DEMARCATION = "SPD_SENDBACK_TO_APPLICANT";

	private static final String CITIZEN_UPDATE_ACTION = "FORWARD";

	@Autowired
	ServicePlanRepo servicePlanRepo;

	@Autowired
	private WorkflowIntegrator wfIntegrator;

	@Autowired
	private TLConfiguration config;

	@Autowired
	private IdGenRepository idGenRepository;

	@Autowired
	private Producer producer;

	@Autowired
	private TradeUtil tradeUtil;

	@Autowired
	private SPRowMapper spRowMapper;

	@Autowired
	private NamedParameterJdbcTemplate namedParameterJdbcTemplate;

	@Autowired
	private JdbcTemplate jdbcTemplate;

	@Autowired
	private WorkflowService workflowService;

	@Autowired
	private ServiceRequestRepository serviceRequestRepository;

	@Autowired
	ObjectMapper mapper;

	public List<ServicePlanRequest> create(ServicePlanContract servicePlanContract) {

		String uuid = servicePlanContract.getRequestInfo().getUserInfo().getUuid();

		AuditDetails auditDetails = tradeUtil.getAuditDetails(uuid, true);

		RequestInfo requestInfo = servicePlanContract.getRequestInfo();

		List<ServicePlanRequest> servicePlanRequestList = servicePlanContract.getServicePlanRequest();

		for (ServicePlanRequest servicePlanRequest : servicePlanRequestList) {
			List<String> applicationNumbers = null;
			int count = 1;
			List<ServicePlanRequest> searchServicePlan = searchServicePlan(servicePlanRequest.getLoiNumber(),
					servicePlanRequest.getApplicationNumber(), requestInfo);
			if (!CollectionUtils.isEmpty(searchServicePlan) || searchServicePlan.size() > 1) {
				throw new CustomException("Already Found  or multiple Service plan applications with LoiNumber.",
						"Already Found or multiple Service plan applications with LoiNumber.");
			}

			servicePlanRequest.setId(UUID.randomUUID().toString());

			servicePlanRequest.setAssignee(
					Arrays.asList(assignee("CTP_HR", servicePlanRequest.getTenantID(), true, requestInfo)));

			applicationNumbers = getIdList(servicePlanContract.getRequestInfo(), servicePlanRequest.getTenantID(),
					config.getSPapplicationNumberIdgenNameTL(), config.getSPapplicationNumberIdgenFormatTL(), count);

			String bussinessServicename = bussinessServicename(servicePlanRequest.getPurpose());
			servicePlanRequest.setBusinessService(bussinessServicename);
			servicePlanRequest.setWorkflowCode(bussinessServicename);

			servicePlanRequest.setAuditDetails(auditDetails);
			servicePlanRequest.setApplicationNumber(applicationNumbers.get(0));

			TradeLicenseRequest prepareProcessInstanceRequest = prepareProcessInstanceRequest(servicePlanRequest,
					requestInfo, bussinessServicename);

			wfIntegrator.callWorkFlow(prepareProcessInstanceRequest);

			servicePlanRequest.setStatus(prepareProcessInstanceRequest.getLicenses().get(0).getStatus());
		}

		servicePlanContract.setServicePlanRequest(servicePlanRequestList);

		producer.push(config.getSPsaveTopic(), servicePlanContract);

		return servicePlanRequestList;

	}

	private String bussinessServicename(String purpose) {
		String result;

		switch (purpose) {
		case "RPL":
			result = businessService_TL_DEMARCATION;
			break;
		case "DDJAY_APHP":
			result = businessService_TL_DEMARCATION;
			break;
		case "CICS":
			result = businessService_TL_DEMARCATION;
			break;
		case "CPRS":
			result = businessService_TL_DEMARCATION;
			break;
		case "IPA":
			result = businessService_TL_DEMARCATION;
			break;
		case "NILPC":
			result = businessService_TL_DEMARCATION;
			break;
		case "NILP":
			result = businessService_TL_DEMARCATION;
			break;

		default:
			result = businessService_TL;
		}
		return result;
	}

	String assignee(String role, String tenantID, boolean b, RequestInfo requestInfo) {

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

	List<String> getIdList(RequestInfo requestInfo, String tenantId, String idKey, String idformat, int count) {
		List<IdResponse> idResponses = idGenRepository.getId(requestInfo, tenantId, idKey, idformat, count)
				.getIdResponses();

		if (CollectionUtils.isEmpty(idResponses))
			throw new CustomException("IDGEN ERROR", "No ids returned from idgen Service");

		return idResponses.stream().map(IdResponse::getId).collect(Collectors.toList());
	}

	public List<ServicePlanRequest> searchServicePlan(String loiNumber, String applicationNumber,
			RequestInfo requestInfo) {

		List<Object> preparedStatement = new ArrayList<>();

		Map<String, String> paramMap = new HashedMap();
		Map<String, List<String>> paramMapList = new HashedMap();
		StringBuilder builder;

		String query = "SELECT loi_number, auto_cad_file, certifiead_copy_of_the_plan, environmental_clearance, self_certified_drawing_from_empaneled_doc, self_certified_drawings_from_chareted_eng, shape_file_as_per_template, undertaking, action, assignee, status, business_service, comment, tenantid, application_number, created_by, created_time, last_modified_by, last_modified_time, sp_action, id, additionaldetails, devname, developmentplan, purpose, totalarea, layoutplan, revisedlayout, demarcation, demarcationgis, layoutexcel, anyotherdoc, externalagency, tcpapplicationnumber, tcpcasenumber, tcpdairynumber\r\n"
				+ "	FROM public.eg_service_plan " + "WHERE  ";

		builder = new StringBuilder(query);

		List<ServicePlanRequest> Result = null;
		if (loiNumber != null) {
			builder.append(" loi_number= :LN");
			paramMap.put("LN", loiNumber);
			preparedStatement.add(loiNumber);
			Result = namedParameterJdbcTemplate.query(builder.toString(), paramMap, spRowMapper);

		} else if (applicationNumber != null) {
			List<String> applicationNumberList = Arrays.asList(applicationNumber.split(","));
			if (applicationNumberList != null) {
				builder.append(" application_number in ( :AN )");
				paramMapList.put("AN", applicationNumberList);
				preparedStatement.add(applicationNumberList);
				Result = namedParameterJdbcTemplate.query(builder.toString(), paramMapList, spRowMapper);
			}

		} else if ((requestInfo.getUserInfo().getUuid() != null)) {
			builder.append(" created_by= :CB");
			paramMap.put("CB", requestInfo.getUserInfo().getUuid());
			preparedStatement.add(requestInfo.getUserInfo().getUuid());
			Result = namedParameterJdbcTemplate.query(builder.toString(), paramMap, spRowMapper);

		}

		return Result;
	}

	public List<ServicePlanRequest> Update(ServicePlanContract servicePlanContract) {

		String uuid = servicePlanContract.getRequestInfo().getUserInfo().getUuid();

		AuditDetails auditDetails = tradeUtil.getAuditDetails(uuid, false);

		RequestInfo requestInfo = servicePlanContract.getRequestInfo();

		List<ServicePlanRequest> servicePlanRequestList = servicePlanContract.getServicePlanRequest();

		for (ServicePlanRequest servicePlanRequest : servicePlanRequestList) {

			if (Objects.isNull(servicePlanContract) || Objects.isNull(servicePlanContract.getServicePlanRequest())) {
				throw new CustomException("ServicePlanContract must not be null",
						"ServicePlanContract must not be null");
			}

			if (StringUtils.isEmpty(servicePlanRequest.getApplicationNumber())) {
				throw new CustomException("ApplicationNumber must not be null", "ApplicationNumber must not be null");
			}

			List<ServicePlanRequest> searchServicePlan = searchServicePlan(servicePlanRequest.getLoiNumber(),
					servicePlanRequest.getApplicationNumber(), requestInfo);
			if (CollectionUtils.isEmpty(searchServicePlan) || searchServicePlan.size() > 1) {
				throw new CustomException("Found none or multiple service plan applications with applicationNumber.",
						"Found none or multiple service plan applications with applicationNumber.");
			}

			servicePlanRequest.setBusinessService(servicePlanRequest.getBusinessService());
			servicePlanRequest.setWorkflowCode(servicePlanRequest.getBusinessService());

			// EMPLOYEE RUN THE APPLICATION NORMALLY
			if (!servicePlanRequest.getStatus().equalsIgnoreCase(SENDBACK_STATUS) && !usercheck(requestInfo)) {

				String currentStatus = searchServicePlan.get(0).getStatus();

				BusinessService workflow = workflowService.getBusinessService(servicePlanRequest.getTenantID(),
						servicePlanContract.getRequestInfo(), servicePlanRequest.getBusinessService());

				validateUpdateRoleAndActionFromWorkflow(workflow, currentStatus, servicePlanContract,
						servicePlanRequest);

				servicePlanRequest.setAuditDetails(auditDetails);

				TradeLicenseRequest prepareProcessInstanceRequest = prepareProcessInstanceRequest(servicePlanRequest,
						requestInfo, servicePlanRequest.getBusinessService());

				wfIntegrator.callWorkFlow(prepareProcessInstanceRequest);

				servicePlanRequest.setStatus(prepareProcessInstanceRequest.getLicenses().get(0).getStatus());

			}

			// CITIZEN MODIFY THE APPLICATION WHEN EMPLOYEE SENDBACK TO CITIZEN
			else if ((servicePlanRequest.getStatus().equalsIgnoreCase(SENDBACK_STATUS)) && usercheck(requestInfo)) {

				String currentStatus = searchServicePlan.get(0).getStatus();

				servicePlanRequest.setAssignee(
						Arrays.asList(assignee("STP_HQ", servicePlanRequest.getTenantID(), true, requestInfo)));

				servicePlanRequest.setAction(CITIZEN_UPDATE_ACTION);

				BusinessService workflow = workflowService.getBusinessService(servicePlanRequest.getTenantID(),
						servicePlanContract.getRequestInfo(), servicePlanRequest.getBusinessService());

				validateUpdateRoleAndActionFromWorkflow(workflow, currentStatus, servicePlanContract,
						servicePlanRequest);

				servicePlanRequest.setAuditDetails(auditDetails);

				TradeLicenseRequest prepareProcessInstanceRequest = prepareProcessInstanceRequest(servicePlanRequest,
						requestInfo, servicePlanRequest.getBusinessService());

				wfIntegrator.callWorkFlow(prepareProcessInstanceRequest);

				servicePlanRequest.setStatus(prepareProcessInstanceRequest.getLicenses().get(0).getStatus());

			}
			// CITIZEN MODIFY THE APPLICATION WHEN EMPLOYEE SENDBACK TO CITIZEN AT
			// DEMARCATION PROCESS
			else if ((servicePlanRequest.getStatus().equalsIgnoreCase(SENDBACK_STATUS_DEMARCATION))
					&& usercheck(requestInfo)) {

				String currentStatus = searchServicePlan.get(0).getStatus();

				servicePlanRequest.setAssignee(
						Arrays.asList(assignee("CTP_HR", servicePlanRequest.getTenantID(), true, requestInfo)));

				servicePlanRequest.setAction(CITIZEN_UPDATE_ACTION);

				BusinessService workflow = workflowService.getBusinessService(servicePlanRequest.getTenantID(),
						servicePlanContract.getRequestInfo(), servicePlanRequest.getBusinessService());

				validateUpdateRoleAndActionFromWorkflow(workflow, currentStatus, servicePlanContract,
						servicePlanRequest);

				servicePlanRequest.setAuditDetails(auditDetails);

				TradeLicenseRequest prepareProcessInstanceRequest = prepareProcessInstanceRequest(servicePlanRequest,
						requestInfo, servicePlanRequest.getBusinessService());

				wfIntegrator.callWorkFlow(prepareProcessInstanceRequest);

				servicePlanRequest.setStatus(prepareProcessInstanceRequest.getLicenses().get(0).getStatus());

			}
		}

		servicePlanContract.setServicePlanRequest(servicePlanRequestList);

		producer.push(config.getSPupdateTopic(), servicePlanContract);

		return servicePlanRequestList;

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

	private TradeLicenseRequest prepareProcessInstanceRequest(ServicePlanRequest servicePlanRequest,
			RequestInfo requestInfo, String bussinessServicename) {

		TradeLicenseRequest tradeLicenseRequest = new TradeLicenseRequest();
		TradeLicense tradeLicenseSP = new TradeLicense();
		List<TradeLicense> tradeLicenseSPlist = new ArrayList<>();
		tradeLicenseSP.setBusinessService(servicePlanRequest.getBusinessService());
		tradeLicenseSP.setAction(servicePlanRequest.getAction());
		tradeLicenseSP.setAssignee(servicePlanRequest.getAssignee());
		tradeLicenseSP.setApplicationNumber(servicePlanRequest.getApplicationNumber());
		tradeLicenseSP.setWorkflowCode(servicePlanRequest.getWorkflowCode());
		TradeLicenseDetail tradeLicenseDetail = new TradeLicenseDetail();
		tradeLicenseDetail.setTradeType(bussinessServicename);
		tradeLicenseSP.setTradeLicenseDetail(tradeLicenseDetail);
		tradeLicenseSP.setComment(servicePlanRequest.getComment());
		tradeLicenseSP.setWfDocuments(servicePlanRequest.getWfDocuments());
		tradeLicenseSP.setTenantId(servicePlanRequest.getTenantID());
		tradeLicenseSP.setBusinessService(bussinessServicename);

		tradeLicenseRequest.setRequestInfo(requestInfo);
		tradeLicenseSPlist.add(tradeLicenseSP);
		tradeLicenseRequest.setLicenses(tradeLicenseSPlist);

		return tradeLicenseRequest;
	}

	private void validateUpdateRoleAndActionFromWorkflow(BusinessService workflow, String currentStatus,
			ServicePlanContract servicePlanContract, ServicePlanRequest servicePlanRequest) {
		// validate Action-
		Optional<State> currentWorkflowStateOptional = workflow.getStates().stream()
				.filter(state -> state.getState().equals(currentStatus)).findFirst();
		if (!currentWorkflowStateOptional.isPresent()) {
			throw new CustomException("workflow State not found:" + currentStatus,
					"workflow State not found:" + currentStatus);
		}
		State currentWorkflowState = currentWorkflowStateOptional.get();
		List<Action> permissibleActions = currentWorkflowState.getActions();
		String currentActionFromRequest = servicePlanRequest.getAction();
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
		List<Role> rolesFromUserInfo = servicePlanContract.getRequestInfo().getUserInfo().getRoles();
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
		servicePlanRequest.setStatus(nextStateName);
	}
}
