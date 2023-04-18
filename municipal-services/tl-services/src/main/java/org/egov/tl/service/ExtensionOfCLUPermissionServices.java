package org.egov.tl.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.apache.commons.collections.map.HashedMap;
import org.egov.common.contract.request.RequestInfo;
import org.egov.tl.config.TLConfiguration;
import org.egov.tl.producer.Producer;
import org.egov.tl.repository.rowmapper.ExtensionOfCLUPermissionRowMapper;
import org.egov.tl.util.TradeUtil;
import org.egov.tl.web.models.AuditDetails;
import org.egov.tl.web.models.ExtensionOfCLUPermission;
import org.egov.tl.web.models.ExtensionOfCLUPermissionRequest;
import org.egov.tl.web.models.TradeLicense;
import org.egov.tl.web.models.TradeLicenseDetail;
import org.egov.tl.web.models.TradeLicenseRequest;
import org.egov.tl.workflow.WorkflowIntegrator;
import org.egov.tl.workflow.WorkflowService;
import org.egov.tracer.model.CustomException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class ExtensionOfCLUPermissionServices {
	
//	private static final String BUSINESS_SURRENDER = "SURREND_OF_LICENSE";
//
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
//			extensionOfCLUPermission.setBusinessService(BUSINESS_SURRENDER);
//			extensionOfCLUPermission.setWorkflowCode(BUSINESS_SURRENDER);

			extensionOfCLUPermission.setApplicationNo(applicationNo.get(0));
//			TradeLicenseRequest prepareProcessInstanceRequest = prepareProcessInstanceRequest(
//					extensionOfCLUPermissionRequest.getExtensionOfCLUPermission().get(0), requestInfo, extensionOfCLUPermission.getBusinessService());
//
//			wfIntegrator.callWorkFlow(prepareProcessInstanceRequest);

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



		producer.push(cluUpdateTopic, extensionOfCLUPermissionRequest);

		return extensionOfCLUPermissionList;

	}
	
	/*private TradeLicenseRequest prepareProcessInstanceRequest(ExtensionOfCLUPermission extensionOfCLUPermission,
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
	}*/
	
	public List<ExtensionOfCLUPermission> search(RequestInfo info, String applicattionNo, String licenseNo) {
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
			builder.append(" createdBy= :CB");
			paramMap.put("CB", info.getUserInfo().getUuid());
			preparedStatement.add(info.getUserInfo().getUuid());
			Result = namedParameterJdbcTemplate.query(builder.toString(), paramMap, extensionOfCLUPermissionRowMapper);

		}
		return Result;

	}

}
