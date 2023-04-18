package org.egov.tl.service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.egov.common.contract.request.RequestInfo;
import org.egov.tl.abm.newservices.contract.PerformaContract;
import org.egov.tl.producer.Producer;
import org.egov.tl.util.TradeUtil;
import org.egov.tl.web.models.AuditDetails;
import org.egov.tl.web.models.Performa;
import org.egov.tl.web.models.ReviseLayoutPlan;
import org.egov.tl.web.models.RevisedPlan;
import org.egov.tl.web.models.ServicePlanRequest;
import org.egov.tracer.model.CustomException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
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

	@Value("${persister.create.performa.topic}")
	private String performaCreateTopic;
	@Autowired
	ObjectMapper mapper;

	public List<Performa> create(PerformaContract performaContract) {
		String uuid = performaContract.getRequestInfo().getUserInfo().getUuid();

		AuditDetails auditDetails = tradeUtil.getAuditDetails(uuid, true);

		RequestInfo requestInfo = performaContract.getRequestInfo();
		List<Performa> performaList = performaContract.getPerforma();
		for (Performa performa : performaList) {

		//	int count = 1;
//			List<Performa> searchPerforma = searchServicePlan(servicePlanRequest.getLoiNumber(),
//					servicePlanRequest.getApplicationNumber(), requestInfo);
//			if (!CollectionUtils.isEmpty(searchServicePlan) || searchServicePlan.size() > 1) {
//				throw new CustomException("Already Found  or multiple Service plan applications with LoiNumber.",
//						"Already Found or multiple Service plan applications with LoiNumber.");
//			}

			performa.setId(UUID.randomUUID().toString());
			performa.setAuditDetails(auditDetails);
		}
		performaContract.setPerforma(performaList);
		producer.push(performaCreateTopic, performaContract);
		return performaList;

	}
}
