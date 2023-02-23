package org.egov.web.notification.mail.controller;

import java.util.Set;

import org.egov.web.notification.mail.consumer.contract.Email;
import org.egov.web.notification.mail.service.EmailService;
import org.egov.web.notification.mail.utils.Category;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/otpEmail")
public class TestController {
	@Autowired
	EmailService emailService;

	@GetMapping
	public ResponseEntity<?> test(@RequestParam(value = "emailId", required = true) Set<String> emailId,
			@RequestParam(value = "body", required = true) String body,
			@RequestParam(value = "subject", required = true) String subject,
			@RequestParam(value = "category", required = true) Category category) {

		Email email = new Email(emailId, body, subject, category, true);
		emailService.sendEmail(email);

		return null;
	}
}
