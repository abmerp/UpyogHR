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
import org.egov.tl.repository.rowmapper.SPRowMapper;
import org.egov.tl.service.repo.ServicePlanRepo;
import org.egov.tl.util.TradeUtil;
import org.egov.tl.web.models.AuditDetails;
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

import com.itextpdf.text.pdf.PdfStructTreeController.returnType;

@Service
public class ServicePlanService {

	private static final String businessService_TL = "SERVICE_PLAN";

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

	public ServicePlanRequest create(ServicePlanContract servicePlanContract) {

		String uuid = servicePlanContract.getRequestInfo().getUserInfo().getUuid();

		AuditDetails auditDetails = tradeUtil.getAuditDetails(uuid, true);

		ServicePlanRequest servicePlanRequest = servicePlanContract.getServicePlanRequest();

		List<String> applicationNumbers = null;
		int count = 1;

		applicationNumbers = getIdList(servicePlanContract.getRequestInfo(), servicePlanRequest.getTenantID(),
				config.getSPapplicationNumberIdgenNameTL(), config.getSPapplicationNumberIdgenFormatTL(), count);

		servicePlanRequest.setAuditDetails(auditDetails);
		servicePlanRequest.setApplicationNumber(applicationNumbers.get(0));

		TradeLicenseRequest prepareProcessInstanceRequest = prepareProcessInstanceRequest(servicePlanContract);

		wfIntegrator.callWorkFlow(prepareProcessInstanceRequest);

		servicePlanRequest.setStatus(prepareProcessInstanceRequest.getLicenses().get(0).getStatus());

		servicePlanContract.setServicePlanRequest(servicePlanRequest);

		producer.push(config.getSPsaveTopic(), servicePlanContract);

		return servicePlanRequest;

	}

	private List<String> getIdList(RequestInfo requestInfo, String tenantId, String idKey, String idformat, int count) {
		List<IdResponse> idResponses = idGenRepository.getId(requestInfo, tenantId, idKey, idformat, count)
				.getIdResponses();

		if (CollectionUtils.isEmpty(idResponses))
			throw new CustomException("IDGEN ERROR", "No ids returned from idgen Service");

		return idResponses.stream().map(IdResponse::getId).collect(Collectors.toList());
	}

	public List<ServicePlanRequest> searchServicePlan(String loiNumber, String applicationNumber) {

		List<Object> preparedStatement = new ArrayList<>();

		Map<String, String> paramMap = new HashedMap();
		Map<String, List<String>> paramMapList = new HashedMap();
		StringBuilder builder;

		String query = "SELECT loi_number, auto_cad_file, certifiead_copy_of_the_plan, environmental_clearance, self_certified_drawing_from_empaneled_doc, self_certified_drawings_from_chareted_eng, shape_file_as_per_template, status, sp_action, undertaking, assignee, action, business_service, comment, tenantid, application_number , created_by, created_time, last_modified_by, last_modified_time\r\n"
				+ "FROM public.eg_service_plan\r\n" + "WHERE business_service = 'SERVICE_PLAN' ";

		builder = new StringBuilder(query);

		List<ServicePlanRequest> Result = null;
		if (loiNumber != null) {
			builder.append("and loi_number= :LN");
			paramMap.put("LN", loiNumber);
			preparedStatement.add(loiNumber);
			Result = namedParameterJdbcTemplate.query(builder.toString(), paramMap, spRowMapper);

		} else if (applicationNumber != null) {
			List<String> applicationNumberList = Arrays.asList(applicationNumber.split(","));
			if (applicationNumberList != null) {
				builder.append("and application_number in ( :AN )");
				paramMapList.put("AN", applicationNumberList);
				preparedStatement.add(applicationNumberList);
				Result = namedParameterJdbcTemplate.query(builder.toString(), paramMapList, spRowMapper);
			}
		}

		return Result;
	}

	public ServicePlanRequest Update(ServicePlanContract servicePlanContract) {

		String uuid = servicePlanContract.getRequestInfo().getUserInfo().getUuid();

		AuditDetails auditDetails = tradeUtil.getAuditDetails(uuid, false);

		ServicePlanRequest servicePlanRequest = servicePlanContract.getServicePlanRequest();

		if (Objects.isNull(servicePlanContract) || Objects.isNull(servicePlanContract.getServicePlanRequest())) {
			throw new CustomException("ServicePlanContract must not be null", "ServicePlanContract must not be null");
		}

		if (StringUtils.isEmpty(servicePlanContract.getServicePlanRequest().getApplicationNumber())) {
			throw new CustomException("ApplicationNumber must not be null", "ApplicationNumber must not be null");
		}

		List<ServicePlanRequest> searchServicePlan = searchServicePlan(
				servicePlanContract.getServicePlanRequest().getLoiNumber(),
				servicePlanContract.getServicePlanRequest().getApplicationNumber());
		if (CollectionUtils.isEmpty(searchServicePlan) || searchServicePlan.size() > 1) {
			throw new CustomException("Found none or multiple service plan applications with applicationNumber.",
					"Found none or multiple service plan applications with applicationNumber.");
		}

		String currentStatus = searchServicePlan.get(0).getStatus();

		BusinessService workflow = workflowService.getBusinessService(servicePlanRequest.getTenantID(),
				servicePlanContract.getRequestInfo(), businessService_TL);

		validateUpdateRoleAndActionFromWorkflow(workflow, currentStatus, servicePlanContract);
//		enrichAuditDetailsOnUpdate(servicePlanContract); 

		List<String> applicationNumbers = null;
		int count = 1;

//		applicationNumbers = getIdList(servicePlanContract.getRequestInfo(), servicePlanRequest.getTenantID(),
//				config.getSPapplicationNumberIdgenNameTL(), config.getSPapplicationNumberIdgenFormatTL(), count);

		servicePlanRequest.setAuditDetails(auditDetails);
//		servicePlanRequest.setApplicationNumber(applicationNumbers.get(0));

		TradeLicenseRequest prepareProcessInstanceRequest = prepareProcessInstanceRequest(servicePlanContract);

		wfIntegrator.callWorkFlow(prepareProcessInstanceRequest);

		servicePlanRequest.setStatus(prepareProcessInstanceRequest.getLicenses().get(0).getStatus());

		servicePlanContract.setServicePlanRequest(servicePlanRequest);

		producer.push(config.getSPupdateTopic(), servicePlanContract);

		return servicePlanRequest;

	}

	private TradeLicenseRequest prepareProcessInstanceRequest(ServicePlanContract servicePlanContract) {

		ServicePlanRequest servicePlanRequest = servicePlanContract.getServicePlanRequest();

		TradeLicenseRequest tradeLicenseRequest = new TradeLicenseRequest();
		TradeLicense tradeLicenseSP = new TradeLicense();
		List<TradeLicense> tradeLicenseSPlist = new ArrayList<>();
		tradeLicenseSP.setBusinessService(servicePlanRequest.getBusinessService());
		tradeLicenseSP.setAction(servicePlanRequest.getAction());
		tradeLicenseSP.setAssignee(servicePlanRequest.getAssignee());
		tradeLicenseSP.setApplicationNumber(servicePlanRequest.getApplicationNumber());
		tradeLicenseSP.setWorkflowCode(businessService_TL);
		TradeLicenseDetail tradeLicenseDetail = new TradeLicenseDetail();
		tradeLicenseDetail.setTradeType(businessService_TL);
		tradeLicenseSP.setTradeLicenseDetail(tradeLicenseDetail);
		tradeLicenseSP.setComment(servicePlanRequest.getComment());
		tradeLicenseSP.setWfDocuments(null);
		tradeLicenseSP.setTenantId(servicePlanRequest.getTenantID());
		tradeLicenseSP.setBusinessService(businessService_TL);

		tradeLicenseRequest.setRequestInfo(servicePlanContract.getRequestInfo());
		tradeLicenseSPlist.add(tradeLicenseSP);
		tradeLicenseRequest.setLicenses(tradeLicenseSPlist);

		return tradeLicenseRequest;
	}

	private void validateUpdateRoleAndActionFromWorkflow(BusinessService workflow, String currentStatus,
			ServicePlanContract servicePlanContract) {
		// validate Action-
		Optional<State> currentWorkflowStateOptional = workflow.getStates().stream()
				.filter(state -> state.getState().equals(currentStatus)).findFirst();
		if (!currentWorkflowStateOptional.isPresent()) {
			throw new CustomException("workflow State not found:" + currentStatus,
					"workflow State not found:" + currentStatus);
		}
		State currentWorkflowState = currentWorkflowStateOptional.get();
		List<Action> permissibleActions = currentWorkflowState.getActions();
		String currentActionFromRequest = servicePlanContract.getServicePlanRequest().getAction();
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
		servicePlanContract.getServicePlanRequest().setStatus(nextStateName);
	}
}
