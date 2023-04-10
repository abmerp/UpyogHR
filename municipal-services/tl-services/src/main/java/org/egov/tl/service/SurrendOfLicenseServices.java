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
import org.egov.tl.web.models.TradeLicenseRequest;
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

			surrendOfLicense.setId(UUID.randomUUID().toString());
			surrendOfLicense.setAssignee(Arrays
					.asList(servicePlanService.assignee("CTP_HR", surrendOfLicense.getTenantId(), true, requestInfo)));
////		approvalStandardRequest.setAssignee(Arrays.asList("f9b7acaf-c1fb-4df2-ac10-83b55238a724"));
			applicationNumbers = servicePlanService.getIdList(requestInfo, surrendOfLicense.getTenantId(),
					config.getSurrenderName(), config.getSurrenderFormat(), count);

			surrendOfLicense.setAuditDetails(auditDetails);
			surrendOfLicense.setApplicationNumber(applicationNumbers.get(0));

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

	public List<SurrendOfLicense> search(RequestInfo info, String applicattionNumber, String licenseNo) {
		List<Object> preparedStatement = new ArrayList<>();

		Map<String, String> paramMap = new HashedMap();
		Map<String, List<String>> paramMapList = new HashedMap();
		StringBuilder builder;

		String query = "SELECT id, \"licenseNo\", \"selectType\", \"areaFallingUnder\", \"thirdPartyRights\", \"areraRegistration\", \"zoningLayoutPlanfileUrl\", \"licenseCopyfileUrl\", \"edcaVailedfileUrl\", \"detailedRelocationSchemefileUrl\", \"giftDeedfileUrl\", \"mutationfileUrl\", \"jamabandhifileUrl\", \"thirdPartyRightsDeclarationfileUrl\", \"areaInAcres\", \"applicationNumber\", \"additionaldetails\", \"createdBy\", \"lastModifiedBy\", \"createdTime\", \"lastModifiedTime\"\r\n"
				+ "	FROM public.eg_surrend_of_license " + " Where ";
		builder = new StringBuilder(query);

		List<SurrendOfLicense> Result = null;

		if (licenseNo != null) {
			builder.append("\"licenseNo\" = :LN");
			paramMap.put("LN", licenseNo);
			preparedStatement.add(licenseNo);
			Result = namedParameterJdbcTemplate.query(builder.toString(), paramMap, surrendOfLicenseRowMapper);
		} else if (applicattionNumber != null) {
			List<String> applicationNumberList = Arrays.asList(applicattionNumber.split(","));
			log.info("applicationNumberList" + applicationNumberList);
			if (applicationNumberList != null) {
				builder.append("\"applicationNumber\" in ( :AN )");
				paramMapList.put("AN", applicationNumberList);
				preparedStatement.add(applicationNumberList);
				Result = namedParameterJdbcTemplate.query(builder.toString(), paramMapList, surrendOfLicenseRowMapper);
			}

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
