package org.egov.tl.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.commons.collections.map.HashedMap;
import org.egov.common.contract.request.RequestInfo;
import org.egov.tl.abm.newservices.contract.ApprovalStandardContract;
import org.egov.tl.abm.newservices.entity.ApprovalStandardEntity;
import org.egov.tl.producer.Producer;
import org.egov.tl.repository.rowmapper.ApprovalStandardRowMapper;
import org.egov.tl.util.TradeUtil;
import org.egov.tl.web.models.AuditDetails;
import org.egov.tl.web.models.ServicePlanRequest;
import org.egov.tl.web.models.TradeLicenseRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Service;

import lombok.val;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class ApprovalStandardService {
	@Value("${persister.create.approval.standard.topic}")
	private String approvaltopic;
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

	public List<ApprovalStandardEntity> createNewServic(ApprovalStandardContract approvalStandardContract) {
		List<ApprovalStandardEntity> approvalList = new ArrayList<>();

		ApprovalStandardEntity approvalStandardRequest = new ApprovalStandardEntity();
		String licenseNumber=approvalStandardContract.getApprovalStandardRequest().get(0).getLicenseNo();
		
		String uuid = approvalStandardContract.getRequestInfo().getUserInfo().getUuid();

		AuditDetails auditDetails = tradeUtil.getAuditDetails(uuid, true);

		RequestInfo requestInfo = approvalStandardContract.getRequestInfo();
		approvalStandardRequest
				.setLicenseNo(licenseNumber);
		approvalStandardRequest.setAmount(approvalStandardContract.getApprovalStandardRequest().get(0).getAmount());
		approvalStandardRequest
				.setOtherDocument(approvalStandardContract.getApprovalStandardRequest().get(0).getOtherDocument());
		approvalStandardRequest.setPlan(approvalStandardContract.getApprovalStandardRequest().get(0).getPlan());
		approvalStandardRequest.setAuditDetails(auditDetails);
		approvalStandardContract.setApprovalStandardRequest(Arrays.asList(approvalStandardRequest));
		approvalList.add(approvalStandardRequest);
		log.info(approvaltopic);
		try {
			producer.push(approvaltopic, approvalStandardContract);

		} catch (Exception e) {
			e.printStackTrace();
		}
		return approvalList;

	}

	public List<ApprovalStandardEntity> searchApprovalStandard(RequestInfo requestInfo, String licenseNo) {
		List<Object> preparedStatement = new ArrayList<>();

		Map<String, String> paramMap = new HashedMap();
		Map<String, List<String>> paramMapList = new HashedMap();
		StringBuilder builder;

		String query = "SELECT license_no, plan, other_document, amount" + " FROM public.eg_approval_standard"
				+ " Where ";
		builder = new StringBuilder(query);

		List<ApprovalStandardEntity> Result = null;
		if (licenseNo != null) {
			builder.append(" license_no= :LN");
			paramMap.put("LN", licenseNo);
			preparedStatement.add(licenseNo);
			Result = namedParameterJdbcTemplate.query(builder.toString(), paramMap, approvalStandardRowMapper);
		}

		return Result;
	}

}
