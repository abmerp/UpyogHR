package org.egov.tl.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.commons.collections.map.HashedMap;
import org.egov.common.contract.request.RequestInfo;
import org.egov.tl.abm.newservices.contract.ApprovalStandardContract;
import org.egov.tl.abm.newservices.entity.ApprovalStandardEntity;
import org.egov.tl.config.TLConfiguration;
import org.egov.tl.producer.Producer;
import org.egov.tl.repository.IdGenRepository;
import org.egov.tl.repository.ServiceRequestRepository;
import org.egov.tl.repository.rowmapper.ApprovalStandardRowMapper;
import org.egov.tl.util.TradeUtil;
import org.egov.tl.web.models.AuditDetails;
import org.egov.tl.web.models.ServicePlanRequest;
import org.egov.tl.web.models.TradeLicense;
import org.egov.tl.web.models.TradeLicenseDetail;
import org.egov.tl.web.models.TradeLicenseRequest;
import org.egov.tl.workflow.WorkflowIntegrator;
import org.egov.tl.workflow.WorkflowService;
import org.egov.tracer.model.CustomException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.val;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class ApprovalStandardService {
	@Value("${persister.create.approval.standard.topic}")
	private String approvaltopic;

	private static final String businessService_AS = "APPROVAL_OF_STANDARD";

	private static final String SENDBACK_STATUS = "AS_SENDBACK_TO_APPLICANT";

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

	public List<ApprovalStandardEntity> createNewServic(ApprovalStandardContract approvalStandardContract) {

		String licenseNumber = approvalStandardContract.getApprovalStandardRequest().get(0).getLicenseNo();

		String uuid = approvalStandardContract.getRequestInfo().getUserInfo().getUuid();

		AuditDetails auditDetails = tradeUtil.getAuditDetails(uuid, true);

		RequestInfo requestInfo = approvalStandardContract.getRequestInfo();
		List<ApprovalStandardEntity> approvalList = approvalStandardContract.getApprovalStandardRequest();
		for (ApprovalStandardEntity approvalStandardRequest : approvalList) {

			List<String> applicationNumbers = null;
			int count = 1;
			List<ApprovalStandardEntity> searchApprovalPlan = searchApprovalStandard(requestInfo,
					approvalStandardRequest.getLicenseNo(), approvalStandardRequest.getApplicationNumber(),
					approvalStandardRequest.getTenantId());
			if (!CollectionUtils.isEmpty(searchApprovalPlan) || searchApprovalPlan.size() > 1) {
				throw new CustomException("Already Found  or multiple Service plan applications with LoiNumber.",
						"Already Found or multiple Service plan applications with LoiNumber.");
			}

			approvalStandardRequest.setId(UUID.randomUUID().toString());
			approvalStandardRequest.setAssignee(Arrays.asList(
					servicePlanService.assignee("CTP_HR", approvalStandardRequest.getTenantId(), true, requestInfo)));
//			approvalStandardRequest.setAssignee(Arrays.asList("f9b7acaf-c1fb-4df2-ac10-83b55238a724"));
			applicationNumbers = servicePlanService.getIdList(approvalStandardContract.getRequestInfo(),
					approvalStandardRequest.getTenantId(), config.getApprovalStandardApplicationName(),
					config.getApprovalStandardformat(), count);

			approvalStandardRequest.setBusinessService(businessService_AS);
			approvalStandardRequest.setWorkflowCode(businessService_AS);

			approvalStandardRequest.setAuditDetails(auditDetails);
			approvalStandardRequest.setApplicationNumber(applicationNumbers.get(0));

			TradeLicenseRequest prepareProcessInstanceRequest = prepareProcessInstanceRequest(approvalStandardRequest,
					requestInfo, businessService_AS);

			wfIntegrator.callWorkFlow(prepareProcessInstanceRequest);

			approvalStandardRequest.setStatus(prepareProcessInstanceRequest.getLicenses().get(0).getStatus());

		}

		approvalStandardContract.setApprovalStandardRequest(approvalList);

		log.info(approvaltopic);

		producer.push(approvaltopic, approvalStandardContract);

		return approvalList;

	}

	public List<ApprovalStandardEntity> searchApprovalStandard(RequestInfo requestInfo, String licenseNo,
			String applicationNumber, String tenantId) {
		List<Object> preparedStatement = new ArrayList<>();

		Map<String, String> paramMap = new HashedMap();
		Map<String, List<String>> paramMapList = new HashedMap();
		StringBuilder builder;

		String query = "SELECT license_no, plan, other_document, amount, created_by, created_time, last_modified_by, last_modified_time, application_number, tenantid, id, action, status, business_service, comment, workflow_code\r\n"
				+ "" + " FROM public.eg_approval_standard" + " Where ";
		builder = new StringBuilder(query);

		List<ApprovalStandardEntity> Result = null;
		if (licenseNo != null) {
			builder.append(" license_no= :LN");
			paramMap.put("LN", licenseNo);
			preparedStatement.add(licenseNo);
			Result = namedParameterJdbcTemplate.query(builder.toString(), paramMap, approvalStandardRowMapper);
		} else if (applicationNumber != null) {
			List<String> applicationNumberList = Arrays.asList(applicationNumber.split(","));
			log.info("applicationNumberList"+applicationNumberList);
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
}
