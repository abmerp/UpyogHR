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

		ResponseEntity<String> response = restTemplate.getForEntity(tcpurl + tcpAuthToken, String.class, entity);
		if (response.getStatusCode() == HttpStatus.CREATED) {
			System.out.println("Request Successful");
			System.out.println(response.getBody());
		} else {
			System.out.println("Request Failed");
			System.out.println(response.getStatusCode());
		}
		return response;
	}

	public ResponseEntity<Map> generateTransactionNo(Map<String, Object> request) {

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

		ResponseEntity<Map> response = restTemplate.postForEntity(tcpurl + tcpgenrateTokenNumber, entity, Map.class);
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
