package org.egov.tl.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.apache.commons.collections.map.HashedMap;
import org.egov.common.contract.request.RequestInfo;
import org.egov.tl.abm.newservices.contract.PerformaContract;
import org.egov.tl.producer.Producer;
import org.egov.tl.repository.rowmapper.PerformaRowMapper;
import org.egov.tl.util.TradeUtil;
import org.egov.tl.web.models.AuditDetails;
import org.egov.tl.web.models.Performa;

import org.egov.tracer.model.CustomException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.val;

@Service
public class PerformaService {
	@Autowired
	private Producer producer;
	@Autowired
	private TradeUtil tradeUtil;

	@Autowired
	private PerformaRowMapper performaRowMapper;

	@Value("${persister.create.performa.topic}")
	private String performaCreateTopic;
	@Autowired
	ObjectMapper mapper;
	@Autowired
	private NamedParameterJdbcTemplate namedParameterJdbcTemplate;

	public List<Performa> create(PerformaContract performaContract) {
		String uuid = performaContract.getRequestInfo().getUserInfo().getUuid();

		AuditDetails auditDetails = tradeUtil.getAuditDetails(uuid, true);

		RequestInfo requestInfo = performaContract.getRequestInfo();
		List<Performa> performaList = performaContract.getPerforma();
		for (Performa performa : performaList) {

			// int count = 1;
			List<Performa> performaSearch = search(performaContract.getRequestInfo(),performa.getApplicationNumber());

			if (!CollectionUtils.isEmpty(performaSearch) || performaSearch.size() > 1) {
				throw new CustomException("Already found application number or business service..",
						"Already found application number or business service.");
			}

			performa.setId(UUID.randomUUID().toString());
			performa.setAuditDetails(auditDetails);
			
			
		}
		performaContract.setPerforma(performaList);
		producer.push(performaCreateTopic, performaContract);
		return performaList;

	}

	public List<Performa> search(RequestInfo requestInfo, String applicationNumber) {

		List<Object> preparedStatement = new ArrayList<>();

		Map<String, String> paramMap = new HashedMap();
		Map<String, List<String>> paramMapList = new HashedMap();
		StringBuilder builder;

		String query = "SELECT id, application_number, business_service, additional_details, created_by, last_modify_by, created_time, last_modified_time, tenant_id\r\n"
				+ "	FROM public.eg_performa " + "WHERE  ";

		builder = new StringBuilder(query);

		List<Performa> Result = null;
		 if (applicationNumber != null) {
			List<String> applicationNumberList = Arrays.asList(applicationNumber.split(","));
			if (applicationNumberList != null) {
				builder.append(" application_number in ( :AN )");
				paramMapList.put("AN", applicationNumberList);
				preparedStatement.add(applicationNumberList);
				Result = namedParameterJdbcTemplate.query(builder.toString(), paramMapList, performaRowMapper);
			}

		} 

		return Result;
	}

}
