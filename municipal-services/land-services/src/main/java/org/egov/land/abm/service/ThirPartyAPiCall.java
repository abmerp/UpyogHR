package org.egov.land.abm.service;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.egov.land.service.LandService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class ThirPartyAPiCall {

	@Value("${tcp.url}")
	public String tcpurl;
	@Value("${tcp.auth.token}")
	public String tcpAuthToken;
	@Value("${tcp.access.key}")
	public String tcpAccessKey;
	@Value("${tcp.secret.key}")
	public String tcpSecretKey;
	@Value("${tcp.userId}")
	public String tpUserId;
	@Value("${tcp.tpUserId}")
	public String tcptpUserId;
	@Value("${tcp.emailId}")
	public String tcpEmailId;
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
	@Autowired
	public RestTemplate restTemplate;

	public ResponseEntity<Map> getAuthToken() {

		HttpHeaders headers = new HttpHeaders();
		Map<String, Object> map = new HashMap<String, Object>();

		map.put("userId", tpUserId);
		map.put(tpUserId, tcptpUserId);
		map.put("emailid", tcpEmailId);
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

	public ResponseEntity<Map> generateTransactionNumber(Map<String, Object> request) {

		request.put("userId", tpUserId);
		request.put(tpUserId, tcptpUserId);
		request.put("emailid", tcpEmailId);
		request.put("TokenId", getAuthToken().getBody().get("Value"));

		log.info("request info\n" + request);
		ResponseEntity<Map> response = restTemplate.postForEntity(tcpurl + tcpgenratetransactionnumber, request, Map.class);
		if (response.getStatusCode() == HttpStatus.OK) {
			log.info("transaction Number\n" + response.getBody().get("Value"));
		} 
		return response;
	}

	public ResponseEntity<Map> saveTransactionData(Map<String, Object> request) {

		request.put("userId", tpUserId);
		request.put(tpUserId, tcptpUserId);
		request.put("emailid", tcpEmailId);
		request.put("TokenId", getAuthToken().getBody().get("Value"));
		request.put("TxnNo",generateTransactionNumber(request).getBody().get("Value"));

		ResponseEntity<Map> response = restTemplate.postForEntity(tcpurl + tcpSaveTransactionData, request, Map.class);
		if (response.getStatusCode() == HttpStatus.CREATED) {
			log.info("save transaction Number\n" + response.getBody().get("Value"));
		}
		return response;
	}

	public ResponseEntity<Map> generateDiaryNumber(Map<String, Object> request) {

		
		request.put("userId", tpUserId);
		request.put(tpUserId, tcptpUserId);
		request.put("emailid", tcpEmailId);
		request.put("TokenId", getAuthToken().getBody().get("Value"));
		
		ResponseEntity<Map> response = restTemplate.postForEntity(tcpurl + tcpGenerateDairyNumber, request, Map.class);
		if (response.getStatusCode() == HttpStatus.CREATED) {
			log.info("Dairy Number\n" + response.getBody().get("Value"));
		}
		return response;
	}

	public ResponseEntity<Map> generateCaseNumber(Map<String, Object> request) {

		request.put("userId", tpUserId);
		request.put(tpUserId, tcptpUserId);
		request.put("emailid", tcpEmailId);
		request.put("TokenId", getAuthToken().getBody().get("Value"));
		request.put("DiaryNo", generateDiaryNumber(request).getBody().get("Value"));
		
		ResponseEntity<Map> response = restTemplate.postForEntity(tcpurl + tcpGenerateCaseNumber, request, Map.class);
		if (response.getStatusCode() == HttpStatus.CREATED) {
			log.info("Case Number\n" + response.getBody().get("Value"));
		}
		return response;
	}

	public ResponseEntity<Map> generateApplicationNumber(Map<String, Object> request) {

		request.put("userId", tpUserId);
		request.put(tpUserId, tcptpUserId);
		request.put("emailid", tcpEmailId);
		request.put("TokenId", getAuthToken().getBody().get("Value"));
		request.put("DiaryNo", generateDiaryNumber(request).getBody().get("Value"));
		request.put("CaseId",generateCaseNumber(request).getBody().get("Value"));    
		 
		ResponseEntity<Map> response = restTemplate.postForEntity(tcpurl + tcpGenerateApplicationNumber, request,Map.class);
		if (response.getStatusCode() == HttpStatus.CREATED) {
			log.info("application Number\n" + response.getBody().get("Value"));
		}
		return response;
	}

	public ResponseEntity<Map> isExistSSOToken(Map<String, Object> request) {

	
		request.put("userId", tpUserId);
		request.put(tpUserId, tcptpUserId);

		ResponseEntity<Map> response = restTemplate.postForEntity(tcpurl + tcpExistSSoNumber, request, Map.class);
		if (response.getStatusCode() == HttpStatus.CREATED) {
			log.info("isexistSSO Number\n" + response.getBody().get("Value"));
		}
		return response;
	}
}
