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
import org.egov.tl.abm.newservices.contract.SurrendOfLicenseContract;
import org.egov.tl.abm.newservices.entity.ApprovalStandardEntity;

import org.egov.tl.config.TLConfiguration;
import org.egov.tl.producer.Producer;
import org.egov.tl.repository.rowmapper.RevisedLayoutPlanRowMapper;
import org.egov.tl.repository.rowmapper.SurrendOfLicenseRowMapper;
//import org.egov.tl.service.repo.SurrendOfLicenseRepo;
import org.egov.tl.util.TradeUtil;
import org.egov.tl.web.models.AuditDetails;
import org.egov.tl.web.models.RevisedPlan;
import org.egov.tl.web.models.RevisedPlanRequest;
import org.egov.tl.web.models.SurrendOfLicense;
import org.egov.tl.web.models.SurrendOfLicenseRequest;
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
public class SurrendOfLicenseServices {

	private static final String BUSINESS_SURRENDER = "SURREND_OF_LICENSE";

	@Value("${persister.create.surrend.of.license.topic}")
	private String surrendTopic;

	@Value("${persister.update.surrend.of.license.topic}")
	private String surrendUpdateTopic;

	@Autowired
	private SurrendOfLicenseRowMapper surrendOfLicenseRowMapper;
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
	@Autowired
	ObjectMapper mapper;
	private static final String SENDBACK_STATUS = "SENDBACK_TO_APPLICANT";

	private static final String CITIZEN_UPDATE_ACTION = "FORWARD";

	@SuppressWarnings("null")
	public List<SurrendOfLicense> create(SurrendOfLicenseRequest surrendOfLicenseRequest)
			throws JsonProcessingException {

		String uuid = surrendOfLicenseRequest.getRequestInfo().getUserInfo().getUuid();

		AuditDetails auditDetails = tradeUtil.getAuditDetails(uuid, true);

		RequestInfo requestInfo = surrendOfLicenseRequest.getRequestInfo();
		SurrendOfLicense surrendOfLicense = surrendOfLicenseRequest.getSurrendOfLicense();

		// for (SurrendOfLicense surrendOfLicense : renewalList) {

		List<String> applicationNumbers = null;
		int count = 1;
		List<SurrendOfLicense> searchSurrendOfLicense = search(requestInfo, surrendOfLicense.getLicenseNo(),
				surrendOfLicense.getApplicationNumber());
		if (!CollectionUtils.isEmpty(searchSurrendOfLicense) || searchSurrendOfLicense.size() > 1) {

			throw new CustomException("Already Found  or multiple surender of licence applications with LoiNumber.",
					"Already Found or multiple Service plan applications with LoiNumber.");
		}
		surrendOfLicense.setTenantId("hr");
		surrendOfLicense.setId(UUID.randomUUID().toString());

		applicationNumbers = servicePlanService.getIdList(requestInfo, surrendOfLicense.getTenantId(),
				config.getSurrenderName(), config.getSurrenderFormat(), count);

		surrendOfLicense.setAuditDetails(auditDetails);

		surrendOfLicense.setApplicationNumber(applicationNumbers.get(0));

		// }

//		surrendOfLicense.setTcpApplicationNumber(surrendOfLicenseData.getTcpApplicationNumber());
//		surrendOfLicense.setTcpCaseNumber(surrendOfLicenseData.getTcpCaseNumber());
//		surrendOfLicense.setTcpDairyNumber(surrendOfLicenseData.getTcpDairyNumber());
		surrendOfLicenseRequest.setSurrendOfLicense(surrendOfLicense);

		log.info(surrendTopic);

		producer.push(surrendTopic, surrendOfLicenseRequest);
		List<SurrendOfLicense> surrendOfLicenseData = makePayment(surrendOfLicenseRequest);
		return surrendOfLicenseData;
	}

	public List<SurrendOfLicense> update(SurrendOfLicenseContract surrendOfLicenseRequest) {

		String uuid = surrendOfLicenseRequest.getRequestInfo().getUserInfo().getUuid();

		AuditDetails auditDetails = tradeUtil.getAuditDetails(uuid, false);

		RequestInfo requestInfo = surrendOfLicenseRequest.getRequestInfo();

		List<SurrendOfLicense> surrendOfLicenseList = surrendOfLicenseRequest.getSurrendOfLicense();

		for (SurrendOfLicense surrendOfLicense : surrendOfLicenseList) {

			if (Objects.isNull(surrendOfLicenseRequest)
					|| Objects.isNull(surrendOfLicenseRequest.getSurrendOfLicense())) {
				throw new CustomException("approval of standard design must not be null",
						"approval of standard design must not be null");
			}

			if (StringUtils.isEmpty(surrendOfLicense.getApplicationNumber())) {
				throw new CustomException("ApplicationNumber must not be null", "ApplicationNumber must not be null");
			}

			List<SurrendOfLicense> searchSurrendOfLicense = search(requestInfo, surrendOfLicense.getLicenseNo(),
					surrendOfLicense.getApplicationNumber());
			if (CollectionUtils.isEmpty(searchSurrendOfLicense) || searchSurrendOfLicense.size() > 1) {
				throw new CustomException(
						"Found none or multiple approval of standard design applications with applicationNumber.",
						"Found none or multiple approval of standard design applications with applicationNumber.");
			}

			surrendOfLicense.setBusinessService(surrendOfLicense.getBusinessService());
			surrendOfLicense.setWorkflowCode(surrendOfLicense.getBusinessService());

			// EMPLOYEE RUN THE APPLICATION NORMALLY
			if (!surrendOfLicense.getStatus().equalsIgnoreCase(SENDBACK_STATUS) && !usercheck(requestInfo)) {

				String currentStatus = searchSurrendOfLicense.get(0).getStatus();

				BusinessService workflow = workflowService.getBusinessService(surrendOfLicense.getTenantId(),
						surrendOfLicenseRequest.getRequestInfo(), surrendOfLicense.getBusinessService());

				validateUpdateRoleAndActionFromWorkflow(workflow, currentStatus, surrendOfLicenseRequest,
						surrendOfLicense);

				surrendOfLicense.setAuditDetails(auditDetails);

				TradeLicenseRequest prepareProcessInstanceRequest = prepareProcessInstanceRequest(surrendOfLicense,
						requestInfo, surrendOfLicense.getBusinessService());

				wfIntegrator.callWorkFlow(prepareProcessInstanceRequest);

				surrendOfLicense.setStatus(prepareProcessInstanceRequest.getLicenses().get(0).getStatus());

			}

			// CITIZEN MODIFY THE APPLICATION WHEN EMPLOYEE SENDBACK TO CITIZEN
			else if ((surrendOfLicense.getStatus().equalsIgnoreCase(SENDBACK_STATUS)) && usercheck(requestInfo)) {

				String currentStatus = searchSurrendOfLicense.get(0).getStatus();

				surrendOfLicense.setAssignee(Arrays
						.asList(servicePlanService.assignee("CAO", surrendOfLicense.getTenantId(), true, requestInfo)));

				surrendOfLicense.setAction(CITIZEN_UPDATE_ACTION);

				BusinessService workflow = workflowService.getBusinessService(surrendOfLicense.getTenantId(),
						surrendOfLicenseRequest.getRequestInfo(), surrendOfLicense.getBusinessService());

				validateUpdateRoleAndActionFromWorkflow(workflow, currentStatus, surrendOfLicenseRequest,
						surrendOfLicense);

				surrendOfLicense.setAuditDetails(auditDetails);

				TradeLicenseRequest prepareProcessInstanceRequest = prepareProcessInstanceRequest(surrendOfLicense,
						requestInfo, surrendOfLicense.getBusinessService());

				wfIntegrator.callWorkFlow(prepareProcessInstanceRequest);

				surrendOfLicense.setStatus(prepareProcessInstanceRequest.getLicenses().get(0).getStatus());

			}
		}

		surrendOfLicenseRequest.setSurrendOfLicense(surrendOfLicenseList);

		producer.push(surrendUpdateTopic, surrendOfLicenseRequest);

		return surrendOfLicenseList;

	}

	private TradeLicenseRequest prepareProcessInstanceRequest(SurrendOfLicense surrendOfLicense,
			RequestInfo requestInfo, String bussinessServicename) {

		TradeLicenseRequest tradeLicenseASRequest = new TradeLicenseRequest();
		TradeLicense tradeLicenseAS = new TradeLicense();
		List<TradeLicense> tradeLicenseASlist = new ArrayList<>();
		tradeLicenseAS.setBusinessService(bussinessServicename);
		tradeLicenseAS.setAction(surrendOfLicense.getAction());
		tradeLicenseAS.setAssignee(surrendOfLicense.getAssignee());
		tradeLicenseAS.setApplicationNumber(surrendOfLicense.getApplicationNumber());
		tradeLicenseAS.setWorkflowCode(bussinessServicename);
		TradeLicenseDetail tradeLicenseDetail = new TradeLicenseDetail();
		tradeLicenseDetail.setTradeType(bussinessServicename);
		tradeLicenseAS.setTradeLicenseDetail(tradeLicenseDetail);
		tradeLicenseAS.setComment("Workflow for surrender");
		tradeLicenseAS.setWfDocuments(null);
		tradeLicenseAS.setTenantId(surrendOfLicense.getTenantId());
		// tradeLicenseAS.setBusinessService("SURRENDER_OF_LICENSE");

		tradeLicenseASRequest.setRequestInfo(requestInfo);
		tradeLicenseASlist.add(tradeLicenseAS);
		tradeLicenseASRequest.setLicenses(tradeLicenseASlist);

		return tradeLicenseASRequest;
	}

	public List<SurrendOfLicense> search(RequestInfo info, String licenseNo, String applicationNumber) {
		List<Object> preparedStatement = new ArrayList<>();

		Map<String, String> paramMap = new HashedMap();
		Map<String, List<String>> paramMapList = new HashedMap();
		StringBuilder builder;

		String query = "SELECT id, license_no, select_type, area_falling_under, third_party_rights, arera_registration, zoning_layout_planfileurl, license_copyfileurl, edca_vailedfileurl, detailed_relocationschemefileurl, gift_deedfileurl, mutationfileurl, jamabandhifileurl, third_partyrights_declarationfileurl, areain_acres, application_number, additionaldetails, created_by, \"lastModified_by\", created_time, \"lastModified_time\", workflowcode, status, businessservice, tenant_id, declarationi_dwworksfileurl, revised_layout_planfileurl, availed_edc_file_url, area_falling_underfileurl, area_falling_dividing, tcpapplicationnumber, tcpcasenumber, tcpdairynumber,newadditionaldetails\r\n"
				+ "	FROM public.eg_surrend_of_license " + " Where ";
		builder = new StringBuilder(query);

		List<SurrendOfLicense> Result = null;

		if (licenseNo != null) {
			builder.append(" license_no= :LN");
			paramMap.put("LN", licenseNo);
			preparedStatement.add(licenseNo);
			Result = namedParameterJdbcTemplate.query(builder.toString(), paramMap, surrendOfLicenseRowMapper);
		} else if (applicationNumber != null) {
			List<String> applicationNumberList = Arrays.asList(applicationNumber.split(","));
			log.info("applicationNumberList" + applicationNumberList);
			if (applicationNumberList != null) {
				builder.append(" application_number in ( :AN )");
				paramMapList.put("AN", applicationNumberList);
				preparedStatement.add(applicationNumberList);
				Result = namedParameterJdbcTemplate.query(builder.toString(), paramMapList, surrendOfLicenseRowMapper);
			}

		} else if ((info.getUserInfo().getUuid() != null)) {
			builder.append(" createdBy= :CB");
			paramMap.put("CB", info.getUserInfo().getUuid());
			preparedStatement.add(info.getUserInfo().getUuid());
			Result = namedParameterJdbcTemplate.query(builder.toString(), paramMap, surrendOfLicenseRowMapper);

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
			SurrendOfLicenseContract surrendOfLicenseRequest, SurrendOfLicense surrendOfLicense) {
		// validate Action-
		Optional<State> currentWorkflowStateOptional = workflow.getStates().stream()
				.filter(state -> state.getState().equals(currentStatus)).findFirst();
		if (!currentWorkflowStateOptional.isPresent()) {
			throw new CustomException("workflow State not found:" + currentStatus,
					"workflow State not found:" + currentStatus);
		}
		State currentWorkflowState = currentWorkflowStateOptional.get();
		List<Action> permissibleActions = currentWorkflowState.getActions();
		String currentActionFromRequest = surrendOfLicense.getAction();
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
		List<Role> rolesFromUserInfo = surrendOfLicenseRequest.getRequestInfo().getUserInfo().getRoles();
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
		surrendOfLicense.setStatus(nextStateName);
	}

//	@Autowired
//	SurrendOfLicenseRepo surrendOfLicenseRepo;
//
//	@SuppressWarnings("null")
//	public SurrendOfLicense create(SurrendOfLicenseRequest surrendOfLicenseRequest) {
//		return surrendOfLicenseRepo.save(surrendOfLicenseRequest.getSurrendOfLicense());
//
//	}
//
//	public SurrendOfLicense search(Integer id) {
//		return surrendOfLicenseRepo.findById(id).get();

//	} 
	public List<SurrendOfLicense> makePayment(SurrendOfLicenseRequest surrendOfLicenseRequest)
			throws JsonProcessingException {

		RequestInfo requestInfo = surrendOfLicenseRequest.getRequestInfo();
		SurrendOfLicense surrendOfLicense = surrendOfLicenseRequest.getSurrendOfLicense();
		surrendOfLicense.setAssignee(Arrays
				.asList(servicePlanService.assignee("CTP_HR", surrendOfLicense.getTenantId(), true, requestInfo)));
////		approvalStandardRequest.setAssignee(Arrays.asList("f9b7acaf-c1fb-4df2-ac10-83b55238a724"));
		surrendOfLicense.setBusinessService(BUSINESS_SURRENDER);
		surrendOfLicense.setWorkflowCode(BUSINESS_SURRENDER);
		surrendOfLicense.setAction("INITIATE");
		TradeLicenseRequest prepareProcessInstanceRequest = prepareProcessInstanceRequest(
				surrendOfLicenseRequest.getSurrendOfLicense(), requestInfo, surrendOfLicense.getBusinessService());

		wfIntegrator.callWorkFlow(prepareProcessInstanceRequest);
		surrendOfLicense.setStatus(prepareProcessInstanceRequest.getLicenses().get(0).getStatus());

		TradeLicenseSearchCriteria tradeLicenseSearchCriteria = new TradeLicenseSearchCriteria();
		List<String> licenseNumberList = new ArrayList<>();
		licenseNumberList.add(surrendOfLicense.getLicenseNo());
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

		// SurrendOfLicense surrendOfLicense = new SurrendOfLicense();

		surrendOfLicense.setTcpApplicationNumber(application);
		surrendOfLicense.setTcpCaseNumber(caseNumber);
		surrendOfLicense.setTcpDairyNumber(dairyNumber);
		List<SurrendOfLicense> surrendOfLicenseList = new ArrayList<>();
		surrendOfLicenseList.add(surrendOfLicense);
		SurrendOfLicenseContract surrendOfLicenseContract = new SurrendOfLicenseContract();
		surrendOfLicenseContract.setRequestInfo(requestInfo);
		surrendOfLicenseContract.setSurrendOfLicense(surrendOfLicenseList);
		List<SurrendOfLicense> update = update(surrendOfLicenseContract);
		return update;

	}

}
