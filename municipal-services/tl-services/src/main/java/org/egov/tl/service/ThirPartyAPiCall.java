package org.egov.tl.service;

import java.io.IOException;
import java.util.Collections;
import java.util.Map;

import org.egov.tl.web.models.ResponseTransaction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectReader;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class ThirPartyAPiCall {

	@Value("${tcp.dept.auth.token}")
	private String deptAuthToken;
	@Value("${tcp.dispatch.number}")
	private String deptDispatchNumber;

	@Value("${tcp.url}")
	public String tcpurl;
	@Value("${tcp.auth.token}")
	public String tcpAuthToken;
	@Value("${tcp.access.key}")
	public String tcpAccessKey;
	@Value("${tcp.secret.key}")
	public String tcpSecretKey;
	@Value("${tcp.genrate.transactionnumber}")
	public String tcpgenratetransactionnumber;
	@Value("${tcp.save.transactiondata}")
	public String tcpSaveTransactionData;
	@Value("${tcp.generate.dairynumber}")
	public String tcpGenerateDairyNumber;
	@Value("${tcp.generate.casenumber}")
	public String tcpGenerateCaseNumber;
	@Value("${tcp.generate.applicationnumber}")
	public String tcpGenerateApplicationNumber;
	@Value("${tcp.is.existSSO.Token}")
	public String tcpExistSSoNumber;

	@Value("${tcp.casetypeid}")
	public String caseTypeId;
	@Value("${tcp.apptypeid}")
	public String appTypeId;
	@Value("${tcp.chargestypeid}")
	public String chargesTypeId;
	@Value("${tcp.colonyname}")
	public String colonyName;
	@Value("${tcp.applicationdocid}")
	public String applicationDocId;
	@Value("${tcp.applicationid}")
	public String applicationId;
	@Value("${tcp.flag}")
	public String flag;
	@Value("${tcp.developmentplancode}")
	public String developmentPlanCode;
	@Value("${tcp.remarks}")
	public String remarks;
	@Value("${tcp.fileid}")
	public String fileId;
	@Value("${tcp.caseid}")
	public String caseId;
	@Value("${tcp.casenumber}")
	public String caseNumber;
	@Value("${tcp.applicationtypeid}")
	public String applicationTypeId;
	@Value("${tcp.applicationnumber}")
	public String applicationNumber;
	@Value("${tcp.plotno}")
	public String plotNumber;
	@Value("${tcp.createdbyroleid}")
	public String createdByRoleId;
	@Value("${tcp.relatedapplicationid}")
	public String relatedApplicationId;
	@Value("${tcp.IsBpocForResiPlotted}")
	public String isBpocForResiPlotted;
	@Value("${tcp.detailsofapplication}")
	public String detailsOfApplication;
	@Value("${tcp.plotid}")
	public String plotId;
	@Value("${tcp.isconfirmed}")
	public String isConfirmed;
	@Value("${tcp.UserId}")
	public String userId;
	@Value("${tcp.UserLoginId}")
	public String userLoginId;

	@Autowired
	public RestTemplate restTemplate;

	public ResponseEntity<Map> getAuthToken(Map<String, Object> map) {

		HttpHeaders headers = new HttpHeaders();

		headers.set("access_key", tcpAccessKey);
		headers.set("secret_key", tcpSecretKey);
		headers.setContentType(MediaType.APPLICATION_JSON);

		headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
		HttpEntity<Map<String, Object>> entity = new HttpEntity<>(map, headers);

		ResponseEntity<Map> response = restTemplate.postForEntity(tcpurl + tcpAuthToken, entity, Map.class);
		if (response.getStatusCode() == HttpStatus.OK) {
			log.info("Token No\n" + response.getBody().get("Value"));
		}
		return response;
	}

	public ResponseEntity<Map> generateTransactionNumber(Map<String, Object> request, Map<String, Object> authtoken) {
		request.put("CaseTypeId", caseTypeId);
		request.put("AppTypeId", appTypeId);
		request.put("ChargesTypeId", chargesTypeId);

		request.put("TokenId", getAuthToken(authtoken).getBody().get("Value"));

		log.info("request info\n" + request);
		ResponseEntity<Map> response = restTemplate.postForEntity(tcpurl + tcpgenratetransactionnumber, request,
				Map.class);
		if (response.getStatusCode() == HttpStatus.OK) {
			log.info("transaction Number\n" + response.getBody().get("Value"));
		}
		return response;
	}

	public ResponseEntity<Map> saveTransactionData(Map<String, Object> request, Map<String, Object> authtoken) {

		request.put("CaseTypeId", caseTypeId);
		request.put("AppTypeId", appTypeId);
		request.put("ChargesTypeId", chargesTypeId);
		request.put("TokenId", getAuthToken(authtoken).getBody().get("Value"));

		ResponseEntity<Map> response = restTemplate.postForEntity(tcpurl + tcpSaveTransactionData, request, Map.class);
		if (response.getStatusCode() == HttpStatus.CREATED) {
			log.info("save transaction Number\n" + response.getBody().get("Value"));
		}
		return response;
	}

	public ResponseEntity<Map> generateDiaryNumber(Map<String, Object> request, Map<String, Object> authtoken) {

		request.put("ApplicationDocId", applicationDocId);
		request.put("ApplicationId", applicationId);
		request.put("Flag", flag);
		request.put("UserId", userId);
		request.put("UserLoginId", userLoginId);
		// request.put("DevelopmentPlanCode", developmentPlanCode);
		// request.put("Remarks", remarks);
		// request.put("FileId", fileId);
		// request.put("ColonyName", colonyName);
		request.put("TokenId", getAuthToken(authtoken).getBody().get("Value"));

		ResponseEntity<Map> response = restTemplate.postForEntity(tcpurl + tcpGenerateDairyNumber, request, Map.class);
		if (response.getStatusCode() == HttpStatus.OK) {
			log.info("Dairy Number\n" + response.getBody().get("Value"));
		}
		return response;
	}

	public ResponseEntity<Map> generateCaseNumber(Map<String, Object> request, Map<String, Object> authtoken) {
		request.put("CaseId", caseId);
		request.put("CaseTypeId", caseTypeId);
		request.put("CaseNo", caseNumber);
//		request.put("DevelopmentPlanCode", developmentPlanCode);
//		request.put("ColonyName", colonyName);
		request.put("TokenId", getAuthToken(authtoken).getBody().get("Value"));
		request.put("UserId", userId);
		request.put("UserLoginId", userLoginId);

		ResponseEntity<Map> response = restTemplate.postForEntity(tcpurl + tcpGenerateCaseNumber, request, Map.class);
		if (response.getStatusCode() == HttpStatus.OK) {
			log.info("Case Number\n" + response.getBody().get("Value"));
		}
		return response;
	}

	public ResponseEntity<Map> generateApplicationNumber(Map<String, Object> request, Map<String, Object> authtoken) {

		// request.put("CaseId", caseId);
		request.put("ApplicationTypeId", applicationTypeId);
		// request.put("ApplicationId", applicationId);
		// request.put("ApplicationNo", applicationNumber);
		request.put("PlotNo", plotNumber);
		request.put("RelatedApplicationId", relatedApplicationId);
		request.put("IsBpocForResiPlotted", isBpocForResiPlotted);
		// request.put("DetailsOfApplication", detailsOfApplication);
		request.put("PlotId", plotId);
		request.put("CreatedByRoleId", createdByRoleId);
		request.put("IsConfirmed", isConfirmed);
		request.put("TokenId", getAuthToken(authtoken).getBody().get("Value"));
		request.put("UserId", userId);
		request.put("UserLoginId", userLoginId);
		ResponseEntity<Map> response = restTemplate.postForEntity(tcpurl + tcpGenerateApplicationNumber, request,
				Map.class);

		if (response.getStatusCode() == HttpStatus.OK) {
			log.info("application Number\n" + response.getBody().get("Value"));
		}
		return response;
	}

	public ResponseEntity<Map> isExistSSOToken(Map<String, Object> request) {

		ResponseEntity<Map> response = restTemplate.postForEntity(tcpurl + tcpExistSSoNumber, request, Map.class);
		if (response.getStatusCode() == HttpStatus.OK) {
			log.info("isexistSSO Number\n" + response.getBody().get("Value"));
		}
		return response;
	}

	public ResponseEntity<Map> getDepartmenAuthToken(Map<String, Object> map) {

		HttpHeaders headers = new HttpHeaders();

		headers.set("access_key", tcpAccessKey);
		headers.set("secret_key", tcpSecretKey);
		headers.setContentType(MediaType.APPLICATION_JSON);

		headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
		HttpEntity<Map<String, Object>> entity = new HttpEntity<>(map, headers);

		ResponseEntity<Map> response = restTemplate.postForEntity(tcpurl + deptAuthToken, entity, Map.class);
		if (response.getStatusCode() == HttpStatus.OK) {
			log.info("Token No\n" + response.getBody().get("Value"));
		}
		return response;
	}

	public ResponseEntity<String> getDispatchNumber(Map<String, Object> request, Map<String, Object> depAuthtoken) {

		request.put("TokenId", getDepartmenAuthToken(depAuthtoken).getBody().get("Value"));
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
		HttpEntity<Map<String, Object>> entity = new HttpEntity<>(request, headers);
		ResponseEntity<String> response = restTemplate.postForEntity(tcpurl + deptDispatchNumber, entity, String.class);
		log.info("response" + response);
		if (response.getStatusCode() == HttpStatus.OK) {
			log.info("dispatch Number\n" + response.getBody());
		}
		return response;
	}
}
