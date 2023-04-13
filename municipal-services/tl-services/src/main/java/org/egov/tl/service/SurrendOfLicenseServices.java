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

	@SuppressWarnings("null")
	public List<SurrendOfLicense> create(SurrendOfLicenseRequest surrendOfLicenseRequest) {

		String uuid = surrendOfLicenseRequest.getRequestInfo().getUserInfo().getUuid();

		AuditDetails auditDetails = tradeUtil.getAuditDetails(uuid, true);

		RequestInfo requestInfo = surrendOfLicenseRequest.getRequestInfo();
		List<SurrendOfLicense> renewalList = surrendOfLicenseRequest.getSurrendOfLicense();

		for (SurrendOfLicense surrendOfLicense : renewalList) {

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
			surrendOfLicense.setAssignee(Arrays
					.asList(servicePlanService.assignee("CTP_HR", surrendOfLicense.getTenantId(), true, requestInfo)));
////		approvalStandardRequest.setAssignee(Arrays.asList("f9b7acaf-c1fb-4df2-ac10-83b55238a724"));
			applicationNumbers = servicePlanService.getIdList(requestInfo, surrendOfLicense.getTenantId(),
					config.getSurrenderName(), config.getSurrenderFormat(), count);
			surrendOfLicense.setAction("INITIATE");
			surrendOfLicense.setAuditDetails(auditDetails);
			surrendOfLicense.setBusinessService(BUSINESS_SURRENDER);
			surrendOfLicense.setWorkflowCode(BUSINESS_SURRENDER);

			surrendOfLicense.setApplicationNumber(applicationNumbers.get(0));
			TradeLicenseRequest prepareProcessInstanceRequest = prepareProcessInstanceRequest(
					surrendOfLicenseRequest.getSurrendOfLicense().get(0), requestInfo, surrendOfLicense.getBusinessService());

			wfIntegrator.callWorkFlow(prepareProcessInstanceRequest);

		}

//		surrendOfLicenseRequest.setSurrendOfLicense(renewalList);

		log.info(surrendTopic);

		producer.push(surrendTopic, surrendOfLicenseRequest);

		return renewalList;
	}

	public List<SurrendOfLicense> update(SurrendOfLicenseRequest surrendOfLicenseRequest) {

		String uuid = surrendOfLicenseRequest.getRequestInfo().getUserInfo().getUuid();

		AuditDetails auditDetails = tradeUtil.getAuditDetails(uuid, false);

		RequestInfo requestInfo = surrendOfLicenseRequest.getRequestInfo();

		List<SurrendOfLicense> surrendOfLicenseList = surrendOfLicenseRequest.getSurrendOfLicense();

//		revisedPlanRequest.setRevisedPlan(revisedPlanList);

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

	public List<SurrendOfLicense> search(RequestInfo info, String applicattionNumber, String licenseNo) {
		List<Object> preparedStatement = new ArrayList<>();

		Map<String, String> paramMap = new HashedMap();
		Map<String, List<String>> paramMapList = new HashedMap();
		StringBuilder builder;

		String query = "SELECT id, license_no, select_type, area_falling_under, third_party_rights, arera_registration, zoning_layout_planfileurl, license_copyfileurl, edca_vailedfileurl, detailed_relocationschemefileurl, gift_deedfileurl, mutationfileurl, jamabandhifileurl, third_partyrights_declarationfileurl, areain_acres, application_number, additionaldetails, created_by, \"lastModified_by\", created_time, \"lastModified_time\", workflowcode, status, businessservice, tenant_id, declarationi_dwworksfileurl, revised_layout_planfileurl, availed_edc_file_url, area_falling_underfileurl, area_falling_dividing\r\n"
				+ "	FROM public.eg_surrend_of_license " + " Where ";
		builder = new StringBuilder(query);

		List<SurrendOfLicense> Result = null;

		if (licenseNo != null) {
			builder.append(" license_no= :LN");
			paramMap.put("LN", licenseNo);
			preparedStatement.add(licenseNo);
			Result = namedParameterJdbcTemplate.query(builder.toString(), paramMap, surrendOfLicenseRowMapper);
		} else if (applicattionNumber != null) {
			List<String> applicationNumberList = Arrays.asList(applicattionNumber.split(","));
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

}
