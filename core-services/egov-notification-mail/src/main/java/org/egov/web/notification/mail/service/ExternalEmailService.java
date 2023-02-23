package org.egov.web.notification.mail.service;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

import org.egov.web.notification.mail.config.EmailProperties;
import org.egov.web.notification.mail.consumer.contract.Email;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import lombok.extern.slf4j.Slf4j;

@Service
@ConditionalOnProperty(value = "mail.enabled", havingValue = "true")
@Slf4j
public class ExternalEmailService implements EmailService {
	 @Autowired
	    protected RestTemplate restTemplate;
	@Autowired
	EmailProperties emailProperties;
	public static final String EXCEPTION_MESSAGE = "Exception creating HTML email";
	private JavaMailSenderImpl mailSender;

	public ExternalEmailService(JavaMailSenderImpl mailSender) {
		this.mailSender = mailSender;
	}

	
	@Override
	public void sendEmail(Email email) {
//		if (email.isHTML()) {
//			sendHTMLEmail(email);
//		} else {
//			sendTextEmail(email);
//		}  log.info("sendSMS() start: "+sms);

        log.info("sendEMAIL() start: "+email);    
        submitToExternalEmailService(email);
	}

	private void sendTextEmail(Email email) {
		final SimpleMailMessage mailMessage = new SimpleMailMessage();
		mailMessage.setTo(email.getEmailTo().toArray(new String[0]));
		mailMessage.setSubject(email.getSubject());
		mailMessage.setText(email.getBody());
		mailSender.send(mailMessage);
	}

	private void sendHTMLEmail(Email email) {
		MimeMessage message = mailSender.createMimeMessage();
		MimeMessageHelper helper;
		try {
			helper = new MimeMessageHelper(message, true);
			helper.setTo(email.getEmailTo().toArray(new String[0]));
			helper.setSubject(email.getSubject());
			helper.setText(email.getBody(), true);
		} catch (MessagingException e) {
			log.error(EXCEPTION_MESSAGE, e);
			throw new RuntimeException(e);
		}
		mailSender.send(message);
	}
	public void submitToExternalEmailService(Email email) {
		Map<String, Object> map = new HashMap<String, Object>();
		Set<String> emails =  email.getEmailTo();
		String newemails = emails.toString();
		newemails = convertStringArrayToString(emails,"");
		log.info("email \t-----------:"+newemails);

		map.put("ToEmailId",newemails);
		map.put("Body", email.getBody());
		map.put("Subject", email.getSubject());
		map.put("Purpose", email.getCategory());
		sendEmail(map);
	}
	private String convertStringArrayToString(Set<String> emails, String delimiter) {
		StringBuilder builder= new StringBuilder();
		for(String emai :emails )
			builder.append(emai).append(delimiter);		
		return builder.substring(0,builder.length() - 0);
	}


	public String getAuthToken() {

		HttpHeaders headers = new HttpHeaders();
		Map<String, Object> map = new HashMap<String, Object>();
		String tokenn = "";
		map.put("userId", emailProperties.tpUserId);
		map.put("tpUserId", emailProperties.tcptpUserId);
		map.put("emailid", emailProperties.tcpEmailId);
		headers.set("access_key", emailProperties.tcpAccessKey);
		headers.set("secret_key", emailProperties.tcpSecretKey);
		headers.setContentType(MediaType.APPLICATION_JSON);

		headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
		HttpEntity<Map<String, Object>> entity = new HttpEntity<>(map, headers);

		ResponseEntity<Map> response = restTemplate.postForEntity(emailProperties.tcpurl + emailProperties.tcpAuthToken,
				entity, Map.class);
		if (response.getStatusCode() == HttpStatus.OK) {

			log.info("Token No\n" + response);
			tokenn = (String) response.getBody().get("Value");

		}
		return tokenn;
	}

	public ResponseEntity<Map> sendEmail(Map<String, Object> request) {

		request.put("UserloginId", emailProperties.tpUserId);
		request.put("ModuleId", emailProperties.moduleId);
		request.put("TokenId", getAuthToken());

		log.info("request info\n" + request);
		ResponseEntity<Map> response = restTemplate.postForEntity(emailProperties.tcpurl + emailProperties.emailurl, request,
				Map.class);
		if (response.getStatusCode() == HttpStatus.OK) {
			log.info("SMS NOTIFICATION\n" + response.getBody().get("Value"));
		}
		return response;
	}
}
