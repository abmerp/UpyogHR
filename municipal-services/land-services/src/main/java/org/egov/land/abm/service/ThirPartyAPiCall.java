package org.egov.land.abm.service;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
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
	@Value("tcp.genrate.tokennumber")
	public String tcpgenrateTokenNumber;
	@Value("tcp.save.transactiondata")
	public String tcpSaveTransactionData;
	@Value("tcp.generate.dairynumber")
	public String tcpGenerateDairyNumber;
	@Value("tcp.generate.casenumber")
	public String tcpGenerateCaseNumber;
	@Value("tcp.generate.applicationnumber")
	public String tcpGenerateApplicationNumber;
	@Value("tcp.is.existSSO.Token")
	public String tcpExistSSoNumber;
	@Autowired
	public RestTemplate restTemplate;

	public ResponseEntity<String> getAuthToken() {

		HttpHeaders headers = new HttpHeaders();
		Map<String, Object> map = new HashMap<String, Object>();

		map.put("userId", tpUserId);
		map.put(tpUserId, tcptpUserId);
		map.put("emailid", tcpEmailId);
		headers.set("access_key", tcpAccessKey);
		headers.set("secret_key", tcpSecretKey);
		headers.setContentType(MediaType.APPLICATION_JSON);
		// set `accept` header
		headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
		HttpEntity<Map<String, Object>> entity = new HttpEntity<>(map, headers);

		ResponseEntity<String> response = restTemplate.postForEntity(tcpurl + tcpAuthToken, entity,String.class);
		if (response.getStatusCode() == HttpStatus.CREATED) {
			System.out.println("Request Successful");
			System.out.println(response.getBody());
		} else {
			System.out.println("Request Failed");
			System.out.println(response.getStatusCode());
		}
		return response;
	}

	public ResponseEntity<Map> generateTransactionNumber(Map<String, Object> request) {

		HttpHeaders headers = new HttpHeaders();

		headers.set("access_key", tcpAccessKey);
		headers.set("secret_key", tcpSecretKey);
		headers.setContentType(MediaType.APPLICATION_JSON);
		// set `accept` header
		request.put("userId", tpUserId);
		request.put(tpUserId, tcptpUserId);
		request.put("emailid", tcpEmailId);
		System.out.println(getAuthToken().getBody());
		request.put("TokenId", "");
		headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
		HttpEntity<Map<String, Object>> entity = new HttpEntity<>(request, headers);

		ResponseEntity<Map> response = restTemplate.getForEntity(tcpurl + tcpgenrateTokenNumber,  Map.class,entity);
		if (response.getStatusCode() == HttpStatus.CREATED) {
			System.out.println("Request Successful");
			System.out.println(response.getBody());
		} else {
			System.out.println("Request Failed");
			System.out.println(response.getStatusCode());
		}
		return response;
	}

	public ResponseEntity<Map> saveTransactionData(Map<String, Object> request) {

		HttpHeaders headers = new HttpHeaders();

		headers.set("access_key", tcpAccessKey);
		headers.set("secret_key", tcpSecretKey);
		headers.setContentType(MediaType.APPLICATION_JSON);
		// set `accept` header
		request.put("userId", tpUserId);
		request.put(tpUserId, tcptpUserId);
		request.put("emailid", tcpEmailId);
		request.put("TokenId", getAuthToken());
		headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
		HttpEntity<Map<String, Object>> entity = new HttpEntity<>(request, headers);

		ResponseEntity<Map> response = restTemplate.postForEntity(tcpurl + tcpSaveTransactionData, entity, Map.class);
		if (response.getStatusCode() == HttpStatus.CREATED) {
			System.out.println("Request Successful");
			System.out.println(response.getBody());
		} else {
			System.out.println("Request Failed");
			System.out.println(response.getStatusCode());
		}
		return response;
	}

	public ResponseEntity<Map> generateDiaryNumber(Map<String, Object> request) {

		HttpHeaders headers = new HttpHeaders();

		headers.set("access_key", tcpAccessKey);
		headers.set("secret_key", tcpSecretKey);
		headers.setContentType(MediaType.APPLICATION_JSON);
		// set `accept` header
		request.put("userId", tpUserId);
		request.put(tpUserId, tcptpUserId);
		request.put("emailid", tcpEmailId);
		request.put("TokenId", getAuthToken());
		headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
		HttpEntity<Map<String, Object>> entity = new HttpEntity<>(request, headers);

		ResponseEntity<Map> response = restTemplate.postForEntity(tcpurl + tcpGenerateDairyNumber, entity, Map.class);
		if (response.getStatusCode() == HttpStatus.CREATED) {
			System.out.println("Request Successful");
			System.out.println(response.getBody());
		} else {
			System.out.println("Request Failed");
			System.out.println(response.getStatusCode());
		}
		return response;
	}

	public ResponseEntity<Map> generateCaseNumber(Map<String, Object> request) {

		HttpHeaders headers = new HttpHeaders();

		headers.set("access_key", tcpAccessKey);
		headers.set("secret_key", tcpSecretKey);
		headers.setContentType(MediaType.APPLICATION_JSON);
		// set `accept` header
		request.put("userId", tpUserId);
		request.put(tpUserId, tcptpUserId);
		request.put("emailid", tcpEmailId);
		request.put("TokenId", getAuthToken());
		request.put("DiaryNo",generateDiaryNumber(request));
		headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
		HttpEntity<Map<String, Object>> entity = new HttpEntity<>(request, headers);

		ResponseEntity<Map> response = restTemplate.postForEntity(tcpurl + tcpGenerateCaseNumber, entity, Map.class);
		if (response.getStatusCode() == HttpStatus.CREATED) {
			System.out.println("Request Successful");
			System.out.println(response.getBody());
		} else {
			System.out.println("Request Failed");
			System.out.println(response.getStatusCode());
		}
		return response;
	}

	public ResponseEntity<Map> generateApplicationNumber(Map<String, Object> request) {

		HttpHeaders headers = new HttpHeaders();

		headers.set("access_key", tcpAccessKey);
		headers.set("secret_key", tcpSecretKey);
		headers.setContentType(MediaType.APPLICATION_JSON);
		// set `accept` header
		request.put("userId", tpUserId);
		request.put(tpUserId, tcptpUserId);
		request.put("emailid", tcpEmailId);
		request.put("TokenId", getAuthToken());
		request.put("DiaryNo",generateDiaryNumber(request));
		headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
		HttpEntity<Map<String, Object>> entity = new HttpEntity<>(request, headers);

		ResponseEntity<Map> response = restTemplate.postForEntity(tcpurl + tcpGenerateApplicationNumber, entity,
				Map.class);
		if (response.getStatusCode() == HttpStatus.CREATED) {
			System.out.println("Request Successful");
			System.out.println(response.getBody());
		} else {
			System.out.println("Request Failed");
			System.out.println(response.getStatusCode());
		}
		return response;
	}

	public ResponseEntity<Map> isExistSSOToken(Map<String, Object> request) {

		HttpHeaders headers = new HttpHeaders();

		headers.set("access_key", tcpAccessKey);
		headers.set("secret_key", tcpSecretKey);
		headers.setContentType(MediaType.APPLICATION_JSON);
		// set `accept` header
		request.put("userId", tpUserId);
		request.put(tpUserId, tcptpUserId);
		
		headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
		HttpEntity<Map<String, Object>> entity = new HttpEntity<>(request, headers);

		ResponseEntity<Map> response = restTemplate.postForEntity(tcpurl + tcpExistSSoNumber, entity, Map.class);
		if (response.getStatusCode() == HttpStatus.CREATED) {
			System.out.println("Request Successful");
			System.out.println(response.getBody());
		} else {
			System.out.println("Request Failed");
			System.out.println(response.getStatusCode());
		}
		return response;
	}
}
