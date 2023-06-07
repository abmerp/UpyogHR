package org.egov.tl.web.controllers;

import org.egov.tl.service.PaymentUpdateService;
import org.egov.tl.service.TradeLicenseService;
import org.egov.tl.service.notification.PaymentNotificationService;
import org.egov.tl.service.notification.TLNotificationService;
import org.egov.tl.util.ResponseInfoFactory;
import org.egov.tl.web.models.*;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.swagger.annotations.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

import javax.validation.constraints.*;
import javax.validation.Valid;
import javax.servlet.http.HttpServletRequest;

import static org.egov.tl.util.TLConstants.businessService_TL;

@RestController
@RequestMapping("/v1")
public class TradeLicenseController {

	private final ObjectMapper objectMapper;

	private final HttpServletRequest request;

	private final TradeLicenseService tradeLicenseService;

	private final ResponseInfoFactory responseInfoFactory;

	private final PaymentNotificationService paymentNotificationService;

	private final TLNotificationService tlNotificationService;

	@Autowired
	public TradeLicenseController(ObjectMapper objectMapper, HttpServletRequest request,
			TradeLicenseService tradeLicenseService, ResponseInfoFactory responseInfoFactory,
			PaymentNotificationService paymentNotificationService, TLNotificationService tlNotificationService) {
		this.objectMapper = objectMapper;
		this.request = request;
		this.tradeLicenseService = tradeLicenseService;
		this.responseInfoFactory = responseInfoFactory;
		this.paymentNotificationService = paymentNotificationService;
		this.tlNotificationService = tlNotificationService;
	}

	@PostMapping({ "/{servicename}/_create", "/_create" })
	public ResponseEntity<TradeLicenseResponse> create(@Valid @RequestBody TradeLicenseRequest tradeLicenseRequest,
			@PathVariable(required = false) String servicename) {
		List<TradeLicense> licenses = tradeLicenseService.create(tradeLicenseRequest, servicename);
		TradeLicenseResponse response = TradeLicenseResponse.builder().licenses(licenses).responseInfo(
				responseInfoFactory.createResponseInfoFromRequestInfo(tradeLicenseRequest.getRequestInfo(), true))
				.build();
		return new ResponseEntity<>(response, HttpStatus.OK);
	}

	@RequestMapping(value = { "/{servicename}/_search", "/_search" }, method = RequestMethod.POST)
	public ResponseEntity<TradeLicenseResponse> search(@Valid @RequestBody RequestInfoWrapper requestInfoWrapper,
			@Valid @ModelAttribute TradeLicenseSearchCriteria criteria,
			@PathVariable(required = false) String servicename, @RequestHeader HttpHeaders headers)
			throws JsonProcessingException, ParseException {
		List<TradeLicense> licenses = tradeLicenseService.search(criteria, requestInfoWrapper.getRequestInfo(),
				servicename, headers);

		int count = tradeLicenseService.countLicenses(criteria, requestInfoWrapper.getRequestInfo(), servicename,
				headers);

		licenses = licenses.stream().filter(tradelicense -> {
			Date resultdate = null;
			String resultStrdate = null;
			SimpleDateFormat df = new SimpleDateFormat("MM/dd/yyyy");
			Long applicationDate = tradelicense.getApplicationDate();
			if (applicationDate != null) {
				try {
					String currentdate = df.format(applicationDate);
					Calendar c1 = Calendar.getInstance();
					c1.setTime(df.parse(currentdate));
					c1.add(Calendar.DATE, 30);
					df = new SimpleDateFormat("MM/dd/yyyy");
					resultdate = new Date(c1.getTimeInMillis());

					resultStrdate = df.format(resultdate);
				} catch (ParseException e) {
					e.printStackTrace();
				}
				if (resultdate.after(new Date()) || resultStrdate.equals(df.format(new Date())))
					return true;
				else
					return false;
			} else {
				return false;
			}
		}).collect(Collectors.toList());

		TradeLicenseResponse response = TradeLicenseResponse
				.builder().licenses(licenses).responseInfo(responseInfoFactory
						.createResponseInfoFromRequestInfo(requestInfoWrapper.getRequestInfo(), true))
				.count(count).build();
		return new ResponseEntity<>(response, HttpStatus.OK);
	}

	@RequestMapping(value = { "/{servicename}/_update", "/_update" }, method = RequestMethod.POST)
	public ResponseEntity<TradeLicenseResponse> update(@Valid @RequestBody TradeLicenseRequest tradeLicenseRequest,
			@PathVariable(required = false) String servicename) {
		List<TradeLicense> licenses = tradeLicenseService.update(tradeLicenseRequest, servicename);

		TradeLicenseResponse response = TradeLicenseResponse.builder().licenses(licenses).responseInfo(
				responseInfoFactory.createResponseInfoFromRequestInfo(tradeLicenseRequest.getRequestInfo(), true))
				.build();
		return new ResponseEntity<>(response, HttpStatus.OK);
	}

	@RequestMapping(value = { "/{servicename}/{jobname}/_batch", "/_batch" }, method = RequestMethod.POST)
	public ResponseEntity sendReminderSMS(@Valid @RequestBody RequestInfoWrapper requestInfoWrapper,
			@PathVariable(required = false) String servicename, @PathVariable(required = true) String jobname) {

		tradeLicenseService.runJob(servicename, jobname, requestInfoWrapper.getRequestInfo());

		return new ResponseEntity(HttpStatus.ACCEPTED);
	}

	@RequestMapping(value = "/_plainsearch", method = RequestMethod.POST)
	public ResponseEntity<TradeLicenseResponse> plainsearch(@Valid @RequestBody RequestInfoWrapper requestInfoWrapper,
			@Valid @ModelAttribute TradeLicenseSearchCriteria criteria) {

		List<TradeLicense> licenses = tradeLicenseService.plainSearch(criteria, requestInfoWrapper.getRequestInfo());

		TradeLicenseResponse response = TradeLicenseResponse.builder().licenses(licenses).responseInfo(
				responseInfoFactory.createResponseInfoFromRequestInfo(requestInfoWrapper.getRequestInfo(), true))
				.build();
		return new ResponseEntity<>(response, HttpStatus.OK);
	}

	@PostMapping("/_test")
	public ResponseEntity test(@Valid @RequestBody HashMap<String, Object> record) {
		paymentNotificationService.processBusinessService(record, businessService_TL);
		return new ResponseEntity(HttpStatus.OK);
	}

	@PostMapping("/_test1")
	public ResponseEntity test1(@Valid @RequestBody TradeLicenseRequest tradeLicenseRequest) {
		tlNotificationService.process(tradeLicenseRequest);
		return new ResponseEntity(HttpStatus.OK);
	}

}
