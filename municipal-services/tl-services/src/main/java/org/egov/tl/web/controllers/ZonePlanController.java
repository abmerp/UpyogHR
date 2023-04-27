package org.egov.tl.web.controllers;

import java.util.List;

import org.egov.tl.service.ZonePlanServices;
import org.egov.tl.util.ResponseInfoFactory;
import org.egov.tl.web.models.RequestInfoWrapper;
import org.egov.tl.web.models.ZonePlan;
import org.egov.tl.web.models.ZonePlanRequest;
import org.egov.tl.web.models.ZonePlanResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.core.JsonProcessingException;

@RestController
@RequestMapping("/ZonePlanRequest")
public class ZonePlanController {

	@Autowired
	private ResponseInfoFactory responseInfoFactory;
	@Autowired
	private ZonePlanServices zonePlanServices;

	@PostMapping("/_create")
	public ResponseEntity<ZonePlanResponse> create(
			@RequestBody ZonePlanRequest zonePlanRequest) throws JsonProcessingException {

		ZonePlan zonePlan = zonePlanServices.create(zonePlanRequest);

		ZonePlanResponse zonePlanResponse = ZonePlanResponse.builder()
				.zonePlan(zonePlan).responseInfo(responseInfoFactory
						.createResponseInfoFromRequestInfo(zonePlanRequest.getRequestInfo(), true))
				.build();

		return new ResponseEntity<>(zonePlanResponse, HttpStatus.OK);
	}

	@PostMapping("/_update")
	public ResponseEntity<ZonePlanResponse> update(
			@RequestBody ZonePlanRequest zonePlanRequest) {

		ZonePlan zonePlan = zonePlanServices.update(zonePlanRequest);

		ZonePlanResponse zonePlanResponse = ZonePlanResponse.builder()
				.zonePlan(zonePlan).responseInfo(responseInfoFactory
						.createResponseInfoFromRequestInfo(zonePlanRequest.getRequestInfo(), true))
				.build();

		return new ResponseEntity<>(zonePlanResponse, HttpStatus.OK);
	}
	
	@PostMapping("/_search")
	public ResponseEntity<ZonePlanResponse> search(@RequestBody RequestInfoWrapper requestInfoWrapper,
			@RequestParam(value = "licenseNo", required = false) String licenceNumber,
			@RequestParam(value = "applicationNumber", required = false) String applicationNumber) {
		ZonePlan zonePlan = zonePlanServices.search(requestInfoWrapper.getRequestInfo(),
				licenceNumber,applicationNumber);
		
		ZonePlanResponse zonePlanResponse = ZonePlanResponse.builder(). zonePlan(zonePlan)
				.responseInfo(responseInfoFactory
						.createResponseInfoFromRequestInfo(requestInfoWrapper.getRequestInfo(), true))
				.build();
		
		return new ResponseEntity<>(zonePlanResponse, HttpStatus.OK);
	}

	
}
