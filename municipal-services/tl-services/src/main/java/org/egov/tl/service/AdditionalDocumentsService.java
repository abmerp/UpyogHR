package org.egov.tl.service;

import java.io.IOException;
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
import org.egov.tl.abm.newservices.contract.AdditionalDocumentResponse;
import org.egov.tl.abm.newservices.contract.AdditionalDocumentsContract;

import org.egov.tl.producer.Producer;
import org.egov.tl.repository.rowmapper.AdditionalDocumentsRowMapper;
import org.egov.tl.service.dao.AdditionalDocumentsDao;
import org.egov.tl.util.TradeUtil;
import org.egov.tl.web.models.AdditionalDocumentReport;
import org.egov.tl.web.models.AdditionalDocuments;
import org.egov.tl.web.models.AuditDetails;
import org.egov.tl.web.models.CalculationRes;
import org.egov.tl.web.models.DocumentsDetails;
import org.egov.tl.web.models.Fileddetail;
import org.egov.tl.web.models.GuranteeCalculatorResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;

import lombok.extern.slf4j.Slf4j;

@Slf4j
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
		AdditionalDocuments allServiceFind = allServiceFindContract.getAddtionalDocuments();
//		for (AdditionalDocuments allServiceFind : allServiceFindList) {
//			String applicationNumber = allServiceFind.getApplicationNumber();
//			String loiNumber = allServiceFind.getLoiNumber();
//			String licenceNumber = allServiceFind.getLicenceNumber();
//			List<AdditionalDocuments> allServiceFindsearch = search(requestInfo, allServiceFind.getType());
//
//			if (!CollectionUtils.isEmpty(allServiceFindsearch) || allServiceFindsearch.size() > 1) {
//				throw new CustomException("Already Found multiple service numbers",
//						"Already Found multiple service numbers");
//			}
		List<DocumentsDetails> documentsDetails = allServiceFind.getDocumentsDetails();
		List<DocumentsDetails> documentDetails = new ArrayList<>();
		for (DocumentsDetails documentsDetail : documentsDetails) {

			documentsDetail.setDate(date);
			documentDetails.add(documentsDetail);
			// documentsDetails.add(documentsDetail);
		}
		allServiceFind.setId(UUID.randomUUID().toString());
		allServiceFind.setAuditDetails(auditDetails);
		AdditionalDocumentsDao additionalDocumentsDao = new AdditionalDocumentsDao();
		String data = mapper.writeValueAsString(documentDetails);
		JsonNode jsonNode = mapper.readTree(data);
		additionalDocumentsDao.setAdditionalDetails(jsonNode);
		additionalDocumentsDao.setAuditDetails(auditDetails);
		additionalDocumentsDao.setBusinessService(allServiceFind.getBusinessService());
		additionalDocumentsDao.setDeveloperName(allServiceFind.getDeveloperName());
		additionalDocumentsDao.setId(allServiceFind.getId());
		additionalDocumentsDao.setLicenceNumber(allServiceFind.getLicenceNumber());
		additionalDocumentsDao.setDeveloperName(allServiceFind.getDeveloperName());
		additionalDocumentsDao.setUserName(allServiceFind.getUserName());
		// additionalDocumentsDao.setType(allServiceFind.getType());
		// additionalDocumentsDao.setApplicationSection(allServiceFind.getApplicationSection());

		allServiceFindContract.setAddtionalDocuments(allServiceFind);

		producer.push(topic, additionalDocumentsDao);
		List<AdditionalDocuments> additionalDocumentsList = new ArrayList<>();
		additionalDocumentsList.add(allServiceFind);
		return additionalDocumentsList;
	}

	public List<AdditionalDocuments> search(RequestInfo requestInfo, String serviceName, String licenceNumber,
			String applicationSection) {

		List<Object> preparedStatement = new ArrayList<>();

		Map<String, String> paramMap = new HashedMap();
		Map<String, List<String>> paramMapList = new HashedMap();
		StringBuilder builder;

		String query = "SELECT id, licence_number, additional_details, created_by, created_time, last_modify_by, last_modified_time, business_service, username, developername, applicationsection\r\n"
				+ "	FROM public.eg_additional_documents " + "WHERE  ";

		builder = new StringBuilder(query);

		List<AdditionalDocuments> Result = null;
//		if (type != null) {
//			builder.append(" type= :TY");
//			paramMap.put("TY", type);
//			preparedStatement.add(type);
//			Result = namedParameterJdbcTemplate.query(builder.toString(), paramMap, allServiceRowMapper);

		if (serviceName != null) {
			builder.append(" business_service= :BS");
			paramMap.put("BS", serviceName);
			preparedStatement.add(serviceName);
			Result = namedParameterJdbcTemplate.query(builder.toString(), paramMap, allServiceRowMapper);

			if (licenceNumber != null) {
				builder.append(" AND licence_number= :LN");
				paramMap.put("LN", licenceNumber);
				preparedStatement.add(licenceNumber);
				Result = namedParameterJdbcTemplate.query(builder.toString(), paramMap, allServiceRowMapper);

//				if (applicationSection != null) {
//					builder.append(" AND applicationsection= :AS");
//					paramMap.put("AS", applicationSection);
//					preparedStatement.add(applicationSection);
//					Result = namedParameterJdbcTemplate.query(builder.toString(), paramMap, allServiceRowMapper);
//				}
			}
			// }
		}

		log.info("Result" + Result);

		return Result;
	}

	public List<AdditionalDocumentReport> searchSectionData(RequestInfo requestInfo, String serviceName,
			String licenceNumber, String applicationSection) throws JsonProcessingException {

		List<AdditionalDocuments> result = search(requestInfo, serviceName, licenceNumber, applicationSection);
		List<AdditionalDocumentReport> securityReport = new ArrayList<AdditionalDocumentReport>();
		AdditionalDocumentReport object = null;
		List<Fileddetail> applicantInfo = null;
		List<Fileddetail> applicantPurpose = null;
		List<Fileddetail> landSchedule = null;
		List<Fileddetail> detailsOfAppliedland = null;
		List<Fileddetail> feesANdCharges = null;
		List<AdditionalDocumentReport> securityReportf = new ArrayList<AdditionalDocumentReport>();

		for (AdditionalDocuments additionalDocuments : result) {
			// securityReport = new ArrayList<EmployeeSecurtinyReport>();
			object = new AdditionalDocumentReport();
			boolean isExisting = false;
			applicantInfo = new ArrayList<Fileddetail>();
			applicantPurpose = new ArrayList<Fileddetail>();
			landSchedule = new ArrayList<Fileddetail>();
			detailsOfAppliedland = new ArrayList<Fileddetail>();
			feesANdCharges = new ArrayList<Fileddetail>();
			object.setId(additionalDocuments.getId());
			object.setDeveloperName(additionalDocuments.getDeveloperName());
			object.setBusinessService(additionalDocuments.getBusinessService());
			object.setLicenceNumber(additionalDocuments.getLicenceNumber());
			object.setUserName(additionalDocuments.getUserName());

			// int i = 0;
			for (AdditionalDocuments additionalDocuments1 : result) {
				Object additionalDetailData = additionalDocuments1.getAdditionalDetails();
				String data = mapper.writeValueAsString(additionalDetailData);
				JsonNode jsonNode = mapper.readTree(data);
				log.info("jsonNode" + jsonNode);
				List<DocumentsDetails> documentsDetailsList = null;
				ObjectReader readerValue = mapper.readerFor(new TypeReference<List<DocumentsDetails>>() {
				});

				documentsDetailsList = readerValue.readValue(data);
				for (DocumentsDetails documentsDetails : documentsDetailsList) {
					String applicationSections = documentsDetails.getApplicationSection();
					log.info("applicationSection" + applicationSections);

					if (applicationSections.equalsIgnoreCase(documentsDetails.getApplicationSection())) {

						Fileddetail comments2 = new Fileddetail();
						comments2.setDate(documentsDetails.getDate());
						comments2.setDocumentName(documentsDetails.getDocumentName());
						comments2.setDocument(documentsDetails.getDocument());
						comments2.setApplicationSection(documentsDetails.getApplicationSection());
						if (documentsDetails.getApplicationSection().equalsIgnoreCase("ApplicantInfo"))
							applicantInfo.add(comments2);
						else if (documentsDetails.getApplicationSection().equalsIgnoreCase("ApplicantPurpose"))
							applicantPurpose.add(comments2);
						else if (documentsDetails.getApplicationSection().equalsIgnoreCase("DetailsOfAppliedLand"))
							detailsOfAppliedland.add(comments2);
						else if (documentsDetails.getApplicationSection().equalsIgnoreCase("LandSchedule"))
							landSchedule.add(comments2);
						else if (documentsDetails.getApplicationSection().equalsIgnoreCase("FeesAndCharges"))
							feesANdCharges.add(comments2);
						// documentsDetails.remove(i);
					}

				}
				// i++;

			}

//			if (securityReport.size() > 0)
//				for (AdditionalDocumentReport objecttmp : securityReport) {
//					if ((objecttmp.getApplicationStatus().equalsIgnoreCase(object.getApplicationStatus())
//							&& objecttmp.getUserID().equalsIgnoreCase(object.getUserID())
//							&& objecttmp.getEmployeeName().equalsIgnoreCase(object.getEmployeeName()))) {
//						isExisting = true;
//						break;
//					}
//				}
//
			// if (!isExisting) {
			object.setApplicantInfo(applicantInfo);
			object.setApplicantPurpose(applicantPurpose);
			object.setDetailsOfAppliedland(detailsOfAppliedland);
			object.setLandSchedule(landSchedule);
			object.setFeesANdCharges(feesANdCharges);

			securityReport.add(object);
			// securityReportf.addAll(securityReport);
			log.info("\t" + securityReport.size() + "\t");

			// }
//		}
		}
		return securityReport;
	}
}
