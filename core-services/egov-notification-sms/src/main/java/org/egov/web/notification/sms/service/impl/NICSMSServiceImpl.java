package org.egov.web.notification.sms.service.impl;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLEncoder;
import java.security.KeyStore;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import javax.annotation.PostConstruct;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.X509TrustManager;

import org.egov.web.notification.sms.config.SMSProperties;
import org.egov.web.notification.sms.models.Sms;
import org.egov.web.notification.sms.service.BaseSMSService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import lombok.Value;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@ConditionalOnProperty(value = "sms.provider.class", matchIfMissing = true, havingValue = "NIC")
public class NICSMSServiceImpl extends BaseSMSService {

	@Autowired
	private SMSProperties smsProperties;

	private SSLContext sslContext;

	@PostConstruct

	public void postConstruct() {
		log.info("postConstruct() start");
		try {
			sslContext = SSLContext.getInstance("TLSv1.2");
			if (smsProperties.isVerifyCertificate()) {
				log.info("checking certificate");
				KeyStore trustStore = KeyStore.getInstance(KeyStore.getDefaultType());
				// File file = new File(System.getenv("JAVA_HOME")+"/lib/security/cacerts");
				File file = new File(getClass().getClassLoader().getResource("smsgwsmsgovin.cer").getFile());
				InputStream is = new FileInputStream(file);
				trustStore.load(is, "changeit".toCharArray());
				TrustManagerFactory trustFactory = TrustManagerFactory
						.getInstance(TrustManagerFactory.getDefaultAlgorithm());
				trustFactory.init(trustStore);

				TrustManager[] trustManagers = trustFactory.getTrustManagers();
				sslContext.init(null, trustManagers, null);
			} else {
				log.info("not checking certificate");
				TrustManager tm = new X509TrustManager() {
					@Override
					public void checkClientTrusted(java.security.cert.X509Certificate[] chain, String authType)
							throws java.security.cert.CertificateException {
					}

					@Override
					public void checkServerTrusted(java.security.cert.X509Certificate[] chain, String authType)
							throws java.security.cert.CertificateException {
					}

					@Override
					public java.security.cert.X509Certificate[] getAcceptedIssuers() {
						return null;
					}
				};
				sslContext.init(null, new TrustManager[] { tm }, null);
			}
			SSLContext.setDefault(sslContext);

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	protected void submitToExternalSmsService(Sms sms) {
		log.info("submitToExternalSmsService() start");
		Map<String, Object> map = new HashMap<String, Object>();

		map.put("MobileNo", sms.getMobileNumber());
		map.put("Content", sms.getMessage());
		map.put("TemplateId", sms.getTemplateId());
		map.put("Purpose", sms.getCategory());
		log.info("OPT DAta "+map);
		sendSMS(map);
	}

	public String getAuthToken() {

		HttpHeaders headers = new HttpHeaders();
		Map<String, Object> map = new HashMap<String, Object>();
		String tokenn="";
		map.put("userId", smsProperties.tpUserId);
		map.put("tpUserId", smsProperties.tcptpUserId);
		map.put("emailid", smsProperties.tcpEmailId);
		headers.set("access_key", smsProperties.tcpAccessKey);
		headers.set("secret_key", smsProperties.tcpSecretKey);
		headers.setContentType(MediaType.APPLICATION_JSON);

		headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
		HttpEntity<Map<String, Object>> entity = new HttpEntity<>(map, headers);

		ResponseEntity<Map> response = restTemplate.postForEntity(smsProperties.tcpurl + smsProperties.tcpAuthToken,
				entity, Map.class);
		if (response.getStatusCode() == HttpStatus.OK) {
			
			log.info("Token No\n" +response );
			tokenn=(String)response.getBody().get("Value");

		}
		return tokenn;
	}

	public ResponseEntity<Map> sendSMS(Map<String, Object> request) {

		request.put("UserloginId", smsProperties.tpUserId);
		request.put("ModuleId", smsProperties.moduleId);
		request.put("TokenId", getAuthToken());

		log.info("request info\n" + request);
		ResponseEntity<Map> response = restTemplate.postForEntity(smsProperties.tcpurl + smsProperties.smsurl, request,
				Map.class);
		if (response.getStatusCode() == HttpStatus.OK) {
			log.info("transaction Number\n" + response.getBody().get("Value"));
		}
		return response;
	}

	public boolean textIsInEnglish(String text) {
		ArrayList<Character.UnicodeBlock> english = new ArrayList<>();
		english.add(Character.UnicodeBlock.BASIC_LATIN);
		english.add(Character.UnicodeBlock.LATIN_1_SUPPLEMENT);
		english.add(Character.UnicodeBlock.LATIN_EXTENDED_A);
		english.add(Character.UnicodeBlock.GENERAL_PUNCTUATION);
		for (char currentChar : text.toCharArray()) {
			Character.UnicodeBlock unicodeBlock = Character.UnicodeBlock.of(currentChar);
			if (!english.contains(unicodeBlock)) {
				return false;
			}
		}
		return true;
	}

}