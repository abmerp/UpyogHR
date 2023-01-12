package org.egov.tl.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import org.apache.commons.collections.map.HashedMap;
import org.egov.common.contract.request.RequestInfo;
import org.egov.common.contract.request.Role;
import org.egov.tl.config.TLConfiguration;
import org.egov.tl.producer.Producer;
import org.egov.tl.repository.IdGenRepository;
import org.egov.tl.repository.rowmapper.EPRowMapper;
import org.egov.tl.repository.rowmapper.SPRowMapper;
import org.egov.tl.service.repo.ElectricPlanRepo;
import org.egov.tl.util.TradeUtil;
import org.egov.tl.web.models.AuditDetails;
import org.egov.tl.web.models.ElectricPlan;
import org.egov.tl.web.models.ElectricPlanContract;
import org.egov.tl.web.models.ElectricPlanRequest;
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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

@Service
public class ElectricPlanService {

	private static final String businessService_TL = "ELECTRICAL_PLAN";

	@Autowired
	ElectricPlanRepo electricPlanRepo;

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
	private WorkflowService workflowService;

	public ElectricPlanRequest create(ElectricPlanContract electricPlanContract) {

		String uuid = electricPlanContract.getRequestInfo().getUserInfo().getUuid();

		AuditDetails auditDetails = tradeUtil.getAuditDetails(uuid, true);

		ElectricPlanRequest electricPlanRequest = electricPlanContract.getElectricPlanRequest();

		List<String> applicationNumbers = null;
		int count = 1;

		applicationNumbers = getIdList(electricPlanContract.getRequestInfo(), electricPlanRequest.getTenantID(),
				config.getEPapplicationNumberIdgenNameTL(), config.getEPapplicationNumberIdgenFormatTL(), count);

		electricPlanRequest.setAuditDetails(auditDetails);
		electricPlanRequest.setApplicationNumber(applicationNumbers.get(0));

		TradeLicenseRequest prepareProcessInstanceRequest = prepareProcessInstanceRequest(electricPlanContract);

		wfIntegrator.callWorkFlow(prepareProcessInstanceRequest);

		electricPlanRequest.setStatus(prepareProcessInstanceRequest.getLicenses().get(0).getStatus());

		electricPlanContract.setElectricPlanRequest(electricPlanRequest);

		producer.push(config.getEPsaveTopic(), electricPlanContract);

		return electricPlanRequest;

	}

	public List<ElectricPlanRequest> searchElectricPlan(String loiNumber, String applicationNumber) {

		List<Object> preparedStatement = new ArrayList<>();

		Map<String, String> paramMap = new HashedMap();
		Map<String, List<String>> paramMapList = new HashedMap();
		StringBuilder builder;

		String query = "SELECT id, auto_cad, elecric_distribution, electrical_capacity, electrical_infra, environmental_clearance, load_sancation, switching_station, verified_plan, loi_number, self_centred_drawing, shap_file_template, tenantid, created_by, created_time, last_modified_by, last_modified_time, application_number, business_service, action, status, comment, pdf_format\r\n"
				+ "FROM public.eg_electric_plan" + " WHERE business_service = 'ELECTRICAL_PLAN' ";

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

	private TradeLicenseRequest prepareProcessInstanceRequest(ElectricPlanContract electricPlanContract) {

		ElectricPlanRequest electricPlanRequest = electricPlanContract.getElectricPlanRequest();

		TradeLicenseRequest tradeLicenseRequest = new TradeLicenseRequest();
		TradeLicense tradeLicenseEP = new TradeLicense();
		List<TradeLicense> tradeLicenseEPlist = new ArrayList<>();
		tradeLicenseEP.setBusinessService(electricPlanRequest.getBusinessService());
		tradeLicenseEP.setAction(electricPlanRequest.getAction());
		tradeLicenseEP.setAssignee(electricPlanRequest.getAssignee());
		tradeLicenseEP.setApplicationNumber(electricPlanRequest.getApplicationNumber());
		tradeLicenseEP.setWorkflowCode(businessService_TL);
		TradeLicenseDetail tradeLicenseDetail = new TradeLicenseDetail();
		tradeLicenseDetail.setTradeType(businessService_TL);
		tradeLicenseEP.setTradeLicenseDetail(tradeLicenseDetail);
		tradeLicenseEP.setComment(electricPlanRequest.getComment());
		tradeLicenseEP.setWfDocuments(null);
		tradeLicenseEP.setTenantId(electricPlanRequest.getTenantID());
		tradeLicenseEP.setBusinessService(businessService_TL);

		tradeLicenseRequest.setRequestInfo(electricPlanContract.getRequestInfo());
		tradeLicenseEPlist.add(tradeLicenseEP);
		tradeLicenseRequest.setLicenses(tradeLicenseEPlist);

		return tradeLicenseRequest;
	}

	public ElectricPlanRequest Update(ElectricPlanContract electricPlanContract) {

		String uuid = electricPlanContract.getRequestInfo().getUserInfo().getUuid();

		AuditDetails auditDetails = tradeUtil.getAuditDetails(uuid, false);

		ElectricPlanRequest electricPlanRequest = electricPlanContract.getElectricPlanRequest();

		if (Objects.isNull(electricPlanContract) || Objects.isNull(electricPlanContract.getElectricPlanRequest())) {
			throw new CustomException("ElectricalPlanContract must not be null",
					"ServicePlanContract must not be null");
		}

		if (StringUtils.isEmpty(electricPlanContract.getElectricPlanRequest().getApplicationNumber())) {
			throw new CustomException("ApplicationNumber must not be null", "ApplicationNumber must not be null");
		}

		String loiNumber = null;
		String applicationNumber = electricPlanContract.getElectricPlanRequest().getApplicationNumber();
		List<ElectricPlanRequest> searchServicePlan = searchElectricPlan(loiNumber, applicationNumber);
		if (CollectionUtils.isEmpty(searchServicePlan) || searchServicePlan.size() > 1) {
			throw new CustomException("Found none or multiple service plan applications with applicationNumber.",
					"Found none or multiple service plan applications with applicationNumber.");
		}

		String currentStatus = searchServicePlan.get(0).getStatus();

		BusinessService workflow = workflowService.getBusinessService(electricPlanRequest.getTenantID(),
				electricPlanContract.getRequestInfo(), businessService_TL);

		validateUpdateRoleAndActionFromWorkflow(workflow, currentStatus, electricPlanContract);

		List<String> applicationNumbers = null;
		int count = 1;

		electricPlanRequest.setAuditDetails(auditDetails);

		TradeLicenseRequest prepareProcessInstanceRequest = prepareProcessInstanceRequest(electricPlanContract);

		wfIntegrator.callWorkFlow(prepareProcessInstanceRequest);

		electricPlanRequest.setStatus(prepareProcessInstanceRequest.getLicenses().get(0).getStatus());

		electricPlanContract.setElectricPlanRequest(electricPlanRequest);

		producer.push(config.getEPupdateTopic(), electricPlanContract);

		return electricPlanRequest;

	}

	private void validateUpdateRoleAndActionFromWorkflow(BusinessService workflow, String currentStatus,
			ElectricPlanContract electricPlanContract) {
		// validate Action-
		Optional<State> currentWorkflowStateOptional = workflow.getStates().stream()
				.filter(state -> state.getState().equals(currentStatus)).findFirst();
		if (!currentWorkflowStateOptional.isPresent()) {
			throw new CustomException("workflow State not found:" + currentStatus,
					"workflow State not found:" + currentStatus);
		}
		State currentWorkflowState = currentWorkflowStateOptional.get();
		List<Action> permissibleActions = currentWorkflowState.getActions();
		String currentActionFromRequest = electricPlanContract.getElectricPlanRequest().getAction();
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
		electricPlanContract.getElectricPlanRequest().setStatus(nextStateName);
	}
}
