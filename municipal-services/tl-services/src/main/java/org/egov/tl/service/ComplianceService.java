package org.egov.tl.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.apache.commons.collections.map.HashedMap;
import org.egov.common.contract.request.RequestInfo;
import org.egov.tl.abm.newservices.contract.ComplianceContract;
import org.egov.tl.producer.Producer;
import org.egov.tl.repository.rowmapper.ComplianceRowMapper;
import org.egov.tl.util.TradeUtil;
import org.egov.tl.web.models.AdditionalDocuments;
import org.egov.tl.web.models.AuditDetails;
import org.egov.tl.web.models.ComplianceRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
@Service
public class ComplianceService {
	@Autowired
	private TradeUtil tradeUtil;
	@Autowired
	private NamedParameterJdbcTemplate namedParameterJdbcTemplate;
	@Autowired
	private Producer producer;
	@Autowired
	ObjectMapper mapper;
	@Autowired
	private ComplianceRowMapper complianceRowMapper;
	@Value("${persister.create.loi.compliance.topic}")
	private String topic;
	
	@Value("${persister.update.loi.compliance.topic}")
	private String updateTopic;

	public List<ComplianceRequest> create(ComplianceContract complianceContract) throws JsonProcessingException {
		String uuid = complianceContract.getRequestInfo().getUserInfo().getUuid();

		AuditDetails auditDetails = tradeUtil.getAuditDetails(uuid, true);

		RequestInfo requestInfo = complianceContract.getRequestInfo();
		List<ComplianceRequest> complianceRequestList = complianceContract.getComplianceRequest();

		for (ComplianceRequest complianceRequest : complianceRequestList) {
			complianceRequest.setId(UUID.randomUUID().toString());
			complianceRequest.setAuditDetails(auditDetails);
			String data = mapper.writeValueAsString(complianceRequest.getCompliance());
			JsonNode jsonNode = mapper.readTree(data);
			complianceRequest.setAdditionalDetails(jsonNode);
			complianceRequest.setCompliance(null);
		}
		complianceContract.setComplianceRequest(complianceRequestList);

		producer.push(topic, complianceContract);
		return complianceRequestList;
	}
	public List<ComplianceRequest> search(RequestInfo requestInfo,String tcpApplicationNumber){

		List<Object> preparedStatement = new ArrayList<>();

		Map<String, String> paramMap = new HashedMap();
		Map<String, List<String>> paramMapList = new HashedMap();
		StringBuilder builder;

		String query = "SELECT id, tcp_application_number, loi_number, additional_details, created_by, created_time, last_modify_by, last_modified_time, business_service\r\n"
				+ "	FROM public.eg_loi_compliances " + "WHERE  ";

		builder = new StringBuilder(query);

		List<ComplianceRequest> Result = null;
		if (tcpApplicationNumber != null) {
			List<String> applicationNumberList = Arrays.asList(tcpApplicationNumber.split(","));
			if (applicationNumberList != null) {
				builder.append(" tcp_application_number in ( :AN )");
				paramMapList.put("AN", applicationNumberList);
				preparedStatement.add(applicationNumberList);
				Result = namedParameterJdbcTemplate.query(builder.toString(), paramMapList, complianceRowMapper);
			}
		} 
		else if ((requestInfo.getUserInfo().getUuid() != null)) {
			builder.append(" created_by= :CB");
			paramMap.put("CB", requestInfo.getUserInfo().getUuid());
			preparedStatement.add(requestInfo.getUserInfo().getUuid());
			Result = namedParameterJdbcTemplate.query(builder.toString(), paramMap, complianceRowMapper);

		}
	
		return Result;
	}
	public List<ComplianceRequest> update(ComplianceContract complianceContract) throws JsonProcessingException {
		String uuid = complianceContract.getRequestInfo().getUserInfo().getUuid();

		AuditDetails auditDetails = tradeUtil.getAuditDetails(uuid, true);

		RequestInfo requestInfo = complianceContract.getRequestInfo();
		List<ComplianceRequest> complianceRequestList = complianceContract.getComplianceRequest();

		complianceContract.setComplianceRequest(complianceRequestList);

		producer.push(updateTopic, complianceContract);
		return complianceRequestList;
	}

}


