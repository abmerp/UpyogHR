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

import org.egov.tl.repository.rowmapper.TransferRowMapper;
import org.egov.tl.service.dao.TransferDao;

import org.egov.tl.util.TradeUtil;
import org.egov.tl.web.models.AuditDetails;

import org.egov.tl.web.models.TradeLicense;
import org.egov.tl.web.models.TradeLicenseDetail;
import org.egov.tl.web.models.TradeLicenseRequest;
import org.egov.tl.web.models.TradeLicenseSearchCriteria;
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
import net.minidev.json.JSONObject;

@Slf4j
@Service
public class TransferOfLicenseServices {

	@Autowired
	ObjectMapper mapper;
	@Value("${persister.create.transfer.licence.topic}")
	private String transferTopic;

	@Value("${persister.update.transfer.licence.topic}")
	private String tranferUpdateTopic;
	private static final String businessService_TRANSFER = "TRANSFER_OF_LICIENCE";

	private static final String SENDBACK_STATUS = "SEND_BACK_TO_APPLICANT";

	private static final String CITIZEN_UPDATE_ACTION = "FORWARD";
	@Autowired
	private Producer producer;

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
	@Autowired
	GenerateTcpNumbers generateTcpNumbers;

	public List<Transfer> create(TransferOfLicenseRequest transferOfLicenseRequest) throws JsonProcessingException {

		String licenseNumber = transferOfLicenseRequest.getTransfer().getLicenseNo();

		String uuid = transferOfLicenseRequest.getRequestInfo().getUserInfo().getUuid();

		AuditDetails auditDetails = tradeUtil.getAuditDetails(uuid, true);

		RequestInfo requestInfo = transferOfLicenseRequest.getRequestInfo();

		Transfer transferRequest = transferOfLicenseRequest.getTransfer();
		List<String> applicationNumbers = null;
		int count = 1;
		List<Transfer> transferSearch = search(requestInfo, transferRequest.getLicenseNo(),
				transferRequest.getApplicationNumber());
		if (!CollectionUtils.isEmpty(transferSearch) || transferSearch.size() > 1) {

			throw new CustomException("Already Found  or multiple transfer of licence applications with LicenceNumber.",
					"Already Found or multiple transfer of licence applications with LicenceNumber.");
		}
		transferRequest.setId(UUID.randomUUID().toString());
		applicationNumbers = servicePlanService.getIdList(transferOfLicenseRequest.getRequestInfo(),
				transferRequest.getTenantId(), config.getTransferName(), config.getTransferFormat(), count);
		transferRequest.setAuditDetails(auditDetails);
		transferRequest.setApplicationNumber(applicationNumbers.get(0));

		TransferDao transferDao = new TransferDao();
		List<Transfer> transferData = makePayment(transferOfLicenseRequest);
		TransferOfLicence transferOfLicence = transferRequest.getTransferOfLicence();
		String data = mapper.writeValueAsString(transferOfLicence);
		JsonNode jsonNode = mapper.readTree(data);
		transferDao.setAdditionalDetails(jsonNode);
		transferDao.setId(transferRequest.getId());
		transferDao.setAuditDetails(auditDetails);
		transferDao.setLicenseNo(licenseNumber);
		transferDao.setApplicationNumber(transferRequest.getApplicationNumber());
		transferDao.setNewAdditionalDetails(transferRequest.getNewAdditionalDetails());
		transferDao.setTenantId(transferRequest.getTenantId());
		transferDao.setStatus(transferData.get(0).getStatus());
		transferDao.setAction(transferData.get(0).getAction());
		transferDao.setApplicationNumber(applicationNumbers.get(0));
		transferDao.setAssignee(transferData.get(0).getAssignee());
		transferDao.setBusinessService(transferData.get(0).getBusinessService());
		transferDao.setTcpApplicationNumber(transferData.get(0).getTcpApplicationNumber());
		transferDao.setTcpDairyNumber(transferData.get(0).getTcpDairyNumber());
		transferDao.setTcpCaseNumber(transferData.get(0).getTcpCaseNumber());
		// producer.push(transferTopic, new TransferOfLicenseRequestDao(requestInfo,
		// transferDao));

		producer.push(transferTopic, transferDao);
//		List<Transfer> transferData = new ArrayList<>();
//		transferData.add(transferRequest);
		// transferRequest.setAdditionalDetails(transferDao.getAdditionalDetails());

		return transferData;

	}

	public List<Transfer> search(RequestInfo requestInfo, String licenseNo, String applicationNumber) {
		List<Object> preparedStatement = new ArrayList<>();

		Map<String, String> paramMap = new HashedMap();
		Map<String, List<String>> paramMapList = new HashedMap();
		StringBuilder builder;

		String query = "SELECT id, license_no, application_number, tenant_id, action, businessservice, additiona_details, created_by, last_modify_by, created_time, last_modified_time, status, tcpapplicationnumber, tcpcasenumber, tcpdairynumber,newadditionaldetails\r\n"
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

	public List<Transfer> Update(TransferOfLicenseRequest transferOfLicenseRequest) throws JsonProcessingException {

		String uuid = transferOfLicenseRequest.getRequestInfo().getUserInfo().getUuid();

		AuditDetails auditDetails = tradeUtil.getAuditDetails(uuid, false);

		RequestInfo requestInfo = transferOfLicenseRequest.getRequestInfo();

		Transfer transfer = transferOfLicenseRequest.getTransfer();

		// for (Transfer transfer : transferList) {
		// Transfer transfer = transferOfLicenseRequest.getTransfer();
		if (Objects.isNull(transferOfLicenseRequest) || Objects.isNull(transferOfLicenseRequest.getTransfer())) {
			throw new CustomException("transfer of licence must not be null", "transfer of licence must not be null");
		}
		if(StringUtils.isEmpty(transfer.getId()) &&StringUtils.isEmpty(transfer.getApplicationNumber())){
			throw new CustomException("ApplicationNumber or Id must not be null", "ApplicationNumber or Id must not be null");
		}

		List<Transfer> transferSearch = search(requestInfo, transfer.getLicenseNo(), transfer.getApplicationNumber());
		if (CollectionUtils.isEmpty(transferSearch) || transferSearch.size() > 1) {
			throw new CustomException("Found none or multiple transfer of licence applications with applicationNumber.",
					"Found none or multiple transfer of licence applications with applicationNumber.");
		}

		transfer.setBusinessService(transfer.getBusinessService());
		transfer.setWorkflowCode(transfer.getBusinessService());
		if (transfer.getAdditionalDetails() == null || transfer.getAdditionalDetails().isEmpty()) {
			String data = mapper.writeValueAsString(transfer.getTransferOfLicence());
			JsonNode jsonNode = mapper.readTree(data);
			transfer.setAdditionalDetails(jsonNode);
		} else {
			transfer.setAdditionalDetails(transfer.getAdditionalDetails());
		}
		// EMPLOYEE RUN THE APPLICATION NORMALLY
		if (!transfer.getStatus().equalsIgnoreCase(SENDBACK_STATUS) && !usercheck(requestInfo)) {

			// transfer.setAdditionalDetails(transferOfLicenseRequest.getTransfer().getTransferOfLicence());
			String currentStatus = transferSearch.get(0).getStatus();

			BusinessService workflow = workflowService.getBusinessService(transfer.getTenantId(),
					transferOfLicenseRequest.getRequestInfo(), transfer.getBusinessService());

			validateUpdateRoleAndActionFromWorkflow(workflow, currentStatus, transferOfLicenseRequest, transfer);

			transfer.setAuditDetails(auditDetails);

			TradeLicenseRequest prepareProcessInstanceRequest = prepareProcessInstanceRequest(transfer, requestInfo,
					transfer.getBusinessService());

			wfIntegrator.callWorkFlow(prepareProcessInstanceRequest);

			transfer.setStatus(prepareProcessInstanceRequest.getLicenses().get(0).getStatus());

		}

		// CITIZEN MODIFY THE APPLICATION WHEN EMPLOYEE SENDBACK TO CITIZEN
		else if ((transfer.getStatus().equalsIgnoreCase(SENDBACK_STATUS)) && usercheck(requestInfo)) {
			transfer.setAdditionalDetails(transferOfLicenseRequest.getTransfer().getAdditionalDetails());
			String currentStatus = transferSearch.get(0).getStatus();

			transfer.setAssignee(
					Arrays.asList(servicePlanService.assignee("CAO", transfer.getTenantId(), true, requestInfo)));

			transfer.setAction(CITIZEN_UPDATE_ACTION);

			BusinessService workflow = workflowService.getBusinessService(transfer.getTenantId(),
					transferOfLicenseRequest.getRequestInfo(), transfer.getBusinessService());

			validateUpdateRoleAndActionFromWorkflow(workflow, currentStatus, transferOfLicenseRequest, transfer);

			transfer.setAuditDetails(auditDetails);

			TradeLicenseRequest prepareProcessInstanceRequest = prepareProcessInstanceRequest(transfer, requestInfo,
					transfer.getBusinessService());

			wfIntegrator.callWorkFlow(prepareProcessInstanceRequest);

			transfer.setStatus(prepareProcessInstanceRequest.getLicenses().get(0).getStatus());
			// transferList.add(transfer);
		}
		// }

		transferOfLicenseRequest.setTransfer(transfer);

		producer.push(tranferUpdateTopic, transferOfLicenseRequest);
		List<Transfer> transferList = new ArrayList<>();
		transferList.add(transfer);
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

	public List<Transfer> makePayment(TransferOfLicenseRequest transferOfLicenseRequest)
			throws JsonProcessingException {
		Transfer transferRequest = transferOfLicenseRequest.getTransfer();
		RequestInfo requestInfo = transferOfLicenseRequest.getRequestInfo();
		transferRequest.setId(UUID.randomUUID().toString());
		transferRequest.setAssignee(
				Arrays.asList(servicePlanService.assignee("CTP_HR", transferRequest.getTenantId(), true, requestInfo)));
//			approvalStandardRequest.setAssignee(Arrays.asList("f9b7acaf-c1fb-4df2-ac10-83b55238a724"));

		transferRequest.setBusinessService(businessService_TRANSFER);
		transferRequest.setWorkflowCode(businessService_TRANSFER);
		// transferRequest.setAdditionalDetails(null);

		TradeLicenseRequest prepareProcessInstanceRequest = prepareProcessInstanceRequest(transferRequest, requestInfo,
				businessService_TRANSFER);

		wfIntegrator.callWorkFlow(prepareProcessInstanceRequest);

		transferRequest.setStatus(prepareProcessInstanceRequest.getLicenses().get(0).getStatus());

		TradeLicenseSearchCriteria tradeLicenseSearchCriteria = new TradeLicenseSearchCriteria();
		List<String> licenseNumberList = new ArrayList<>();
		licenseNumberList.add(transferOfLicenseRequest.getTransfer().getLicenseNo());
		tradeLicenseSearchCriteria.setLicenseNumbers(licenseNumberList);

		Map<String, Object> tcpNumbers = generateTcpNumbers.tcpNumbers(tradeLicenseSearchCriteria,
				transferOfLicenseRequest.getRequestInfo());
		log.info("tcpnumbers:\t" + tcpNumbers);
		String data = null;

		data = mapper.writeValueAsString(tcpNumbers);
//			
		JSONObject json = new JSONObject(tcpNumbers);

		json.toString();
		String application = json.getAsString("TCPApplicationNumber");
		String caseNumber = json.getAsString("TCPCaseNumber");
		String dairyNumber = json.getAsString("TCPDairyNumber");

		List<Transfer> transfer = new ArrayList<>();

		transferRequest.setTcpApplicationNumber(application);
		transferRequest.setTcpCaseNumber(caseNumber);
		transferRequest.setTcpDairyNumber(dairyNumber);
		// transferRequest.setAdditionalDetails(transferRequest.getAdditionalDetails());
		// transfer.add(transferRequest);
//		TransferOfLicenseRequest transferOfLicenseContract = new TransferOfLicenseRequest();
//		transferOfLicenseContract.setTransfer(transferRequest);
//		transferOfLicenseContract.setRequestInfo(requestInfo);
//		List<Transfer> Update = Update(transferOfLicenseContract);
		List<Transfer> transferList = new ArrayList<>();
		transferList.add(transferRequest);
		return transferList;

	}

}
