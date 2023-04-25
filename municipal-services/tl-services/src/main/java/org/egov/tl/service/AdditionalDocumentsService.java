package org.egov.tl.service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;

import org.apache.commons.collections.map.HashedMap;
import org.egov.common.contract.request.RequestInfo;
import org.egov.tl.abm.newservices.contract.AdditionalDocumentsContract;
import org.egov.tl.abm.newservices.contract.AdditionalDocumentResponse;
import org.egov.tl.producer.Producer;
import org.egov.tl.repository.rowmapper.AdditionalDocumentsRowMapper;
import org.egov.tl.util.TradeUtil;
import org.egov.tl.web.models.AdditionalDocuments;
import org.egov.tl.web.models.AuditDetails;
import org.egov.tl.web.models.DocumentsDetails;
import org.egov.tl.web.models.ServicePlanContract;
import org.egov.tl.web.models.ServicePlanRequest;
import org.egov.tracer.model.CustomException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class AdditionalDocumentsService {
	@Autowired
	private TradeUtil tradeUtil;
	@Autowired
	private NamedParameterJdbcTemplate namedParameterJdbcTemplate;
	@Autowired
	private Producer producer;

	@Autowired
	private AdditionalDocumentsRowMapper allServiceRowMapper;

	@Value("${persister.create.all-service-topic}")
	private String topic;
	@Autowired
	ObjectMapper mapper;
//	private String APPLICATION_NUMBER = "aplicationNumber";
//	private String LOI_NUMBER = "loiNumber";
//	private String LICENCE_NUMBER = "licenceNumber";

	public List<AdditionalDocuments> create(AdditionalDocumentsContract allServiceFindContract)
			throws JsonProcessingException {

		String uuid = allServiceFindContract.getRequestInfo().getUserInfo().getUuid();

		AuditDetails auditDetails = tradeUtil.getAuditDetails(uuid, true);

		RequestInfo requestInfo = allServiceFindContract.getRequestInfo();
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.ENGLISH);
		LocalDateTime localDateTime = LocalDateTime.now();
		String date = formatter.format(localDateTime);
		List<AdditionalDocuments> allServiceFindList = allServiceFindContract.getAddtionalDocuments();
		for (AdditionalDocuments allServiceFind : allServiceFindList) {
//			String applicationNumber = allServiceFind.getApplicationNumber();
//			String loiNumber = allServiceFind.getLoiNumber();
//			String licenceNumber = allServiceFind.getLicenceNumber();
			List<AdditionalDocuments> allServiceFindsearch = search(requestInfo, allServiceFind.getType());

			if (!CollectionUtils.isEmpty(allServiceFindsearch) || allServiceFindsearch.size() > 1) {
				throw new CustomException("Already Found multiple service numbers",
						"Already Found multiple service numbers");
			}
			List<DocumentsDetails> documentsDetails = allServiceFind.getDocumentsDetails();
			List<DocumentsDetails> documentDetails = new ArrayList<>();
			for(DocumentsDetails documentsDetail:documentsDetails) {
	
				documentsDetail.setDate(date);
				documentDetails.add(documentsDetail);
		//	documentsDetails.add(documentsDetail);
			}
			allServiceFind.setId(UUID.randomUUID().toString());
			allServiceFind.setAuditDetails(auditDetails);
			String data = mapper.writeValueAsString(documentDetails);
			JsonNode jsonNode = mapper.readTree(data);
			allServiceFind.setAdditionalDetails(jsonNode);
			allServiceFind.setDocumentsDetails(null);

		}
		allServiceFindContract.setAddtionalDocuments(allServiceFindList);

		producer.push(topic, allServiceFindContract);

		return allServiceFindList;
	}

	public List<AdditionalDocuments> search(RequestInfo requestInfo, String type) {

		List<Object> preparedStatement = new ArrayList<>();

		Map<String, String> paramMap = new HashedMap();
		Map<String, List<String>> paramMapList = new HashedMap();
		StringBuilder builder;

		String query = "SELECT id, application_number, loi_number, licence_number, additional_details, created_by, created_time, last_modify_by, last_modified_time, business_service, type\r\n"
				+ "	FROM public.eg_additional_documents " + "WHERE  ";

		builder = new StringBuilder(query);

		List<AdditionalDocuments> Result = null;
		if (type != null) {
			builder.append(" type= :LN");
			paramMap.put("LN", type);
			preparedStatement.add(type);
			Result = namedParameterJdbcTemplate.query(builder.toString(), paramMap, allServiceRowMapper);
		}
//		} else if (applicationNumber != null) {
//			List<String> applicationNumberList = Arrays.asList(applicationNumber.split(","));
//			if (applicationNumberList != null) {
//				builder.append(" application_number in ( :AN )");
//				paramMapList.put("AN", applicationNumberList);
//				preparedStatement.add(applicationNumberList);
//				Result = namedParameterJdbcTemplate.query(builder.toString(), paramMapList, allServiceRowMapper);
//			}
//		} else if (licenceNumber != null) {
//
//			builder.append(" licence_number in ( :LIN )");
//			paramMap.put("LIN", licenceNumber);
//			preparedStatement.add(licenceNumber);
//			Result = namedParameterJdbcTemplate.query(builder.toString(), paramMap, allServiceRowMapper);
//		}

		else if ((requestInfo.getUserInfo().getUuid() != null)) {
			builder.append(" created_by= :CB");
			paramMap.put("CB", requestInfo.getUserInfo().getUuid());
			preparedStatement.add(requestInfo.getUserInfo().getUuid());
			Result = namedParameterJdbcTemplate.query(builder.toString(), paramMap, allServiceRowMapper);

		}

		return Result;
	}
}
