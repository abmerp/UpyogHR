package org.egov.tl.service;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.map.HashedMap;
import org.egov.common.contract.request.RequestInfo;
import org.egov.tl.abm.newservices.contract.PerformaScrutinyContract;
import org.egov.tl.abm.newservices.entity.PerformaScruitny;
import org.egov.tl.producer.Producer;
import org.egov.tl.repository.rowmapper.PerformaScrutinyRowMapper;
import org.egov.tl.repository.rowmapper.TransferRowMapper;
import org.egov.tl.web.models.Transfer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class PerformaScrutinyService {
	@Autowired
	private Producer producer;
	@Autowired
	private NamedParameterJdbcTemplate namedParameterJdbcTemplate;
	@Autowired
	private PerformaScrutinyRowMapper performaScrutinyRowMapper;
	
	@Value("${persister.create.performa.scrutiny.topic}")
	private String performascrutinyTopic;
	public PerformaScruitny create(PerformaScrutinyContract performaScrutinyContract) {
		PerformaScruitny performaScruitny= performaScrutinyContract.getPerformaScruitny();
		producer.push(performascrutinyTopic, performaScrutinyContract);
		return performaScruitny;

	}
	public List<PerformaScruitny> search(RequestInfo requestInfo,String applicationNumber) {
		List<Object> preparedStatement = new ArrayList<>();

		Map<String, String> paramMap = new HashedMap();
		Map<String, List<String>> paramMapList = new HashedMap();
		StringBuilder builder;

		String query = "SELECT applicationnumber, applicationstatus, username, userid, designation, created_on, additionaldetails\r\n"
				+ "	FROM public.eg_performa_scruitny" + " Where ";
		builder = new StringBuilder(query);

		List<PerformaScruitny> Result = null;
		 if (applicationNumber != null) {
			List<String> applicationNumberList = Arrays.asList(applicationNumber.split(","));
			log.info("applicationNumberList" + applicationNumberList);
			if (applicationNumberList != null) {
				builder.append(" applicationnumber in ( :AN )");
				paramMapList.put("AN", applicationNumberList);
				preparedStatement.add(applicationNumberList);
				Result = namedParameterJdbcTemplate.query(builder.toString(), paramMapList, performaScrutinyRowMapper);
			}

		}
		return Result;

	}
	public PerformaScruitny update(PerformaScrutinyContract performaScrutinyContract) {
		PerformaScruitny performaScruitny= performaScrutinyContract.getPerformaScruitny();
		producer.push(performascrutinyTopic, performaScrutinyContract);
		return performaScruitny;

	}
	

}
