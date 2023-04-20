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
import org.egov.tl.repository.rowmapper.ExtensionOfCLUPermissionRowMapper;
import org.egov.tl.util.TradeUtil;
import org.egov.tl.web.models.AuditDetails;
import org.egov.tl.web.models.ExtensionOfCLUPermission;
import org.egov.tl.web.models.ExtensionOfCLUPermissionRequest;
import org.egov.tl.web.models.SurrendOfLicense;
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
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class ExtensionOfCLUPermissionServices {
	
	private static final String BUSINESS_EXTENTION = "EXTENTION_OF_CLU_PERMISSION";

	@Value("${persister.create.extension.of.clu.permission.topic}")
	private String cluTopic;

	@Value("${persister.update.extension.of.clu.permission.topic}")
	private String cluUpdateTopic;

	@Autowired
	private ExtensionOfCLUPermissionRowMapper extensionOfCLUPermissionRowMapper;
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
	
	private static final String SENDBACK_STATUS = "SENDBACK_TO_APPLICANT";

	private static final String CITIZEN_UPDATE_ACTION = "FORWARD";
	
	
	@SuppressWarnings("null")
	public List<ExtensionOfCLUPermission> create(ExtensionOfCLUPermissionRequest extensionOfCLUPermissionRequest) {

		String uuid = extensionOfCLUPermissionRequest.getRequestInfo().getUserInfo().getUuid();

		AuditDetails auditDetails = tradeUtil.getAuditDetails(uuid, true);

		RequestInfo requestInfo = extensionOfCLUPermissionRequest.getRequestInfo();
		List<ExtensionOfCLUPermission> renewalList = extensionOfCLUPermissionRequest.getExtensionOfCLUPermission();

		for (ExtensionOfCLUPermission extensionOfCLUPermission : renewalList) {

			List<String> applicationNo = null;
			int count = 1;
			List<ExtensionOfCLUPermission> searchExtensionOfCLUPermission = search(requestInfo, extensionOfCLUPermission.getLicenseNo(),
					extensionOfCLUPermission.getApplicationNo());
			if (!CollectionUtils.isEmpty(searchExtensionOfCLUPermission) || searchExtensionOfCLUPermission.size() > 1) {
				throw new CustomException("Already Found  or multiple surender of licence applications with LoiNumber.",
						"Already Found or multiple Service plan applications with LoiNumber.");
			}		
			extensionOfCLUPermission.setId(UUID.randomUUID().toString());
			extensionOfCLUPermission.setAssignee(Arrays
				.asList(servicePlanService.assignee("CTP_HR", extensionOfCLUPermission.getTenantId(), true, requestInfo)));
//////		approvalStandardRequest.setAssignee(Arrays.asList("f9b7acaf-c1fb-4df2-ac10-83b55238a724"));
			applicationNo = servicePlanService.getIdList(requestInfo, extensionOfCLUPermission.getTenantId(),
					config.getSurrenderName(), config.getSurrenderFormat(), count);
			extensionOfCLUPermission.setAction("INITIATE");
			extensionOfCLUPermission.setAuditDetails(auditDetails);
			extensionOfCLUPermission.setBusinessService(BUSINESS_EXTENTION);
			extensionOfCLUPermission.setWorkflowCode(BUSINESS_EXTENTION);

			extensionOfCLUPermission.setApplicationNo(applicationNo.get(0));
			TradeLicenseRequest prepareProcessInstanceRequest = prepareProcessInstanceRequest(
					extensionOfCLUPermissionRequest.getExtensionOfCLUPermission().get(0), requestInfo, extensionOfCLUPermission.getBusinessService());

			wfIntegrator.callWorkFlow(prepareProcessInstanceRequest);
			extensionOfCLUPermission.setStatus(prepareProcessInstanceRequest.getLicenses().get(0).getStatus());
		}

		extensionOfCLUPermissionRequest.setExtensionOfCLUPermission(renewalList);

		log.info(cluTopic);

		producer.push(cluTopic, extensionOfCLUPermissionRequest);

		return renewalList;
	}
	
	public List<ExtensionOfCLUPermission> update(ExtensionOfCLUPermissionRequest extensionOfCLUPermissionRequest) {

		String uuid = extensionOfCLUPermissionRequest.getRequestInfo().getUserInfo().getUuid();

		AuditDetails auditDetails = tradeUtil.getAuditDetails(uuid, false);

		RequestInfo requestInfo = extensionOfCLUPermissionRequest.getRequestInfo();

		List<ExtensionOfCLUPermission> extensionOfCLUPermissionList = extensionOfCLUPermissionRequest.getExtensionOfCLUPermission();

		for (ExtensionOfCLUPermission extensionOfCLUPermission : extensionOfCLUPermissionList) {

			if (Objects.isNull(extensionOfCLUPermissionRequest)
					|| Objects.isNull(extensionOfCLUPermissionRequest.getExtensionOfCLUPermission())) {
				throw new CustomException("approval of standard design must not be null",
						"approval of standard design must not be null");
			}

			if (StringUtils.isEmpty(extensionOfCLUPermission.getApplicationNo())) {
				throw new CustomException("ApplicationNumber must not be null", "ApplicationNumber must not be null");
			}

			List<ExtensionOfCLUPermission> searchExtensionOfCLUPermission = search(requestInfo, extensionOfCLUPermission.getLicenseNo(),
					extensionOfCLUPermission.getApplicationNo());
			if (CollectionUtils.isEmpty(searchExtensionOfCLUPermission) || searchExtensionOfCLUPermission.size() > 1) {
				throw new CustomException(
						"Found none or multiple approval of standard design applications with applicationNumber.",
						"Found none or multiple approval of standard design applications with applicationNumber.");
			}

			extensionOfCLUPermission.setBusinessService(extensionOfCLUPermission.getBusinessService());
			extensionOfCLUPermission.setWorkflowCode(extensionOfCLUPermission.getBusinessService());

			// EMPLOYEE RUN THE APPLICATION NORMALLY
			if (!extensionOfCLUPermission.getStatus().equalsIgnoreCase(SENDBACK_STATUS) && !usercheck(requestInfo)) {

				String currentStatus = searchExtensionOfCLUPermission.get(0).getStatus();

				BusinessService workflow = workflowService.getBusinessService(extensionOfCLUPermission.getTenantId(),
						extensionOfCLUPermissionRequest.getRequestInfo(), extensionOfCLUPermission.getBusinessService());

				validateUpdateRoleAndActionFromWorkflow(workflow, currentStatus, extensionOfCLUPermissionRequest,
						extensionOfCLUPermission);

				extensionOfCLUPermission.setAuditDetails(auditDetails);

				TradeLicenseRequest prepareProcessInstanceRequest = prepareProcessInstanceRequest(extensionOfCLUPermission,
						requestInfo, extensionOfCLUPermission.getBusinessService());

				wfIntegrator.callWorkFlow(prepareProcessInstanceRequest);

				extensionOfCLUPermission.setStatus(prepareProcessInstanceRequest.getLicenses().get(0).getStatus());

			}

			// CITIZEN MODIFY THE APPLICATION WHEN EMPLOYEE SENDBACK TO CITIZEN
			else if ((extensionOfCLUPermission.getStatus().equalsIgnoreCase(SENDBACK_STATUS)) && usercheck(requestInfo)) {

				String currentStatus = searchExtensionOfCLUPermission.get(0).getStatus();

				extensionOfCLUPermission.setAssignee(Arrays
						.asList(servicePlanService.assignee("CAO", extensionOfCLUPermission.getTenantId(), true, requestInfo)));

				extensionOfCLUPermission.setAction(CITIZEN_UPDATE_ACTION);

				BusinessService workflow = workflowService.getBusinessService(extensionOfCLUPermission.getTenantId(),
						extensionOfCLUPermissionRequest.getRequestInfo(), extensionOfCLUPermission.getBusinessService());

				validateUpdateRoleAndActionFromWorkflow(workflow, currentStatus, extensionOfCLUPermissionRequest,
						extensionOfCLUPermission);

				extensionOfCLUPermission.setAuditDetails(auditDetails);

				TradeLicenseRequest prepareProcessInstanceRequest = prepareProcessInstanceRequest(extensionOfCLUPermission,
						requestInfo, extensionOfCLUPermission.getBusinessService());

				wfIntegrator.callWorkFlow(prepareProcessInstanceRequest);

				extensionOfCLUPermission.setStatus(prepareProcessInstanceRequest.getLicenses().get(0).getStatus());

			}
		}

		extensionOfCLUPermissionRequest.setExtensionOfCLUPermission(extensionOfCLUPermissionList);
		
		producer.push(cluUpdateTopic, extensionOfCLUPermissionRequest);

		return extensionOfCLUPermissionList;

	}
	
	private TradeLicenseRequest prepareProcessInstanceRequest(ExtensionOfCLUPermission extensionOfCLUPermission,
			RequestInfo requestInfo, String bussinessServicename) {

		TradeLicenseRequest tradeLicenseASRequest = new TradeLicenseRequest();
		TradeLicense tradeLicenseAS = new TradeLicense();
		List<TradeLicense> tradeLicenseASlist = new ArrayList<>();
		tradeLicenseAS.setBusinessService(bussinessServicename);
		tradeLicenseAS.setAction(extensionOfCLUPermission.getAction());
		tradeLicenseAS.setAssignee(extensionOfCLUPermission.getAssignee());
		tradeLicenseAS.setApplicationNumber(extensionOfCLUPermission.getApplicationNo());
		tradeLicenseAS.setWorkflowCode(bussinessServicename);
		TradeLicenseDetail tradeLicenseDetail = new TradeLicenseDetail();
		tradeLicenseDetail.setTradeType(bussinessServicename);
		tradeLicenseAS.setTradeLicenseDetail(tradeLicenseDetail);
		tradeLicenseAS.setComment("Workflow for Extention CLU");
		tradeLicenseAS.setWfDocuments(null);
		tradeLicenseAS.setTenantId(extensionOfCLUPermission.getTenantId());
		// tradeLicenseAS.setBusinessService("SURRENDER_OF_LICENSE");

		tradeLicenseASRequest.setRequestInfo(requestInfo);
		tradeLicenseASlist.add(tradeLicenseAS);
		tradeLicenseASRequest.setLicenses(tradeLicenseASlist);

		return tradeLicenseASRequest;
	}
	
	public List<ExtensionOfCLUPermission> search(RequestInfo info,  String licenseNo, String applicattionNo) {
		List<Object> preparedStatement = new ArrayList<>();

		Map<String, String> paramMap = new HashedMap();
		Map<String, List<String>> paramMapList = new HashedMap();
		StringBuilder builder;

		String query = "SELECT id, license_no, case_no, application_no, nature_purpose, total_area_sq, clu_date, expiry_clu, stage_construction, applicant_name, mobile, email_address, address, village, tehsil, pin_code, reason_delay, building_plan_approval_status, zoning_plan_approval_date, date_of_sanction_building_plan, applied_first_time, \"upload_brIIIfile_url\", clu_permission_letterfile_url, upload_photographsfile_url, receipt_applicationfile_url, upload_building_planfile_url, indemnity_bondfile_url, additional_details, \"businessService\", comment, workflow_code, status, tenant_id, created_by, created_time, \"lastModified_by\", \"lastModified_time\"\r\n"
				+ "	FROM public.eg_extension_of_clu_permission " + " Where ";
		builder = new StringBuilder(query);

		List<ExtensionOfCLUPermission> Result = null;

		if (licenseNo != null) {
			builder.append(" license_no= :LN");
			paramMap.put("LN", licenseNo);
			preparedStatement.add(licenseNo);
			Result = namedParameterJdbcTemplate.query(builder.toString(), paramMap, extensionOfCLUPermissionRowMapper);
		} else if (applicattionNo != null) {
			List<String> applicationNoList = Arrays.asList(applicattionNo.split(","));
			log.info("applicationNoList" + applicationNoList);
			if (applicationNoList != null) {
				builder.append(" application_no in ( :AN )");
				paramMapList.put("AN", applicationNoList);
				preparedStatement.add(applicationNoList);
				Result = namedParameterJdbcTemplate.query(builder.toString(), paramMapList, extensionOfCLUPermissionRowMapper);
			}

		} else if ((info.getUserInfo().getUuid() != null)) {
			builder.append(" created_by= :CB");
			paramMap.put("CB", info.getUserInfo().getUuid());
			preparedStatement.add(info.getUserInfo().getUuid());
			Result = namedParameterJdbcTemplate.query(builder.toString(), paramMap, extensionOfCLUPermissionRowMapper);

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
			ExtensionOfCLUPermissionRequest extensionOfCLUPermissionRequest, ExtensionOfCLUPermission extensionOfCLUPermission) {
		// validate Action-
		Optional<State> currentWorkflowStateOptional = workflow.getStates().stream()
				.filter(state -> state.getState().equals(currentStatus)).findFirst();
		if (!currentWorkflowStateOptional.isPresent()) {
			throw new CustomException("workflow State not found:" + currentStatus,
					"workflow State not found:" + currentStatus);
		}
		State currentWorkflowState = currentWorkflowStateOptional.get();
		List<Action> permissibleActions = currentWorkflowState.getActions();
		String currentActionFromRequest = extensionOfCLUPermission.getAction();
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
		List<Role> rolesFromUserInfo = extensionOfCLUPermissionRequest.getRequestInfo().getUserInfo().getRoles();
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
		extensionOfCLUPermission.setStatus(nextStateName);
	}

}
