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
	private String tcpurl;
	@Value("${tcp.auth.token}")
	private String tcpAuthToken;
	@Value("${tcp.access.key}")
	private String tcpAccessKey;
	@Value("${tcp.secret.key}")
	private String tcpSecretKey;
	@Value("${tcp.UserId}")
	private String tpUserId;
	@Value("${tcp.tpUserId}")
	private String tcptpUserId;
	@Value("${tcp.emailId}")
	private String tcpEmailId;
	@Autowired
	private RestTemplate restTemplate;

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
		ResponseEntity<String> response = restTemplate.postForEntity(tcpurl + tcpAuthToken, entity, String.class);
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
